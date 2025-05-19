package com.spzx.payment.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.order.api.RemoteOrderInfoService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.payment.config.AlipayConfig;
import com.spzx.payment.domain.PaymentInfo;
import com.spzx.payment.service.IAlipayService;
import com.spzx.payment.service.IPaymentInfoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class AlipayServiceImpl implements IAlipayService {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private IPaymentInfoService paymentInfoService;

    @Autowired
    private RemoteOrderInfoService remoteOrderInfoService;

    @SneakyThrows
    @Override
    public String submitAlipay(String orderNo) {
        R<OrderInfo> orderInfoResult = remoteOrderInfoService.getByOrderNo(orderNo, SecurityConstants.INNER);
        if(R.FAIL == orderInfoResult.getCode()){
            throw new ServiceException("订单不存在");
        }
        OrderInfo orderInfo = orderInfoResult.getData();
        //保存支付记录
        PaymentInfo paymentInfo = paymentInfoService.savePaymentInfo(orderNo);
        // 生产二维码
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        // 同步回调
        // return_payment_url=http://sph-payment.atguigu.cn/alipay/callback/return
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        // 异步回调
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址
        // 参数
        // 声明一个map 集合
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no",paymentInfo.getOrderNo());
        map.put("product_code","QUICK_WAP_WAY");
        //map.put("total_amount",orderInfo.getTotalAmount());
        map.put("total_amount",orderInfo.getTotalAmount());
        map.put("subject",0.01);

        alipayRequest.setBizContent(JSON.toJSONString(map));

        return alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单;
    }

}