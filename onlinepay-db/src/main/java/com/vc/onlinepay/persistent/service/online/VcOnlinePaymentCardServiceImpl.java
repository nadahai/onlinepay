/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.online;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePaymentCard;
import com.vc.onlinepay.persistent.mapper.online.VcOnlinePaymentCardMapper;

/**
 * 在线出款方案Service
 * @author 李海
 * @version 2017-06-30
 */
@Service
@Transactional(readOnly = true)
public class VcOnlinePaymentCardServiceImpl {

	public static final Logger logger = LoggerFactory.getLogger(VcOnlinePaymentCardServiceImpl.class);
	
	@Autowired
	private VcOnlinePaymentCardMapper vcOnlinePaymentCardMapper;
	
    @Autowired
    private RedisCacheApi redisCacheApi;

	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public int save(VcOnlinePaymentCard card) {
		try {
			return vcOnlinePaymentCardMapper.save(card);
		} catch (Exception e) {
			return 0;
		}
	}

	public List<VcOnlinePaymentCard> findDisableBank(VcOnlinePaymentCard card) {
		return vcOnlinePaymentCardMapper.findDisableBank(card);
	}
	
	/**
	 * @描述:是否已经存在银行卡号
	 * @时间:2018年6月19日 下午4:34:40
	 */
	public boolean isExistBankNo(String bankCardNo,int loopNum){
		try {
			Map<Object, Object>  bankNoMap = redisCacheApi.getMap(CacheConstants.CACHE_TABLE_KEY_PAYMENT_CARD +"map");
			 if(bankNoMap != null && !bankNoMap.isEmpty() && bankNoMap.size() > 0){
				 if(bankNoMap.containsKey(bankCardNo)){
					return true;
				 }
			 }else{
				 bankNoMap = new HashMap<>();
			 }
			 List<VcOnlinePaymentCard> list = this.findDisableBank(new VcOnlinePaymentCard(bankCardNo));
			 if(list == null || list.size() < 1){
				return false; 
			 }
			 for (VcOnlinePaymentCard bank : list) {
				 if(bank !=null && StringUtil.isNotEmpty(bank.getBankCard())){
					 bankNoMap.put(bank.getBankCard(), String.valueOf(bank.getMerchId()));
				 }
			 }
			 redisCacheApi.setMap(CacheConstants.CACHE_TABLE_KEY_PAYMENT_CARD +"map", bankNoMap);
			 if(loopNum > 2){
				 return false;
			 }
			 return this.isExistBankNo(bankCardNo,loopNum++);
		} catch (Exception e) {
			logger.error("是否已经存在银行卡号异常",e);
			return false;
		}
	}
}