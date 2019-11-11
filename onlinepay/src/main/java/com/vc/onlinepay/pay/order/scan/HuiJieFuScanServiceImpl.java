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
public class HuiJieFuScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (HuiJieFuScanServiceImpl.class);
    
    
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
     * @描述:惠捷云支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("惠捷云支付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String mer_id = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String notify_url = reqData.getString ("projectDomainUrl") + "/huiJieYunCallBackController";
            String amount = reqData.getString ("amount");
            String bank_code = "0015";
            String order_id = orderNo;
            JSONObject parms = new JSONObject();
            parms.put ("return_format", "json");
            parms.put ("mer_id", mer_id);
            parms.put ("bank_code",bank_code);
            parms.put ("amount",amount);
            parms.put ("order_id", order_id);
            parms.put ("notify_url", notify_url);
            String signStr = "amount="+amount+"&bank_code="+bank_code+"&mer_id="+mer_id+"&notify_url="+notify_url+"&order_id="+order_id+"&key="+key;
            
            System.out.println("排序后"+signStr);
            String sign = MD5.md5(signStr).toLowerCase();
            parms.put ("sign",sign);
           
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL, parms);
            logger.info("支付接口返参{}",response);
            if(StringUtils.isEmpty (response)){
	            return listener.failedHandler (Constant.failedMsg ("下单为空"));
	        }
            
            JSONObject payParams = Constant.stringToJson (response);
            
            if(payParams == null || payParams.isEmpty () 
            		|| !payParams.containsKey ("pay_url") 
            		|| !"1".equals(payParams.getString("code"))){
                return listener.failedHandler (Constant.failedMsg ("获取连接为空"));
            }
            
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "获取链接成功");
            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getString("pay_url")));
            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getString("pay_url")));
            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getString("pay_url")));
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("惠捷云支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String url = "http://api.huijie9.com/payment/";
            String mer_id = "1596";
            String key = "cfaedf4da018b1bd43c8dec217776e2f";

            String amount = "100";
            String bank_code = "0013";
            String order_id = (System.currentTimeMillis () + "").substring (0,13);
            String notify_url = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            JSONObject parms = new JSONObject();
            parms.put ("return_format", "json");
            parms.put ("mer_id", mer_id);
            parms.put ("bank_code",bank_code);
            parms.put ("amount",amount);
            parms.put ("order_id", order_id);
            parms.put ("notify_url", notify_url);
           
            String signStr = "amount="+amount+"&bank_code="+bank_code+"&mer_id="+mer_id+"&notify_url="+notify_url+"&order_id="+order_id+"&key="+key;
            
            System.out.println("排序后"+signStr);
            String sign = MD5.md5(signStr).toLowerCase();
            parms.put ("sign",sign);
           
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(url, parms);
            logger.info("支付接口返参{}",response);
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () || !"0".equals(payParams.getString("code")) 
            		|| !payParams.getJSONObject("result").containsKey ("pay_url")){
                System.out.println("response==="+response);
            }
            System.out.println(payParams.getString("pay_url"));
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
