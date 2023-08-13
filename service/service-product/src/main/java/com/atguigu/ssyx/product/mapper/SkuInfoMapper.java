package com.atguigu.ssyx.product.mapper;


import com.atguigu.ssyx.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * sku信息 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-07-31
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    SkuInfo checkStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    Integer lockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}
