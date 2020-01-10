package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Component
public class HangKongScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (HangKongScanServiceImpl.class);
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;
    private static Map<Integer,Integer> wxScanAmount  = new HashMap<Integer,Integer> ();
    static { 
    	 wxScanAmount.put (10,10); 
		 wxScanAmount.put (50,50); 
		 wxScanAmount.put (100,100);
		  }
    

    /**
     * @描述:航空支付宝扫码通道
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
            String backUrl = reqData.getString ("projectDomainUrl") + "/hangKongCallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String tranAmt = reqData.getString ("amount");
            
            String pay_applydate = DateUtils.getTimeForY_M_D_H_m_s();

            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
	         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
		     //payType 1:微信 2:支付宝
	         String pay_bankcode = "901";
	         if ( type == 10 || Constant.service_alipay.equals (service)) {
	        	 pay_bankcode = "901";
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
	            result.put("actionUrl", API_PAY_URL);
	            result.put("code", Constant.SUCCESSS);
	            result.put("viewPath","auto/autoSubmit");
	            result.put("data",orderInfo);
	            result.put("msg", "下单成功");
	            
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("航空支付宝扫码通道下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
        	JSONObject orderInfo = new JSONObject();
            orderInfo.put("pay_memberid", "10281");
            orderInfo.put("pay_orderid", System.currentTimeMillis ());
            orderInfo.put("pay_applydate", DateUtils.getTimeForY_M_D_H_m_s());
            orderInfo.put("pay_bankcode", "901");
            orderInfo.put("pay_notifyurl", "http://pay.mastepay.com/Pay/yunPay/TUNPay.php");
            orderInfo.put("pay_callbackurl", "http://pay.mastepay.com/Pay/yunPay/TUNPay.php");
            orderInfo.put("pay_amount", "100");
            orderInfo.put("pay_md5sign", Md5CoreUtil.md5ascii(orderInfo, "lm9ym854fy8zxlzemm1a5eauskhykjnm").toUpperCase());
			
	         logger.info("支付接口入参{}",orderInfo);
	         String response = HttpClientTools.httpSendPostFrom("http://www.g41t.cn/Pay_index", orderInfo);
	         
	         logger.info("支付接口返参{}",response);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
