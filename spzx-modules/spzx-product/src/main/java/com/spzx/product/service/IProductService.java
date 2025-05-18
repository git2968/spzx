package com.spzx.product.service;

import com.spzx.product.api.domain.SkuQuery;
import com.spzx.product.api.domain.vo.SkuPriceVo;
import com.spzx.product.api.domain.vo.SkuStockVo;
import com.spzx.product.api.domain.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.ProductDetails;

import java.util.List;
import java.util.Map;

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

    List<ProductSku> getTopSale();

    List<ProductSku> selectProductSkuList(SkuQuery skuQuery);

    SkuPriceVo getSkuPrice(Long skuId);

    ProductDetails getProductDetails(Long id);

    Map<String, Long> getSkuSpecValue(Long id);

    SkuStockVo getSkuStock(Long skuId);

    Product getProduct(Long id);

    List<SkuPriceVo> getSkuPriceList(List<Long> skuIdList);
}
