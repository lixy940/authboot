package com.lixy.mapper;

import com.lixy.entity.SysUserLoginRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserLoginRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SysUserLoginRecord record);

    int insertSelective(SysUserLoginRecord record);

    SysUserLoginRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUserLoginRecord record);

    int updateByPrimaryKey(SysUserLoginRecord record);
}