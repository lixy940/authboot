package com.lixy.service.impl;

import com.lixy.entity.SysRole;
import com.lixy.mapper.SysRoleMapper;
import com.lixy.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    public List<SysRole> listRolesByUserId(Long userId) {
        return roleMapper.listRolesByUserId(userId);
    }
}
