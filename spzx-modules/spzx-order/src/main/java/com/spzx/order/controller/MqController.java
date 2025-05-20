package com.spzx.order.controller;

import com.rabbitmq.client.Channel;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.order.config.DeadLetterMqConfig;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.order.config.DelayedMqConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Tag(name = "Mq接口管理")
@RestController
@RequestMapping("/mq")
public class MqController extends BaseController
{
    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Operation(summary = "发送消息")
    @GetMapping("/sendMessage")
    public AjaxResult sendMessage()
    {
        rabbitService.sendMessage(MqConst.EXCHANGE_TEST, MqConst.ROUTING_TEST, "hello");
        return success();
    }

    @Operation(summary = "发送确认消息")
    @GetMapping("/sendConfirmMessage")
    public AjaxResult sendConfirmMessage()
    {
        rabbitService.sendMessage(MqConst.EXCHANGE_TEST, MqConst.ROUTING_CONFIRM, "hello, confirm");
        return success();
    }

    /**
     * 消息发送延迟消息：基于死信实现
     */
    @Operation(summary = "发送延迟消息：基于死信实现")
    @GetMapping("/sendDeadLetterMsg")
    public AjaxResult sendDeadLetterMsg() {
        rabbitService.sendMessage(DeadLetterMqConfig.exchange_dead, DeadLetterMqConfig.routing_dead_1, "我是延迟消息");
        return success();
    }

    @Operation(summary = "发送延迟消息：基于延迟插件")
    @GetMapping("/sendDelayMsg")
    public AjaxResult sendDelayMsg() {
        //调用工具方法发送延迟消息
        int delayTime = 10;
        rabbitService.sendDealyMessage(DelayedMqConfig.exchange_delay, DelayedMqConfig.routing_delay, "我是延迟消息", delayTime);
        return success();
    }

    /**
     * 监听延迟消息
     * @param msg
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = {DeadLetterMqConfig.queue_dead_2})
    public void getDeadLetterMsg(String msg, Message message, Channel channel) {
        //接收消息，消费者端判断是否需要做幂等性处理
        //如果业务保证幂等性，基于redis setnx保证
        String key = "mq:" + msg;
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, "", 200, TimeUnit.SECONDS);
        if (!flag) {
            //说明该业务数据以及被执行
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        // 执行业务
        //  TODO

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}