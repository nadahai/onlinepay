package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.sand.MD5;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Component
public class HuiJieScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (HuiJieScanServiceImpl.class);
    

    /**
     * @描述: 汇捷支付接口
     * 
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("汇捷支付接收入参{}", reqData);
            String fxid=StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String user_order_no = reqData.getString ("vcOrderNo");
            String fxattch = user_order_no;//附加信息
            String fxddh = user_order_no.substring(3, user_order_no.length());
            String fxdesc = "淘宝购物";
            String fxfee= reqData.getString ("amount");
            String fxnotifyurl = reqData.getString ("projectDomainUrl") + "/huiJieCallBackController";
            String fxbackurl = reqData.getString("projectDomainUrl")+"/success";
            String fxpay = "weixin";
            
            String token = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String singVal = fxid + fxddh + fxfee + fxnotifyurl + token;
            
            String fxsign = MD5.md5(singVal);
            logger.info("汇捷签名值="+singVal+"===md5==="+fxsign);
            String fxip = "127.0.0.1";//IP
            
            Map<String, String> prams = new HashMap<String, String>();
            prams.put("fxid",fxid);
            prams.put("fxddh",fxddh);
            prams.put("fxdesc",fxdesc);
            prams.put("fxfee",fxfee);
            prams.put("fxnotifyurl",fxnotifyurl);
            prams.put("fxbackurl",fxbackurl);
            prams.put("fxpay",fxpay);
            prams.put("fxattch",fxattch);
            prams.put("fxsign",fxsign);
            prams.put("fxip",fxip);
            
            logger.info("支付接口签名串{}",singVal);
            logger.info("支付接口入参{}",prams);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String response = HttpClientTools.doPostMethodWithUrl(API_PAY_URL, prams);
            
            logger.info("支付接口返参{}",response);
            if(StringUtils.isEmpty (response)){
                return listener.failedHandler (Constant.failedMsg ("下单为空"));
            }
            
            JSONObject payParams = Constant.stringToJson (response);
            if("0".equals(payParams.getString ("status"))){
                return listener.failedHandler (Constant.failedMsg ("下单失败="+payParams.containsKey ("error")));
            }
            if(payParams == null || payParams.isEmpty () || !payParams.containsKey ("payurl")){
                return listener.failedHandler (Constant.failedMsg ("获取连接为空"));
            }
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "获取链接成功");
            result.put ("bankUrl",payParams.getString ("payurl"));
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("汇捷支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String url = "http://www.xixib.cn/Pay";
            
            String token = "LjYZdOcinuQRWEqcraecAjgPHfRotxdm";
            String fxid = "2019170";
            String user_order_no = (System.currentTimeMillis () + "").substring (0,13);
            String fxddh = "41425026798413";
            String fxdesc = "淘宝购物";
            String fxfee= "300";
            System.out.println(fxfee);
            String fxnotifyurl = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String fxbackurl = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String fxpay = "weixin";//1支付宝，4微信
            String fxattch = user_order_no;//附加信息
            String singVal = fxid + fxddh + fxfee + fxnotifyurl + token;
            String fxsign = MD5.md5(singVal);
            String fxip = "127.0.0.1";//附加信息		
           
            Map<String, String> prams = new HashMap<String, String>();
            prams.put("fxid",fxid);
            prams.put("fxddh",fxddh);
            prams.put("fxdesc",fxdesc);
            prams.put("fxfee",fxfee);
            prams.put("fxnotifyurl",fxnotifyurl);
            prams.put("fxbackurl",fxbackurl);
            prams.put("fxpay",fxpay);
            prams.put("fxattch",fxattch);
            prams.put("fxsign",fxsign);
            prams.put("fxip",fxip);
            
            logger.info("支付接口入参{}",prams);
            String response = HttpClientTools.doPostMethodWithUrl(url, prams);
            logger.info("支付接口返参{}",response);

        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
