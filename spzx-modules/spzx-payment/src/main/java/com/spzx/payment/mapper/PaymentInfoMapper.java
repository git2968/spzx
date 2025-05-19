package com.spzx.payment.mapper;

import java.util.List;
import com.spzx.payment.domain.PaymentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 付款信息Mapper接口
 *
 * @author atguigu
 * @date 2024-08-03
 */
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo>
{

    /**
     * 查询付款信息列表
     *
     * @param paymentInfo 付款信息
     * @return 付款信息集合
     */
    public List<PaymentInfo> selectPaymentInfoList(PaymentInfo paymentInfo);

}
