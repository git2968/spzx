package com.spzx.product.service.impl;

import com.spzx.common.core.utils.uuid.UUID;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.mapper.ProductSkuMapper;
import com.spzx.product.service.IProductSkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 商品sku 服务实现类
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Service
public class ProductSkuServiceImpl extends ServiceImpl<ProductSkuMapper, ProductSku> implements IProductSkuService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ProductSku getProduceSKU(Long skuId) {
        try {
            //1.优先从缓存中获取数据
            //1.1 构建业务数据Key 形式：前缀+业务唯一标识
            String dataKey = "product:sku:" + skuId;
            //1.2 查询Redis获取业务数据
            ProductSku productSku = (ProductSku) redisTemplate.opsForValue().get(dataKey);
            //1.3 命中缓存则直接返回
            if (productSku != null) {
                return productSku;
            }
            //2.尝试获取分布式锁（set k v ex nx可能获取锁失败）
            //2.1 构建锁key
            String lockKey = "product:sku:lock:" + skuId;
            //2.2 采用UUID作为线程标识
            String lockVal = UUID.randomUUID().toString().replaceAll("-", "");
            //2.3 利用Redis提供set nx ex 获取分布式锁
            Boolean flag = redisTemplate.opsForValue().setIfAbsent(lockKey, lockVal, 5, TimeUnit.SECONDS);
            if (flag) {
                //3.获取锁成功执行业务,将查询业务数据放入缓存Redis
                try {
                    productSku = this.getProductSkuFromDB(skuId);
                    long ttl = productSku == null ? 1 * 60 : 10 * 60;
                    if (productSku == null) {
                        productSku = new ProductSku();
                    }
                    redisTemplate.opsForValue().set(dataKey, productSku, ttl, TimeUnit.SECONDS);
                    return productSku;
                } finally {
                    //4.业务执行完毕释放锁
                    String scriptText = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                            "then\n" +
                            "    return redis.call(\"del\",KEYS[1])\n" +
                            "else\n" +
                            "    return 0\n" +
                            "end";
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    redisScript.setScriptText(scriptText);
                    redisScript.setResultType(Long.class);
                    redisTemplate.execute(redisScript, Arrays.asList(lockKey), lockVal);
                }
            } else {
                try {
                    //5.获取锁失败则自旋（业务要求必须执行）
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return this.getProduceSKU(skuId);
            }
        } catch (Exception e) {
            //兜底处理方案：Redis服务有问题，将业务数据获取自动从数据库获取
            log.error("[商品服务]查询商品信息异常：{}", e);
            return this.getProductSkuFromDB(skuId);
        }
    }
    public ProductSku getProductSkuFromDB(Long skuId) {
        return baseMapper.selectById(skuId);
    }
}
