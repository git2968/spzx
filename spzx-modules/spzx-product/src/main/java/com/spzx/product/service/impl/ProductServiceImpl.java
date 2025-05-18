package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.redis.cache.GuiguCache;
import com.spzx.product.api.domain.SkuQuery;
import com.spzx.product.api.domain.vo.SkuPriceVo;
import com.spzx.product.api.domain.vo.SkuStockVo;
import com.spzx.product.api.domain.Product;
import com.spzx.product.api.domain.ProductDetails;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.domain.SkuStock;
import com.spzx.product.mapper.ProductDetailsMapper;
import com.spzx.product.mapper.ProductMapper;
import com.spzx.product.mapper.ProductSkuMapper;
import com.spzx.product.mapper.SkuStockMapper;
import com.spzx.product.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品 服务实现类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private ProductDetailsMapper productDetailsMapper;
    @Autowired
    private SkuStockMapper skuStockMapper;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    @GuiguCache(prefix = "product_list:")
    public List<Product> selectProductList(Product product) {
        return baseMapper.selectProductList(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @GuiguCache(prefix = "product_audit:")
    public void updateAuditStatus(Long id, Integer auditStatus) {
        if (auditStatus != 1 && auditStatus != -1){
            throw new ServiceException("审核状态只能是1或-1");
        }
        Product product = new Product();
        product.setId(id);
        product.setAuditStatus(auditStatus);
        if (auditStatus == 1){
            product.setAuditMessage("审批通过");
        }else if (auditStatus == -1){
            product.setAuditMessage("审批不通过");
        }

        baseMapper.updateById(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        if(status == 1) {
            product.setStatus(1);

            //sku加入布隆过滤器
            RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("sku:bloom:filter");
            List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id));
            productSkuList.forEach(item -> {
                bloomFilter.add(item.getId());
            });
        } else {
            product.setStatus(-1);
        }
        baseMapper.updateById(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @GuiguCache(prefix = "product_insert:")
    public int insertProduct(Product product) {
        //商品
        baseMapper.insert(product);
        List<ProductSku> productSkuList = product.getProductSkuList();
        for (int i = 0; i < productSkuList.size(); i++) {
            //sku
            ProductSku productSku = productSkuList.get(i);
            productSku.setProductId(product.getId());
            productSku.setSkuCode(product.getId() + "_"+i);
            productSku.setSkuName(product.getName()+" "+productSku.getSkuSpec());
            productSkuMapper.insert(productSku);
            //库存
            SkuStock skuStock = new SkuStock();
            skuStock.setSkuId(productSku.getId());
            skuStock.setTotalNum(productSku.getStockNum());
            skuStock.setLockNum(0);
            skuStock.setAvailableNum(productSku.getStockNum());
            skuStock.setSaleNum(0);
            skuStockMapper.insert(skuStock);
        }
        //详情
        ProductDetails productDetails = new ProductDetails();
        productDetails.setProductId(product.getId());
        productDetails.setImageUrls(String.join(",",product.getDetailsImageUrlList()));
        productDetailsMapper.insert(productDetails);

        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @GuiguCache(prefix = "product_by_id:")
    public Product selectProductById(Long id) {
        //商品信息
        Product product = baseMapper.selectById(id);

        //商品SKU列表
        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id));
        //查询库存
        List<Long> skuIdList = productSkuList.stream().map(ProductSku::getId).collect(Collectors.toList());
        List<SkuStock> skuStockList = skuStockMapper
                .selectList(new LambdaQueryWrapper<SkuStock>()
                        .in(SkuStock::getSkuId, skuIdList)
                        .select(SkuStock::getSkuId, SkuStock::getTotalNum));
        Map<Long, Integer> skuIdToStockNumMap = skuStockList.stream().collect(Collectors.toMap(SkuStock::getSkuId, SkuStock::getTotalNum));
        productSkuList.forEach(item -> {
            item.setStockNum(skuIdToStockNumMap.get(item.getId()));
        });
        product.setProductSkuList(productSkuList);

        //商品详情
        ProductDetails productDetails = productDetailsMapper
                .selectOne(new LambdaQueryWrapper<ProductDetails>()
                        .eq(ProductDetails::getProductId, id));
        product.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));
        return product;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @GuiguCache(prefix = "product_update:")
    public int updateProduct(Product product) {
        baseMapper.updateById(product);

        List<ProductSku> productSkuList = product.getProductSkuList();

        productSkuList.forEach(productSku -> {
            productSku.setSkuName(product.getName()+" "+productSku.getSkuSpec());
            productSkuMapper.updateById(productSku);
            if(productSku.getStockNum()!=null){
                SkuStock skuStock = skuStockMapper.selectOne(
                        new LambdaQueryWrapper<SkuStock>()
                                .eq(SkuStock::getSkuId,productSku.getId())
                );
                skuStock.setTotalNum(productSku.getStockNum());
                int availableNum = skuStock.getTotalNum()-skuStock.getLockNum();
                skuStock.setAvailableNum(availableNum);
                skuStockMapper.updateById(skuStock);
            }
        });

        ProductDetails productDetails = productDetailsMapper.selectOne(
                new LambdaQueryWrapper<ProductDetails>()
                        .eq(ProductDetails::getProductId,product.getId())
        );
        productDetails.setImageUrls(String.join(",",product.getDetailsImageUrlList()));
        productDetailsMapper.updateById(productDetails);

        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @GuiguCache(prefix = "product_delete:")
    public int deleteProductByIds(Long[] ids) {
        baseMapper.deleteBatchIds(Arrays.asList(ids));
        //获取sku列表
        List<ProductSku> productSkuList = productSkuMapper.selectList(
                new LambdaQueryWrapper<ProductSku>()
                        .in(ProductSku::getProductId, ids)
                        .select(ProductSku::getId)
        );
        List<Long> skuIdList = productSkuList.stream().map(ProductSku::getId).collect(Collectors.toList());
        productSkuMapper.delete(
                new LambdaQueryWrapper<ProductSku>()
                        .in(ProductSku::getProductId, ids)
        );
        skuStockMapper.delete(
                new LambdaQueryWrapper<SkuStock>()
                        .in(SkuStock::getSkuId, skuIdList)
        );
        productDetailsMapper.delete(
                new LambdaQueryWrapper<ProductDetails>()
                        .in(ProductDetails::getProductId, ids)
        );
        return 1;
    }

    @Override
    @GuiguCache(prefix = "product_top_sale:")
    public List<ProductSku> getTopSale() {
        return productSkuMapper.selectTopSale();
    }

    @Override
    @GuiguCache(prefix = "product_sku_list:")
    public List<ProductSku> selectProductSkuList(SkuQuery skuQuery) {
        return productSkuMapper.selectProductSkuList(skuQuery);
    }

    @Override
    public SkuPriceVo getSkuPrice(Long skuId) {
        ProductSku productSku = productSkuMapper.selectOne(
                new LambdaQueryWrapper<ProductSku>()
                        .eq(ProductSku::getId, skuId)
                        .select(ProductSku::getSalePrice, ProductSku::getMarketPrice));
        SkuPriceVo skuPrice = new SkuPriceVo();
        BeanUtils.copyProperties(productSku, skuPrice);
        skuPrice.setSkuId(skuId);
        return skuPrice;
    }

    @Override
    @GuiguCache(prefix = "product_details:")
    public ProductDetails getProductDetails(Long id) {
        return productDetailsMapper.selectOne(
                new LambdaQueryWrapper<ProductDetails>()
                        .eq(ProductDetails::getProductId, id)
        );
    }

    @Override
    @GuiguCache(prefix = "product_sku_spec:")
    public Map<String, Long> getSkuSpecValue(Long id) {
        LambdaQueryWrapper<ProductSku> queryWrapper = new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getProductId, id)
                .select(ProductSku::getId, ProductSku::getSkuSpec);
        List<ProductSku> productSkuList = productSkuMapper.selectList(queryWrapper);
        return productSkuList.stream().collect(Collectors.toMap(item -> item.getSkuSpec(), item -> item.getId()));
    }

    @Override
    @GuiguCache(prefix = "product_sku_stock:")
    public SkuStockVo getSkuStock(Long skuId) {
        LambdaQueryWrapper<SkuStock> queryWrapper = new LambdaQueryWrapper<SkuStock>()
                .eq(SkuStock::getSkuId, skuId)
                .select(SkuStock::getAvailableNum, SkuStock::getSaleNum);
        SkuStockVo skuStockVo = new SkuStockVo();
        SkuStock skuStock = skuStockMapper.selectOne(queryWrapper);
        BeanUtils.copyProperties(skuStock, skuStockVo);
        skuStockVo.setSkuId(skuId);
        return skuStockVo;
    }

    @Override
    @GuiguCache(prefix = "product_single:")
    public Product getProduct(Long id) {
        //return this.getById(id);
        return baseMapper.selectById(id);
    }

    @Override
    public List<SkuPriceVo> getSkuPriceList(List<Long> skuIdList) {

        LambdaQueryWrapper<ProductSku> queryWrapper = new LambdaQueryWrapper<ProductSku>()
                .in(ProductSku::getId, skuIdList)
                .select(ProductSku::getId, ProductSku::getSalePrice);
        return productSkuMapper.selectList(queryWrapper).stream().map(item -> {
                SkuPriceVo skuPrice = new SkuPriceVo();
                BeanUtils.copyProperties(item, skuPrice);
                skuPrice.setSkuId(item.getId());
                return skuPrice;
                }).collect(Collectors.toList());
    }


}
