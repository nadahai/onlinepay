/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.channel;

import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.exception.OnlineDbException;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.merch.SupplierSubno;
import com.vc.onlinepay.persistent.mapper.channel.SupplierSubnoMapper;
import com.vc.onlinepay.utils.AutoFloatAmountUtil;
import com.vc.onlinepay.utils.LoopRobinUtil;
import com.vc.onlinepay.utils.StringUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 供应商账号管理(支付宝账号管理)
 *
 * @author 李海
 * @version 2018-09-12
 */
@Service
@Transactional (readOnly = true,rollbackFor = Exception.class)
public class SupplierSubnoServiceImpl {

    public static final Logger logger = LoggerFactory.getLogger (SupplierSubnoServiceImpl.class);

    @Autowired
    private SupplierSubnoMapper subnoMapper;
    @Autowired
    private RedisCacheApi redisCacheApi;
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;
    @Autowired
    private AutoFloatAmountUtil autoFloatAmountUtil;

    /**
     * @描述:获取单个账号信息
     * @时间:2018年9月14日 下午6:28:10
     */
    public SupplierSubno getCacheSubNo (String upMerchNo) {
        if (StringUtil.isEmpty (upMerchNo)) {
            return null;
        }
        String key = CacheConstants.LOOP_ROBIN_ALIPAY_NO + upMerchNo;
        if (redisCacheApi.exists (key)) {
            return (SupplierSubno) redisCacheApi.getBean (key, SupplierSubno.class);
        }
        SupplierSubno prms = new SupplierSubno ();
        prms.setUpMerchNo (upMerchNo);
        List<SupplierSubno> channelSubNos = this.getByUpMerchNo (prms);
        if (channelSubNos == null || channelSubNos.size () < 1) {
            return null;
        }
        redisCacheApi.setBeanValid (key, channelSubNos.get (0), CacheConstants.DEFAULT_INVALID_TIMER_5, SupplierSubno.class);
        return channelSubNos.get (0);
    }

    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int updateUserId (SupplierSubno subno) {
        return subnoMapper.updateUserId (subno);
    }

    /**
     * @描述:获取单个渠道下所有账号列表
     * @时间:2018年9月14日 下午12:18:03
     */
    public Map<String, Integer> getCacheSubNoMap (long channelId,double traAmount) throws OnlineDbException {
        Map<String, Integer> noMap = new HashMap<> ();
        SupplierSubno supplierNos = new SupplierSubno ();
        supplierNos.setChannelId (channelId);
        supplierNos.setTraAmount (traAmount);
        supplierNos.setLastOrderExpiredTime (coreEngineProviderService.getIntCacheCfgKey ("online.subno.expired.time"));
        List<SupplierSubno> lists = this.getAllSupplierList (supplierNos);
        if (lists == null || lists.size () < 1) {
            return null;
        }
        for (SupplierSubno subNo : lists) {
            String upMerchNo = subNo.getUpMerchNo ();
            noMap.put (upMerchNo, subNo.getWeight ());
        }
        return noMap;
        /*Map<String, Integer> noMap = new HashMap<> ();
        if (redisCacheApi.getBean2 (CacheConstants.LOOP_ROBIN_ALIPAY_LIST + supplierNo, List.class) == null || redisCacheApi.getMap (CacheConstants.LOOP_ROBIN_ALIPAY_MAP + supplierNo) == null) {
            SupplierSubno supplierNos = new SupplierSubno (supplierNo, channelId);
            List<SupplierSubno> lists = this.getByUpMerchNo (supplierNos);
            if (lists == null || lists.size () < 1) {
                return null;
            }
            redisCacheApi.setBeanValid2 (CacheConstants.LOOP_ROBIN_ALIPAY_LIST + supplierNo, lists, CacheConstants.DEFAULT_INVALID_TIMER_5, List.class);
            for (SupplierSubno subNo : lists) {
                redisCacheApi.setBeanValid (CacheConstants.LOOP_ROBIN_ALIPAY_NO + subNo.getUpMerchNo (), subNo, CacheConstants.DEFAULT_INVALID_TIMER_5, SupplierSubno.class);//1分钟
                noMap.put (subNo.getUpMerchNo (), subNo.getWeight ());
            }
            redisCacheApi.setMapValid (CacheConstants.LOOP_ROBIN_ALIPAY_MAP + supplierNo, noMap, CacheConstants.DEFAULT_INVALID_TIMER_5);
            return noMap;
        }
        return redisCacheApi.getMapValid (CacheConstants.LOOP_ROBIN_ALIPAY_MAP + supplierNo);*/
    }

    /**
     * @描述:轮询算法获取单个供应商支付宝信息
     * @时间:2018年9月14日 上午10:59:27
     */
    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public SupplierSubno getOneSubNo (long channelId, double traAmount, int loopRobin, Map<String, Integer> allSubNoMap, int tryNum, String orderNo, LoopRobinUtil loopRobinUtil) throws OnlineDbException {
        if (allSubNoMap == null || allSubNoMap.isEmpty () || allSubNoMap.size () < 1) {
            return null;
        }
        String upMerchNo = loopRobinUtil.loopRobin (allSubNoMap, 3);
        logger.info ("第{}次轮询获取单个供应商:{},获取账号:{},支付宝账号:{},轮询模式:{}", tryNum, orderNo, upMerchNo, allSubNoMap, loopRobin);
        if (StringUtil.isEmpty (upMerchNo)) {
            return null;
        }
        SupplierSubno tempNo = this.getCacheSubNo (upMerchNo);
        if (tempNo == null && allSubNoMap != null && allSubNoMap.size () > 0) {
            allSubNoMap.remove (upMerchNo);
            tryNum = tryNum - 1;
            return getOneSubNo (channelId, traAmount, loopRobin, allSubNoMap, tryNum, orderNo, loopRobinUtil);
        }
        if (tempNo.getMinPrice () >= 0 && tempNo.getMaxPrice () > 0 && tempNo.getMinPrice () <= traAmount && tempNo.getMaxPrice () >= traAmount) {
            if (allSubNoMap != null && allSubNoMap.size () > 0 && channelId != 167) {
                if (redisCacheApi.exists (upMerchNo) && StringUtils.isNoneBlank ((String) redisCacheApi.get (upMerchNo))) {
                    return tempNo;
                }
                redisCacheApi.set (upMerchNo, upMerchNo, CacheConstants.DEFAULT_INVALID_TIMER_10);
            }
            return tempNo;
        }
        if (allSubNoMap != null && allSubNoMap.size () > 0 && tryNum > 0) {
            allSubNoMap.remove (upMerchNo);
            tryNum = tryNum - 1;
            return getOneSubNo (channelId, traAmount, loopRobin, allSubNoMap, tryNum, orderNo, loopRobinUtil);
        }
        return null;
    }

    /**
     * @描述:根据账号获取列表
     * @时间:2018年9月13日 下午12:12:24
     */
    public List<SupplierSubno> getByUpMerchNo (SupplierSubno subno){
        return subnoMapper.getByUpMerchNo (subno);
    }

    /**
     * @描述:根据账号获取列表
     * @时间:2018年9月13日 下午12:12:24
     */
    public List<SupplierSubno> getByUpMerchNo (String upMerchNo) {
        SupplierSubno subno = new SupplierSubno ();
        subno.setUpMerchNo (upMerchNo);
        return subnoMapper.getByUpMerchNo (subno);
    }

    /**
     * @描述:根据账号获取信息
     * @时间:2018年9月13日 下午12:12:24
     */
    public SupplierSubno getSubnoById (long id) {
        SupplierSubno subno = new SupplierSubno ();
        subno.setId (id);
        return subnoMapper.getSubnoById (subno);
    }

    /**
     * @描述:根据账号获取单个信息
     * @时间:2018年9月13日 下午12:12:24
     */
    public SupplierSubno getOneByUpMerchNo (String upMerchNo) {
        SupplierSubno subno = new SupplierSubno ();
        subno.setUpMerchNo (upMerchNo);
        List<SupplierSubno> list = subnoMapper.getOneByUpMerchNo (subno);
        if(list == null || list.size () <1){
            return  null;
        }else{
            return list.get (0);
        }
    }

    /**
     * @描述: 修改渠道子账号状态
     * @作者:nada
     * @时间:2018/12/21
     **/
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int updateStatus (SupplierSubno subno) {
        return subnoMapper.updateStatus (subno);
    }

    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int updateCardIdx (SupplierSubno subno) {
        return subnoMapper.updateCardIdx (subno);
    }

    /**
     * @描述: 修改渠道子账号金额
     * @作者:nada
     * @时间:2018/12/21
     **/
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int updateSubNoAmount (SupplierSubno subno) {
        return subnoMapper.updateSubNoAmount (subno);
    }

    /**
     * @描述: 获取所有当前账号列表
     * @作者:nada
     * @时间:2018/12/25
     **/
    public List<SupplierSubno> getAllSupplierList (SupplierSubno subno){
        return subnoMapper.getAllSupplierList (subno);
    }

    /**
     * @描述:更新金额
     * @时间:2018年5月17日 下午7:19:07
     */
    public Integer updateSettleAmount (SupplierSubno channelSubNo){
        return subnoMapper.updateSettleAmount (channelSubNo);
    }

    /**
     * @描述:商户更新下单时间
     * @作者:ChaiJing THINK
     * @时间:2018/8/13 14:36
     */
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public Integer updateLastOrderTime (SupplierSubno channelSubNo) {
        return subnoMapper.updateLastOrderTime (channelSubNo);
    }

}