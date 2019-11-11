package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class XiongKongUnionServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(XiongKongUnionServiceImpl.class);

    /**
     * @描述:星空支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("星空支付交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String merchNo = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String backUrl = reqData.getString("projectDomainUrl")+"/gaoYangPayCallBackApi";
            BigDecimal amount = new BigDecimal(reqData.getString("amount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            String merchantId = StringUtils.deleteWhitespace(reqData.getString("merchantId"));

            JSONObject prams = new JSONObject();
            prams.put("command","cmd101");
            prams.put("serverCode","ser2001");
            prams.put("merchNo",merchNo);
            prams.put("version","2.0");
            prams.put("charset","utf-8");
            prams.put("currency","CNY");
            prams.put("reqIp","47.25.125.14");
            prams.put("reqTime",Constant.ymdhms.format(new Date ()));
            prams.put("signType","MD5");
            prams.put("payType","8");
            prams.put("cOrderNo",reqData.getString("vcOrderNo"));
            prams.put("amount",amount);
            prams.put("goodsName","深圳盛源网络科技有限公司");
            prams.put("goodsNum",1);
            prams.put("goodsDesc","深圳盛源网络科技有限公司");
            prams.put("memberId",2432434);
            prams.put("notifyUrl",backUrl);
            String sign = Md5CoreUtil.md5ascii(prams, key);
            prams.put("sign",sign);

            logger.info("星空支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("星空支付接口返参{}",response);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.FAILED);
                result.put("msg", "下单失败");
                return listener.failedHandler(result);
            }
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () ){
                return Constant.failedMsg ("获取连接为空");
            }
            if(!payParams.containsKey ("payUrl")){
                String msg = payParams.containsKey ("message")?payParams.getString ("message"):"下单失败";
                return Constant.failedMsg (msg);
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            result.put("bankUrl",payParams.getString ("payUrl"));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("星空支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("下单异常"));
        }
    }

    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://online.raaftu.cn:8088/onlinepay/gateway/epayapi";
            JSONObject prams = new JSONObject();
            prams.put("command","cmd101");
            prams.put("serverCode","ser2001");
            prams.put("merchNo","999941001031");
            prams.put("version","2.0");
            prams.put("charset","utf-8");
            prams.put("currency","CNY");
            prams.put("reqIp","47.25.125.14");
            prams.put("reqTime",Constant.ymdhms.format(new Date ()));
            prams.put("signType","MD5");
            prams.put("payType","8");
            prams.put("cOrderNo","1_"+System.currentTimeMillis ());
            prams.put("amount","12.23");
            prams.put("goodsName","深圳盛源网络科技有限公司");
            prams.put("goodsNum",1);
            prams.put("goodsDesc","深圳盛源网络科技有限公司");
            prams.put("memberId",2432434);
            prams.put("notifyUrl","http://www.baidu.com");
            String sign = Md5CoreUtil.md5ascii(prams, "77698D45126F734D2837A3A765AE2EEB");
            prams.put("sign",sign);
            logger.info("星空支付接口入参{}",prams);
            //"charset":"utf-8","amount":18.66,"sign":"9a2be0a8eb571bfbbe2c6afde4d856cf","reqTime":"20190409173045","version":"2.0","command":"cmd101","serverCode":"ser2001","reqIp":"47.25.125.14","payType":"8","merchNo":"999941001031","cOrderNo":"8_040917304522222","signType":"MD5","notifyUrl":"http://online.toxpay.com/xpay/gaoYangPayCallBackApi","currency":"CNY","goodsName":"深圳盛源网络科技有限公司","goodsNum":1,"goodsDesc":"深圳盛源网络科技有限公司"}
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("惠云支付接口返参{}",response);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
