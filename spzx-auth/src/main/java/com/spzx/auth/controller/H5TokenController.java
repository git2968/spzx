package com.spzx.auth.controller;

import com.spzx.auth.form.LoginBody;
import com.spzx.auth.form.RegisterBody;
import com.spzx.auth.service.H5LoginService;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.utils.JwtUtils;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.security.auth.AuthUtil;
import com.spzx.common.security.service.TokenService;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.system.api.model.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class H5TokenController {

    @Autowired
    private H5LoginService h5LoginService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/h5/register")
    public R<?> register(@RequestBody RegisterBody registerBody)
    {
        // 用户注册
        h5LoginService.register(registerBody);
        return R.ok();
    }

    @PostMapping("/h5/login")
    public R<?> login(@RequestBody LoginBody form)
    {
        // 用户登录
        LoginUser userInfo = h5LoginService.login(form.getUsername(), form.getPassword());
        // 获取登录token
        return R.ok(tokenService.createToken(userInfo));
    }

    @DeleteMapping("/h5/logout")
    public R<?> logout(HttpServletRequest request)
    {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            //sysLoginService.logout(username);
        }
        return R.ok();
    }

}
