package com.spzx.order.mapper;

import java.util.List;
import com.spzx.order.domain.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;

/**
 * 订单Mapper接口
 *
 * @author atguigu
 * @date 2025-04-29
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo>
{

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单集合
     */
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

    public List<OrderInfo> selectUserOrderInfoList(@Param("userId") Long userId, @Param("orderStatus") Integer orderStatus);
}
