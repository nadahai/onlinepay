/**
 * @类名称:MerchInfoServiceImpl.java
 * @时间:2017年6月5日下午7:00:26
 * @作者:nada
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.persistent.service.merch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vc.onlinepay.persistent.entity.merch.MerchInfo;
import com.vc.onlinepay.persistent.mapper.merch.MerchInfoMapper;

/**
 * @描述:商户信息业务类
 * @作者:nada
 * @时间:2017年6月5日 下午7:00:26 
 */
@Service
@Transactional (readOnly = true,rollbackFor = Exception.class)
public class MerchInfoServiceImpl {

    @Autowired
    private MerchInfoMapper merchInfoMapper;

    /**
     * @描述:根据编号获取商户信息
     * @作者:nada
     * @时间:2017年6月5日 下午9:09:21
     */
    public MerchInfo getMerchInfoByNo (long merchNo) {
        return merchInfoMapper.getMerchInfoByNo (merchNo);
    }
}

