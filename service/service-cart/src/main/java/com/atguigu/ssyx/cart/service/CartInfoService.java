package com.atguigu.ssyx.cart.service;

import com.atguigu.ssyx.model.order.CartInfo;

import java.util.List;

public interface CartInfoService {
    void addToCart(Long userId, Long skuId, Integer skuNum);

    void deleteCart(Long userId, Long skuId);

    void deleteAllCart(Long userId);

    void batchDeleteCart(Long userId, List<Long> skuIds);

    List<CartInfo> getCartList(Long userId);

    void checkCart(Long userId, Long skuId, Integer isChecked);

    void checkAllCart(Long userId, Integer isChecked);

    void batchCheckCart(Long userId, List<Long> skuIdList, Integer isChecked);

    List<CartInfo> getCartCheckedList(Long userId);

    void deleteCartChecked(Long userId);
}
