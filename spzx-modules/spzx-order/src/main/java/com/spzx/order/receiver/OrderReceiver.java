package com.spzx.order.receiver;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.rabbitmq.client.Channel;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.service.IOrderInfoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OrderReceiver {

    @Autowired
    private IOrderInfoService orderInfoService;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 延迟关闭订单消费者
     *
     * @param orderId
     * @throws IOException
     */
    @SneakyThrows
    @RabbitListener(queues = MqConst.QUEUE_CANCEL_ORDER)
    public void processCloseOrder(String orderId, Message message, Channel channel) throws IOException {
        //1.处理业务
        if (orderId != null) {
            log.info("【订单微服务】关闭订单消息：{}", orderId);
            orderInfoService.processCloseOrder(Long.parseLong(orderId));

            //发送MQ消息通知支付系统关闭可能产生本地交易记录跟支付宝交易记录
            OrderInfo orderInfo = orderInfoService.getById(orderId);
            rabbitService.sendMessage(MqConst.EXCHANGE_PAYMENT_PAY, MqConst.ROUTING_PAYMENT_CLOSE, orderInfo.getOrderNo());

        }
        //2.手动应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听订单支付成功消息；更新订单状态
     *
     * @param orderNo
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_PAYMENT_PAY, durable = "true"),
            value = @Queue(value = MqConst.QUEUE_PAYMENT_PAY, durable = "true"),
            key = MqConst.ROUTING_PAYMENT_PAY
    ))
    public void processPaySucess(String orderNo, Message message, Channel channel) {
        //业务处理
        if (StringUtils.isNotEmpty(orderNo)) {
            log.info("[订单服务]监听订单支付成功消息：{}", orderNo);
            //更改订单支付状态
            orderInfoService.processPaySucess(orderNo);
            //基于MQ通知扣减库存
            rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT, MqConst.ROUTING_MINUS, orderNo);
        }


        //手动应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


}