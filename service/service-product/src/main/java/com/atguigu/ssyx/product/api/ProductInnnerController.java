package com.atguigu.ssyx.product.api;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.product.service.CategoryService;
import com.atguigu.ssyx.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/")
public class ProductInnnerController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuInfoService skuInfoService;

    //根据分类id获取分类信息
    @GetMapping("inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable Long categoryId) {
        Category category = categoryService.getById(categoryId);
        return category;
    }

    // 根据skuid获取sku信息
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {
        return skuInfoService.getById(skuId);
    }

    // 根据skuid列表获取sku信息列表
    @PostMapping("inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuIdList) {
        return skuInfoService.findSkuInfoList(skuIdList);
    }

    // 根据关键字列表获取sku信息列表
    @GetMapping("inner/findSkuInfoListByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoListByKeyword(@PathVariable("keyword") String keyword) {
        return skuInfoService.findSkuInfoListByKeyword(keyword);
    }

    // 根据分类id获取列表
    @PostMapping("inner/findCategoryList")
    public List<Category> findCategoryList(@RequestBody List<Long> categoryIdList) {
        return categoryService.listByIds(categoryIdList);
    }
}
