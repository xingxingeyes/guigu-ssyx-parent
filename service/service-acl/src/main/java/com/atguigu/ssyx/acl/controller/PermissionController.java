package com.atguigu.ssyx.acl.controller;

import com.atguigu.ssyx.acl.service.PermissionService;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.acl.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/acl/permission")
// @CrossOrigin 使用gateway就不用跨域注解了
public class PermissionController {

    @Autowired
    private PermissionService permissionService;
    @ApiOperation("获取菜单列表")
    @GetMapping
    public Result getList(){
       List<Permission> permissionList = permissionService.queryAllPermission();
        return Result.ok(permissionList);
    }

    @ApiOperation("添加菜单")
    @PostMapping("/save")
    public Result save(@RequestBody Permission permission){
        permissionService.save(permission);
        return Result.ok(null);
    }

    @ApiOperation("修改菜单")
    @PutMapping("/update")
    public Result update(@RequestBody Permission permission){
        permissionService.updateById(permission);
        return Result.ok(null);
    }

    @ApiOperation("递归删除菜单")
    @PutMapping("/delete/{id}")
    public Result update(@PathVariable Long id){
        permissionService.removeChildById(id);
        return Result.ok(null);
    }
    @ApiOperation("查看角色权限列表")
    @GetMapping("/toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId){
        //permissionService.removeChildById(id);
        return Result.ok(null);
    }

}
