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
 * @描述:众联支付交易接口类
 * @时间:2020年7月2日14:05:27
 */
@Service
public class ZhongLianServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ZhongLianServiceImpl.class);

    @Autowired
    private RedisCacheApi redisCacheApi;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String vcOrderNo = reqData.getString("vcOrderNo");
        	result.put("orderNo", vcOrderNo);
            logger.info("众联支付交易通道参数列表{}", reqData);  
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));//上游商户号
            String channelDesKey = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));//上游key
            String channelPayUrl = reqData.getString("channelPayUrl");
            String amount = reqData.getString("amount");  
            String notifyUrl = reqData.getString("projectDomainUrl")+"/zhongLianPayCallBackApi"; // 异步通知地址
            String payType = reqData.getString("payType");
            String ipAddress = reqData.getString("netIpaddress");
            String goodsName = reqData.getString("goodsName");

            JSONObject reqJson = new JSONObject();
            reqJson.put("merchantNo", channelKey);
            reqJson.put("nonceStr", DateUtils.getTimeYMDhms()+new Random().nextInt(100));
            reqJson.put("paymentType", "ALIPAY_APP");
            reqJson.put("mchOrderNo", vcOrderNo);
            reqJson.put("orderTime", DateUtils.getTimeYMDhms());
            reqJson.put("goodsName", reqData.getString("goodsName"));
            reqJson.put("amount", new BigDecimal(amount).multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString());
            reqJson.put("clientIp",ipAddress);
            reqJson.put("notifyUrl",notifyUrl);
            reqJson.put("buyerId","10001");
            reqJson.put("buyerName","张三");
            String signStr = Md5CoreUtil.getSignStr(reqJson)+"&appkey="+channelDesKey;
            logger.info("众联支付加密前:{}",signStr);
        	reqJson.put("sign", Md5Util.MD5(signStr));
            logger.info("众联支付入参:{}",reqJson);
            String response = HttpClientTools.baseHttpSendPost(channelPayUrl,reqJson);
            if(StringUtils.isBlank(response)){
                logger.info("众联支付响应为空{}",vcOrderNo);
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("message", "下单异常");
                return listener.failedHandler(result);
            }
            logger.info("众联支付响应{}|{}",response,vcOrderNo);
            if(response.startsWith("<form") || response.startsWith("<html")){
                result.put("redirectHtml", response);
                result.put("status", 1);
                result.put("code", Constant.SUCCESSS);
                result.put("message", "下单成功");
                return listener.successHandler(result);
            }
            JSONObject respJson = JSON.parseObject(response);
            String message = respJson.getString("returnMsg");
            String payUrl = respJson.getString("payUrl");
            if(StringUtils.isNotBlank(payUrl)){
                result.put("redirectUrl", payUrl);
                result.put("status", 1);
                result.put("code", Constant.SUCCESSS);
                result.put("message", "下单成功");
                return listener.successHandler(result);
            }
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("message", StringUtils.isBlank(message)?"下单失败":message);
            return listener.failedHandler(result);
        } catch (Exception e) {
            logger.error("众联支付下单异常", e);
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("message", "下单异常");
            return listener.failedHandler(result);
        }
    }

    public static void main(String[] args) {
        JSONObject reqJson = new JSONObject();
        reqJson.put("merchantNo", "O4XS110437");
        reqJson.put("nonceStr", DateUtils.getTimeYMDhms()+new Random().nextInt(100));
        reqJson.put("paymentType", "ALIPAY_APP");
        reqJson.put("mchOrderNo", DateUtils.getTimeYMDhms());
        reqJson.put("orderTime", DateUtils.getTimeYMDhms());
        reqJson.put("goodsName", "test");
        reqJson.put("amount", new BigDecimal("89").multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString());
        reqJson.put("clientIp","192.168.0.1");
        reqJson.put("notifyUrl","http://192.168.0.1/test");
        reqJson.put("buyerId","10001");
        reqJson.put("buyerName","张三");
        String signStr = Md5CoreUtil.getSignStr(reqJson)+"&appkey=TZSHAGVLLCDLNBVPMBKI";
        logger.info("众联支付加密前:{}",signStr);
        reqJson.put("sign", Md5Util.MD5(signStr));
        logger.info("众联支付入参:{}",reqJson);
        try {
            String response = HttpClientTools.baseHttpSendPost("http://api.51qsjz.com/api_gateway/pay/order/create",reqJson);
            System.out.println("响应："+response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
}

