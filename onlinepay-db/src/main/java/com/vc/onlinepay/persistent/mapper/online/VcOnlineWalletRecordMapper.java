/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.online;

import org.springframework.stereotype.Repository;

import com.vc.onlinepay.persistent.entity.online.VcOnlineWalletRecord;

/**
 * 在线交易财富信息DAO接口
 * @author Alan
 * @version 2017-06-30
 */
@Repository
public interface VcOnlineWalletRecordMapper{
	
	public int saveWalletRecord(VcOnlineWalletRecord vcOnlineWalletRecord);
	
	public int updateWalletRecord(VcOnlineWalletRecord vcOnlineWalletRecord);

}