package com.spzx.user.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.utils.DateUtils;
import com.spzx.user.service.IRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spzx.user.mapper.UserAddressMapper;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.service.IUserAddressService;

/**
 * 用户地址Service业务层处理
 *
 * @author atguigu
 * @date 2025-04-28
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService
{
    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private IRegionService regionService;

    /**
     * 查询用户地址列表
     *
     * @return 用户地址
     */
    @Override
    public List<UserAddress> selectUserAddressList(Long userId)
    {
        return userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId));
    }

    @Override
    public List<UserAddress> selectUserAddressListByUserId(Long userId)
    {
        return baseMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
        );
    }

    @Override
    public int insertUserAddress(UserAddress userAddress) {
        String provinceName = regionService.getNameByCode(userAddress.getProvinceCode());
        String cityName = regionService.getNameByCode(userAddress.getCityCode());
        String districtName = regionService.getNameByCode(userAddress.getDistrictCode());
        userAddress.setFullAddress(provinceName + cityName + districtName + userAddress.getAddress());
        userAddress.setCreateTime(DateUtils.getNowDate());

        if(userAddress.getIsDefault().intValue()==1){
            LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserAddress::getUserId, userAddress.getUserId())
                    .set(UserAddress::getIsDefault, 0);

            baseMapper.update(null,updateWrapper);
        }
        userAddress.setUserId(SecurityContextHolder.getUserId());
        return baseMapper.insert(userAddress);
    }

    @Override
    public int updateUserAddress(UserAddress userAddress) {
        String provinceName = regionService.getNameByCode(userAddress.getProvinceCode());
        String cityName = regionService.getNameByCode(userAddress.getCityCode());
        String districtName = regionService.getNameByCode(userAddress.getDistrictCode());
        String fullAddress = provinceName + cityName + districtName + userAddress.getAddress();
        userAddress.setFullAddress(fullAddress);
        userAddress.setUpdateTime(DateUtils.getNowDate());
        //如果是默认地址，其他地址更新为非默认地址
        if(userAddress.getIsDefault().intValue() == 1) {
            UserAddress updateUserAddress = new UserAddress();
            updateUserAddress.setIsDefault(0);
            userAddressMapper.update(updateUserAddress, new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userAddress.getUserId()));
        }
        return userAddressMapper.updateById(userAddress);
    }

}
