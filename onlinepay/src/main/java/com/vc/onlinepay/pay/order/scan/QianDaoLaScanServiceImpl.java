package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.AutoFloatAmountUtil;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.alipay.AlipayUtils;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import java.math.BigDecimal;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:
 * @时间:2018年5月15日 22:14:30
 */
@SuppressWarnings("deprecation")
@Service
@Component
public class QianDaoLaScanServiceImpl {
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;
    @Autowired
    private AutoFloatAmountUtil autoFloatAmountUtil;
    @Autowired
    private RedisCacheApi redisCacheApi;
    
    /**
     * @描述:扫码支付下单
     * @时间:2018年5月15日 22:14:30
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
    	JSONObject result = new JSONObject();
        try {
        	logger.info("扫码接口入参{}",reqData);
            if(!Constant.isNumeric (reqData.getString ("amount"))){
                return listener.failedHandler ( Constant.failedMsg ("仅支持整数金额"));
            }
        	BigDecimal amount2 = reqData.getBigDecimal("amount").setScale (2, BigDecimal.ROUND_HALF_UP);
        	String  channelDesKey = reqData.containsKey ("channelDesKey")?reqData.getString("channelDesKey").trim ():"";
        	String upMerchNo = reqData.containsKey ("channelKey")?reqData.getString("channelKey").trim ():"";
        	String vcOrderNo = reqData.containsKey ("vcOrderNo")?reqData.getString("vcOrderNo").trim ():"";
            if(StringUtils.isAnyEmpty (channelDesKey,vcOrderNo,amount2.toString ())){
                return listener.failedHandler(Constant.failedMsg ("账号为空"));
            }
            String floatAmount = autoFloatAmountUtil.getAutoAmount(upMerchNo,amount2.toString (),vcOrderNo);
            if(StringUtil.isEmpty (floatAmount)){
                return listener.failedHandler (Constant.failedMsg ("当前金额频繁，稍后再试"));
            }
            String key = AlipayUtils.buildBankTransKey (upMerchNo,floatAmount);
            logger.info ("钱到啦缓存key:{}",key);
            redisCacheApi.set(key,vcOrderNo,CacheConstants.EXPIRED_TIME_5 * CacheConstants.DEFAULT_INVALID_TIMER_1);
            String allKey = AlipayUtils.buildBankTransAllCash (upMerchNo,floatAmount);
            redisCacheApi.set(allKey,vcOrderNo,20 * CacheConstants.DEFAULT_INVALID_TIMER_1);

	        VcOnlineOrderMade made = new VcOnlineOrderMade ();
	        made.setChannelId(reqData.getIntValue("channelLabel"));
            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
	        made.setMerchNo(reqData.getString("merchantNo"));
	        made.setOrderNo(reqData.getString("vcOrderNo"));
            made.setOpenType (reqData.getIntValue("channelSource"));
            made.setRemarks (reqData.getString("channelKey"));
	        made.setPaySource(reqData.getIntValue("channelSource"));
	        made.setTraAmount(new BigDecimal (floatAmount));
	        made.setUpMerchKey(upMerchNo);
	        made.setUpMerchNo(reqData.getString("channelKey").trim());
	        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(reqData.getString("vcOrderNo")));
	        made.setQrcodeUrl(reqData.getString("channelDesKey").trim());
	        JSONObject response = onlineOrderMadeService.getOrderMadePayUrl(made);
	        logger.info("扫码支付响应{}",response);
			if(response == null || response.isEmpty()){
                return listener.failedHandler(Constant.failedMsg ("扫码支付超时"));
            }
            result.put("upperParams", response);
            result.put("realAmount", floatAmount);
            result.put("pOrder", reqData.getString("vcOrderNo"));
            if (StringUtils.isNotBlank(response.getString("openUrl"))) {
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "下单成功");
                result.put("bankUrl", StringEscapeUtils.unescapeJava(response.getString("openUrl")));
                return listener.successHandler(result);
            }
            String msg = reqData.containsKey ("msg")?reqData.getString ("msg"):"下单失败";
            return listener.failedHandler(Constant.failedMsg (msg));
        } catch (Exception e) {
            logger.error("扫码支付异常", e);
            return listener.failedHandler(Constant.failedMsg("扫码支付失败"));
        }
    }
}

