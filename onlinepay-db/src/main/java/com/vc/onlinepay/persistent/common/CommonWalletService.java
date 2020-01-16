/**
 * @类名称:CommonWalletServiceImpl.java
 * @时间:2018年3月5日下午5:20:18
 * @作者:nada
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.persistent.common;

import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.entity.channel.ChannelSubNo;
import com.vc.onlinepay.persistent.entity.merch.SupplierSubno;
import com.vc.onlinepay.persistent.entity.merch.XkPddBuyer;
import com.vc.onlinepay.persistent.entity.merch.XkPddGoods;
import com.vc.onlinepay.persistent.entity.merch.XkPddMerch;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.entity.online.VcOnlineWallet;
import com.vc.onlinepay.persistent.entity.online.VcOnlineWalletRecord;
import com.vc.onlinepay.persistent.mapper.merch.XkPddBuyerMapper;
import com.vc.onlinepay.persistent.mapper.merch.XkPddGoodsMapper;
import com.vc.onlinepay.persistent.mapper.merch.XkPddMerchMapper;
import com.vc.onlinepay.persistent.monitor.AsynMonitor;
import com.vc.onlinepay.persistent.service.channel.MerchChannelServiceImpl;
import com.vc.onlinepay.persistent.service.channel.SupplierSubnoServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineWalletRecordServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineWalletServiceImpl;
import com.vc.onlinepay.utils.StringUtil;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @描述:通用财富业务处理接口实现(禁止随意修改)
 * @作者:nada
 * @时间:2018年3月5日 下午5:20:18
 */
@Service
public class CommonWalletService {

    private static final Logger logger = LoggerFactory.getLogger(CommonWalletService.class);
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Autowired
    private VcOnlineWalletServiceImpl vcOnlineWalletService;
    @Autowired
    private MerchChannelServiceImpl merchChannelServiceImpl;
    @Autowired
    private VcOnlineWalletRecordServiceImpl vcOnlineWalletRecordService;
    @Autowired
    private AsynMonitor asynMonitor;
    @Autowired
    private XkPddMerchMapper xkPddMerchMapper;
    @Autowired
    private XkPddGoodsMapper xkPddGoodsMapper;
    @Autowired
    private XkPddBuyerMapper xkPddBuyerMapper;

    /**
     * @描述:同步更新代付中财富信息
     * @时间:2018年3月5日 下午5:32:28
     */
    public boolean syncUpdatePaddingPaymentWallet(VcOnlinePayment onlinePayment,VcOnlineWallet vcOnlineWallet) throws OnlineServiceException{
        try {
            String orderNo = onlinePayment.getOrderNo();
            int res = vcOnlineWalletRecordService.saveWalletRecord(VcOnlineWalletRecord.getPaymentPaddingRecord(onlinePayment,vcOnlineWallet));
            if(res < 1){
                logger.error("代付账务记录保存失败{}",orderNo);
                return false;
            }
            VcOnlineWallet cashStart = VcOnlineWallet.buildWalletCashAmount(vcOnlineWallet.getMerchId(),onlinePayment.getCashAmount(),vcOnlineWallet.getServiceCharge());
            int c = 0;
            if(onlinePayment.getCashMode() == 1){
                c = vcOnlineWalletService.updateD0WalletCashStart(cashStart);
            }else{
                c = vcOnlineWalletService.updateD1WalletCashStart(cashStart);
            }
            if (c < 1) {
                logger.error("更新财富信息失败{}",orderNo);
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("同步更新代付中财富信息异常",e);
            return false;
        }
    }

    /**
     * @描述:同步更新代付失败财富信息
     * @时间:2018年3月5日 下午5:32:28
     */
    public boolean syncUpdateFailedPaymentWallet(VcOnlinePayment onlinePayment,String orderNo) throws OnlineServiceException {
        try {
            synchronized (orderNo) {
                if(StringUtils.isEmpty(orderNo)){
                    logger.error("同步代付失败订单订单号为空{}",orderNo);
                    return false;
                }
                VcOnlineWallet wallet =  vcOnlineWalletService.findByMerchId(onlinePayment.getMerchId());
                if(wallet == null){
                    logger.error("同步更新代付商户账户信息不存在,请核实开户信息{}",orderNo);
                    return false;
                }
                int res = vcOnlineWalletRecordService.saveWalletRecord(VcOnlineWalletRecord.saveFailedWalletRecord(wallet,onlinePayment));
                if(res < 1){
                    logger.error("同步更新代付代付失败历史记录更新异常:{}",orderNo);
                    return false;
                }
                VcOnlineWallet cashDone = VcOnlineWallet.buildWalletCashAmount(onlinePayment.getMerchId(),onlinePayment.getCashAmount(),onlinePayment.getPoundageRate());
                if(onlinePayment.getCashMode() == 1 || onlinePayment.getCashMode() == 3){
                    res = vcOnlineWalletService.updateD0WalletCashRollback(cashDone);
                }else{
                    res = vcOnlineWalletService.updateD1WalletCashRollback(cashDone);
                }
                if(res < 1){
                    logger.error("同步更新代付代付失败账户信息更新失败:{}",orderNo);
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("同步更新代付代付失败账户信息更新异常:{}",orderNo,e);
            return false;
        }
    }

    /**
     * @描述:同步更新成功代付财富信息
     * @时间:2018年3月5日 下午5:32:43
     */
    public boolean syncUpdateOkPaymentWallet(VcOnlinePayment onlinePayment,String orderNo) throws OnlineServiceException {
        try {
            if(StringUtils.isEmpty(orderNo)){
                logger.error("同步代付成功订单订单号为空{}",orderNo);
                return false;
            }
            VcOnlineWallet wallet =  vcOnlineWalletService.findByMerchId(onlinePayment.getMerchId());
            if(wallet == null){
                logger.error("同步更新代付商户账户信息不存在,请核实开户信息{}",orderNo);
                return false;
            }
            int res = vcOnlineWalletRecordService.updateWalletRecord(VcOnlineWalletRecord.updateSuccessWalletRecord(wallet,onlinePayment));
            if(res < 1){
                logger.error("代付历史记录更新异常:{}",orderNo);
                return false;
            }
            VcOnlineWallet cashDone = VcOnlineWallet.buildWalletCashAmount(onlinePayment.getMerchId(),onlinePayment.getCashAmount(),onlinePayment.getPoundageRate());
            if(onlinePayment.getCashMode() == 1){
                res = vcOnlineWalletService.updateD0WalletCashDone(cashDone);
            }else{
                res = vcOnlineWalletService.updateD1WalletCashDone(cashDone);
            }
            if(res < 1){
                logger.error("账户财富信息{}更新异常:{}",onlinePayment.getMerchId(),orderNo);
                return false;
            }
//            int b = vcOnlineThirdBalanceService.cashSuccessUpdateBalance(new VcOnlineThirdBalance(onlinePayment.getCashMode(),onlinePayment.getRemarks(),onlinePayment.getCashAmount()));
//            if(b < 1){
//                logger.error("提现成功，更新上游账户信息失败可忽略,模式:{},通道标记:{}",onlinePayment.getCashMode(),onlinePayment.getRemarks());
//            }
            return true;
        } catch (Exception e){
            logger.error("统一更新成功回调监听处理异常:{}",orderNo,e);
            return false;
        }
    }

    /**
     * @描述:异步更新成功订单财富信息
     * @时间:2018年3月5日 下午5:33:32
     */
    @Transactional(readOnly=false,rollbackFor=Exception.class)
    public boolean asynUpdateOkOrderWallet(String orderNo) throws OnlineServiceException {
        try {
            if(StringUtil.isEmpty(orderNo)){
                logger.error("异步更新财富信息订单号为空{}",orderNo);
                return false;
            }
            VcOnlineOrder  vcOnlineOrder = vcOnlineOrderService.findOrderByOrderNo(orderNo);
            if(vcOnlineOrder == null){
                logger.error("异步更新财富信息订单号信息为空{}",orderNo);
                return false;
            }
            if (vcOnlineOrder.getStatus() != 4L){
                logger.error("异步更新财富信息订单号{}不是成功状态{}",orderNo,vcOnlineOrder.getStatus());
                return false;
            }
            if (vcOnlineOrder.getSettleStatus() !=1){
                logger.error("异步更新财富信息订单号{}结算状态{}",orderNo,vcOnlineOrder.getSettleStatus());
                return false;
            }
            VcOnlineWallet oldWallet = vcOnlineWalletService.findVcOnlineWalletBymerchNo(vcOnlineOrder.getMerchNo());
            //账务历史记录
            int s = vcOnlineWalletRecordService.saveWalletRecord(VcOnlineWalletRecord.getOrderSuccessRecord(vcOnlineOrder, oldWallet));
            if (s < 1) {
                logger.error("保存账务记录失败{}",s);
                return false;
            }
            //账务结算
            int updateOk = 0;
            VcOnlineWallet newWallet = new VcOnlineWallet();
            newWallet.setMerchId(vcOnlineOrder.getMerchId());
            newWallet.setMerchNo(vcOnlineOrder.getMerchNo());
            newWallet.setTraTotalAmount(vcOnlineOrder.getTraAmount());
            if(vcOnlineOrder.getPayMode() == 1 || vcOnlineOrder.getPayMode() == 3){
                //T0入账
                newWallet.setD0WaitAmount(vcOnlineOrder.getWaitAmount());
                newWallet.setD0UsableAmount((vcOnlineOrder.getActualAmount().subtract(vcOnlineOrder.getWaitAmount())).setScale(3,BigDecimal.ROUND_HALF_DOWN));
                updateOk =  vcOnlineWalletService.updateD0SuccessOrder(newWallet);
            }else{
                //D1入账
                newWallet.setWaitTotalAmount(vcOnlineOrder.getWaitAmount());
                updateOk = vcOnlineWalletService.updateD1SuccessOrder(newWallet);
            }
            if(updateOk < 1 ) {
                logger.error("回调交易成功更新财富失败{}",orderNo);
                return false;
            }
            //更新订单结算状态
            VcOnlineOrder order = new VcOnlineOrder(orderNo);
            order.setSettleStatus(2);
            order.setOrderDes("成功已结算");
            int r = vcOnlineOrderService.updateOrderSettleStatus(order);
            logger.info ("更新订单结算结果{},{}",orderNo,r);
            return this.updateSubInfo (vcOnlineOrder,orderNo);
        } catch (Exception e) {
            logger.error("异步更新成功订单财富信息,订单号{},异常{}",orderNo,e);
            return false;
        }
    }

    /**
     * @描述:更新订单子账号信息
     * @作者:nada
     * @时间:2018/12/28
     **/
    @Transactional(readOnly=false,rollbackFor=Exception.class)
    public boolean updateSubInfo(VcOnlineOrder vcOnlineOrder,String orderNo){
        try {
            long channelId = vcOnlineOrder.getChannelId();
            long merchId = vcOnlineOrder.getMerchId();
            if(vcOnlineOrder.getPaySource() == 51 || vcOnlineOrder.getPaySource() == 65 || vcOnlineOrder.getPaySource() == 81 || vcOnlineOrder.getPaySource() == 83){
                if(channelId == 177L){
                    channelId = 147L;
                }else if(channelId == 203L){
                    channelId = 200L;
                }else if(channelId == 213L){
                    channelId = 167L;
                }
                SupplierSubno supplierSubno = new SupplierSubno(vcOnlineOrder.getUpMerchNo(),vcOnlineOrder.getUpMerchKey(),vcOnlineOrder.getTraAmount());
                int r = merchChannelServiceImpl.updateAlipaySubNoAmount(supplierSubno);
                logger.info ("更新支付宝子账号信息{},{}",orderNo,r);
                r = merchChannelServiceImpl.updateSubNoAmount(new ChannelSubNo(channelId,merchId,vcOnlineOrder.getUpMerchNo(),vcOnlineOrder.getTraAmount()));
                logger.info ("更新支付宝渠道大商户子账号信息{},{}",orderNo,r);
                return true;
            }else if(vcOnlineOrder.getPaySource()==111L){
                int r = merchChannelServiceImpl.updateSubNoAmount(new ChannelSubNo(channelId,merchId,vcOnlineOrder.getUpMerchNo(),vcOnlineOrder.getTraAmount()));
                logger.info ("更新大商户子账号信息{},{}",orderNo,r);
                return true;
            }else{
                if(channelId == 232){
                    channelId = 231L;
                }
                int r = merchChannelServiceImpl.updateSubNoAmount(new ChannelSubNo(channelId,vcOnlineOrder.getUpMerchNo(),vcOnlineOrder.getTraAmount()));
                logger.info ("更新大商户子账号信息{},{}",orderNo,r);
            }
            boolean r = asynMonitor.orderMonitor(vcOnlineOrder.getUpMerchNo(),vcOnlineOrder.getStatus());
            logger.info ("账号监控结果{},{}",orderNo,r);
            return true;
        } catch (Exception e) {
            logger.error("更新订单子账号信息订单号{},异常{}",orderNo,e);
            return false;
        }
    }
}

