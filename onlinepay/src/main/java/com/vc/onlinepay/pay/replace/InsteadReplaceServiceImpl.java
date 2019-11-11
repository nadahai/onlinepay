package com.vc.onlinepay.pay.replace;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.pay.common.ResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:人工审核通道
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component

public class InsteadReplaceServiceImpl {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @描述:人工审核通道代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String merchId =  reqData.getString("channelMerchNo");
            String channelKey =  reqData.getString("channelKeyDes");

            if(reqData.getString("vcOrderNo").startsWith("df")){
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "待人工出款");
                result.put("status", 3);
                return listener.failedHandler(result);
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "人工出款中");
            result.put("status", 2);
            return listener.paddingHandler(result);

        } catch (Exception e) {
            logger.error("人工审核通道代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            return listener.paddingHandler(result);
        }
    }
    /**
     * @描述:人工审核通道查询接口
     * @时间:2018/6/15 11:22
     */
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            String merchId =  reqData.getString("channelMerchNo");
            String channelKey =  reqData.getString("channelKeyDes");

            result.put("status", 3);
            result.put("code", Constant.FAILED);
            result.put("msg","查询失败！");
            return listener.failedHandler(result);
        }catch (Exception e){
            logger.error("人工审核通道查询接口异常",e);
            result.put("code", Constant.FAILED);
            result.put("msg","查询异常");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }finally {
            result.put("vcOrderNo",reqData.get("vcOrderNo"));
            result.put("amount",reqData.get("amount"));
            result.put("merchantId",reqData.get("merchantId"));
            result.put("orderNo", reqData.get("orderId"));
            result.put("password",reqData.get("password"));
        }
    }
    /**
     * @描述:余额查询
     * @时间:2018/6/15 11:23
     */
    public JSONObject walletQuery(JSONObject reqData,ResultListener listener){
        JSONObject result = new JSONObject();
        try {
            String merchId =reqData.getString("channelMerchNo");
            String channelKey =  reqData.getString("channelKeyDes");

            result.put("code", Constant.FAILED);
            result.put("balance","0");
            result.put("msg", "查询完成！");
            return listener.failedHandler(result);
        } catch (Exception e) {
            logger.error("人工审核通道余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }

}
