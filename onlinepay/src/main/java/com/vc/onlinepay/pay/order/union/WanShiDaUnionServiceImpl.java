package com.vc.onlinepay.pay.order.union;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class WanShiDaUnionServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(WanShiDaUnionServiceImpl.class);

    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;

    /**
     * @描述:万事达网银快捷交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("万事达网银快捷交易接收入参{}",reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String API_PAY_URL  = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            String merchNo = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String key  = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
            String backUrl = reqData.getString("projectDomainUrl")+"/wanShiDaPayCallBackApi";
            BigDecimal amount = new BigDecimal(reqData.getString("amount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            String merchantId = StringUtils.deleteWhitespace(reqData.getString("merchantId"));
            String userId  = StringUtils.deleteWhitespace(reqData.containsKey ("userId")?reqData.getString("userId"):"");
            if(StringUtil.isEmpty (userId)){
                userId = Constant.getUUid ();
            }else{
                userId = userId+merchantId;
            }
            JSONObject prams = new JSONObject();
            prams.put("merid",merchNo);
            prams.put("sn",reqData.getString("vcOrderNo"));
            prams.put("money",String.valueOf(amount));
            prams.put("subject","深圳盛源网络科技有限公司");
            prams.put("urlCallback",backUrl);
            prams.put ("userid",userId);
            prams.put("extra",reqData.getString("vcOrderNo"));
            String md5Sign = Md5CoreUtil.getSignStr (prams)+key;
            String sign = Md5Util.md5(md5Sign);
            logger.info ("加密前参数{}加密后sign{}",md5Sign,sign);
            prams.put("sign",sign);
            logger.info("万事达网银快捷接口入参{}",prams);

            VcOnlineOrderMade made = new VcOnlineOrderMade ();
            made.setChannelId (reqData.getIntValue ("channelLabel"));
            made.setExpiredTime (300);
            made.setMerchNo (reqData.getString ("merchantNo"));
            made.setOrderNo (reqData.getString ("vcOrderNo"));
            made.setPaySource (reqData.getIntValue ("channelSource"));
            made.setTraAmount (reqData.getBigDecimal ("amount"));
            made.setUpMerchKey (key);
            made.setUpMerchNo (merchNo);
            made.setOpenType (10011);
            made.setOpenUrl (reqData.getString ("projectDomainUrl") + "/code/" + HiDesUtils.desEnCode (reqData.getString ("vcOrderNo")));
            made.setQrcodeUrl (API_PAY_URL);
            made.setRemarks (prams.toJSONString ());
            int r = onlineOrderMadeService.save(made);
            if(r<1){
                result.put("code", Constant.FAILED);
                result.put("msg", "下单失败");
                result.put("bankUrl", "");
                return listener.failedHandler(result);
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            result.put("bankUrl",made.getOpenUrl());
            return listener.successHandler(result);


           /* result.put("actionUrl", API_PAY_URL);
            result.put("viewPath","auto/autoSubmit");
            result.put("data",prams);
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "下单成功");
            return listener.successHandler(result);*/
        } catch (Exception e) {
            logger.error("万事达网银快捷下单异常", e);
            result.put("code", Constant.ERROR);
            result.put("msg", "网银快捷处理异常");
            return listener.paddingHandler(result);
        }
    }
}
