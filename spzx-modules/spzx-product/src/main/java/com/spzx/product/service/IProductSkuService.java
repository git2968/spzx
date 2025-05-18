package com.spzx.product.service;

import com.spzx.product.api.domain.ProductSku;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品sku 服务类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface IProductSkuService extends IService<ProductSku> {

    ProductSku getProduceSKU(Long skuId);
}
