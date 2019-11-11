package com.vc.onlinepay.pay.order.union;

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
public class GaoYangUnionServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(GaoYangUnionServiceImpl.class);

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
            JSONObject prams = new JSONObject();
            prams.put("serverCode","ser2001");
            prams.put("merchNo",merchNo);
            prams.put("version","2.0");
            prams.put("charset","utf-8");
            prams.put("currency","CNY");
            prams.put("reqIp","47.25.125.14");
            prams.put("reqTime",Constant.ymdhms.format(new Date ()));
            prams.put("signType","MD5");
            prams.put("payType","13");
            prams.put("cOrderNo",reqData.getString("vcOrderNo"));
            prams.put("amount",amount);
            prams.put("goodsName","深圳盛源网络科技有限公司");
            prams.put("goodsNum",1);
            prams.put("goodsDesc","深圳盛源网络科技有限公司");
            prams.put("memberId",merchantId);
            prams.put("notifyUrl",backUrl);
            String sign = Md5CoreUtil.md5ascii(prams, key);
            prams.put("sign",sign);
            logger.info("高阳科技接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("支付接口返参{}",response);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.FAILED);
                result.put("msg", "支付下单失败");
                return listener.failedHandler(result);
            }

            /*VcOnlineOrderMade made = new VcOnlineOrderMade ();
            made.setChannelId (reqData.getIntValue ("channelLabel"));
            made.setExpiredTime (300);
            made.setMerchNo (reqData.getString ("merchantNo"));
            made.setOrderNo (reqData.getString ("vcOrderNo"));
            made.setPaySource (reqData.getIntValue ("channelSource"));
            made.setTraAmount (reqData.getBigDecimal ("amount"));
            made.setUpMerchKey (key);
            made.setUpMerchNo (merchNo);
            made.setOpenType (10012);
            made.setOpenUrl (reqData.getString ("projectDomainUrl") + "/code/" + HiDesUtils.desEnCode (reqData.getString ("vcOrderNo")));
            made.setQrcodeUrl (API_PAY_URL);
            made.setRemarks (response);
            int r = onlineOrderMadeService.save(made);
            if(r<1){
                result.put("code", Constant.FAILED);
                result.put("msg", "下单失败");
                result.put("bankUrl", "");
                return listener.failedHandler(result);
            }*/
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            result.put("redirectHtml",response);
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
