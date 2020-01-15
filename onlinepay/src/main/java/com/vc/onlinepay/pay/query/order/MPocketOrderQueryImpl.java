package com.vc.onlinepay.pay.query.order;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 口袋零钱 微信、支付宝扫码 查询
 * @author nada
 */
@Service
@Component
public class MPocketOrderQueryImpl {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String API_QUERY_URL = "http://www.koudailingqian.com/merchantpay/appTrade/orderquery";

    /**
     * 查询
     * @param reqData 入参
     * @param listener 监听
     * @return JSON
     */
    public JSONObject orderQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("vcOrderNo",reqData.getString("vcOrderNo"));
            Map<String, String> param = new LinkedHashMap<>();
            String orderTime =  DateUtils.getDate("yyyyMMddHHmmss");
            param.put ("accessToken","40012_5cba54f288ac2c77973d34ac792c214a");
            param.put ("outChannelNo",reqData.getString("plOrderNo"));
            param.put ("clientId","20002");
            param.put ("merchantNo","21c1dd2573ac46228c2c05eff547a25d");
            param.put ("rancode","2019");
            param.put ("reqtime",orderTime);
            param.put ("systemCode","10003");
            param.put ("version","3.4.4");

            // 数据的签名字符串
            String signStr = Md5CoreUtil.getSignStr(param) + "&key=" + "7c508fcd81f04a67a3a9a20997b3ef00";
            String sign = Md5Util.md5(signStr);
            param.put("sign", sign);

            logger.info("口袋零钱查询接口入参{}",result);
            String response = HttpClientTools.sendBasicNameValueData(API_QUERY_URL,param);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.FAILED);
                result.put("msg", "查询订单失败");
                return listener.failedHandler(result);
            }
            JSONObject resJson = JSONObject.parseObject(response);
            logger.info("口袋零钱查询接口返参{}",resJson);
            String data = resJson.containsKey("data")?resJson.getString("data"):"";
            if(StringUtils.isBlank(data)){
                result.put("code", Constant.FAILED);
                result.put("msg", resJson.containsKey("returnMessage")? resJson.getString("returnMessage"):"支付下单失败");
                result.put("status", 6);
                return listener.failedHandler(result);
            }
            JSONObject dataJson = JSONObject.parseObject(data);

            //状态 1下单成功 2下单失败 3下单中 4支付成功 5支付失败 6:状态不详 7:代付中 8:代付失败 9交易退款
            if ("P".equals(dataJson.getString("payStatus"))) {
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "支付成功");
                result.put("status", 4);
                return listener.successHandler(result);
            } else {
                result.put("code", Constant.UNKNOW);
                result.put("msg", "订单处理中");
                result.put("status", 1);
                return listener.failedHandler(result);
            }
        } catch (Exception e) {
            logger.error("异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "查询异常");
            result.put("status", 6);
            return listener.failedHandler(result);
        }
    }
}
