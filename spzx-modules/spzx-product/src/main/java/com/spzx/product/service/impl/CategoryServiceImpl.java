package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.product.domain.Category;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品分类 服务实现类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    /**
     * 根据父记录的id返回子记录id
     * 并返回子记录中的每一条记录是否有子记录
     * @param parentId
     * @return
     */
    @Override
    public List<Category> treeSelect(Long parentId) {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getParentId, parentId);
        List<Category> categoryList = baseMapper.selectList(categoryLambdaQueryWrapper);
        if(!CollectionUtils.isEmpty(categoryList)){
            categoryList.forEach(category -> {
                LambdaQueryWrapper<Category> categoryLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
                categoryLambdaQueryWrapper1.eq(Category::getParentId, category.getId());
                category.setHasChildren(baseMapper.selectList(categoryLambdaQueryWrapper1).size() > 0);
            });
        }
        return categoryList;

    }

    @Override
    public List<Long> getAllCategoryIdList(Long categoryId) {
        List<Long> list = new ArrayList<>();
        List<Category> categoryList = getParentCategory(categoryId,new ArrayList<>());
        for (int i = categoryList.size(); i > 0; i--) {
            list.add(categoryList.get(i-1).getId());
        }
        return list;
    }

    private List<Category> getParentCategory(Long id,List<Category> list){

        while(id>0){

            LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
            categoryLambdaQueryWrapper
                    .select(Category::getId,Category::getParentId)
                    .eq(Category::getId,id);
            Category category = baseMapper.selectOne(categoryLambdaQueryWrapper);
            list.add(category);
            return getParentCategory(category.getParentId(),list);
        }

        return list;
    }
}
