/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.online;

import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 李海在线交易订单DAO接口
 * 
 * @author 李海
 * @version 2017-06-30
 */
@Repository
public interface VcOnlineOrderMapper {

    VcOnlineOrder findOrderByOrderNo(String orderNo);
    
    /**
     * @描述:通过归属订单找
     * @时间:2018年11月22日 下午4:58:43
     */
    List<VcOnlineOrder> findOrderBySmstrxId(VcOnlineOrder order) ;

    /**
     * @描述:获取未分润的列表
     * @时间:2018年11月22日 下午4:58:43
     */
    List<VcOnlineOrder> findNoPrifitOrderList(VcOnlineOrder order) ;

    /**
     * @desc 更新订单描述
     * @author nada
     * @create 2019/6/5 14:36
     */
    int updateDesByOrderNo(VcOnlineOrder vcOnlineOrder);

    int updateOrderError(VcOnlineOrder vcOnlineOrder);

    /**
     * @描述:修改订单
     * @作者:nada
     * @时间:2018年1月31日 下午2:37:24
     */
    int updateByOrderNo(VcOnlineOrder vcOnlineOrder);
    
    /**
     * @描述:保存交易订单
     * @作者:nada
     * @时间:2018年1月31日 下午2:32:08
     */
    int save(VcOnlineOrder vcOnlineOrder);
    
    /**
     * @描述:根据下游订单号查询交易订单信息
     * @作者:nada
     * @时间:2018年1月31日 下午2:36:20
     */
    List<VcOnlineOrder> findOrderByCOrderNo(String cOrder);
	
	/**
	 * @描述:验证商户上送订单是否已经存在
	 * @作者:nada
	 * @时间:2018年1月31日 下午2:32:49
	 */
  List<VcOnlineOrder> verifyMerchOrderExist(String cOrder);
	
	/**
     * @描述:修改订单状态
     * @作者:nada
     * @时间:2018年3月8日 上午11:00:49
     */
  int updateOrderStatus(VcOnlineOrder vcOnlineOrder);
  /**
   * @描述:修改订单描述
   * @作者:nada
   * @时间:2018年3月8日 上午11:00:49
   */
  int updateOrderDes(VcOnlineOrder vcOnlineOrder);
  

    /**
     * @描述:修改分润状态
     * @作者:nada
     * @时间:2018年3月8日 上午11:00:49
     */
    int updateProfitStatus(VcOnlineOrder vcOnlineOrder);

	/**
     * @描述:修改订单结算状态
     * @作者:nada
     * @时间:2018年3月8日 上午11:00:49
     */
  int updateOrderSettleStatus(VcOnlineOrder vcOnlineOrder);
    
    /**
     * @描述:修改下游订单通知信息
     * @作者:nada
     * @时间:2018年1月31日 下午2:37:24
     */
    int updateOrderNotify(VcOnlineOrder vcOnlineOrder);
    
    /**
     * @描述:汇总今日交易
     * @时间:2018年6月15日 下午4:55:14
     */
    VcOnlineOrder totalTodayTrad();
    /**
     * @描述:查询中间状态订单
     * @作者:ChaiJing THINK
     * @时间:2018/7/2 16:25
     */
    List<VcOnlineOrder> findPaddingOrder(VcOnlineOrder onlineOrder);
    /**
     * @描述:更新订单金额
     * @作者:ChaiJing THINK
     * @时间:2018/8/29 11:28
     */
    int updateOrderTraAmount(VcOnlineOrder onlineOrder);

    int updateUpMerchNoOrderNo(VcOnlineOrder vcOnlineOrder);
    /**
     * @描述:重复支付订单保存
     * @作者:ChaiJing THINK
     * @时间:2018/9/4 9:36
     */
    int saveCopyOrder(VcOnlineOrder onlineOrder);
    /**
     * @描述:匹配上游单号
     * @作者:THINK Daniel
     * @时间:2018/10/17 10:19
     */
    List<VcOnlineOrder> verifyPOrderExist(VcOnlineOrder onlineOrder);

    List<VcOnlineOrder> verifyRePayPOrderExist(VcOnlineOrder onlineOrder);

    /**
     * 根据支付金额匹配订单
     */
    List<VcOnlineOrder> findOrderByUniqueAmount(VcOnlineOrder order);

    /**
     * @desc 更新上游信息
     * @author Tequila
     * @create 2019/5/18 14:43
     * @param vcOnlineOrder
     * @return
     */
    int updateUpInfoByOrderNo(VcOnlineOrder vcOnlineOrder);
}