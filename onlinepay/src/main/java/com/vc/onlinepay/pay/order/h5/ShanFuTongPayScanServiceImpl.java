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

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @描述:闪付通支付交易接口类
 * @时间:2020年6月29日10:58:35
 */
@Service
public class ShanFuTongPayScanServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ShanFuTongPayScanServiceImpl.class);

    @Autowired
    private RedisCacheApi redisCacheApi;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String vcOrderNo = reqData.getString("vcOrderNo");
        	result.put("orderNo", vcOrderNo);
            logger.info("闪付通支付交易通道参数列表{}", reqData);  
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));//上游商户号
            String channelDesKey = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));//上游key
            String channelPayUrl = reqData.getString("channelPayUrl");
            String amount = reqData.getString("amount");  
            String notifyUrl = reqData.getString("projectDomainUrl")+"/shanFuTongPayCallBackApi"; // 异步通知地址
            String pay_applydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            JSONObject reqJson = new JSONObject();
            reqJson.put("mchId", channelKey);
            reqJson.put("appId", "4d914144269d405ab57c78ecaed3f9f5");
            reqJson.put("productId", 8016);
            reqJson.put("mchOrderNo", vcOrderNo);
            reqJson.put("currency", "cny");
            reqJson.put("amount", new BigDecimal(amount).multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString());
            reqJson.put("notifyUrl", notifyUrl);
            reqJson.put("subject", reqData.getString("goodsName"));
            reqJson.put("body", reqData.getString("goodsDesc"));
            reqJson.put("extra", new JSONObject());
            String sign = Md5CoreUtil.md5ascii(reqJson, channelDesKey).toUpperCase();
        	reqJson.put("sign", sign);

            logger.info("闪付通支付入参:{}",reqJson);
            String response = HttpClientTools.sendPost(channelPayUrl,"params="+reqJson.toString());
            if(StringUtils.isBlank(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("message", "响应为空");
            }
            JSONObject respJson = JSON.parseObject(response);
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
            logger.error("闪付通支付下单异常", e);
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("message", "下单异常");
            return listener.failedHandler(result);
        }
    }

    public static void main(String[] args) {
        JSONObject reqJson = new JSONObject();
        reqJson.put("mchId", "20000020");
        reqJson.put("appId", "4d914144269d405ab57c78ecaed3f9f5");
        reqJson.put("productId", 8016);
        reqJson.put("mchOrderNo", DateUtils.getTimeYMDhms());
        reqJson.put("currency", "cny");
        reqJson.put("amount", new BigDecimal("198").multiply(new BigDecimal("100")).toPlainString());
        reqJson.put("notifyUrl", "http://192.168.0.1/aa/test");
        reqJson.put("subject", "test");
        reqJson.put("body", "test");
        reqJson.put("extra", new JSONObject());
        String sign = Md5CoreUtil.md5ascii(reqJson, "8XZHUJYSN17PZELLQZB3FAFH0KQQKT2YFIQHX0NKN24WCTXUNVIHTOIERPG6TQXRXMYI7VOFWJANDHCC5UFO6XLO3RUZZXFBQNG4M3KSGOXXYI6R9UIJTMUOUZ0ZIJWE").toUpperCase();
        reqJson.put("sign", sign);
        logger.info("闪付通支付入参:{}",reqJson);
        try {
            String response = HttpClientTools.sendPost("http://47.75.96.1:3020/api/pay/create_order","params="+reqJson.toString());
            System.out.println("响应："+response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
}

