package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.product.domain.Product;
import com.spzx.product.domain.ProductDetails;
import com.spzx.product.domain.ProductSku;
import com.spzx.product.domain.SkuStock;
import com.spzx.product.mapper.ProductDetailsMapper;
import com.spzx.product.mapper.ProductMapper;
import com.spzx.product.mapper.ProductSkuMapper;
import com.spzx.product.mapper.SkuStockMapper;
import com.spzx.product.service.ICategoryBrandService;
import com.spzx.product.service.ICategoryService;
import com.spzx.product.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.Collection;
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

    @Override
    public List<Product> selectProductList(Product product) {

        return baseMapper.selectProductList(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
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
        if (status != 1 && status != -1){
            throw new ServiceException("上下架状态只能是1或-1");
        }
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        baseMapper.updateById(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
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

}
