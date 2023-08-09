package com.atguigu.ssyx.activity.service.impl;


import com.atguigu.ssyx.activity.mapper.CouponInfoMapper;
import com.atguigu.ssyx.activity.mapper.CouponRangeMapper;
import com.atguigu.ssyx.activity.service.CouponInfoService;
import com.atguigu.ssyx.client.product.ProductFeignClient;
import com.atguigu.ssyx.enums.CouponRangeType;
import com.atguigu.ssyx.model.activity.CouponInfo;
import com.atguigu.ssyx.model.activity.CouponRange;
import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.activity.CouponRuleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-08-07
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Autowired
    private CouponRangeMapper couponRangeMapper;
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public IPage<CouponInfo> selectPageCouponInfo(Long page, Long limit) {
        Page<CouponInfo> pageParam = new Page<>(page,limit);
        IPage<CouponInfo> couponInfoPage = baseMapper.selectPage(pageParam, null);
        List<CouponInfo> couponInfoList = couponInfoPage.getRecords();
        couponInfoList.stream().forEach(couponInfo -> {
            couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
            CouponRangeType rangeType = couponInfo.getRangeType();
            if (rangeType != null) {
                couponInfo.setRangeTypeString(rangeType.getComment());
            }
        });

        return couponInfoPage;
    }

    @Override
    public CouponInfo getCouponInfo(Long id) {
        CouponInfo couponInfo = baseMapper.selectById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (couponInfo.getRangeType() != null) {
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }
        return couponInfo;
    }

    @Override
    public Map<String, Object> findCouponRuleList(Long id) {
        CouponInfo couponInfo = baseMapper.selectById(id);
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id));
        List<Long> rangeIdIdList = couponRangeList.stream().map(CouponRange::getRangeId).collect(Collectors.toList());
        Map<String, Object> resultMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(rangeIdIdList)) {
            if (couponInfo.getRangeType() == CouponRangeType.SKU) {
                List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(rangeIdIdList);
                resultMap.put("skuInfoList", skuInfoList);
            }else if(couponInfo.getRangeType() == CouponRangeType.CATEGORY){
                List<Category> categoryList = productFeignClient.findCategoryList(rangeIdIdList);
                resultMap.put("categoryList", categoryList);
            }
        }
        return resultMap;
    }

    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        couponRangeMapper.delete(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponRuleVo.getCouponId()));
        CouponInfo couponInfo = baseMapper.selectById(couponRuleVo.getCouponId());
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());
        baseMapper.updateById(couponInfo);
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        couponRangeList.stream().forEach(item->{
            item.setCouponId(couponRuleVo.getCouponId());
            couponRangeMapper.insert(item);
        });

    }

    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        // 根据skuId获取skuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        // 根据条件查询 skuId + 分分类Id + userId
        List<CouponInfo> couponInfoList = baseMapper.selectCouponInfoList(skuInfo.getId(), skuInfo.getCategoryId(), userId);
        return couponInfoList;
    }
}
