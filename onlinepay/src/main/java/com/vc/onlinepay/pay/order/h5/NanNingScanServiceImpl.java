package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class NanNingScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (NanNingScanServiceImpl.class);
    

    /**
     * @描述:南宁支付宝H5
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("南宁支付宝H5接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String appid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String notifyurl = reqData.getString ("projectDomainUrl") + "/nanNingCallBackController";
            String backurl = reqData.getString("projectDomainUrl")+"/success";
            BigDecimal total_fee = new BigDecimal (reqData.getString ("amount")).setScale (2, BigDecimal.ROUND_HALF_DOWN);
            String out_trade_no = orderNo;
            String desc=out_trade_no;
            String pay="1";
            
            
            JSONObject parms = new JSONObject();
            parms.put ("appid",appid);
            parms.put ("total_fee",reqData.getString ("amount"));
            parms.put ("out_trade_no",out_trade_no);
            parms.put ("desc", desc);
            parms.put ("notifyurl", notifyurl);
            parms.put ("backurl",backurl);
            parms.put ("pay",pay);
            
            String signStr = "appid="+appid+"&total_fee="+total_fee+"&desc="+desc+"&notifyurl="+notifyurl+"&out_trade_no="+out_trade_no+"&pay="+pay+"&key="+key;
            logger.info("排序后{}",signStr);
            String sign = Md5Util.md5(signStr);
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            result.put("actionUrl", API_PAY_URL);
            result.put("code", Constant.SUCCESSS);
            result.put("viewPath","auto/autoSubmit");
            //result.put("redirectActionHtml","auto/autoSubmit");
            result.put("data",parms);
            result.put("msg", "下单成功");
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("南宁支付宝H5下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://app.womimo.cn/lrpay/codepay.php";
            String key = "676021d15a71d30cd797de5af1b10198";
            
            String appid = "19066353";
            String total_fee="100.00";
            String out_trade_no = (System.currentTimeMillis () + "").substring (0,13);
            String desc=out_trade_no;
            String notifyurl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String backurl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String pay="1";
            
            
            JSONObject parms = new JSONObject();
            parms.put ("appid",appid);
            parms.put ("total_fee",total_fee);
            parms.put ("out_trade_no",out_trade_no);
            parms.put ("desc", desc);
            parms.put ("notifyurl", notifyurl);
            parms.put ("backurl",backurl);
            parms.put ("pay",pay);
            
            String signStr = "appid="+appid+"&total_fee="+total_fee+"&desc="+desc+"&notifyurl="+notifyurl+"&out_trade_no="+out_trade_no+"&pay="+pay+"&key="+key;
            logger.info("排序后{}",signStr);
            String sign = Md5Util.md5(signStr);
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);

           
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
