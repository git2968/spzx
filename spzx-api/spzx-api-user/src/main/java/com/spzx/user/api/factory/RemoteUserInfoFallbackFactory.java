package com.spzx.user.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.user.api.RemoteUserInfoService;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteUserInfoFallbackFactory implements FallbackFactory<RemoteUserInfoService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteUserInfoFallbackFactory.class);

    @Override
    public RemoteUserInfoService create(Throwable cause) {
        log.error("用户服务调用失败:{}", cause.getMessage());
        return new RemoteUserInfoService()
        {

            @Override
            public R<Boolean> register(UserInfo userInfo, String source) {
                return R.fail("会员注册失败:" + cause.getMessage());
            }

            @Override
            public R<UserInfo> getUserInfo(String username, String source) {
                return R.fail("根据用户名获取会员信息失败:" + cause.getMessage());
            }

            @Override
            public R<Boolean> updateUserLogin(UpdateUserLogin updateUserLogin, String source) {
                return R.fail("更新会员登录信息失败:" + cause.getMessage());
            }

        };
    }
}
