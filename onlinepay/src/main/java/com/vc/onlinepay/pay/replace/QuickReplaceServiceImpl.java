/**
 * @类名称:InpayReplaceServiceImpl.java
 * @时间:2017年12月19日上午11:09:35
 * @作者:nada
 * @版权:公司 Copyright (c) 2017
 */
package com.vc.onlinepay.pay.replace;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpsClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;


/**
 * @描述:速付
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component
public class QuickReplaceServiceImpl {
    
    private static Logger logger = LoggerFactory.getLogger(JiaLiangReplaceServiceImpl.class);

    private static String url = "https://pay.rqust.com/Pay_DaiFu.html";
    
  //银行简称
  	public static final Map<String, String> bankAbbr = new HashMap<>();
  	static {
  		bankAbbr.put("01000000","POST");
		bankAbbr.put("01020000","ICBC");
		bankAbbr.put("03080000","CMBCHINA");
		bankAbbr.put("01030000","ABC");
		bankAbbr.put("01050000","CCB");
		bankAbbr.put("04031000","BCCB");
		bankAbbr.put("03010000","BCM");
		bankAbbr.put("03090000","CIB");
		bankAbbr.put("03050000","CMSB");
		bankAbbr.put("03030000","CEB");
		bankAbbr.put("01040000","BOC");
		bankAbbr.put("04100000","PAB");
		bankAbbr.put("03020000","CNCB");
		bankAbbr.put("03070000","SDB");
		bankAbbr.put("03060000","GDB");
		bankAbbr.put("04012900","SHB");
		bankAbbr.put("03100000","SPDB");
		bankAbbr.put("03040000","HXB");
		bankAbbr.put("14181000","BRCB");
		bankAbbr.put("04243010","BON");
		bankAbbr.put("04083320","NBCB");
  	}
  	
    /**
     * @描述:速付支付代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("速付支付代付接口reqData:{}",reqData);
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            String orderNo = reqData.getString("vcOrderNo");
            String openBankName = reqData.containsKey("openBankName")?reqData.getString("openBankName"):"其它";
            String accountName = reqData.containsKey("accountName")?reqData.getString("accountName"):"其它";
            String bankCard = reqData.containsKey("bankCard")?reqData.getString("bankCard"):"其它";
            String bankName = reqData.containsKey("bankName")?reqData.getString("bankName"):"其它";
            String city = reqData.containsKey("city")?reqData.getString("city"):"其它";
            String province = reqData.containsKey("province")?reqData.getString("province"):"其它";
            BigDecimal amount = new BigDecimal(reqData.getString("amount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            String bankCode = reqData.getString("bankCode");
            if(bankAbbr.containsKey(bankCode)){
            	bankCode = bankAbbr.get(bankCode);
            }
            
    		JSONObject reqPrms = new JSONObject();
    		reqPrms.put("memberid", merchantCode);
    		reqPrms.put("orderid", orderNo);
    		reqPrms.put("amount", String.valueOf(amount));
    		reqPrms.put("applydate", Constant.yyyyMMdd.format(new Date()));
    		reqPrms.put("bankname", bankName);
    		reqPrms.put("bankfenname", openBankName);
    		reqPrms.put("bankzhiname", openBankName);
    		reqPrms.put("accountname", accountName);
    		reqPrms.put("bankcode", bankCard);
    		reqPrms.put("sheng", province);
    		reqPrms.put("shi", city);
    		reqPrms.put("tongdao", bankCode);
    		String sign = Md5CoreUtil.md5ascii(reqPrms,md5Key).toUpperCase();
    		reqPrms.put("md5sign", sign);
    		
    		logger.info("速付支付代付接口入参:{}",reqPrms);
    		String response =  HttpsClientTools.sendHttpSSL_appljson(reqPrms,url);
    		logger.info("速付支付代付接口返参:{}",response);
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
            	 result.put("code", "10001");
                 result.put("msg", "代付响应为空");
                 result.put("status", 2);
                 return listener.paddingHandler(result);
            }
            if("00".equals(jsonData.getString("status"))){
              	 result.put("code", Constant.SUCCESSS);
               result.put("msg", "代付成功");
               result.put("status", 1);
               return listener.successHandler(result);
          }else if("01".equals(jsonData.getString("status"))){
          	 	result.put("status", 3);
               result.put("code", Constant.FAILED);
               result.put("msg",jsonData.containsKey("msg")?jsonData.getString("msg"):"代付失败");
               return listener.failedHandler(result);
          }else{
          	 	result.put("status", 2);
               result.put("code", Constant.FAILED);
               result.put("msg",jsonData.containsKey("msg")?jsonData.getString("msg"):"代付中");
               return listener.paddingHandler(result);
          }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("速付支付代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
    
    /**
     * @描述:速付支付代付订单查询接口
     * @时间:2018/6/15 11:22
     */
    @SuppressWarnings("all")
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String queryUrl = "https://pay.rqust.com/Pay_DaiFu_query.html";
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("memberid", merchantCode);
    		reqPrms.put("orderid", reqData.getString("vcOrderNo"));
    		String sign = Md5Util.md5("memberid="+reqPrms.getString("memberid")+"&orderid="+reqPrms.getString("orderid")+"&key="+md5Key);
    		reqPrms.put("md5sign", sign.toUpperCase());
    		
    		logger.info("速付支付代付订单查询接口入参:{}",reqPrms);
    		String response =  HttpsClientTools.sendHttpSSL_appljson(reqPrms,queryUrl);
    		logger.info("速付支付代付订单查询接口响应:{}",response);
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
            if(jsonData.containsKey("status") && "00".equals(jsonData.getString("status"))){
            	 result.put("code", Constant.SUCCESSS);
                 result.put("msg", "代付成功");
                 result.put("status", 1);
                 return listener.successHandler(result);
            }else if("01".equals(jsonData.getString("status"))){
                result.put("status", 3);
                result.put("code", Constant.FAILED);
                result.put("msg",jsonData.get("msg")==null?"代付失败":jsonData.getString("msg"));
                return listener.failedHandler(result);
            }
            result.put("msg",jsonData.containsKey("msg")?jsonData.get("msg"):"处理中");
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            return listener.paddingHandler(result);
        }catch (Exception e){
            logger.error("速付支付查询接口异常",e);
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
    
    public static void main(String[] args) {
    	try {
    		String payurl = "https://pay.rqust.com/Pay_Index.html";
    		String replaceUrl = "https://pay.rqust.com/Pay_DaiFu.html";
    		String queryUrl = "https://pay.rqust.com/Pay_DaiFu_query.html";
    		String memberid = "10224";
			String orderid = "123456";
			JSONObject reqPrms = new JSONObject();
			String md5Key = "W8jxMZilwabu93ua8zdn5Rbruf97L1";
    		String sign = Md5Util.md5("memberid="+memberid+"&orderid="+orderid+"&key="+md5Key);
    		reqPrms.put("memberid", memberid);
    		reqPrms.put("orderid", orderid);
    		reqPrms.put("md5sign", sign.toUpperCase());
    		String result =  HttpsClientTools.sendHttpSSL_textjson(reqPrms, replaceUrl);
    		System.out.println(result);
    		
		    /*HttpClient httpClient = new HttpClient();  	
			PostMethod postMethod = new PostMethod(queryUrl);
			NameValuePair[] pairs = new NameValuePair[3]; 
    		pairs[0] = new NameValuePair("memberid",memberid);
			pairs[1] = new NameValuePair("orderid",orderid);
			pairs[2] = new NameValuePair("md5sign",sign);
			postMethod.setRequestBody(pairs); 
			httpClient.executeMethod(postMethod);
			byte[] responseBody = postMethod.getResponseBody();
		    String ssoVerifyString = new String(responseBody);
		    System.out.println(ssoVerifyString);*/
		} catch (Exception e) {
			e.printStackTrace();
            logger.info("模拟form发送 POST 请求出现异常", e);
		}
	}
    
    /**
     * @描述:速付余额查询
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
            logger.error("速付支付余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }
}
