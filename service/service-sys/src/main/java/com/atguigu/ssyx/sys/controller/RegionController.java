package com.atguigu.ssyx.sys.controller;


import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.sys.RegionWare;
import com.atguigu.ssyx.model.sys.Ware;
import com.atguigu.ssyx.sys.service.RegionService;
import com.atguigu.ssyx.sys.service.RegionWareService;
import com.atguigu.ssyx.sys.service.WareService;
import com.atguigu.ssyx.vo.sys.RegionVo;
import com.atguigu.ssyx.vo.sys.RegionWareQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 地区表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-07-31
 */
@Api(tags = "开通区域接口")
@RestController
@RequestMapping("/admin/sys/region")
// @CrossOrigin 使用gateway就不用跨域注解了
public class RegionController {

    @Autowired
    private RegionService regionService;

    @ApiOperation("根据关键字获取地区列表")
    @GetMapping("findRegionByKeyword/{keyword}")
    public Result findRegionByKeyword(@PathVariable("keyword") String keyword) {
        return Result.ok(regionService.findRegionByKeyword(keyword));
    }
}

