/**
 * @类名称:OrderServiceImpl.java
 * @时间:2017年6月6日上午9:09:22
 * @作者:lihai
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.persistent.service.dict;

import com.vc.onlinepay.exception.OnlineDbException;
import com.vc.onlinepay.persistent.entity.dict.SubbankLinked;
import com.vc.onlinepay.persistent.mapper.dict.SubbankMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @描述:银行卡基础信息
 * @作者:Alan
 * @时间:2017年6月6日 上午9:09:22
 */
@Service
@Transactional (readOnly = true,rollbackFor = Exception.class)
public class SubbankServiceImpl {

    @Autowired
    private SubbankMapper subbankMapper;

    public List<SubbankLinked> findSubbankByBankLink (Long subbankId) throws OnlineDbException {
        return subbankMapper.findSubbankByBankLink (subbankId);
    }

}
