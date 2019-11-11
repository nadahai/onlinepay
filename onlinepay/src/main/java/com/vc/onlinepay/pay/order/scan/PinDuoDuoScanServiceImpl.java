package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DigestUtil;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;

import java.math.BigDecimal;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class PinDuoDuoScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (PinDuoDuoScanServiceImpl.class);
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;
    

    /**
     * @描述: 拼多多支付接口
     * 
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("拼多多支付接收入参{}", reqData);
            
            String url = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String p1_MerId = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String p0_Cmd="Buy";
            String p2_Order = reqData.getString ("vcOrderNo");
            String p3_Amt= reqData.getString ("amount");
            String p4_Cur = "CNY";
            String p8_Url = reqData.getString ("projectDomainUrl") + "/pinDuoDuoCallBackController";
            String pd_FrpId = "alipaywap";
            String pr_NeedResponse = "1";
            String hmacStr = p0_Cmd+p1_MerId+p2_Order+p3_Amt+p4_Cur+p8_Url+pd_FrpId+pr_NeedResponse;
            
            System.out.println(hmacStr);
            String hmac = DigestUtil.hmacSign(hmacStr, key); //数据签名
           
            VcOnlineOrderMade made = new VcOnlineOrderMade();
	        made.setChannelId(reqData.getIntValue("channelLabel"));
            made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
	        made.setMerchNo(reqData.getString("merchantNo"));
	        made.setOrderNo(p2_Order);
            made.setOpenType (97);
            made.setRemarks (reqData.getString("channelKey"));
	        made.setPaySource(reqData.getIntValue("channelSource"));
	        made.setTraAmount(new BigDecimal (p3_Amt));
	        made.setUpMerchKey(key);
	        made.setUpMerchNo(reqData.getString("merchantNo"));
	        made.setOpenUrl(reqData.getString("projectDomainUrl")+"/code/"+ HiDesUtils.desEnCode(p2_Order));
	        String val = url+"?p0_Cmd="+p0_Cmd
            		+ "&p1_MerId="+p1_MerId
		            + "&p2_Order="+p2_Order
		            + "&p3_Amt="+p3_Amt
		            + "&p4_Cur="+p4_Cur
		            + "&p8_Url="+p8_Url
		            + "&pd_FrpId="+pd_FrpId
		            + "&pr_NeedResponse="+pr_NeedResponse
		            + "&hmac="+hmac;
	        made.setQrcodeUrl(val);
	        JSONObject response = vcOnlineOrderMadeService.getOrderMadePayUrl(made);
	        logger.info("扫码支付响应{}",response);
			if(response == null || response.isEmpty()){
                return listener.failedHandler(Constant.failedMsg ("扫码支付超时"));
            }
			
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "获取链接成功");
            result.put ("bankUrl",StringEscapeUtils.unescapeJava(response.getString("openUrl")));
            result.put ("redirectUrl",StringEscapeUtils.unescapeJava(response.getString("openUrl")));
            result.put ("qrCodeUrl",StringEscapeUtils.unescapeJava(response.getString("openUrl")));
            
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("拼多多支付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            String url = "http://pay.niubizf.com/pay/getway";
            
            String key = "TZRBcmFv71MVpxyjzQ0wDuy5uHJ0fuMy";
            String p1_MerId = "30067";
            String p0_Cmd="Buy";
            String p2_Order = (System.currentTimeMillis () + "").substring (0,13);
            String p3_Amt= "300";
            String p4_Cur = "CNY";
            String p8_Url = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String pd_FrpId = "alipaywap";
            String pr_NeedResponse = "1";
            
           
            JSONObject prams = new JSONObject();
            prams.put("p1_MerId",p1_MerId);
            prams.put("p0_Cmd",p0_Cmd);
            prams.put("p2_Order",p2_Order);
            prams.put("p3_Amt",p3_Amt);
            prams.put("p4_Cur",p4_Cur);
            prams.put("p8_Url",p8_Url);
            prams.put("pd_FrpId",pd_FrpId);
            prams.put("pr_NeedResponse",pr_NeedResponse);
          //String hmac = Md5CoreUtil.md5ascii(prams,key).toUpperCase();
            String hmacStr = p0_Cmd+p1_MerId+p2_Order+p3_Amt+p4_Cur+p8_Url+pd_FrpId+pr_NeedResponse;
            System.out.println(hmacStr);
            String hmac = DigestUtil.hmacSign(hmacStr, key); //数据签名
            prams.put("hmac",hmac);
            String val = "p0_Cmd="+p0_Cmd
            		+ "&p1_MerId="+p1_MerId
		            + "&p2_Order="+p2_Order
		            + "&p3_Amt="+p3_Amt
		            + "&p4_Cur="+p4_Cur
		            + "&p8_Url="+p8_Url
		            + "&pd_FrpId="+pd_FrpId
		            + "&pr_NeedResponse="+pr_NeedResponse
		            + "&hmac="+hmac;
            logger.info("支付接口入参{}",prams);
            //String respMsg = HttpClientTools.sendGet(url, val);
            String respMsg = HttpClientTools.httpSendPostFrom(url,prams);
            logger.info("支付接口返参{}",respMsg);

        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
