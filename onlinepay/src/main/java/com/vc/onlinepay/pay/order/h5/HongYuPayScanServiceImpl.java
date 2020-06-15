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

/**
 * @描述:鸿宇支付交易接口类
 * @时间:2020年6月15日16:58:47
 */
@Service
public class HongYuPayScanServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(HongYuPayScanServiceImpl.class);

    @Autowired
    private RedisCacheApi redisCacheApi;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String vcOrderNo = reqData.getString("vcOrderNo");
        	result.put("orderNo", vcOrderNo);
            logger.info("鸿宇支付交易通道参数列表{}", reqData);  
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));//上游商户号
            String channelDesKey = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));//上游key
            String channelPayUrl = reqData.getString("channelPayUrl");
            String amount = reqData.getString("amount");  
            String notifyUrl = reqData.getString("projectDomainUrl")+"/hongYuPayCallBackApi"; // 异步通知地址
            String payType = reqData.getString("payType");
            String ipAddress = reqData.getString("netIpaddress");
            String goodsName = reqData.getString("goodsName");

            JSONObject reqJson = new JSONObject();
            reqJson.put("pay_memberid", channelKey);
            reqJson.put("pay_orderid", vcOrderNo);
            reqJson.put("pay_applydate", DateUtils.getTimeForY_M_D_H_m_s());
            reqJson.put("pay_bankcode", "1002");
            reqJson.put("pay_notifyurl", notifyUrl);
            reqJson.put("pay_callbackurl", reqData.getString("projectDomainUrl")+"/payCallUrl");
            reqJson.put("pay_amount", amount);
            String sign = Md5CoreUtil.md5ascii(reqJson, channelDesKey).toUpperCase();
            reqJson.put("pay_productname",goodsName);
        	reqJson.put("pay_md5sign", sign);
            logger.info("鸿宇支付入参:{}",reqJson);
            String response = HttpClientTools.httpSendPostForm(channelPayUrl,reqJson);
            if(StringUtils.isBlank(response)){
                logger.info("鸿宇支付响应为空{}",vcOrderNo);
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("message", "下单异常");
                return listener.failedHandler(result);
            }
            logger.info("鸿宇支付响应{}|{}",response,vcOrderNo);
            if(response.startsWith("<form") || response.startsWith("<html")){
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
            logger.error("鸿宇支付下单异常", e);
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("message", "下单异常");
            return listener.failedHandler(result);
        }
    }

    public static void main(String[] args) {
        JSONObject reqJson = new JSONObject();
        reqJson.put("pay_memberid", "200649751");
        reqJson.put("pay_orderid", DateUtils.getTimeYMDhms());
        reqJson.put("pay_applydate", DateUtils.getTimeForY_M_D_H_m_s());
        reqJson.put("pay_bankcode", "1002");
        reqJson.put("pay_notifyurl", "http://192.168.0.1/test");
        reqJson.put("pay_callbackurl", "http://192.168.0.1/test");
        reqJson.put("pay_amount", "100");
        String sign = Md5CoreUtil.md5ascii(reqJson, "j3sxiytt70j2pu25crg7bmaqezz843pq").toUpperCase();
        reqJson.put("pay_md5sign", sign);
        reqJson.put("pay_productname","TEST");
        logger.info("鸿宇支付入参:{}",reqJson);
        try {
            String response = HttpClientTools.httpSendPostForm("http://www.zfb51.cn/Pay_Index.html ",reqJson);
            System.out.println("响应："+response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
}

