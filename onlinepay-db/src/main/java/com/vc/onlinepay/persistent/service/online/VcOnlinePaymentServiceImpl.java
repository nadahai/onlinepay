/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.online;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.mapper.online.VcOnlinePaymentMapper;

/**
 * 在线出款方案Service
 *
 * @author 李海
 * @version 2017-06-30
 */
@Service
@Transactional (readOnly = true)
public class VcOnlinePaymentServiceImpl {

    @Autowired
    private VcOnlinePaymentMapper vcOnlinePaymentMapper;

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int save (VcOnlinePayment vcOnlinePayment) {
        return vcOnlinePaymentMapper.insertSelective (vcOnlinePayment);
    }

    public VcOnlinePayment findVcOnlinePaymentByOrderNo (String orderNo) {
        orderNo = StringUtils.deleteWhitespace (orderNo);
        return vcOnlinePaymentMapper.findVcOnlinePaymentByOrderNo (orderNo);
    }

    public VcOnlinePayment findVcOnlinePaymentByPorderNo (String pOrderNo) {
        pOrderNo = StringUtils.deleteWhitespace (pOrderNo);
        return vcOnlinePaymentMapper.selectByPorderNo (pOrderNo);
    }
    
    /**
     * @描述:查询代付中的订单
     * @作者:nada
     * @时间:2018年1月11日 下午4:55:38
     */
    public List<VcOnlinePayment> selectByChannelSource(@Param("channelSource") String channelSource){
    	channelSource = StringUtils.deleteWhitespace (channelSource);
    	return vcOnlinePaymentMapper.selectByChannelSource (channelSource);
    }

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updatePaymentByPnum (VcOnlinePayment vcOnlinePayment) {
        if (StringUtil.isEmpty (vcOnlinePayment.getOrderNo ()) && StringUtil.isEmpty (vcOnlinePayment.getpOrderNo ())) {
            return 0;
        }
        return vcOnlinePaymentMapper.updatePaymentByPnum (vcOnlinePayment);
    }

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updatePaymentStatus (VcOnlinePayment vcOnlinePayment) {
        if (StringUtil.isEmpty (vcOnlinePayment.getOrderNo ()) && StringUtil.isEmpty (vcOnlinePayment.getpOrderNo ())) {
            return 0;
        }
        return vcOnlinePaymentMapper.updatePaymentByPnum (vcOnlinePayment);
    }

    public List<VcOnlinePayment> selectPaymentIsDouble (String pOrderNo) {
        return vcOnlinePaymentMapper.selectPaymentIsDouble (pOrderNo);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateOrderNotify (VcOnlinePayment vcOnlinePayment) {
        if (vcOnlinePayment == null || StringUtil.isEmpty (vcOnlinePayment.getpOrderNo ())) {
            return 0;
        }
        return vcOnlinePaymentMapper.updateOrderNotify (vcOnlinePayment);
    }

    //待查询的代付订单
    public List<VcOnlinePayment> findPaddingOrder (String orderNo, List<Long> channelList) {
        VcOnlinePayment vcOnlinePayment = new VcOnlinePayment ();
        vcOnlinePayment.setOrderNo (orderNo);
        vcOnlinePayment.setRedoResult ("2");
        vcOnlinePayment.setChannelList (channelList);
        return vcOnlinePaymentMapper.findPaddingOrder (vcOnlinePayment);
    }

    //已失败，可以补发的订单
    public List<VcOnlinePayment> findFailPaddingOrder (String orderNo) {
        VcOnlinePayment onlinePayment = new VcOnlinePayment ();
        onlinePayment.setPaymentType (1);
        onlinePayment.setRedoResult ("3");
        onlinePayment.setOrderNo (orderNo);
        return vcOnlinePaymentMapper.findPaddingOrder (onlinePayment);
    }

    /**
     * 代付单日单卡限额
     * @return
     */
    public boolean overFlowCardLimitAmount(String bankCard, String preDayAmountForCard, String amount) {
        try {
            double sumAmountd = vcOnlinePaymentMapper.countCardAmountForDay(bankCard);
            BigDecimal sumAmount = new BigDecimal(sumAmountd).add(new BigDecimal(amount)).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            if(sumAmount.compareTo(new BigDecimal(preDayAmountForCard)) == 1){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}