package com.spzx.user.service;

import java.util.List;

import com.spzx.common.core.domain.R;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员Service接口
 *
 * @author atguigu
 * @date 2025-04-28
 */
public interface IUserInfoService extends IService<UserInfo>
{

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员集合
     */
    public List<UserInfo> selectUserInfoList(UserInfo userInfo);

    R<Boolean> register(UserInfo userInfo);

    UserInfo selectUserByUserName(String username);

    Boolean updateUserLogin(UpdateUserLogin updateUserLogin);
}
