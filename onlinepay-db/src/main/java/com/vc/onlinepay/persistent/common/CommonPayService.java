/**
 * @类名称:CommonPayServiceImpl.java
 * @时间:2018年3月2日上午11:14:33
 * @作者:nada
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.persistent.common;

import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.entity.merch.MerchInfo;
import com.vc.onlinepay.persistent.entity.online.VcOnlineLog;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.service.merch.MerchInfoServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineLogServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @描述:通用支付业务接口实现
 * @作者:nada
 * @时间:2018年3月2日 上午11:14:33 
 */
@Service
public class CommonPayService{

    public static final Logger logger = LoggerFactory.getLogger(CommonPayService.class);
    private static final long  cacheValidTime  = 60*1000;//1分钟
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Autowired
    private MerchInfoServiceImpl merchInfoService;
    @Autowired
    private VcOnlineLogServiceImpl onlineLogService;
    @Autowired
    private RedisCacheApi redisCacheApi;
    @Autowired
    private VcOnlinePaymentServiceImpl vcOnlinePaymentService;

    /**
     * @描述:高并发情况下验证代付订单号唯一性 /默认缓存1分钟后自动失效
     * @return 存在返回：true 不存在返回：false
     * @时间:2018年3月2日 上午11:16:49
     */
    public boolean verifyCacheReplaceOrderExist(String orderNo) throws OnlineServiceException{
        try {
            if(verifyOrderExist (orderNo)){
                return true;
            }
            List<VcOnlinePayment> payment = vcOnlinePaymentService.selectPaymentIsDouble(orderNo);
          return payment != null && payment.size() > 0;
        } catch (Exception e) {
            logger.error("高并发情况下验证订单号唯一性异常",e);
            throw new OnlineServiceException("高并发情况下验证订单号唯一性异常");
        }
    }
    
    /**
     * @描述:高并发情况下验证交易订单号唯一性 /默认缓存1分钟后自动失效
     * @return 存在返回：true 不存在返回：false
     * @时间:2018年3月2日 上午11:16:49
     */
    public boolean verifyCacheMerchOrderExist(String orderNo) throws OnlineServiceException{
        try {
            if(verifyOrderExist (orderNo)){
                return true;
            }
            List<VcOnlineOrder> orders = vcOnlineOrderService.verifyMerchOrderExist(orderNo);
          return orders != null && orders.size() > 0;
        } catch (Exception e) {
            logger.error("高并发情况下验证订单号唯一性异常",e);
            throw new OnlineServiceException("高并发情况下验证订单号唯一性异常");
        }
    }

    /**
     * @描述:验证商户订单号是否已经存在
     * @作者:nada
     * @时间:2018/12/26
     **/
    public boolean verifyOrderExist(String orderNo) throws OnlineServiceException{
        try {
            if(StringUtil.isEmpty(orderNo)){
                throw new OnlineServiceException("验证上送订单号为空");
            }
            synchronized(this) {
                if(redisCacheApi.exists(orderNo)){
                    return true;
                }
                redisCacheApi.set(orderNo,orderNo,cacheValidTime);
            }
            return  false;
        } catch (Exception e) {
            logger.error("高并发情况下验证订单号唯一性异常",e);
            throw new OnlineServiceException("高并发情况下验证订单号唯一性异常");
        }
    }

    /**
     * @描述:高并发情况下验证支付宝订单号唯一性 /默认缓存1分钟后自动失效
     * @return 存在返回：true 不存在返回：false
     * @时间:2018年10月17日 上午11:16:49
     */
    public boolean verifyCacheMerchPOrderExist(VcOnlineOrder vcOnlineOrder) throws OnlineServiceException{
        try {

            if(vcOnlineOrder == null || StringUtils.isAnyEmpty(vcOnlineOrder.getUpMerchNo(), vcOnlineOrder.getpOrder())){
                throw new OnlineServiceException("支付宝订单号为空");
            }
            String merchNoAndPorder = vcOnlineOrder.getUpMerchNo()+"_"+vcOnlineOrder.getpOrder();
            synchronized(this) {
                if(redisCacheApi.exists(merchNoAndPorder)){
                    return true;
                }else{
                    boolean isOk = redisCacheApi.set(merchNoAndPorder,merchNoAndPorder,cacheValidTime);
                }
            }
            List<VcOnlineOrder> list = vcOnlineOrderService.verifyPOrderExist(vcOnlineOrder);
          return list != null && list.size() > 0;
        } catch (Exception e) {
            logger.error("高并发下验证支付宝订单唯一性异常",e);
            throw new OnlineServiceException("高并发下验证支付宝订单号唯一性异常");
        }
    }

    /**
     * @描述:获取缓存商户信息
     * @时间:2018年3月2日 下午12:14:35
     */
    @SuppressWarnings("unused")
	public MerchInfo getCacheMerchInfo(String merchNo) throws OnlineServiceException {
        try {
            if(StringUtils.isEmpty(merchNo)){
                throw new OnlineServiceException("获取商户信息编号为空");
            }
            if(!Constant.isNumberChars(merchNo)){
                return null;
            }
            MerchInfo merchInfo =  merchInfoService.getMerchInfoByNo(Long.valueOf(merchNo));
            /*String key = CacheConstants.CACHE_TABLE_KEY_MERCH_INFO +merchNo;
            if(redisCacheApi.exists(key)){
                merchInfo = (MerchInfo)redisCacheApi.getBean(key,MerchInfo.class);
            }
            if(merchInfo == null){
            	 merchInfo = merchInfoService.getMerchInfoByNo(Long.valueOf(merchNo));
                 if (merchInfo != null) {
                	 boolean isOk = redisCacheApi.setBean(key, merchInfo,MerchInfo.class);
                     return merchInfo;
                 }
            }*/
            if (merchInfo == null) {
                logger.error("获取商户信息为空,商户编号:{}",merchNo);
                return null;
            }
            return merchInfo;
        } catch (Exception e){
            logger.error("获取商户信息异常",e);
            throw new OnlineServiceException("获取商户信息异常");
        }
    }

    /**
     * @描述:保存埋点日志
     * @时间:2018年3月8日 下午2:10:35
     */
    public boolean saveLog(String title,String... logDes) throws OnlineServiceException {
        try {
            StringBuffer str = new StringBuffer();
            if(logDes!=null && logDes.length > 0){
                for (String arg : logDes) {
                    str.append(arg);
                }
            }
            int i = onlineLogService.save(new VcOnlineLog(VcOnlineLog.LEVEL_ERROR,VcOnlineLog.BUS_LOG,title,str.toString()));
          return i > 0;
        } catch (Exception e) {
            logger.error("保存埋点日志异常",e);
            return false;
        }
    }
    /**
     * @描述:签名错误日志
     * @时间:2018/7/23 10:32
     */
    public void saveSignLog(String title,String orderId,String merchNo, String signStr, String sign) {
        try {
            signStr = signStr.substring(0,signStr.indexOf("&key=")+5)+"***";
            onlineLogService.save(new VcOnlineLog(VcOnlineLog.LEVEL_WARN,VcOnlineLog.PAYAPI_LOG,title,"签名串:"+signStr+";签名:"+sign,merchNo,orderId));
        } catch (Exception e) {
            logger.error("保存签名错误日志异常",e);
        }
    }
    
    
    /**
     * @描述:更新订单状态
     * @作者:nada
     * @时间:2018年3月8日 上午10:40:32
     */
    public boolean updateOrderDes(String orderNo, String orderDes) throws OnlineServiceException {
    	try {
            if(StringUtil.isEmpty(orderNo)){
                logger.error("更新订单状态订单号为空");
                return false;
            }
            if(StringUtil.isEmpty(orderDes)){
                logger.error("更新订单状态描述为空");
                orderDes = "更新订单状态描述为空";
            }
            VcOnlineOrder order = new VcOnlineOrder();
            order.setOrderNo(orderNo);
            order.setOrderDes(orderDes);
            int i = vcOnlineOrderService.updateOrderDes(order);
          return i > 0;
        } catch (Exception e) {
            logger.error("更新订单描述",e);
            return false;
        }
    }
    
    /**
     * @描述:更新订单状态
     * @作者:nada
     * @时间:2018年3月8日 上午10:40:32
     */
    public boolean updateOrderStatus(String orderNo, int status, String orderDes) throws OnlineServiceException {
        return this.updateOrderStatus(orderNo,status,orderDes,null);
    }
    public boolean updateOrderStatus(String orderNo, int status, String orderDes,String pOrder) throws OnlineServiceException {
        return this.updateOrderStatus(orderNo,status,orderDes,pOrder,null);
    }
    /**
     * @描述:更新订单状态
     * @作者:nada
     * @时间:2018/12/26
     **/
    public boolean updateOrderStatus(String orderNo, int status, String orderDes, String pOrder, String realAmount) throws OnlineServiceException {
        try {
            if(StringUtil.isEmpty(orderNo)){
                logger.error("更新订单状态订单号为空");
                return false;
            }
            if(status<1 || status > 10){
                logger.error("更新订单状态不正确{},{}",orderNo,status);
                return false;
            }
            if(StringUtil.isEmpty(orderDes)){
                logger.error("更新订单状态描述为空");
                orderDes = "更新订单状态描述为空";
            }
            VcOnlineOrder order = new VcOnlineOrder();
            order.setOrderNo(orderNo);
            order.setStatus(status);
            order.setOrderDes(orderDes);
            order.setpOrder(pOrder);
            order.setPayCode(realAmount);
            int i = vcOnlineOrderService.updateOrderStatus(order);
          return i > 0;
        } catch (Exception e) {
            logger.error("更新订单状态异常",e);
            return false;
        }
    }

}

