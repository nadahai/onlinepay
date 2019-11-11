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
public class XinZhiFuScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (XinZhiFuScanServiceImpl.class);
    
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;

    /**
     * @描述:鑫支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("鑫支付H5接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String orderPrice = reqData.getString ("amount");
			
            String payKey = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String outTradeNo = orderNo;
            String productType = "20000203";
            String orderTime = DateUtils.getTimeYMDhms();
            String productName = "一双黑色运动鞋";
            String orderIp = "127.0.0.1";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String notifyUrl = reqData.getString ("projectDomainUrl") + "/xinZhiFuCallBackController";

            Map<String, String> parms = new HashMap<String, String>();
            parms.put ("payKey",payKey);
            parms.put ("orderPrice",orderPrice);
            parms.put ("outTradeNo",outTradeNo);
            parms.put ("productType", productType);
            parms.put ("orderTime", orderTime);
            parms.put ("productName",productName);
            parms.put ("orderIp",orderIp);
            parms.put ("returnUrl",returnUrl);
            parms.put ("notifyUrl",notifyUrl);
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&paySecret="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpPost(API_PAY_URL, parms);
            logger.info("支付接口返参{}",response);
            if(StringUtils.isEmpty (response)){
	            return listener.failedHandler (Constant.failedMsg ("下单为空"));
	        }
            JSONObject payParams = Constant.stringToJson (response);
            if(!"SUCCESS".equals(payParams.getString("resultCode"))){
	            return listener.failedHandler (Constant.failedMsg ("下单失败"));
	        }
            
	         VcOnlineOrderMade made = new VcOnlineOrderMade();
		        made.setChannelId(reqData.getIntValue("channelLabel"));
	            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
		        made.setMerchNo(reqData.getString("merchantNo"));
		        made.setOrderNo(orderNo);
	            made.setOpenType (103);
	            made.setRemarks (reqData.getString("channelKey"));
		        made.setPaySource(reqData.getIntValue("channelSource"));
		        made.setTraAmount(new BigDecimal (orderPrice));
		        made.setUpMerchKey(key);
		        made.setUpMerchNo(reqData.getString("merchantNo"));
		        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(orderNo));
		        
		        String val = payParams.getString("payMessage");
		        made.setQrcodeUrl(val);
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
            logger.error ("鑫支付H5下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String url = "http://122.14.215.60:8080/pay-web-gateway/cnpPay/initPay";
            String key = "69775476dbba41339c7d1baeda5bccb2";
            
            
            String payKey = "bc4bdece35b7444d92650d9ec2545ae4";
            String orderPrice = "100.00";
            String outTradeNo = (System.currentTimeMillis () + "").substring (0,13);
            String productType = "20000203";
            String orderTime = DateUtils.getTimeYMDhms();
            String productName = "一双黑色运动鞋";
            String orderIp = "127.0.0.1";
            String returnUrl = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String notifyUrl = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";

            Map<String, String> parms = new HashMap<String, String>();
            parms.put ("payKey",payKey);
            parms.put ("orderPrice",orderPrice);
            parms.put ("outTradeNo",outTradeNo);
            parms.put ("productType", productType);
            parms.put ("orderTime", orderTime);
            parms.put ("productName",productName);
            parms.put ("orderIp",orderIp);
            parms.put ("returnUrl",returnUrl);
            parms.put ("notifyUrl",notifyUrl);
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&paySecret="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpPost(url, parms);
            logger.info("支付接口返参{}",response);
            
           
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
