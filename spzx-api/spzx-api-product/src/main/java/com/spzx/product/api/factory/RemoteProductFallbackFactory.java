package com.spzx.product.api.factory;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductDetails;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.Product;
import com.spzx.product.api.domain.SkuQuery;
import com.spzx.product.api.domain.vo.SkuLockVo;
import com.spzx.product.api.domain.vo.SkuPriceVo;
import com.spzx.product.api.domain.vo.SkuStockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@Component
public class RemoteProductFallbackFactory implements FallbackFactory<RemoteProductService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteProductFallbackFactory.class);


    @Override
    public RemoteProductService create(Throwable throwable) {
        log.error("商品服务调用失败:{}", throwable.getMessage());
        return new RemoteProductService() {


            @Override
            public R<List<ProductSku>> getTopSale(@RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
                return null;
            }

            @Override
            public R<TableDataInfo> skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery, String source) {
                return R.fail("获取商品列表失败:" + throwable.getMessage());
            }

            @Override
            public R<ProductSku> getProductSku(Long skuId, String source) {
                return R.fail("获取商品sku失败:" + throwable.getMessage());
            }

            @Override
            public R<Product> getProduct(Long id, String source) {
                return R.fail("获取商品信息失败:" + throwable.getMessage());
            }

            @Override
            public R<SkuPriceVo> getSkuPrice(Long skuId, String source) {
                return R.fail("获取商品sku价格失败:" + throwable.getMessage());
            }

            @Override
            public R<ProductDetails> getProductDetails(Long id, String source) {
                return R.fail("获取商品详情失败:" + throwable.getMessage());
            }

            @Override
            public R<Map<String, Long>> getSkuSpecValue(Long id, String source) {
                return R.fail("获取商品sku规格失败:" + throwable.getMessage());
            }

            @Override
            public R<SkuStockVo> getSkuStock(Long skuId, String source) {
                return R.fail("获取商品sku库存失败:" + throwable.getMessage());
            }

            @Override
            public R<List<SkuPriceVo>> getSkuPriceList(List<Long> skuIdList, String source) {
                return R.fail("获取商品sku价格列表失败:" + throwable.getMessage());
            }

            @Override
            public R<String> checkAndLock(String orderNo, List<SkuLockVo> skuLockVoList, String source) {
                return R.fail("检查与锁定商品失败:" + throwable.getMessage());
            }
        };
    }
}