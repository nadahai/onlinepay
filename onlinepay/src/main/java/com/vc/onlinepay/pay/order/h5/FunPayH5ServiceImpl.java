package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.monitor.AsynAliPayMonitor;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.AutoFloatAmountUtil;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.alipay.AlipayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @描述:自研支付宝
 * @时间:2018年5月15日 22:14:30
 */
@SuppressWarnings ("deprecation")
@Service
@Component
public class FunPayH5ServiceImpl {

    private Logger logger = LoggerFactory.getLogger (getClass ());
    @Autowired
    private RedisCacheApi redisCacheApi;
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;
    @Autowired
    private AsynAliPayMonitor asynAliPayMonitor;
    @Autowired
    private AutoFloatAmountUtil autoFloatAmountUtil;
    private static final long expireTime = 300000;
    private static Map<String,Integer>  loopMaps = new HashMap<>();
    
    private static Map<Integer,Integer> wxScanAmount  = new HashMap<Integer,Integer> ();
    static { 
    	 wxScanAmount.put (100,100);
    	 wxScanAmount.put (200,200);
    	 wxScanAmount.put (300,300);
		 wxScanAmount.put (500,500);
		 wxScanAmount.put (800,800);
		 wxScanAmount.put (1000,1000);
		 wxScanAmount.put (1300,1300);
		 wxScanAmount.put (1500,1500);
		 wxScanAmount.put (1800,1800);
		 wxScanAmount.put (2000,2000);
		 wxScanAmount.put (2500,2500);
		 wxScanAmount.put (3000,3000);
		 wxScanAmount.put (3500,3500);
		 wxScanAmount.put (4000,4000);
		 wxScanAmount.put (4500,4500);
		 wxScanAmount.put (5000,5000);
		 
	}

    @Value ("${onlinepay.project.domainName:}")
    private String domainName;

    @Value("${onlinepay.project.actualName:}")
    private String actualName;

    /**
     * @描述:自研支付宝支付下单
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        logger.info ("自研支付宝H5接口入参{}", reqData);
        JSONObject result = new JSONObject ();
        Integer funType = reqData.containsKey ("appType")?reqData.getIntValue ("appType"):0;
        if(funType == null || funType <1){
            funType = coreEngineProviderService.getIntCacheCfgKey ("online.alipay.funpay.open.type");
        }
        result.put("pOrder", reqData.getString("vcOrderNo"));
        try {
            String userId = reqData.containsKey("appUserId")?reqData.getString("appUserId").trim():"";
            String alipayNo = reqData.containsKey("channelKey")?reqData.getString("channelKey").trim():"";
            String userName = reqData.containsKey("appUserName")?reqData.getString("appUserName").trim():"";
            String vcOrderNo = reqData.getString("vcOrderNo").trim();
            BigDecimal amount = reqData.getBigDecimal ("amount");
            
			/*
			 * int at = amount.intValue(); if(!wxScanAmount.containsKey(at)){ return
			 * listener.failedHandler (Constant.failedMsg
			 * ("仅支持固定金额100,200,300,500,800,1000,1300,1500,1800,2000,2500,3000,3500,4000,4500,5000"
			 * )); }
			 */
            
            if(StringUtils.isEmpty (alipayNo)){
                return listener.failedHandler(Constant.failedMsg ("支付宝账号为空"));
            }
            if(StringUtils.isEmpty(userId)){
                return listener.failedHandler (Constant.failedMsg (alipayNo+"UserID未配置"));
            }
            if(StringUtils.isAnyEmpty(userName,vcOrderNo)){
                return listener.failedHandler(Constant.failedMsg (alipayNo+":账号信息不全"));
            }
            if(funType < 1){
                return listener.failedHandler (Constant.failedMsg (alipayNo+"收款类型未配置"));
            }
            VcOnlineOrderMade made = VcOnlineOrderMade.buildAlipayMade (reqData,funType);
            Long merchType = reqData.containsKey("merchType")?reqData.getLong("merchType"):0;
            if(merchType !=null && merchType == 8 && funType!=12){
                String merchId = reqData.containsKey("merchId")?reqData.getString("merchId"):"";
                alipayNo = merchId+"_"+alipayNo;
            }
            int source = reqData.getIntValue ("channelSource");
            return this.buildTrans (source,userId,alipayNo,userName,amount,vcOrderNo,reqData, made,funType,listener);
        } catch (Exception e) {
            logger.error ("自研支付宝支付异常", e);
            return listener.paddingHandler (Constant.failedMsg ("支付宝下单异常"));
        }
    }

    /**
     * @描述:支付宝转账构建参数
     * 1.个人转账(app)
     * 2.协议转账
     * 3.AA收款(app)
     * 4.转账到银行卡(app)浮动金额
     * 5.红包收款(企业营销红包)
     * 6:转账到银行卡不浮动金额
     * 7:好友转账
     * 8:个人红包
     * 9：历史记录转账
     * 10: 主动收款
     * 11: 历史收款
     * 12：商家码收款
     **/
    public JSONObject buildTrans (int channelSource,String userId,String accountNo,String userName,BigDecimal amount,String orderNo,JSONObject reqData, VcOnlineOrderMade made, int funType,ResultListener listener) {
        made.setNetOrder (orderNo);
        made.setPaySource(channelSource);
        String cardNo4End = "";
        if(funType == 1){
            made.setRemarks(userId);
            made.setNetOrder (Constant.getNetOrder (amount));
            made.setQrcodeUrl(userId);
        }else if(funType == 9){
            made.setRemarks(userId);
            made.setQrcodeUrl(AlipayUtils.buildAliPayPrms(userId,userName,amount,orderNo,funType));
        }else if (funType == 2 || funType == 3 || funType == 8 || funType == 10) {
            made.setRemarks(userId);
            made.setQrcodeUrl(AlipayUtils.buildAliPayPrms(userId,userName,amount,orderNo,funType));
        }else if(funType == 4){
            String cardIdx = reqData.containsKey("cardIdx")?reqData.getString("cardIdx").trim():"";
            String bankRemarks = reqData.containsKey("bankRemarks")?reqData.getString("bankRemarks").trim():"";
            String bankMark =  StringUtils.deleteWhitespace(reqData.containsKey("bankMark")?reqData.getString ("bankMark"):"");
            String accountName =  StringUtils.deleteWhitespace(reqData.containsKey("appUserName")?reqData.getString ("appUserName"):"");
            if(StringUtils.isAnyEmpty(bankMark,accountName)){
                return listener.failedHandler (Constant.failedMsg ("姓名或编码为空"));
            }
            cardNo4End = bankRemarks.substring (bankRemarks.length ()-4);
            String floatAmount = autoFloatAmountUtil.getAutoAmount(accountNo,amount.toString (),orderNo);
            if(StringUtil.isEmpty (floatAmount)){
                return listener.failedHandler (Constant.failedMsg ("当前金额频繁，稍后再试"));
            }
            String key = AlipayUtils.buildBankTransKey (accountNo,floatAmount);
            String allKey = AlipayUtils.buildBankTransAllCash (accountNo,floatAmount,cardNo4End);
            redisCacheApi.set(key,orderNo,CacheConstants.DEFAULT_INVALID_TIMER_10);
            redisCacheApi.set(allKey,orderNo,CacheConstants.DEFAULT_INVALID_TIMER_30);
            logger.info ("开始缓存银行卡订单:{},{}",key,orderNo);
            made.setTraAmount(new BigDecimal (floatAmount));
            String bankUrl = AlipayUtils.buildBankPayUrl (cardIdx,bankRemarks,bankMark,accountName,floatAmount);
            made.setQrcodeUrl(bankUrl);
            made.setRemarks(userName);
            String aliUser = coreEngineProviderService.getCacheCfgKey("online.ali.login.username");
            //获取体验金
//            String url="http://hawkeyepay.cn:9988/netty/push";
//            String parms="accountNo="+aliUser+
//            		"&amount="+amount+
//            		"&orderNo="+orderNo+
//            		"&msg=test"+
//            		"&qrway=101"+
//            		"&bizContent="+URLEncoder.encode(bankUrl);
//           try {
//        	   logger.info ("获取体验金请求参数{}",parms);
//        	   String response = HttpClientTools.sendGet(url, parms);
//        	   logger.info ("获取体验金响应参数{}",response);
//			} catch (Exception e) {
//				logger.error ("获取体验金异常{}",parms,e);
//			}
            //made.setQrcodeUrl (AlipayUtils.buildBankPayPrms(accountName,cardNo,bankMark,floatAmount,cardIdx,bankRemarks));
        }else if(funType == 7){
            //好友转账记录
            made.setRemarks(userName);
            made.setQrcodeUrl (AlipayUtils.biuldAlipayUrl (made, userId, userName));
        }else if(funType == 12){
            String bankRemarks = reqData.containsKey("bankRemarks")?reqData.getString("bankRemarks").trim():"";
            if(StringUtils.isAnyEmpty(bankRemarks)){
                return listener.failedHandler (Constant.failedMsg ("支付链接为空"));
            }
            String floatAmount = "";
            Integer amountType = coreEngineProviderService.getIntCacheCfgKey("online.trad.amount.type");
            // String floatAmount = amount.toString();
            if(amountType!=null && amountType == 2){
                floatAmount = amount.setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
                String key = AlipayUtils.buildBankTransKey (accountNo,floatAmount);
                if(redisCacheApi.exists(key) && redisCacheApi.get(key)!=null){
                    logger.info("固定金额金额频繁，稍后再试orderNo:{},amount:{}",orderNo,amount);
                    return  listener.failedHandler (Constant.failedMsg (amount+"金额频繁，稍后再试"));
                }
            }else{
                floatAmount = autoFloatAmountUtil.getAutoAmount(accountNo, amount.toString(),orderNo);
                if(StringUtil.isEmpty (floatAmount)){
                    logger.info("浮动金额金额频繁，稍后再试orderNo:{},amount:{}",orderNo,amount);
                    return  listener.failedHandler (Constant.failedMsg (amount+"金额频繁，稍后再试"));
                }
            }
            String key = AlipayUtils.buildBankTransKey (accountNo,floatAmount);
            redisCacheApi.set(key,orderNo,5 * CacheConstants.DEFAULT_INVALID_TIMER_1);
            logger.info ("开始缓存商家订单:{},{}",key,orderNo);
            made.setTraAmount(new BigDecimal (floatAmount));
            made.setQrcodeUrl(bankRemarks);
        }else if(funType == 6){
            //单独转账到银行卡
            String cardNo = StringUtils.deleteWhitespace(reqData.containsKey("cardNo")?reqData.getString ("cardNo"):"");
            String bankMark =  StringUtils.deleteWhitespace(reqData.containsKey("bankMark")?reqData.getString ("bankMark"):"");
            if(StringUtils.isEmpty(cardNo) || StringUtils.isEmpty(bankMark)){
                return listener.failedHandler (Constant.failedMsg ("银行卡号或编码为空"));
            }
            made.setOpenType (110);
            made.setTraAmount (amount);
            String accountName = reqData.getString ("appUserName").trim ();
            made.setQrcodeUrl (AlipayUtils.buildBankPayPrms(accountName,cardNo,bankMark,amount));
        }else{
            return this.buildAliPayUrl (funType,made, listener);
        }
        
        int r = vcOnlineOrderMadeService.save (made);
        if (r < 1) {
            return listener.failedHandler (Constant.failedMsg ("保存支付宝失败"));
        }
        JSONObject result = Constant.successMsg ("获取订单成功");
        result.put ("realAmount",made.getTraAmount());
        result.put ("pOrderNo",made.getUpMerchNo ()+"_"+made.getTraAmount()+"_"+cardNo4End);
        result.put ("redirectUrl", StringEscapeUtils.unescapeJava (made.getOpenUrl()));
        result.put ("bankUrl", StringEscapeUtils.unescapeJava (made.getOpenUrl()));
        return listener.successHandler (result);
    }

    /**
     * @描述:通过网络层框架获取自研支付宝支付链接
     * @时间:2018年1月31日 下午2:32:08
     */
    @Transactional (readOnly = false)
    public JSONObject buildAliPayUrl (int funType,VcOnlineOrderMade vcOnlineOrder,ResultListener listener) {
        JSONObject result = new JSONObject ();
        String orderNo = vcOnlineOrder.getOrderNo ();
        result.put ("vcOrderNo",orderNo);
        result.put ("pOrder",orderNo);
        String upmerchNo = vcOnlineOrder.getUpMerchNo ();
        String upmerchKey = vcOnlineOrder.getUpMerchKey();
        String payUrl = "";
        try {
            JSONObject payResult = this.pushNettyUrl (funType,upmerchNo,vcOnlineOrder.getNetOrder (),vcOnlineOrder.getTraAmount (),vcOnlineOrder.getPayUserId ());
            payUrl = payResult.containsKey ("payUrl") ? payResult.getString ("payUrl") : "";
            if(StringUtils.isEmpty (payUrl)) {
                return listener.failedHandler (Constant.failedMsg (payResult.containsKey ("msg") ? payResult.getString ("msg") : "获取链接超时"));
            }
            if(vcOnlineOrder.getOpenType() == 1){
                vcOnlineOrder.setQrcodeUrl (payUrl);
                vcOnlineOrderMadeService.updateQrcodelByOrderNo(VcOnlineOrderMade.bulidMadeQr (orderNo,payUrl));
            }else if(vcOnlineOrder.getOpenType()==3){
                vcOnlineOrder.setQrcodeUrl(AlipayUtils.buildAAPayUrl(payUrl));
                result.put ("redirectUrl",AlipayUtils.biuldH5AlipayUrl (payUrl,Constant.buildUrl(domainName,actualName)));
            }else if(vcOnlineOrder.getOpenType()==10){
                vcOnlineOrder.setQrcodeUrl(payUrl);
                result.put ("tradeNo",payUrl);
                result.put ("payUrl",payUrl);
                vcOnlineOrderMadeService.updateQrcodelByOrderNo(vcOnlineOrder);
            } else {
                vcOnlineOrder.setQrcodeUrl (payUrl);
            }
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "下单成功");
            result.put ("payUrl",payUrl);
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("获取支付链接信息异常", e);
            return listener.failedHandler (Constant.failedMsg ("获取支付链接异常"));
        } finally {
            if(StringUtils.isEmpty (payUrl) && StringUtils.isNotEmpty (upmerchNo)) {
                asynAliPayMonitor.orderMonitor (upmerchNo, upmerchKey, 2);
            }
        }
    }

    /**
     * @描述:推送支付宝下单
     * @作者:nada
     * @时间:2019/3/26
     **/
    public JSONObject pushNettyUrl (int funType,String accountNo,String orderNo,BigDecimal amount,String userId) {
        try {
            String alipayNoAmountKey = userId +"_"+amount;
            redisCacheApi.set (alipayNoAmountKey,orderNo, expireTime);
            StringBuffer param = new StringBuffer ();
            param.append ("accountNo=").append (accountNo);
            param.append ("&amount=").append (Constant.format2BigDecimal (amount));
            param.append ("&orderNo=").append (orderNo);
            param.append ("&qrway=").append (funType>1?funType:0);
            param.append ("&userId=").append (userId);
            String response = HttpClientTools.sendUrlGet (coreEngineProviderService.getCacheCfgKey ("online.alipay.netty.push.url"), param.toString ());
            logger.info ("支付链接入参:{},响应结果:{}", param,response);
            if(StringUtil.isEmpty (response)){
                return null;
            }
            return  JSONObject.parseObject (response);
        } catch (IOException e) {
            logger.error ("推送支付宝下单异常{}",orderNo,e);
            return null;
        }
    }
}
