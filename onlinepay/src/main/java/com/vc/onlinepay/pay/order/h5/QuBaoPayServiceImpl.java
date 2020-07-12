package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @描述:趣宝支付交易接口类
 * @时间:2020年7月10日20:00:59
 */
@Service
public class QuBaoPayServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(QuBaoPayServiceImpl.class);

    @Autowired
    private RedisCacheApi redisCacheApi;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String vcOrderNo = reqData.getString("vcOrderNo");
        	result.put("orderNo", vcOrderNo);
            logger.info("趣宝支付交易通道参数列表{}", reqData);  
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));//上游商户号
            String channelDesKey = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));//上游key
            String channelPayUrl = reqData.getString("channelPayUrl");
            String amount = reqData.getString("amount");  
            String notifyUrl = reqData.getString("projectDomainUrl")+"/quBaoPayCallBackApi"; // 异步通知地址
            
            JSONObject reqJson = new JSONObject();
            reqJson.put("merchant_id", channelKey);
            reqJson.put("version", "V2.0");
            reqJson.put("pay_type", "8001015");
            reqJson.put("device_type", "wap");
            reqJson.put("request_time", DateUtils.getTimeYMDhms());
            reqJson.put("nonce_str", DateUtils.getTimeYMDhms()+(new Random().nextInt(9000)+1000));
            reqJson.put("pay_ip", reqData.getString("netIpaddress"));
            reqJson.put("out_trade_no", vcOrderNo);
            reqJson.put("amount", new BigDecimal(amount).setScale(2,BigDecimal.ROUND_DOWN));
            reqJson.put("currency", "CNY");
            reqJson.put("notify_url", notifyUrl);
            reqJson.put("return_url", reqData.getString("projectDomainUrl")+"/returnurl");
            String sign = Md5CoreUtil.getSignStr(reqJson)+channelDesKey;
            logger.info("趣宝支付加密前:{}",sign);
        	reqJson.put("sign", Md5Util.MD5(sign));
            logger.info("趣宝支付入参:{}",reqJson);
            String response = HttpClientTools.baseHttpSendPost(channelPayUrl,reqJson);
            logger.info("趣宝支付响应:{}",response);
            if(StringUtils.isBlank(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("message", "响应为空");
            }
            if(response.startsWith("<form") || response.startsWith("<html")){
                result.put("redirectHtml", response);
                result.put("status", 1);
                result.put("code", Constant.SUCCESSS);
                result.put("message", "下单成功");
                return listener.successHandler(result);
            }
            JSONObject respJson = JSON.parseObject(response);
            String message = respJson.getString("message");
            if(respJson.containsKey("pay_url")){
                String payUrl = respJson.getString("pay_url");
                result.put("redirectUrl", payUrl);
                result.put("status", 1);
                result.put("code", Constant.SUCCESSS);
                result.put("message", "下单成功");
                return listener.successHandler(result);
            }else if(respJson.containsKey("code_url")){
                String payUrl = respJson.getString("code_url");
                result.put("redirectUrl", payUrl);
                result.put("status", 1);
                result.put("code", Constant.SUCCESSS);
                result.put("message", "下单成功");
                return listener.successHandler(result);
            }else{
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("message", StringUtils.isBlank(message)?"下单失败":message);
                return listener.failedHandler(result);
            }
        } catch (Exception e) {
            logger.error("趣宝支付下单异常", e);
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("message", "下单异常");
            return listener.failedHandler(result);
        }
    }

    public static void main(String[] args) {
        JSONObject reqJson = new JSONObject();
        reqJson.put("merchant_id","13455");
        reqJson.put("version", "V2.0");
        reqJson.put("pay_type", "8001019");
        reqJson.put("device_type", "wap");
        reqJson.put("request_time", DateUtils.getTimeYMDhms());
        reqJson.put("nonce_str", DateUtils.getTimeYMDhms()+(new Random().nextInt(9000)+1000));
        reqJson.put("pay_ip", "192.168.0.1");
        reqJson.put("out_trade_no", DateUtils.getTimeYMDhms());
        reqJson.put("amount", new BigDecimal("100").setScale(2,BigDecimal.ROUND_DOWN));
        reqJson.put("currency", "CNY");
        reqJson.put("notify_url", "http://192.168.0.1/test");
        reqJson.put("return_url", "http://192.168.0.1/test");
        String sign = Md5CoreUtil.getSignStr(reqJson)+"181EA64A27D34CBDB21F58B25A733FB4";
        logger.info("趣宝支付加密前:{}",sign);
        reqJson.put("sign", Md5Util.MD5(sign));
        logger.info("趣宝支付入参:{}",reqJson);
        try {
            String response = HttpClientTools.baseHttpSendPost("https://pay.youjiapay.cn:81/gateway/dopay",reqJson);
            System.out.println("响应："+response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
}

