package com.vc.onlinepay.persistent.mapper.online;

import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VcOnlinePaymentMapper {

    /**
     * @描述:保存订单
     * @作者:nada
     * @时间:2018年1月11日 下午4:56:10
     */
    int insertSelective(VcOnlinePayment record);

    /**
     * @描述:查询订单
     * @作者:nada
     * @时间:2018年1月11日 下午4:55:38
     */
    VcOnlinePayment selectByPorderNo(@Param("pOrderNo") String pOrderNo);
    
    /**
     * @描述:查询代付中的订单
     * @作者:nada
     * @时间:2018年1月11日 下午4:55:38
     */
    List<VcOnlinePayment> selectByChannelSource(@Param("channelSource") String channelSource);
    
    

    /**
     * @描述:查询订单
     * @作者:nada
     * @时间:2018年1月11日 下午4:55:38
     */
    VcOnlinePayment findVcOnlinePaymentByOrderNo(String orderNo);
    
    VcOnlinePayment selectMoneyAndCount(@Param("merchNo") String merchNo);
    
    /**
     * @描述:修改提现订单
     * @作者:nada
     * @时间:2018年1月2日 上午11:17:12
     */
    int updatePaymentByPnum(VcOnlinePayment vcOnlinePayment);
    
    /**
     * @描述:修改提现订单
     * @作者:nada
     * @时间:2018年1月2日 上午11:17:12
     */
    int updatePaymentStatus(VcOnlinePayment vcOnlinePayment);
    
    /**
     * @描述:查询是否订单重复
     * @作者:nada
     * @时间:2018年1月11日 下午4:56:27
     */
    List<VcOnlinePayment> selectPaymentIsDouble(@Param("pOrderNo") String pOrderNo);
    
    /**
     * @描述:修改下游订单通知信息
     * @作者:nada
     * @时间:2018年1月31日 下午2:37:24
     */
    int updateOrderNotify(VcOnlinePayment vcOnlinePayment);

    /**
     * @描述:查找特定单号特定通道代付中订单
     * @作者:ChaiJing THINK
     * @时间:2018/5/21 17:17
     */
    List<VcOnlinePayment> findPaddingOrder(VcOnlinePayment vcOnlinePayment);

    /**
     * 统计当日单卡代付额
     * @param bankCard
     * @return
     */
    double countCardAmountForDay(String bankCard);
}