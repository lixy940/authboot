package com.lixy.service.impl;

import com.lixy.constants.StaticParameterUtils;
import com.lixy.entity.SysMenu;
import com.lixy.entity.SysUser;
import com.lixy.service.ShiroService;
import com.lixy.service.SysMenuService;
import com.lixy.service.SysUserService;
import com.lixy.shiro.realm.ShiroRealm;
import com.lixy.utils.SpringContextUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shiro-权限相关的业务处理
 * @since 1.0
 */
@Service
public class ShiroServiceImpl implements ShiroService {

    private static final Logger LOG = LoggerFactory.getLogger(ShiroService.class);
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 初始化权限
     */
    @Override
    public Map<String, String> loadFilterChainDefinitions() {
        /*
            配置访问权限
            - anon:所有url都都可以匿名访问
            - authc: 需要认证才能进行访问（此处指所有非匿名的路径都需要登陆才能访问）
            - user:配置记住我或认证通过可以访问
         */
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();
        //<!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        filterChainDefinitionMap.put(StaticParameterUtils.PASSPORT_LOGOUT_URI, "kickout,logout");
        //登录页
        filterChainDefinitionMap.put(StaticParameterUtils.PASSPORT_LOGIN_URI, "anon");
        //将kickout过滤器增加到url过滤中
        filterChainDefinitionMap.put(StaticParameterUtils.PASSPORT_SIGNIN_URI, "anon,kickout");
        filterChainDefinitionMap.put("favicon.ico", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");

        //swagger配置，无需登录即可访问,否则必须登录才能访问
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/v2/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");

        // 加载数据库中配置的资源权限列表
        List<SysMenu> sysMenus = sysMenuService.listUrlAndPermission();
        for (SysMenu sysMenu : sysMenus) {
            if (!StringUtils.isEmpty(sysMenu.getUrl()) && !StringUtils.isEmpty(sysMenu.getPerms())) {
                String permission = "perms[" + sysMenu.getPerms() + "]";
                filterChainDefinitionMap.put(sysMenu.getUrl(), permission);
            }
        }
        // 本例子中并不存在什么特别关键的操作，所以直接使用user认证。如果有朋友是参考本例子的shiro开发其他安全功能（比如支付等）时，建议针对这类操作使用authc权限 by yadong.zhang
        filterChainDefinitionMap.put("/**", "user,kickout");
        return filterChainDefinitionMap;
    }

    /**
     *
     * 重新加载权限
     */
    @Override
    public void updatePermission() {
        ShiroFilterFactoryBean shirFilter = SpringContextUtils.getBean(ShiroFilterFactoryBean.class);
        synchronized (shirFilter) {
            AbstractShiroFilter shiroFilter = null;
            try {
                shiroFilter = (AbstractShiroFilter) shirFilter.getObject();
            } catch (Exception e) {
                throw new RuntimeException("get ShiroFilter from shiroFilterFactoryBean error!");
            }

            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter.getFilterChainResolver();
            DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();

            // 清空老的权限控制
            manager.getFilterChains().clear();

            shirFilter.getFilterChainDefinitionMap().clear();
            shirFilter.setFilterChainDefinitionMap(loadFilterChainDefinitions());
            // 重新构建生成
            Map<String, String> chains = shirFilter.getFilterChainDefinitionMap();
            for (Map.Entry<String, String> entry : chains.entrySet()) {
                String url = entry.getKey();
                String chainDefinition = entry.getValue().trim().replace(" ", "");
                manager.createChain(url, chainDefinition);
            }
        }
    }


    /**
     * 重新加载用户权限
     *
     * @param sysUser
     */
    @Override
    public void reloadAuthorizingByUserId(SysUser sysUser) {
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        ShiroRealm shiroRealm = (ShiroRealm) rsm.getRealms().iterator().next();
        Subject subject = SecurityUtils.getSubject();
        String realmName = subject.getPrincipals().getRealmNames().iterator().next();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(sysUser, realmName);
        subject.runAs(principals);
        shiroRealm.getAuthorizationCache().remove(subject.getPrincipals());
        subject.releaseRunAs();

        LOG.info("用户[{}]的权限更新成功！！", sysUser.getLoginName());

    }

    /**
     * 重新加载所有拥有roleId角色的用户的权限
     *
     * @param roleId
     */
    @Override
    public void reloadAuthorizingByRoleId(Long roleId) {
        List<SysUser> SysUserList = sysUserService.listByRoleId(roleId);
        if (CollectionUtils.isEmpty(SysUserList)) {
            return;
        }
        for (SysUser SysUser : SysUserList) {
            reloadAuthorizingByUserId(SysUser);
        }
    }

}
