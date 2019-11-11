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
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.sand.MD5;


/**
 * @描述:闪付通代付
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component
public class ShanFuTongReplaceServiceImpl {
    
    private static Logger logger = LoggerFactory.getLogger(ShanFuTongReplaceServiceImpl.class);
  
    

    
    public static void main(String[] args) {
    	try {
    		String merchantCode = "19211307uh";
            String md5Key = "7682aae3fba842b0b09cfc8c160246ca";
        	
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
     * @描述:闪付通代付接口
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
            String bankSubName = reqData.containsKey("bankSubName")?reqData.getString("bankSubName"):"其它";
            String mobile = reqData.containsKey("mobile")?reqData.getString("mobile"):"其它";
            String idCardNo = reqData.containsKey("idCardNo")?reqData.getString("idCardNo"):"1";
            String bankLinked = reqData.containsKey("bankLinked")?reqData.getString("bankLinked"):"其它";
			
            String uid = merchantCode;
            String bankName = reqData.containsKey("bankName")?reqData.getString("bankName"):"其它";
            String bankCardNo = reqData.containsKey("bankCard")?reqData.getString("bankCard"):"其它";
            String cardholder = reqData.containsKey("accountName")?reqData.getString("accountName"):"其它";
            String amount = reqData.containsKey("province")?reqData.getString("province"):"其它";	
            
            String signStr = uid+ amount+bankName+bankCardNo+ cardholder+ md5Key;
            String sign = Md5Util.md5(signStr).toUpperCase();
            
    		JSONObject reqPrms = new JSONObject();
    		reqPrms.put("uid", uid);
    		reqPrms.put("bankName", bankName);
    		reqPrms.put("bankCardNo", bankCardNo);
    		reqPrms.put("cardholder", cardholder);
    		reqPrms.put("amount", amount);
    		reqPrms.put("sign", sign);
    		
    		String url = "http://pay.niubizf.com/settlement/dftradeQuery";
    		logger.info("闪付通代付接口入参:{}",reqPrms);
    		String response =  HttpClientTools.httpSendPostFrom(url, reqPrms);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            logger.info("闪付通代付接口返参:{}",response);
            if("success".equals(response)){
            	
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "代付申请已提交");
                return listener.paddingHandler(result);
            }else{
            	 result.put("status", 3);
                 result.put("code", Constant.FAILED);
                 result.put("msg",response==null?"代付失败":response);
                 return listener.failedHandler(result);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("闪付通代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "闪付通代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
    
}
