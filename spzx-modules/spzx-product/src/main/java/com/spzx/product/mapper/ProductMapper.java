package com.spzx.product.mapper;

import com.spzx.product.api.domain.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 商品 Mapper 接口
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface ProductMapper extends BaseMapper<Product> {

    List<Product> selectProductList(Product product);


}
