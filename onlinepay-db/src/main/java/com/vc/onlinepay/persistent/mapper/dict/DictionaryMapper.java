/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.dict;

import com.vc.onlinepay.persistent.entity.dict.Dictionary;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * @类名称:自定义系统字典值持久化接口
 * @时间:2017年8月24日下午3:54:23
 * @作者:lihai
 * @版权:版权所有 Copyright (c) 2017
 */
@Repository
public interface DictionaryMapper {

  /**
   * @描述:根据key获取字典值
   * @作者:lihai
   * @时间:2017年7月6日 下午7:49:41
   */
  Dictionary findByKey(Dictionary entity);

  /**
   * @描述:获取所有字典列表
   * @作者:lihai
   * @时间:2018年3月12日 上午11:50:28
   */
  List<Dictionary> getAllList();

  /**
   * @描述:获取访问授权IP列表
   * @作者:THINK Daniel
   * @时间:2018/10/29 17:09
   */
  List<Dictionary> findAccessIp(Dictionary dictionary);
}
