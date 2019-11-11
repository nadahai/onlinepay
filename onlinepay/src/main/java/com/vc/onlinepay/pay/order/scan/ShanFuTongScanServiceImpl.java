package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import cn.hutool.json.XML;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class ShanFuTongScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (ShanFuTongScanServiceImpl.class);
    
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;

    /**
     * @描述:闪付通
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("闪付通接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String spid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String tranAmt = reqData.getString ("amount");
            
            String uid = spid;
            String price = tranAmt;
            String paytype = "14";
            String notify_url=reqData.getString ("projectDomainUrl") + "/shanFuTongCallBackController";
            String return_url=reqData.getString("projectDomainUrl")+"/success";
            String user_order_no = orderNo;
            String note = user_order_no;
            
            String signStr = uid + price + paytype + notify_url + return_url + user_order_no + key;
            String sign = Md5Util.md5(signStr).toUpperCase();
	        
	        
	         String postUrl = API_PAY_URL+
	        		 "?uid="+uid+
	        		 "&price="+price+
	        		 "&paytype="+paytype+
	        		 "&notify_url="+notify_url+
	        		 "&return_url="+return_url+
	        		 "&user_order_no="+user_order_no+
	        		 "&note="+note+
	        		 "&sign="+sign;
	            
	         logger.info("排序后{}",signStr);
	         logger.info("支付接口入参{}",postUrl);
	         
	         VcOnlineOrderMade made = new VcOnlineOrderMade();
		        made.setChannelId(reqData.getIntValue("channelLabel"));
	            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
		        made.setMerchNo(reqData.getString("merchantNo"));
		        made.setOrderNo(orderNo);
	            made.setOpenType (102);
	            made.setRemarks (reqData.getString("channelKey"));
		        made.setPaySource(reqData.getIntValue("channelSource"));
		        made.setTraAmount(new BigDecimal (tranAmt));
		        made.setUpMerchKey(key);
		        made.setUpMerchNo(reqData.getString("merchantNo"));
		        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(orderNo));
		        String val = postUrl;
		        made.setQrcodeUrl(val);
		        JSONObject response2 = vcOnlineOrderMadeService.getOrderMadePayUrl(made);
		        logger.info("扫码支付响应{}",response2);
				if(response2 == null || response2.isEmpty()){
	                return listener.failedHandler(Constant.failedMsg ("扫码支付超时"));
	            }
				
	            result.put ("code", Constant.SUCCESSS);
	            result.put ("msg", "获取链接成功");
	            result.put ("bankUrl",StringEscapeUtils.unescapeJava(response2.getString("openUrl")));
	            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(response2.getString("openUrl")));
	            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(response2.getString("openUrl")));
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("闪付通下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("闪付通处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String url = "http://m.818x.cn:8848/qrpay/pay";
            String key = "7682aae3fba842b0b09cfc8c160246ca";
            
            String uid = "19211307uh";
            String price = "100";
            String paytype = "14";
            String notify_url="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String return_url="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String user_order_no = (System.currentTimeMillis () + "").substring (0,13);
            String note = user_order_no;
            
            String signStr = uid + price + paytype + notify_url + return_url + user_order_no + key;
            String sign = Md5Util.md5(signStr).toUpperCase();
            
            JSONObject prams = new JSONObject();
            prams.put ("uid",uid);
            prams.put ("price",price);
            prams.put ("paytype",paytype);
            prams.put ("notify_url", notify_url);
            prams.put ("return_url", return_url);
            prams.put ("user_order_no",user_order_no);
            prams.put ("note",note);
            prams.put ("sign",sign);
            logger.info("排序后{}",signStr);
            
            logger.info("支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(url, prams);
            logger.info("支付接口返参{}",response);
            
            
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
