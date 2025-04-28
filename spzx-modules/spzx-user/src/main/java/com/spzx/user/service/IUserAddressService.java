package com.spzx.user.service;

import java.util.List;
import com.spzx.user.domain.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户地址Service接口
 *
 * @author atguigu
 * @date 2025-04-28
 */
public interface IUserAddressService extends IService<UserAddress>
{

    /**
     * 查询用户地址列表
     *
     * @param userAddress 用户地址
     * @return 用户地址集合
     */
    public List<UserAddress> selectUserAddressList(UserAddress userAddress);

    public List<UserAddress> selectUserAddressListByUserId(Long userId);

}
