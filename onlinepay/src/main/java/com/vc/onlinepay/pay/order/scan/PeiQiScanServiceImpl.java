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
public class PeiQiScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (PeiQiScanServiceImpl.class);
    

    /**
     * @描述:佩奇支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("佩奇支付支付宝接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String merchantId = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String notifyUrl = reqData.getString ("projectDomainUrl") + "/peiQiCallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            BigDecimal amount = new BigDecimal (reqData.getString ("amount")).multiply (new BigDecimal ("100")).setScale (0, BigDecimal.ROUND_HALF_DOWN);
            String mOrderId = orderNo;
            String body = mOrderId;
            String payType = "alipay_rec";
            String signType="MD5";
            String version = "1.0";
            
            
            JSONObject parms = new JSONObject();
            parms.put ("amount",amount);
            parms.put ("mOrderId",mOrderId);
            parms.put ("body",body);
            parms.put ("merchantId", merchantId);
            parms.put ("notifyUrl", notifyUrl);
            parms.put ("payType",payType);
            parms.put ("signType",signType);
            parms.put ("version",version);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("returnUrl",returnUrl);
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);
	            if(StringUtils.isEmpty (response)){
	                return listener.failedHandler (Constant.failedMsg ("下单为空"));
	            }
	            JSONObject payParams = Constant.stringToJson (response);
	            
	            if(payParams == null || payParams.isEmpty () || !"0".equals(payParams.getString("code"))){
	                return listener.failedHandler (Constant.failedMsg ("获取连接为空"));
	            }
				
	            result.put ("code", Constant.SUCCESSS);
	            result.put ("msg", "获取链接成功");
	            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString("url")));
	            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString("url")));
	            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString("url")));
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("佩奇支付支付宝下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://api.paych.top/api/unified/order";
            String key = "893a37ea08a445288379e6e6136b31c0";
            
            String amount="1000";
            String mOrderId = (System.currentTimeMillis () + "").substring (0,13);
            String body = mOrderId;
            String merchantId = "8201";
            String notifyUrl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String payType = "alipay_rec";
            String signType="MD5";
            String version = "1.0";
            
            String returnUrl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            
            JSONObject parms = new JSONObject();
            parms.put ("amount",amount);
            parms.put ("mOrderId",mOrderId);
            parms.put ("body",body);
            parms.put ("merchantId", merchantId);
            parms.put ("notifyUrl", notifyUrl);
            parms.put ("payType",payType);
            parms.put ("signType",signType);
            parms.put ("version",version);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("returnUrl",returnUrl);
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            JSONObject payParams = Constant.stringToJson (response);
            if("0".equals(payParams.getString("code"))) {
            	System.out.println(payParams.getJSONObject("data").getString("url"));
            }
            logger.info("支付接口返参{}",response);
            //{"code":"0","data":{"pOrderId":"2019080119220858882019480","mOrderId":"1564658526145","url":"http://mq.qimcwy.cn/checkstand/alipay/rec/entry?orderId=2019080119220858882019480"},"msg":"操作成功"}

           
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
