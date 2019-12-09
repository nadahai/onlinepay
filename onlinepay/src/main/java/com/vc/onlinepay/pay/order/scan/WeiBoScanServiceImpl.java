package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Component
public class WeiBoScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(WeiBoScanServiceImpl.class);
    private static Map<Integer,Integer> wxScanAmount  = new HashMap<Integer,Integer> ();
    
    /**
     * @描述:微博支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("微博支付交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String parter = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String callbackurl = reqData.getString("projectDomainUrl")+"/weiBoCallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String amount = reqData.getString("amount");
            
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
            
            BigDecimal ba = new BigDecimal(amount);
			 int amountInt = ba.intValue();
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amountInt+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }


            String type = "1006";
            String value = amount;
            String orderid = reqData.getString("vcOrderNo");

            String device = "wap";


            JSONObject prams = new JSONObject();
            prams.put("parter",parter);
            prams.put("type",type);
            prams.put("value",value);
            prams.put("orderid",orderid);
            prams.put("callbackurl",callbackurl);
            prams.put("device",device);

            String singStr = "parter="+parter+"&type="+type+"&value="+value+"&orderid="+orderid+"&callbackurl="+callbackurl+key;
            String sign = Md5Util.md5(singStr);
            prams.put("sign",sign);

            logger.info("微博支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("微博支付接口返参{}",response);
            result.put("actionUrl", API_PAY_URL);
            result.put("code", Constant.SUCCESSS);
            result.put("viewPath","auto/autoSubmit");
            result.put("data",prams);
            result.put("msg", "下单成功");
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error("微博支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("下单异常"));
        }
    }

    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://pay.zgpay888.com/Pay/GateWay";
            String key = "8d8fa6ae375e4fee83ac39663969492d";

            String parter = "1664";
            String type = "1006";
            String value = "100";
            String orderid = System.currentTimeMillis () + "";
            String callbackurl = "http://www.baidu.com";
            String device = "wap";


            JSONObject prams = new JSONObject();
            prams.put("parter",parter);
            prams.put("type",type);
            prams.put("value",value);
            prams.put("orderid",orderid);
            prams.put("callbackurl",callbackurl);
            prams.put("device",device);

            String singStr = "parter="+parter+"&type="+type+"&value="+value+"&orderid="+orderid+"&callbackurl="+callbackurl+key;
            String sign = Md5Util.md5(singStr);
            prams.put("sign",sign);
            logger.info("微博支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("租用支付接口返参{}",response);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
