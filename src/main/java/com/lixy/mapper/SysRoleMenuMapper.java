package com.lixy.mapper;

import com.lixy.entity.SysRoleMenu;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleMenuMapper {
    int deleteByPrimaryKey(SysRoleMenu key);

    int insert(SysRoleMenu record);

    int insertSelective(SysRoleMenu record);
}