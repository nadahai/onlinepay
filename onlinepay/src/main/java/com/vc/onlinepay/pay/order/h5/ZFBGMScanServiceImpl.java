package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Component
public class ZFBGMScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (ZFBGMScanServiceImpl.class);
    

    /**
     * @描述:个码通道
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("个码通道接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String pid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String notify_url = reqData.getString ("projectDomainUrl") + "/zFBGMCallBackController";
            String return_url = reqData.getString("projectDomainUrl")+"/success";
            String money = reqData.getString("amount");
            String out_trade_no = orderNo;

            String version = "1.0";

            String sign_type = "md5";

            String type = "alipay2";
            String name = "abc";
            String out_user_id = out_trade_no;

            JSONObject prams = new JSONObject();
            prams.put("version",version);
            prams.put("pid",pid);
            prams.put("notify_url",notify_url);
            prams.put("return_url",return_url);
            prams.put("money",money);
            prams.put("type",type);
            prams.put("name",name);
            prams.put("out_trade_no",out_trade_no);
            prams.put("out_user_id",out_user_id);

            String sourctxt1 = Md5CoreUtil.getSignStr(prams)+""+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            prams.put("sign",sign);
            prams.put("sign_type",sign_type);
            
            logger.info("支付接口入参{}",prams);
            result.put("actionUrl", API_PAY_URL);
            result.put("code", Constant.SUCCESSS);
            result.put("viewPath","auto/autoSubmit");
            result.put("data",prams);
            result.put("msg", "下单成功");
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("个码通道下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://iscrfp.xlyada.com/s7vTt0L0h3ugmzJrntEo5O9EixWRKSaE";
            String key = "6M67Tm7tmY1Nd6Hm9h166nmD7H097QMM";

            String version = "1.0";
            String pid = "192";
            String sign_type = "md5";
            String notify_url = "http://www.baidu.com";
            String return_url = "http://www.baidu.com";
            String money = "300";
            String type = "alipay2";
            String name = "abc";
            String out_trade_no = System.currentTimeMillis () + "";
            String out_user_id = out_trade_no;

            JSONObject prams = new JSONObject();
            prams.put("version",version);
            prams.put("pid",pid);
            prams.put("notify_url",notify_url);
            prams.put("return_url",return_url);
            prams.put("money",money);
            prams.put("type",type);
            prams.put("name",name);
            prams.put("out_trade_no",out_trade_no);
            prams.put("out_user_id",out_user_id);


            String sourctxt1 = Md5CoreUtil.getSignStr(prams)+""+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            prams.put("sign",sign);
            prams.put("sign_type",sign_type);
            logger.info("个码通道支付接口入参{}",prams);
            //"charset":"utf-8","amount":18.66,"sign":"9a2be0a8eb571bfbbe2c6afde4d856cf","reqTime":"20190409173045","version":"2.0","command":"cmd101","serverCode":"ser2001","reqIp":"47.25.125.14","payType":"8","merchNo":"999941001031","cOrderNo":"8_040917304522222","signType":"MD5","notifyUrl":"http://online.toxpay.com/xpay/gaoYangPayCallBackApi","currency":"CNY","goodsName":"深圳盛源网络科技有限公司","goodsNum":1,"goodsDesc":"深圳盛源网络科技有限公司"}
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("个码支付接口返参{}",response);

           
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
