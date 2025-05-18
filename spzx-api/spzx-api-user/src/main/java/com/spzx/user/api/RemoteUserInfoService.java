package com.spzx.user.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.api.factory.RemoteUserInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "remoteUserInfoService",
        value = ServiceNameConstants.USER_SERVICE,
        fallbackFactory = RemoteUserInfoFallbackFactory.class)
public interface RemoteUserInfoService{
    @PostMapping("/userInfo/register")
    public R<Boolean> register(@RequestBody UserInfo userInfo,
                               @RequestHeader(SecurityConstants.INNER) String source);

    @GetMapping("/userInfo/info/{username}")
    public R<UserInfo> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @PutMapping("/userInfo/updateUserLogin")
    public R<Boolean> updateUserLogin(@RequestBody UpdateUserLogin updateUserLogin, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}
