package com.spzx.product.service.impl;

import com.spzx.product.domain.ProductDetails;
import com.spzx.product.mapper.ProductDetailsMapper;
import com.spzx.product.service.IProductDetailsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品sku属性表 服务实现类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Service
public class ProductDetailsServiceImpl extends ServiceImpl<ProductDetailsMapper, ProductDetails> implements IProductDetailsService {

}
