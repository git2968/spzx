package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.mapper.CategoryBrandMapper;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.ICategoryBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 分类品牌 服务实现类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Service
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements ICategoryBrandService {

    @Autowired
    private ICategoryService categoryService;
    @Override
    public List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand) {
        return baseMapper.selectCategoryBrandList(categoryBrand);
    }

    @Override
    public CategoryBrand selectCategoryBrandById(Long id) {
        CategoryBrand categoryBrand = baseMapper.selectById(id);
        List<Long> categoryIdList = categoryService.getAllCategoryIdList(categoryBrand.getCategoryId());
        categoryBrand.setCategoryIdList(categoryIdList);
        return categoryBrand;
    }

    @Override
    public int insertCategoryBrand(CategoryBrand categoryBrand) {
        LambdaQueryWrapper<CategoryBrand> categoryBrandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryBrandLambdaQueryWrapper
                .eq(CategoryBrand::getCategoryId,categoryBrand.getCategoryId())
                .eq(CategoryBrand::getBrandId,categoryBrand.getBrandId());
        if (baseMapper.selectCount(categoryBrandLambdaQueryWrapper)>0){
            throw new ServiceException("该分类下已有该品牌");
        }
        return baseMapper.insert(categoryBrand);
    }

    @Override
    public int updateCategoryBrand(CategoryBrand categoryBrand) {
        CategoryBrand categoryBrandFromDB = baseMapper.selectById(categoryBrand.getId());
        if (categoryBrandFromDB.getCategoryId().equals(categoryBrand.getCategoryId())
                && categoryBrandFromDB.getBrandId().equals(categoryBrand.getBrandId())){
            return 1;
        }
        LambdaQueryWrapper<CategoryBrand> categoryBrandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryBrandLambdaQueryWrapper
                .eq(CategoryBrand::getCategoryId,categoryBrand.getCategoryId())
                .eq(CategoryBrand::getBrandId,categoryBrand.getBrandId());
        if (baseMapper.selectCount(categoryBrandLambdaQueryWrapper)>0){
            throw new ServiceException("该分类下已有该品牌");
        }
        return baseMapper.updateById(categoryBrand);
    }

    @Override
    public List<Brand> selectBrandListByCategoryId(Long categoryId) {
        return baseMapper.selectBrandListByCategoryId(categoryId);
    }
}
