package com.lixy.service;

import com.lixy.entity.SysUser;

import java.util.List;

public interface SysUserService {
    SysUser getByPrimaryKey(Long userId);

    SysUser getByUserName(String username);

    List<SysUser> listByRoleId(Long roleId);

    void updateUserLastLoginInfo(SysUser user);
}
