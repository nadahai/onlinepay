/**
 * @类名称:OrderMapper.java
 * @时间:2017年6月6日上午9:07:41
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.persistent.mapper.online;

import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.entity.online.VcOnlineContact;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @时间:2017年6月6日 上午9:07:41
 */
@Repository
public interface VcOnlineContactMapper {
    
    /**
     * @描述:保存订单
     * @时间:2017年6月6日 上午9:08:21
     */
    int saveNetContact(VcOnlineContact vcOnlineContact) throws OnlineServiceException;
    
    /**
     * @描述:修改成功订单表信息
     * @时间:2017年6月5日 下午9:48:07
     */
    void updateNetContact(VcOnlineContact vcOnlineContact) throws OnlineServiceException;
    
    /**
     * @描述:根据ID修改成功订单表信息
     * @时间:2017年6月5日 下午9:48:07
     */
    void updateNetContactById(VcOnlineContact vcOnlineContact) throws OnlineServiceException;
    
    
    /**
     * @描述:修改根据卡号逻辑删除银行信息
     * @时间:2019年6月4日11:26:57
     */
    int delCardInfoById(VcOnlineContact vcOnlineContact) throws OnlineServiceException;
    /**
     * @描述:修改成功订单表信息
     * @时间:2017年6月5日 下午9:48:07
     */
    void updateNetContactSms(VcOnlineContact vcOnlineContact) throws OnlineServiceException;
    /**
     * @描述:根据订单编号查询订单信息
     * @时间:2017年6月7日 上午9:10:37
     */
    List<VcOnlineContact> findContactByCondition(VcOnlineContact vcOnlineContact) throws OnlineServiceException;
    
    /**
     * @描述:批量保存信息
     * @时间:2019年5月9日20:11:07
     */
    int batchSaveNetContact(@Param("vcOnlineContacts") List<VcOnlineContact> vcOnlineContacts) throws OnlineServiceException;
    
    /**
     * @描述:批量更新信息
     * @时间:2019年5月9日20:11:36
     */
    int batchUpdateNetContact(VcOnlineContact vcOnlineContact) throws OnlineServiceException;
    
}

