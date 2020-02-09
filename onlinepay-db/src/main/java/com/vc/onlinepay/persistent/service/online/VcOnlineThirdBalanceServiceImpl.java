/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.online;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.vc.onlinepay.persistent.entity.online.VcOnlineThirdBalance;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineThirdBalanceMapper;

/**
 * 在线交易财富信息Service
 *
 * @author Alan
 * @version 2017-06-30
 */
@Service
@Transactional (readOnly = true)
public class VcOnlineThirdBalanceServiceImpl {

    @Autowired
    private VcOnlineThirdBalanceMapper vcOnlineThirdBalanceMapper;

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updateBalance (VcOnlineThirdBalance vcOnlineThirdBalance) {
        return vcOnlineThirdBalanceMapper.updateBalance (vcOnlineThirdBalance);
    }

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int saveThirdBalance (VcOnlineThirdBalance vcOnlineThirdBalance) {
        return vcOnlineThirdBalanceMapper.saveThirdBalance (vcOnlineThirdBalance);
    }

    /**
     * @描述:查找最优代付通道
     * @作者:nada
     * @时间:2017年12月20日 上午11:28:55
     */
    public VcOnlineThirdBalance findLoadBalance (VcOnlineThirdBalance thirdBalance) {
        thirdBalance.setIsTransfer (1);
        List<VcOnlineThirdBalance> lists = vcOnlineThirdBalanceMapper.findLoadBalance (thirdBalance);
        if (lists == null || lists.size () < 1) {
            return null;
        }
        return lists.get (0);
    }

    /**
     * @描述:查找所有代付通道（包括禁用和关闭）
     * @作者:nada
     * @时间:2017年12月20日 上午11:28:55
     */
    public List<VcOnlineThirdBalance> findAllBalance (VcOnlineThirdBalance thirdBalance) {
        List<VcOnlineThirdBalance> lists = vcOnlineThirdBalanceMapper.findAllBalance (thirdBalance);
        if (lists == null || lists.size () < 1) {
            return null;
        }
        return lists;
    }

    /**
     * @描述:查找所有代付通道（包括禁用和关闭）
     * @作者:nada
     * @时间:2017年12月20日 上午11:28:55
     */
    public List<VcOnlineThirdBalance> findAllBalanceList (VcOnlineThirdBalance thirdBalance) {
        List<VcOnlineThirdBalance> lists = vcOnlineThirdBalanceMapper.findAllBalance (thirdBalance);
        return lists;
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int cashSuccessUpdateBalance (VcOnlineThirdBalance vcOnlineThirdBalance) {
        return vcOnlineThirdBalanceMapper.cashSuccessUpdateBalance (vcOnlineThirdBalance);
    }

    public List<VcOnlineThirdBalance> findLoadBalancelist (VcOnlineThirdBalance thirdBalance) {
        thirdBalance.setIsTransfer (1);
        return vcOnlineThirdBalanceMapper.findLoadBalance (thirdBalance);
    }

    /**
     * @描述:查询回调IP
     * @作者:leoncongee
     * @时间:2019年5月13日15:18:39
     */
    @Transactional (readOnly = false)
    public VcOnlineThirdBalance getAccessIpByChannelId(VcOnlineThirdBalance vcOnlineThirdBalance){
        return vcOnlineThirdBalanceMapper.getAccessIpByChannelId(vcOnlineThirdBalance);
    }

    /**
     * @描述:更新回调IP
     * @作者:leoncongee
     * @时间:2019年5月13日14:23:57
     */
    @Transactional (readOnly = false)
    public int updateAccessIpByChannelId(VcOnlineThirdBalance vcOnlineThirdBalance){
        return vcOnlineThirdBalanceMapper.updateAccessIpByChannelId(vcOnlineThirdBalance);
    }

}