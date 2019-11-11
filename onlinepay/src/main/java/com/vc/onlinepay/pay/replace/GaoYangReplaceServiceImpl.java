/**
 * @类名称:InpayReplaceServiceImpl.java
 * @时间:2017年12月19日上午11:09:35
 * @作者:lihai
 * @版权:公司 Copyright (c) 2017
 */
package com.vc.onlinepay.pay.replace;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


/**
 * @描述:高阳
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component

public class GaoYangReplaceServiceImpl {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static String url = "http://gateway.7986sun.com/onlinepay/gateway/sunpayapi";

    /**
     * @描述:高阳代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("高阳代付接口reqData:{}",reqData);
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            String orderNo = reqData.getString("vcOrderNo");
            String openBankName = reqData.containsKey("openBankName")?reqData.getString("openBankName"):"其它";
            String bankName = reqData.containsKey("bankName")?reqData.getString("bankName"):"其它";
            String accountName = reqData.containsKey("accountName")?reqData.getString("accountName"):"其它";
            String bankCard = reqData.containsKey("bankCard")?reqData.getString("bankCard"):"其它";
            BigDecimal amount = new BigDecimal(reqData.getString("amount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            String city = reqData.containsKey("city")?reqData.getString("city"):"其它";
            String province = reqData.containsKey("province")?reqData.getString("province"):"其它";
            String idCardNo = reqData.containsKey("idCardNo")?reqData.getString("idCardNo"):"其它";
            String bankLinked = reqData.containsKey("bankLinked")?reqData.getString("bankLinked"):"其它";
            String mobile = reqData.containsKey("mobile")?reqData.getString("mobile"):"其它";

            JSONObject prams = new JSONObject();
            prams.put("serverCode","ser3001");
            prams.put("merchNo",merchantCode);
            prams.put("version","2.0");
            prams.put("charset","utf-8");
            prams.put("currency","CNY");
            prams.put("reqIp","47.25.125.14");
            prams.put("reqTime",Constant.ymdhms.format(new Date ()));
            prams.put("signType","MD5");
            prams.put("cOrderNo",orderNo);
            prams.put("amount",String.valueOf(amount));
            prams.put("idCardNo",idCardNo);
            prams.put("accountName",accountName);
            prams.put("bankCard",bankCard);
            prams.put("bankName",bankName);
            prams.put("bankSubName",openBankName);
            prams.put("province",province);
            prams.put("city",city);
            prams.put("bankLinked",bankLinked);
            prams.put("mobile",mobile);
            prams.put("extendField",orderNo);
            String sign = Md5CoreUtil.md5ascii(prams, md5Key);
            prams.put("sign",sign);
    		String response = HttpClientTools.httpSendPostFrom(url, prams);
            logger.info("高阳代付接口入参:{},响应:{}",prams,response);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "订单处理中");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if(jsonData.containsKey ("status") && "1".equals(jsonData.getString("status"))){
                result.put("status", 1);
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "代付成功");
                return listener.successHandler (result);
            }else{
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", jsonData.containsKey ("msg")?jsonData.getString ("msg"):"代付响应中");
                return listener.paddingHandler (result);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("高阳代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }

    /**
     * @描述:高阳代付订单查询接口
     * @时间:2018/6/15 11:22
     */
    @SuppressWarnings("all")
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            JSONObject prams = new JSONObject();
            prams.put("serverCode","ser4002");
            prams.put("merchNo",merchantCode);
            prams.put("version","2.0");
            prams.put("charset","utf-8");
            prams.put("currency","CNY");
            prams.put("reqIp","47.25.125.14");
            prams.put("reqTime",Constant.ymdhms.format(new Date ()));
            prams.put("signType","MD5");
            prams.put("cOrderNo",reqData.getString("vcOrderNo"));
            String sign = Md5CoreUtil.md5ascii(prams, md5Key);
            prams.put("sign",sign);
            String response = HttpClientTools.httpSendPostFrom(url, prams);
            logger.info("高阳代付订单查询接口响应{}",response);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if(jsonData.containsKey("status") &&  "1".equals(jsonData.getString("status"))){
                result.put("status", 1);
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "代付成功");
                return listener.successHandler (result);
            }
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("msg", "处理中");
            return listener.paddingHandler(result);
        }catch (Exception e){
            logger.error("高阳查询接口异常",e);
            result.put("code", Constant.FAILED);
            result.put("msg","查询异常");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }finally {
            result.put("vcOrderNo",reqData.get("vcOrderNo"));
            result.put("amount",reqData.get("amount"));
            result.put("merchantId",reqData.get("merchantId"));
            result.put("orderNo", reqData.get("orderId"));
            result.put("password",reqData.get("password"));
        }
    }

    /**
     * @描述:余额查询
     * @时间:2018/6/15 11:23
     */
    public JSONObject walletQuery(JSONObject reqData,ResultListener listener){
        JSONObject result = new JSONObject();
        result.put("code", Constant.FAILED);
        try {
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            result.put("orderNo",reqData.getString("vcOrderNo"));
            JSONObject prams = new JSONObject();
            prams.put("serverCode","ser4003");
            prams.put("merchNo",merchantCode);
            prams.put("version","2.0");
            prams.put("charset","utf-8");
            prams.put("currency","CNY");
            prams.put("reqIp","47.25.125.14");
            prams.put("reqTime",Constant.ymdhms.format(new Date ()));
            prams.put("signType","MD5");
            prams.put("mode","T0");
            prams.put("sign",Md5CoreUtil.md5ascii(prams, md5Key));
            String response = HttpClientTools.httpSendPostFrom(url, prams);
            if(StringUtils.isEmpty(response)){
                result.put("balance", "0");
                result.put("code", Constant.FAILED);
                result.put("msg", "查询超时");
                return listener.failedHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            String balance = jsonData.getString("amount");
            result.put("code", Constant.SUCCESSS);
            result.put("balance", balance);
            result.put("msg", "查询成功！");
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("高阳余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }
}
