package com.spzx.product.service;

import com.spzx.product.domain.CategoryBrand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 分类品牌 服务类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface ICategoryBrandService extends IService<CategoryBrand> {

    List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand);

    CategoryBrand selectCategoryBrandById(Long id);

    int insertCategoryBrand(CategoryBrand categoryBrand);

    int updateCategoryBrand(CategoryBrand categoryBrand);
}
