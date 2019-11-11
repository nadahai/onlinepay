/**
 * @类名称:MerchChannelServiceImpl.java
 * @时间:2017年6月5日下午10:43:53
 * @作者:lihai
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.persistent.service.channel;

import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.entity.channel.ChannelSubNo;
import com.vc.onlinepay.persistent.entity.channel.MerchChannel;
import com.vc.onlinepay.persistent.entity.merch.SupplierSubno;
import com.vc.onlinepay.persistent.mapper.channel.MerchChannelMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @描述:商户通道配置信息
 * @作者:lihai
 * @时间:2017年6月5日 下午10:43:53
 */
@Service
@Transactional (readOnly = true,rollbackFor = Exception.class)
public class MerchChannelServiceImpl {

    public static final Logger logger = LoggerFactory.getLogger (MerchChannelServiceImpl.class);
    @Autowired
    private MerchChannelMapper merchChannelMapper;
    @Autowired
    private ChannelSubNoServiceImpl channelSubNoService;
    @Autowired
    private SupplierSubnoServiceImpl supplierSubnoService;
    @Autowired
    private RedisCacheApi redisCacheApi;
    
    /**
     * @描述:更新下单时间
     * @时间:2018年5月17日 下午5:57:58
     */
    @Transactional (readOnly = false)
    public Integer updateLastOrderTime (ChannelSubNo channelSubNo) throws OnlineServiceException {
        try {
            if (StringUtils.isEmpty (channelSubNo.getUpMerchNo ()) || "0".equals (channelSubNo.getUpMerchNo ())) {
                return 0;
            }
            return channelSubNoService.updateLastOrderTime (channelSubNo);
        } catch (Exception e) {
            logger.error ("更新下单信息异常", e);
            return 0;
        }
    }
    
    /**
     * @描述:根据商户编号,支付方式，结算方式 查商户通道信息
     * @时间:2017年6月5日 下午10:46:05
     */
    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public void updateSubNoStatus (String upMerchNo, String supplierNo, String remark) {
        try {
            SupplierSubno subno = new SupplierSubno ();
            subno.setUpMerchNo (upMerchNo);
            subno.setStatus (2);
            subno.setRemark (remark);
            supplierSubnoService.updateStatus (subno);
            redisCacheApi.remove (CacheConstants.LOOP_ROBIN_ALIPAY_NO + upMerchNo);
            if (StringUtils.isNoneBlank (supplierNo)) {
                redisCacheApi.remove (CacheConstants.LOOP_ROBIN_ALIPAY_MAP + supplierNo);
            }
        } catch (Exception e) {
            logger.error ("修改支付宝账号状态异常", e);
        }
    }

    /**
     * @描述:根据商户编号,支付方式，结算方式 查商户通道信息
     * @时间:2017年6月5日 下午10:46:05
     */
    public List<MerchChannel> findMerchChannel (Long merchId, List<Long> payTypes) throws OnlineServiceException {
        return merchChannelMapper.findMerchChannelPayTypes (merchId, payTypes);
    }

    /**
     * @描述:根据商户编号,支付方式，结算方式 查商户通道信息
     * @时间:2017年6月5日 下午10:46:05
     */
    public List<MerchChannel> findMerchChannelPayTypes (MerchChannel merchChannel) throws OnlineServiceException {
        return merchChannelMapper.findMerchChannelPayTypes (merchChannel.getMerchId (), merchChannel.getPayTypes ());
    }

    /**
     * @描述:根据支付方式和商户id获取结算方式
     * @时间:2017年12月18日 下午10:46:05
     */
    public List<MerchChannel> findCanUsedMerchChannelByMerchId (Long merchId, Long payType) throws OnlineServiceException {
        return merchChannelMapper.findCanUsedMerchChannelByMerchId (merchId, payType);
    }

    /**
     * @描述:获取一个子商户号
     * @时间:2018年5月17日 下午5:57:58
     */
    public ChannelSubNo getChannelSubNo (ChannelSubNo channelSubNo, double traAmount) throws OnlineServiceException {
        List<ChannelSubNo> lists = this.getChannelSubNoList(channelSubNo);
        if (lists == null || lists.size () < 1) {
            return null;
        }
        if (lists.size () == 1) {
            return lists.get (0);
        }
        //金额筛选
        List<ChannelSubNo> okList = new ArrayList<ChannelSubNo>();
        for (ChannelSubNo no : lists) {
            if (no.getMinPrice () > 0 && no.getMaxPrice () > 0 && no.getMinPrice () <= traAmount && no.getMaxPrice () > traAmount) {
                okList.add (no);
            }
        }
        //TODO
    	/*
        int index = coreEngineProviderService.getCacheKeyValOrUpdateVal (CacheConstants.CHANNEL_LIST_POLLING_INDEX,okList.size ());
	
		 * //随机取出一个商户号 int listLen = okList.size (); if (listLen < 1) { return lists.get
		 * (new Random ().nextInt (lists.size ())); } return okList.get (new Random
		 * ().nextInt (listLen));
		 
        //轮询取出一个
        return okList.get (index);
        */
        
      //随机取出一个商户号
        int listLen = okList.size ();
        if (listLen < 1) {
            return lists.get (new Random ().nextInt (lists.size ()));
        }
        return okList.get (new Random ().nextInt (listLen));
    }

    /**
     * 获取子商户号缓存列表
     */
    public List<ChannelSubNo> getChannelSubNoList (ChannelSubNo channelSubNo) throws OnlineServiceException {
        return channelSubNoService.getChannelSubNoList (channelSubNo);
    }


    /**
     * 获取子商户号缓存列表
     */
    public ChannelSubNo getoneChannelSubNo (ChannelSubNo channelSubNo) throws OnlineServiceException {
        List<ChannelSubNo> list = channelSubNoService.getNoCacheChannelSubNoList (channelSubNo);
        if(list == null || list.size() <=0){
            return  null;
        }
        return list.get(0);
    }

    /**
     * @描述:拉卡拉算法
     * @时间:2018年5月17日 下午5:57:58
     */
    public ChannelSubNo getChannelLimitTimeSubNoList (ChannelSubNo channelSubNo, int sameOrderExpiredTime) throws OnlineServiceException {
        List<ChannelSubNo> lists = channelSubNoService.getChannelLimitTimeSubNoList (channelSubNo);
        if (lists == null || lists.size () < 1) {
            return null;
        }
        if (lists.size () == 1) {   return lists.get (0);
        }

        double traAmount = channelSubNo.getTraAmount ().doubleValue ();
        //查找时间段内下过单的相同金额的商户号
        if (sameOrderExpiredTime > 0) {
            List<String> usedMerchNos = channelSubNoService.findUsedMerchNo (channelSubNo);
            for (ChannelSubNo subno : lists) {
                if (usedMerchNos != null && !usedMerchNos.isEmpty () && usedMerchNos.contains (subno.getUpMerchNo ())) {
                    continue;
                }
                if (subno.getMinPrice () > 0 && subno.getMaxPrice () > 0 && subno.getMinPrice () < traAmount && subno.getMaxPrice () > traAmount) {
                    return subno;
                }
            }
        }
        for (ChannelSubNo no : lists) {
            if (no.getMinPrice () > 0 && no.getMaxPrice () > 0 && no.getMinPrice () < traAmount && no.getMaxPrice () > traAmount) {
                return no;
            }
        }
        return lists.get (0);
    }

    /**
     * @描述:修改子商户日额度
     * @时间:2018年5月17日 下午5:57:58
     */
    @Transactional (readOnly = false)
    public Integer updateSubNoAmount (ChannelSubNo channelSubNo) throws OnlineServiceException {
        try {
            if (StringUtils.isEmpty (channelSubNo.getUpMerchNo ()) || "0".equals (channelSubNo.getUpMerchNo ())) {
                return 0;
            }
            return channelSubNoService.updateSubNoAmount (channelSubNo);
        } catch (Exception e) {
            logger.error ("修改子商户日额度异常", e);
            return 0;
        }
    }

    /**
     * @描述:修改账号日额度
     * @时间:2018年5月17日 下午5:57:58
     */
    @Transactional (readOnly = false)
    public Integer updateAlipaySubNoAmount (SupplierSubno subno) throws OnlineServiceException {
        try {
            if (StringUtils.isEmpty (subno.getUpMerchNo ()) || "0".equals (subno.getUpMerchNo ())) {
                return 0;
            }
            String key = CacheConstants.LOOP_ROBIN_ALIPAY_NO + subno.getUpMerchNo ();
            redisCacheApi.remove (key);
            return supplierSubnoService.updateSubNoAmount (subno);
        } catch (Exception e) {
            logger.error ("修改子商户日额度异常", e);
            return 0;
        }
    }

    /**
     * @描述:大额路由
     * @作者:ChaiJing THINK
     * @时间:2018/9/21 12:39
     */
    public MerchChannel getUpPriceChannel (String orderAmount, String routeAmountStr, Long newchannelSource, MerchChannel merchChannel) {
        BigDecimal traAmount = new BigDecimal (orderAmount);
        BigDecimal routeAmount = new BigDecimal (routeAmountStr);
        if (routeAmount.compareTo (traAmount) == 1) {
            return null;
        }
        return getMoneyRoutChannel (newchannelSource, merchChannel);
    }

    /**
     * @描述:获取相同支付方式结算方式通道
     * @备注：null则通过，其他返回订单会跳到指定通道
     * @作者:ChaiJing THINK
     * @时间:2018/8/22 16:33
     */
    public MerchChannel getMoneyRoutChannel (Long newchannelSource, MerchChannel merchChannel) {
        try {
            MerchChannel tempChannel = findSameChannelBysource (new MerchChannel (merchChannel.getPayType (), merchChannel.getSettleType (), newchannelSource));
            if (tempChannel == null || tempChannel.getStatus () != 1L) {
                return null;
            }
            tempChannel.setMerchId (merchChannel.getMerchId ());
            tempChannel.setTranRate (merchChannel.getTranRate ());
            tempChannel.setAgentId (merchChannel.getAgentId ());
            tempChannel.setMangerId (merchChannel.getMangerId ());
            tempChannel.setAgentRate (merchChannel.getAgentRate ());
            tempChannel.setMangerRate (merchChannel.getMangerRate ());
            merchChannel = tempChannel;
            return merchChannel;
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return null;
    }

    /**
     * @描述: 从缓存获取通道信息
     * @作者:nada
     * @时间:2018/12/21
     **/
    public MerchChannel findSameChannelBysource (MerchChannel merchChannel) {
        MerchChannel cachechannel = null;
        String key = CacheConstants.CACHE_TABLE_KEY_CHANNEL + merchChannel.getChannelSource () + CacheConstants.COMMON_SPLIT + merchChannel.getPayType () + CacheConstants.COMMON_SPLIT + merchChannel.getSettleType ();
        if (redisCacheApi.exists (key)) {
            cachechannel = (MerchChannel) redisCacheApi.getBean (key, MerchChannel.class);
            return cachechannel;
        }
        if (cachechannel == null) {
            List<MerchChannel> list = merchChannelMapper.findSameChannelBysource (merchChannel);
            cachechannel = (list == null || list.isEmpty ()) ? null : list.get (0);
        }
        if (cachechannel != null) {
            boolean isOk = redisCacheApi.setBean (key, cachechannel, MerchChannel.class);
        }
        return cachechannel;
    }

    /**
     * @描述: 通道金额路由
     * @作者:nada
     * @时间:2018/12/21
     **/
    public MerchChannel getRouteChannel (MerchChannel merchChannel, BigDecimal traAmount, String merchantId) {
        if (null == merchChannel || merchChannel.getRouteChannel () < 1) {
            return null;
        }
        //
        if (StringUtils.isNotBlank (merchChannel.getIncludeMerchno ()) && !merchChannel.getIncludeMerchno ().contains (merchantId)) {
            return null;
        }
        //
        if (StringUtils.isNotBlank (merchChannel.getExcludeMerchno ()) && merchChannel.getExcludeMerchno ().contains (merchantId)) {
            return null;
        }
        //大于最小金额(包含)，自动路由
        if (null != merchChannel.getRouteMinAmount () && merchChannel.getRouteMinAmount ().compareTo (BigDecimal.ZERO) == 1) {
            if (traAmount.compareTo (merchChannel.getRouteMinAmount ()) == -1) {
                return null;
            }
        }
        //小于最大金额(包含)，自动路由
        if (null != merchChannel.getRouteMaxAmount () && merchChannel.getRouteMaxAmount ().compareTo (BigDecimal.ZERO) == 1) {
            if (traAmount.compareTo (merchChannel.getRouteMaxAmount ()) == 1) {
                return null;
            }
        }
        //正则匹配，匹配中的金额进行路由
        if (StringUtils.isNotBlank (merchChannel.getRouteRegularEx ())) {
            String amount = String.valueOf (traAmount.setScale (2, BigDecimal.ROUND_HALF_DOWN));
            if (!amount.matches (StringUtils.deleteWhitespace (merchChannel.getRouteRegularEx ()))) {
                return null;
            }
        }
        return getMoneyRoutChannel (merchChannel.getRouteChannel (), merchChannel);
    }
}

