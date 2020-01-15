/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.online;

import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderDetail;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineOrderDetailMapper;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 李海在线交易订单Service
 *
 * @author 李海
 * @version 2017-06-30
 */
@Service
@Transactional (readOnly = true)
public class VcOnlineOrderServiceImpl {

    @Autowired
    private VcOnlineOrderMapper vcOnlineOrderMapper;

    @Autowired
    private VcOnlineOrderDetailMapper vcOnlineOrderDetailMapper;

    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int save (VcOnlineOrder vcOnlineOrder) {
        return vcOnlineOrderMapper.save (vcOnlineOrder);
    }

    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int saveDetail (VcOnlineOrderDetail vcOnlineOrder) {
        return vcOnlineOrderDetailMapper.save (vcOnlineOrder);
    }


    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updateByOrderNo (VcOnlineOrder vcOnlineOrder) {
        return vcOnlineOrderMapper.updateByOrderNo (vcOnlineOrder);
    }

    /**
     * @描述:根据下游订单号查询订单信息
     */
    public List<VcOnlineOrder> findOrderByCOrderNo (String orderNo) {
        return vcOnlineOrderMapper.findOrderByCOrderNo (orderNo);
    }

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updateDesByOrderNo (String orderNo, String des) {
        VcOnlineOrder vcOnlineOrder = new VcOnlineOrder();
        vcOnlineOrder.setOrderNo (orderNo);
        vcOnlineOrder.setOrderDes (des);
        return vcOnlineOrderMapper.updateDesByOrderNo (vcOnlineOrder);
    }

    /**
     * @描述:根据订单号查询订单信息
     * @作者:nada
     * @时间:2018年1月5日 下午5:00:53
     */
    public VcOnlineOrder findOrderByOrderNo (String orderNo) {
        return vcOnlineOrderMapper.findOrderByOrderNo (orderNo);
    }

    /**
     * @描述:根据订单号查询订单信息
     * @作者:nada
     * @时间:2018年1月5日 下午5:00:53
     */
    public List<VcOnlineOrder> findOrderBySmstrxId (VcOnlineOrder order) {
        return vcOnlineOrderMapper.findOrderBySmstrxId (order);
    }

    /**
     * @描述:获取未分润列表
     * @作者:nada
     * @时间:2019/1/5
     **/
    public List<VcOnlineOrder> findNoPrifitOrderList (VcOnlineOrder order) {
        return vcOnlineOrderMapper.findNoPrifitOrderList (order);
    }

    /**
     * @描述:修改订单通知信息
     * @作者:nada
     * @时间:2018年3月8日 上午10:44:34
     */
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int updateOrderNotify (VcOnlineOrder vcOnlineOrder) {
        return vcOnlineOrderMapper.updateOrderNotify (vcOnlineOrder);
    }

    /**
     * @描述:验证商户上送订单是否已经存在
     * @作者:nada
     * @时间:2018年3月2日 上午11:35:07
     */
    public List<VcOnlineOrder> verifyMerchOrderExist (String cOrder) {
        return vcOnlineOrderMapper.verifyMerchOrderExist (cOrder);
    }

    /**
     * @描述:修改订单状态
     * @作者:nada
     * @时间:2018年3月8日 上午11:02:47
     */
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int updateOrderStatus (VcOnlineOrder vcOnlineOrder) {
        return vcOnlineOrderMapper.updateOrderStatus (vcOnlineOrder);
    }
    
    /**
     * @描述:修改订单描述
     * @作者:nada
     * @时间:2018年3月8日 上午11:02:47
     */
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int updateOrderDes (VcOnlineOrder vcOnlineOrder) {
        return vcOnlineOrderMapper.updateOrderDes (vcOnlineOrder);
    }

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updateOrderDes (String orderNo, String des,String porder) {
        VcOnlineOrder vcOnlineOrder = new VcOnlineOrder();
        vcOnlineOrder.setOrderNo (orderNo);
        vcOnlineOrder.setOrderDes (des);
        vcOnlineOrder.setpOrder (porder);
        return vcOnlineOrderMapper.updateUpMerchNoOrderNo (vcOnlineOrder);
    }
    

    /**
     * @描述:修改订单状态
     * @作者:nada
     * @时间:2018年3月8日 上午11:02:47
     */
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int updateProfitStatus (VcOnlineOrder vcOnlineOrder) {
        return vcOnlineOrderMapper.updateProfitStatus(vcOnlineOrder);
    }


    /**
     * @描述:修改订单结算状态
     * @作者:nada
     * @时间:2018年3月8日 上午11:02:47
     */
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int updateOrderSettleStatus (VcOnlineOrder vcOnlineOrder) {
        return vcOnlineOrderMapper.updateOrderSettleStatus (vcOnlineOrder);
    }

    /**
     * @描述:汇总今日交易
     * @时间:2018年6月15日 下午4:55:14
     */
    public VcOnlineOrder totalTodayTrad () {
        return vcOnlineOrderMapper.totalTodayTrad ();
    }

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updateUpMerchNoOrderNo (VcOnlineOrder vcOnlineOrder) {
        return vcOnlineOrderMapper.updateUpMerchNoOrderNo (vcOnlineOrder);
    }

    /**
     * @描述:查询中间状态订单
     * @作者:ChaiJing THINK
     * @时间:2018/7/2 16:25
     */
    public List<VcOnlineOrder> findPaddingOrder (VcOnlineOrder onlineOrder) {
        return vcOnlineOrderMapper.findPaddingOrder (onlineOrder);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateOrderDetailRealAmount (VcOnlineOrderDetail onlineOrderDetail) {
        return vcOnlineOrderDetailMapper.updateOrderDetailRealAmount (onlineOrderDetail);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int saveCopyOrder (VcOnlineOrder onlineOrder) {
        return vcOnlineOrderMapper.saveCopyOrder (onlineOrder);
    }

    public List<VcOnlineOrder> verifyPOrderExist (VcOnlineOrder onlineOrder) {
        return vcOnlineOrderMapper.verifyPOrderExist (onlineOrder);
    }
    public List<VcOnlineOrder> verifyRePayPOrderExist (VcOnlineOrder onlineOrder) {
        return vcOnlineOrderMapper.verifyRePayPOrderExist (onlineOrder);
    }

    public List<VcOnlineOrder> findOrderByUniqueAmount(VcOnlineOrder order) {
        return vcOnlineOrderMapper.findOrderByUniqueAmount (order);
    }

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updateUpInfoByOrderNo(VcOnlineOrder vcOnlineOrder) {
        return vcOnlineOrderMapper.updateUpInfoByOrderNo(vcOnlineOrder);
    }
}