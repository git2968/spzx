package com.spzx.payment.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.security.annotation.RequiresLogin;
import com.spzx.payment.service.IAlipayService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/alipay")
public class AlipayController extends BaseController {

    @Autowired
    private IAlipayService alipayService;

    @Operation(summary = "支付宝下单")
    @RequiresLogin
    @RequestMapping("submitAlipay/{orderNo}")
    @ResponseBody
    public AjaxResult submitAlipay(@PathVariable(value = "orderNo") String orderNo) {
        String form = alipayService.submitAlipay(orderNo);
        return success(form);
    }

}