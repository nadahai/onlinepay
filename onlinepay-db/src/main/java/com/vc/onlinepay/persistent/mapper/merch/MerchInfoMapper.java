/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.merch;

import org.springframework.stereotype.Repository;

import com.vc.onlinepay.persistent.entity.merch.MerchInfo;

import java.util.List;

/**
 * @类名称:MerchInfoMapper.java
 * @时间:2017年6月5日下午10:39:04
 * @作者:nada
 * @版权:版权所有 Copyright (c) 2017
 */
@Repository
public interface MerchInfoMapper {

    /**
     * @描述:根据编号获取商户信息
     * @作者:nada
     * @时间:2017年6月5日 下午9:09:01
     */
    public MerchInfo getMerchInfoByNo(long merchNo);
    
}