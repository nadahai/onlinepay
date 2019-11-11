/**
 * @类名称:InpayReplaceServiceImpl.java
 * @时间:2017年12月19日上午11:09:35
 * @作者:lihai
 * @版权:公司 Copyright (c) 2017
 */
package com.vc.onlinepay.pay.replace;

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
 * @描述:嘉联支付
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component
public class JiaLiangReplaceServiceImpl {
    
    private static Logger logger = LoggerFactory.getLogger(JiaLiangReplaceServiceImpl.class);

    private static String url = "http://47.105.48.165/Payment_api";
    
    public static void main(String[] args) {
    	try {
    		String merchantCode = "10105";
            String md5Key = "b6w7m5yjfcf1zzp81g2a81ze55lax90x";
            JSONObject reqPrms = new JSONObject();
    		
            String queryUrl = "http://47.105.48.165/Payment_api_query";
            reqPrms.put("pay_memberid", merchantCode);
    		reqPrms.put("pay_orderid","df190305161327756813");
    		String md5str = Md5CoreUtil.md5ascii (reqPrms,md5Key).toUpperCase();
    		reqPrms.put("pay_md5sign", md5str);
    		logger.info("嘉联支付代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("嘉联支付代付订单查询接口响应{}",response);
    		
		    /*String url = "http://47.105.48.165/Payment_api_queryBalance";
     		reqPrms.put("pay_memberid", merchantCode);
     		String md5str = Md5CoreUtil.md5ascii (reqPrms,md5Key).toUpperCase();
     		reqPrms.put("pay_md5sign", md5str);
     		logger.info("嘉联余额查询接口入参{}",reqPrms);
     		String response =  HttpClientTools.httpSendPostFrom(url, reqPrms);
     		logger.info("嘉联余额查询接口响应{}",response);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  	
    /**
     * @描述:嘉联支付代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("嘉联支付代付接口reqData:{}",reqData);
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            String orderNo = reqData.getString("vcOrderNo");
            String openBankName = reqData.containsKey("openBankName")?reqData.getString("openBankName"):"其它";
            String city = reqData.containsKey("city")?reqData.getString("city"):"其它";
            String province = reqData.containsKey("province")?reqData.getString("province"):"其它";
            String accountName = reqData.containsKey("accountName")?reqData.getString("accountName"):"其它";
            String bankCard = reqData.containsKey("bankCard")?reqData.getString("bankCard"):"其它";
            String bankName = reqData.containsKey("bankName")?reqData.getString("bankName"):"其它";
    		
    		JSONObject reqPrms = new JSONObject();
    		reqPrms.put("pay_memberid", merchantCode);
    		reqPrms.put("pay_amount", reqData.getString("amount"));
    		reqPrms.put("pay_orderid", orderNo);
    		reqPrms.put("pay_banknumber", bankCard);
    		reqPrms.put("pay_applydate", Constant.yyyyMMdd.format(new Date()));
    		String md5str = Md5CoreUtil.md5ascii (reqPrms,md5Key).toUpperCase();
    		reqPrms.put("pay_md5sign", md5str);
    		reqPrms.put("pay_bankname", bankName);
    		reqPrms.put("pay_bankzhiname", openBankName);
    		reqPrms.put("pay_bankfullname", accountName);
    		reqPrms.put("pay_province", province);
    		reqPrms.put("pay_city", city);
    		
    		logger.info("嘉联支付代付接口入参:{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(url, reqPrms);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            logger.info("嘉联支付代付接口返参:{}",jsonData);
            if("00".equals(jsonData.getString("status"))){
            	boolean isSuccess = replaceOrderQuery(merchantCode, md5Key, reqData.getString("vcOrderNo"));
	           	if(isSuccess){
	           		 result.put("code", Constant.SUCCESSS);
                     result.put("msg", "代付成功");
                     result.put("status", 1);
                     return listener.successHandler(result);
	           	} 
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "代付申请已提交");
                return listener.paddingHandler(result);
            }else{
            	 result.put("status", 3);
                 result.put("code", Constant.FAILED);
                 result.put("msg",jsonData.get("msg")==null?"代付失败":jsonData.getString("msg"));
                 return listener.failedHandler(result);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("嘉联支付代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
    
    /**
     * @描述:嘉联支付代付订单查询接口
     * @时间:2018/6/15 11:22
     */
    @SuppressWarnings("all")
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String queryUrl = "http://47.105.48.165/Payment_api_query";
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("pay_memberid", merchantCode);
    		reqPrms.put("pay_orderid", reqData.getString("vcOrderNo"));
    		String md5str = Md5CoreUtil.md5ascii (reqPrms,md5Key).toUpperCase();
    		reqPrms.put("pay_md5sign", md5str);
    		
    		logger.info("嘉联支付代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("嘉联支付代付订单查询接口响应{}",response);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if(jsonData == null){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "查询失败");
                return listener.paddingHandler(result);
            }
            if(jsonData.containsKey("status") && "00".equals(jsonData.getString("status"))){
            	 result.put("code", Constant.SUCCESSS);
                 result.put("msg", "代付成功");
                 result.put("status", 1);
                 return listener.successHandler(result);
            }else if("3".equals(jsonData.getString("status"))){
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
            logger.error("嘉联支付查询接口异常",e);
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
            String queryUrl = "http://47.105.48.165/Payment_api_query";
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("pay_memberid", merchantCode);
    		reqPrms.put("pay_orderid", orderNo);
    		String md5str = Md5CoreUtil.md5ascii (reqPrms,md5Key).toUpperCase();
    		reqPrms.put("pay_md5sign", md5str);
    		logger.info("代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("代付订单查询接口响应{}",response);
            if(StringUtils.isEmpty(response)){
                return false;
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if(jsonData == null){
                return false;
            }
            if(jsonData.containsKey("status") && "00".equals(jsonData.getString("status"))){
            	 return true;
            }else{
            	return false;
            }
        }catch (Exception e){
            logger.error("嘉联支付查询接口异常",e);
            return false;
        }
    }
    
    /**
     * @描述:嘉联余额查询
     * @时间:2018/6/15 11:23
     */
    public JSONObject walletQuery(JSONObject reqData,ResultListener listener){
        JSONObject result = new JSONObject();
        result.put("code", Constant.FAILED);
        try {
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));

            String url = "http://47.105.48.165/Payment_api_queryBalance";
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("pay_memberid", merchantCode);
    		String md5str = Md5CoreUtil.md5ascii (reqPrms,md5Key).toUpperCase();
    		reqPrms.put("pay_md5sign", md5str);
    		
    		logger.info("嘉联余额查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(url, reqPrms);
    		logger.info("嘉联余额查询接口响应{}",response);
    		
            if(StringUtils.isEmpty(response)){
                result.put("balance", "0");
                result.put("code", Constant.FAILED);
                result.put("msg", "查询超时");
                return listener.failedHandler(result);
            }
            //嘉联余额查询接口响应{"status":"00","msg":"请求成功!","data":{"memberid":10105,"balance":"10.00"}}
            JSONObject jsonData = JSONObject.parseObject(response);
            if("00".equals(jsonData.getString("status"))){
            	 JSONObject Data = jsonData.getJSONObject("data");
            	 if(Data !=null){
            		   String balance =Data.containsKey("balance")?Data.getString("balance"):"0";
                       result.put("code", Constant.SUCCESSS);
                       result.put("balance", balance);
                       result.put("msg", "查询成功！");
                       return listener.successHandler(result);
            	 }
            }
            result.put("balance", "0");
            result.put("code", Constant.FAILED);
            result.put("msg",jsonData.containsKey("msg")?jsonData.getString("msg"):"查询失败");
            return listener.failedHandler(result);
        } catch (Exception e) {
            logger.error("嘉联支付余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }
}
