package com.spzx.product.service;

import com.spzx.product.api.domain.vo.CategoryVo;
import com.spzx.product.domain.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品分类 服务类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface ICategoryService extends IService<Category> {

    List<Category> treeSelect(Long parentId);

    List<Long> getAllCategoryIdList(Long categoryId);

    String importCategory(List<Category> categoryList, boolean updateSupport, String operName);

    public List<CategoryVo> getOneCategory();

    List<CategoryVo> tree();
}
