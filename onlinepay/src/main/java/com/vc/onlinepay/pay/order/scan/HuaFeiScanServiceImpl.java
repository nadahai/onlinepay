package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import cn.hutool.json.XML;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class HuaFeiScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (HuaFeiScanServiceImpl.class);
    private static Map<Integer,Integer> wxScanAmount  = new HashMap<Integer,Integer> ();
    static { 
    	 wxScanAmount.put (10,10);
    	 wxScanAmount.put (20,20);
    	 wxScanAmount.put (30,30);
		 wxScanAmount.put (50,50); 
		 wxScanAmount.put (100,100);
		 wxScanAmount.put (200,200);
		 wxScanAmount.put (300,300);
		 wxScanAmount.put (500,500);
		  }
    

    /**
     * @描述:花费通道
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("花费通道接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String spid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String backUrl = reqData.getString ("projectDomainUrl") + "/huaFeiCallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String tranAmt = reqData.getString ("amount");
            
			/*
			 * if(!Constant.isNumeric (tranAmt)){ return listener.failedHandler
			 * (Constant.failedMsg ("仅支持整数金额")); }
			 */
            
            BigDecimal ba = new BigDecimal(tranAmt);
			 int amount = ba.intValue();
			 if(!wxScanAmount.containsKey(amount)){
	                return listener.failedHandler (Constant.failedMsg ("仅支持10、20、30、50、100、200、300、500"));
	            }
			 tranAmt = amount*100+"";
            
            JSONObject orderInfo = new JSONObject();
            orderInfo.put("out_trade_no", orderNo);
            orderInfo.put("total_amount", tranAmt);
            orderInfo.put("timestamp", DateUtils.getTimeForY_M_D_H_m_s());
            orderInfo.put("partner_id", spid);
            orderInfo.put("product_code", "WX_QRCODE");
            orderInfo.put("subject", "花费充值");
            orderInfo.put("body", "花费充值");
            orderInfo.put("notify_url", backUrl);
            orderInfo.put("return_url", returnUrl);
            orderInfo.put("client_ip", "127.0.0.1");
            orderInfo.put("sign", Md5CoreUtil.md5ascii(orderInfo, key));
			
	         logger.info("支付接口入参{}",orderInfo);
	         String response = HttpClientTools.baseHttpSendPost(API_PAY_URL, orderInfo);
	         if(StringUtils.isEmpty (response)){
	            return listener.failedHandler (Constant.failedMsg ("下单为空"));
	         }
	         logger.info("支付接口返参{}",response);
	         JSONObject payParams = Constant.stringToJson (response);
	         if(payParams == null || payParams.isEmpty()){
	        	 return listener.failedHandler (Constant.failedMsg("获取连接为空"));
	         }
             if(!payParams.containsKey ("pay_url")){
                String msg = payParams.containsKey("msg")?payParams.getString("msg"):"获取连接为空";
                return listener.failedHandler (Constant.failedMsg (msg));
             }
	         result.put ("code", Constant.SUCCESSS);
	         result.put ("msg", payParams.containsKey("msg")?payParams.getString("msg"):"获取链接成功");
	         int resamount = Integer.valueOf (payParams.getString("pay_amount"));
	         BigDecimal b = new BigDecimal(resamount);
	         result.put ("realAmount",b.divide(new BigDecimal(100)));
	         result.put ("bankUrl",payParams.getString("pay_url"));
	         result.put ("redirectUrl",payParams.getString("pay_url"));
	         result.put ("qrCodeUrl",payParams.getString("pay_url"));
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("花费下单下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
        	BigDecimal b = new BigDecimal("50.00");
        	System.out.println(b.intValue());
        	System.out.println(b.divide(new BigDecimal(100)));
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
