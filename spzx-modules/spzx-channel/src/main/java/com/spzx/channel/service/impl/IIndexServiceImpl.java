package com.spzx.channel.service.impl;

import com.spzx.channel.service.IIndexService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.vo.CategoryVo;
import com.spzx.product.api.domain.ProductSku;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IIndexServiceImpl implements IIndexService {
    @Autowired
    private RemoteCategoryService remoteCategoryService;
    @Autowired
    private RemoteProductService remoteProductService;


    @Override
    public Map<String, Object> getIndexData() {

        R<List<CategoryVo>> categoryListResult = remoteCategoryService.getOneCategory(SecurityConstants.INNER);
        if (R.FAIL == categoryListResult.getCode()) {
            log.error(categoryListResult.getMsg());
        }
        R<List<ProductSku>> productSkuListResult = remoteProductService.getTopSale(SecurityConstants.INNER);
        if (R.FAIL == productSkuListResult.getCode()) {
            log.error(productSkuListResult.getMsg());
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("productSkuList",productSkuListResult.getData());
        map.put("categoryList",categoryListResult.getData());

        return map;
    }
}
