package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @描述:太阳支付交易接口类
 * @时间:2020年6月6日20:55:12
 */
@Service
public class SunPayScanServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(SunPayScanServiceImpl.class);

    @Autowired
    private RedisCacheApi redisCacheApi;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String vcOrderNo = reqData.getString("vcOrderNo");
        	result.put("orderNo", vcOrderNo);
            logger.info("太阳支付交易通道参数列表{}", reqData);  
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));//上游商户号
            String channelDesKey = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));//上游key
            String channelPayUrl = reqData.getString("channelPayUrl");
            String amount = reqData.getString("amount");  
            String notifyUrl = reqData.getString("projectDomainUrl")+"/sunPayCallBackApi"; // 异步通知地址
            String payType = reqData.getString("payType");
            String ipAddress = reqData.getString("netIpaddress");
            if("10".equals(payType)){
                payType = "8034";
            }else if("12".equals(payType)){
                payType = "8032";
            }
            JSONObject reqJson = new JSONObject();
            reqJson.put("mchId", channelKey);
            reqJson.put("appId", "c87e43bff53249bd9ae33b35620523cb");
            reqJson.put("productId", payType);
            reqJson.put("mchOrderNo", vcOrderNo);
            reqJson.put("currency", "cny");
            reqJson.put("clientIp", ipAddress);
            reqJson.put("pay_callbackurl", reqData.getString("projectDomainUrl")+"/payCallUrl");
            reqJson.put("amount", new BigDecimal(amount).multiply(new BigDecimal(100)).toPlainString());
            reqJson.put("returnUrl",reqData.getString("projectDomainUrl")+"/retUrl");
            reqJson.put("notifyUrl",notifyUrl);
            reqJson.put("subject",reqData.getString("goodsName"));
            reqJson.put("body",reqData.getString("goodsDesc"));
            String sign = Md5CoreUtil.md5ascii(reqJson, channelDesKey).toUpperCase();
        	reqJson.put("sign", sign);
            logger.info("太阳支付入参:{}",reqJson);
            String response = HttpClientTools.sendPost(channelPayUrl,"params="+reqJson.toString());
            if(StringUtils.isBlank(response)){
                logger.info("太阳支付响应为空{}",vcOrderNo);
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("message", "下单异常");
                return listener.failedHandler(result);
            }
            JSONObject respJson = JSON.parseObject(response);
            logger.info("太阳支付响应{}|{}",respJson,vcOrderNo);
            String message = respJson.getString("errDes");
            if(respJson.containsKey("payParams")){
                String payParams = respJson.getString("payParams");
                JSONObject jsonData = JSON.parseObject(payParams);
                String payUrl = jsonData.getString("payUrl");
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
            logger.error("太阳支付下单异常", e);
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("message", "下单异常");
            return listener.failedHandler(result);
        }
    }

    public static void main(String[] args) {
        JSONObject reqJson = new JSONObject();
        reqJson.put("mchId", "285");
        reqJson.put("appId", "c87e43bff53249bd9ae33b35620523cb");
        reqJson.put("productId", "8032");
        reqJson.put("mchOrderNo", DateUtils.getTimeYMDhms());
        reqJson.put("currency", "cny");
        reqJson.put("clientIp", "192.168.0.1");
        reqJson.put("pay_callbackurl", "http://www.test.com/test");
        reqJson.put("amount", new BigDecimal(100).multiply(new BigDecimal(100)).toPlainString());
        reqJson.put("returnUrl","http://www.test.com/test");
        reqJson.put("notifyUrl","http://www.test.com/test");
        reqJson.put("subject","test");
        reqJson.put("body","test");
        String sign = Md5CoreUtil.md5ascii(reqJson, "XV366VKJWQY1AEMJF1LKM42YW99IIVJT6OPGAR25KLBVST6BQ8DQ18X5KDQOBSRKYB1LOXTQEERX9ZVNWJUBWYDX6QMHQ1NCRWGX28YNKXDSPNTPUSJE9XITL3YX4JHK").toUpperCase();
        reqJson.put("sign", sign);
        logger.info("太阳支付入参:{}",reqJson);
        try {
            String response = HttpClientTools.sendPost("http://180.178.35.170:3020/api/pay/create_order","params="+reqJson.toString());
            System.out.println("响应："+response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
}

