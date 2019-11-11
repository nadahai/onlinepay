package com.vc.onlinepay.pay.query.account;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.remittance.RemittanceConstant;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Service
@Component

public class RemitUnionAccountServiceImpl{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @描述:汇付宝查询余额接口
	 * @作者:lihai 
	 * @时间:2018年1月4日 上午11:45:55
	 */
	public JSONObject  walletQuery(JSONObject reqData, ResultListener listener) {
		JSONObject result = new JSONObject();
        try {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String partnerId =reqData.getString("channelMerchNo");
            String channelKeyDes =  reqData.getString("channelKeyDes");

            SortedMap<String, String> req = new TreeMap<String, String>();
            req.put("merchantId",partnerId);            //商户ID
            req.put("requestTime", timeStamp);          //请求时间
            req.put("version", "2.0");                  //请求版本
            String signature = RemittanceConstant.signatueContainKey(req, channelKeyDes);
            req.put("sign", signature);
            logger.info("查询余额入参:{}", req);
            String resMsg = HttpClientTools.httpPost(RemittanceConstant.BALANCEAMOUNT_QUERYURL_UNION,req);
            logger.info("查询余额结果{}", resMsg);
            JSONObject jsonRes = JSONObject.parseObject(resMsg);

            int retCode = (Integer)jsonRes.get("retCode");
            if (retCode==1) {
                JSONObject orderjson = JSONObject.parseObject(jsonRes.getString("queryRes"));
                //商户账户余额
                String balanceAmount = orderjson.getString("balanceAmount");
                //商户可提现余额
                String balance = orderjson.getString("balanceAvailableWithdrawAmount");

                result.put("code", Constant.SUCCESSS);
                result.put("msg", "查询余额成功");
                result.put("balance", balance);
            }else{
                result.put("code", Constant.FAILED);
                result.put("msg", jsonRes.getString("retMsg"));
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
