package com.spzx.product.mapper;

import com.spzx.product.api.domain.ProductSku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.api.domain.SkuQuery;

import java.util.List;

/**
 * <p>
 * 商品sku Mapper 接口
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    List<ProductSku> selectTopSale();

    List<ProductSku> selectProductSkuList(SkuQuery skuQuery);
}
