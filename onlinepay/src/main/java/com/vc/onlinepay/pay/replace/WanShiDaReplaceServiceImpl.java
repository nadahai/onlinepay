/**
 * @类名称:InpayReplaceServiceImpl.java
 * @时间:2017年12月19日上午11:09:35
 * @作者:lihai
 * @版权:公司 Copyright (c) 2017
 */
package com.vc.onlinepay.pay.replace;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


/**
 * @描述:万事达
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component

public class WanShiDaReplaceServiceImpl {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static String url = "http://pay.mastepay.com/newadmin/Api/out";

    /**
     * @描述:万事达代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("万事达代付接口reqData:{}",reqData);
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));
            String orderNo = reqData.getString("vcOrderNo");
            String notifyUrl = reqData.getString("projectDomainUrl")+"/wanshidaReplaceCallbackApi";
            String openBankName = reqData.containsKey("openBankName")?reqData.getString("openBankName"):"其它";
            String bankName = reqData.containsKey("bankName")?reqData.getString("bankName"):"其它";
            String accountName = reqData.containsKey("accountName")?reqData.getString("accountName"):"其它";
            String bankCard = reqData.containsKey("bankCard")?reqData.getString("bankCard"):"其它";
            BigDecimal amount = new BigDecimal(reqData.getString("amount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);

            JSONObject prams = new JSONObject();
            prams.put("merid",merchantCode);
            prams.put("order_no",orderNo);
            prams.put("money",String.valueOf(amount));
            prams.put("bank_name",bankName);
            prams.put("subbranch",openBankName);
            prams.put("bank_user",accountName);
            prams.put("bank_card",bankCard);
            prams.put("urlCallback",notifyUrl);
            prams.put("remark",orderNo);
            String md5Sign = Md5CoreUtil.getSignStr (prams)+md5Key;
            String sign = Md5Util.md5(md5Sign);
            logger.info ("加密前参数{}加密后sign{}",md5Sign,sign);
            prams.put("sign",sign);
    		String response = HttpClientTools.httpSendPostFrom(url, prams);
            logger.info("万事达代付接口入参:{},响应:{}",prams,response);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "订单处理中");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if("200".equals(jsonData.getString("code"))){
                result.put("status", 2);
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "订单已经受理");
                return listener.paddingHandler(result);
            }else{
                result.put("status", 3);
                result.put("code", Constant.FAILED);
                result.put("msg", jsonData.containsKey ("mess")?Constant.unicodeToString (jsonData.getString ("mess")):"代付响应失败");
                return listener.failedHandler (result);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("万事达代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
}
