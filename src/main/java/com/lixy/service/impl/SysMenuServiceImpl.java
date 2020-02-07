package com.lixy.service.impl;

import com.lixy.entity.SysMenu;
import com.lixy.mapper.SysMenuMapper;
import com.lixy.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysMenuServiceImpl implements SysMenuService {


    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Override
    public List<SysMenu> listByUserId(Long userId) {
        return sysMenuMapper.listByUserId(userId);
    }

    @Override
    public List<SysMenu> listUrlAndPermission() {
        return sysMenuMapper.listUrlAndPermission();
    }
}
