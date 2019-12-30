package com.vc.onlinepay.pay.order.scan;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.sand.MD5;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

@Service
@Component
public class HuaFeiNewScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(HuaFeiNewScanServiceImpl.class);
    
    /**
     * @描述:新话费通道支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("新话费通道支付交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String uid = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String callback = reqData.getString("projectDomainUrl")+"/huaFeiNewCallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String money = reqData.getString("amount");
            
            BigDecimal ba = new BigDecimal(money);
			int amount = ba.intValue();

            
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amount+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }


            String order = reqData.getString("vcOrderNo");
            JSONObject prams = new JSONObject();
            prams.put("uid",uid);
            prams.put("order",order);
            prams.put("money",money);

            String signStr = uid+money+key;
            logger.info("新话费通道加密前{}",signStr);
            String sign = MD5.md5(signStr).toLowerCase();
            prams.put("sign",sign);
            logger.info("新话费通道支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);

            logger.info("新话费通道支付接口返参{}",response);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.FAILED);
                result.put("msg", "下单失败");
                return listener.failedHandler(result);
            }
            cn.hutool.json.JSONObject payParams = XML.toJSONObject(response);
            logger.info("话费支付接口返参{}",payParams.getJSONObject("items").getJSONObject("pro").getStr("payurl"));
            if(payParams == null || payParams.isEmpty () ){
                return Constant.failedMsg ("获取连接为空");
            }
            if(!payParams.getJSONObject("items").getJSONObject("pro").containsKey ("payurl")){
                String msg = "下单失败";
                return listener.failedHandler (Constant.failedMsg (msg));
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("items").getJSONObject("pro").getStr("payurl")));
            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("items").getJSONObject("pro").getStr("payurl")));
            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("items").getJSONObject("pro").getStr("payurl")));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("新话费通道支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("下单异常"));
        }
    }

    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://bl.ipv.so/interface/getorder.aspx";
            String key = "5TNr3fggHsigy5ji";
            
            String agentOrderId = System.currentTimeMillis ()+"";
            String uid = "sifang-13";
            String money = "30";
            JSONObject prams = new JSONObject();
            prams.put("uid",uid);
            prams.put("order",agentOrderId);
            prams.put("money",money);

            String signStr = uid+money+key;
            logger.info("新话费通道加密前{}",signStr);
            String sign = MD5.md5(signStr).toLowerCase();
            prams.put("sign",sign);
            logger.info("新话费通道支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            cn.hutool.json.JSONObject payParams = XML.toJSONObject(response);
            logger.info("话费支付接口返参{}",response);
            //String vcOrderNo = payParams.getJSONObject("items").getJSONArray("pro");
            logger.info("话费支付接口返参{}",payParams.getJSONObject("items").getJSONObject("pro").getStr("payurl"));

        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    
}
