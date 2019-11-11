/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.merch;

import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.entity.merch.SettleSubno;
import com.vc.onlinepay.persistent.mapper.merch.SettleSubnoMapper;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 供应商账号管理Service
 *
 * @author 李海
 * @version 2018-09-12
 */
@Service
@Transactional (readOnly = true,rollbackFor = Exception.class)
public class SettleSubnoServiceImpl {

    @Autowired
    private SettleSubnoMapper settleSubnoMapper;

    public SettleSubno get (int id) {
        return settleSubnoMapper.get (id);
    }

    /**
     * @描述:修改账号限额
     * @时间:2018年9月13日 下午12:12:24
     */
    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateSettleAmount (SettleSubno settleSubno) {
        return settleSubnoMapper.updateSettleAmount (settleSubno);
    }

    /**
     * @描述:获取所有渠道商列表
     * @时间:2018年9月14日 下午12:18:03
     */
    public SettleSubno getOneSettleNo (SettleSubno settleSubno) throws OnlineServiceException {
        List<SettleSubno> lists = settleSubnoMapper.findSettleList (settleSubno);
        if (lists == null || lists.size () < 1) {
            return null;
        }
        double traAmount = settleSubno.getTraAmount ().doubleValue ();
        BigDecimal settleAmount = settleSubno.getTraAmount ();
        for (SettleSubno subNo : lists) {
            if (subNo.getDayQuotaAmount ().compareTo (subNo.getDayTraAmount ().add (settleAmount)) < 0) {
                continue;
            }
            if (subNo.getQuotaAmount ().compareTo (subNo.getTraAmount ().add (settleAmount)) < 0) {
                continue;
            }
            if (subNo.getMinPrice () >= 0 && subNo.getMaxPrice () > 0 && subNo.getMinPrice () <= traAmount && subNo.getMaxPrice () >= traAmount) {
                return subNo;
            }
        }
        return null;
    }
}