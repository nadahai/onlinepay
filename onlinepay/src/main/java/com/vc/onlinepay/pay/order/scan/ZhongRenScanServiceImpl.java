package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
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
public class ZhongRenScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (ZhongRenScanServiceImpl.class);
    

    /**
     * @描述:众任微信扫码支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("众任微信扫码支付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String pay_memberid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String pay_notifyurl = reqData.getString ("projectDomainUrl") + "/zhongRenCallBackController";
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
            
	         String pay_bankcode = "919";//微信扫码
	         
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
            parms.put ("pay_types","1");
            
            logger.info("支付接口入参{}",parms);
            result.put("actionUrl", API_PAY_URL);
            result.put("code", Constant.SUCCESSS);
            result.put("viewPath","auto/autoSubmit");
            result.put("data",parms);
            result.put("msg", "下单成功");
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("众任微信扫码支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://www.egemie.cn/Pay_Index.html";
            String key = "8wxtw2s6ssppd088py1wjrrylp1sapsv";
            
            String pay_memberid = "10265";
            String pay_orderid = (System.currentTimeMillis () + "").substring (0,13);
            String pay_applydate	=Constant.yyyyMMdd.format(new Date());
            String pay_bankcode = "919";//微信扫码
            String pay_notifyurl	="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String pay_callbackurl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String pay_amount="100";
            
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
            parms.put ("pay_types","1");
            String pay_productname = pay_orderid;
            parms.put ("pay_productname",pay_productname);
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
