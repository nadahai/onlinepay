package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.HtmlCompressor;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import com.vc.onlinepay.utils.sand.MD5;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Component
public class XunJie2ScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (XunJie2ScanServiceImpl.class);
    
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;
    
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
     * @描述:迅捷拼多多
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("迅捷云闪付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String merchNo = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String backUrl = reqData.getString ("projectDomainUrl") + "/xunJie2CallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String amount2 = reqData.getString ("amount");
			/*
			 * if(!Constant.isNumeric (amount2)){ return listener.failedHandler
			 * (Constant.failedMsg ("仅支持整数金额")); }
			 * 
			 * int amount = Integer.valueOf (amount2); amount2 = amount*100+"";
			 */
			/*
			 * if(!wxScanAmount.containsKey (amount) || wxScanAmount.get (amount) <1){
			 * return listener.failedHandler(Constant.failedMsg ("仅支持固定金额")); }
			 */
			 
            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
	         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
		     //payType 1:微信 2:支付宝
	         String paytype = "14";
	         if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
	        	 paytype = "13";
	         }
            
            JSONObject prams = new JSONObject ();
            prams.put ("uid", merchNo);
            prams.put ("price", amount2);
            prams.put ("paytype",paytype);
            prams.put ("notify_url", backUrl);
            prams.put ("return_url", returnUrl);
            prams.put ("user_order_no",orderNo);
            prams.put ("note",orderNo);
            prams.put ("cuid", orderNo);
            prams.put ("tm",Constant.yyyyMMdd.format(new Date ()));
            String sourctxt1 = merchNo + amount2 + paytype + backUrl + returnUrl + orderNo+ key;
            String sign = DigestUtils.md5DigestAsHex(sourctxt1.getBytes()).toUpperCase();
            prams.put ("sign",sign);
            logger.info("支付接口入参{}",prams);
            //String response = HttpClientTools.baseHttpSendPost(API_PAY_URL,prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL, prams);
            logger.info("支付接口返参{}",response);
            if(StringUtils.isEmpty (response)){
                return listener.failedHandler (Constant.failedMsg ("下单为空"));
            }
            response = HtmlCompressor.compress(response);
           /* JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () 
            		|| !"0".equals(payParams.getString("code")) 
            		|| !payParams.getJSONObject("result").containsKey ("QRCodeLink")){
                return listener.failedHandler (Constant.failedMsg ("获取连接为空"));
            }*/
            VcOnlineOrderMade made = new VcOnlineOrderMade();
	        made.setChannelId(reqData.getIntValue("channelLabel"));
            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
	        made.setMerchNo(reqData.getString("merchantNo"));
	        made.setOrderNo(orderNo);
            made.setOpenType (98);
            made.setRemarks (reqData.getString("channelKey"));
	        made.setPaySource(reqData.getIntValue("channelSource"));
	        made.setTraAmount(new BigDecimal (amount2));
	        made.setUpMerchKey(key);
	        made.setUpMerchNo(reqData.getString("merchantNo"));
	        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(orderNo));
	        //String val = payParams.getJSONObject("result").getString("QRCodeLink");
            //String base64Str = Base64.getEncoder().encodeToString(response.getBytes());
	        made.setQrcodeUrl(response);
	        JSONObject response2 = vcOnlineOrderMadeService.getOrderMadePayUrl(made);
	        logger.info("扫码支付响应{}",response2);
			if(response2 == null || response.isEmpty()){
                return listener.failedHandler(Constant.failedMsg ("扫码支付超时"));
            }
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "获取链接成功");
            result.put ("bankUrl",StringEscapeUtils.unescapeJava(response2.getString("openUrl")));
            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(response2.getString("openUrl")));
            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(response2.getString("openUrl")));
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("迅捷拼多多下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String url = "http://manage.xtiane.top:8848/qrpay/pay";
            String app_id = "1924414Z7d";
            String key = "5bf768203a324705bbfce09e1ae294ec";

            String price = "1";
            String user_order_no = (System.currentTimeMillis () + "").substring (0,13);
            String notify_url = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String return_url = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            JSONObject parms = new JSONObject();
            parms.put ("uid", app_id);
            parms.put ("third_order_id", user_order_no);
            parms.put ("price",price);
            parms.put ("nonce_str", user_order_no);
            parms.put ("notify_url", notify_url);
            parms.put ("return_url",return_url);
            parms.put ("order_type","8");
            parms.put ("paytype",14);
            parms.put ("cuid", user_order_no);


            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+ "&key="+key;
            System.out.println("排序后"+sourctxt1);
            String sign = MD5.md5(sourctxt1).toUpperCase();
            parms.put ("sign",sign);
            String par = "?app_id="+app_id
            		+ "&third_order_id="+user_order_no
            		+ "&price="+price
            		+ "&nonce_str="+user_order_no
            		+ "&notify_url="+notify_url
            		+ "&return_url="+return_url
            		+ "&order_type=8";
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(url, parms);
            //String response = HttpClientTools.sendUrlGet(url, par);
            //String response = "{'code':0,'result':{'qr_url':'http://pay.1000pays.com/Pay/api_wx_jsapis?api_data=M1T3M224M7T0gxMAO0O0OO0O0O'},'msg':'成功'}";
            logger.info("支付接口返参{}",response);
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () || !"0".equals(payParams.getString("code")) 
            		|| !payParams.getJSONObject("result").containsKey ("qr_url")){
                System.out.println("response==="+response);
            }
            System.out.println(payParams.getJSONObject("result").getString("qr_url"));
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
