/**
 * @类名称:OrderMapper.java
 * @时间:2017年6月6日上午9:07:41
 * @作者:nada
 * @版权:版权所有 Copyright (c) 2017 
 */
package com.vc.onlinepay.persistent.mapper.online;


import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.entity.online.VcOnlineLog;
import org.springframework.stereotype.Repository;

/**
 * @描述:TODO
 * @作者:nada
 * @时间:2017年6月6日 上午9:07:41 
 */
@Repository
public interface VcOnlineLogMapper {
    
   /**
    * @描述:保存日志
    * @作者:nada
    * @时间:2018年3月5日 下午3:27:50
    */
   int save(VcOnlineLog vcOnlineLog) throws OnlineServiceException;
    
}

