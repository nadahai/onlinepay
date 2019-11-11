/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.channel;

import com.vc.onlinepay.persistent.entity.channel.ChannelWxno;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * 微信账号DAO接口
 * @author nada
 * @version 2018-12-14
 */
@Repository
public interface ChannelWxnoMapper {

  /**
   * @描述:修改狀態
   * @时间:2018年5月17日 下午7:19:07
   */
  int updateStatus (ChannelWxno channelWxno);

  /**
   * @描述:修改限额
   * @时间:2018年5月17日 下午7:19:07
   */
  int updateWxbNoAmount (ChannelWxno channelWxno);

  /**
   * @描述:根据账号获取列表
   * @时间:2018年9月13日 下午12:12:24
   */
  List<ChannelWxno> getByUpMerchNo (ChannelWxno subno);

  /**
   * @描述:获取所有子账号列表
   * @时间:2018年9月13日 下午12:12:24
   */
  List<ChannelWxno> getAllChannelWxNoList (ChannelWxno subno);

  /**
   * @描述:获取所名称列表
   * @时间:2018年9月13日 下午12:12:24
   */
  List<ChannelWxno> getNameList (ChannelWxno subno);

  /**
   * @描述:商户更新下单时间
   * @时间:2018/8/13 14:36
   */
  Integer updateLastOrderTime(ChannelWxno channelSubNo);

  /**
   * 更新子商户状态
   * @param channelSubNo
   */
  void removeSubNo(ChannelWxno channelSubNo);
}