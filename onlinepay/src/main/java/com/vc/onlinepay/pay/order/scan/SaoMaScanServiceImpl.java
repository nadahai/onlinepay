package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import java.math.BigDecimal;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class SaoMaScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(SaoMaScanServiceImpl.class);
    
    /**
     * @描述:扫码通道支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("扫码通道支付交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String uid = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String notifyUrl = reqData.getString("projectDomainUrl")+"/saoMaCallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String tranAmt = reqData.getString("amount");
            
            BigDecimal ba = new BigDecimal(tranAmt);
			int amount = ba.intValue();
			
            
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amount+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }
	            
	            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
		         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
			     //payType 1:微信 2:支付宝
		         String channelType = "wechat";
		         
		         if (type == 2 || type == 10 || type==22 || Constant.service_alipay.equals (service)) {
		        	 channelType = "alipay";
		         }
	            
		            String goodsName = "abc";
		            String outUserId = "localhost";
		            String timestamp = System.currentTimeMillis ()+"";
		            
		            
		            JSONObject prams = new JSONObject();
		            prams.put("uid",uid);
		            prams.put("money",tranAmt);
		            prams.put("channelType",channelType);
		            //prams.put("channel",channel);
		            prams.put("notifyUrl",notifyUrl);
		            prams.put("returnUrl",returnUrl);
		            prams.put("outTradeNo",reqData.getString("vcOrderNo"));
		            prams.put("goodsName",goodsName);
		            prams.put("outUserId",outUserId);
		            prams.put("timestamp",timestamp);
		            prams.put("token",key);
		            String sourctxt1 = Md5CoreUtil.getSignStr(prams);
			        logger.info("排序后{}",sourctxt1);
			        String sign = Md5Util.md5(sourctxt1).toUpperCase();
		            prams.put("sign",sign);
		            logger.info("扫码通道支付接口入参{}",prams);
		            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("扫码通道支付接口返参{}",response);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.FAILED);
                result.put("msg", "下单失败");
                return listener.failedHandler(result);
            }
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () ){
                return Constant.failedMsg ("获取连接为空");
            }
            if(payParams.getJSONObject("data") == null || !payParams.getJSONObject("data").containsKey ("payUrl")){
                String msg = payParams.containsKey ("msg")?payParams.getString ("msg"):"下单失败";
                return listener.failedHandler (Constant.failedMsg (msg));
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString ("payUrl")));
            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString ("payUrl")));
            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString ("payUrl")));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("扫码通道支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("下单异常"));
        }
    }

    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://pp.acshua.cn/api/v1/charges";
            String key = "e6dfb1ba6e6f4bd097a2b16b4c98d176";
            
            String uid = "399649827803103232";
            String money = "10";
            String channelType = "alipay";
            String outTradeNo = System.currentTimeMillis ()+"";
            String notifyUrl = "http://www.baidu.com";
            String returnUrl = "http://www.baidu.com";
            String goodsName = "abc";
            String outUserId = "localhost";
            String timestamp = System.currentTimeMillis ()+"";
            
            
            JSONObject prams = new JSONObject();
            prams.put("uid",uid);
            prams.put("money",money);
            prams.put("channelType",channelType);
            //prams.put("channel",channel);
            prams.put("notifyUrl",notifyUrl);
            prams.put("returnUrl",returnUrl);
            prams.put("outTradeNo",outTradeNo);
            prams.put("goodsName",goodsName);
            prams.put("outUserId",outUserId);
            prams.put("timestamp",timestamp);
            prams.put("token",key);
            String sourctxt1 = Md5CoreUtil.getSignStr(prams);
	        logger.info("排序后{}",sourctxt1);
	        String sign = Md5Util.md5(sourctxt1).toUpperCase();
            prams.put("sign",sign);
            logger.info("扫码通道支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("扫码支付支付接口返参{}",response);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    
}
