/**
 * @类名称:InpayReplaceServiceImpl.java
 * @时间:2017年12月19日上午11:09:35
 * @作者:nada
 * @版权:公司 Copyright (c) 2017
 */
package com.vc.onlinepay.pay.replace;

import java.util.HashMap;
import java.util.Map;
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

import cn.hutool.json.XML;


/**
 * @描述:拿铁代付
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component
public class NaTieReplaceServiceImpl {
    
    private static Logger logger = LoggerFactory.getLogger(NaTieReplaceServiceImpl.class);
    public static final Map<String, String> bankAbbr = new HashMap<String, String>();
    
  	static {
  		bankAbbr.put("工商银行","1001");
  		bankAbbr.put("农业银行","1002");
  		bankAbbr.put("中国银行","1003");
  		bankAbbr.put("建设银行","1004");
  		bankAbbr.put("交通银行","1005");
  		bankAbbr.put("邮储银行","1006");
  		bankAbbr.put("中信银行","1007");
  		bankAbbr.put("光大银行","1008");
  		bankAbbr.put("华夏银行","1009");
  		bankAbbr.put("民生银行","1010");
  		bankAbbr.put("平安银行","1011");
  		bankAbbr.put("招商银行","1012");
  		bankAbbr.put("江苏银行","1013");
  		bankAbbr.put("浦发银行","1014");
  		bankAbbr.put("江苏银行","1015");
  		bankAbbr.put("北京银行","1016");
  		bankAbbr.put("广发银行","1017");
  		bankAbbr.put("广州银行","1018");
  		bankAbbr.put("浙商银行","1019");
  		bankAbbr.put("恒丰银行","1020");
  		bankAbbr.put("南京银行","1021");
  		bankAbbr.put("宁波银行","1022");
  		bankAbbr.put("渤海银行","1023");
  		bankAbbr.put("哈尔滨银行","1024");
  		bankAbbr.put("上海银行","1025");
  		bankAbbr.put("中原银行","1026");
  		bankAbbr.put("杭州银行","1027");
  		bankAbbr.put("包商银行","1028");
  		bankAbbr.put("兴业银行","1029");
  		bankAbbr.put("北京农商行","2001");
  		bankAbbr.put("上海农商银行","2002");
  		bankAbbr.put("杭州联合银行","2003");
  		bankAbbr.put("温州市商业银行","2004");
  		bankAbbr.put("浙江稠州商业银行","2005");
  		bankAbbr.put("广州市农信社","2006");
  		bankAbbr.put("集友银行","2007");
  		bankAbbr.put("长沙银行","2008");
  		bankAbbr.put("重庆三峡银行","2009");
  		
  	}
  
    
    public static void main(String[] args) {
    	try {
    		String bankCode = "中国长沙银行";
    		for (Map.Entry<String, String> entity : bankAbbr.entrySet()) {
                if(bankCode.indexOf(entity.getKey())>-1){
                	bankCode = entity.getValue();
                     break;
                }
            }
    		System.out.println(bankCode);
			/*
			 * String merchantCode = "30067"; String md5Key =
			 * "TZRBcmFv71MVpxyjzQ0wDuy5uHJ0fuMy";
			 * 
			 * String queryUrl = "http://pay.niubizf.com/settlement/dftradeQuery";
			 * JSONObject reqPrms = new JSONObject(); reqPrms.put("p1_MerId", merchantCode);
			 * reqPrms.put("orderid", "df0429192115942346"); String pd_FrpId = "alipay_df";
			 * reqPrms.put("pd_FrpId", pd_FrpId);
			 * 
			 * StringBuffer sb=new StringBuffer(); sb.append(merchantCode);
			 * sb.append("df0429192115942346"); System.out.println(sb.toString()); String
			 * hmac = DigestUtil.hmacSign(sb.toString(), md5Key); //数据签名 reqPrms.put("hmac",
			 * hmac);
			 * 
			 * logger.info("拼多多代付订单查询接口入参{}",reqPrms); String response =
			 * HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
			 * logger.info("拼多多代付订单查询接口响应{}",response);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  	
    /**
     * @描述:拿铁代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("拿铁代付接口reqData:{}",reqData);
        	
        	String url = "http://pay.yudugs.com:89/tran/cashier/TX0001.ac";
        	
            result.put("orderNo",reqData.getString("vcOrderNo"));
            
            
            //String openBankName = 
            //String city = reqData.containsKey("city")?reqData.getString("city"):"其它";
            //String bankSubName = reqData.containsKey("bankSubName")?reqData.getString("bankSubName"):"其它";
            //String bankLinked = reqData.containsKey("bankLinked")?reqData.getString("bankLinked"):"其它";
            
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            
            String version="2.1";
            
            String custId=StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            
            String orgNo="0190600442";
            if("19062800002051".equals(custId)) {
            	orgNo="0190600441";
            }
            
            String custOrdNo = reqData.getString("vcOrderNo");
            String casType = "00";
            String prce = reqData.containsKey("amount")?reqData.getString("amount"):"0";
            Double amount = Double.parseDouble (prce)*100;
            String casAmt=amount.intValue()+"";
            String deductWay="02";
            //String callBackUrl=reqData.getString("projectDomainUrl")+"/naTieReplaceCallBackController";
            String accountName = reqData.containsKey("accountName")?reqData.getString("accountName"):"其它";//姓名
            String cardNo = reqData.containsKey("bankCard")?reqData.getString("bankCard"):"其它";//银行卡号
            
            JSONObject parms = new JSONObject();
            parms.put ("version",version);
            parms.put ("orgNo",orgNo);
            parms.put ("custId",custId);
            parms.put ("custOrdNo", custOrdNo);
            parms.put ("casType", casType);
            parms.put ("casAmt",casAmt);
            parms.put ("deductWay",deductWay);
            //parms.put ("callBackUrl",callBackUrl);
            parms.put ("accountName",accountName);
            parms.put ("cardNo",cardNo);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1);
            parms.put ("sign",sign);
            
    		logger.info("拿铁代付接口入参:{}",parms);
    		String response = HttpClientTools.httpSendPostFrom(url,parms);
            logger.info("拿铁代付接口返参{}",response);
    		if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
    		JSONObject payParams = Constant.stringToJson (response);
	        if(payParams == null || payParams.isEmpty () 
	            /*|| !"000000".equals(payParams.getString("code"))*/
	        		 ){
	        	 result.put("status", 2);
	             result.put("code", Constant.FAILED);
	             result.put("msg", "代付异常");
	             return listener.paddingHandler(result);
	          }
            
            logger.info("拿铁代付接口返参:{}",payParams);
            if("000000".equals(payParams.getString("code"))){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "代付申请已提交");
                return listener.paddingHandler(result);
            }else{
            	 result.put("status", 3);
                 result.put("code", Constant.FAILED);
                 result.put("msg",payParams.getString("msg")==null?"代付失败":payParams.getString("msg"));
                 return listener.failedHandler(result);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("拿铁支付代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
    
    /**
     * @描述:拿铁代付订单查询接口
     * @时间:2018/6/15 11:22
     */
    @SuppressWarnings("all")
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo",reqData.getString("vcOrderNo"));
            
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            
            String queryUrl = "http://pay.yudugs.com:89/tran/cashier/TX0002.ac";
            String version = "2.1";
            String orgNo="0190600442";
            String custId = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            if("19062800002051".equals(custId)) {
            	orgNo="0190600441";
            }
            String custOrdNo = reqData.getString("vcOrderNo");
            
            JSONObject parms = new JSONObject();
            parms.put ("version",version);
            parms.put ("orgNo",orgNo);
            parms.put ("custId",custId);
            parms.put ("custOrdNo", custOrdNo);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1);
            parms.put ("sign",sign);
           
            
            logger.info("拿铁代付查询接口入参:{}",parms);
    		String response = HttpClientTools.httpSendPostFrom(queryUrl,parms);
            logger.info("拿铁代付查询接口返参{}",response);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "查询失败");
                return listener.paddingHandler(result);
            }
            if(payParams.containsKey("ordStatus") 
            		&& "07".equals(payParams.getString("ordStatus"))){
            	 result.put("code", Constant.SUCCESSS);
                 result.put("msg", "代付成功");
                 result.put("status", 1);
                 return listener.successHandler(result);
            }else if(!"07".equals(payParams.getString("ordStatus"))){
                result.put("status", 3);
                result.put("code", Constant.FAILED);
                result.put("msg",payParams.getString("msg")==null?"代付失败":payParams.getString("msg"));
                return listener.failedHandler(result);
            }
            if(payParams.containsKey("msg")){
            	result.put("msg",payParams.getString("msg"));
            }else{
            	result.put("msg", "处理中");
            }
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            return listener.paddingHandler(result);
        }catch (Exception e){
            logger.error("拿铁代付查询接口异常",e);
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
     * @描述:拿铁余额查询
     * @时间:2018/6/15 11:23
     */
    public JSONObject walletQuery(JSONObject reqData,ResultListener listener){
        JSONObject result = new JSONObject();
        result.put("code", Constant.FAILED);
        try {
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            String queryUrl = "http://pay.yudugs.com:89/tran/cashier/balance.ac";
            
            String version = "2.1";
            String orgNo="0190600442";
            String custId = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            if("19062800002051".equals(custId)) {
            	orgNo="0190600441";
            }
           
            JSONObject parms = new JSONObject();
            parms.put ("version",version);
            parms.put ("orgNo",orgNo);
            parms.put ("custId",custId);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+md5Key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1);
            parms.put ("sign",sign);
           
            
            logger.info("拿铁代付余额查询接口入参:{}",parms);
    		String response = HttpClientTools.httpSendPostFrom(queryUrl,parms);
            logger.info("拿铁代付余额查询接口返参{}",response);
	   		if(StringUtils.isEmpty(response)){
                result.put("balance", "0");
                result.put("code", Constant.FAILED);
                result.put("msg", "查询超时");
                return listener.failedHandler(result);
            }
	   		
	   		JSONObject payParams = Constant.stringToJson (response);
            //拿铁余额查询接口响应
            if(payParams.containsKey("code")
            		&& "000000".equals(payParams.getString("code"))){
            	
            	String balance =payParams.containsKey("acBal")?payParams.getString("acBal"):"0";
            	 result.put("code", Constant.SUCCESSS);
                 result.put("balance", Double.parseDouble(balance)/100);
                 result.put("msg", "查询成功！");
                 return listener.successHandler(result);
            }
            result.put("balance", "0");
            result.put("code", Constant.FAILED);
            result.put("msg",payParams.containsKey("msg")?payParams.getString("msg"):"查询失败");
            return listener.failedHandler(result);
        } catch (Exception e) {
            logger.error("拿铁余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }
}
