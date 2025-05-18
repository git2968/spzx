package com.spzx.channel.service;

import com.spzx.channel.domain.vo.ItemVo;

public interface IItemService {

    ItemVo item(Long skuId);
}