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

/**
 * @描述:安逸支付交易接口类
 * @时间:2020年6月21日10:33:27
 */
@Service
public class AnYiPayScanServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(AnYiPayScanServiceImpl.class);

    @Autowired
    private RedisCacheApi redisCacheApi;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String vcOrderNo = reqData.getString("vcOrderNo");
        	result.put("orderNo", vcOrderNo);
            logger.info("安逸支付交易通道参数列表{}", reqData);  
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));//上游商户号
            String channelDesKey = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));//上游key
            String channelPayUrl = reqData.getString("channelPayUrl");
            String amount = reqData.getString("amount");  
            String notifyUrl = reqData.getString("projectDomainUrl")+"/anYiPayCallBackApi"; // 异步通知地址
            String payType = reqData.getString("payType");
            String ipAddress = reqData.getString("netIpaddress");
            String goodsName = reqData.getString("goodsName");

            JSONObject reqJson = new JSONObject();
            reqJson.put("pid", channelKey);
            reqJson.put("type", "alipay");
            reqJson.put("out_trade_no", vcOrderNo);
            reqJson.put("notify_url", notifyUrl);
            reqJson.put("return_url", reqData.getString("projectDomainUrl")+"/returnUrl");
            reqJson.put("name", goodsName);
            reqJson.put("money", amount);
            String signStr = Md5CoreUtil.getSignStr(reqJson)+channelDesKey;
            logger.info("加密前：{}",signStr);
            String sign = Md5Util.md5(signStr);
            reqJson.put("sign_type","MD5");
        	reqJson.put("sign", sign);
            logger.info("安逸支付入参:{}",reqJson);
            String response = HttpClientTools.httpSendPostForm(channelPayUrl,reqJson);
            if(StringUtils.isBlank(response)){
                logger.info("安逸支付响应为空{}",vcOrderNo);
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("message", "下单异常");
                return listener.failedHandler(result);
            }
            logger.info("安逸支付响应{}|{}",response,vcOrderNo);
            if(response.startsWith("<form") || response.startsWith("<html") || response.startsWith("<!DOCTYPE")){
                result.put("redirectHtml", response);
                result.put("status", 1);
                result.put("code", Constant.SUCCESSS);
                result.put("message", "下单成功");
                return listener.successHandler(result);
            }
            JSONObject respJson = JSON.parseObject(response);
            String message = respJson.getString("msg");
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("message", StringUtils.isBlank(message)?"下单失败":message);
            return listener.failedHandler(result);
        } catch (Exception e) {
            logger.error("安逸支付下单异常", e);
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("message", "下单异常");
            return listener.failedHandler(result);
        }
    }

    public static void main(String[] args) {
        JSONObject reqJson = new JSONObject();
        reqJson.put("pid", "1022");
        reqJson.put("type", "alipay");
        reqJson.put("out_trade_no", DateUtils.getTimeYMDhms());
        reqJson.put("notify_url", "http://192.168.0.1/test");
        reqJson.put("return_url", "http://192.168.0.1/test");
        reqJson.put("name", "test");
        reqJson.put("money", "300");
        String signStr = Md5CoreUtil.getSignStr(reqJson)+"ktYcDF4ZWZ493t49mww49ywtfUn4yw31";
        System.out.println("加密前："+signStr);
        String sign = Md5Util.md5(signStr);
        reqJson.put("sign_type","MD5");
        reqJson.put("sign", sign);
        logger.info("安逸支付入参:{}",reqJson);
        try {
            String response = HttpClientTools.httpSendPostForm("http://156.253.14.72/submit.php",reqJson);
            System.out.println("响应："+response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
}

