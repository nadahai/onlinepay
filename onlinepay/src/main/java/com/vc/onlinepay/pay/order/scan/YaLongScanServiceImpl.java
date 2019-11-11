package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
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
public class YaLongScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (YaLongScanServiceImpl.class);
    
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;

    /**
     * @描述:亚龙支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("亚龙支付H5接收入参{}", reqData);
            String OrderID = reqData.getString ("vcOrderNo");
            result.put ("orderNo", OrderID);
            

            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String MerID = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            
            String Money = reqData.getString ("amount");
            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
	        String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
	        //payType 1:微信 2:支付宝
	        String Channel = "WxpayH5";//WxpayH5
	         if (type == 2 || type == 10 || type == 22 || Constant.service_alipay.equals (service)) {
	        	 Channel = "AlipayH5";//WxpayH5
	         }
            
            String Return = reqData.getString("projectDomainUrl")+"/success";
            String Notify = reqData.getString ("projectDomainUrl") + "/yaLongCallBackController";
            String Mark = OrderID;
            Map<String, String> parms = new HashMap<String, String>();
            parms.put ("MerID",MerID);
            parms.put ("OrderID",OrderID);
            parms.put ("Money",Money);
            parms.put ("Channel", Channel);
            parms.put ("Notify", Notify);
            parms.put ("Return",Return);
            parms.put ("Mark",Mark);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&Key="+key;
            logger.info("排序后{}",sourctxt1);
            String Sign = Md5Util.md5(sourctxt1);
            
            String payUrl = "url="+API_PAY_URL
            		+"&MerID="+MerID
            		+"&OrderID="+OrderID
            		+"&Money="+Money
            		+"&Channel="+Channel
            		+"&Notify="+Notify
            		+"&Return="+Return
            		+"&Mark="+Mark
            		+"&Sign="+Sign;
            
            logger.info("支付接口入参{}",payUrl);
            
            
	         VcOnlineOrderMade made = new VcOnlineOrderMade();
		        made.setChannelId(reqData.getIntValue("channelLabel"));
	            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
		        made.setMerchNo(reqData.getString("merchantNo"));
		        made.setOrderNo(OrderID);
	            made.setOpenType (132);
	            made.setRemarks (reqData.getString("channelKey"));
		        made.setPaySource(reqData.getIntValue("channelSource"));
		        made.setTraAmount(new BigDecimal (Money));
		        made.setUpMerchKey(key);
		        made.setUpMerchNo(reqData.getString("merchantNo"));
		        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(OrderID));
		        made.setQrcodeUrl(payUrl);
		        JSONObject response2 = vcOnlineOrderMadeService.getOrderMadePayUrl(made);
		        logger.info("扫码支付响应{}",response2);
				if(response2 == null || response2.isEmpty()){
	                return listener.failedHandler(Constant.failedMsg ("扫码支付超时"));
	            }
				result.put ("realAmount",made.getTraAmount());
	            result.put ("code", Constant.SUCCESSS);
	            result.put ("msg", "获取链接成功");
	            result.put ("bankUrl",StringEscapeUtils.unescapeJava(response2.getString("openUrl")));
	            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(response2.getString("openUrl")));
	            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(response2.getString("openUrl")));
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("亚龙支付H5下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String url = "http://www.yalongpay.cn:88/Pay";
            String key = "HLEQdKIW90rCWkj49DVw1ndsi4gT2aBKR2hBHiI9geBrxwjkFM";
            
            String MerID = "10012118";
            String OrderID = (System.currentTimeMillis () + "").substring (0,13);
            String Money = "100";
            String Channel = "AlipayH5";//WxpayH5
            String Notify = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String Return = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String Mark = OrderID;
            Map<String, String> parms = new HashMap<String, String>();
            parms.put ("MerID",MerID);
            parms.put ("OrderID",OrderID);
            parms.put ("Money",Money);
            parms.put ("Channel", Channel);
            parms.put ("Notify", Notify);
            parms.put ("Return",Return);
            parms.put ("Mark",Mark);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&Key="+key;
            logger.info("排序后{}",sourctxt1);
            String Sign = Md5Util.md5(sourctxt1);
            
            parms.put ("Sign",Sign);
            
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpPost(url, parms);
            logger.info("支付接口返参{}",response);
            
           
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
