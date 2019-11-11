package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Component
public class PingAnYunScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (PingAnYunScanServiceImpl.class);
    private static Map<Integer,Integer> wxScanAmount  = new HashMap<Integer,Integer> ();
    static { 
		 wxScanAmount.put (50,50); 
		 wxScanAmount.put (100,100);
		 wxScanAmount.put (200,200); 
		 wxScanAmount.put (300,300); 
		 wxScanAmount.put (500,500); 
		 wxScanAmount.put (800,800); 
		 wxScanAmount.put (1000,1000);
		 wxScanAmount.put (2000,2000); 
		 wxScanAmount.put (3000,3000); 
		 wxScanAmount.put (5000,5000); 
		 wxScanAmount.put (8000,8000); 
		 wxScanAmount.put (10000,10000);
		 wxScanAmount.put (20000,20000); 
		 wxScanAmount.put (30000,30000); }


    /**
     * @描述:平安云支付
     * 
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("平安云闪付接收入参{}", reqData);
            String uid=StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String price= reqData.getString ("amount");
            
            if(!Constant.isNumeric (price)){
                return listener.failedHandler (Constant.failedMsg ("仅支持整数金额"));
            }
            String paytype = "4";//1支付宝，4微信
            String notify_url = reqData.getString ("projectDomainUrl") + "/pingAnYunPayCallBackApi";
            String return_url = reqData.getString("projectDomainUrl")+"/success";
            String user_order_no = reqData.getString ("vcOrderNo");
            String note = reqData.getString ("vcOrderNo");
            String cuid = reqData.getString ("vcOrderNo");
            //请求时间yyyy-mm-dd hh:mi:ss
            String tm = Constant.yyyyMMdd.format(new Date ());
            String token = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String singVal = uid + price + paytype + notify_url + return_url + user_order_no + token;
            String sign = DigestUtils.md5DigestAsHex(singVal.getBytes()).toUpperCase();
            		
           
            JSONObject prams = new JSONObject();
            prams.put("uid",uid);
            prams.put("price",price);
            prams.put("paytype",paytype);
            prams.put("notify_url",notify_url);
            prams.put("return_url",return_url);
            prams.put("user_order_no",user_order_no);
            prams.put("note",note);
            prams.put("cuid",cuid);
            prams.put("tm",tm);
            prams.put("sign",sign);
            logger.info("支付接口签名串{}",singVal);
            logger.info("支付接口入参{}",prams);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String response = HttpClientTools.baseHttpSendPost(API_PAY_URL,prams);
            logger.info("支付接口返参{}",response);
            if(StringUtils.isEmpty (response)){
                return listener.failedHandler (Constant.failedMsg ("下单为空"));
            }
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () || !payParams.containsKey ("QRCodeLink")){
                return listener.failedHandler (Constant.failedMsg ("获取连接为空"));
            }
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "获取链接成功");
            result.put ("bankUrl",payParams.getString ("QRCodeLink"));
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("平安云闪付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String url = "http://www.pinganyunzf.com:8848/qrpay/pay/json";
            String uid = "19171298qZ";
            String token = "5db0bc6f34bc4cfca62bcf941db1ac86";
            
            String price= "300";
			
            String paytype = "4";//1支付宝，4微信
            String notify_url = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String return_url = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String user_order_no = (System.currentTimeMillis () + "").substring (0,13);
            String note = user_order_no;
            String cuid = user_order_no;
            //请求时间yyyy-mm-dd hh:mi:ss
            String tm = Constant.yyyyMMdd.format(new Date ());
            String singVal = uid + price + paytype + notify_url + return_url + user_order_no + token;
            String sign = DigestUtils.md5DigestAsHex(singVal.getBytes()).toUpperCase();
            		
           
            JSONObject prams = new JSONObject();
            prams.put("uid",uid);
            prams.put("price",price);
            prams.put("paytype",paytype);
            prams.put("notify_url",notify_url);
            prams.put("return_url",return_url);
            prams.put("user_order_no",user_order_no);
            prams.put("note",note);
            prams.put("cuid",cuid);
            prams.put("tm",tm);
            prams.put("sign",sign);
            
            logger.info("支付接口入参{}",prams);
            String response = HttpClientTools.baseHttpSendPost(url,prams);
            logger.info("支付接口返参{}",response);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
