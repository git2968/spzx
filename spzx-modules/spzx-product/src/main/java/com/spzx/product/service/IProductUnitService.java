package com.spzx.product.service;

import com.spzx.product.domain.ProductUnit;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品单位 服务类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface IProductUnitService extends IService<ProductUnit> {

    List<ProductUnit> selectProductUnitList(ProductUnit productUnit);
}
