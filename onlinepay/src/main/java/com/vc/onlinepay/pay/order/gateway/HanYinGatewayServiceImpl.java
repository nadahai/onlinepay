package com.vc.onlinepay.pay.order.gateway;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Component
public class HanYinGatewayServiceImpl  {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @描述:商讯通网关支付
     * @时间:2018年1月22日 下午3:22:51
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo", reqData.getString("vcOrderNo"));
//            logger.error("商讯通网关接口入参{}", reqData);
            // 请求地址
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String channelKeyDes = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String api_url   = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String insCode = Constant.getChannelKeyDes(channelKeyDes,0);
            String insMerchantCode = Constant.getChannelKeyDes(channelKeyDes,1);
            String signKey = Constant.getChannelKeyDes(channelKeyDes,2);

            int amount = new BigDecimal(reqData.getString("amount")).multiply(new BigDecimal("100")).intValue();
            String notifyUrl = reqData.getString("projectDomainUrl")+"/hanyingPayCallBackApi"; // 异步通知地址

            String frontUrl = reqData.getString("projectDomainUrl")+"/success";

            JSONObject detail = new JSONObject();
            detail.put("orderNo", reqData.getString("vcOrderNo"));
            detail.put("goods", reqData.getString("goodsName"));
            detail.put("amount",  reqData.getString("amount"));
            detail.put("orderTime", DateUtils.getTimeForY_M_D_H_m_s());

            JSONObject prams = new JSONObject();
            prams.put("insCode",insCode);
            prams.put("insMerchantCode",insMerchantCode);
            prams.put("hpMerCode",channelKey);
            prams.put("orderNo",reqData.getString("vcOrderNo"));
            prams.put("orderTime",Constant.getDateString());
            prams.put("currencyCode","156");
            prams.put("orderAmount",String.valueOf(amount));//单位：分
            prams.put("name", "");
            prams.put("idNumber", "");
            prams.put("accNo", "");
            prams.put("telNo", "");
            prams.put("productType","100000");
            prams.put("paymentType","2000");//网银B2C：2000; 网银B2B：2025
            prams.put("merGroup","1");
            prams.put("nonceStr",Constant.getRandomString(6));
            prams.put("frontUrl",frontUrl);
            prams.put("backUrl",notifyUrl);
            prams.put("orderReceiveTimeOut","");
            prams.put("paymentTimeOut","");
            prams.put("paymentChannel","");

            StringBuilder sb=new StringBuilder();
            sb.append(prams.get("insCode")).append("|")
                    .append(prams.get("insMerchantCode")).append("|")
                    .append(prams.get("hpMerCode")).append("|")
                    .append(prams.get("orderNo")).append("|")
                    .append(prams.get("orderTime")).append("|")
                    .append(prams.get("orderAmount")).append("|")
                    .append(prams.get("name")).append("|")
                    .append(prams.get("idNumber")).append("|")
                    .append(prams.get("accNo")).append("|")
                    .append(prams.get("telNo")).append("|")
                    .append(prams.get("productType")).append("|")
                    .append(prams.get("paymentType")).append("|")
                    .append(prams.get("nonceStr")).append("|")
                    .append(signKey);
            prams.put("signature", Md5Util.MD5(sb.toString()));

            logger.info("商讯通网关入参:{}",prams);
            result.put("code", Constant.SUCCESSS);
            result.put("msg","表单拼装成功");

            result.put("viewPath","gateway/hanyinPayGatewayPay");
                JSONObject formdata = new JSONObject();
                formdata.put("actionUrl", api_url);
                formdata.put("map", prams );
                formdata.put("details", detail);
            result.put("data",formdata);

            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("商讯通网关统一支付异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "网关支付获取链接失败");
            return listener.failedHandler(result);
        }
    }
}
