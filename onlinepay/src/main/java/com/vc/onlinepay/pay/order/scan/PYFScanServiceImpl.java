package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class PYFScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (PYFScanServiceImpl.class);
    private static Map<Integer,Integer> wxScanAmount  = new HashMap<Integer,Integer> ();
    static {
    	//500-1000-2000-3000-5000
    	wxScanAmount.put (500,500);
   	 	wxScanAmount.put (1000,1000);
		 wxScanAmount.put (2000,2000); 
		 wxScanAmount.put (3000,3000);
		 wxScanAmount.put (5000,5000);
		 
		  }
    

    /**
     * @描述:PYF微信扫码支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("PYF微信扫码支付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String account = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            
            //String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
            String productName = "HS";
            String orginalOrderNo = orderNo;
            String payWayCode = "11";//微信扫码
            String amount= reqData.getString ("amount");
            String orderCreateTime	=Constant.yyyyMMdd.format(new Date());
            
            String callBackUrl = reqData.getString ("projectDomainUrl") + "/pYFCallBackController";
            String successUrl = reqData.getString("projectDomainUrl")+"/success";
            
            JSONObject parms = new JSONObject();
            parms.put ("account",account);
            parms.put ("productName",productName);
            parms.put ("orginalOrderNo",orginalOrderNo);
            parms.put ("payWayCode", payWayCode);
            parms.put ("amount", amount);
            parms.put ("orderCreateTime",orderCreateTime);
            parms.put ("callBackUrl",callBackUrl);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toLowerCase();
            parms.put ("sign",pay_md5sign);
            parms.put ("successUrl",successUrl);
            logger.info("支付接口入参{}",parms);
            //String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            //logger.info("支付接口返参{}",response);
            
            logger.info("支付接口入参{}",parms);
            result.put("actionUrl", API_PAY_URL);
            result.put("code", Constant.SUCCESSS);
            result.put("viewPath","auto/autoSubmit");
            //result.put("redirectActionHtml","auto/autoSubmit");
            result.put("data",parms);
            result.put("msg", "下单成功");
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("PYF微信扫码支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://www.pyfpay.com/pay/order";
            String key = "778ce555d2231d0c2d69ffa7db9cff75";
            
            String account = "A100000030";
            String productName = "HS";
            String orginalOrderNo = (System.currentTimeMillis () + "").substring (0,13);
            String payWayCode = "01";//微信扫码
            String amount="100";
            String orderCreateTime	=Constant.yyyyMMdd.format(new Date());
            
            String callBackUrl ="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String successUrl ="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            
            
            JSONObject parms = new JSONObject();
            parms.put ("account",account);
            parms.put ("productName",productName);
            parms.put ("orginalOrderNo",orginalOrderNo);
            parms.put ("payWayCode", payWayCode);
            parms.put ("amount", amount);
            parms.put ("orderCreateTime",orderCreateTime);
            parms.put ("callBackUrl",callBackUrl);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toLowerCase();
            parms.put ("sign",pay_md5sign);
            parms.put ("successUrl",successUrl);
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
