package com.lixy.service;

import com.lixy.entity.SysRole;

import java.util.List;

public interface SysRoleService {
    List<SysRole> listRolesByUserId(Long userId);
}
