package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
@Component

public class ChunPayScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(ChunPayScanServiceImpl.class);
    /**
     * @描述:通付交易
     * @作者:lihai
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            String orderId = reqData.getString("vcOrderNo");
            result.put("orderNo",orderId);
            String merch   = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String channelDesKey  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String api_url   = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));

            String md5Key  = channelDesKey;
            if(channelDesKey.contains("###")){
                md5Key  = Constant.getChannelKeyDes(channelDesKey,0);
                api_url = Constant.getChannelKeyDes(channelDesKey,1);
            }

            String notifyUrl = reqData.getString("projectDomainUrl") + "/tongbaoPayCallbackApi";
            String returnUrl = reqData.getString("projectDomainUrl") + "/success";

            JSONObject parms = new JSONObject();
            parms.put("merchantId",merch);
            parms.put("orderId",orderId);
            parms.put("amount",reqData.getString("amount"));
            parms.put("orderTime",Constant.getDateString());
            parms.put("requestIp",reqData.getString("ipaddress"));
            parms.put("goodsName",reqData.getString("goodsName"));
            parms.put("goodsDesc","收款");
            parms.put("notifyUrl", notifyUrl);
            parms.put("returnUrl", returnUrl);
            parms.put("openId", "1");
            parms.put("payType","42");
            String sign = Md5CoreUtil.md5ascii(parms,md5Key).toUpperCase();
            parms.put("sign", sign);
            logger.info("通付企业H5扫码接口入参{}",parms);
            String respMsg = HttpClientTools.httpSendPostFrom(api_url,parms);
            logger.info("通付企业扫码支付响应{}",respMsg);
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
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("通付扫码下单异常", e);
            result.put("code", Constant.ERROR);
            result.put("msg", "支付处理异常");
            return listener.paddingHandler(result);
        }
    }
}
