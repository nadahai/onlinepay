package com.vc.onlinepay.pay.order.union;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.hanyin.HanYinUtils;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class KuaiBaoKJScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (KuaiBaoKJScanServiceImpl.class);
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;
    

    /**
     * @描述:快包支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("快包支付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String app_id = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            
            String version = "1.0";
            
            String order_id = orderNo;
            String amount= reqData.getString ("amount");
            amount = new BigDecimal(amount).multiply(new BigDecimal(100)).intValue()+"";
            String back_url = reqData.getString ("projectDomainUrl") + "/kuaiBaoCallBackController";
            String front_url = reqData.getString("projectDomainUrl")+"/success";
            
	        String c_group_id = "5068";
	        
            JSONObject parms = new JSONObject();
            parms.put ("app_id",app_id);
            parms.put ("version",version);
            parms.put ("c_group_id", c_group_id);
            parms.put ("order_id", order_id);
            parms.put ("amount",amount);
            parms.put ("front_url",front_url);
            parms.put ("back_url",back_url);
            parms.put ("card_fingerprint",order_id);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            
            String url = API_PAY_URL+"?app_id="+app_id+"&version="+version+"&c_group_id="+c_group_id
            		+"&order_id="+order_id+"&amount="+amount
            		+"&front_url="+front_url+"&back_url="+back_url+"&sign="+sign+"&card_fingerprint="+order_id;
            logger.info("支付接口SENDURL{}",url);
            
            logger.info("支付接口入参{}",parms);
			
            /**
			String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
			logger.info("支付接口返参{}",response); 
			if(StringUtils.isEmpty (response)){ 
				return listener.failedHandler (Constant.failedMsg ("下单为空")); 
			} 
			**/
            
            
            VcOnlineOrderMade made = new VcOnlineOrderMade();
	        made.setChannelId(reqData.getIntValue("channelLabel"));
            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
	        made.setMerchNo(reqData.getString("merchantNo"));
	        made.setOrderNo(orderNo);
            made.setOpenType (302);
            made.setRemarks (reqData.getString("channelKey"));
	        made.setPaySource(reqData.getIntValue("channelSource"));
	        made.setTraAmount(new BigDecimal (amount));
	        made.setUpMerchKey(key);
	        made.setUpMerchNo(reqData.getString("merchantNo"));
	        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(orderNo));
	        String val = url;
	        made.setQrcodeUrl(val);
	        JSONObject response2 = vcOnlineOrderMadeService.getOrderMadePayUrl(made);
	        logger.info("扫码支付响应{}",response2);
			if(response2 == null || response2.isEmpty()){
                return listener.failedHandler(Constant.failedMsg ("扫码支付超时"));
            }
			
           
	            result.put("code", Constant.SUCCESSS);
	            result.put("msg", "获取链接成功");
	            result.put("redirectUrl", made.getOpenUrl());
	            
	            //result.put("bankUrl",made.getOpenUrl());
	            //result.put("redirectHtml",response);
	            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error ("快包支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String API_PAY_URL = "https://api.fufengpay.com/payment/pay";
            String key = "54RAYrWqwharoxPR2pimdJEMHlP56BjW";
            
            String app_id = "1000050";
            String version = "1.0";
            String c_group_id = "5071";
            String order_id = (System.currentTimeMillis () + "").substring (0,13);
            String amount="100";
            String front_url	="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String back_url="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            
            
            JSONObject parms = new JSONObject();
            parms.put ("app_id",app_id);
            parms.put ("version",version);
            parms.put ("c_group_id", c_group_id);
            parms.put ("order_id", order_id);
            parms.put ("amount",amount);
            parms.put ("front_url",front_url);
            parms.put ("back_url",back_url);
            parms.put ("card","6225880159092528");
            parms.put ("real_name","岁先生");
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            
            String url = API_PAY_URL+"?app_id="+app_id+"&version="+version+"&c_group_id="+c_group_id
            		+"&order_id="+order_id+"&amount="+amount
            		+"&front_url="+front_url+"&back_url="+back_url+"&sign="+sign;
            System.out.println(url);
            
            String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            logger.info("支付接口返参{}",response);


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public static void unionCustom(){
        try {
            String API_PAY_URL = "https://api.fufengpay.com/payment/pay";
            String pwd = "54RAYrWqwharoxPR2pimdJEMHlP56BjW";
            String app_id = "1000050";
            String version = "1.0";
            String c_group_id = "5068";
            String card = "6230580000238400589";
            String order_id = (System.currentTimeMillis () + "").substring (0,13);
            String amount="10000";
            String front_url	="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String back_url="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            JSONObject reqData = new JSONObject();
            reqData.put ("app_id",app_id);
            reqData.put ("version",version);
            reqData.put ("c_group_id", c_group_id);
            reqData.put ("order_id", order_id);
            reqData.put ("amount",amount);
            reqData.put ("front_url",front_url);
            reqData.put ("back_url",back_url);
            reqData.put ("card_fingerprint",order_id);
            reqData.put("card",card);
            String sourctxt1 = Md5CoreUtil.getSignStr(reqData)+"&key="+pwd;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            reqData.put ("sign",sign);
            logger.info("支付接口入参{}",reqData);
            String url = API_PAY_URL+"?app_id="+app_id+"&version="+version+"&c_group_id="+c_group_id +"&order_id="+order_id+"&amount="+amount  +"&front_url="+front_url+"&back_url="+back_url+"&sign="+sign+"&card_fingerprint="+order_id+"&card="+card;
            logger.info("快包支付1:{},入参:{}",url,reqData);
            JSONObject response = HanYinUtils.postUnionSubmitOrder(API_PAY_URL,reqData);
            logger.info("快包支付返参1{}",response);

            String actionUrl2 = response.getString("action");
            JSONObject paramsData2 = response.getJSONObject("params");
            logger.info("银联支付2:{},入参:{}",actionUrl2,paramsData2);
            JSONObject response2 = HanYinUtils.postUnionSubmitOrder(actionUrl2,paramsData2);
            logger.info("银联支付返参2{}",response2);

            String actionUrl3 = response.getString("action");
            JSONObject paramsData3 = response.getJSONObject("params");
            logger.info("银联支付3:{},入参:{}",actionUrl3,paramsData3);
            JSONObject response3 = HanYinUtils.postUnionSubmitOrder(actionUrl3,paramsData3);
            logger.info("银联支付返参3{}",response3);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
