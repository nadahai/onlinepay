package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class HuFeiScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(HuFeiScanServiceImpl.class);
    
    /**
     * @描述:话费通道支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("话费通道支付交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String agentAcct = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String callback = reqData.getString("projectDomainUrl")+"/huFeiCallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String tranAmt = reqData.getString("amount");
            
            BigDecimal ba = new BigDecimal(tranAmt);
			int amount = ba.intValue();
			tranAmt = amount*100+"";
            
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amount+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }
            
	            String agentOrderId = reqData.getString("vcOrderNo");
	            String bizType = "E_CHARGE";
	            
	            Random rm = new Random();
	         // 获得随机数  
	            double pross = (1 + rm.nextDouble()) * Math.pow(10, 11);  
	            // 将获得的获得随机数转化为字符串  
	            String fixLenthString = String.valueOf(pross);  
	            // 返回固定的长度的随机数  
	            String number = "187"+(fixLenthString.substring(2, 10)+"");
	            String timestamp = new Date().getTime()+"";
	            
	            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
		         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
			     //payType 1:微信 2:支付宝
		         String channel = "WXDIRECT";
		         
		         if (type == 2 || type == 10 || type==22 || Constant.service_alipay.equals (service)) {
		        	 channel = "ZFBDIRECT";
		         }
	            
	            
	            JSONObject prams = new JSONObject();
	            prams.put("agentAcct",agentAcct);
	            prams.put("agentOrderId",agentOrderId);
	            prams.put("bizType",bizType);
	            prams.put("number",number);
	            prams.put("amount",tranAmt);
	            prams.put("timestamp",timestamp);
	            prams.put("callback",callback);
	            prams.put("channel",channel);
	            String sign = Md5CoreUtil.md5ascii(prams, key);
	            prams.put("sign",sign);

            logger.info("话费通道支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("话费通道支付接口返参{}",response);
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
                String msg = payParams.containsKey ("statusMsg")?payParams.getString ("statusMsg"):"下单失败";
                return listener.failedHandler (Constant.failedMsg (msg));
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            //result.put("bankUrl",payParams.getString ("bankUrl"));
            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getString ("payUrl")));
            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getString ("payUrl")));
            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getString ("bankUrl")));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("话费通道支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("下单异常"));
        }
    }

    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://103.61.37.30:9088/api/recharge/h5/order.do";
            String key = "25d55ad283aa400af464c76d713c07ad";
            
            String agentAcct = "ttxin";
            String agentOrderId = System.currentTimeMillis ()+"";
            String bizType = "E_CHARGE";
            
            Random rm = new Random();
            // 获得随机数
            double pross = (1 + rm.nextDouble()) * Math.pow(10, 11);  
            // 将获得的获得随机数转化为字符串  
            String fixLenthString = String.valueOf(pross);  
            // 返回固定的长度的随机数  
            String number = "187"+(fixLenthString.substring(2, 10)+"");
            String amount = "1000";
            String timestamp = new Date().getTime()+"";
            String callback = "http://www.baidu.com";
            String channel = "WXDIRECT";
            
            JSONObject prams = new JSONObject();
            prams.put("agentAcct",agentAcct);
            prams.put("agentOrderId",agentOrderId);
            prams.put("bizType",bizType);
            prams.put("number",number);
            prams.put("amount",amount);
            prams.put("timestamp",timestamp);
            prams.put("callback",callback);
            prams.put("channel",channel);
            String sign = Md5CoreUtil.md5ascii(prams, key);
            prams.put("sign",sign);
            logger.info("话费通道支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("话费支付接口返参{}",response);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
    
    
}
