package com.spzx.user.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.user.service.ISmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "短信接口")
@RestController
@RequestMapping("/sms")
public class SmsController extends BaseController {
    @Autowired
    private ISmsService smsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Operation(summary = "获取短信验证码")
    @GetMapping(value = "sendCode/{phone}")
    public AjaxResult sendCode(
            @Parameter(name = "phone", description = "手机", required = true)
            @PathVariable String phone) {
        String code = new DecimalFormat("0000").format(new Random().nextInt(10000));
        redisTemplate.opsForValue().set("phone:code:"+phone , code, 5, TimeUnit.MINUTES);
        log.info(phone+": " + code);

        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        smsService.send(phone, "CST_ptdie100", param);
        return success();
    }
}
