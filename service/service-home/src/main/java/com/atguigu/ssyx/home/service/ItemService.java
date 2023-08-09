package com.atguigu.ssyx.home.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface ItemService {
    Map<String, Object> item(Long id, Long userId);
}
