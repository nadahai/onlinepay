/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.channel;

import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.exception.OnlineDbException;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.channel.ChannelSubNo;
import com.vc.onlinepay.persistent.mapper.channel.ChannelSubNoMapper;
import com.vc.onlinepay.utils.LoopRobinUtil;
import com.vc.onlinepay.utils.StringUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通道子商户号管理
 *
 * @author 李海
 * @version 2018-09-12
 */
@Service
@Transactional (readOnly = true,rollbackFor = Exception.class)
public class ChannelSubNoServiceImpl {

    @Autowired
    private ChannelSubNoMapper channelSubNoMapper;

    @Autowired
    private RedisCacheApi redisCacheApi;

    @Autowired
    private CoreEngineProviderService coreEngineProviderService;

    /**
     * @描述:获取所有缓存渠道商列表
     * @时间:2018年9月14日 下午12:18:03
     */
    public Map<String, Integer> getCacheSupplierMap (ChannelSubNo channelSubNo) throws OnlineDbException {
        Map<String, Integer> noMap = new HashMap<> ();
        if (redisCacheApi.getBean2 (CacheConstants.ALL_SUPPER_ACCOUNT_LIST, List.class) == null || redisCacheApi.getMap (CacheConstants.ALL_SUPPER_ACCOUNT_MAP) == null) {
            List<ChannelSubNo> lists = channelSubNoMapper.getLoopRobinSubNoList (channelSubNo);
            if (lists == null || lists.size () < 1) {
                return null;
            }
            redisCacheApi.setBeanValid2 (CacheConstants.ALL_SUPPER_ACCOUNT_LIST, lists, CacheConstants.DEFAULT_INVALID_TIMER_5, List.class);
            for (ChannelSubNo subNo : lists) {
                redisCacheApi.setBeanValid (CacheConstants.SUPPER_ACCOUNT_NO + subNo.getUpMerchNo (), subNo, CacheConstants.DEFAULT_INVALID_TIMER_5, ChannelSubNo.class);
                noMap.put (subNo.getUpMerchNo (), subNo.getWeight ());
            }
            redisCacheApi.setMapValid (CacheConstants.ALL_SUPPER_ACCOUNT_MAP, noMap, CacheConstants.DEFAULT_INVALID_TIMER_5);
            return noMap;
        }
        return redisCacheApi.getMapValid (CacheConstants.ALL_SUPPER_ACCOUNT_MAP);
    }

    /**
     * @描述:获取单个缓存提供商信息
     * @时间:2018年9月14日 下午6:28:10
     */
    public ChannelSubNo getCacheSupperSubNo (String upMerchNo) {
        if (StringUtil.isEmpty (upMerchNo)) {
            return null;
        }
        String key = CacheConstants.SUPPER_ACCOUNT_NO + upMerchNo;
        if (redisCacheApi.exists (key)) {
            return (ChannelSubNo) redisCacheApi.getBean (key, ChannelSubNo.class);
        }
        ChannelSubNo channelSubNo = this.getByUpMerchNo (new ChannelSubNo (upMerchNo));
        if (channelSubNo == null) {
            return null;
        }
        redisCacheApi.setBeanValid (key, channelSubNo, CacheConstants.DEFAULT_INVALID_TIMER_5, ChannelSubNo.class);
        return channelSubNo;
    }

    /**
     * @描述:轮询算法获取单个供应商信息
     * @时间:2018年9月14日 上午10:59:27
     */
    public ChannelSubNo getOneSupplier (double traAmount, int loopRobin, Map<String, Integer> allSupplierMap, int tryNum, String clientIp, LoopRobinUtil loopRobinUtil) throws OnlineDbException {
        if (allSupplierMap == null || allSupplierMap.isEmpty () || allSupplierMap.size () < 1) {
            return null;
        }
        String upMerchNo = loopRobinUtil.loopRobin (allSupplierMap, 3);
        if (StringUtil.isEmpty (upMerchNo)) {
            return null;
        }
        ChannelSubNo tempNo = this.getCacheSupperSubNo (upMerchNo);
        if (tempNo == null) {
            allSupplierMap.remove (upMerchNo);
            return getOneSupplier (traAmount, loopRobin, allSupplierMap, tryNum--, clientIp, loopRobinUtil);
        }
        if (tempNo.getMinPrice () >= 0 && tempNo.getMaxPrice () > 0 && tempNo.getMinPrice () <= traAmount && tempNo.getMaxPrice () >= traAmount) {
            return tempNo;
        }
        if (allSupplierMap != null && allSupplierMap.size () > 0 && tryNum > 0) {
            allSupplierMap.remove (upMerchNo);
            return getOneSupplier (traAmount, loopRobin, allSupplierMap, tryNum--, clientIp, loopRobinUtil);
        }
        return null;
    }

    /**
     * @描述:找渠道商
     * @时间:2018年9月14日 下午6:34:04
     */
    public ChannelSubNo getByUpMerchNo (ChannelSubNo channelSubNo) {
        return channelSubNoMapper.getByUpMerchNo (channelSubNo);
    }

    /**
     * @描述:从缓存获取一批子商户号
     */
    public List<ChannelSubNo> getChannelSubNoList (ChannelSubNo channelSubNo) {
        if (channelSubNo.getChannelId() < 1 && channelSubNo.getChannelSource() < 1) {
            return null;
        }
        List<ChannelSubNo> list = channelSubNoMapper.getChannelSubNoList(channelSubNo);
        if (null == list || list.size() < 1 ) {
            return null;
        }
        return list;
    }

    /**
     * @描述:从缓存获取一批子商户号
     */
    public List<ChannelSubNo> getNoCacheChannelSubNoList (ChannelSubNo channelSubNo) {
        List<ChannelSubNo> list = channelSubNoMapper.getChannelSubNoList (channelSubNo);
        if (null == list || list.size() < 1 ) {
            return null;
        }
        return list;
    }

    /**
     * @描述:获取指定时间范围内一批子商户号
     * @作者:nada
     * @时间:2017年6月5日 下午9:09:01
     */
    public List<ChannelSubNo> getChannelLimitTimeSubNoList (ChannelSubNo channelSubNo) {
        return channelSubNoMapper.getChannelLimitTimeSubNoList (channelSubNo);
    }


    /**
     * @描述:自研支付宝获取指定时间范围内一批子商户号
     * @作者:nada
     * @时间:2017年6月5日 下午9:09:01
     */
    public List<ChannelSubNo> getLoopRobinSubNoList (ChannelSubNo channelSubNo) {
        return channelSubNoMapper.getLoopRobinSubNoList (channelSubNo);
    }


    /**
     * @描述:企业支付宝列表
     * @作者:nada
     * @时间:2017年6月5日 下午9:09:01
     */
    public List<ChannelSubNo> getAllSupplierList (ChannelSubNo channelSubNo) {
        return channelSubNoMapper.getAllSupplierList (channelSubNo);
    }


    /**
     * @描述:获取所有渠道商列表
     * @时间:2018年9月14日 下午12:18:03
     */
    public Map<String, Integer> getAllSupplierMap (ChannelSubNo channelSubNo) throws OnlineDbException {
        Map<String, Integer> noMap = new HashMap<> ();
        List<ChannelSubNo> lists = channelSubNoMapper.getAllSupplierList (channelSubNo);
        if (lists == null || lists.size () < 1) {
            return null;
        }
        for (ChannelSubNo subNo : lists) {
            noMap.put (subNo.getUpMerchNo (), subNo.getWeight ());
        }
        return noMap;
    }

    /**
     * @描述:更新金额
     * @时间:2018年5月17日 下午7:19:07
     */
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public Integer updateSubNoAmount (ChannelSubNo channelSubNo) {
        return channelSubNoMapper.updateSubNoAmount (channelSubNo);
    }

    /**
     * @描述:商户更新下单时间
     * @作者:ChaiJing THINK
     * @时间:2018/8/13 14:36
     */
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public Integer updateLastOrderTime (ChannelSubNo channelSubNo) {
        return channelSubNoMapper.updateLastOrderTime (channelSubNo);
    }

    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public Integer cleanDayTradeAmount (ChannelSubNo channelSubNo) {
        return channelSubNoMapper.cleanDayAmount (channelSubNo);
    }

    /**
     * @描述:查找已禁用商户
     * @作者:ChaiJing THINK
     * @时间:2018/8/13 14:44
     */
    List<ChannelSubNo> findLimited (ChannelSubNo channelSubNo) {
        return channelSubNoMapper.findLimited (channelSubNo);
    }

    /**
     * @描述:十分钟内下过单的商户
     * @作者:ChaiJing THINK
     * @时间:2018/9/6 10:23
     */
    List<String> findUsedMerchNo (ChannelSubNo channelSubNo) {
        return channelSubNoMapper.findUsedMerchNo (channelSubNo);
    }
}