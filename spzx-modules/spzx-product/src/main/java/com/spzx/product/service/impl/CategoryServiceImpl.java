package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.core.utils.bean.BeanValidators;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.Category;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.system.api.domain.SysUser;
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

    @Override
    public String importCategory(List<Category> categoryList, boolean isUpdateSupport, String operName) {

        if (StringUtils.isNull(categoryList) || categoryList.size() == 0)
        {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (Category category : categoryList)
        {
            try
            {
                // 验证是否存在这个用户
                Category c = baseMapper.selectById(category.getId());
                if (StringUtils.isNull(c))
                {
                    category.setCreateBy(operName);
                    baseMapper.insert(category);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、类别 " + category.getName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    category.setUpdateBy(operName);
                    baseMapper.updateById(category);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、类别 " + category.getName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、类别 " + category.getName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + category.getName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
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
