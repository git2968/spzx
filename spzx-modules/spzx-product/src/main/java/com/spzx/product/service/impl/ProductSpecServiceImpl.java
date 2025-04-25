package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.mapper.ProductSpecMapper;
import com.spzx.product.service.ICategoryService;
import com.spzx.product.service.IProductSpecService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品规格 服务实现类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec> implements IProductSpecService {

    @Autowired
    private ICategoryService categoryService;
    @Override
    public List<ProductSpec> selectProductSpecList(ProductSpec productSpec) {
        return baseMapper.selectProductSpecList(productSpec);
    }

    @Override
    public ProductSpec selectProductSpecById(Long id) {
        ProductSpec productSpec = baseMapper.selectById(id);
        List<Long> allCategoryIdList = categoryService.getAllCategoryIdList(productSpec.getCategoryId());
        productSpec.setCategoryIdList(allCategoryIdList);
        return productSpec;

    }

    @Override
    public List<ProductSpec> selectProductSpecListByCategoryId(Long categoryId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<ProductSpec>()
                        .eq(ProductSpec::getCategoryId, categoryId)
        );
    }
}
