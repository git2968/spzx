package com.spzx.payment.service;

public interface IAlipayService{
    String submitAlipay(String orderNo);

    void closePayment(String orderNo);
}