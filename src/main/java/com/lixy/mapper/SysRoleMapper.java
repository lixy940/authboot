package com.lixy.mapper;

import com.lixy.entity.SysRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRoleMapper {
    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Long roleId);

    void deleteByPrimaryKey(Long roleId);

    void updateByPrimaryKeySelective(SysRole sysRole);

    void updateByPrimaryKey(SysRole sysRole);

    List<SysRole> listRolesByUserId(Long userId);
}