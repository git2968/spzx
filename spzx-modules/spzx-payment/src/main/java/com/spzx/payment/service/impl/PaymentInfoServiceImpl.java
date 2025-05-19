package com.spzx.payment.service.impl;

import java.util.List;
import java.util.Arrays;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.utils.DateUtils;
import com.spzx.order.api.RemoteOrderInfoService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.api.domain.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spzx.payment.mapper.PaymentInfoMapper;
import com.spzx.payment.domain.PaymentInfo;
import com.spzx.payment.service.IPaymentInfoService;

/**
 * 付款信息Service业务层处理
 *
 * @author atguigu
 * @date 2024-08-03
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements IPaymentInfoService
{
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private RemoteOrderInfoService remoteOrderInfoService;

    /**
     * 查询付款信息列表
     *
     * @param paymentInfo 付款信息
     * @return 付款信息
     */
    @Override
    public List<PaymentInfo> selectPaymentInfoList(PaymentInfo paymentInfo)
    {
        return paymentInfoMapper.selectPaymentInfoList(paymentInfo);
    }

    @Override
    public PaymentInfo savePaymentInfo(String orderNo) {
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        if(null == paymentInfo) {
            R<OrderInfo> orderInfoResult = remoteOrderInfoService.getByOrderNo(orderNo, SecurityConstants.INNER);
            if (R.FAIL == orderInfoResult.getCode()) {
                throw new ServiceException(orderInfoResult.getMsg());
            }
            OrderInfo orderInfo = orderInfoResult.getData();

            paymentInfo = new PaymentInfo();
            paymentInfo.setUserId(orderInfo.getUserId());
            String content = "";
            for(OrderItem item : orderInfo.getOrderItemList()) {
                content += item.getSkuName() + " ";
            }
            paymentInfo.setContent(content);
            paymentInfo.setAmount(orderInfo.getTotalAmount());
            paymentInfo.setOrderNo(orderNo);
            paymentInfo.setPaymentStatus(0);
            paymentInfoMapper.insert(paymentInfo);
        }
        return paymentInfo;
    }

}
