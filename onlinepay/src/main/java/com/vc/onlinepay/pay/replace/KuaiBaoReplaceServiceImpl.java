/**
 * @类名称:InpayReplaceServiceImpl.java
 * @时间:2017年12月19日上午11:09:35
 * @作者:nada
 * @版权:公司 Copyright (c) 2017
 */
package com.vc.onlinepay.pay.replace;


import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;


/**
 * @描述:快包代付
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component
public class KuaiBaoReplaceServiceImpl {
    
    private static Logger logger = LoggerFactory.getLogger(KuaiBaoReplaceServiceImpl.class);

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
    		logger.info("快包代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("快包代付订单查询接口响应{}",response);
    		
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
     * @描述:快包代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("快包代付接口reqData:{}",reqData);
        	String dfURL = "https://api.fufengpay.com/payment/pay";
        	
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
    		
            String version = "1.0";
            String order_id = out_trade_no;
            String amount= reqData.getString ("amount");
            amount = new BigDecimal(amount).multiply(new BigDecimal(100)).intValue()+"";
            String back_url = reqData.getString ("projectDomainUrl") + "/kuaiBaoReplaceCallBackController";
            String front_url = reqData.getString("projectDomainUrl")+"/success";
            
	        String c_group_id = "5071";
	        
            JSONObject parms = new JSONObject();
            parms.put ("app_id",mchid);
            parms.put ("version",version);
            parms.put ("c_group_id", c_group_id);
            parms.put ("order_id", order_id);
            parms.put ("amount",amount);
            parms.put ("front_url",front_url);
            parms.put ("back_url",back_url);
            parms.put ("card",cardnumber);
            parms.put ("real_name",accountname);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("sign",sign);
            
            logger.info("代付接口入参{}",parms);
			
			String response = HttpClientTools.httpSendPostFrom(dfURL,parms);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            logger.info("快包代付接口返参:{}",jsonData);
            if("10000".equals(jsonData.getString("errcode"))){
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
            logger.error("快包代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
    
    /**
     * @描述:快包代付订单查询接口
     * @时间:2018/6/15 11:22
     */
    @SuppressWarnings("all")
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String queryUrl = "https://api.fufengpay.com/payment/query";
            String mchid = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("order_id", reqData.getString("vcOrderNo"));
    		reqPrms.put("app_id", mchid);
    		String sourctxt1 = Md5CoreUtil.getSignStr(reqPrms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
    		reqPrms.put("sign", pay_md5sign);
    		
    		logger.info("快包代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("快包代付订单查询接口响应{}",response);
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
            if(jsonData.containsKey("status") && "19".equals(jsonData.getString("status"))){
            	 result.put("code", Constant.SUCCESSS);
                 result.put("msg", "代付成功");
                 result.put("status", 1);
                 return listener.successHandler(result);
            }else if("17".equals(jsonData.getString("status"))){
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
            logger.error("快包代付查询接口异常",e);
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
        	String queryUrl = "https://api.fufengpay.com/payment/query";
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("order_id", orderNo);
    		reqPrms.put("app_id", merchantCode);
    		String sourctxt1 = Md5CoreUtil.getSignStr(reqPrms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
    		reqPrms.put("sign", pay_md5sign);
    		
    		logger.info("快包代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("快包代付订单查询接口响应{}",response);
            if(StringUtils.isEmpty(response)){
                return false;
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if(jsonData == null){
                return false;
            }
            if(jsonData.containsKey("status") && "19".equals(jsonData.getString("status"))){
            	 return true;
            }else{
            	return false;
            }
        }catch (Exception e){
            logger.error("快包代付查询接口异常",e);
            return false;
        }
    }
    
    /**
     * @描述:快包银联余额查询
     * @时间:2018/6/15 11:23
     */
    public JSONObject walletQuery(JSONObject reqData,ResultListener listener){
        JSONObject result = new JSONObject();
        result.put("code", Constant.FAILED);
        try {
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));

            String url = "https://api.fufengpay.com/payment/query-balance";
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("app_id", merchantCode);
    		String sourctxt1 = Md5CoreUtil.getSignStr(reqPrms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
    		reqPrms.put("sign", pay_md5sign);
    		
    		logger.info("快包代付余额查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(url, reqPrms);
    		logger.info("快包代付余额查询接口响应{}",response);
    		
            if(StringUtils.isEmpty(response)){
                result.put("balance", "0");
                result.put("code", Constant.FAILED);
                result.put("msg", "查询超时");
                return listener.failedHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            JSONObject jsonData2 = JSONObject.parseObject(jsonData.getString("content"));
            if(!jsonData2.isEmpty()){
            	String balance =jsonData2.containsKey("available")?jsonData2.getString("available"):"0";
            	//balance = new BigDecimal(balance).divide(new BigDecimal(100)).doubleValue()+"";
                result.put("code", Constant.SUCCESSS);
                result.put("balance", new BigDecimal(balance).divide(new BigDecimal(100)).doubleValue());
                result.put("msg", "查询成功！");
                return listener.successHandler(result);
            }
            result.put("balance", "0");
            result.put("code", Constant.FAILED);
            result.put("msg",jsonData.containsKey("msg")?jsonData.getString("msg"):"查询失败");
            return listener.failedHandler(result);
        } catch (Exception e) {
            logger.error("快包代付余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }
}
