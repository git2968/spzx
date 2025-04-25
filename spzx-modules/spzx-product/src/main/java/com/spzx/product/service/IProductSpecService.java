package com.spzx.product.service;

import com.spzx.product.domain.ProductSpec;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品规格 服务类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface IProductSpecService extends IService<ProductSpec> {

    List<ProductSpec> selectProductSpecList(ProductSpec productSpec);

    ProductSpec selectProductSpecById(Long id);

    public List<ProductSpec> selectProductSpecListByCategoryId(Long categoryId);
}
