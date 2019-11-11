package com.vc.onlinepay.pay.order.scan;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;

@Service
@Component
public class QuickPayScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (QuickPayScanServiceImpl.class);
    private static String url = "https://pay.rqust.com/Pay_Index.html";

    /**
     * @描述:速付交易
     * @作者:lihai
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            String vcOrderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", vcOrderNo);
            String merno = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String urlCallback = reqData.getString ("projectDomainUrl") + "/quickPayCallbackApi";
            String returnUrl = reqData.getString("projectDomainUrl") + "/success";
            BigDecimal amount = new BigDecimal(reqData.getString("amount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            
            JSONObject parms = new JSONObject();
            parms.put("pay_memberid",merno);
            parms.put("pay_orderid",vcOrderNo);
            parms.put("pay_amount",String.valueOf(amount));
            parms.put("pay_applydate", Constant.yyyyMMdd.format(new Date()));
            parms.put("pay_bankcode","yh");//支付宝用ZFB 其他用yh
            parms.put("pay_notifyurl", urlCallback);
            parms.put("pay_callbackurl", returnUrl);
            String hmacstr = Md5Util.md5("pay_amount=>"+parms.getString("pay_amount")+"&pay_applydate=>"+parms.getString("pay_applydate")+"&pay_bankcode=>"+parms.getString("pay_bankcode")+"&pay_callbackurl=>"+parms.getString("pay_callbackurl")+"&pay_memberid=>"+parms.getString("pay_memberid")+"&pay_notifyurl=>"+parms.getString("pay_notifyurl")+"&pay_orderid=>"+parms.getString("pay_orderid")+"&key="+key);
            parms.put("pay_md5sign",hmacstr.toUpperCase());
            //如：微信：WX  支付宝：ZFB 快捷：KJ 云闪付：YSF  网银：WY  京东：JD 百度钱包：BD 虚拟货币：XN
            parms.put("tongdao","YSF");
            parms.put("pay_reserved1",vcOrderNo);
            parms.put("pay_reserved2",vcOrderNo);
            parms.put("pay_reserved3",vcOrderNo);
            parms.put("pay_productname",reqData.getString("goodsName"));
            parms.put("pay_productnum","1");
            parms.put("pay_productdesc","收款");
            
            logger.info("速付接口入参{}",parms);
            /*String respMsg = HttpClientTools.httpSendPostFrom(url,parms);
            logger.info("速付支付响应{}",respMsg);
            if(StringUtil.isEmpty(respMsg)) {
                result.put("code", Constant.FAILED);
                result.put("msg", "扫码支付获取链接为空");
                return listener.failedHandler(result);
            }
            JSONObject resJson = JSONObject.parseObject(respMsg);
            if(null==resJson||!resJson.containsKey("code") ||!Constant.SUCCESSS.equals(resJson.getString("code"))){
                result.put("code", Constant.FAILED);
                result.put("msg", null !=resJson && resJson.containsKey("msg")?resJson.getString("msg"):"下单失败");
                return listener.failedHandler(result);
            }
            String payUrl = resJson.getString("bankUrl");
            result.put("code", Constant.SUCCESSS);
            result.put("bankUrl", payUrl);
            result.put("redirectUrl", payUrl);
            result.put("msg", "下单成功");
            return listener.successHandler(result);*/
            result.put("actionUrl", url);
            result.put("code", Constant.SUCCESSS);
            result.put("viewPath","auto/autoSubmit");
            result.put("data",parms);
            result.put("msg", "下单成功");
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error ("速付扫码下单异常", e);
            result.put ("code", Constant.ERROR);
            result.put ("msg", "支付处理异常");
            return listener.paddingHandler (result);
        }
    }
}
