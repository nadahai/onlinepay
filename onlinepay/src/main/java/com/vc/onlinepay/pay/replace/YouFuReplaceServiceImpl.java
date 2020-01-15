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
 * @描述:友付代付
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component
public class YouFuReplaceServiceImpl {
    
    private static Logger logger = LoggerFactory.getLogger(YouFuReplaceServiceImpl.class);
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
     * @描述:友付代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("友付代付接口reqData:{}",reqData);
        	
        	String url = "https://payment.surperpay.com/payment/paymentPay";
        	
            result.put("orderNo",reqData.getString("vcOrderNo"));
            
            
            //String openBankName = 
            //String city = reqData.containsKey("city")?reqData.getString("city"):"其它";
            //String bankSubName = reqData.containsKey("bankSubName")?reqData.getString("bankSubName"):"其它";
            //String bankLinked = reqData.containsKey("bankLinked")?reqData.getString("bankLinked"):"其它";
            
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            
            String version="2.0";
            String charset="UTF-8";
            String spid = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String spbillno = reqData.getString("vcOrderNo");
            String province = reqData.containsKey("province")?reqData.getString("province"):"其它";
            Double amount = Double.valueOf (province);
            String tranAmt = amount*100+"";
            String acctName = reqData.containsKey("accountName")?reqData.getString("accountName"):"其它";
            String acctId = reqData.containsKey("bankCardNo")?reqData.getString("bankCardNo"):"其它";
            String acctType="0";
            String certType="1";
            String certId=reqData.containsKey("idCardNo")?reqData.getString("idCardNo"):"1";
            String mobile = reqData.containsKey("mobile")?reqData.getString("mobile"):"其它";
            String bankName = reqData.containsKey("openBankName")?reqData.getString("openBankName"):"其它";
            String bankCode = reqData.containsKey("bankCode")?reqData.getString("bankCode"):"其它";
            
            for (Map.Entry<String, String> entity : bankAbbr.entrySet()) {
                if(bankCode.indexOf(entity.getKey())>-1){
                	bankCode = entity.getValue();
                    break;
                }
            }
            String accountNo="NP-47881461";
            String notifyUrl=reqData.getString("projectDomainUrl")+"/youFuReplaceCallBackController";
            String attach=spbillno;
            
            
            Map<String, String> parms = new HashMap<String, String>();
	         parms.put ("version",version);
	         parms.put ("charset",charset);
	         parms.put ("spid",spid);
	         parms.put ("spbillno", spbillno);
	         parms.put ("tranAmt", tranAmt);
	         parms.put ("acctName",acctName);
	         parms.put ("acctId",acctId);
	         parms.put ("acctType",acctType);
	         parms.put ("certType",certType);
	         parms.put ("certId",certId);
	         parms.put ("mobile",mobile);
	         parms.put ("bankName",bankName);
	         parms.put ("bankCode",bankCode);
	         parms.put ("accountNo",accountNo);
	         parms.put ("notifyUrl",notifyUrl);
	         parms.put ("attach",attach);
	         String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+md5Key;
	         logger.info("排序后{}",sourctxt1);
	         
	         String sign = Md5Util.md5(sourctxt1).toUpperCase();
	         String signType="MD5";
	         parms.put ("signType",signType);
	         parms.put ("sign",sign);
	         String strxml = HttpClientTools.getStringXML(parms);
    		logger.info("友付代付接口入参:{}",strxml);
    		String response = HttpClientTools.httpPostWithXML(url, strxml);
    		if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
    		cn.hutool.json.JSONObject payParams = XML.toJSONObject(response);
	         if(payParams == null || payParams.isEmpty () 
	            || !"0".equals(payParams.getJSONObject("xml").getStr("retcode"))
	        		 ){
	        	 result.put("status", 2);
	             result.put("code", Constant.FAILED);
	             result.put("msg", "发送超时");
	             return listener.paddingHandler(result);
	          }
            
            logger.info("友付代付接口返参:{}",payParams);
            if("0".equals(payParams.getJSONObject("xml").getStr("retcode"))){
				/*
				 * boolean isSuccess = replaceOrderQuery(spid, md5Key,
				 * reqData.getString("vcOrderNo")); if(isSuccess){ result.put("code",
				 * Constant.SUCCESSS); result.put("msg", "代付成功"); result.put("status", 1);
				 * return listener.successHandler(result); }
				 */
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "代付申请已提交");
                return listener.paddingHandler(result);
            }else{
            	 result.put("status", 3);
                 result.put("code", Constant.FAILED);
                 result.put("msg",payParams.getJSONObject("xml").getStr("retmsg")==null?"代付失败":payParams.getJSONObject("xml").getStr("retmsg"));
                 return listener.failedHandler(result);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("友付支付代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
    
    /**
     * @描述:友付代付订单查询接口
     * @时间:2018/6/15 11:22
     */
    @SuppressWarnings("all")
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            
            String queryUrl = "https://payment.surperpay.com/payment/paymentQuery";
            String version = "2.0";
            String charset = "UTF-8";
            String spid = merchantCode;
            String spbillno = reqData.getString("vcOrderNo");
            
            
            Map<String, String> parms = new HashMap<String, String>();
	         parms.put ("version",version);
	         parms.put ("charset",charset);
	         parms.put ("spid",spid);
	         parms.put ("spbillno", spbillno);
	         
	         String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+md5Key;
	         logger.info("排序后{}",sourctxt1);
	         
	         String sign = Md5Util.md5(sourctxt1).toUpperCase();
	         String signType="MD5";
	         parms.put ("signType",signType);
	         parms.put ("sign",sign);
	         String strxml = HttpClientTools.getStringXML(parms);
	         logger.info("友付代付订单查询接口入参:{}",strxml);
	   		String response = HttpClientTools.httpPostWithXML(queryUrl, strxml);
    		logger.info("拼多多代付订单查询接口响应{}",response);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            cn.hutool.json.JSONObject payParams = XML.toJSONObject(response);
            if(payParams == null){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "查询失败");
                return listener.paddingHandler(result);
            }
            if(payParams.getJSONObject("xml").containsKey("retcode") 
            		&& "0".equals(payParams.getJSONObject("xml").getStr("retcode"))){
            	 result.put("code", Constant.SUCCESSS);
                 result.put("msg", "代付成功");
                 result.put("status", 1);
                 return listener.successHandler(result);
            }else if(!"0".equals(payParams.getJSONObject("xml").getStr("retcode"))){
                result.put("status", 3);
                result.put("code", Constant.FAILED);
                result.put("msg",payParams.getJSONObject("xml").getStr("retmsg")==null?"代付失败":payParams.getJSONObject("xml").getStr("retmsg"));
                return listener.failedHandler(result);
            }
            if(payParams.getJSONObject("xml").containsKey("retmsg")){
            	result.put("msg",payParams.getJSONObject("xml").getStr("retmsg"));
            }else{
            	result.put("msg", "处理中");
            }
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            return listener.paddingHandler(result);
        }catch (Exception e){
            logger.error("友付代付查询接口异常",e);
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
     * @描述:友付余额查询
     * @时间:2018/6/15 11:23
     */
    public JSONObject walletQuery(JSONObject reqData,ResultListener listener){
        JSONObject result = new JSONObject();
        result.put("code", Constant.FAILED);
        try {
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            String url = "https://payment.surperpay.com/payment/balance";
            
            String version = "2.0";
            String charset = "UTF-8";
            String spid = merchantCode;
            String spbillno = (System.currentTimeMillis () + "").substring (0,13);
            
            Map<String, String> parms = new HashMap<String, String>();
	         parms.put ("version",version);
	         parms.put ("charset",charset);
	         parms.put ("spid",spid);
	         parms.put ("spbillno", spbillno);
	         
	         String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+md5Key;
	         logger.info("排序后{}",sourctxt1);
	         String sign = Md5Util.md5(sourctxt1).toUpperCase();
	         String signType="MD5";
	         parms.put ("signType",signType);
	         parms.put ("sign",sign);
	         String strxml = HttpClientTools.getStringXML(parms);
	         logger.info("友付余额查询接口入参:{}",strxml);
	   		String response = HttpClientTools.httpPostWithXML(url, strxml);
	   		logger.info("友付余额查询接口响应{}",response);
	   		if(StringUtils.isEmpty(response)){
                result.put("balance", "0");
                result.put("code", Constant.FAILED);
                result.put("msg", "查询超时");
                return listener.failedHandler(result);
            }
	   		
           cn.hutool.json.JSONObject payParams = XML.toJSONObject(response);
            //友付余额查询接口响应
            if(payParams.getJSONObject("xml").containsKey("retcode")
            		&& "0".equals(payParams.getJSONObject("xml").getStr("retcode"))){
            	 String balance =payParams.getJSONObject("xml").containsKey("availableBalance")?payParams.getJSONObject("xml").getStr("availableBalance"):"0";
                 
            	 result.put("code", Constant.SUCCESSS);
                 result.put("balance", Double.parseDouble(balance)*100);
                 result.put("msg", "查询成功！");
                 return listener.successHandler(result);
            }
            result.put("balance", "0");
            result.put("code", Constant.FAILED);
            result.put("msg",payParams.getJSONObject("xml").containsKey("retmsg")?payParams.getJSONObject("xml").getStr("retmsg"):"查询失败");
            return listener.failedHandler(result);
        } catch (Exception e) {
            logger.error("友付余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }
}
