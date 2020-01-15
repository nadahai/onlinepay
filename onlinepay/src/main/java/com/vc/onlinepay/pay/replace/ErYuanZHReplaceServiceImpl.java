/**
 * @类名称:InpayReplaceServiceImpl.java
 * @时间:2017年12月19日上午11:09:35
 * @作者:nada
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
 * @描述:二元智慧代付
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component
public class ErYuanZHReplaceServiceImpl {
    
    private static Logger logger = LoggerFactory.getLogger(ErYuanZHReplaceServiceImpl.class);

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
    		logger.info("二元智慧代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("二元智慧代付订单查询接口响应{}",response);
    		
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
     * @描述:二元智慧代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("二元智慧代付接口reqData:{}",reqData);
        	String dfURL = "https://www.transfarpay.net/Payment_Dfpay_add.html";
        	
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));//key
            
            String mchid = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));//商户号
            String out_trade_no = reqData.getString("vcOrderNo");//订单号
            String money = reqData.getString("amount");//金额
            String bankname = reqData.containsKey("bankName")?reqData.getString("bankName"):"其它";//银行名称
            String subbranch = reqData.containsKey("bankSubName")?reqData.getString("bankSubName"):"其它";//支行名称
            String accountname = reqData.containsKey("accountName")?reqData.getString("accountName"):"其它";//姓名
            String cardnumber = reqData.containsKey("bankCard")?reqData.getString("bankCard"):"其它";//银行卡号
            String province = reqData.containsKey("province")?reqData.getString("province"):"其它";//省会
            String city = reqData.containsKey("city")?reqData.getString("city"):"其它";//城市
    		
    		JSONObject reqPrms = new JSONObject();
    		reqPrms.put("mchid", mchid);
    		reqPrms.put("out_trade_no", out_trade_no);
    		reqPrms.put("money", money);
    		reqPrms.put("bankname", bankname);
    		reqPrms.put("subbranch", subbranch);
    		reqPrms.put("accountname", accountname);
    		reqPrms.put("cardnumber", cardnumber);
    		reqPrms.put("province", province);
    		reqPrms.put("city", city);
    		String sourctxt1 = Md5CoreUtil.getSignStr(reqPrms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
            reqPrms.put ("pay_md5sign",pay_md5sign);
    		
    		logger.info("二元智慧代付接口入参:{}",reqPrms);
    		String response =  HttpClientTools.httpSendHTTPSPostFrom(dfURL, reqPrms);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            logger.info("二元智慧代付接口返参:{}",jsonData);
            if("success".equals(jsonData.getString("status"))){
            	boolean isSuccess = replaceOrderQuery(mchid, md5Key, reqData.getString("vcOrderNo"));
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
            logger.error("二元智慧代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
    
    /**
     * @描述:二元智慧代付订单查询接口
     * @时间:2018/6/15 11:22
     */
    @SuppressWarnings("all")
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String queryUrl = "https://www.transfarpay.net/Payment_Dfpay_query.html";
            String mchid = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("out_trade_no", reqData.getString("vcOrderNo"));
    		reqPrms.put("mchid", mchid);
    		String sourctxt1 = Md5CoreUtil.getSignStr(reqPrms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
    		reqPrms.put("pay_md5sign", pay_md5sign);
    		
    		logger.info("二元智慧代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendHTTPSPostFrom(queryUrl, reqPrms);
    		logger.info("二元智慧代付订单查询接口响应{}",response);
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
            if(jsonData.containsKey("status") && "success".equals(jsonData.getString("status"))
            		&& "1".equals(jsonData.getString("refCode"))
            		){
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
            logger.error("二元智慧代付查询接口异常",e);
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
        	String queryUrl = "https://www.transfarpay.net/Payment_Dfpay_query.html";
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("out_trade_no", orderNo);
    		reqPrms.put("mchid", merchantCode);
    		String sourctxt1 = Md5CoreUtil.getSignStr(reqPrms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
    		reqPrms.put("pay_md5sign", pay_md5sign);
    		
    		logger.info("二元智慧代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendHTTPSPostFrom(queryUrl, reqPrms);
    		logger.info("二元智慧代付订单查询接口响应{}",response);
            if(StringUtils.isEmpty(response)){
                return false;
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if(jsonData == null){
                return false;
            }
            if(jsonData.containsKey("status") && "success".equals(jsonData.getString("status"))
            		&& "1".equals(jsonData.getString("refCode"))
            		){
            	 return true;
            }else{
            	return false;
            }
        }catch (Exception e){
            logger.error("二元智慧代付查询接口异常",e);
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

            String url = "https://www.transfarpay.net/Payment_Dfpay_balance.html";
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("mchid", merchantCode);
    		String sourctxt1 = Md5CoreUtil.getSignStr(reqPrms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
    		reqPrms.put("pay_md5sign", pay_md5sign);
    		
    		logger.info("二元智慧代付余额查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendHTTPSPostFrom(url, reqPrms);
    		logger.info("二元智慧代付余额查询接口响应{}",response);
    		
            if(StringUtils.isEmpty(response)){
                result.put("balance", "0");
                result.put("code", Constant.FAILED);
                result.put("msg", "查询超时");
                return listener.failedHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if("success".equals(jsonData.getString("status"))){
            	String balance =jsonData.containsKey("balance")?jsonData.getString("balance"):"0";
                result.put("code", Constant.SUCCESSS);
                result.put("balance", balance);
                result.put("msg", "查询成功！");
                return listener.successHandler(result);
            }
            result.put("balance", "0");
            result.put("code", Constant.FAILED);
            result.put("msg",jsonData.containsKey("msg")?jsonData.getString("msg"):"查询失败");
            return listener.failedHandler(result);
        } catch (Exception e) {
            logger.error("二元智慧代付余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }
}
