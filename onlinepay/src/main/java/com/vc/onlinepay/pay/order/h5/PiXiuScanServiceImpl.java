package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Iterator;

@Service
public class PiXiuScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (PiXiuScanServiceImpl.class);
    

    /**
     * @描述:貔貅支付
     * @时间:2020年5月6日20:32:05
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("貔貅支付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String pay_memberid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String pay_notifyurl = reqData.getString ("projectDomainUrl") + "/piXiuCallBackController";
            //String pay_callbackurl = reqData.getString("projectDomainUrl")+"/success";
            String pay_amount= reqData.getString ("amount");
            JSONObject parms = new JSONObject();
            parms.put ("appid",pay_memberid);
            parms.put ("pay_type","ThreeAlipayCode");
            parms.put ("amount",new BigDecimal(pay_amount).setScale(2).toPlainString());
            parms.put ("callback_url", pay_notifyurl);
            parms.put ("out_trade_no",orderNo);
            parms.put ("version","v2.0");
            String signStr = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("貔貅支付排序后{}",signStr);
            String pay_md5sign = Md5Util.md5(signStr).toUpperCase();
            parms.put ("sign",pay_md5sign);

            logger.info("貔貅支付接口入参{}",parms);
            String payUrl = appendHtml(parms,API_PAY_URL).toString();
            result.put("redirectHtml", payUrl);
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "下单成功");
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("貔貅支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }

    public StringBuffer appendHtml(JSONObject jsonObject,String payUrl){
        try {
            if ( jsonObject == null) {
                return null;
            }
            StringBuffer bf = new StringBuffer();
            bf.append("<html><head><meta http-equiv=\"refresh\" content=\"5;\" /></head><body>");
            bf.append("<form id=\"pay_form\" action=\"").append(payUrl).append("\" method=\"post\">");
            bf.append("<h1>下单成功！页面跳转中...</h1>");
            Iterator<String> item = jsonObject.keySet().iterator();
            while (item.hasNext()) {
                String key = item.next();
                String value = jsonObject.getString(key);
                bf.append("<input type=\"hidden\" name=\""+key+"\" id=\""+key+"\" value=\"").append(value).append(("\" />"));
            }
            bf.append("</form><script type=\"text/javascript\">document.getElementById(\"pay_form\").submit();</script></body></html>");
            return bf;
        } catch (Exception e) {
            return null;
        }
    }

    public static StringBuffer appendHtml1(JSONObject jsonObject,String payUrl){
        try {
            if ( jsonObject == null) {
                return null;
            }
            StringBuffer bf = new StringBuffer();
            bf.append("<html><head><meta http-equiv=\"refresh\" content=\"5;\" /></head><body>");
            bf.append("<form id=\"pay_form\" action=\"").append(payUrl).append("\" method=\"post\">");
            bf.append("<h1>下单成功！页面跳转中...</h1>");
            Iterator<String> item = jsonObject.keySet().iterator();
            while (item.hasNext()) {
                String key = item.next();
                String value = jsonObject.getString(key);
                bf.append("<input type=\"hidden\" name=\""+key+"\" id=\""+key+"\" value=\"").append(value).append(("\" />"));
            }
            bf.append("</form><script type=\"text/javascript\">document.getElementById(\"pay_form\").submit();</script></body></html>");
            return bf;
        } catch (Exception e) {
            return null;
        }
    }


    public static void main (String[] args) {
        System.out.println(new BigDecimal("10").setScale(2).toPlainString());
        try {
            String API_PAY_URL = "https://apipay.52jpp.com/index/unifiedorder";
            String key = "sYzk4NaheTo7xEZSXOlw4zEOxBQKtj9K";
            String pay_notifyurl = "http://127.0.0.1/test";

            JSONObject parms = new JSONObject();
            parms.put ("appid","1039106");
            parms.put ("pay_type","ThreeAlipayCode");
            parms.put ("amount","10.00");
            parms.put ("callback_url", pay_notifyurl);
            parms.put ("out_trade_no","sc202005062112666666");
            parms.put ("version","v2.0");

            String signStr = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",signStr);
            String pay_md5sign = Md5Util.md5(signStr).toUpperCase();
            parms.put ("sign",pay_md5sign);
            logger.info("支付接口入参{}",parms);
            String url = appendHtml1(parms,API_PAY_URL).toString();
            System.out.println(url);
//            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
//            logger.info("支付接口返参{}",response);


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
