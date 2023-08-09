package com.atguigu.ssyx.client.product;

import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.product.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "service-product")
public interface ProductFeignClient {

    //根据分类id获取分类信息
    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId);

    // 根据skuid获取sku信息
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    // 根据skuid列表获取sku信息列表
    @PostMapping("/api/product/inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuIdList);

    // 根据关键字列表获取sku信息列表
    @GetMapping("/api/product/inner/findSkuInfoListByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoListByKeyword(@PathVariable("keyword") String keyword);

    // 根据分类id获取列表
    @PostMapping("/api/product/inner/findCategoryList")
    public List<Category> findCategoryList(@RequestBody List<Long> categoryIdList);

    // 获取所有分类
    @GetMapping("/api/product/inner/findAllCategoryList")
    public List<Category> findAllCategoryList();

    // 获取新人专享
    @GetMapping("/api/product/inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList();

    // 根据skuId获取skuVo信息
    @GetMapping("/api/product/inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVO(@PathVariable Long skuId);
}
