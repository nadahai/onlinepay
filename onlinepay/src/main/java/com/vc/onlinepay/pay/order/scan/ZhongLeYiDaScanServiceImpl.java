package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.yida.BaseSdk;
import com.vc.onlinepay.utils.yida.PayDigestUtil;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class ZhongLeYiDaScanServiceImpl {

    private static final String CONTENT_TYPE_TEXT_JSON = "text/html;charset=utf-8";

    private static Logger logger = LoggerFactory.getLogger (ZhongLeYiDaScanServiceImpl.class);

    /**
     * @描述:众乐益达扫码交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("众乐益达扫码交易接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", reqData.getString ("vcOrderNo"));
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String merchNo = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String backUrl = reqData.getString ("projectDomainUrl") + "/zhongLeYidaPayCallBackApi";
            BigDecimal amount = new BigDecimal (reqData.getString ("amount")).multiply (new BigDecimal ("100")).setScale (0, BigDecimal.ROUND_HALF_DOWN);

            JSONObject paramMap = new JSONObject();
            paramMap.put("mchId", merchNo);
            paramMap.put("appId", "41c1755f7f644ae6aaea5397876d9d6c");
            paramMap.put("mchOrderNo",orderNo);
            paramMap.put("productId", "8007");
            paramMap.put("amount", amount);
            paramMap.put("currency", "cny");
            paramMap.put("clientIp", "211.94.116.218");
            paramMap.put("device", "WEB");
            paramMap.put("subject", "深圳盛源网络科技有限公司");
            paramMap.put("body", "深圳盛源网络科技有限公司");
            paramMap.put("notifyUrl", backUrl);
            paramMap.put("param1",orderNo);
            paramMap.put("param2",orderNo);
            paramMap.put("extra", "{\"productId\":\"100\"}");
            String reqSign = PayDigestUtil.getSign(paramMap, key);
            paramMap.put("sign", reqSign);
            String params = "?params=" + paramMap.toJSONString();
            logger.info("接口入参{}",params);
            String respMsg = BaseSdk.call4Post(API_PAY_URL + params);
            logger.info("响应{}",respMsg);
            if(StringUtil.isEmpty (respMsg)){
                return listener.failedHandler (Constant.failedMsg ("下单失败"));
            }
            JSONObject retMap = Constant.stringToJson (respMsg);
            if("SUCCESS".equals(retMap.get("retCode"))) {
                JSONObject payParams = retMap.getJSONObject ("payParams");
                if(payParams == null || payParams.isEmpty () || !payParams.containsKey ("payUrl")){
                    return listener.failedHandler (Constant.failedMsg ("下单失败"));
                }
                result.put ("code", Constant.SUCCESSS);
                result.put ("msg", "获取链接成功");
                result.put ("bankUrl", payParams.getString ("payUrl"));
                return listener.successHandler (result);
            }
            return listener.failedHandler (Constant.failedMsg ("下单失败"));
        } catch (Exception e) {
            logger.error ("众乐益达扫码下单异常", e);
            return listener.paddingHandler (Constant.failedMsg ("下单异常"));
        }
    }
}
