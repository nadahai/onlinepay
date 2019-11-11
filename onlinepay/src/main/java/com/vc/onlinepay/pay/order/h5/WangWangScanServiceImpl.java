package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class WangWangScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (WangWangScanServiceImpl.class);
    

    /**
     * @描述:旺旺支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("旺旺支付H5接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String MerID = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String NotifyUrl = reqData.getString ("projectDomainUrl") + "/wangWangCallBackController";
            String ReturnUrl = reqData.getString("projectDomainUrl")+"/success";
            String Amount = reqData.getString ("amount");
            String ApiMethod="OnLinePay";
            String Version="V2.0";
            String TradeNum = orderNo;
            String TransTime=Constant.ymdhms.format(new Date ());
            //wxwap
            
            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
            
	         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
		     //payType 1:微信 2:支付宝
	         String PayType = "wxwap";
	         if (type == 2 || type == 10 || type == 22 || Constant.service_alipay.equals (service)) {
	        	 PayType = "alipaywap";
	         }
            String SignType = "MD5";
            
            
            JSONObject parms = new JSONObject();
            parms.put ("ApiMethod",ApiMethod);
            parms.put ("Version",Version);
            parms.put ("MerID",MerID);
            parms.put ("TradeNum", TradeNum);
            parms.put ("Amount", Amount);
            parms.put ("NotifyUrl",NotifyUrl);
            parms.put ("ReturnUrl",ReturnUrl);
            parms.put ("TransTime",TransTime);
            parms.put ("PayType",PayType);
            parms.put ("SignType",SignType);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("Sign",sign);
            
            logger.info("支付接口入参{}",parms);
            result.put("actionUrl", API_PAY_URL);
            result.put("code", Constant.SUCCESSS);
            result.put("viewPath","auto/autoSubmit");
            //result.put("redirectActionHtml","auto/autoSubmit");
            result.put("data",parms);
            result.put("msg", "下单成功");
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("旺旺支付H5下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "https://api.wwbpay.com:8066/GateWay/ApiInterFace.aspx";
            String key = "GrjgBDseZotuGW54xd8GesHdcu8RIaIc";
            
            String ApiMethod="OnLinePay";
            String Version="V2.0";
            String MerID = "1742";
            String TradeNum = (System.currentTimeMillis () + "").substring (0,13);
            String Amount="1000";
            String NotifyUrl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String ReturnUrl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String TransTime=Constant.ymdhms.format(new Date ());
            //wxwap
            String PayType="alipaywap";
            String SignType = "MD5";
            
            
            JSONObject parms = new JSONObject();
            parms.put ("ApiMethod",ApiMethod);
            parms.put ("Version",Version);
            parms.put ("MerID",MerID);
            parms.put ("TradeNum", TradeNum);
            parms.put ("Amount", Amount);
            parms.put ("NotifyUrl",NotifyUrl);
            parms.put ("ReturnUrl",ReturnUrl);
            parms.put ("TransTime",TransTime);
            parms.put ("PayType",PayType);
            parms.put ("SignType",SignType);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("Sign",sign);
            
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);

           
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
