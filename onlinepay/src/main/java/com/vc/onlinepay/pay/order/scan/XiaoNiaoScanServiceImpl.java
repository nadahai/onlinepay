package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class XiaoNiaoScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (XiaoNiaoScanServiceImpl.class);
    

    /**
     * @描述:小鸟支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("小鸟支付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String merchant_number = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String server_url = reqData.getString ("projectDomainUrl") + "/xiaoNiaoCallBackController";
            String brower_url = reqData.getString("projectDomainUrl")+"/success";
            String cash= reqData.getString ("amount");
            String order_id = orderNo;
            
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
            
            BigDecimal ba = new BigDecimal(cash);
			 int amountInt = ba.intValue();
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amountInt+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }
            
			    String version = "2";
	            String order_time = System.currentTimeMillis()+"";
	            String pay_type = "2";
	            
	            
	            JSONObject parms = new JSONObject();
	            parms.put ("version",version);
	            parms.put ("merchant_number",merchant_number);
	            parms.put ("cash",cash);
	            parms.put ("server_url", server_url);
	            parms.put ("brower_url", brower_url);
	            parms.put ("order_id",order_id);
	            parms.put ("order_time",order_time);
	            parms.put ("pay_type",pay_type);
	            
	            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
	            logger.info("排序后{}",sourctxt1);
	            String pay_md5sign = Md5Util.md5(sourctxt1).toLowerCase();
	            parms.put ("sign",pay_md5sign);
	            logger.info("支付接口入参{}",parms);
	            /**
	            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
	            logger.info("支付接口返参{}",response);
	            if(StringUtils.isEmpty (response)){
	                return listener.failedHandler (Constant.failedMsg ("下单为空"));
	            }
	            JSONObject payParams = Constant.stringToJson (response);
	            if(payParams == null || payParams.isEmpty () || !payParams.getJSONObject("data").containsKey ("qr_code_url")){
	                return listener.failedHandler (Constant.failedMsg ("获取连接为空"));
	            }
				
	            result.put ("code", Constant.SUCCESSS);
	            result.put ("msg", "获取链接成功");
	            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString("qr_code_url")));
	            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString("qr_code_url")));
	            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("data").getString("qr_code_url")));
            **/
	            logger.info("支付接口入参{}",parms);
	            result.put("actionUrl", API_PAY_URL);
	            result.put("code", Constant.SUCCESSS);
	            result.put("viewPath","auto/autoSubmit");
	            //result.put("redirectActionHtml","auto/autoSubmit");
	            result.put("data",parms);
	            result.put("msg", "下单成功");
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("小鸟支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://mch211.upay6.cn/api/recharge/index";
            String key = "46841ffe-7c5f-4716-848a-c0d4c223a1c3";
            
            String version = "2";
            String merchant_number = "1029";
            String cash = "10";
            String server_url	="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String brower_url="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String order_id = (System.currentTimeMillis () + "").substring (0,13);
            String order_time = System.currentTimeMillis()+"";
            String pay_type = "2";
            
            
            JSONObject parms = new JSONObject();
            parms.put ("version",version);
            parms.put ("merchant_number",merchant_number);
            parms.put ("cash",cash);
            parms.put ("server_url", server_url);
            parms.put ("brower_url", brower_url);
            parms.put ("order_id",order_id);
            parms.put ("order_time",order_time);
            parms.put ("pay_type",pay_type);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toLowerCase();
            parms.put ("sign",pay_md5sign);
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
