package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
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
public class WxSXBScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(WxSXBScanServiceImpl.class);
    
    /**
     * @描述:微信扫码随心宝支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("微信扫码随心宝支付交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String mch_id = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String notify_url = reqData.getString("projectDomainUrl")+"/wxSXBCallBackController";
            String return_url = reqData.getString("projectDomainUrl")+"/success";
            String tranAmt = reqData.getString("amount");


            BigDecimal ba = new BigDecimal(tranAmt);
			int amount = ba.intValue();

            
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amount+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }

            String service = "pay";
            String paytype = "4";
            String out_trade_no = reqData.getString("vcOrderNo");
            String total_fee =tranAmt;
            String time = new Date().getTime()+"";

            JSONObject prams = new JSONObject();
            prams.put("service",service);
            prams.put("mch_id",mch_id);
            prams.put("paytype",paytype);
            prams.put("out_trade_no",out_trade_no);
            prams.put("total_fee",total_fee);
            prams.put("time",time);
            prams.put("notify_url",notify_url);
            prams.put("return_url",return_url);
            String sign = Md5CoreUtil.md5ascii(prams, key).toLowerCase();
            prams.put("sign",sign);
            logger.info("微信扫码随心宝支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("话费支付接口返参{}",response);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.FAILED);
                result.put("msg", "下单失败");
                return listener.failedHandler(result);
            }
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () ){
                return Constant.failedMsg ("获取连接为空");
            }
            if(payParams.getJSONObject("data") == null ||  !payParams.getJSONObject("data").containsKey ("pay_img")){
                String msg = payParams.containsKey ("statusMsg")?payParams.getString ("statusMsg"):"下单失败";
                return listener.failedHandler (Constant.failedMsg (msg));
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            //result.put("bankUrl",payParams.getString ("bankUrl"));
            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString ("pay_img")));
            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString ("pay_img")));
            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString ("pay_img")));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("微信扫码随心宝支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("下单异常"));
        }
    }

    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://www.taoxiangmai.com/pay/spay/";
            String key = "fee792af28d5f26be496c3eab9bab5b0";

            String service = "pay";
            String mch_id = "966944";
            String paytype = "4";
            String out_trade_no = System.currentTimeMillis ()+"";
            String total_fee = "100";
            String time = new Date().getTime()+"";
            String notify_url = "http://www.baidu.com";
            String return_url = "http://www.baidu.com";

            JSONObject prams = new JSONObject();
            prams.put("service",service);
            prams.put("mch_id",mch_id);
            prams.put("paytype",paytype);
            prams.put("out_trade_no",out_trade_no);
            prams.put("total_fee",total_fee);
            prams.put("time",time);
            prams.put("notify_url",notify_url);
            prams.put("return_url",return_url);
            String sign = Md5CoreUtil.md5ascii(prams, key);
            prams.put("sign",sign);
            logger.info("微信扫码随心宝支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("话费支付接口返参{}",response);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
    
    
}
