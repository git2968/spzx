package com.spzx.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.spzx.user.service.ISmsService;
import com.spzx.user.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsServiceImpl implements ISmsService {
    @Override
    public void send(String phone, String templateCode, Map<String, Object> param) {
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "e42b0b9cd5134982a1abb5f31504ec28";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();

        StringBuffer stringBuffer = new StringBuffer();

        for (Map.Entry<String, Object> item : param.entrySet()) {
            String key = item.getKey();
            Object value = item.getValue();
            stringBuffer.append(item.getKey()).append(":").append(item.getValue()).append(",");
        }
        String content = stringBuffer.substring(0, stringBuffer.length() - 1);

        bodys.put("content", content);
        bodys.put("template_id", templateCode);  //注意，CST_ptdie100该模板ID仅为调试使用，调试结果为"status": "OK" ，即表示接口调用成功，然后联系客服报备自己的专属签名模板ID，以保证短信稳定下发
        bodys.put("phone_number", phone);


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);

            String data = EntityUtils.toString(response.getEntity());
            HashMap<String,String> map = JSONObject.parseObject(data, HashMap.class);
            String status = map.get("status");
            String reason = map.get("reason");
            if (!"OK".equals(status)) {
                log.error("短信发送失败: status = "+ status + "reason" + reason);
                throw new RuntimeException("短信发送失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("短信发送失败");
        }
    }
}
