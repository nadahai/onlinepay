/**
 * @类名称:OrderMapper.java
 * @时间:2017年6月6日上午9:07:41
 * @作者:lihai
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.persistent.mapper.dict;


import com.vc.onlinepay.persistent.entity.dict.Bank;
import org.springframework.stereotype.Repository;

/**
 * @描述:银行卡持久化接口
 * @作者:lihai
 * @时间:2017年6月6日 上午9:07:41 
 */
@Repository
public interface BankMapper {

  /**
   * @描述:根据订单编号查询订单信息
   * @作者:lihai
   * @时间:2017年6月7日 上午9:10:37
   */
  Bank findBankByBankId(Long bankId);
}

