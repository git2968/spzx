package com.spzx.channel.service.impl;

import com.spzx.channel.service.ICategoryService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.domain.vo.CategoryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class CategoryServiceImpl implements ICategoryService {
    
    @Autowired
    private RemoteCategoryService remoteCategoryService;

    @Override
    public List<CategoryVo> tree() {
        R<List<CategoryVo>> categoryVoListResult = remoteCategoryService.tree(SecurityConstants.INNER);
        if (R.FAIL == categoryVoListResult.getCode()) {
            log.error(categoryVoListResult.getMsg());
        }
        return categoryVoListResult.getData();
    }

}