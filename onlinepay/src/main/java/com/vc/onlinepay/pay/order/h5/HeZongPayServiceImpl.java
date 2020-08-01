package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.http.HttpsClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @描述:合纵支付交易接口类
 * @时间:2020年8月1日17:36
 */
@Service
public class HeZongPayServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(HeZongPayServiceImpl.class);

    @Autowired
    private RedisCacheApi redisCacheApi;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String vcOrderNo = reqData.getString("vcOrderNo");
        	result.put("orderNo", vcOrderNo);
            logger.info("合纵支付交易通道参数列表{}", reqData);  
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));//上游商户号
            String channelDesKey = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));//上游key
            String channelPayUrl = reqData.getString("channelPayUrl");
            String amount = reqData.getString("amount");  
            String notifyUrl = reqData.getString("projectDomainUrl")+"/heZongPayCallBackApi"; // 异步通知地址
            String pay_applydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            JSONObject reqJson = new JSONObject();
            reqJson.put("amount", new BigDecimal(amount).multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString());
            reqJson.put("backUrl", notifyUrl);
            reqJson.put("mcnNum", channelKey);
            reqJson.put("orderId", vcOrderNo);
            reqJson.put("payType", 2);
            reqJson.put("ip", reqData.getString("ipAddress"));
            String signStr = "mcnNum="+reqJson.getString("mcnNum")+"&"+
                    "orderId="+reqJson.getString("orderId")+"&"+
                    "backUrl="+reqJson.getString("backUrl")+"&"+
                    "payType="+reqJson.getString("payType")+"&"+
                    "amount="+reqJson.getString("amount")+"&"+
                    "secreyKey="+channelDesKey;
            logger.info("合纵支付加密前:{}"+signStr);
            String sign = Md5Util.MD5(signStr);
        	reqJson.put("sign", sign);

            logger.info("合纵支付入参:{}",reqJson);
            String response = HttpsClientTools.sendHttpSSL_appljson(reqJson,"https://api.hzpay.info/api/v1/pay_qrcode.api");
            if(StringUtils.isBlank(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("message", "响应为空");
            }
            JSONObject respJson = JSON.parseObject(response);
            String message = respJson.getString("message");
            String qrCode = respJson.getString("qrCode");
            if(StringUtils.isNotBlank(qrCode)){
                result.put("redirectUrl", qrCode);
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
            logger.error("合纵支付下单异常", e);
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("message", "下单异常");
            return listener.failedHandler(result);
        }
    }

    public static void main(String[] args) {
        JSONObject reqJson = new JSONObject();
        reqJson.put("amount", new BigDecimal("200").multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString());
        reqJson.put("backUrl", "http://www.test.com/test");
        reqJson.put("mcnNum", "yn003");
        reqJson.put("orderId", DateUtils.getTimeYMDhms());
        reqJson.put("payType", 12);
        reqJson.put("ip", "192.168.0.1");
        String signStr = "mcnNum="+reqJson.getString("mcnNum")+"&"+
                "orderId="+reqJson.getString("orderId")+"&"+
                "backUrl="+reqJson.getString("backUrl")+"&"+
                "payType="+reqJson.getString("payType")+"&"+
                "amount="+reqJson.getString("amount")+"&"+
                "secreyKey="+"7013b5c9ce56dfbb905b436a61cf3158";
        logger.info("合纵支付加密前:{}",signStr);
        String sign = Md5Util.MD5(signStr);
        reqJson.put("sign", sign);

        logger.info("合纵支付入参:{}",reqJson);
        try {
            String response = HttpsClientTools.sendHttpSSL_appljson(reqJson,"https://api.hzpay.info/api/v1/pay_qrcode.api");
            System.out.println("响应："+response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
}

