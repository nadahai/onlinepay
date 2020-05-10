package com.vc.onlinepay.pay.order.h5;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;

/**
 * @author leoncongee
 * @描述:小狼支付交易接口类
 * @时间:2019年12月16日17:55:35
 */
@Service
public class WolfCubScanServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(WolfCubScanServiceImpl.class);
    
    @Autowired
    private CommonCallBackService commonCallBackService;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String vcOrderNo = reqData.getString("vcOrderNo");
        	result.put("orderNo", vcOrderNo);
            logger.info("小狼支付交易通道参数列表{}", reqData);  
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));//上游商户号
            String channelDesKey = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));//上游key
            String channelPayUrl = reqData.getString("channelPayUrl");
            String amount = reqData.getString("amount");  
            String goodsName = reqData.getString("goodsName");
            String goodsDesc = reqData.getString("goodsDesc");
            String notifyUrl = reqData.getString("projectDomainUrl")+"/wolfCubScanCallBackApi"; // 异步通知地址
            if(StringUtils.isNotBlank(reqData.getString("serviceCallbackUrl"))){
        		notifyUrl = reqData.getString("serviceCallbackUrl");
        	}
            String timestamp = new Date().getTime()+"";
            String ipAddress = reqData.getString("ipaddress");
            String payType = reqData.getString("payType");
            if(StringUtils.isBlank(payType)){
            	payType = reqData.getString("service");
            }
            JSONObject reqJson = new JSONObject();
            reqJson.put("charset", "UTF-8");
            reqJson.put("version", "1.0");
            reqJson.put("pid", channelKey);
            //reqJson.put("timestamp",timestamp);
            reqJson.put("notify_url", notifyUrl);
            reqJson.put("return_url", "http://192.168.0.1/test");
            reqJson.put("type", getPayType(vcOrderNo,payType));
            reqJson.put("name", goodsName);
            reqJson.put("money", amount);
            reqJson.put("out_trade_no", vcOrderNo);
            String outUserId = DateUtils.getTimeYMDhms()+String.valueOf(new Random().nextInt(10000));
            reqJson.put("out_user_id", outUserId);
            //验签参数
            String decodeKey = commonCallBackService.getDecodeKey(channelDesKey);
			String signStr = Md5CoreUtil.getSignStr(reqJson)+decodeKey;
			logger.info("加密前明文串:{}",signStr);
        	reqJson.put("sign", Md5Util.md5(signStr));
            reqJson.put("sign_type", "md5");
        	logger.info("小狼支付支付入参{}",reqJson);
            StringBuilder builder = new StringBuilder();
            for(String objKey:reqJson.keySet()){
                builder.append(objKey).append("=").append(URLEncoder.encode(reqJson.getString(objKey),"utf-8")).append("&");
            }
            String reqParam = builder.toString();
            result.put("status", 1);
            result.put("code", Constant.SUCCESSS);
            result.put("message", "下单成功");
            result.put("bankUrl", channelPayUrl+"?"+reqParam.substring(0,reqParam.length()-1));
            result.put("redirectUrl", channelPayUrl+"?"+reqParam.substring(0,reqParam.length()-1));
            return listener.successHandler(result);
            /*Map reqMap = reqJson;
        	String respMsg = HttpClientTools.httpPost(channelPayUrl, reqMap);
        	if(StringUtil.isEmpty(respMsg)) {
        		result.put("code", Constant.FAILED);
        		result.put("message", "网络超时");
        		result.put("status", 3);
        		return listener.paddingHandler(result);
        	}
            logger.info("小狼支付支付响应{}|{}",vcOrderNo,respMsg);
        	if(StringUtils.isNotBlank(respMsg) && respMsg.startsWith("http")){
                result.put("status", 1);
                result.put("code", Constant.SUCCESSS);
                result.put("message", "下单成功");
                result.put("bankUrl", respMsg);
                result.put("redirectUrl", respMsg);
                return listener.successHandler(result);
        	}
        	result.put("status", 3);
            result.put("code", Constant.FAILED);
            result.put("message", "下单失败");
            return listener.paddingHandler(result);*/
        } catch (Exception e) {
            logger.error("小狼支付下单异常", e);
            result.put("status", 3);
            result.put("code", Constant.FAILED);
            result.put("message", "下单异常");
            return listener.paddingHandler(result);
        }
    }

    /**
     * 支付类型转换
     * alipay2:支付宝、wechat2:微信、transfer:银行卡直充
     * @param vcOrderNo
     * @param payType
     * @return
     */
    private String getPayType(String vcOrderNo,String payType){
        String result = "";
        try {
            switch (payType) {
                case "22":
                case "0010":
                    result = "alipay2";
                    break;
                case "30":
                case "0002":
                    result = "wechat2";
                    break;
                case "1005":
                case "1006":
                    result = "transfer";
                    break;
                default:
                    result = "alipay2";
                    break;
            }
            return result;
        } catch (Exception e) {
            logger.error("payType类型转换异常{},{}",vcOrderNo,payType,e);
            return result;
        }
    }
}

