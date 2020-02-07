package com.lixy.service.impl;

import com.lixy.entity.SysUser;
import com.lixy.enums.BussinessException;
import com.lixy.framework.holder.RequestHolder;
import com.lixy.mapper.SysUserMapper;
import com.lixy.service.SysRoleService;
import com.lixy.service.SysUserService;
import com.lixy.utils.IPUtil;
import com.lixy.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleService roleService;

    @Override
    public SysUser getByPrimaryKey(Long userId) {
        Assert.notNull(userId, "PrimaryKey不可为空！");
        return sysUserMapper.selectByPrimaryKey(userId);
    }


    @Override
    public SysUser getByUserName(String username) {
        return sysUserMapper.getByUserName(username);
    }

    @Override
    public List<SysUser> listByRoleId(Long roleId) {
        return sysUserMapper.listByRoleId(roleId);
    }

    @Override
    public void updateUserLastLoginInfo(SysUser user) {
        if (user != null) {
            user.setLoginDate(new Date());
            user.setLoginIp(IPUtil.getRealIp(RequestHolder.getRequest()));
            sysUserMapper.updateByPrimaryKeySelective(user);
        }
    }
}
