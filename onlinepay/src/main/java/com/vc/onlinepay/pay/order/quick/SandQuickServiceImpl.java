/**
 * @类名称:RemitReplaceServiceImpl.java
 * @时间:2017年12月19日下午2:57:29
 * @作者:lihai 
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.pay.order.quick;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpPaySubmit;
import com.vc.onlinepay.utils.remittance.SmallTools;
import com.vc.onlinepay.utils.sand.HttpKit;
import com.vc.onlinepay.utils.sand.JsonApiPayDTO;
import com.vc.onlinepay.utils.remittance.RemittanceConstant;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:杉德快捷
 * @作者:lihai 
 * @时间:2017年12月19日 下午2:57:29 
 */
@Service
@Component
public class SandQuickServiceImpl{

    private Logger logger = LoggerFactory.getLogger(getClass());

    //自动提交地址
    private static final String url = "http://lepay.hfzc888.com:8083/api/pay/v1";
    
    @Value("${onlinepay.project.successUrl:}")
    private String successUrl;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String merchNo = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String md5Key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String backUrl = reqData.getString("projectDomainUrl")+"/sandPayCallBackApi";
            
    		Map<String, String> requestMap = new HashMap<String, String>();
    		requestMap.put("orderNo",reqData.getString("vcOrderNo"));
    		requestMap.put("orderPrice",reqData.getString("amount"));// 單位元
    		requestMap.put("bankCode", "ICBC");
    		requestMap.put("version", "V1");// 常量V1
    		requestMap.put("noticeUrl",backUrl);
    		requestMap.put("returnUrl",successUrl);
    		requestMap.put("token",md5Key);
    		requestMap.put("tradeNo",merchNo);
    		requestMap.put("payType", "1");
    		requestMap.put("timestamp", Constant.ymdhms.format(new Date()));
    		JsonApiPayDTO apiPay = new JsonApiPayDTO(requestMap);
    		requestMap.put("sign", apiPay.sign());
    		requestMap.remove("token"); // token不传递
    		String res = HttpKit.get(url, requestMap);
    		 if(StringUtil.isEmpty(res)){
                 result.put("code", Constant.FAILED);
                 result.put("msg", "快捷支付获取链接失败");
                 return listener.failedHandler(result);
             }
             logger.info("杉德快捷支付获取参数{}",res);
             result.put("code", Constant.SUCCESSS);
             result.put("redirectHtml", res);
             return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("汇付宝银联快捷统一支付异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "银联快捷支付获取链接失败");
            return listener.failedHandler(result);
        }
    }

}

