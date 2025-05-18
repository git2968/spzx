package com.spzx.product.mapper;

import com.spzx.product.api.domain.Brand;
import com.spzx.product.domain.CategoryBrand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 分类品牌 Mapper 接口
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface CategoryBrandMapper extends BaseMapper<CategoryBrand> {

    List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand);

    List<Brand> selectBrandListByCategoryId(Long categoryId);
}
