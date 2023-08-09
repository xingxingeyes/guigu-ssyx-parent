package com.atguigu.ssyx.search;

import com.atguigu.ssyx.model.search.SkuEs;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "service-search")
public interface SkuFeignClient {

    // 获取爆款商品
    @GetMapping("/api/search/sku/inner/findHotSkuList")
    public List<SkuEs> findHotSkuList();

    @ApiOperation(value = "更新商品incrHotScore")
    @GetMapping("/api/search/sku/inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId);
}
