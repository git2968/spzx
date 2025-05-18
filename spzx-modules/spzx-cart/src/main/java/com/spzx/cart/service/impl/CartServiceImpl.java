package com.spzx.cart.service.impl;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.service.ICartService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.vo.SkuPriceVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements ICartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RemoteProductService remoteProductService;

    @Override
    public void addToCart(Long skuId, Integer skuNum) {
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> hashOps= redisTemplate.boundHashOps(cartKey);
        String hashKey = skuId.toString();
        Integer threshold = 99;
        if(hashOps.hasKey(hashKey)){
            CartInfo cartInfo = hashOps.get(hashKey);
            int count = cartInfo.getSkuNum() + skuNum;
            cartInfo.setSkuNum(count>threshold?threshold:skuNum);
            hashOps.put(hashKey,cartInfo);
        }else{
            CartInfo cartInfo = new CartInfo();
            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum>threshold?threshold:skuNum);
            cartInfo.setUpdateTime(new Date());
            cartInfo.setCreateTime(new Date());

            R<ProductSku> productSkuResult = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);
            if(R.FAIL==productSkuResult.getCode()){
                throw new ServiceException(productSkuResult.getMsg());
            }
            ProductSku productSku = productSkuResult.getData();
            cartInfo.setSkuId(skuId);
            cartInfo.setSkuName(productSku.getSkuName());
            cartInfo.setThumbImg(productSku.getThumbImg());
            cartInfo.setSkuPrice(productSku.getSalePrice());
            cartInfo.setCartPrice(productSku.getSalePrice());

            hashOps.put(hashKey,cartInfo);
        }
    }

    @Override
    public List<CartInfo> getCartList() {
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        List<CartInfo> cartInfoList = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(cartInfoList)){
            List<CartInfo> list = cartInfoList.stream()
                    .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                    .collect(Collectors.toList());

            R<List<SkuPriceVo>> skuPriceList = remoteProductService.getSkuPriceList(list.stream()
                    .map(CartInfo::getSkuId)
                    .collect(Collectors.toList()), SecurityConstants.INNER);
            if (R.FAIL == skuPriceList.getCode()){
                throw new ServiceException(skuPriceList.getMsg());
            }

            List<SkuPriceVo> skuPriceVoList = skuPriceList.getData();
            Map<Long, BigDecimal> skuPriceMap = skuPriceVoList.stream().collect(Collectors.toMap(
                    SkuPriceVo::getSkuId, SkuPriceVo::getSalePrice));
            list.forEach(cartInfo -> {
                cartInfo.setSkuPrice(skuPriceMap.get(cartInfo.getSkuId()));
            });


            return list;
        }

        return new ArrayList<>();

    }

    @Override
    public void deleteCart(Long skuId) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        //获取缓存对象
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        hashOperations.delete(skuId.toString());
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        // 修改缓存
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        // 先获取用户选择的商品
        if (hashOperations.hasKey(skuId.toString())) {
            CartInfo cartInfoUpd = hashOperations.get(skuId.toString());
            // cartInfoUpd 写会缓存
            cartInfoUpd.setIsChecked(isChecked);
            // 更新缓存
            hashOperations.put(skuId.toString(), cartInfoUpd);
        }
    }

    @Override
    public void allCheckCart(Integer isChecked) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = hashOperations.values();
        cartInfoList.forEach(item -> {
            CartInfo cartInfoUpd = hashOperations.get(item.getSkuId().toString());
            cartInfoUpd.setIsChecked(isChecked);

            // 更新缓存
            hashOperations.put(item.getSkuId().toString(), cartInfoUpd);
        });
    }

    @Override
    public void clearCart() {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        //获取缓存对象
        redisTemplate.delete(cartKey);
    }

    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();

        String cartKey = this.getCartKey(userId);
        List<CartInfo> cartCachInfoList = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(cartCachInfoList)) {
            for (CartInfo cartInfo : cartCachInfoList) {
                // 获取选中的商品！
                if (cartInfo.getIsChecked().intValue() == 1) {
                    cartInfoList.add(cartInfo);
                }
            }
        }
        return cartInfoList;
    }

    @Override
    public Boolean updateCartPrice(Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations hashOps = redisTemplate.boundHashOps(cartKey);
        if(!CollectionUtils.isEmpty(hashOps.values())){
            List<CartInfo> cartInfoList =hashOps.values();
            for (CartInfo cartInfo : cartInfoList) {
                R<SkuPriceVo> skuPriceResult = remoteProductService.getSkuPrice(cartInfo.getSkuId(), SecurityConstants.INNER);
                if (R.FAIL == skuPriceResult.getCode()) {
                    throw new ServiceException(skuPriceResult.getMsg());
                }
                SkuPriceVo skuPrice = skuPriceResult.getData();
                cartInfo.setSkuPrice(skuPrice.getSalePrice());
                hashOps.put(cartInfo.getSkuId().toString(),cartInfo);
            }
        }
        return true;
    }

    @Override
    public Boolean deleteCartCheckedList(Long userId) {
        String cartKey = this.getCartKey(userId);
        List<CartInfo> cartInfoList = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(cartInfoList)){
            cartInfoList.forEach(cartInfo -> {
                if (cartInfo.getIsChecked().intValue() == 1) {
                    redisTemplate.opsForHash().delete(cartKey,cartInfo.getSkuId().toString());
                }
            });
        }
        return true;
    }

    private String getCartKey(Long userId){
        return "user:cart:" + userId;
    }

}