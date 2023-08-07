package com.atguigu.ssyx.acl.controller;

import com.atguigu.ssyx.acl.service.AdminService;
import com.atguigu.ssyx.acl.service.RoleService;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.common.utils.MD5;
import com.atguigu.ssyx.model.acl.Admin;
import com.atguigu.ssyx.vo.acl.AdminQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "用户接口")
@RestController
// @CrossOrigin 使用gateway就不用跨域注解了
@RequestMapping("/admin/acl/user")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @ApiOperation("给用户分配角色")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long adminId, @RequestParam Long[] roleId) {
        roleService.saveAdminRole(adminId, roleId);
        return Result.ok(null);
    }

    //获取所有角色，和根据用户id查询用户分配角色列表
    @ApiOperation("获取用户角色")
    @GetMapping("toAssign/{adminId}")
    public Result toAssign(@PathVariable Long adminId) {
        Map<String, Object> map = roleService.getRoleByAdminId(adminId);
        return Result.ok(map);
    }


    //1.用户列表
    @ApiOperation("用户列表")
    @GetMapping("{current}/{limit}")
    public Result getPageList(@PathVariable Long current, @PathVariable Long limit, AdminQueryVo adminQueryVo) {
        Page<Admin> pageParam = new Page<>(current, limit);
        IPage<Admin> pageModel = adminService.selectAdminPage(pageParam, adminQueryVo);
        return Result.ok(pageModel);
    }

    //2.id查询用户
    @ApiOperation("根据id查询")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        return Result.ok(admin);
    }

    //3.添加用户
    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody Admin admin) {
        String password = MD5.encrypt(admin.getPassword());
        admin.setPassword(password);
        boolean is_success = adminService.save(admin);
        return is_success == true ? Result.ok(null) : Result.fail(null);
    }

    //4.修改用户
    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result update(@RequestBody Admin admin) {
        boolean is_success = adminService.updateById(admin);
        return is_success == true ? Result.ok(null) : Result.fail(null);
    }

    //5.id删除
    @ApiOperation("id删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean is_success = adminService.removeById(id);
        return is_success == true ? Result.ok(null) : Result.fail(null);
    }

    //4.修改删除
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids) {
        boolean is_success = adminService.removeByIds(ids);
        return is_success == true ? Result.ok(null) : Result.fail(null);
    }
}
