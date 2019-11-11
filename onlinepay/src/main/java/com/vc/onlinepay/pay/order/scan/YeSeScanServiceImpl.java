package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class YeSeScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (YeSeScanServiceImpl.class);
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;

    /**
     * @描述:夜色支付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("夜色支付宝接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            
            String appid = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String notifyurl = reqData.getString ("projectDomainUrl") + "/yeSeCallBackController";
            String backurl = reqData.getString("projectDomainUrl")+"/success";
            String total_fee= reqData.getString ("amount");
            
            String out_trade_no = orderNo;
            String desc = "ADIDAS";
            String pay = "3";
            
            Integer type = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
            String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
            //payType 1:微信 2:支付宝 5:qq支付
            if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
            	pay = "1";
            }
            
            BigDecimal bg = new BigDecimal(total_fee);
            DecimalFormat df = new DecimalFormat("#.00");
            total_fee = df.format(bg);
            JSONObject parms = new JSONObject();
            parms.put ("appid",appid);
            parms.put ("total_fee",total_fee);
            parms.put ("out_trade_no",out_trade_no);
            parms.put ("desc", desc);
            parms.put ("notifyurl", notifyurl);
            parms.put ("backurl",backurl);
            parms.put ("pay",pay);
            
            String sourctxt1 = "appid="+appid+"&total_fee="+total_fee+"&desc="+desc+"&notifyurl="+notifyurl
            		+"&out_trade_no="+out_trade_no+"&pay="+pay+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1);
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            
            String url = API_PAY_URL+"?appid="+appid+"&total_fee="+total_fee+"&desc="+desc+
            		"&notifyurl="+notifyurl+"&out_trade_no="+out_trade_no+"&pay="+pay+"&sign="+sign+"&backurl="+backurl;
            logger.info("支付接口入参URL{}",url);
            
            VcOnlineOrderMade made = new VcOnlineOrderMade();
	        made.setChannelId(reqData.getIntValue("channelLabel"));
            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
	        made.setMerchNo(reqData.getString("merchantNo"));
	        made.setOrderNo(orderNo);
            made.setOpenType (138);
            made.setRemarks (reqData.getString("channelKey"));
	        made.setPaySource(reqData.getIntValue("channelSource"));
	        made.setTraAmount(new BigDecimal (total_fee));
	        made.setUpMerchKey(key);
	        made.setUpMerchNo(reqData.getString("merchantNo"));
	        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(orderNo));
	        made.setQrcodeUrl(url);
	        JSONObject response2 = vcOnlineOrderMadeService.getOrderMadePayUrl(made);
	        logger.info("扫码支付响应{}",response2);
			if(response2 == null || response2.isEmpty()){
                return listener.failedHandler(Constant.failedMsg ("扫码支付超时"));
            }
				
	            result.put ("code", Constant.SUCCESSS);
	            result.put ("msg", "获取链接成功");
	            result.put ("bankUrl",made.getOpenUrl());
	            result.put ("redirectUrl",made.getOpenUrl());
	            result.put ("qrCodeUrl",made.getOpenUrl());
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("夜色支付宝下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
        	String API_PAY_URL = "http://api.womimo.cn/pay/codepay.php";
            String key = "2ac0417219aab69449e1d6a5d388b8b1";
            
            String appid = "19063462";
            String total_fee="100";
            String out_trade_no = (System.currentTimeMillis () + "").substring (0,13);
            String desc = "ADIDAS";
            String notifyurl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String backurl="http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String pay = "1";
            BigDecimal bg = new BigDecimal(total_fee);
            DecimalFormat df = new DecimalFormat("#.00");
            total_fee = df.format(bg);
            JSONObject parms = new JSONObject();
            parms.put ("appid",appid);
            parms.put ("total_fee",total_fee);
            parms.put ("out_trade_no",out_trade_no);
            parms.put ("desc", desc);
            parms.put ("notifyurl", notifyurl);
            parms.put ("backurl",backurl);
            parms.put ("pay",pay);
            
            String sourctxt1 = "appid="+appid+"&total_fee="+total_fee+"&desc="+desc+"&notifyurl="+notifyurl
            		+"&out_trade_no="+out_trade_no+"&pay="+pay+"&key="+key;
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1);
            parms.put ("sign",sign);
            
            logger.info("支付接口入参{}",parms);
            
            String url = API_PAY_URL+"?appid="+appid+"&total_fee="+total_fee+"&desc="+desc+
            		"&notifyurl="+notifyurl+"&out_trade_no="+out_trade_no+"&pay="+pay+"&sign="+sign+"&backurl="+backurl;
            System.out.println(url);
            //String response = HttpClientTools.httpSendPostFrom(API_PAY_URL,parms);
            //logger.info("支付接口返参{}",response);
           
           
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
