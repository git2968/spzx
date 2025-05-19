package com.spzx.product.mapper;

import com.spzx.product.domain.SkuStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;

/**
 * <p>
 * 商品sku库存表 Mapper 接口
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface SkuStockMapper extends BaseMapper<SkuStock> {
    SkuStock check(@Param("skuId") Long skuId, @Param("num")Integer num);

    Integer lock(@Param("skuId") Long skuId, @Param("num")Integer num);

    Integer unlock(@Param("skuId") Long skuId, @Param("num")Integer num);

    Integer minus(@Param("skuId") Long skuId, @Param("num")Integer num);
}
