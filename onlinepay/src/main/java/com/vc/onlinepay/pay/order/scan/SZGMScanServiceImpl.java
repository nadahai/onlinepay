package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class SZGMScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (SZGMScanServiceImpl.class);

    /**
     * @描述:深圳个码支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("深圳个码支付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String AgentId = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String Thid = orderNo;
            String ParValue= reqData.getString ("amount");
            String ReturnURL = reqData.getString ("projectDomainUrl") + "/sZGMCallBackController";
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
            BigDecimal ba = new BigDecimal(ParValue);
			 int amountInt = ba.intValue();
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amountInt+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }
	            String Timestamp = new Date().getTime()+"";
	            String PayType = "1";
	            Integer type = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
	            String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
	            //PayType 1:微信 2:支付宝 
	            if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
	            	PayType = "7";
	            }
	            
	            JSONObject parms = new JSONObject();
	            parms.put ("AgentId",AgentId);
	            parms.put ("Timestamp",Timestamp);
	            parms.put ("PayType",PayType);
	            parms.put ("ParValue", ParValue);
	            parms.put ("ReturnURL",ReturnURL);
	            parms.put ("Thid",Thid);
	            
	            String sourctxt1 = AgentId+ParValue+PayType+ReturnURL+Thid+Timestamp+Md5Util.md5(key).toUpperCase();
	            logger.info("排序后{}",sourctxt1);
	            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
	            parms.put ("Sign",pay_md5sign);
	            logger.info("深圳个码支付接口入参{}",parms);
	            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
	            logger.info("深圳个码支付接口返参{}",response);
           if(StringUtils.isBlank(response)){
               result.put("code", Constant.FAILED);
               result.put("msg", "下单失败");
               return listener.failedHandler(result);
           }
           JSONObject payParams = Constant.stringToJson (response);
           if(payParams == null || payParams.isEmpty () ){
               return Constant.failedMsg ("获取连接为空");
           }
           if(!payParams.containsKey ("pay_url")){
               String msg = payParams.containsKey ("msg")?payParams.getString ("msg"):"下单失败";
               return listener.failedHandler (Constant.failedMsg (msg));
           }
           result.put("code", Constant.SUCCESSS);
           result.put("msg", "获取链接成功");
           result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getString ("pay_url")));
           result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getString ("pay_url")));
           result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getString ("pay_url")));
           return listener.successHandler(result);
        } catch (Exception e) {
            logger.error ("深圳个码支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://gpay.8bdh.cn/?Ac=SubmitPay";
            String key = "19F34BB8C0C168092D5DD6C12F1EFF6B";
            
            String AgentId = "16013";
            String Timestamp = new Date().getTime()+"";
            String PayType = "2";
            String ParValue="300";
            String ReturnURL ="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String Thid = (System.currentTimeMillis () + "").substring (0,13);
            
            
            JSONObject parms = new JSONObject();
            parms.put ("AgentId",AgentId);
            parms.put ("Timestamp",Timestamp);
            parms.put ("PayType",PayType);
            parms.put ("ParValue", ParValue);
            parms.put ("ReturnURL",ReturnURL);
            parms.put ("Thid",Thid);
            
            String sourctxt1 = AgentId+ParValue+PayType+ReturnURL+Thid+Timestamp+Md5Util.md5(key).toUpperCase();
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("Sign",pay_md5sign);
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
