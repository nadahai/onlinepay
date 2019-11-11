package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
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
public class KuaiBaoScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (KuaiBaoScanServiceImpl.class);
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
            String back_url = reqData.getString ("projectDomainUrl") + "/kuaiBaoCallBackController";
            String front_url = reqData.getString("projectDomainUrl")+"/success";
            
            
            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
	         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
		     //payType 1:微信 2:支付宝
	         String c_group_id = "5070";
	         
	         if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
	        	 c_group_id = "5070";
	         }
            
            
            JSONObject parms = new JSONObject();
            parms.put ("app_id",app_id);
            parms.put ("version",version);
            parms.put ("c_group_id", c_group_id);
            parms.put ("order_id", order_id);
            parms.put ("amount",amount);
            parms.put ("front_url",front_url);
            parms.put ("back_url",back_url);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            
            String url = API_PAY_URL+"?app_id="+app_id+"&version="+version+"&c_group_id="+c_group_id
            		+"&order_id="+order_id+"&amount="+amount
            		+"&front_url="+front_url+"&back_url="+back_url+"&sign="+sign;
            logger.info("支付接口SENDURL{}",url);
            
            VcOnlineOrderMade made = new VcOnlineOrderMade();
	        made.setChannelId(reqData.getIntValue("channelLabel"));
            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
	        made.setMerchNo(reqData.getString("merchantNo"));
	        made.setOrderNo(orderNo);
            made.setOpenType (137);
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
	            result.put ("code", Constant.SUCCESSS);
	            result.put ("msg", "获取链接成功");
	            result.put ("bankUrl",StringEscapeUtils.unescapeJava(response2.getString("payurl")));
	            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(response2.getString("payurl")));
	            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(response2.getString("payurl")));
            
            return listener.successHandler (result);
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
            String c_group_id = "5068";
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
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            
            String url = API_PAY_URL+"?app_id="+app_id+"&version="+version+"&c_group_id="+c_group_id
            		+"&order_id="+order_id+"&amount="+amount
            		+"&front_url="+front_url+"&back_url="+back_url+"&sign="+sign;
            System.out.println(url);
            
            //String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            //logger.info("支付接口返参{}",response);


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
