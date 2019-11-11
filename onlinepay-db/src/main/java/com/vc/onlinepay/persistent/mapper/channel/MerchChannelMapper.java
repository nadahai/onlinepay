/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.channel;

import com.vc.onlinepay.exception.OnlineServiceException;
import java.util.List;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.vc.onlinepay.persistent.entity.channel.MerchChannel;

/**
 * @类名称:MerchChannelMapper.java
 * @时间:2017年6月5日下午10:38:56
 * @作者:lihai 
 * @版权:版权所有 Copyright (c) 2017
 */
@Repository
public interface MerchChannelMapper {
    /**
     * @Description: 根据商户编号,支付方式查商户通道列表信息
     * @param: @param merchId
     * @param: @param payTypes
     */
    List<MerchChannel> findMerchChannelPayTypes (@Param ("merchId") Long merchId, @Param ("payTypes") List<Long> payTypes) throws OnlineServiceException;
    
    /**
     * @描述:根据支付方式和商户id获取结算方式
     * @作者:Jauyang
     * @时间:2017年12月18日 下午10:46:05
     */
    List<MerchChannel> findCanUsedMerchChannelByMerchId (@Param ("merchId") Long merchId, @Param ("payType") Long payType);
    /**
     * @描述:根据通道来源、支付方式、结算方式 查找通道
     * @作者:ChaiJing THINK
     * @时间:2018/8/22 16:49
     */
    List<MerchChannel> findSameChannelBysource (MerchChannel merchChannel);
}