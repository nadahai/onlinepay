/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.merch;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.vc.onlinepay.persistent.entity.merch.SettleSubno;

/**
 * 账号归集DAO接口
 * @author 李海
 * @version 2018-10-23
 */
@Repository
public interface SettleSubnoMapper {
	
	/**
	 * @描述:归集账号列表
	 * @时间:2018年10月29日 上午11:19:07
	 */
	public List<SettleSubno> findSettleList(SettleSubno subno);

	/**
	 * @描述:根据渠道修改状态
	 * @时间:2018年9月13日 下午12:12:24
	 */
	public int updateStatus(SettleSubno subno);
	
	/**
	 * @描述:归集限额问题
	 * @时间:2018年10月29日 上午11:36:19
	 */
	public int updateSettleAmount(SettleSubno subno);


    SettleSubno get(int id);
}