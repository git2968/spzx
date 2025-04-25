package com.spzx.product.mapper;

import com.spzx.product.domain.ProductSpec;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 商品规格 Mapper 接口
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface ProductSpecMapper extends BaseMapper<ProductSpec> {

    List<ProductSpec> selectProductSpecList(ProductSpec productSpec);
}
