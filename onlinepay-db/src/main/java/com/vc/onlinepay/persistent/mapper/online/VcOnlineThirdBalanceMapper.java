/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.online;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.vc.onlinepay.persistent.entity.online.VcOnlineThirdBalance;

/**
 * 在线交易财富信息DAO接口
 * @author Alan
 * @version 2017-06-30
 */
@Repository
public interface VcOnlineThirdBalanceMapper{
	
	public int saveThirdBalance(VcOnlineThirdBalance vcOnlineThirdBalance);

	public int updateBalance(VcOnlineThirdBalance vcOnlineThirdBalance);

	/**
	 * @描述:查找最优代付通道
	 * @作者:nada
	 * @时间:2017年12月20日 上午11:27:20
	 */
	public List<VcOnlineThirdBalance> findLoadBalance(VcOnlineThirdBalance thirdBalance);
	
	/**
	 * @Description:  查找所有代付通道（包括禁用和关闭）
	 * @param: @param thirdBalance
	 */
	public List<VcOnlineThirdBalance> findAllBalance(VcOnlineThirdBalance thirdBalance);
	
	/**
	 * @描述:提现成功后更新账户表
	 * @作者:nada
	 * @时间:2017年12月21日 上午10:25:32
	 */
	public int cashSuccessUpdateBalance(VcOnlineThirdBalance vcOnlineThirdBalance);
}