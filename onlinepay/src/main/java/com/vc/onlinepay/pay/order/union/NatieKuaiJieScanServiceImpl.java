package com.vc.onlinepay.pay.order.union;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class NatieKuaiJieScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (NatieKuaiJieScanServiceImpl.class);
    

    /**
     * @描述:拿铁快捷支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("拿铁快捷支付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String custId = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String backUrl = reqData.getString ("projectDomainUrl") + "/natieCallBackController";
            String frontUrl = reqData.getString("projectDomainUrl")+"/success";
            String money= reqData.getString ("amount");
            String version = "2.1";
            String orgNo = "0190600441";
            String custOrderNo = orderNo;
            String tranType = "0302";
            Double amount = Double.parseDouble (money)*100;
            String payAmt=amount.intValue()+"";
            String goodsName=custOrderNo;
            
            JSONObject parms = new JSONObject();
            parms.put ("version",version);
            parms.put ("orgNo",orgNo);
            parms.put ("custId",custId);
            parms.put ("custOrderNo", custOrderNo);
            parms.put ("tranType", tranType);
            parms.put ("payAmt",payAmt);
            parms.put ("backUrl",backUrl);
            parms.put ("frontUrl",frontUrl);
            parms.put ("goodsName",goodsName);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1);
            parms.put ("sign",sign);
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);
	            if(StringUtils.isEmpty (response)){
	                return listener.failedHandler (Constant.failedMsg ("下单为空"));
	            }
	            JSONObject payParams = Constant.stringToJson (response);
	            if(payParams == null || payParams.isEmpty () || !payParams.containsKey ("busContent")){
	                return listener.failedHandler (Constant.failedMsg ("获取连接为空"));
	            }
				
	            result.put("code", Constant.SUCCESSS);
	            result.put("msg", "获取链接成功");
	            result.put("redirectHtml",payParams.getString("busContent"));
	            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error ("拿铁快捷支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
        	String API_PAY_URL = "http://pay.yudugs.com:89/tran/cashier/pay.ac";
            String key = "0BFBB311BF0C6CBCD08CCB2FB5522EBF";
            
            String version = "2.1";
            String orgNo = "0190600441";
            String custId = "19062800002051";
            String custOrderNo = (System.currentTimeMillis () + "").substring (0,13);
            String tranType = "0302";
            String payAmt="10000";
            String backUrl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String frontUrl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String goodsName=custOrderNo;
            
           // String time=Constant.yyyyMMdd.format(new Date ());
            
            JSONObject parms = new JSONObject();
            parms.put ("version",version);
            parms.put ("orgNo",orgNo);
            parms.put ("custId",custId);
            parms.put ("custOrderNo", custOrderNo);
            parms.put ("tranType", tranType);
            parms.put ("payAmt",payAmt);
            parms.put ("backUrl",backUrl);
            parms.put ("frontUrl",frontUrl);
            parms.put ("goodsName",goodsName);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1);
            parms.put ("sign",sign);
            
            
            logger.info("支付接口入参{}",parms);
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);
            
           
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
