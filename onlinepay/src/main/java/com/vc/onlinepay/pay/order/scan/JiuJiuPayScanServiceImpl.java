package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @描述:久久支付交易接口类
 * @时间:2020年6月5日16:04:31
 */
@Service
public class JiuJiuPayScanServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(JiuJiuPayScanServiceImpl.class);

    @Autowired
    private RedisCacheApi redisCacheApi;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String vcOrderNo = reqData.getString("vcOrderNo");
        	result.put("orderNo", vcOrderNo);
            logger.info("久久支付交易通道参数列表{}", reqData);  
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));//上游商户号
            String channelDesKey = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));//上游key
            String channelPayUrl = reqData.getString("channelPayUrl");
            String amount = reqData.getString("amount");  
            String notifyUrl = reqData.getString("projectDomainUrl")+"/jiuJiuPayCallBackApi"; // 异步通知地址
            String pay_applydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            JSONObject reqJson = new JSONObject();
            reqJson.put("pay_memberid", channelKey);  
            reqJson.put("pay_orderid", vcOrderNo);  
            reqJson.put("pay_applydate", pay_applydate);
            reqJson.put("pay_bankcode", "933");
            reqJson.put("pay_notifyurl", notifyUrl); 
            reqJson.put("pay_callbackurl", reqData.getString("projectDomainUrl")+"/payCallUrl");
            reqJson.put("pay_amount", amount);
            String sign = Md5CoreUtil.md5ascii(reqJson, channelDesKey).toUpperCase();
        	reqJson.put("pay_md5sign", sign);
            reqJson.put("pay_productname", reqData.getString("goodsName"));

            logger.info("久久支付入参:{}",reqJson);
            reqJson.put ("payUrl",channelPayUrl);
            result.put("bankUrl", reqData.getString ("projectDomainUrl")+"/cashier/jiujiu/"+vcOrderNo);
            result.put("status", 1);
            result.put("code", Constant.SUCCESSS);
            result.put("message", "下单成功");
            redisCacheApi.set("cashier_jiujiu_"+vcOrderNo,reqJson);
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("久久支付下单异常", e);
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("message", "下单异常");
            return listener.failedHandler(result);
        }
    }

    public static void main(String[] args) {
        JSONObject reqJson = new JSONObject();
        reqJson.put("pay_memberid", "12594");
        reqJson.put("pay_orderid", DateUtils.getTimeYMDhms());
        reqJson.put("pay_applydate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        reqJson.put("pay_bankcode", "933");
        reqJson.put("pay_notifyurl", "http://127.0.0.1/test");
        reqJson.put("pay_callbackurl", "http://127.0.0.1/test");
        reqJson.put("pay_amount", "500");
        String sign = Md5CoreUtil.md5ascii(reqJson, "yp0g4gzwpbl71me7ylx90ylg539v3hcy").toUpperCase();
        reqJson.put("pay_md5sign", sign);
        reqJson.put("pay_productname", "test");
        logger.info("久久支付入参:{}",reqJson);
        try {
            String resultData = HttpClientTools.httpSendPostForm("http://www.99qrpay.com/Pay_Index.html", reqJson);
            System.out.println("响应："+resultData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  
}

