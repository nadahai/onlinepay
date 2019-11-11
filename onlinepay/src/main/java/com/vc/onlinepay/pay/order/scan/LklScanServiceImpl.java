package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:拉卡拉
 * @时间:2018年5月15日 22:14:30
 */
@SuppressWarnings("deprecation")
@Service
@Component
public class LklScanServiceImpl {
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;
    
    /**
     * @描述:扫码支付下单
     * @时间:2018年5月15日 22:14:30
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
    	JSONObject result = new JSONObject();
        try {
        	logger.info("拉卡拉扫码接口入参{}",reqData);
	        VcOnlineOrderMade made = new VcOnlineOrderMade ();
	        made.setChannelId(reqData.getIntValue("channelLabel"));
	        //支付有效期5分钟 拉卡拉相同金额进同一商户号的时间间隔
	        made.setExpiredTime(300);
	        made.setMerchNo(reqData.getString("merchantNo"));
	        made.setOrderNo(reqData.getString("vcOrderNo"));
	        made.setPaySource(reqData.getIntValue("channelSource"));
            made.setOpenType (reqData.getIntValue("channelSource"));
	        made.setTraAmount(reqData.getBigDecimal("amount"));
	        made.setUpMerchKey(reqData.getString("channelDesKey").trim());
	        made.setUpMerchNo(reqData.getString("channelKey").trim());
	        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(reqData.getString("vcOrderNo")));
	        made.setQrcodeUrl(reqData.getString("channelDesKey").trim());
	        JSONObject response = onlineOrderMadeService.getOrderMadePayUrl(made);
	        logger.info("拉卡拉扫码支付响应{}",response);
			if(response == null || response.isEmpty()){
				result.put("code", Constant.FAILED);
                result.put("msg", "扫码支付超时");
                result.put("payUrl", "");
                return listener.failedHandler(result);
            }
            result.put("upperParams", response);
            result.put("pOrder", reqData.getString("vcOrderNo"));
            if (StringUtils.isNotBlank(response.getString("openUrl"))) {
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "下单成功");
                result.put("bankUrl", StringEscapeUtils.unescapeJava(response.getString("openUrl")));
                return listener.successHandler(result);
            } else {
                result.put("code", Constant.FAILED);
                result.put("msg", response.getString("retMsg"));
                result.put("bankUrl", "");
                return listener.failedHandler(result);
            }
        } catch (Exception e) {
            logger.error("拉卡拉扫码支付异常", e);
            return listener.paddingHandler(Constant.failedMsg("拉卡拉扫码支付失败"));
        }
    }
}

