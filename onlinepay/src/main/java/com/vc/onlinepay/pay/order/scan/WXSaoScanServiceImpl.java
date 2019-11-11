package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class WXSaoScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (WXSaoScanServiceImpl.class);
    
    /**
     * @描述:跑分通道
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("跑分通道接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String uid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String notify_url = reqData.getString ("projectDomainUrl") + "/wXSaoCallBackController";
            String return_url = reqData.getString("projectDomainUrl")+"/success";
            String price = reqData.getString ("amount");
            String istype = "1";
            Integer type = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
            String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
            //payType 1:微信 2:支付宝 5:qq支付
            if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
            	istype = "2";
            }
            String orderid = orderNo;
            String attach = orderid;
            String sourctxt1 = istype + notify_url + orderid + price + return_url + key + uid;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1);
            
            String prm = "uid="+uid+"&price="+price+"&istype="+istype+"&notify_url="+notify_url+"&return_url="+return_url+"&orderid="+orderid+"&attach="+attach+"&key="+sign;
            logger.info("支付接口入参{}",prm);
            String response = HttpClientTools.sendGet(API_PAY_URL,prm);
            logger.info("支付接口返参{}",response);
	         JSONObject payParams = Constant.stringToJson (response);
	         if(payParams == null || payParams.isEmpty()){
	        	 return listener.failedHandler (Constant.failedMsg("获取连接为空"));
	         }
             if(!"200".equals(payParams.getString("code")) && !payParams.getJSONObject("body").containsKey ("pay_url")){
                String msg = payParams.containsKey("msg")?payParams.getString("msg"):"获取连接为空";
                return listener.failedHandler (Constant.failedMsg (msg));
             }
	         result.put ("code", Constant.SUCCESSS);
	         result.put ("msg", payParams.containsKey("msg")?payParams.getString("msg"):"获取链接成功");
	         
	         result.put ("bankUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("body").getString ("payurl")));
	         result.put ("redirectUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("body").getString ("payurl")));
	         result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(payParams.getJSONObject("body").getString ("payurl")));
	         
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("跑分下单下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
        	String API_PAY_URL = "http://api.winpay.live/payapi/applyOrder";
            String key = "B8886043E72B0448";
            
            String uid = "1012";
            String price="100";
            String istype = "1";
            String notify_url="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String return_url="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String orderid = (System.currentTimeMillis () + "").substring (0,13);
            String attach = orderid;
            String sourctxt1 = istype + notify_url + orderid + price + return_url + key + uid;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1);
            
            String prm = "uid="+uid+"&price="+price+"&istype="+istype+"&notify_url="+notify_url+"&return_url="+return_url+"&orderid="+orderid+"&attach="+attach+"&key="+sign;
            logger.info("支付接口入参{}",prm);
            String response = HttpClientTools.sendGet(API_PAY_URL,prm);
            logger.info("支付接口返参{}",response);
            
            //{"msg":"请求成功","body":{"orderid":"T613488594325078016","payurl":"http://api.winpay.live/payapi/gopay?orderid=T613488594325078016","orderexpiretime":"298000","qrcodebase64":"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAYAAACtWK6eAAAJFklEQVR4nO2USZIDRwwD5/+fHl98sqUSiwCXHiUi+sYiwSX75+fn53fD90pbfESk9KT07tTETjt8iN+4gVWDch6XO87pV6np3GmHD/EbN7BqUM7jcsc5/So1nTvt8CF+4wZWDcp5XO44p1+lpnOnHT7Er27g7+QcZjS/clyVB1Lxtnqnzlm68ysCEACxCEAGjETilPwA4hOADBiJxCn5AcQnABkwEolT8gOITwASe2hddLau2++3HOumWU54u8i3w0i2rtsvgHyOA5ABI9m6br8A8jkOQAaMZOu6/QLI5zgAGTCSrev2CyCf4wBkwEi1lJrVi1ZUXWPz3ADEqM2LVgQgAGLR5kUrAhAAsWjzohUBCIBYtHnRigAEQI6agO1dXfNibIcZremOAxAAAZCCHgAEQFK5st6iNd1xAAIgAFLQA4AASCpX1lu0pjvuawBxyzmA7Lu/fIQT38Tc3AKQpYsGkNw83AKQpYsGkNw83AKQpYsGkNw83AKQpYsGkNw83AoBsmnAW44Qb/PeBr9xA1+1aLwBSOr7lkXjDUBS37csGm8Akvq+ZdF4exggLx0uUWSYkXddb53a4iMq5Ue4WasdA8i8j6gAZEAAMu8jKgAZEIDM+4gKQAYEIPM+ovpqQKLNRweixG0Z+BYAnTNS9pytW51f7muiCQABEEfNlr4mmgAQAHHUbOlrogkAARBHzZa+sskUc1k5weqAq2P5kbpTft3AVXt7GZdtShl6VgCSqzvldxoMAAGQUN0pv9NgAAiAhOpO+Z0GA0AAJFR3yu80GI8ARDGXbqoBJKePqLJ+Ow7OqQ4ow2+dBZQBON8BSO4dgADIxzgAARAAOcQBCIAAyCEOQADkP1+/kWw+JZci5yE9bW7Ve1gGA4BkBCAA8ucXrQhAAOTPL1oRgADIn1+0IgD5YkDC3SblHLp7gUoPmZibOLdfpzfnYVbnl9++jDQKQHJxbr8AAiAAcqgBIAACIIcaAAIgAHKoASDpt/PHddPY1iNUfHQcRLWPrKr7VP0CiKkvxQeAAMj4oqv7UnwACICML7q6L8UHgADI+KKr+1J8AMgfBEQxkh2KM5d7mB1QZr1sUvUPDkBMuQBkRgBifAsgHi+bBCDGtwDi8bJJAGJ8CyAeL5v0JwHpOMJsY9Ga7qFX5lJ7qN6pEhdRx8/mYsYA4qjhzAUgAJKuITQKIB98dMRFBCBCDaFRAPngoyMuIgARagiNAsgHHx1xEa0CpLqAZK54SIqqe1d8TNTM5nPv2d7rhBEAAZAb/zcCkCUCEAApMwIgAHLj/0YFveaW2mHO6UOJm/CmyAlvtQ/lZprAB5CN3hQBCIAAyKUXAEn7BZCN3hQBCIAAyKUXAEn6dTalGEk3sGjATr9bIK/20XLkSr5sAQCp9QsgAAIgAAIg0SaczQNIrV+nDwAJNuFsHkBq/Tp9PA4Qd1HnsbrVsYjq5Ufi3PkVbdlpOC5bFEAAJKMtOwWQZF/KWwC5r1GdH0AEAUguv6ItOwWQZF/KWwC5r1Gd33CX/cN0Ho4TwIqFReSE7VUPHTOagNf9U3oTCyAAcucj+jYSt2VGhxoAAiB3PqJvI3FbZnSoASAAcucj+jYSt2VGhxoAAiB3PqJvI3FbZnSoMX8gB3OlzStxWf/KPLLqmJvTi5KrwBuAZOKy/pV5ZAUgkjcAycRl/SvzyApAJG8AkonL+lfmkRWASN4AJBOX9a/MIysAkbzVFlDigg2MLD+b3/1l51Y9j2gNJb9zRoe6AAIg/nkASLAJJS7yDkByc6ueB4AEm1DiIu8AJDe36nkASLAJJS7yDkByc6uex58BJP1waOjOmh09ZOX01vET2Tyj6NuXX7U5AMkJQD4LQJYsdfPyAQRAxpe6efkAAiDjS928fAApBiQ7OLFo6ef2m33b4SObvyMu8lbpoXp/AAIgpXGRt0oPAAIgAHKoCSAAAiCHmi2AhB0m5Rz6JriqtRlep9+O/Un9hyOTApCcAARAruMABEAABEB+f38BBEAScQACIO2AbBlc9UC2gOV+q+Sr9lZdU9HFLQEIgNR4q66pCEAA5FoA8jIOQACkxlt1TUUAAiDXApCXcbmDU+Kib7fA5fQ/0bvSZ3UPUbn7v6gLII4eAARAAMTgf6J3pU8AARBLDwACIABi8D/Ru9IngBiTvSlgW+KUqo9V8eFURw+Rmoq3gh8JgHwSgAAIgBwEIAACIAcBCIAAyEEAAiCtX/UwlQE787l9ZN927Crbg5LfPbc3H4AACIAACIAACIDUDDibz+0DQADkekgdA87mc/sAkAWAOJt3G1bkPpKtBzf1KT1U96QIQAAEQM5eAARAAOTgBUAABEAOXgAEQADk4MW3QPdBTBx09q1bzll21Izmc+7KfW9v4gAEQPSa0XwAAiAWAQiApPIByP3bjprRfAACIBYByIMAcR5v14FllfU2BfSTam76Lu5hx+C2KOvtacc6UXPTd3EPOwa3RVlvTzvWiZqbvot72DG4Lcp6e9qxTtTc9IXvIRw5IGujxVBOHWG2htub00dU7nt4+UkOiwUgAHISgAAIgBwEIAACIAcBCIAAyEEtgLgX5hyws1FlwJG4juNS+u+eR9Rbhw9FAAIgJfOIeuvwoQhAAKRkHlFvHT4UAQiAlMwj6q3DhyIAAZCSeUS9dfhQ9D9AOvQ0QLJ1t8j9k3LOfFNfb2oACIAAyKEGgAAIgBxqAAiAAMihBoAACIAcauQOqeMwJ5bq/Ny9ZzVR88bL8j0AyNLFAMiOPQDI0sUAyI49AMjSxQDIjj0AyNLFAMiOPTwbEDc0ytvKXEr/m7xV76rAI4AASJ83AAGQ8SPs7hNAACTlzZkLQADEVtc99Kw3Zy4AAZBUXUWDQy89puo+lbpb7+jwFkAA5K5Ppe7WOwKQYh8AUju3qb4ABECu+1Tqbr0jACn2ASC1c5vqKwSIW9mhuwFRVL1UxYfLf0df7poFfQFIRgDS68PdA4AUC0B6fbh7AJBiAUivD3cPAFIsAOn14e4hDcjUVz3MjgE7D+lp3pS4rKrz/1tjHg4Aeb43JS4rAAkMBEB2eFPisgKQwEAAZIc3JS4rAAkMBEB2eFPisuoA5B+O2DlNoN0VbwAAAABJRU5ErkJggg==","qrcodeurl":"wxp://f2f0lfeQlu95ZyIAS-0EfstfWpVSeo4kD72Q","status":0},"code":200}

        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
