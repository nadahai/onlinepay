/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.online;

import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderAa;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineOrderAaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * aa订单收款Service
 *
 * @author nada
 * @version 2019-04-24
 */
@Service
@Transactional (readOnly = true)
public class VcOnlineOrderAaService {

    private static final Logger LOG = LoggerFactory.getLogger (VcOnlineOrderAaService.class);

    public static SimpleDateFormat bankformat = new SimpleDateFormat ("dd日");

    @Autowired
    private VcOnlineOrderAaMapper vcOnlineOrderAaMapper;

    /**
     * @描述:根据金额查询所有正常使用的交易NO
     * @作者:nada
     * @时间:2019/4/26
     **/
    public List<String> findAllOkTradNos (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.findAllOkTradNos (vcOnlineOrderAa);
    }

    /**
     * @描述:根据tradNo查询账号信息
     * @作者:nada
     * @时间:2019/4/26
     **/
    public VcOnlineOrderAa findAAByTradNo (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.findAAByTradNo (vcOnlineOrderAa);
    }

    /**
    * @描述 获取一条AA库下单中的AA链接
    * @作者 nada
    * @时间 2019/5/19 22:34
    */
    public VcOnlineOrderAa findOneOkAATradNo (VcOnlineOrderAa vcOnlineOrderAa) {
        List<VcOnlineOrderAa> lists = vcOnlineOrderAaMapper.findOneOkAATradNo (vcOnlineOrderAa);
        if(lists == null || lists.size()<1){
            return  null;
        }
        return lists.get(0);
    }

    /**
     * @描述:查询所有下单中的交易订单号
     * @作者:nada
     * @时间:2019/4/26
     **/
    public VcOnlineOrderAa findOneOkTradNos (VcOnlineOrderAa vcOnlineOrderAa) {
        List<String> allTradNos = this.findAllOkTradNos(vcOnlineOrderAa);
        if(allTradNos == null || allTradNos.size()<=0){
           return null;
        }
        vcOnlineOrderAa.setTradeno (allTradNos.get (0));
        return this.findAAByTradNo(vcOnlineOrderAa);
    }


    public List<VcOnlineOrderAa> findAllList (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.findAllList (vcOnlineOrderAa);
    }

    public List<VcOnlineOrderAa> findCallList (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.findCallList (vcOnlineOrderAa);
    }

    public List<VcOnlineOrderAa> findCallByUserIds (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.findCallByUserIds (vcOnlineOrderAa);
    }

    public List<VcOnlineOrderAa> findByUserId (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.findByUserId (vcOnlineOrderAa);
    }

    public VcOnlineOrderAa findOtherUserId (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.findOtherUserId (vcOnlineOrderAa);
    }

    public VcOnlineOrderAa getOneAA (VcOnlineOrderAa vcOnlineOrderAa) {
        List<VcOnlineOrderAa> list = this.findAllList (vcOnlineOrderAa);
        if (list != null && list.size () > 0) {
            return list.get (0);
        }
        return null;
    }

    /*public VcOnlineOrderAa getThreeAA (VcOnlineOrderAa vcOnlineOrderAa,int amount) {
        List<String> upMerchNos = this.findUpMerchNosByAmount(vcOnlineOrderAa);
        if(upMerchNos == null || upMerchNos.size ()< 1){
            return null;
        }
        return this.getOneAA (new VcOnlineOrderAa(upMerchNos.get (0),amount));
    }*/

    //根据金额查询指定的子商户号
    /*public List<String> findUpMerchNosByAmount (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.findUpMerchNosByAmount (vcOnlineOrderAa);
    }*/


    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateStatus (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.updateStatus (vcOnlineOrderAa);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateOrderSuccess (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.updateOrderSuccess (vcOnlineOrderAa);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateToken (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.updateToken (vcOnlineOrderAa);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int updateUserId (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.updateUserId (vcOnlineOrderAa);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int save (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.insert (vcOnlineOrderAa);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public void saveBatch (VcOnlineOrderAa vcOnlineOrderAa) {
        if (vcOnlineOrderAa == null || vcOnlineOrderAa.getShowTimesTotal () < 1) {
            return;
        }
        int showTimesTotal = vcOnlineOrderAa.getShowTimesTotal ();
        String zhOrder = bankformat.format (new Date ()) + vcOnlineOrderAa.getZhOrder ();
        List<VcOnlineOrderAa> entityList = new ArrayList<VcOnlineOrderAa> ();
        for (int i = 0; i < showTimesTotal; i++) {
            VcOnlineOrderAa item = new VcOnlineOrderAa();
            BeanUtils.copyProperties (vcOnlineOrderAa, item);
            if (i == 0) {
                item.setType (1);
                item.setStatus (4);
            } else {
                item.setType (2);
                item.setStatus (3);
            }
            item.setZhOrder (zhOrder + i);
            entityList.add (item);
        }
        int count = vcOnlineOrderAaMapper.insertBatch (entityList);
        LOG.info ("批次插入订单：{} 条!", count);
    }

    @Transactional (readOnly = true, rollbackFor = Exception.class)
    public int findRepead (Integer amount, String upMerchNo, String reason) {
        return vcOnlineOrderAaMapper.selectRepeat (amount, upMerchNo, reason);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int insertOne (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.insertOne (vcOnlineOrderAa);
    }

    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public VcOnlineOrderAa findAAByZhOrder (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.findAAByZhOrder (vcOnlineOrderAa);
    }

    @Transactional (readOnly = true, rollbackFor = Exception.class)
    public int findOrderCount (String upMerchNo) {
        return vcOnlineOrderAaMapper.selectOrderCount (upMerchNo);
    }


    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int delete (VcOnlineOrderAa vcOnlineOrderAa) {
        return vcOnlineOrderAaMapper.delete (vcOnlineOrderAa);
    }

}