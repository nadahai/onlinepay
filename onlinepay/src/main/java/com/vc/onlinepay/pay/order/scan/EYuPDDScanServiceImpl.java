package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class EYuPDDScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(EYuPDDScanServiceImpl.class);
    
    /**
     * @描述:鳄鱼PDD通道支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("鳄鱼PDD通道支付交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String merchNo = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String notifyUrl = reqData.getString("projectDomainUrl")+"/eYuCallBackController";
            String amount = reqData.getString("amount");
            
            
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
            
            BigDecimal ba = new BigDecimal(amount);
			 int amountInt = ba.intValue();
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amountInt+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }
            
           
            
            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
	         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
		     //payType 1:微信 2:支付宝
	         String payType = "12";
	         
	         if (type == 2 || type == 10 || type==22 || Constant.service_alipay.equals (service)) {
	        	 payType = "10";
	         }
	         JSONObject prams = new JSONObject();
	            String command = "cmd102";
	            String serverCode = "ser2001";
	            String version = "3.0";
	            String charset = "utf-8";
	            String currency = "CNY";
	            String reqIp = "127.0.0.1";
	            String reqTime = DateUtils.getDate("yyyyMMddHHmmss");
	            String signType = "MD5";
	            String cOrderNo = reqData.getString("vcOrderNo");
	            prams.put("command",command);
	            prams.put("serverCode",serverCode);
	            prams.put("merchNo",merchNo);
	            prams.put("version",version);
	            prams.put("charset",charset);
	            prams.put("currency",currency);
	            prams.put("reqIp",reqIp);
	            prams.put("reqTime",reqTime);
	            prams.put("signType",signType);
	            prams.put("payType",payType);
	            prams.put("cOrderNo",cOrderNo);
	            prams.put("amount",amount);
	            prams.put("notifyUrl",notifyUrl);
	            
            String sign = Md5CoreUtil.md5ascii(prams, key);
            prams.put("sign",sign);

            logger.info("鳄鱼PDD通道支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("鳄鱼PDD通道支付接口返参{}",response);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.FAILED);
                result.put("msg", "下单失败");
                return listener.failedHandler(result);
            }
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () ){
                return Constant.failedMsg ("获取连接为空");
            }
            if(!payParams.containsKey ("payUrl")){
                String msg = payParams.containsKey ("message")?payParams.getString ("message"):"下单失败";
                return listener.failedHandler (Constant.failedMsg (msg));
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getString ("payUrl")));
            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getString ("payUrl")));
            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getString ("payUrl")));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("鳄鱼PDD通道支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("下单异常"));
        }
    }

    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://47.75.156.125:8080/onlinepay/gateway/eypayapi";
            JSONObject prams = new JSONObject();
            String command = "cmd102";
            String serverCode = "ser2001";
            String merchNo = "999941001154";
            String version = "3.0";
            String charset = "utf-8";
            String currency = "CNY";
            String reqIp = "127.0.0.1";
            String reqTime = DateUtils.getDate("yyyyMMddHHmmss");
            String signType = "MD5";
            String payType = "10";
            String cOrderNo = System.currentTimeMillis ()+"";
            String amount = "500";
            String notifyUrl = "http://www.baidu.com";
            prams.put("command",command);
            prams.put("serverCode",serverCode);
            prams.put("merchNo",merchNo);
            prams.put("version",version);
            prams.put("charset",charset);
            prams.put("currency",currency);
            prams.put("reqIp",reqIp);
            prams.put("reqTime",reqTime);
            prams.put("signType",signType);
            prams.put("payType",payType);
            prams.put("cOrderNo",cOrderNo);
            prams.put("amount",amount);
            prams.put("notifyUrl",notifyUrl);
            
            
            String sign = Md5CoreUtil.md5ascii(prams, "93335B744E57B5DEF921DF521A347DA2");
            prams.put("sign",sign);
            logger.info("鳄鱼PDD通道支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("鳄鱼PDD支付接口返参{}",response);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
