package com.spzx.payment.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.utils.DateUtils;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.order.api.RemoteOrderInfoService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.api.domain.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spzx.payment.mapper.PaymentInfoMapper;
import com.spzx.payment.domain.PaymentInfo;
import com.spzx.payment.service.IPaymentInfoService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 付款信息Service业务层处理
 *
 * @author atguigu
 * @date 2024-08-03
 */
@Service
@Slf4j
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements IPaymentInfoService
{
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private RemoteOrderInfoService remoteOrderInfoService;

    @Autowired
    private RabbitService rabbitService;


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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentStatus(Map<String, String> map, Integer payType) {
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, map.get("out_trade_no")));
        //1.已支付，直接返回
        if (paymentInfo.getPaymentStatus() == 1) {
            return;
        }
        //2.订单已关闭单，走退款流程
        if (paymentInfo.getPaymentStatus() == -1) {
            log.info("订单已关闭单，退款");
            //TODO 退款业务
            return;
        }


        //更新支付信息
        paymentInfo.setPayType(payType);
        paymentInfo.setPaymentStatus(1);
        paymentInfo.setTradeNo(map.get("trade_no"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(JSON.toJSONString(map));
        paymentInfoMapper.updateById(paymentInfo);

        //基于MQ通知订单系统，修改订单状态
        rabbitService.sendMessage(MqConst.EXCHANGE_PAYMENT_PAY, MqConst.ROUTING_PAYMENT_PAY, paymentInfo.getOrderNo());
    }

}
