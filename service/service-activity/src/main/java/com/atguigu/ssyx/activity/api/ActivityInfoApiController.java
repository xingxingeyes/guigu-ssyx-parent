package com.atguigu.ssyx.activity.api;

import com.atguigu.ssyx.activity.service.ActivityInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "促销与优惠券接口")
@RestController
@RequestMapping("/api/activity")
@Slf4j
public class ActivityInfoApiController {

    @Resource
    private ActivityInfoService activityInfoService;

    @ApiOperation(value = "根据skuId列表获取促销信息")
    @PostMapping("inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuList) {
       return activityInfoService.findActivity(skuList);
    }

    @ApiOperation(value = "根据skuId获取促销与优惠券信息")
    @GetMapping("inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable Long skuId, @PathVariable Long userId) {
        return activityInfoService.findActivityAndCoupon(skuId, userId);
    }

}
