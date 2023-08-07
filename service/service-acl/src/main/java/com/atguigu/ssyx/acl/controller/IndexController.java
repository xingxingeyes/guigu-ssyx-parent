package com.atguigu.ssyx.acl.controller;

import com.atguigu.ssyx.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@Api(tags = "登录接口")
@RestController
@RequestMapping("/admin/acl/index")
// @CrossOrigin 使用gateway就不用跨域注解了 // 允许跨域
public class IndexController {
    //1.login 登录
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login() {
        //返回token值
        Map<String, String> map = new HashMap<>();
        map.put("token", "token-admin");
        return Result.ok(map);
    }

    //url: '/admin/acl/index/info',
    //method: 'get',

    @GetMapping("info")
    @ApiOperation("获取信息")
    public Result info() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "admin");
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(map);
    }

    //url: '/admin/acl/index/logout',
    //method: 'post'
    /**
     * 3 退出
     */
    @PostMapping("logout")
    @ApiOperation("退出")
    public Result logout(){
        return Result.ok(null);
    }

}
