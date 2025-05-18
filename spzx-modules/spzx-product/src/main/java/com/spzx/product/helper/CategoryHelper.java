package com.spzx.product.helper;

import com.spzx.product.api.domain.vo.CategoryVo;

import java.util.ArrayList;
import java.util.List;

public class CategoryHelper {

    /**
     * 使用递归方法建分类树
     * @param categoryVoList
     * @return
     */
    public static List<CategoryVo> buildTree(List<CategoryVo> categoryVoList) {
        List<CategoryVo> trees = new ArrayList<>();
        for (CategoryVo categoryVo : categoryVoList) {
            if (categoryVo.getParentId().longValue() == 0) {
                trees.add(findChildren(categoryVo,categoryVoList));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     * @param treeNodes
     * @return
     */
    public static CategoryVo findChildren(CategoryVo categoryVo, List<CategoryVo> treeNodes) {
        categoryVo.setChildren(new ArrayList<CategoryVo>());

        for (CategoryVo it : treeNodes) {
            if(categoryVo.getId().longValue() == it.getParentId().longValue()) {
                categoryVo.getChildren().add(findChildren(it,treeNodes));
            }
        }
        return categoryVo;
    }
}