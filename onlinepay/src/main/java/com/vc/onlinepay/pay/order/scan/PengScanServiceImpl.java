package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;

import java.io.IOException;
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
public class PengScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(PengScanServiceImpl.class);
    
    /**
     * @描述:peng支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("peng支付交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String mch_id = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String notify_url = reqData.getString("projectDomainUrl")+"/pengCallBackController";
            String return_url = reqData.getString("projectDomainUrl")+"/success";
            String amount = reqData.getString("amount");
            String serviceCallbackUrl =  reqData.getString("serviceCallbackUrl");
            
            BigDecimal ba = new BigDecimal(amount);
			 int amountInt = ba.intValue();
			  if(!serviceCallbackUrl.isEmpty() &&!serviceCallbackUrl.contains(","+amountInt+",")){ 
				  return listener.failedHandler (Constant.failedMsg("金额与通道不符合")); 
			  }
			  
			  String pay_type = "10001";
			  String create_time = new Date().getTime()+"";
			  String subject = "notebook";
	         JSONObject parms = new JSONObject();
	            parms.put ("mch_id",mch_id);
	            parms.put ("mch_order_no",reqData.getString("vcOrderNo"));
	            parms.put ("pay_type",pay_type);
	            parms.put ("total_amount", amount);
	            parms.put ("notify_url", notify_url);
	            parms.put ("return_url",return_url);
	            parms.put ("body",reqData.getString("vcOrderNo"));
	            parms.put ("create_time",create_time);
	            parms.put ("subject",subject);
	            
	            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
	            logger.info("排序后{}",sourctxt1);
	            String pay_md5sign = Md5Util.md5(sourctxt1).toLowerCase();
	            parms.put ("sign",pay_md5sign);
	            logger.info("支付接口入参{}",parms);
	            String response = HttpClientTools.baseHttpSendPost(API_PAY_URL,parms);
	            logger.info("支付接口返参{}",response);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.FAILED);
                result.put("msg", "下单失败");
                return listener.failedHandler(result);
            }
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () ){
                return Constant.failedMsg ("获取连接为空");
            }
            if(!"000000".equals(payParams.getString("errorCode"))){
                String msg = payParams.containsKey ("errorMsg")?payParams.getString ("errorMsg"):"下单失败";
                return listener.failedHandler (Constant.failedMsg (msg));
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            //result.put("bankUrl",payParams.getString ("bankUrl"));
            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("mchInfo").getString("url")));
            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("mchInfo").getString("url")));
            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("mchInfo").getString("url")));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("peng支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("下单异常"));
        }
    }

    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://www.chundapay.com:9002/pay/zxPay.do";
            String key = "8e3a80fd895e020d9a39cb470094dbb8";
            
            String mch_id = "1000000161";
            String mch_order_no = (System.currentTimeMillis () + "").substring (0,13);
            String pay_type = "10001";
            String total_amount = "500";
            String notify_url = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String return_url = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String body = mch_order_no;
            String create_time = new Date().getTime()+"";
            String subject = "notebook";
            
            
            JSONObject parms = new JSONObject();
            parms.put ("mch_id",mch_id);
            parms.put ("mch_order_no",mch_order_no);
            parms.put ("pay_type",pay_type);
            parms.put ("total_amount", total_amount);
            parms.put ("notify_url", notify_url);
            parms.put ("return_url",return_url);
            parms.put ("body",body);
            parms.put ("create_time",create_time);
            parms.put ("subject",subject);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toLowerCase();
            parms.put ("sign",pay_md5sign);
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.baseHttpSendPost(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);
            //{"errorCode":"000000","errorMsg":"success","mchInfo":{"order_no":"15686490798787577511936","sign":"56795e4c3dfaf612ca49f771a9469b78","out_order_no":"1568621949721","url":"http://xd.nvlnpr.cn/wap/pay/gopaybank.php?order_id=LZ1315686219498757358"}}
        } catch (IOException e) {
            e.printStackTrace ();
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
