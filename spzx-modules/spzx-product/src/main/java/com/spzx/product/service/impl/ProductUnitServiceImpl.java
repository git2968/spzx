package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.mapper.ProductUnitMapper;
import com.spzx.product.service.IProductUnitService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品单位 服务实现类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Service
public class ProductUnitServiceImpl extends ServiceImpl<ProductUnitMapper, ProductUnit> implements IProductUnitService {

    @Override
    public List<ProductUnit> selectProductUnitList(ProductUnit productUnit) {
        LambdaQueryWrapper<ProductUnit> productUnitLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productUnitLambdaQueryWrapper.like(StringUtils.hasText(productUnit.getName()), ProductUnit::getName, productUnit.getName());
        return baseMapper.selectList(productUnitLambdaQueryWrapper);
    }
}
