package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.sand.MD5;
import cn.hutool.json.XML;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class YouFuScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (YouFuScanServiceImpl.class);
    
    

    /**
     * @描述:友付扫码
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("友付扫码接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String spid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String backUrl = reqData.getString ("projectDomainUrl") + "/youFuCallBackController";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String tranAmt = reqData.getString ("amount");
			/*
			 * if(!Constant.isNumeric (tranAmt)){ return listener.failedHandler
			 * (Constant.failedMsg ("仅支持整数金额")); }
			 */
			
			 Double amount = Double.parseDouble (tranAmt);
			 tranAmt = amount*100+"";
	         String version="2.0";
	         String charset="UTF-8";
	         String spbillno=orderNo;
	         
	         Integer type = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
	         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
		     //payType 1:微信 2:支付宝
		     String payType = "pay.weixin.native";
	         if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
	        	 payType = "pay.alipay.native";
	         }
	         
	         String attach=spbillno;
	         String productName="运动鞋（男）";
	         String signType="MD5";
	            
	         Map<String, String> parms = new HashMap<String, String>();
	         parms.put ("version",version);
	         parms.put ("charset",charset);
	         parms.put ("spid",spid);
	         parms.put ("spbillno", spbillno);
	         parms.put ("tranAmt", tranAmt);
	         parms.put ("payType",payType);
	         parms.put ("backUrl",returnUrl);
	         parms.put ("notifyUrl",backUrl);
	         parms.put ("attach",attach);
	         parms.put ("productName",productName);
	         String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
	         logger.info("排序后{}",sourctxt1);
	         String sign = Md5Util.md5(sourctxt1).toUpperCase();
	         parms.put ("signType",signType);
	         parms.put ("sign",sign);
	         String strxml = HttpClientTools.getStringXML(parms);
	         logger.info("支付接口入参{}",strxml);
	         String response = HttpClientTools.httpPostWithXML(API_PAY_URL, strxml);
	         if(StringUtils.isEmpty (response)){
	            return listener.failedHandler (Constant.failedMsg ("下单为空"));
	         }
	         logger.info("支付接口返参{}",response);
	         cn.hutool.json.JSONObject payParams = XML.toJSONObject(response);
	         if(payParams == null || payParams.isEmpty () 
	            || !"0".equals(payParams.getJSONObject("xml").getStr("retcode"))
	            || !payParams.getJSONObject("xml").containsKey ("codeImgUrl")
	        		 ){
	        	 return listener.failedHandler (Constant.failedMsg ("获取连接为空"));
	          }
	         result.put ("code", Constant.SUCCESSS);
	         result.put ("msg", "获取链接成功");
	         result.put ("bankUrl",payParams.getJSONObject("xml").getStr("codeImgUrl"));
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("友付扫码下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String url = "https://pay.surperpay.com/pay/nativePay";
            String key = "b7d76880afcb4f33957bd71e10fbb446";
            
            String tranAmt1 = "200.01";
            Double amount = Double.parseDouble (tranAmt1);
            System.out.println(amount);
            
            String version="2.0";
            String charset="UTF-8";
            String spid="C1557481849527";
            String spbillno=(System.currentTimeMillis () + "").substring (0,13);
            String tranAmt="10000";
            String payType="pay.alipay.native";
            String backUrl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String notifyUrl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String attach=spbillno;
            String productName="运动鞋（男）";
            String signType="MD5";
            
            Map<String, String> parms = new HashMap<String, String>();
            parms.put ("version",version);
            parms.put ("charset",charset);
            parms.put ("spid",spid);
            parms.put ("spbillno", spbillno);
            parms.put ("tranAmt", tranAmt);
            parms.put ("payType",payType);
            parms.put ("backUrl",backUrl);
            parms.put ("notifyUrl",notifyUrl);
            parms.put ("attach",attach);
            parms.put ("productName",productName);
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = MD5.md5(sourctxt1).toUpperCase();
            parms.put ("signType",signType);
            parms.put ("sign",sign);
            String strxml = HttpClientTools.getStringXML(parms);
            logger.info("支付接口入参{}",strxml);
            String response = HttpClientTools.httpPostWithXML(url, strxml);
            logger.info("支付接口返参{}",response);
            
            cn.hutool.json.JSONObject payParams = XML.toJSONObject(response);
            if(payParams == null || payParams.isEmpty () 
            		|| !"0".equals(payParams.getJSONObject("xml").getStr("retcode")) ){
                
            }
            System.out.println(payParams.getJSONObject("xml").getStr("codeImgUrl"));
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
