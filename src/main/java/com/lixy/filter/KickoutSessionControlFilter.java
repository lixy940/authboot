package com.lixy.filter;

import com.lixy.constants.SessionConst;
import com.lixy.constants.StaticParameterUtils;
import com.lixy.entity.SysUser;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

/**
 * shiro 账号踢出过滤器
 */
public class KickoutSessionControlFilter extends AccessControlFilter {

    private String kickoutUrl; //踢出后到的地址
    private boolean kickoutAfter = false; //踢出之前登录的/之后登录的用户 默认踢出之前登录的用户
    private int maxSession = 1; //同一个帐号最大会话数 默认1

    private Cache<String, Deque<Serializable>> cache;


    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }


    //设置Cache的key的前缀
    public void setCacheManager(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("shiro_redis_cache");
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        Session session = subject.getSession();
        Serializable sessionId = session.getId();
        String requestURI = ((HttpServletRequest) request).getRequestURI();
//        System.out.println(requestURI);
        if(requestURI.equals(StaticParameterUtils.PASSPORT_SIGNIN_URI)){
            //登录时会穿用户名
            String username = request.getParameter(SessionConst.USER_USERNAME_KEY);
            //读取缓存   没有就存入
            Deque<Serializable> deque = cache.get(username);
            if (deque == null) {
                deque = new ArrayDeque<>();
                deque.add(sessionId);
                cache.put(username, deque);
                return Boolean.TRUE;
            }
            //如果队列里没有此sessionId，且用户没有被踢出；放入队列
            if (!deque.contains(sessionId)) {
                //将sessionId存入队列
                deque.add(sessionId);
                //将用户的sessionId队列缓存
                cache.put(username, deque);
            }
            //如果队列里的sessionId数超出最大会话数，开始踢人
            while (deque.size() > maxSession) {
                Serializable kickoutSessionId = null;
                if (kickoutAfter) { //如果踢出后者
                    deque.removeLast();
                } else { //否则踢出前者
                     deque.removeFirst();
                }
                //踢出后再更新下缓存队列
                cache.put(username, deque);
            }
        }else if(requestURI.equals(StaticParameterUtils.PASSPORT_LOGOUT_URI)) {
            SysUser user = (SysUser) subject.getPrincipal();
            String username = user.getLoginName();
            Deque<Serializable> deques = cache.get(username);
            if (deques != null) {
                Iterator<Serializable> it = deques.iterator();
                while (it.hasNext()) {
                    if (it.next().equals(sessionId)) {
                        it.remove();
                    }
                }
            }
            //shiro的logout退出
            return true;
        }else{
            SysUser user = (SysUser) subject.getPrincipal();
            String username = user.getLoginName();
            Deque<Serializable> deques = cache.get(username);
            if (!deques.contains(sessionId)) {
                subject.logout();
                saveRequest(request);
                //重定向
                WebUtils.issueRedirect(request, response, kickoutUrl);
                return false;
            }

        }


        return true;
    }
}