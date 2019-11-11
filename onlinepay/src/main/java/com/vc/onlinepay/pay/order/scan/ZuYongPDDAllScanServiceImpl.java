package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;

import cn.hutool.http.HttpRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class ZuYongPDDAllScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(ZuYongPDDAllScanServiceImpl.class);
    private static Map<Integer,Integer> wxScanAmount  = new HashMap<Integer,Integer> ();
    
    /**
     * @描述:租用PDD码商匹配通道支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("租用PDD码商匹配通道支付交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            //String merchNo = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            //String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            
            String merchNo = StringUtils.deleteWhitespace(reqData.getString("levelNo"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("levelViewNo"));
            
            String backUrl = reqData.getString("projectDomainUrl")+"/zuYongCallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String amount = reqData.getString("amount");
            
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
            
            BigDecimal ba = new BigDecimal(amount);
			 int amountInt = ba.intValue();
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amountInt+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }
            
            JSONObject prams = new JSONObject();
            prams.put("reqCmd","req.trade.order");
            prams.put("merchNo",merchNo);
            prams.put("charset","UTF-8");
            prams.put("signType","MD5");
            prams.put("reqIp","47.25.125.14");
            
            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
	         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
		     //payType 1:微信 2:支付宝
	         String payType = "12";
	         
	         if (type == 2 || type == 10 || type==22 || Constant.service_alipay.equals (service)) {
	        	 payType = "10";
	         }
            prams.put("payType",payType);
            prams.put("tradeNo",reqData.getString("vcOrderNo"));
            prams.put("currency","CNY");
            prams.put("amount",amount);
            prams.put("userId",reqData.getString("vcOrderNo"));
            prams.put("notifyUrl",backUrl);
            prams.put("returnUrl",returnUrl);
            prams.put("goodsName","好东西一起分享");
            prams.put("goodsDesc","好东西一起分享");
            String sign = Md5CoreUtil.md5ascii(prams, key);
            prams.put("sign",sign);

            logger.info("租用PDD码商匹配通道支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("租用PDD码商匹配通道支付接口返参{}",response);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.FAILED);
                result.put("msg", "下单失败");
                return listener.failedHandler(result);
            }
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () ){
                return Constant.failedMsg ("获取连接为空");
            }
            if(!payParams.containsKey ("bankUrl")){
                String msg = payParams.containsKey ("msg")?payParams.getString ("msg"):"下单失败";
                return listener.failedHandler (Constant.failedMsg (msg));
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            //result.put("bankUrl",payParams.getString ("bankUrl"));
            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getString ("bankUrl")));
            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getString ("bankUrl")));
            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getString ("bankUrl")));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("租用PDD码商匹配通道支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("下单异常"));
        }
    }

    public static void main (String[] args) {
    	
        try {
        	
            String API_PAY_URL = "http://boss.hawkeyepay.cn/hipay/openapi";
            JSONObject prams = new JSONObject();
            prams.put("reqCmd","req.trade.order");
            prams.put("merchNo","444441000003");
            prams.put("charset","UTF-8");
            prams.put("signType","MD5");
            prams.put("reqIp","47.25.125.14");
            prams.put("payType","12");
            prams.put("tradeNo",System.currentTimeMillis ());
            prams.put("currency","CNY");
            prams.put("amount","200");
            prams.put("userId",System.currentTimeMillis ());
            prams.put("notifyUrl","http://www.baidu.com");
            prams.put("returnUrl","http://www.baidu.com");
            prams.put("goodsName","深圳盛源网络科技有限公司");
            prams.put("goodsDesc","深圳盛源网络科技有限公司");
            
            
            String sign = Md5CoreUtil.md5ascii(prams, "D42F09977BC3AE310C99C2D866D51444");
            prams.put("sign",sign);
            logger.info("租用PDD码商匹配通道支付接口入参{}",prams);
            //"charset":"utf-8","amount":18.66,"sign":"9a2be0a8eb571bfbbe2c6afde4d856cf","reqTime":"20190409173045","version":"2.0","command":"cmd101","serverCode":"ser2001","reqIp":"47.25.125.14","payType":"8","merchNo":"999941001031","cOrderNo":"8_040917304522222","signType":"MD5","notifyUrl":"http://online.toxpay.com/xpay/gaoYangPayCallBackApi","currency":"CNY","goodsName":"深圳盛源网络科技有限公司","goodsNum":1,"goodsDesc":"深圳盛源网络科技有限公司"}
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("租用PDD码商匹配支付接口返参{}",response);
            
            
            
        } catch (IOException e) {
            e.printStackTrace ();
        }
        
    	/**
    	String accessToken = "JKAB4H7G2T5ERWZL2SSJM6OQXVWVABPB7TUQ6LVCUPCM5Z44VJSQ112b349";
    	
    	String url = "https://apiv2.pinduoduo.net/api/galilei/refresh/token";
        logger.info("PDD校验登录信息：{}，地址：{}",accessToken,url);
        String result = HttpRequest.post(url).header("accesstoken",accessToken).execute().body();
        JSONObject jsonObject = JSON.parseObject(result);
        logger.info("PDD校验登录信息：{}",jsonObject);
        **/
    }
}
