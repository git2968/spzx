package com.spzx.auth.service;

import com.spzx.auth.form.RegisterBody;
import com.spzx.common.core.constant.CacheConstants;
import com.spzx.common.core.constant.Constants;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.UserConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.enums.UserStatus;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.text.Convert;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.core.utils.ip.IpUtils;
import com.spzx.common.redis.service.RedisService;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.system.api.model.LoginUser;
import com.spzx.user.api.RemoteUserInfoService;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class H5LoginService {
    @Autowired
    private RemoteUserInfoService remoteUserInfoService;
    @Autowired
    private SysRecordLogService sysRecordLogService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private RedisService redisService;

    public void register(RegisterBody registerBody) {
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String code = registerBody.getCode();
        String nickName = registerBody.getNickName();
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password))
        {
            throw new ServiceException("用户/密码必须填写");
        }
        if (username.length() != 11)
        {
            throw new ServiceException("账户长度必须是11个字符");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }
        if (StringUtils.isEmpty(code))
        {
            throw new ServiceException("验证码必须填写");
        }

        String codeValue = redisTemplate.opsForValue().get("phone:code:"+username);
        if(!registerBody.getCode().equals(codeValue)) {
            throw new ServiceException("验证码不正确");
        }

        // 注册用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        nickName = StringUtils.isEmpty(nickName) ? username : nickName;
        userInfo.setNickName(nickName);
        userInfo.setPassword(SecurityUtils.encryptPassword(password));
        userInfo.setPhone(username);
        userInfo.setStatus(1);
        R<?> registerResult = remoteUserInfoService.register(userInfo, SecurityConstants.INNER);

        if (R.FAIL == registerResult.getCode())
        {
            throw new ServiceException(registerResult.getMsg());
        }

        sysRecordLogService.recordLogininfor(username, Constants.REGISTER, "注册成功");


    }

    public LoginUser login(String username, String password)
    {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password))
        {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户/密码必须填写");
            throw new ServiceException("用户/密码必须填写");
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户密码不在指定范围");
            throw new ServiceException("用户密码不在指定范围");
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户名不在指定范围");
            throw new ServiceException("用户名不在指定范围");
        }
        // IP黑名单校验
        String blackStr = Convert.toStr(redisService.getCacheObject(CacheConstants.SYS_LOGIN_BLACKIPLIST));
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr()))
        {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "很遗憾，访问IP已被列入系统黑名单");
            throw new ServiceException("很遗憾，访问IP已被列入系统黑名单");
        }
        // 查询用户信息
        R<UserInfo> userResult = remoteUserInfoService.getUserInfo(username, SecurityConstants.INNER);

        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData()))
        {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "登录用户不存在");
            throw new ServiceException("登录用户：" + username + " 不存在");
        }

        if (R.FAIL == userResult.getCode())
        {
            throw new ServiceException(userResult.getMsg());
        }

        UserInfo userInfo = userResult.getData();
        LoginUser loginUser = new LoginUser();
        loginUser.setUserid(userInfo.getId());
        loginUser.setUsername(userInfo.getUsername());
        loginUser.setPassword(userInfo.getPassword());
        loginUser.setStatus(userInfo.getStatus()+"");
        if (UserStatus.DISABLE.getCode().equals(userInfo.getStatus()))
        {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户已停用，请联系管理员");
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        passwordService.validate(loginUser, password);
        sysRecordLogService.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");

        //更新登录信息
        UpdateUserLogin updateUserLogin = new UpdateUserLogin();
        updateUserLogin.setUserId(userInfo.getId());
        updateUserLogin.setLastLoginIp(IpUtils.getIpAddr());
        updateUserLogin.setLastLoginTime(new Date());
        remoteUserInfoService.updateUserLogin(updateUserLogin, SecurityConstants.INNER);
        return loginUser;
    }
}
