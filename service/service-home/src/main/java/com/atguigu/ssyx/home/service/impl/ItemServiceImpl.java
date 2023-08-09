package com.atguigu.ssyx.home.service.impl;

import com.atguigu.ssyx.activity.client.ActivityFeignClient;
import com.atguigu.ssyx.client.product.ProductFeignClient;
import com.atguigu.ssyx.home.service.ItemService;
import com.atguigu.ssyx.search.SkuFeignClient;
import com.atguigu.ssyx.vo.product.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private SkuFeignClient skuFeignClient;
    @Autowired
    private ActivityFeignClient activityFeignClient;
    @Autowired
    private ProductFeignClient productFeignClient;



    @Override
    public Map<String, Object> item(Long skuId, Long userId) {
        Map<String, Object> result = new HashMap<String, Object>();

        CompletableFuture<SkuInfoVo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVO(skuId);
            result.put("skuInfoVo", skuInfoVo);
            return skuInfoVo;
        },threadPoolExecutor);

        CompletableFuture<Void> activityCompletableFuture = CompletableFuture.runAsync(() -> {
            Map<String, Object> activityMap = activityFeignClient.findActivityAndCoupon(skuId,userId);
            result.putAll(activityMap);
        },threadPoolExecutor);

        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            skuFeignClient.incrHotScore(skuId);
        },threadPoolExecutor);

        CompletableFuture.allOf(skuInfoCompletableFuture, activityCompletableFuture, hotCompletableFuture).join();
        return result;
    }
}
