package com.atguigu.ssyx.activity.service.impl;


import com.atguigu.ssyx.activity.mapper.ActivityInfoMapper;
import com.atguigu.ssyx.activity.mapper.ActivityRuleMapper;
import com.atguigu.ssyx.activity.mapper.ActivitySkuMapper;
import com.atguigu.ssyx.activity.service.ActivityInfoService;
import com.atguigu.ssyx.activity.service.CouponInfoService;
import com.atguigu.ssyx.client.product.ProductFeignClient;
import com.atguigu.ssyx.enums.ActivityType;
import com.atguigu.ssyx.model.activity.ActivityInfo;
import com.atguigu.ssyx.model.activity.ActivityRule;
import com.atguigu.ssyx.model.activity.ActivitySku;
import com.atguigu.ssyx.model.activity.CouponInfo;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.activity.ActivityRuleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-08-07
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {

    @Autowired
    private ActivityRuleMapper activityRuleMapper;
    @Autowired
    private ActivitySkuMapper activitySkuMapper;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private CouponInfoService couponInfoService;


    @Override
    public IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam) {
        Page<ActivityInfo> activityInfoPage = baseMapper.selectPage(pageParam, null);
        //分页查询对象里面获取列表数据
        List<ActivityInfo> activityInfoList = activityInfoPage.getRecords();
        activityInfoList.stream().forEach(item -> {
            item.setActivityTypeString(item.getActivityType().getComment());
        });
        return activityInfoPage;
    }

    @Override
    public Map<String, Object> findActivityRuleMap(Long id) {
        Map<String, Object> result = new HashMap<String, Object>();
        // 根据活动id进行查询
        LambdaQueryWrapper<ActivityRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRule::getActivityId, id);
        List<ActivityRule> activityRuleList = activityRuleMapper.selectList(wrapper);
        result.put("activityRuleList", activityRuleList);
        List<ActivitySku> activitySkuList = activitySkuMapper.selectList(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, id));
        List<Long> skuIdList = activitySkuList.stream().map(ActivitySku::getSkuId).collect(Collectors.toList());

        List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(skuIdList);
        result.put("skuInfoList", skuInfoList);
        return result;
    }

    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        Long activityId = activityRuleVo.getActivityId();
        activityRuleMapper.delete(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, activityId));
        activitySkuMapper.delete(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, activityId));
        ActivityInfo activityInfo = baseMapper.selectById(activityId);
        ActivityType activityType = activityInfo.getActivityType();
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        activityRuleList.forEach(item -> {
            item.setActivityId(activityId);
            item.setActivityType(activityType);
            activityRuleMapper.insert(item);
        });
        for (ActivitySku activitySku : activityRuleVo.getActivitySkuList()) {
            activitySku.setActivityId(activityId);
            activitySkuMapper.insert(activitySku);
        }


    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoListByKeyword(keyword);
        if (skuInfoList == null || skuInfoList.size() == 0) {
            return null;
        }
        List<Long> skuIdList = skuInfoList.stream().map(SkuInfo::getId).collect(Collectors.toList());
        List<Long> existSkuIdList = baseMapper.selectSkuIdListExist(skuIdList);
        List<SkuInfo> findSkuList = new ArrayList<>();
        for (SkuInfo skuInfo : skuInfoList) {
            if (existSkuIdList.contains(skuInfo.getId())) {
                findSkuList.add(skuInfo);
            }
        }
        return findSkuList;
    }

    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuList) {
        Map<Long, List<String>> result = new HashMap<>();
        skuList.forEach(skuId -> {
            List<ActivityRule> activityRuleList = baseMapper.findActivityRule(skuId);
            if (!CollectionUtils.isEmpty(activityRuleList)) {
                List<String> ruleList = new ArrayList<>();
                // 处理规则名称
                for (ActivityRule activityRule : activityRuleList) {
                    ruleList.add(getRuleDesc(activityRule));
                }
                result.put(skuId, ruleList);
            }
        });
        return result;
    }

    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        // 1.根据skuId获取sku营销活动，一个活动有多少个规则
        List<ActivityRule> activityRuleList = this.findActivityRuleBySkuId(skuId);
        // 2.根据skuId + userId查询优惠券信息
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId, userId);
        // 3.封装到Map集合，返回
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("couponInfoList", couponInfoList);
        result.put("activityRuleList", activityRuleList);
        return result;
    }

    @Override
    public List<ActivityRule> findActivityRuleBySkuId(Long skuId) {
        List<ActivityRule> activityRuleList = baseMapper.findActivityRule(skuId);
        activityRuleList.forEach(item -> {
            item.setRuleDesc(this.getRuleDesc(item));
        });
        return activityRuleList;
    }

    //构造规则名称的方法
    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }
}

