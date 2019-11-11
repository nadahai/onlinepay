/**
 * @类名称:CoreEngineProviderServiceImpl.java
 * @时间:2018年3月6日上午10:36:51
 * @作者:lihai
 * @版权:公司 Copyright (c) 2018
 */
package com.vc.onlinepay.persistent.common;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeOrderSettleRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeOrderSettleResponse;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.exception.OnlineDbException;
import com.vc.onlinepay.persistent.entity.channel.ChannelSubNo;
import com.vc.onlinepay.persistent.entity.dict.Dictionary;
import com.vc.onlinepay.persistent.entity.merch.SettleSubno;
import com.vc.onlinepay.persistent.entity.merch.SupplierSubno;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.monitor.AsynAliPayMerchMonitor;
import com.vc.onlinepay.persistent.service.channel.SupplierSubnoServiceImpl;
import com.vc.onlinepay.persistent.service.dict.DictionaryServiceImpl;
import com.vc.onlinepay.persistent.service.merch.SettleSubnoServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.LoopRobinUtil;
import com.vc.onlinepay.utils.StringUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lihai
 * @描述:核心引擎服务提供者接口实现
 * @作者:lihai
 * @时间:2018年3月6日 上午10:36:51
 */
@Service
@Transactional (readOnly = true, rollbackFor = Exception.class)
public class CoreEngineProviderService {

    public static final Logger logger = LoggerFactory.getLogger (CoreEngineProviderService.class);

    @Value("${onlinepay.project.domainName:}")
    private String domainName;

    @Value("${onlinepay.project.actualName:}")
    private String actualName;

    @Autowired
    private DictionaryServiceImpl dictionaryService;
    @Autowired
    private RedisCacheApi redisCacheApi;
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Autowired
    private SupplierSubnoServiceImpl supplierSubnoService;
    @Autowired
    private AsynAliPayMerchMonitor asynAliPayMerchMonitor;
    @Autowired
    private SettleSubnoServiceImpl settleSubnoService;
    private static final int TRY_LOOP_NUM = 5;
    private static Map<Long, LoopRobinUtil> LoopRobinUtils = new HashMap<> ();
    private static Map<Long, LoopRobinUtil> LoopRobinWxNoMap = new HashMap<> ();

    private static Map<String,JSONObject> ORDER_SETTLE_LIST = new HashMap<>();

    public CoreEngineProviderService () {
        if (LoopRobinUtils == null || LoopRobinUtils.size () < 1 || LoopRobinUtils.isEmpty ()) {
            LoopRobinUtils = new HashMap<> ();
        }
        if (LoopRobinWxNoMap == null || LoopRobinWxNoMap.size () < 1 || LoopRobinWxNoMap.isEmpty ()) {
            LoopRobinWxNoMap = new HashMap<> ();
        }
    }

    /**
     * @描述:自研支付宝轮询算法,获取一指定时间范围内个子商户号
     * @时间:2018年5月17日 下午5:57:58
     */
    public SupplierSubno getLoopRobin (ChannelSubNo channelSubNo) throws OnlineDbException {
        try {
            String upmerchNo = "";
            double traAmount = channelSubNo.getTraAmount ().doubleValue ();
            int loopRobin = channelSubNo.getLoopRobin () < 1 ? 1 : channelSubNo.getLoopRobin ();
            Long channelId = channelSubNo.getChannelId ();
            if (!LoopRobinUtils.containsKey (channelId) || LoopRobinUtils.get (channelId) == null) {
                LoopRobinUtils.put (channelId, new LoopRobinUtil ());
            }
            String orderNo = channelSubNo.getOrderNo ();
            Map<String, Integer> allSubNoMap = supplierSubnoService.getCacheSubNoMap (channelId,traAmount);
            logger.info ("取所有子账号列表订单:{},渠道号:{},支付宝账号:{}", orderNo, upmerchNo, allSubNoMap);
            if(allSubNoMap == null || allSubNoMap.size ()<1){
                return  null;
            }
            return supplierSubnoService.getOneSubNo (channelId, traAmount, loopRobin, allSubNoMap, TRY_LOOP_NUM, orderNo, LoopRobinUtils.get (channelId));
        } catch (Exception e) {
            logger.error ("自研支付宝轮询算法获取账号异常", e);
            return null;
        }
    }
    
    
    /**
     * @描述:集合算法轮询下标
     * @时间:2019年4月19日 上午11:55:46
     */
    public int getCacheKeyValOrUpdateVal (String key,int listSize) throws OnlineDbException {
    	int resVal = 0;
        try {
            if (redisCacheApi.exists (key) && StringUtil.isNotEmpty ((String) redisCacheApi.get (key))) {
            	resVal =  Integer.valueOf (redisCacheApi.get (key).toString()) ;
            } else {
            	Dictionary dictionary = dictionaryService.findDictionaryByKey (new Dictionary (key));
                if (dictionary == null || StringUtil.isEmpty (dictionary.getStrValue ())) {
                    return 0;
                }
                resVal =  Integer.valueOf (dictionary.getStrValue ()) ;
                
            }
            if(resVal < listSize) {
            	boolean isOk = redisCacheApi.set (key, resVal+1);
            }else {
            	resVal = 0;
            	boolean isOk = redisCacheApi.set (key, resVal);
            }
            
        } catch (Exception e) {
            logger.error ("获取缓存配置key异常", e);
        }
        return resVal;
    }
    
    /**
     * @描述:从缓存获取配置信息
     * @时间:2018年3月12日 上午11:55:46
     */
    public String getCacheCfgKey (String key) throws OnlineDbException {
        try {
            if (redisCacheApi.exists (key) && StringUtil.isNotEmpty ((String) redisCacheApi.get (key))) {
                return (String) redisCacheApi.get (key);
            }
            Dictionary dictionary = dictionaryService.findDictionaryByKey (new Dictionary (key));
            if (dictionary == null || StringUtil.isEmpty (dictionary.getStrValue ())) {
                return null;
            }
            boolean isOk = redisCacheApi.set (key, dictionary.getStrValue ());
            return dictionary.getStrValue ();
        } catch (Exception e) {
            logger.error ("获取缓存配置key异常", e);
            return "error";
        }
    }

    public int getIntCacheCfgKey (String cacheKey) {
        try {
            String value = getCacheCfgKey (cacheKey);
            if (StringUtils.isEmpty (value) || "error".equals (value)) {
                return 0;
            }
            return Integer.valueOf (value);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return 0;
    }

    /**
     * @描述:验证收钱吧20笔交叉跑
     * @时间:2018/10/27 23:36
     */
    public boolean checkSqb (String upMerchNo,int type) {
        try {
            if (StringUtils.isEmpty (upMerchNo)) {
                return true;
            }
            if (type < 1) {
                return true;
            }
            String key = CacheConstants.REDIS_SHOUQIANBA_UPNO+upMerchNo+"_"+type;
            Integer num = (Integer) redisCacheApi.get (key);
            if(num == null || num <1){
                redisCacheApi.set (key,1);
            }
            if(num >=10){
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * @描述:设置收钱吧信息
     * @作者:nada
     * @时间:2019/5/2
     **/
    public boolean setSqbNum (String upMerchNo,int type) {
        try {
            if (StringUtils.isEmpty (upMerchNo)) {
                return true;
            }
            if (type < 1) {
                return true;
            }
            String key = CacheConstants.REDIS_SHOUQIANBA_UPNO+upMerchNo+"_"+type;
            Integer num = (Integer) redisCacheApi.get (key);
            if(num == null || num <1){
                redisCacheApi.set (key,1);
            }else{
                redisCacheApi.set (key,num+1);
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * @描述:获取解密的秘钥
     * @作者:THINK Daniel
     * @时间:2018/10/27 23:36
     */
    public String getDecodeChannlKey (String channelDesKey) {
        try {
            if (StringUtils.isEmpty (channelDesKey)) {
                return "";
            }
            if (channelDesKey.length () % 4 != 0) {
                return channelDesKey;
            }
            String encodedKey = org.apache.commons.lang.StringUtils.deleteWhitespace (channelDesKey);
            String publicKey = getCacheCfgKey (CacheConstants.ONLINE_DECODE_PUBLIC_KEY);
            return ConfigTools.decrypt (publicKey, encodedKey);
        } catch (Exception e) {
            return channelDesKey;
        }
    }

    /**
     * 访问黑名单控制 返回true不可访问
     */
    public boolean accessFilter (String urlKey, String text) {
        try {
            if (StringUtils.isAnyBlank (urlKey, text)) {
                return false;
            }
            String contents = getCacheAllowedAccessIps (urlKey);
            if (StringUtils.isEmpty (contents) || ",".equals (contents)) {
                return false;
            }
            text = StringUtils.deleteWhitespace (text);
            if (contents.contains (text)) {
                logger.error ("拦截成功 key:{},text:{},", urlKey, text);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return false;
    }

    /**
     * @描述:接口访问IP限制 返回true可访问
     * @作者:THINK Daniel
     * @时间:2018/10/29 16:46
     */
    public boolean isAllowedAccessIp (String uri, String ip) {
        if (uri.contains ("?")) {
            uri = uri.substring (0, uri.indexOf ("?"));
        }
        if (uri.endsWith ("/")) {
            uri = uri.substring (0, uri.length () - 1);
        }
        if (uri.contains ("static/") || uri.contains ("/cashier") || uri.contains ("/code/") || uri.contains ("/api/") || uri.contains ("/wechatQr/")) {
            return true;
        }
        if ("127.0.0.1".equals (ip)) {
            return true;
        }
        String alllowIps = getCacheAllowedAccessIps (uri);
        if (StringUtils.isEmpty (alllowIps) || ",".equals (alllowIps)) {
            return true;
        }
        ip = StringUtils.deleteWhitespace (ip);
        if (alllowIps.contains (ip)) {
            return true;
        }
        if (!"payTestHost".equals (uri)) {
            logger.error ("拦截成功 uri:{},ip:{},", uri, ip);
        }
        return false;
    }

    public String getCacheAllowedAccessIps (String url) throws OnlineDbException {
        try {
            String key = "ACCESS_IP[" + url + "]";
            if (redisCacheApi.exists (key) && StringUtil.isNotEmpty ((String) redisCacheApi.get (key))) {
                return (String) redisCacheApi.get (key);
            }
            Dictionary dictionary = dictionaryService.findAllowedAccessIp (new Dictionary (url));
            String value = ",";
            if (dictionary != null && StringUtil.isNotEmpty (dictionary.getStrValue ())) {
                value = StringUtils.deleteWhitespace (dictionary.getStrValue ());
            }
            boolean isOk = redisCacheApi.set (key, value);
            return value;
        } catch (Exception e) {
            logger.error ("获取缓存配置key异常", e);
            return null;
        }
    }

//    /**
//     * @描述:企业支付宝收款后自动归集分润
//     * @时间:2018年10月16日 下午5:43:14
//     */
//    @Transactional (readOnly = false, rollbackFor = Exception.class)
//    public JSONObject aliPaySettleProfit (VcOnlineOrder vcOnlineOrder, int tryTimes,boolean isUpdateNo,boolean isDeleteNo) {
//        try {
//            if (vcOnlineOrder == null || StringUtil.isEmpty (vcOnlineOrder.getpRescode ())) {
//                return Constant.failedMsg ("订单或者分润码为空");
//            }
//            String resCode = vcOnlineOrder.getpRescode ();
//            if ("4".equals (vcOnlineOrder.getpRescode ())) {
//                return Constant.failedMsg ("订单已经归集");
//            }else if (!"0".equals (resCode) && !"3".equals (resCode) ) {
//                return Constant.failedMsg ("未知的归集码"+resCode);
//            }
//            //收款账号信息
//            String upMerchNo = vcOnlineOrder.getUpMerchNo ();
//            String upMerchkey = vcOnlineOrder.getUpMerchKey ();
//            String orderNo = vcOnlineOrder.getOrderNo ();
//            SupplierSubno subno = supplierSubnoService.getOneByUpMerchNo (upMerchNo);
//            if (subno == null) {
//                return Constant.failedMsg (upMerchNo+"收款账号信息为空");
//            }
//            String APP_ID = subno.getAppId ();
//            String APP_PRIVATE_KEY = subno.getPrivateKey ();
//            String ALIPAY_PUBLIC_KEY = subno.getPublicKey ();
//            String trans_out = subno.getUserId ();
//            //收款账号配置一对一归集账号
//            SettleSubno settleSubno = new SettleSubno ();
//            if (subno.getSettleSubNoId () != null) {
//                settleSubno = settleSubnoService.get (subno.getSettleSubNoId ());
//                if (settleSubno == null || settleSubno.getUserId () == null) {
//                    return Constant.failedMsg ("未配置归集账号，结算失败");
//                }
//                if (settleSubno.getStatus () != 1) {
//                    return Constant.failedMsg ("配置归集账号，已被禁用，结算失败");
//                }
//                double traAmount = vcOnlineOrder.getTraAmount ().doubleValue ();
//                if (settleSubno.getMinPrice () >= 0 && settleSubno.getMaxPrice () > 0 && settleSubno.getMinPrice () <= traAmount && settleSubno.getMaxPrice () >= traAmount) {
//                    logger.info ("归集账号设置正常，可进行结算:{}", orderNo);
//                } else {
//                    return Constant.failedMsg ("已超出归集账号限额，结算失败");
//                }
//            } else {
//                //自动轮询一个归集账号
//                settleSubno = settleSubnoService.getOneSettleNo (new SettleSubno (vcOnlineOrder.getTraAmount ()));
//            }
//            if (settleSubno == null || settleSubno.getUserId () == null) {
//                return Constant.failedMsg ("未配置归集账号，结算失败");
//            }
//            String settleUpMerchNo = settleSubno.getUpMerchNo ();
//            String trans_in = settleSubno.getUserId ();
//            if (StringUtils.isEmpty (APP_ID) || StringUtils.isEmpty (APP_PRIVATE_KEY) || StringUtils.isEmpty (ALIPAY_PUBLIC_KEY) || StringUtils.isEmpty (trans_out)) {
//                return Constant.failedMsg ("信息不全，结算失败");
//            }
//            //归集账号信息
//            String pOrderNo = vcOnlineOrder.getPOrder ();
//            BigDecimal settleAmount = Constant.format2BigDecimal (vcOnlineOrder.getTraAmount ().multiply (settleSubno.getSettleRate ().divide (new BigDecimal (100))).stripTrailingZeros ());
//            //判断日限额和总归集额
//            if (settleSubno.getDayQuotaAmount ().compareTo (settleSubno.getDayTraAmount ().add (settleAmount)) < 0) {
//                return Constant.failedMsg ("超出归集账号日限额，结算失败");
//            }
//            if (settleSubno.getQuotaAmount ().compareTo (settleSubno.getTraAmount ().add (settleAmount)) < 0) {
//                return Constant.failedMsg ("超出归集账号总限额，结算失败");
//            }
//            synchronized (orderNo){
//                AlipayTradeOrderSettleResponse response = this.aliPaySettle (vcOnlineOrder,subno,trans_in,settleAmount,tryTimes);
//                VcOnlineOrder order = new VcOnlineOrder (vcOnlineOrder.getOrderNo ());
//                String orderDesc = "无法分润";
//                if (response != null && response.isSuccess ()) {
//                    //等待接收分润通知
//                    this.alipayOrderSettleListener(orderNo,upMerchNo,upMerchkey,true);
//
//                    int r = supplierSubnoService.updateSettleAmount (new SupplierSubno(upMerchNo,settleAmount));
//                    if (r < 1) {
//                        return Constant.failedMsg ("收款账号结算信息更新失败"+orderNo);
//                    }
//                    r = settleSubnoService.updateSettleAmount (new SettleSubno (settleUpMerchNo,settleAmount));
//                    if (r < 1) {
//                        return Constant.failedMsg (orderNo+"结算账号更新失败");
//                    }
//                    order.setpRescode("4");
//                    order.setPayCode (settleAmount.toPlainString ());
//                    order.setSmstrxid (settleUpMerchNo);
//                    vcOnlineOrderService.updateProfitStatus (order);
//                    return Constant.successMsg (orderNo+"分润成功");
//                } else {
//                    if (tryTimes > 0) {
//                        tryTimes = tryTimes - 1;
//                        return aliPaySettleProfit (vcOnlineOrder, tryTimes,isUpdateNo,isDeleteNo);
//                    }
//                    order.setpRescode ("3");
//                    order.setOrderDes (orderDesc);
//                    //手工结算失败不更新结算账号
//                    if(isUpdateNo){
//                        order.setSmstrxid (settleUpMerchNo);
//                    }
//                    vcOnlineOrderService.updateProfitStatus (order);
//                    if (!"ACQ.TRADE_SETTLE_ERROR".equalsIgnoreCase(response.getSubCode()) && !response.getSubMsg().contains("繁忙") && isDeleteNo) {
//                        logger.info ("企业支付宝结算分润失败orderNo:{},pOrderNo:{},settleNo:{},upMerchNo:{},upMerchkey:{},isOk{}", orderNo, pOrderNo, settleUpMerchNo, upMerchNo,upMerchkey);
//                    }
//                    return Constant.failedMsg (orderNo+"分润失败");
//                }
//            }
//        } catch (Exception e) {
//            logger.error ("企业支付宝自动归集分润异常", e);
//            return Constant.failedMsg ("归集分润异常");
//        }
//    }

    /**
     * @描述:企业支付宝收款后自动归集分润
     * @时间:2018年10月16日 下午5:43:14
     */
    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public JSONObject aliPaySettleProfit (VcOnlineOrder vcOnlineOrder, int tryTimes,boolean isUpdateNo,boolean isDeleteNo) {
        try {
            if (vcOnlineOrder == null || StringUtil.isEmpty (vcOnlineOrder.getpRescode ())) {
                return Constant.failedMsg ("订单或者分润码为空");
            }
            String resCode = vcOnlineOrder.getpRescode ();
            if ("4".equals (vcOnlineOrder.getpRescode ())) {
                return Constant.failedMsg ("订单已经归集");
            }else if (!"0".equals (resCode) && !"3".equals (resCode) ) {
                return Constant.failedMsg ("未知的归集码"+resCode);
            }
            // 收款账号信息
            String upMerchNo = vcOnlineOrder.getUpMerchNo ();
            String upMerchkey = vcOnlineOrder.getUpMerchKey ();
            String orderNo = vcOnlineOrder.getOrderNo ();
            // 获取渠道信息
            SupplierSubno subno = supplierSubnoService.getOneByUpMerchNo (upMerchNo);
            if (subno == null) {
                return Constant.failedMsg (upMerchNo+"收款账号信息为空");
            }
            String APP_ID = subno.getAppId ();
            String APP_PRIVATE_KEY = subno.getPrivateKey ();
            String ALIPAY_PUBLIC_KEY = subno.getPublicKey ();
            String trans_out = subno.getUserId ();
            //收款账号配置一对一归集账号
            SettleSubno settleSubno = new SettleSubno ();
            if (subno.getSettleSubNoId () != null && subno.getSettleSubNoId () >0) {
                // 供应商信息
                settleSubno = settleSubnoService.get (subno.getSettleSubNoId ());
                if (settleSubno == null || settleSubno.getUserId () == null) {
                    return Constant.failedMsg ("未配置归集账号，结算失败");
                }
                if (settleSubno.getStatus () != 1) {
                    return Constant.failedMsg ("配置归集账号，已被禁用，结算失败");
                }
                double traAmount = vcOnlineOrder.getTraAmount ().doubleValue ();
                if (settleSubno.getMinPrice () >= 0 && settleSubno.getMaxPrice () > 0 && settleSubno.getMinPrice () <= traAmount && settleSubno.getMaxPrice () >= traAmount) {
                    logger.info ("归集账号设置正常，可进行结算:{}", orderNo);
                } else {
                    return Constant.failedMsg ("已超出归集账号限额，结算失败");
                }
            } else {
                //自动轮询一个归集账号
                settleSubno = settleSubnoService.getOneSettleNo (new SettleSubno (vcOnlineOrder.getTraAmount ()));
            }
            if (settleSubno == null || settleSubno.getUserId () == null) {
                return Constant.failedMsg ("未配置归集账号，结算失败");
            }
            String settleUpMerchNo = settleSubno.getUpMerchNo ();
            String trans_in = settleSubno.getUserId ();
            if (StringUtils.isEmpty (APP_ID) || StringUtils.isEmpty (APP_PRIVATE_KEY) || StringUtils.isEmpty (ALIPAY_PUBLIC_KEY) || StringUtils.isEmpty (trans_out)) {
                return Constant.failedMsg ("信息不全，结算失败");
            }
            //归集账号信息
            String pOrderNo = vcOnlineOrder.getPOrder ();
            BigDecimal settleAmount = Constant.format2BigDecimal (vcOnlineOrder.getTraAmount ().multiply (settleSubno.getSettleRate ().divide (new BigDecimal (100))).stripTrailingZeros ());
            //判断日限额和总归集额
            if (settleSubno.getDayQuotaAmount ().compareTo (settleSubno.getDayTraAmount ().add (settleAmount)) < 0) {
                return Constant.failedMsg ("超出归集账号日限额，结算失败");
            }
            if (settleSubno.getQuotaAmount ().compareTo (settleSubno.getTraAmount ().add (settleAmount)) < 0) {
                return Constant.failedMsg ("超出归集账号总限额，结算失败");
            }
            synchronized (orderNo){
                JSONObject response =  this.aliPaytransfer (vcOnlineOrder,subno,trans_in,settleAmount,settleUpMerchNo,tryTimes);
                // 归集方式 1 自动分润 ，2 单笔转账
                //String funpayOpenType = this.getCacheCfgKey("online.alipay.settle.type");
                /*if(StringUtil.isNotEmpty (funpayOpenType) && funpayOpenType.equals ("1")){
                    response = this.aliPaySettle (vcOnlineOrder,subno,trans_in,settleAmount,tryTimes);
                }else{*/
                //}
                logger.info ("分账响应结果:{},{}",vcOnlineOrder.getOrderNo(),response);
                VcOnlineOrder order = new VcOnlineOrder (vcOnlineOrder.getOrderNo ());
                String orderDesc = "无法分润";
                if (response != null && response.get ("code").equals (Constant.SUCCESSS)) {
                    //等待接收分润通知
                    this.alipayOrderSettleListener(orderNo,upMerchNo,upMerchkey,true);
                    int r = supplierSubnoService.updateSettleAmount (new SupplierSubno(upMerchNo,settleAmount));
                    if (r < 1) {
                        return Constant.failedMsg ("收款账号结算信息更新失败"+orderNo);
                    }
                    r = settleSubnoService.updateSettleAmount (new SettleSubno (settleUpMerchNo,settleAmount));
                    if (r < 1) {
                        return Constant.failedMsg (orderNo+"结算账号更新失败");
                    }
                    order.setpRescode("4");
                    order.setPayCode (settleAmount.toPlainString ());
                    order.setSmstrxid (settleUpMerchNo);
                    vcOnlineOrderService.updateProfitStatus (order);
                    asynAliPayMerchMonitor.orderMonitor (upMerchNo, upMerchkey, true);
                    return Constant.successMsg (orderNo+"分润成功");
                } else {
                    if (tryTimes > 0) {
                        tryTimes = tryTimes - 1;
                        return aliPaySettleProfit (vcOnlineOrder, tryTimes,isUpdateNo,isDeleteNo);
                    }
                    order.setpRescode ("3");
                    order.setOrderDes (orderDesc);
                    //手工结算失败不更新结算账号
                    if(isUpdateNo){
                        order.setSmstrxid (settleUpMerchNo);
                    }
                    vcOnlineOrderService.updateProfitStatus (order);
                    String subCode = response.containsKey ("subCode")?response.getString ("subCode"):"";
                    String message = response.containsKey ("message")?response.getString ("message"):"";
                    if (!"PAYER_BALANCE_NOT_ENOUGH".equalsIgnoreCase(subCode) &&  !"ACQ.TRADE_SETTLE_ERROR".equalsIgnoreCase(subCode) && !message.contains("繁忙") && isDeleteNo) {
                        boolean isOk = asynAliPayMerchMonitor.orderMonitor (upMerchNo, upMerchkey, false);
                        logger.info ("企业支付宝结算分润失败orderNo:{},pOrderNo:{},settleNo:{},upMerchNo:{},upMerchkey:{},isOk{}", orderNo, pOrderNo, settleUpMerchNo, upMerchNo,upMerchkey,isOk);
                    }
                    return Constant.failedMsg (orderNo+"分润失败");
                }
            }
        } catch (Exception e) {
            logger.error ("企业支付宝自动归集分润异常", e);
            return Constant.failedMsg ("归集分润异常");
        }
    }

    /**
     * @描述:单笔转账支付
     * @时间:2018年6月14日 下午5:33:19
     */
    public JSONObject aliPaytransfer(VcOnlineOrder vcOnlineOrder,SupplierSubno subno,String trans_in,BigDecimal settleAmount,String settleUpMerchNo,int tryTimes) {
        JSONObject result = new JSONObject ();
        try {
            String orderNo = vcOnlineOrder.getOrderNo ();
            String pOrderNo = vcOnlineOrder.getPOrder ();
            String APP_ID = subno.getAppId ();
            String APP_PRIVATE_KEY = subno.getPrivateKey ();
            String ALIPAY_PUBLIC_KEY = subno.getPublicKey ();
            String trans_out = subno.getUserId ();

            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",APP_ID,APP_PRIVATE_KEY,"json","UTF-8",ALIPAY_PUBLIC_KEY,"RSA2");
            AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
            JSONObject map = new JSONObject();
            map.put("out_biz_no",orderNo);
            map.put("payee_type","ALIPAY_LOGONID");
            map.put("payee_account",settleUpMerchNo);
            map.put("amount",settleAmount.toPlainString ());
            map.put("payer_show_name",trans_out);
            //map.put("payee_real_name",trans_in);
            map.put("remark","转账" + orderNo + ":" + settleAmount);
            request.setBizContent(map.toString());
            AlipayFundTransToaccountTransferResponse response = null;
            try {
                logger.info ("企业支付宝转账结算入参,{},分润金额{}", map, settleAmount);
                response = alipayClient.execute(request);
                logger.info ("企业支付宝结算转账分润订单:{},结果{}", orderNo, response.getBody ());
                if(response.isSuccess()){
                    result = Constant.successMsg ("转账分润成功");
                } else {
                    result = Constant.failedMsg (response.getSubMsg());
                    result.put ("subCode",response.getSubCode());
                }
                return result;
            } catch (AlipayApiException e) {
                logger.info ("企业支付宝结算超时,重新分润{},{}", orderNo, response.getBody ());
                if (tryTimes > 0) {
                    tryTimes = tryTimes - 1;
                    return aliPaytransfer (vcOnlineOrder,subno,trans_in,settleAmount,settleUpMerchNo,tryTimes);
                }
            }
            return result;
        } catch (Exception e) {
            logger.error ("单笔转账支付异常", e);
            result = Constant.failedMsg ("分账异常");
            return  result;
        }
    }

    /**
     * @描述:支付宝结算接口
     * @作者:nada
     * @时间:2019/1/9
     **//*
    public AlipayTradeOrderSettleResponse aliPaySettle (VcOnlineOrder vcOnlineOrder,SupplierSubno subno,String trans_in,BigDecimal settleAmount,int tryTimes) {
        try {
            //订单信息
            String orderNo = vcOnlineOrder.getOrderNo ();
            String pOrderNo = vcOnlineOrder.getPOrder ();
            //归集账号信息
            String APP_ID = subno.getAppId ();
            String APP_PRIVATE_KEY = subno.getPrivateKey ();
            String ALIPAY_PUBLIC_KEY = subno.getPublicKey ();
            String trans_out = subno.getUserId ();
            AlipayClient alipayClient = new DefaultAlipayClient ("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
            AlipayTradeOrderSettleRequest request = new AlipayTradeOrderSettleRequest ();
            String notifyUrl = Constant.buildUrl(domainName,actualName,"/funpMerchH5CallBackApi/settle");
            request.setNotifyUrl(notifyUrl);
            List<Object> lists = new ArrayList<Object> ();
            Map<String, Object> map = new HashMap<String, Object> ();
            map.put ("trans_out", trans_out);
            map.put ("trans_in", trans_in);
            map.put ("amount", settleAmount.toPlainString ());
            map.put ("desc", "分润" + orderNo + ":" + settleAmount);
            lists.add (map);
            JSONObject map2 = new JSONObject ();
            map2.put ("out_request_no", orderNo);
            map2.put ("trade_no", pOrderNo);
            map2.put ("royalty_parameters", lists);
            map2.put ("operator_id", vcOnlineOrder.getMerchNo ());
            request.setBizContent (map2.toString ());
            AlipayTradeOrderSettleResponse response = null;
            try {
                logger.info ("企业支付宝结算入参,{},分润金额{}", map2, settleAmount);
                response = alipayClient.execute (request);
                logger.info ("企业支付宝结算分润订单:{},结果{}", orderNo, response.getBody ());
            } catch (AlipayApiException e) {
                logger.info ("企业支付宝结算超时,重新分润{},{}", orderNo, response.getBody ());
                if (tryTimes > 0) {
                    tryTimes = tryTimes - 1;
                    return aliPaySettle (vcOnlineOrder, subno, trans_in, settleAmount, tryTimes);
                }
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace ();
            return  null;
        }
    }*/

    /**
     * @描述:支付宝结算接口
     * @作者:nada
     * @时间:2019/1/9
     **/
    public JSONObject aliPaySettle (VcOnlineOrder vcOnlineOrder,SupplierSubno subno,String trans_in,BigDecimal settleAmount,int tryTimes) {
        JSONObject result = new JSONObject ();
        try {
            //订单信息
            String orderNo = vcOnlineOrder.getOrderNo ();
            String pOrderNo = vcOnlineOrder.getPOrder ();
            //归集账号信息
            String APP_ID = subno.getAppId ();
            String APP_PRIVATE_KEY = subno.getPrivateKey ();
            String ALIPAY_PUBLIC_KEY = subno.getPublicKey ();
            String trans_out = subno.getUserId ();
            AlipayClient alipayClient = new DefaultAlipayClient ("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
            AlipayTradeOrderSettleRequest request = new AlipayTradeOrderSettleRequest ();
            String notifyUrl = Constant.buildUrl(domainName,actualName,"/funpMerchH5CallBackApi/settle");
            request.setNotifyUrl(notifyUrl);
            List<Object> lists = new ArrayList<Object> ();
            Map<String, Object> map = new HashMap<String, Object> ();
            map.put ("trans_out", trans_out);
            map.put ("trans_in", trans_in);
            map.put ("amount", settleAmount.toPlainString ());
            map.put ("desc", "分润" + orderNo + ":" + settleAmount);
            lists.add (map);
            JSONObject map2 = new JSONObject ();
            map2.put ("out_request_no", orderNo);
            map2.put ("trade_no", pOrderNo);
            map2.put ("royalty_parameters", lists);
            map2.put ("operator_id", vcOnlineOrder.getMerchNo ());
            request.setBizContent (map2.toString ());
            AlipayTradeOrderSettleResponse response = null;
            try {
                logger.info ("企业支付宝结算入参,{},分润金额{}", map2, settleAmount);
                response = alipayClient.execute (request);
                logger.info ("企业支付宝结算分润订单:{},结果{}", orderNo, response.getBody ());
                if (response != null && response.isSuccess ()) {
                    result = Constant.successMsg ("转账分润成功");
                }else{
                    result = Constant.failedMsg (response.getSubMsg());
                    result.put ("subCode",response.getSubCode());
                }
                return result;
            } catch (AlipayApiException e) {
                logger.info ("企业支付宝结算超时,重新分润{},{}", orderNo, response.getBody ());
                if (tryTimes > 0) {
                    tryTimes = tryTimes - 1;
                    return aliPaySettle (vcOnlineOrder, subno, trans_in, settleAmount, tryTimes);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace ();
            logger.error ("支付宝结算接口异常", e);
            result = Constant.failedMsg ("分润异常");
            return  result;
        }
    }

    /**
     * 监控支付宝分润结算回调
     * @return
     */
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public boolean alipayOrderSettleListener(String orderNo,String upMerchNo,String upMerchkey,boolean isSave){
        return true;
//        synchronized (orderNo){
//            if(isSave){
//                return this.saveCacheOrderSettle(orderNo,upMerchNo,upMerchkey);
//            }else{
//                return this.removeCacheOrderSettle(orderNo);
//            }
//        }
    }
    private boolean saveCacheOrderSettle(String orderNo,String upMerchNo,String upMerchkey){
        try {
            JSONObject data = new JSONObject();
            data.put("time",System.currentTimeMillis());
            data.put("upMerchNo",upMerchNo);
            data.put("upMerchkey",upMerchkey);
            ORDER_SETTLE_LIST.put(orderNo,data);
            logger.info ("企业支付宝结算等待回调列表:{}",ORDER_SETTLE_LIST);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean removeCacheOrderSettle(String orderNo){
        try {
            logger.info ("企业支付宝结算回调等待进程列表开始:{}",ORDER_SETTLE_LIST);
            if(!ORDER_SETTLE_LIST.containsKey(orderNo)){
                return true;
            }
            ORDER_SETTLE_LIST.remove(orderNo);
            List<String> removedList = new ArrayList<>();
            List<String> removeOrderList = new ArrayList<>();
            for(String order : ORDER_SETTLE_LIST.keySet()){
                //五分钟没回调的认为结算失败，剔除账号
                JSONObject data = ORDER_SETTLE_LIST.get(order);
                if(null == data || data.isEmpty()){
                    removeOrderList.add(order);
                    continue;
                }
                if(System.currentTimeMillis() - data.getLong("time") >= CacheConstants.DEFAULT_INVALID_TIMER_30){
                    removeOrderList.add(order);
                    String upMerchNo = data.getString("upMerchNo");
                    String upMerchkey = data.getString("upMerchkey");
                    if(removedList.contains(upMerchNo)){
                        continue;
                    }
                    removedList.add(upMerchNo);
//                    boolean isOk = asynAliPayMerchMonitor.monitorSettleNotice (upMerchNo,upMerchkey,order);
                    logger.info ("企业支付宝结算回调超过三十分钟orderNo:{},移除账号upMerchNo:{},upMerchkey:{},isOk{}", order, upMerchNo,upMerchkey);
                }
            }
            for(String obj : removeOrderList){
                ORDER_SETTLE_LIST.remove(obj);
            }
            logger.info ("企业支付宝结算回调等待进程列表结束:{}",ORDER_SETTLE_LIST);
            return true;
        } catch (Exception e) {
            logger.error ("企业支付宝结算回调处理异常", e);
        }
        return false;
    }
}
