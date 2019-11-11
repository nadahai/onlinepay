/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.online;

import com.vc.onlinepay.persistent.entity.online.VcOnlineWalletRecord;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineWalletRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 在线交易财富信息Service
 *
 * @author Alan
 * @version 2017-06-30
 */
@Service
@Transactional (readOnly = true, rollbackFor = Exception.class)
public class VcOnlineWalletRecordServiceImpl {

    @Autowired
    private VcOnlineWalletRecordMapper vcOnlineWalletRecordMapper;

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int saveWalletRecord (VcOnlineWalletRecord vcOnlineWalletRecord) {
        return vcOnlineWalletRecordMapper.saveWalletRecord (vcOnlineWalletRecord);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateWalletRecord (VcOnlineWalletRecord vcOnlineWalletRecord) {
        return vcOnlineWalletRecordMapper.updateWalletRecord (vcOnlineWalletRecord);
    }

}