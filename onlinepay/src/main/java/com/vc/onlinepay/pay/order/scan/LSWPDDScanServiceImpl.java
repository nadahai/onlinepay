package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.http.HttpsClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class LSWPDDScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (LSWPDDScanServiceImpl.class);
    

    /**
     * @描述:LSWPDD支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("LSWPDD支付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String pay_memberid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String pay_notifyurl = reqData.getString ("projectDomainUrl") + "/lSWPDDCallBackController";
            String pay_callbackurl = reqData.getString("projectDomainUrl")+"/success";
            String pay_amount= reqData.getString ("amount");
            String pay_orderid = orderNo;
            String pay_applydate	=Constant.yyyyMMdd.format(new Date());
            
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
            
            BigDecimal ba = new BigDecimal(pay_amount);
			 int amountInt = ba.intValue();
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amountInt+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }
            
            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
	         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
		     //payType 1:微信 2:支付宝
	         String pay_bankcode = "953";//支付宝h5
	         /**
	         if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
	        	 pay_bankcode = "904";
	         }
            **/
            
            JSONObject parms = new JSONObject();
            parms.put ("pay_memberid",pay_memberid);
            parms.put ("pay_orderid",pay_orderid);
            parms.put ("pay_applydate",pay_applydate);
            parms.put ("pay_bankcode", pay_bankcode);
            parms.put ("pay_notifyurl", pay_notifyurl);
            parms.put ("pay_callbackurl",pay_callbackurl);
            parms.put ("pay_amount",pay_amount);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("pay_md5sign",pay_md5sign);
            String pay_productname = pay_orderid;
            parms.put ("pay_productname",pay_productname);
            
            logger.info("支付接口入参{}",parms);
            result.put("actionUrl", API_PAY_URL);
            result.put("code", Constant.SUCCESSS);
            result.put("viewPath","auto/autoSubmit");
            //result.put("redirectActionHtml","auto/autoSubmit");
            result.put("data",parms);
            result.put("msg", "下单成功");
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("LSWPDD支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "https://www.newpay8586.top/Pay_Index.html";
            String key = "mwgf70695n3pra7el52ln9lxnutmgib0";
            
            String pay_memberid = "190993907";
            String pay_orderid = (System.currentTimeMillis () + "").substring (0,13);
            String pay_applydate	=Constant.yyyyMMdd.format(new Date());
            String pay_bankcode = "953";//原生支付宝h5
            String pay_notifyurl	="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String pay_callbackurl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String pay_amount="1000";
            
            JSONObject parms = new JSONObject();
            parms.put ("pay_memberid",pay_memberid);
            parms.put ("pay_orderid",pay_orderid);
            parms.put ("pay_applydate",pay_applydate);
            parms.put ("pay_bankcode", pay_bankcode);
            parms.put ("pay_notifyurl", pay_notifyurl);
            parms.put ("pay_callbackurl",pay_callbackurl);
            parms.put ("pay_amount",pay_amount);
            //parms.put ("pay_bankcode",pay_bankcode);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("pay_md5sign",pay_md5sign);
            String pay_productname = pay_orderid;
            parms.put ("pay_productname",pay_productname);
            logger.info("支付接口入参{}",parms);
            String response = HttpsClientTools.sendHttpSSL_textjson(parms,API_PAY_URL);
            logger.info("支付接口返参{}",response);


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
