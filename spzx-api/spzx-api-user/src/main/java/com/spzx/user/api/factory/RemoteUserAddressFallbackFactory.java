package com.spzx.user.api.factory;


import com.spzx.common.core.domain.R;
import com.spzx.user.api.RemoteUserAddressService;
import com.spzx.user.api.domain.UserAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 服务降级处理
 *
 */
@Component
public class RemoteUserAddressFallbackFactory implements FallbackFactory<RemoteUserAddressService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteUserAddressFallbackFactory.class);

    @Override
    public RemoteUserAddressService create(Throwable throwable)
    {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserAddressService()
        {

            @Override
            public R<UserAddress> getUserAddress(Long id, String source) {
                return R.fail("获取用户地址失败:" + throwable.getMessage());
            }
        };
    }
}
