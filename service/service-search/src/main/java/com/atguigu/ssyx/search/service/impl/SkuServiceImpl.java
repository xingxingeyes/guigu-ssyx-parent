package com.atguigu.ssyx.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.ssyx.activity.client.ActivityFeignClient;
import com.atguigu.ssyx.client.product.ProductFeignClient;
import com.atguigu.ssyx.common.auth.AuthContextHolder;
import com.atguigu.ssyx.enums.SkuType;
import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.model.search.SkuEs;
import com.atguigu.ssyx.search.repository.SkuRepository;
import com.atguigu.ssyx.search.service.SkuService;
import com.atguigu.ssyx.vo.search.SkuEsQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private SkuRepository skuEsRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 上架商品列表
     *
     * @param skuId
     */
    @Override
    public void upperSku(Long skuId) {
        log.info("upperSku：" + skuId);
        SkuEs skuEs = new SkuEs();

        //查询sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (null == skuInfo) return;

        // 查询分类
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());
        if (category != null) {
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName() + "," + skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if (skuInfo.getSkuType() == SkuType.COMMON.getCode()) {
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        } else {
            //TODO 待完善-秒杀商品

        }
        SkuEs save = skuEsRepository.save(skuEs);
        log.info("upperSku：" + JSON.toJSONString(save));
    }

    /**
     * 下架商品列表
     *
     * @param skuId
     */
    @Override
    public void lowerSku(Long skuId) {
        this.skuEsRepository.deleteById(skuId);
    }

    @Override
    public List<SkuEs> findHotSkuList() {
        // 0代表第一页
        Pageable pageable = PageRequest.of(0, 3);
        Page<SkuEs> pageModel = skuEsRepository.findByOrderByHotScoreDesc(pageable);
        List<SkuEs> skuEsList = pageModel.getContent();
        return skuEsList;
    }

    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {
        // 1.想skuESQueryVo设置wareId,当前登录用户的仓库id
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());
        Page<SkuEs> pageModel = null;
        // 2.调用SkuRepository方法，根据springData明明规则定义方法，进行条件查询
        // 判断keyword是否为空，若为空，根据仓库id+分类id查询
        String keyword = skuEsQueryVo.getKeyword();
        if (StringUtils.isEmpty(keyword)) {
            pageModel = skuEsRepository.findByCategoryIdAndWareId(skuEsQueryVo.getCategoryId(), skuEsQueryVo.getWareId(), pageable);
        } else {
            // 如果keyword不为空根据仓库id+keyword进行查询
            pageModel = skuEsRepository.findByKeywordAndWareId(keyword, skuEsQueryVo.getWareId(), pageable);
        }
        // 3.查询商品参见优惠活动
        List<SkuEs> skuEsList = pageModel.getContent();
        if (!CollectionUtils.isEmpty(skuEsList)) {
            List<Long> skuIdList = skuEsList.stream().map(item -> item.getId()).collect(Collectors.toList());
            // 根据skuId列表远程调用，调用service-activity里面的接口得到数据
            // 返回Map<Long,List<String>>
            // map集合key就是skuId值，Long类型
            // map集合value是list集合，sku参与活动里面多个规则名称列表
            // 一个商品参加一个活动，一个活动可以有多个规则
            // 比如有活动：中秋节满减活动
            // 一个活动可以有多个规则
            // 中秋节满减活动有两个规则：满20元减1元，满58元减5元
            Map<Long, List<String>> skuIdToRuleListMap = activityFeignClient.findActivity(skuIdList);

            // 封装获取数据到skuEs里面ruleList属性里面
            if (skuIdToRuleListMap != null) {
                skuEsList.forEach(item -> {
                    item.setRuleList(skuIdToRuleListMap.get(item.getId()));
                });
            }
        }
        return pageModel;
    }

    @Override
    public void incrHotScore(Long skuId) {
        String key = "hotScore";
        Double hotScore = redisTemplate.opsForZSet().incrementScore(key, "skuId:" + skuId, 1);
        if (hotScore % 10 == 0) {
            Optional<SkuEs> optional = skuEsRepository.findById(skuId);
            SkuEs skuEs = optional.get();
            skuEs.setHotScore(Math.round(hotScore));
            skuEsRepository.save(skuEs);
        }
    }
}
