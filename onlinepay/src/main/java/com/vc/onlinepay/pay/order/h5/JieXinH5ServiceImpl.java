package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import java.math.BigDecimal;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class JieXinH5ServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (JieXinH5ServiceImpl.class);
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;
    

    /**
     * @描述:捷信支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("捷信支付支付宝H5接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            
            
            String parter = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String type = "1006";
            String value= reqData.getString ("amount");
            String orderid = orderNo;
            String callbackurl = reqData.getString ("projectDomainUrl") + "/jieXinCallBackController";
            String hrefbackurl = reqData.getString("projectDomainUrl")+"/success";
            
            String signStr = "parter="+parter+"&type="+type+"&value="+value+"&orderid="+orderid+"&callbackurl="+callbackurl+key;
            String sign = Md5Util.md5(signStr);
            
            String payUrl = API_PAY_URL+"?parter="+parter+"&type="+type+"&value="+value+"&orderid="+orderid+"&callbackurl="+callbackurl+"&hrefbackurl="+hrefbackurl+"&sign="+sign;
            
            logger.info("支付接口入参{}",payUrl);
            
            VcOnlineOrderMade made = new VcOnlineOrderMade();
	        made.setChannelId(reqData.getIntValue("channelLabel"));
            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
	        made.setMerchNo(reqData.getString("merchantNo"));
	        made.setOrderNo(orderNo);
            made.setOpenType (202);
            made.setRemarks (reqData.getString("channelKey"));
	        made.setPaySource(reqData.getIntValue("channelSource"));
	        made.setTraAmount(new BigDecimal (value));
	        made.setUpMerchKey(key);
	        made.setUpMerchNo(reqData.getString("merchantNo"));
	        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(orderNo));
	        made.setQrcodeUrl(payUrl);
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
            logger.error ("捷信支付支付宝H5下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://pay.jiexunzf.net/Pay/GateWay";
            String key = "fade19f503414c0e94b4b4a214c15549";
            
            String parter = "1860";
            String type = "1006";
            String value = "100";
            String orderid = (System.currentTimeMillis () + "").substring (0,13);
            String callbackurl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String hrefbackurl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            
            String signStr = "parter="+parter+"&type="+type+"&value="+value+"&orderid="+orderid+"&callbackurl="+callbackurl+key;
            String sign = Md5Util.md5(signStr);
            String payUrl = API_PAY_URL+"?parter="+parter+"&type="+type+"&value="+value+"&orderid="+orderid+"&callbackurl="+callbackurl+"&hrefbackurl="+hrefbackurl+"&sign="+sign;
            
            logger.info("支付接口入参{}",payUrl);
            System.out.println(payUrl);
            //String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            //logger.info("支付接口返参{}",response);
           
           
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
