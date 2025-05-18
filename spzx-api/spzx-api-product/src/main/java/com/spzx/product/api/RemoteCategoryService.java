package com.spzx.product.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.domain.vo.CategoryVo;
import com.spzx.product.api.factory.RemoteCategoryFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;


/**
 * 文件服务
 *
 * @author ruoyi
 */
@FeignClient(
        contextId = "remoteCategoryService",
        value = ServiceNameConstants.PRODUCT_SERVICE,
        fallbackFactory = RemoteCategoryFallbackFactory.class)

public interface RemoteCategoryService {

    @GetMapping(value = "/category/getOneCategory")
    R<List<CategoryVo>> getOneCategory(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    @GetMapping(value = "/category/tree")
    public R<List<CategoryVo>> tree(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
