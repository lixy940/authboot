//package com.lixy.interceptor;
//
//import com.lixy.constants.SessionConst;
//import com.lixy.constants.StaticParameterUtils;
//import com.lixy.entity.SysUser;
//import com.lixy.service.SysUserService;
//import com.lixy.utils.PasswordUtil;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.UsernamePasswordToken;
//import org.apache.shiro.session.Session;
//import org.apache.shiro.subject.Subject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * 是否记住拦截
// * @since 1.0
// */
//@Component
//public class RememberAuthenticationInterceptor implements HandlerInterceptor {
//    private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private SysUserService userService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        Subject subject = SecurityUtils.getSubject();
//        if (subject.isAuthenticated()) {
//            return true;
//        }
//        Session session = subject.getSession(true);
//        if (session.getAttribute(SessionConst.USER_SESSION_KEY) != null) {
//            return true;
//        }
//        if(!subject.isRemembered()) {
//            log.warn("未设置“记住我”,跳转到登录页...");
//            response.sendRedirect(request.getContextPath() + StaticParameterUtils.PASSPORT_LOGIN_URI);
//            return false;
//        }
//        try {
//            Long userId = Long.parseLong(subject.getPrincipal().toString());
//            SysUser user = userService.getByPrimaryKey(userId);
//            UsernamePasswordToken token = new UsernamePasswordToken(user.getLoginName(), PasswordUtil.decrypt(user.getPassword(), user.getLoginName()), true);
//            subject.login(token);
//            session.setAttribute(SessionConst.USER_SESSION_KEY, user);
//            log.info("[{}] - 已自动登录", user.getLoginName());
//        } catch (Exception e) {
//            log.error("自动登录失败", e);
//            response.sendRedirect(request.getContextPath() + StaticParameterUtils.PASSPORT_LOGIN_URI);
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
//
//    }
//}
