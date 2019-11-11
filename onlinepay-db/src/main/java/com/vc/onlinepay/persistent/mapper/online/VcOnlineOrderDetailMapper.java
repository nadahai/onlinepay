/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.online;

import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderDetail;
import org.springframework.stereotype.Repository;

/**
 * 李海在线交易订单DAO接口
 * 
 * @author 李海
 * @version 2017-06-30
 */
@Repository
public interface VcOnlineOrderDetailMapper {

    /**
     * @描述:保存交易订单
     * @作者:lihai 
     * @时间:2018年1月31日 下午2:32:08
     */
    int save(VcOnlineOrderDetail vcOnlineOrder);
    /**
     * @描述:更新订单实际支付金额
     * @作者:ChaiJing THINK
     * @时间:2018/8/30 15:29
     */
    int updateOrderDetailRealAmount(VcOnlineOrderDetail onlineOrderDetail);
}