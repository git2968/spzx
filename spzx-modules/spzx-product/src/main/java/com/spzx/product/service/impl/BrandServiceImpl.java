package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.product.api.domain.Brand;
import com.spzx.product.mapper.BrandMapper;
import com.spzx.product.service.IBrandService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {
    @Override
    public List<Brand> selectBrandList(Brand brand) {
        LambdaQueryWrapper<Brand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(brand.getName()),Brand::getName,brand.getName());
        queryWrapper.like(StringUtils.hasText(brand.getLogo()),Brand::getLogo,brand.getLogo());
        queryWrapper.eq(StringUtils.hasText(brand.getRemark()),Brand::getRemark,brand.getRemark());
        return baseMapper.selectList(queryWrapper);
    }
}
