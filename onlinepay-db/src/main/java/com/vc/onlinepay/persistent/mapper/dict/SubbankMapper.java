/**
 * @类名称:OrderMapper.java
 * @时间:2017年6月6日上午9:07:41
 * @作者:nada
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.persistent.mapper.dict;

import com.vc.onlinepay.persistent.entity.dict.SubbankLinked;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @描述:支行信息持久化接口
 * @作者:Alan
 * @时间:2017年6月6日 上午9:07:41
 */
@Repository
public interface SubbankMapper {

  /**
   * @描述:根据联行号获取支行列表信息
   * @作者:nada
   * @时间:2018/12/7
   **/
  List<SubbankLinked> findSubbankByBankLink(@Param("subBankId") Long subBankId);

  /***
   * @描述:根据省市获取支行列表信息
   * @作者:nada
   * @时间:2018/12/7
   **/
  List<SubbankLinked> findBycity(SubbankLinked subbankLinked);

  /**
   * @描述:随机获取支行信息（省市编码）
   * @作者:nada
   * @时间:2018/12/7
   **/
  SubbankLinked findRandom();
}

