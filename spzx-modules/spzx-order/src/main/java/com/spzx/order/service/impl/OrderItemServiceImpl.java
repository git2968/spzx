package com.spzx.order.service.impl;

import java.util.List;
import java.util.Arrays;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spzx.order.mapper.OrderItemMapper;
import com.spzx.order.domain.OrderItem;
import com.spzx.order.service.IOrderItemService;

/**
 * 订单项信息Service业务层处理
 *
 * @author atguigu
 * @date 2025-04-29
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService
{
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 查询订单项信息列表
     *
     * @param orderItem 订单项信息
     * @return 订单项信息
     */
    @Override
    public List<OrderItem> selectOrderItemList(OrderItem orderItem)
    {
        return orderItemMapper.selectOrderItemList(orderItem);
    }

}
