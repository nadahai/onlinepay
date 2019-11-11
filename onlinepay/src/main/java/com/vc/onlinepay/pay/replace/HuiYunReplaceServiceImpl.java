/**
 * @类名称:InpayReplaceServiceImpl.java
 * @时间:2017年12月19日上午11:09:35
 * @作者:lihai
 * @版权:公司 Copyright (c) 2017
 */
package com.vc.onlinepay.pay.replace;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.sand.HttpKit;
import com.vc.onlinepay.utils.sand.JsonDefrayDTO;
import com.vc.onlinepay.utils.sand.JsonDefrayQueryDTO;


/**
 * @描述:惠云
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component

public class HuiYunReplaceServiceImpl {
    
    private static Logger logger = LoggerFactory.getLogger(JiaLiangReplaceServiceImpl.class);

    private static String url = "http://www.heepayvip.com/tixian";
  	
    /**
     * @描述:惠云支付代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("惠云支付代付接口reqData:{}",reqData);
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            String orderNo = reqData.getString("vcOrderNo");
            String openBankName = reqData.containsKey("openBankName")?reqData.getString("openBankName"):"其它";
            String accountName = reqData.containsKey("accountName")?reqData.getString("accountName"):"其它";
            String bankCard = reqData.containsKey("bankCard")?reqData.getString("bankCard"):"其它";
            String bankName = reqData.containsKey("bankName")?reqData.getString("bankName"):"其它";
            BigDecimal amount = new BigDecimal(reqData.getString("amount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            
    		JSONObject reqPrms = new JSONObject();
    		reqPrms.put("customerid", merchantCode);
    		reqPrms.put("orderId", orderNo);
    		reqPrms.put("name", accountName);
    		reqPrms.put("total_fee", String.valueOf(amount));
    		reqPrms.put("card_number", bankCard);
    		reqPrms.put("bank_name", bankName);
    		reqPrms.put("bank_addr", openBankName);
    		String sign = Md5Util.md5("customerid="+reqPrms.getString("customerid")+"&orderId="+reqPrms.getString("orderId")+"&name="+reqPrms.getString("name")+"&card_number="+reqPrms.getString("card_number")+"&total_fee="+reqPrms.getString("total_fee")+"&key="+md5Key);
    		reqPrms.put("sign", sign);
    		
    		logger.info("惠云支付代付接口入参:{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(url, reqPrms);
    		logger.info("惠云支付代付接口返参:{}",response);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = null;
            try {
            	jsonData = JSONObject.parseObject(response);
			} catch (Exception e) {}
            if(jsonData !=null && "0000".equals(jsonData.getString("status"))){
            	boolean isSuccess = replaceOrderQuery(merchantCode, md5Key, reqData.getString("vcOrderNo"));
	           	if(isSuccess){
	           		 result.put("code", Constant.SUCCESSS);
                     result.put("msg", "代付成功");
                     result.put("status", 1);
                     return listener.successHandler(result);
	           	} 
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg",response);
                return listener.paddingHandler(result);
            }else{
            	 result.put("status", 3);
                 result.put("code", Constant.FAILED);
                 result.put("msg",StringUtils.isNoneBlank(response)?response:"代付申请失败");
                 return listener.failedHandler(result);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("惠云支付代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
    
    /**
     * @描述:惠云支付代付订单查询接口
     * @时间:2018/6/15 11:22
     */
    @SuppressWarnings("all")
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String queryUrl = "http://www.heepayvip.com/forwardorder";
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("customerid", merchantCode);
    		reqPrms.put("order", reqData.getString("vcOrderNo"));
    		String sign = Md5Util.md5("customerid="+reqPrms.getString("customerid")+"&order="+reqPrms.getString("order")+"&key="+md5Key);
    		reqPrms.put("sign", sign);
    		
    		logger.info("惠云支付代付订单查询接口入参:{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("惠云支付代付订单查询接口响应:{}",response);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = null;
            try {
            	jsonData = JSONObject.parseObject(response);
			} catch (Exception e) {}
            if(jsonData == null){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", StringUtils.isNoneBlank(response)?response:"查询失败");
                return listener.paddingHandler(result);
            }
            //00:待处理;01:已付款;02:已冻结;03:已拒绝;04:系统错误,联系上游
            if(jsonData.containsKey("status") && "01".equals(jsonData.getString("status"))){
            	 result.put("code", Constant.SUCCESSS);
                 result.put("msg", "代付成功");
                 result.put("status", 1);
                 return listener.successHandler(result);
            }else if("03".equals(jsonData.getString("status"))){
                result.put("status", 3);
                result.put("code", Constant.FAILED);
                result.put("msg",jsonData.get("msg")==null?"代付失败":jsonData.getString("msg"));
                return listener.failedHandler(result);
            }
            if(jsonData.containsKey("msg")){
            	result.put("msg",jsonData.get("msg"));
            }else{
            	result.put("msg", "处理中");
            }
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            return listener.paddingHandler(result);
        }catch (Exception e){
            logger.error("惠云支付查询接口异常",e);
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
    
    
    @SuppressWarnings("all")
    public boolean replaceOrderQuery(String merchantCode,String md5Key,String orderNo) {
        JSONObject result = new JSONObject();
        try {
            String queryUrl = "http://www.heepayvip.com/forwardorder";
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("customerid", merchantCode);
    		reqPrms.put("order", orderNo);
    		String sign = Md5Util.md5("customerid="+reqPrms.getString("customerid")+"&order="+reqPrms.getString("order")+"&key="+md5Key);
    		reqPrms.put("sign", sign);
    		
    		logger.info("代付订单查询接口入参:{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("代付订单查询接口响应:{}",response);
            if(StringUtils.isEmpty(response)){
                return false;
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if(jsonData == null){
                return false;
            }
            //00:待处理;01:已付款;02:已冻结;03:已拒绝;04:系统错误,联系上游
            if(jsonData.containsKey("status") && "01".equals(jsonData.getString("status"))){
            	 return true;
            }else{
            	return false;
            }
        }catch (Exception e){
            logger.error("惠云支付查询接口异常",e);
            return false;
        }
    }
    
    /**
     * @描述:惠云余额查询
     * @时间:2018/6/15 11:23
     */
    public JSONObject walletQuery(JSONObject reqData,ResultListener listener){
        JSONObject result = new JSONObject();
        result.put("code", Constant.FAILED);
        try {
            result.put("balance", "0");
            result.put("code", Constant.FAILED);
            result.put("msg","暂无查询接口");
            return listener.failedHandler(result);
        } catch (Exception e) {
            logger.error("惠云支付余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }
}
