package com.spzx.common.rabbit.constant;

public class MqConst {

	/**
     * 测试
     */
    public static final String EXCHANGE_TEST = "spzx.exchange.test";
    public static final String ROUTING_TEST = "spzx.routing.test";
    public static final String ROUTING_CONFIRM = "spzx.routing.confirm";
    //队列
    public static final String QUEUE_TEST  = "spzx.queue.test";
    public static final String QUEUE_CONFIRM  = "spzx.queue.confirm";

    /**
     * 库存
     */
    public static final String EXCHANGE_PRODUCT = "spzx.exchange.product";
    public static final String ROUTING_UNLOCK = "spzx.routing.unlock";
    public static final String ROUTING_MINUS = "spzx.routing.minus";
    //队列
    public static final String QUEUE_UNLOCK  = "spzx.queue.unlock";
    public static final String QUEUE_MINUS  = "spzx.queue.minus";

    /**
     * 支付
     */
    public static final String EXCHANGE_PAYMENT_PAY = "spzx.exchange.payment";
    public static final String ROUTING_PAYMENT_PAY = "spzx.routing.payment.pay";
    public static final String ROUTING_PAYMENT_CLOSE = "spzx.routing.payment.close";;
    public static final String QUEUE_PAYMENT_PAY = "spzx.queue.payment.pay";
    public static final String QUEUE_PAYMENT_CLOSE  = "spzx.queue.payment.close";


    /**
     * 取消订单延迟消息
     */
    public static final String EXCHANGE_CANCEL_ORDER = "spzx.exchange.cancel.order";
    public static final String ROUTING_CANCEL_ORDER = "spzx.routing.cancel.order";
    public static final String QUEUE_CANCEL_ORDER = "spzx.queue.cancel.order";
    public static final Integer CANCEL_ORDER_DELAY_TIME = 15 * 60;


}