package com.lixy.shiro.realm;

import com.lixy.entity.SysMenu;
import com.lixy.entity.SysRole;
import com.lixy.entity.SysUser;
import com.lixy.enums.UserStatusEnum;
import com.lixy.service.SysMenuService;
import com.lixy.service.SysRoleService;
import com.lixy.service.SysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Shiro-密码输入错误的状态下重试次数的匹配管理
 *
 */
public class ShiroRealm extends AuthorizingRealm {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private SysUserService userService;
    @Resource
    private SysRoleService roleService;

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 提供账户信息返回认证信息（用户的角色信息集合）
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //获取用户的输入的账号.
        String username = (String) token.getPrincipal();
        SysUser sysUser = userService.getByUserName(username);
        if (sysUser == null) {
            throw new UnknownAccountException("账号不存在！");
        }
        if (sysUser.getStatus() != null && UserStatusEnum.DISABLE.getCode().equals(sysUser.getStatus())) {
            throw new LockedAccountException("帐号已被锁定，禁止登录！");
        }
        // principal参数使用用户Id，方便动态刷新用户权限
        return new SimpleAuthenticationInfo(
                sysUser,
                sysUser.getPassword(),
                ByteSource.Util.bytes(username),
                getName()
        );
    }

    /**
     * 权限认证，为当前登录的Subject授予角色和权限（角色的权限信息集合）
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 权限信息对象info,用来存放查出的用户的所有的角色（SysRole）及权限（permission）
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
        SysUser sysUser = (SysUser) principalCollection.getPrimaryPrincipal();
        Long userId = sysUser.getId();

        // 赋予角色
        List<SysRole> roleList = roleService.listRolesByUserId(userId);
        for (SysRole SysRole : roleList) {
            info.addRole(SysRole.getRoleName());
        }

        // 赋予权限
        List<SysMenu> sysMenus = sysMenuService.listByUserId(userId);
        if (!CollectionUtils.isEmpty(sysMenus)) {
            for (SysMenu sysMenu : sysMenus) {
                String permission = sysMenu.getPerms();
                logger.info(sysMenu.getMenuName() + "   " + permission);
                if (!StringUtils.isEmpty(permission)) {
                    info.addStringPermission(permission);
                }
            }
        }
        return info;
    }

}
