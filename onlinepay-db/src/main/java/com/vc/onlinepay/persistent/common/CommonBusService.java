package com.vc.onlinepay.persistent.common;

import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.entity.merch.MerchInfo;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderDetail;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.entity.online.VcOnlineWallet;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineWalletServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class CommonBusService {

    private Logger logger = LoggerFactory.getLogger (getClass ());

    @Autowired
    private CommonPayService commonPayService;

    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;

    @Autowired
    private VcOnlinePaymentServiceImpl vcOnlinePaymentService;

    @Autowired
    private VcOnlineWalletServiceImpl vcOnlineWalletService;

    /**
     * @描述:根据商户号获取商户信息
     * @作者:nada
     * @时间:2018/12/20
     **/
    public MerchInfo findMerchInfoByMerchNo (String merchNo) throws OnlineServiceException {
        MerchInfo merchInfo = commonPayService.getCacheMerchInfo (merchNo);
        if (merchInfo == null) {
            return null;
        }
        return merchInfo;
    }

    /**
     * @描述:根据订单号获取订单信息
     * @作者:nada
     * @时间:2018/12/20
     **/
    public List<VcOnlineOrder> findOrderByCOrderNo (String OrderNo) throws OnlineServiceException {
        return vcOnlineOrderService.findOrderByCOrderNo (OrderNo);
    }

    /**
     * @描述:根据订单号获取订单信息
     * @作者:nada
     * @时间:2018/12/20
     **/
    public VcOnlineOrder findOrderStatus (String OrderNo) throws OnlineServiceException {
        List<VcOnlineOrder> vcOnlineOrders = vcOnlineOrderService.findOrderByCOrderNo (OrderNo);
        if (vcOnlineOrders == null ||  vcOnlineOrders.size() < 1) {
            return  null;
        }
        return vcOnlineOrders.get(0);
    }

    /**
     * @描述:根据商户号获取财务信息
     * @作者:nada
     * @时间:2018/12/20
     **/
    public VcOnlineWallet findVcOnlineWalletBymerchNo (String merchantNo) {
        return vcOnlineWalletService.findVcOnlineWalletBymerchNo (merchantNo);
    }

    /**
     * @描述:根据上游商户号获取订单信息
     * @作者:nada
     * @时间:2018/12/20
     **/
    public VcOnlinePayment findVcOnlinePaymentByPorderNo (String pOrderNo) throws OnlineServiceException {
        return vcOnlinePaymentService.findVcOnlinePaymentByPorderNo (pOrderNo);
    }

    /**
     * @描述:根据订单号查询订单
     * @作者:nada
     * @时间:2017年6月5日 下午9:47:43
     */
    public VcOnlineOrder getVcOrderByorderNo (String orderNo) {
        return vcOnlineOrderService.findOrderByOrderNo (orderNo);
    }

    /**
     * @描述:根据订单号查询订单
     * @作者:nada
     * @时间:2017年6月5日 下午9:47:43
     */
    public List<VcOnlineOrder> findOrderBySmstrxId (VcOnlineOrder order) {
        return vcOnlineOrderService.findOrderBySmstrxId (order);
    }

    /**
     * 根据支付金额匹配订单
     * @param order
     * @return
     */
    public List<VcOnlineOrder> findOrderByUniqueAmount (VcOnlineOrder order) {
        return vcOnlineOrderService.findOrderByUniqueAmount (order);
    }


    /**
     * @描述:根据订单号获取代付订单信息
     * @作者:nada
     * @时间:2018/12/20
     **/
    public VcOnlinePayment findVcOnlinePaymentByOrderNo (String orderNo) throws OnlineServiceException {
        return vcOnlinePaymentService.findVcOnlinePaymentByOrderNo (orderNo);
    }

    /**
     * @描述:根据订单号修改真实金额
     * @作者:nada
     * @时间:2018/12/20
     **/
    public int updateOrderDetailRealAmount (String vcOrderNo, BigDecimal realAmount) {
        try {
            VcOnlineOrderDetail onlineOrderDetail = new VcOnlineOrderDetail (vcOrderNo, realAmount);
            return vcOnlineOrderService.updateOrderDetailRealAmount (onlineOrderDetail);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return 0;
    }

    /**
     * @描述: 复制订单信息
     * @作者:nada
     * @时间:2018/12/20
     **/
    public int saveCopyOrder (VcOnlineOrder vcOnlineOrder) {
        return vcOnlineOrderService.saveCopyOrder (vcOnlineOrder);
    }
}