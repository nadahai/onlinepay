/**
 * @类名称:VcOnlineLogServiceImpl.java
 * @时间:2018年3月5日下午3:44:40
 * @作者:nada
 * @版权:公司 Copyright (c) 2018
 */
package com.vc.onlinepay.persistent.service.online;

import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineLog;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @描述:日志类处理
 * @时间:2018年3月5日 下午3:44:40 
 */
@Service
@Transactional (readOnly = true)
public class VcOnlineLogServiceImpl {

    @Autowired
    private VcOnlineLogMapper vcOnlineLogMapper;

    @Autowired
    private CoreEngineProviderService coreEngineProviderService;

    /**
     * @描述:保存日志
     * @作者:nada
     * @时间:2018年3月5日 下午3:46:05
     */
    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int save (VcOnlineLog vcOnlineLog) throws OnlineServiceException {
        if ("true".equals (coreEngineProviderService.getCacheCfgKey (CacheConstants.SAVE_LOG_SWITCH))) {
            return vcOnlineLogMapper.save (vcOnlineLog);
        }
        return 1;
    }
}

