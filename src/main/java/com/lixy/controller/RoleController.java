package com.lixy.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin(origins = "http://localhost:8888", maxAge = 3600)
@Controller
@RequestMapping("/role")
public class RoleController {
    /**
     * shiro 权限验证
     * @return
     */
//    @RequiresPermissions("role:list")
    @GetMapping("/list")
    @ResponseBody
    public String roleList() {
        return "roleList success";
    }

    /**
     * shiro 权限验证
     * @return
     */
//    @RequiresPermissions("role:add")
    @GetMapping("/add")
    @ResponseBody
    public String roleAdd() {
        return "roleAdd success";
    }
}
