package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineOrderMadeMapper;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class HuiYunH5ServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(HuiYunH5ServiceImpl.class);

    @Autowired
    private VcOnlineOrderMadeMapper vcOnlineOrderMadeMapper;

    /**
     * @描述:惠云支付交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String merchNo = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));

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
            prams.put("paytype","alipay");
            prams.put("bankcode","ICBC");
            prams.put("remark",reqData.getString("vcOrderNo"));
            prams.put("get_code","");
            String sign = Md5Util.md5("version="+prams.getString("version")+"&customerid="+prams.getString("customerid")+"&" +
                    "total_fee="+prams.getString("total_fee")+"&sdorderno="+prams.getString("sdorderno")+"&notifyurl="+prams.getString("notifyurl")+"&returnurl="+prams.getString("returnurl")+"&"+key);
            prams.put("sign",sign);
            logger.info("惠云支付接口入参{}",prams);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,prams);
            logger.info("惠云支付接口返参{}",response);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.FAILED);
                result.put("msg", "支付下单失败");
                return listener.failedHandler(result);
            }
            String service = reqData.containsKey("service")?reqData.getString("service"):"";
            if(!"0010".equals(service)){
                result.put("code", Constant.SUCCESSS);
                result.put("redirectHtml",response);
                result.put("msg", "下单成功");
                return listener.successHandler(result);
            }
            VcOnlineOrderMade made = VcOnlineOrderMade.buildAlipayMade (reqData);
            made.setOpenType (86);
            made.setRemarks (reqData.getString("vcOrderNo"));
            made.setQrcodeUrl (response);
            int r = vcOnlineOrderMadeMapper.save (made);
            if (r < 1) {
                result.put ("code", Constant.FAILED);
                result.put ("msg", "保存链接失败");
                return listener.failedHandler (result);
            }
            result.put("code", Constant.SUCCESSS);
            result.put("bankUrl",made.getOpenUrl());
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
