package com.atguigu.ssyx.acl.controller;

import com.atguigu.ssyx.acl.service.PermissionService;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.acl.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/acl/permission")
@CrossOrigin
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    // 查询所有菜单
    @ApiOperation("查询所有菜单")
    @GetMapping
    public Result list() {
        List<Permission> list = permissionService.queryAllPermission();
        return Result.ok(list);
    }
    // 添加菜单
    @ApiOperation("添加菜单")
    @PostMapping
    public Result save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.ok(null);
    }

    // 修改菜单
    @ApiOperation("修改菜单")
    @PutMapping
    public Result update(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return Result.ok(null);
    }
    // 删除菜单
    @ApiOperation("查询所有菜单")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id) {
        permissionService.removeChildById(id);
        return Result.ok(null);
    }

    //查看某个角色的权限列表
    @ApiOperation("查看某个角色的权限列表")
    @GetMapping("/toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId) {
//        Map<String, Object> map = permissionService.getPermissionByRoleId(roleId);
        return Result.ok(null);
    }


}
