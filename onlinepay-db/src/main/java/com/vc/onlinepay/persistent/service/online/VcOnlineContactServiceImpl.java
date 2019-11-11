/**
 * @类名称:OrderServiceImpl.java
 * @时间:2017年6月6日上午9:09:22
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.persistent.service.online;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.entity.online.VcOnlineContact;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineContactMapper;
import com.vc.onlinepay.utils.DisplayUtil;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.ninepie.BankUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @作者:Alan
 * @时间:2017年6月6日 上午9:09:22
 */
@Service
@Transactional (readOnly = true)
public class VcOnlineContactServiceImpl {

    @Autowired
    private VcOnlineContactMapper vcOnlineContactMapper;

    /**
     * @描述:保存协议表信息
     * @作者:Alan
     * @时间:2017年6月5日 下午9:47:43
     */
    @Transactional (readOnly = false,rollbackFor = Exception.class)
    public int saveNetContact (VcOnlineContact vcOnlineContact) throws OnlineServiceException {
        return vcOnlineContactMapper.saveNetContact (vcOnlineContact);
    }

    /**
     * @描述:修改协议表信息
     * @作者:Alan
     * @时间:2017年6月5日 下午9:47:43
     */
    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public void updateStatusContact (VcOnlineContact vcOnlineContact) throws OnlineServiceException {
        vcOnlineContactMapper.updateNetContact (vcOnlineContact);
    }
    
    /**
     * @描述:根据Id修改协议表信息
     * @作者:Alan
     * @时间:2017年6月5日 下午9:47:43
     */
    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public void updateNetContactById (VcOnlineContact vcOnlineContact) throws OnlineServiceException {
        vcOnlineContactMapper.updateNetContactById (vcOnlineContact);
    }
    
    /**
     * @描述:根据id逻辑删除卡号信息
     * @作者:gongchen
     * @时间:2019年6月4日11:26:11
     */
    @Transactional (readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.DEFAULT)
    public int delCardInfoById (VcOnlineContact vcOnlineContact) throws OnlineServiceException {
        return vcOnlineContactMapper.delCardInfoById (vcOnlineContact);
    }

    /**
     * @描述:查询协议表信息
     * @作者:Alan
     * @时间:2017年6月5日 下午9:47:43
     */
    public List<VcOnlineContact> findContactByCondition (VcOnlineContact vcOnlineContact) throws OnlineServiceException {
        return vcOnlineContactMapper.findContactByCondition (vcOnlineContact);
    }


    /**
     * @描述:查询协议表信息
     * @作者:Alan
     * @时间:2017年6月5日 下午9:47:43
     */
    public VcOnlineContact findContactByid (String cardId) throws OnlineServiceException {
        if(StringUtil.isEmpty(cardId)){
            return null;
        }
        VcOnlineContact vcOnlineContact = new VcOnlineContact();
        vcOnlineContact.setId(new BigDecimal(cardId));
        List<VcOnlineContact>  list = vcOnlineContactMapper.findContactByCondition (vcOnlineContact);
        if(list == null || list.size()<1){
            return null;
        }
        return list.get(0);
    }

    /**
     * @描述:查询协议表信息
     * @作者:Alan
     * @时间:2017年6月5日 下午9:47:43
     */
    public ArrayList<JSONObject> getBankList (VcOnlineContact vcOnlineContact) throws OnlineServiceException {
        List<VcOnlineContact> list = findContactByCondition(vcOnlineContact);
        if(list == null || list.size() <1){
            return new ArrayList<>();
        }
        ArrayList<JSONObject> arrayList = new  ArrayList<>();
        for (VcOnlineContact contact : list) {
        	JSONObject result = new JSONObject();
            String cardNo = StringUtils.deleteWhitespace(contact.getCardNo());
            if(StringUtil.isNotEmpty(cardNo)){
            	String bankName = BankUtil.getNameOfBank(cardNo); //根据卡号获取银行名称
            	result.put("cardNo", DisplayUtil.displayBankCard(cardNo));
            	result.put("id", contact.getId().toString());
            	result.put("idNo", StringUtils.deleteWhitespace(contact.getIdNo()));
            	result.put("bankName", bankName);
            	if (Strings.isEmpty(bankName)||bankName.equals("暂未查到此银行")) {
            		result.put("bankType", 2);
            		result.put("bankName", "无效卡");
            	} else {
            		result.put("bankType", 1);
            	}
            	arrayList.add(result);
            }
        }
        return arrayList;
    }

    /**
     * @描述:批量保存信息
     * @作者:leoncongee 
     * @时间:2019年5月9日20:11:07
     */
    @Transactional (readOnly = false)
    public int batchSaveNetContact(List<VcOnlineContact> vcOnlineContacts) throws OnlineServiceException {
    	return vcOnlineContactMapper.batchSaveNetContact (vcOnlineContacts);
    }
    
    /**
     * @描述:批量更新信息
     * @作者:leoncongee 
     * @时间:2019年5月9日20:11:36
     */
    @Transactional (readOnly = false)
    public int batchUpdateNetContact(VcOnlineContact vcOnlineContact) throws OnlineServiceException {
    	return vcOnlineContactMapper.batchUpdateNetContact (vcOnlineContact);
    }
}
