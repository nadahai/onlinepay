package com.vc.onlinepay.pay.order.union;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;

@Service
@Component
public class HuiYunUnionServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(HuiYunUnionServiceImpl.class);

    /**
     * @描述:惠云支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("惠云支付交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String merchNo = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            //支付宝扫码 : alipay 支付宝H5 : alipayh5 微信扫码 : weixin 微信H5 : wxh5 快捷支付 : kuaijie 快捷扫码 : kuaijiesm 网银 : wangyin
            String paytype = reqData.containsKey("payType")?reqData.getString("payType"):"";
            if(StringUtils.isEmpty(paytype)){
            	paytype = reqData.containsKey("paytype")?reqData.getString("paytype"):"kuaijie";
            }
            if(paytype.equals("1008") || paytype.equals("1106") || paytype.equals("1107") ){
            	paytype = "wangyin";
            }else if(paytype.equals("1007")){
            	paytype = "kuaijiesm";
            }else{
            	paytype = "kuaijie";
            }

            String backUrl = reqData.getString("projectDomainUrl")+"/huiyunH5PayCallBackApi";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            BigDecimal amount = new BigDecimal(reqData.getString("amount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);

            JSONObject prams = new JSONObject();
            prams.put("version","1.0");
            prams.put("customerid",merchNo);
            prams.put("total_fee",String.valueOf(amount));
            prams.put("sdorderno",reqData.getString("vcOrderNo"));
            prams.put("notifyurl",backUrl);
            prams.put("returnurl",returnUrl);
            prams.put("paytype",paytype);
            prams.put("bankcode","ICBC");
            prams.put("remark",reqData.getString("vcOrderNo"));
            prams.put("get_code","");
            String sign = Md5Util.md5("version="+prams.getString("version")+"&customerid="+prams.getString("customerid")+"&total_fee="+prams.getString("total_fee")+"&sdorderno="+prams.getString("sdorderno")+"&notifyurl="+prams.getString("notifyurl")+"&returnurl="+prams.getString("returnurl")+"&"+key);
            prams.put("sign",sign);
            logger.info("惠云支付接口入参{}",prams);
            result.put("actionUrl", API_PAY_URL);
            result.put("code", Constant.SUCCESSS);
            result.put("viewPath","auto/autoSubmit");
            result.put("data",prams);
            result.put("msg", "下单成功");
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("惠云支付下单异常", e);
            result.put("code", Constant.ERROR);
            result.put("msg", "支付处理异常");
            return listener.paddingHandler(result);
        }
    }
}
