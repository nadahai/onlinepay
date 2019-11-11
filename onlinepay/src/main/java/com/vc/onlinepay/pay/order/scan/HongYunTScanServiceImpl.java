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
public class HongYunTScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (HongYunTScanServiceImpl.class);
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;
    private static Map<Integer,Integer> wxScanAmount  = new HashMap<Integer,Integer> ();
    static { 
    	 wxScanAmount.put (10,10); 
		 wxScanAmount.put (50,50); 
		 wxScanAmount.put (100,100);
		  }
    

    /**
     * @描述:鸿运通微信个码通道
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("鸿运通接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String spid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String backUrl = reqData.getString ("projectDomainUrl") + "/hongYunTCallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String tranAmt = reqData.getString ("amount");
            
            String pay_applydate = DateUtils.getTimeForY_M_D_H_m_s();
            
            
            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
            
	         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
		     //payType 1:微信 2:支付宝
	         String pay_bankcode = "904";
	         if ( type == 10 || Constant.service_alipay.equals (service)) {
	        	 pay_bankcode = "904";
	         }else if(type == 2) {
	        	 pay_bankcode = "904";
	         }
            
	        
            JSONObject orderInfo = new JSONObject();
            orderInfo.put("pay_memberid", spid);
            orderInfo.put("pay_orderid", orderNo);
            orderInfo.put("pay_applydate", pay_applydate);
            orderInfo.put("pay_bankcode", pay_bankcode);
            orderInfo.put("pay_notifyurl", backUrl);
            orderInfo.put("pay_callbackurl", returnUrl);
            orderInfo.put("pay_amount", tranAmt);
            
            String pay_md5sign = Md5CoreUtil.md5ascii(orderInfo, key).toUpperCase();
            orderInfo.put("pay_md5sign", pay_md5sign);
			
            String bankUrl = API_PAY_URL+"?pay_memberid="+spid
            		+ "&pay_orderid="+orderNo
            		+ "&pay_applydate="+pay_applydate
            		+ "&pay_bankcode="+pay_bankcode
            		+ "&pay_notifyurl="+backUrl
            		+ "&pay_callbackurl="+returnUrl
            		+ "&pay_amount="+tranAmt
            		+ "&pay_md5sign="+pay_md5sign;
            
	         logger.info("支付接口入参{}",bankUrl);
	         /***
	         VcOnlineOrderMade made = new VcOnlineOrderMade();
		        made.setChannelId(reqData.getIntValue("channelLabel"));
	            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
		        made.setMerchNo(reqData.getString("merchantNo"));
		        made.setOrderNo(orderNo);
	            made.setOpenType (127);
	            made.setRemarks (reqData.getString("channelKey"));
		        made.setPaySource(reqData.getIntValue("channelSource"));
		        made.setTraAmount(new BigDecimal (tranAmt));
		        made.setUpMerchKey(key);
		        made.setUpMerchNo(reqData.getString("merchantNo"));
		        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(orderNo));
		        String val = bankUrl;
		        made.setQrcodeUrl(val);
		        JSONObject response2 = vcOnlineOrderMadeService.getOrderMadePayUrl(made);
		        logger.info("扫码支付响应{}",response2);
				if(response2 == null || response2.isEmpty()){
	                return listener.failedHandler(Constant.failedMsg ("扫码支付超时"));
	            }
				**/
	            result.put("actionUrl", API_PAY_URL);
	            result.put("code", Constant.SUCCESSS);
	            result.put("viewPath","auto/autoSubmit");
	            //result.put("redirectActionHtml","auto/autoSubmit");
	            result.put("data",orderInfo);
	            result.put("msg", "下单成功");
	            
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("鸿运通微信个码通道下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
        	JSONObject orderInfo = new JSONObject();
            orderInfo.put("pay_memberid", "10053");
            orderInfo.put("pay_orderid", System.currentTimeMillis ());
            orderInfo.put("pay_applydate", DateUtils.getTimeForY_M_D_H_m_s());
            orderInfo.put("pay_bankcode", "904");
            orderInfo.put("pay_notifyurl", "http://pay.mastepay.com/Pay/yunPay/TUNPay.php");
            orderInfo.put("pay_callbackurl", "http://pay.mastepay.com/Pay/yunPay/TUNPay.php");
            orderInfo.put("pay_amount", "100");
            orderInfo.put("pay_md5sign", Md5CoreUtil.md5ascii(orderInfo, "dovpztazvg5gubrfcv40bt0pqv5lleos").toUpperCase());
			
	         logger.info("支付接口入参{}",orderInfo);
	         String response = HttpClientTools.httpSendPostFrom("http://quanyingfu.shenyangweimeng.com/Pay_Index.html", orderInfo);
	         
	         logger.info("支付接口返参{}",response);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
