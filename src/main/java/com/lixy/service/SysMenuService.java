package com.lixy.service;

import com.lixy.entity.SysMenu;

import java.util.List;

public interface SysMenuService {
    List<SysMenu> listByUserId(Long userId);

    List<SysMenu> listUrlAndPermission();

}
