package com.atguigu.ssyx.acl.controller;

import com.atguigu.ssyx.acl.service.RoleService;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.acl.Role;
import com.atguigu.ssyx.vo.acl.RoleQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色接口")
@RestController
@RequestMapping("/admin/acl/role")
// @CrossOrigin 使用gateway就不用跨域注解了 //跨域
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation("角色条件分页查询")
    @GetMapping("{current}/{limit}")
    public Result PageList(@PathVariable Long current, @PathVariable Long limit, RoleQueryVo roleQueryVo) {
        //1 创建page对象，传递当前页和每页记录数
        Page<Role> pageParam = new Page<>(current, limit);
        //2.调用service方法实现分页查询，返回分页对象
        IPage<Role> pageModel = roleService.selectRolePage(pageParam, roleQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation("根据id查询角色")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.ok(role);
    }

    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody Role role) {
        boolean is_success = roleService.save(role);
        return is_success == true ? Result.ok(null) : Result.fail(null);
    }

    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody Role role) {
        boolean is_success = roleService.updateById(role);
        return is_success == true ? Result.ok(null) : Result.fail(null);
    }

    @ApiOperation("根据id删除角色")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean is_success = roleService.removeById(id);
        return is_success == true ? Result.ok(null) : Result.fail(null);
    }

    @ApiOperation("批量删除多个角色")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids) {
        boolean is_success = roleService.removeByIds(ids);
        return is_success == true ? Result.ok(null) : Result.fail(null);
    }


}
