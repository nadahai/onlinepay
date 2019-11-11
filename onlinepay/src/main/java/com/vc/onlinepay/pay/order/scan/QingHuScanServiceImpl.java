package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import java.util.Date;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class QingHuScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (QingHuScanServiceImpl.class);
    

    /**
     * @描述:深圳清湖支付宝个码
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("深圳清湖支付宝个码接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String lsh= orderNo;
            String money= reqData.getString ("amount");
            String user = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String time=Constant.yyyyMMdd.format(new Date ());
            String type = "2";
            String okreurl = reqData.getString ("projectDomainUrl") + "/qingHuCallBackController";
            String reurl = reqData.getString("projectDomainUrl")+"/success";
            
            String sourctxt1 = lsh+money+user+time+type+reurl+okreurl+key; 
            logger.info("排序后{}",sourctxt1);
            String ch = Md5Util.md5(sourctxt1).toLowerCase();
            
            JSONObject prams = new JSONObject ();
            prams.put ("lsh", lsh);
            prams.put ("money", money);
            prams.put ("type",type);
            prams.put ("user",user);
            prams.put ("time",time);
            prams.put ("reurl",reurl);
            prams.put ("okreurl",okreurl);
            prams.put ("ch", ch);
            
            JSONObject prams2 = new JSONObject ();
            prams2.put ("data", prams);
            
	            
	         	logger.info("支付接口入参{}",prams2);
	         	
	            String response = HttpClientTools.httpSendHTTPSPostFrom(API_PAY_URL,prams2);
	            logger.info("支付接口返参{}",response);
	            if(StringUtils.isEmpty (response)){
	                return listener.failedHandler (Constant.failedMsg ("下单为空"));
	            }
	            JSONObject payParams = Constant.stringToJson (response);
	            if(payParams == null || payParams.isEmpty () || !payParams.containsKey ("url")){
	                return listener.failedHandler (Constant.failedMsg ("获取连接为空"));
	            }
				
	            result.put ("code", Constant.SUCCESSS);
	            result.put ("msg", "获取链接成功");
	            result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getString("url")));
	            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getString("url")));
	            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getString("url")));
	            
	            /***
	         	result.put("actionUrl", API_PAY_URL);
	            result.put("code", Constant.SUCCESSS);
	            result.put("viewPath","auto/autoSubmitQingHu");
	            //result.put("redirectActionHtml","auto/autoSubmit");
	            result.put("data",prams);
	            result.put("msg", "下单成功");
	            **/
	            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("深圳清湖支付宝个码下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "https://dfbbpay.com/api2.php?type=add";
            String key = "EN2CT6W2KPBPFQRMT4D8";
            
            String lsh=(System.currentTimeMillis () + "").substring (0,13);
            String money="100";
            String user="26920190806";
            
            String time=Constant.yyyyMMdd.format(new Date ());
            String type = "2";
            String reurl="https://www.baidu.com/";
            String okreurl="https://www.baidu.com/";
            
            String sourctxt1 = lsh+money+user+time+type+reurl+okreurl+key; 
            logger.info("排序后{}",sourctxt1);
            String ch = Md5Util.md5(sourctxt1);
            
            
            JSONObject prams = new JSONObject ();
            prams.put ("lsh", lsh);
            prams.put ("money", money);
            prams.put ("user",user);
            prams.put ("time",time);
            prams.put ("type",type);
            prams.put ("reurl",reurl);
            prams.put ("okreurl",okreurl);
            prams.put ("ch", ch);
            JSONObject prams2 = new JSONObject ();
            prams2.put ("data", prams);
            
            logger.info("支付接口入参{}",prams2);
            String response = HttpClientTools.httpSendHTTPSPostFrom(API_PAY_URL,prams2);
            logger.info("支付接口返参{}",response);
            
           
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
