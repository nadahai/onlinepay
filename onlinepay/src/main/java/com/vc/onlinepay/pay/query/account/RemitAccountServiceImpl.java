package com.vc.onlinepay.pay.query.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.remittance.HttpStringRequest;
import com.vc.onlinepay.utils.remittance.RemittanceConstant;

import com.alibaba.fastjson.JSONObject;
@Service
@Component

public class RemitAccountServiceImpl{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private CoreEngineProviderService coreEngineProviderService;

	/**
	 * @描述:汇付宝查询余额接口
	 * @作者:lihai 
	 * @时间:2018年1月4日 上午11:45:55
	 */
	public JSONObject  walletQuery(JSONObject reqData, ResultListener listener) {
		JSONObject result = new JSONObject();
        try {
        	String merch= reqData.getString("channelMerchNo");
            String channelKeyDes= reqData.getString("channelKeyDes");
            String payKey = Constant.getChannelKeyDes(channelKeyDes,3);

        	StringBuilder queryUrl=new StringBuilder();
    		queryUrl.append("version=1").append("&agent_id=").append(merch).append("&key=").append(payKey);
    		String sign=Md5Util.MD5(queryUrl.toString());
    		queryUrl.append("&sign=").append(sign);
            logger.info("查询余额入参:{}",queryUrl);
    		String resp=HttpStringRequest.sendPost(RemittanceConstant.BALANCE_URL, queryUrl.toString(),"GBK"); 
    		logger.info("查询余额结果:{}",resp);

            String code = resp.substring(0,1);
            if ("S".equals(code)) {
                String[] array=resp.split("\\|");
                String[] balance=array[1].split("=");
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "查询余额成功");
                result.put("balance", balance[1]);
            }else{
                result.put("code", Constant.FAILED);
                result.put("msg", resp.substring(2));
                result.put("balance", "0");
            }
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("上游余额查询接口异常",e);
            result.put("code", "10001");
            result.put("msg", "查询失败系统异常.");
            return listener.failedHandler(result);
        }
	}
}
