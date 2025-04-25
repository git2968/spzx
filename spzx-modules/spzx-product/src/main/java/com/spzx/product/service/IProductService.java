package com.spzx.product.service;

import com.spzx.product.domain.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品 服务类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
public interface IProductService extends IService<Product> {

    List<Product> selectProductList(Product product);

    void updateAuditStatus(Long id, Integer auditStatus);

    void updateStatus(Long id, Integer status);

    int insertProduct(Product product);

    Product selectProductById(Long id);

    int updateProduct(Product product);

    int deleteProductByIds(Long[] ids);
}
