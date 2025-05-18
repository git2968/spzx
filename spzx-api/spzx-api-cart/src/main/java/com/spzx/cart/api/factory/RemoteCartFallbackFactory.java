package com.spzx.cart.api.factory;


import com.spzx.cart.api.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 购物车降级处理
 *
 * @author atguigu
 */
@Component
public class RemoteCartFallbackFactory implements FallbackFactory<RemoteCartService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteCartFallbackFactory.class);

    @Override
    public RemoteCartService create(Throwable throwable)
    {
        log.error("购物车服务调用失败:{}", throwable.getMessage());
        return new RemoteCartService()
        {

            @Override
            public R<List<CartInfo>> getCartCheckedList(Long userId, String source) {
                return R.fail("获取用户购物车选中数据失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> updateCartPrice(Long userId, String source) {
                return R.fail("更新购物车价格失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> deleteCartCheckedList(Long userId, String source) {
                return R.fail("删除用户购物车选中数据失败:" + throwable.getMessage());
            }

        };
    }
}