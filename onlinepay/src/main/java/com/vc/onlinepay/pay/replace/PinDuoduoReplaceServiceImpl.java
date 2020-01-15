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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DigestUtil;
import com.vc.onlinepay.utils.sand.MD5;


/**
 * @描述:拼多多代付
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component
public class PinDuoduoReplaceServiceImpl {
    
    private static Logger logger = LoggerFactory.getLogger(PinDuoduoReplaceServiceImpl.class);
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
    

    private static String url = "http://pay.niubizf.com/settlement/withdrawalPost";
    private static String psd = "admin0021243";//支付密码
    
    public static void main(String[] args) {
    	try {
    		String merchantCode = "30067";
            String md5Key = "TZRBcmFv71MVpxyjzQ0wDuy5uHJ0fuMy";
        	
            String queryUrl = "http://pay.niubizf.com/settlement/dftradeQuery";
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("p1_MerId", merchantCode);
    		reqPrms.put("orderid", "df0429192115942346");
    		String pd_FrpId = "alipay_df";
    		reqPrms.put("pd_FrpId", pd_FrpId);
    		
    		StringBuffer sb=new StringBuffer();
        	sb.append(merchantCode);
        	sb.append("df0429192115942346");
            System.out.println(sb.toString());
            String hmac = DigestUtil.hmacSign(sb.toString(), md5Key); //数据签名
    		reqPrms.put("hmac", hmac);
    		
    		logger.info("拼多多代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("拼多多代付订单查询接口响应{}",response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  	
    /**
     * @描述:拼多多代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("拼多多代付接口reqData:{}",reqData);
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
            String bankSubName = reqData.containsKey("bankSubName")?reqData.getString("bankSubName"):"其它";
            String mobile = reqData.containsKey("mobile")?reqData.getString("mobile"):"其它";
            String idCardNo = reqData.containsKey("idCardNo")?reqData.getString("idCardNo"):"1";
            String bankLinked = reqData.containsKey("bankLinked")?reqData.getString("bankLinked"):"其它";
            String bankCode = bankName;
			/*
			 * if(bankAbbr.containsKey(bankCode)){ bankCode = bankAbbr.get(bankCode); }
			 */
            
    		JSONObject reqPrms = new JSONObject();
    		reqPrms.put("p1_MerId", merchantCode);
    		reqPrms.put("p2_Order", orderNo);
    		reqPrms.put("p3_Amount", reqData.getString("amount"));
    		String p4_Payment = MD5.md5(psd);
    		reqPrms.put("p4_Payment", p4_Payment);
    		String p5_RefundType = "alipay_df";
    		reqPrms.put("p5_RefundType", p5_RefundType);
    		String p6_PayType = "d0";
    		reqPrms.put("p6_PayType", p6_PayType);
    		reqPrms.put("pa_AccountName", accountName);
    		reqPrms.put("pb_BankAccount", bankCard);
    		reqPrms.put("pc_BankCod", bankCode);
    		reqPrms.put("pd_BankName", bankSubName);
    		reqPrms.put("pd_branchsheng", province);
    		reqPrms.put("pd_branchshi", city);
    		reqPrms.put("pe_BankLinked", bankLinked);
    		reqPrms.put("pf_Mobile", mobile);
    		reqPrms.put("pg_CardId", idCardNo);
    		
    		String sbOld = merchantCode;
	   		 sbOld += orderNo;
	   		 sbOld += reqData.getString("amount");
	   		 sbOld += p4_Payment;
	   		 sbOld += p5_RefundType;
	   		 sbOld += p6_PayType;		 
	   		 sbOld += accountName;
	   		 sbOld += bankCard;
	   		 sbOld += bankCode;
	   		 sbOld += bankSubName;
	   		 sbOld += province;
	   		 sbOld += city;
	   		 sbOld += bankLinked;  			
	   		 sbOld += mobile; 
	   		 sbOld += idCardNo;

	   		String hmac = DigestUtil.hmacSign(sbOld, md5Key); //数据签名
    		reqPrms.put("hmac", hmac);
    		
    		
    		logger.info("拼多多代付接口入参:{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(url, reqPrms);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            logger.info("拼多多代付接口返参:{}",jsonData);
            if("I".equals(jsonData.getString("status"))){
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
            logger.error("拼多多支付代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
    
    /**
     * @描述:拼多多代付订单查询接口
     * @时间:2018/6/15 11:22
     */
    @SuppressWarnings("all")
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            
            String queryUrl = "http://pay.niubizf.com/settlement/dftradeQuery";
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("p1_MerId", merchantCode);
    		reqPrms.put("orderid", reqData.getString("vcOrderNo"));
    		String pd_FrpId = "alipay_df";
    		reqPrms.put("pd_FrpId", pd_FrpId);
    		
    		StringBuffer sb=new StringBuffer();
        	sb.append(merchantCode);
        	sb.append(reqData.getString("vcOrderNo"));
            System.out.println(sb.toString());
            String hmac = DigestUtil.hmacSign(sb.toString(), md5Key); //数据签名
    		reqPrms.put("hmac", hmac);
    		
    		logger.info("拼多多代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("拼多多代付订单查询接口响应{}",response);
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
            if(jsonData.containsKey("status") && "S".equals(jsonData.getString("status"))){
            	 result.put("code", Constant.SUCCESSS);
                 result.put("msg", "代付成功");
                 result.put("status", 1);
                 return listener.successHandler(result);
            }else if("F".equals(jsonData.getString("status"))){
                result.put("status", 3);
                result.put("code", Constant.FAILED);
                result.put("msg",jsonData.get("info")==null?"代付失败":jsonData.getString("info"));
                return listener.failedHandler(result);
            }
            if(jsonData.containsKey("info")){
            	result.put("msg",jsonData.get("info"));
            }else{
            	result.put("msg", "处理中");
            }
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            return listener.paddingHandler(result);
        }catch (Exception e){
            logger.error("拼多多代付查询接口异常",e);
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
            String queryUrl = "http://pay.niubizf.com/settlement/dftradeQuery";
            JSONObject reqPrms = new JSONObject();
    		reqPrms.put("p1_MerId", merchantCode);
    		reqPrms.put("orderid", orderNo);
    		String pd_FrpId = "alipay_df";
    		reqPrms.put("pd_FrpId", pd_FrpId);
    		
    		StringBuffer sb=new StringBuffer();
        	sb.append(merchantCode);
        	sb.append(orderNo);
            System.out.println(sb.toString());
            String hmac = DigestUtil.hmacSign(sb.toString(), md5Key); //数据签名
    		reqPrms.put("hmac", hmac);
    		logger.info("拼多多代付订单查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(queryUrl, reqPrms);
    		logger.info("拼多多代付订单查询接口响应{}",response);
            if(StringUtils.isEmpty(response)){
                return false;
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if(jsonData == null){
                return false;
            }
            if(jsonData.containsKey("status") && "S".equals(jsonData.getString("status"))){
            	 return true;
            }else{
            	return false;
            }
        }catch (Exception e){
            logger.error("拼多多代付查询接口异常",e);
            return false;
        }
    }
    
    /**
     * @描述:拼多多余额查询
     * @时间:2018/6/15 11:23
     */
    public JSONObject walletQuery(JSONObject reqData,ResultListener listener){
        JSONObject result = new JSONObject();
        result.put("code", Constant.FAILED);
        try {
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
        	
			/*
			 * String merchantCode = "30067"; String md5Key =
			 * "TZRBcmFv71MVpxyjzQ0wDuy5uHJ0fuMy";
			 */
        	
            String url = "http://pay.niubizf.com/settlement/balanceSelect";
            JSONObject reqPrms = new JSONObject();
            String dateTime = Constant.ymdhms.format(new Date());
    		reqPrms.put("p1_MerId", merchantCode);
            reqPrms.put("datetime", dateTime);
            StringBuffer sb=new StringBuffer();
        	sb.append(merchantCode);
        	sb.append(dateTime);
            System.out.println(sb.toString());
            String hmac = DigestUtil.hmacSign(sb.toString(), md5Key); //数据签名
    		reqPrms.put("hmac", hmac);
    		
    		logger.info("拼多多余额查询接口入参{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(url, reqPrms);
    		logger.info("拼多多余额查询接口响应{}",response);
    		
            if(StringUtils.isEmpty(response)){
                result.put("balance", "0");
                result.put("code", Constant.FAILED);
                result.put("msg", "查询超时");
                return listener.failedHandler(result);
            }
            //拼多多余额查询接口响应{"balance":"0.000", "info":"SUCCESS","status":"y"}
            JSONObject jsonData = JSONObject.parseObject(response);
            if("y".equals(jsonData.getString("status"))){
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
            logger.error("拼多多余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }
}
