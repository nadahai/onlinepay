package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@Service
@Component
public class GaoYangZuyongServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(GaoYangZuyongServiceImpl.class);

    /**
     * @描述:高阳科技交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("高阳科技入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String merchNo = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String backUrl = reqData.getString("projectDomainUrl")+"/gaoYangPayCallBackApi";
            BigDecimal amount = new BigDecimal(reqData.getString("amount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            String merchantId = StringUtils.deleteWhitespace(reqData.getString("merchantId"));

            String payWay = "";
            Integer type = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
            String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
            if (type == 2 || type == 10 || Constant.service_alipay.equals (service) || type == 22) {
                payWay = "10";
            }else{
                payWay = "12";
            }
            JSONObject prams = new JSONObject();
            prams.put("command","cmd102");
            prams.put("serverCode","ser2001");
            prams.put("merchNo",merchNo);
            prams.put("version","2.0");
            prams.put("charset","utf-8");
            prams.put("currency","CNY");
            prams.put("reqIp","47.25.125.14");
            prams.put("reqTime",Constant.ymdhms.format(new Date ()));
            prams.put("signType","MD5");
            prams.put("payType",payWay);
            prams.put("cOrderNo",reqData.getString("vcOrderNo"));
            prams.put("amount",amount);
            prams.put("goodsName","深圳盛源网络科技有限公司");
            prams.put("goodsNum",1);
            prams.put("goodsDesc","深圳盛源网络科技有限公司");
            prams.put("memberId",2432434);
            prams.put("notifyUrl",backUrl);
            String sign = Md5CoreUtil.md5ascii(prams, key);
            prams.put("sign",sign);
            logger.info("高阳科技接口入参{}",prams);
            String res = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("支付接口返参{}",res);
            if(StringUtils.isBlank(res)){
                result.put("code", Constant.FAILED);
                result.put("msg", "支付下单失败");
                return listener.failedHandler(result);
            }
            JSONObject response = Constant.stringToJson(res);
            if(response == null || response.isEmpty () ){
                return Constant.failedMsg ("获取连接为空");
            }
            if(!response.containsKey ("payUrl")){
                String msg = response.containsKey ("message")?response.getString ("message"):"下单失败";
                return Constant.failedMsg (msg);
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            result.put("redirectUrl",response.getString("payUrl"));
            result.put("bankUrl",response.getString("payUrl"));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("高阳科技下单异常", e);
            result.put("code", Constant.ERROR);
            result.put("msg", "处理异常");
            return listener.paddingHandler(result);
        }
    }

    public static void main (String[] args) {
        try {
            String API_PAY_URL = "http://gateway.7986sun.com/onlinepay/gateway/sunpayapi";
            JSONObject prams = new JSONObject();
            prams.put("serverCode","ser2001");
            prams.put("merchNo","666841000003");
            prams.put("version","2.0");
            prams.put("charset","utf-8");
            prams.put("currency","CNY");
            prams.put("reqIp","47.25.125.14");
            prams.put("reqTime",Constant.ymdhms.format(new Date ()));
            prams.put("signType","MD5");
            prams.put("payType","13");
            prams.put("cOrderNo",System.currentTimeMillis ());
            prams.put("amount","12.23");
            prams.put("goodsName","深圳盛源网络科技有限公司");
            prams.put("goodsNum",1);
            prams.put("goodsDesc","深圳盛源网络科技有限公司");
            prams.put("memberId",2432434);
            prams.put("notifyUrl","http://www.baidu.com");
            String sign = Md5CoreUtil.md5ascii(prams, "D1851AC4AE080042D140A1627504E038");
            prams.put("sign",sign);
            logger.info("高阳科技接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("支付接口返参{}",response);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
