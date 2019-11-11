/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.online;

import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.entity.online.VcOnlineWallet;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineWalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 在线交易财富信息Service
 *
 * @author 李海
 * @version 2017-06-30
 */
@Service
@Transactional (readOnly = true, rollbackFor = Exception.class)
public class VcOnlineWalletServiceImpl {

    @Autowired
    private VcOnlineWalletMapper vcOnlineWalletMapper;

    public VcOnlineWallet findByMerchId (Long merchId) {
        return vcOnlineWalletMapper.findByMerchId (merchId);
    }

    public VcOnlineWallet findVcOnlineWalletBymerchNo (String merchantNo) {
        return vcOnlineWalletMapper.findBymerchNo (merchantNo);
    }

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updateD0SuccessOrder (VcOnlineWallet vcOnlineWallet) {
        return vcOnlineWalletMapper.updateD0SuccessOrder (vcOnlineWallet);
    }

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updateD1SuccessOrder (VcOnlineWallet vcOnlineWallet) {
        return vcOnlineWalletMapper.updateD1SuccessOrder (vcOnlineWallet);
    }

    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int updatStraightSuccessOrder (VcOnlineWallet vcOnlineWallet) {
        return vcOnlineWalletMapper.updatStraightSuccessOrder (vcOnlineWallet);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateD0WalletCashStart (VcOnlineWallet vcOnlineWallet) throws OnlineServiceException {
        return vcOnlineWalletMapper.updateD0WalletCashStart (vcOnlineWallet);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateD0WalletCashRollback (VcOnlineWallet vcOnlineWallet) throws OnlineServiceException {

        return vcOnlineWalletMapper.updateD0WalletCashRollback (vcOnlineWallet);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateD0WalletCashDone (VcOnlineWallet vcOnlineWallet) throws OnlineServiceException {
        return vcOnlineWalletMapper.updateD0WalletCashDone (vcOnlineWallet);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateD1WalletCashStart (VcOnlineWallet vcOnlineWallet) throws OnlineServiceException {

        return vcOnlineWalletMapper.updateD1WalletCashStart (vcOnlineWallet);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateD1WalletCashRollback (VcOnlineWallet vcOnlineWallet) throws OnlineServiceException {

        return vcOnlineWalletMapper.updateD1WalletCashRollback (vcOnlineWallet);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateD1WalletCashDone (VcOnlineWallet vcOnlineWallet) throws OnlineServiceException {
        return vcOnlineWalletMapper.updateD1WalletCashDone (vcOnlineWallet);
    }

}