package com.vc.onlinepay.pay.order.bakup;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.channel.ChannelSubNo;
import com.vc.onlinepay.persistent.service.channel.ChannelSubNoServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Component
public class ZuYongPDD3ScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(ZuYongPDD3ScanServiceImpl.class);
    /**
     * @描述:自研拼多多租用系统
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("自研拼多多租用系统入参{}",reqData);
        	String orderNo = reqData.getString("vcOrderNo");
            String amount = reqData.getString("amount");
            String merchantNo = reqData.getString("merchantNo");
            String serviceCallbackUrl = reqData.getString("serviceCallbackUrl");
            String merchNo = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String merchkey = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String returnUrl = reqData.getString("projectDomainUrl").concat("/success");
            String payUrl  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String backUrl = reqData.getString("projectDomainUrl").concat("/zuYongCallBackController");
            result.put("orderNo",orderNo);
            int tradAmount = new BigDecimal(amount).intValue();
            if(!serviceCallbackUrl.isEmpty() && !serviceCallbackUrl.contains(","+tradAmount+",")){
				  return listener.failedHandler(Constant.failedMsg("金额与通道不符合"));
            }
            JSONObject prams = new JSONObject();
            prams.put("reqCmd","req.trade.order");
            prams.put("merchNo",merchNo);
            prams.put("charset","UTF-8");
            prams.put("signType","MD5");
            prams.put("reqIp","127.0.0.2");
            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
            String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
            String payType = "12";
            if (type == 2 || type == 10 || type==22 || Constant.service_alipay.equals(service)) {
	        	 payType = "10";
            }
            prams.put("payType",payType);
            prams.put("tradeNo",orderNo);
            prams.put("currency","CNY");
            prams.put("amount",amount);
            prams.put("userId",orderNo);
            prams.put("notifyUrl",backUrl);
            prams.put("returnUrl",returnUrl);
            prams.put("goodsName",merchantNo);
            prams.put("goodsDesc",merchantNo);
            String sign = Md5CoreUtil.md5ascii(prams,merchkey);
            prams.put("sign",sign);
            logger.info("自研拼多多租用系统下单:{},入参:{}",orderNo,prams);
            String response = HttpClientTools.httpSendPostFrom(payUrl,prams);
            logger.info("自研拼多多租用系统下单:{},返参:{}",orderNo,response);
            if(StringUtils.isBlank(response)){
                return listener.failedHandler(Constant.failedMsg("下单响应为空"));
            }
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty()){
                return Constant.failedMsg ("下单解析为空");
            }
            if(!payParams.containsKey("bankUrl")){
                String msg = payParams.containsKey("msg")?payParams.getString("msg"):"下单失败";
                return listener.failedHandler (Constant.failedMsg (msg));
            }
            String bankUrl = StringEscapeUtils.unescapeJava(payParams.getString ("bankUrl"));
            //channelSubNoServiceImpl.updateLastOrderTime(new ChannelSubNo(merchNo));
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            result.put ("bankUrl",bankUrl);
            result.put ("redirectUrl",bankUrl);
            result.put ("qrCodeUrl",bankUrl);
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("自研拼多多租用系统下单异常", e);
            return listener.failedHandler (Constant.failedMsg("下单异常"));
        }
    }
}
