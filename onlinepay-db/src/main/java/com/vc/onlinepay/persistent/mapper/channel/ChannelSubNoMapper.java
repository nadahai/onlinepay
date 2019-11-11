/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.channel;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.vc.onlinepay.persistent.entity.channel.ChannelSubNo;

/**
 * @类名称:MerchInfoMapper.java
 * @时间:2017年6月5日下午10:39:04
 * @作者:lihai 
 * @版权:版权所有 Copyright (c) 2017
 */
@Repository
public interface ChannelSubNoMapper {
	
	/**
	 * @描述:找渠道商
	 * @时间:2018年9月14日 下午6:34:04
	 */
    ChannelSubNo getByUpMerchNo (ChannelSubNo channelSubNo);
	
    /**
     * @描述:获取一批子商户号
     * @作者:lihai 
     * @时间:2017年6月5日 下午9:09:01
     */
    List<ChannelSubNo> getChannelSubNoList (ChannelSubNo channelSubNo);
    
    
    /**
     * @描述:获取指定时间范围内一批子商户号
     * @作者:lihai 
     * @时间:2017年6月5日 下午9:09:01
     */
    List<ChannelSubNo> getChannelLimitTimeSubNoList (ChannelSubNo channelSubNo);
    
    
    /**
     * @描述:自研支付宝获取指定时间范围内一批子商户号
     * @作者:lihai 
     * @时间:2017年6月5日 下午9:09:01
     */
    List<ChannelSubNo> getLoopRobinSubNoList (ChannelSubNo channelSubNo);
    
    
    /**
     * @描述:企业支付宝列表
     * @作者:lihai 
     * @时间:2017年6月5日 下午9:09:01
     */
    List<ChannelSubNo> getAllSupplierList (ChannelSubNo channelSubNo);
    
    
    /**
     * @描述:更新金额
     * @时间:2018年5月17日 下午7:19:07
     */
    Integer updateSubNoAmount (ChannelSubNo channelSubNo);

    /**
     * @描述:商户限额更新
     * @作者:ChaiJing THINK
     * @时间:2018/8/13 14:36
     */
    Integer updateLimitAmount(ChannelSubNo channelSubNo);
    
    /**
     * @描述:商户更新下单时间
     * @作者:ChaiJing THINK
     * @时间:2018/8/13 14:36
     */
    Integer updateLastOrderTime(ChannelSubNo channelSubNo);
    
    /**
     * @描述:查找已禁用商户
     * @作者:ChaiJing THINK
     * @时间:2018/8/13 14:44
     */
    List<ChannelSubNo> findLimited(ChannelSubNo channelSubNo);
    /**
     * @描述:十分钟内下过单的商户
     * @作者:ChaiJing THINK
     * @时间:2018/9/6 10:23
     */
    List<String> findUsedMerchNo(ChannelSubNo channelSubNo);
}