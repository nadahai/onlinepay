package com.vc.onlinepay.pay.query.order;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.remittance.HttpStringRequest;
import com.vc.onlinepay.utils.remittance.RemittanceConstant;
import com.vc.onlinepay.utils.remittance.WeiXinHelper;
import com.vc.onlinepay.utils.remittance.WeiXinPayModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Service
@Component

public class RemitOrderQueryImpl {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @描述:上游订单查询接口
     * @作者:lihai
     * @时间:2018年1月18日 下午3:14:22
     */
    public JSONObject orderQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("vcOrderNo", reqData.get("vcOrderNo"));
            result.put("amount", reqData.get("amount"));
            result.put("merchantId", reqData.get("merchantId"));
            result.put("orderNo", reqData.get("orderNo"));
            result.put("password", reqData.get("password"));
            
            String merch=StringUtils.deleteWhitespace(reqData.getString("upMerchNo"));
            String key = StringUtils.deleteWhitespace(reqData.getString("upMerchKey"));

            WeiXinPayModel model = new WeiXinPayModel();
            model.setAgentBillId(reqData.getString("vcOrderNo"));
            model.setAgentBillTime(Constant.getDateString());
            model.setAgentId(merch);
            model.setReturnMode("");
            model.setRemark(reqData.getString("remark"));
            model.setVersion("1");

            String sign = WeiXinHelper.queryMd5(key, model);
            String res = HttpStringRequest.sendPost(RemittanceConstant.PAY_QUERYURL, WeiXinHelper.querySubmitUrl(sign, model), "GBK");
            logger.info(res + "参数");
            JSONObject object = HttpStringRequest.stringToJson(res);
            //状态 1下单成功 2下单失败 3下单中 4支付成功 5支付失败 6:状态不详 7:代付中 8:代付失败 9交易退款
            if (object.containsKey("result") && "1".equals(object.getString("result"))) {
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "支付成功");
                result.put("status", 4);
                return listener.successHandler(result);
            } else if ("1".equals(object.getString("result"))) {
                result.put("code", Constant.UNKNOW);
                result.put("msg", "订单处理中");
                result.put("status", 3);
                return listener.failedHandler(result);
            } else {
                result.put("code", Constant.FAILED);
                result.put("msg", object.getString("pay_message"));
                result.put("status", 3);
                return listener.failedHandler(result);
            }
        } catch (Exception e) {
            logger.error("异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "查询异常");
            result.put("status", 6);
            return listener.failedHandler(result);
        } finally {
            result.put("vcOrderNo", reqData.get("vcOrderNo"));
            result.put("amount", reqData.get("amount"));
            result.put("merchantId", reqData.get("merchantId"));
            result.put("orderNo", reqData.get("orderNo"));
            result.put("password", reqData.get("password"));
        }
    }
}
