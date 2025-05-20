package com.spzx.payment.receiver;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.rabbitmq.client.Channel;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.payment.service.IAlipayService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentReceiver {

    @Autowired
    private IAlipayService alipayService;

    /**
     * 监听关闭交易记录消息
     *
     * @param orderNo
     * @param channel
     * @param message
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_PAYMENT_PAY, durable = "true"),
            value = @Queue(value = MqConst.QUEUE_PAYMENT_CLOSE, durable = "true"),
            key = MqConst.ROUTING_PAYMENT_CLOSE
    ))
    public void closePayment(String orderNo, Message message, Channel channel) {
        //业务处理
        if (StringUtils.isNotEmpty(orderNo)) {
            log.info("[支付服务]监听关闭交易消息：{}", orderNo);
            alipayService.closePayment(orderNo);
        }
        //手动应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}