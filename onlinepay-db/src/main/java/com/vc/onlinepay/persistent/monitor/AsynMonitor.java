/**
 * @类名称:AsynMonitorNotice.java
 * @时间:2018年6月1日下午3:12:25
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.persistent.monitor;

import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.utils.StringUtil;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:异步监控处理
 * @时间:2018年6月1日 下午3:12:25 
 */
@Service
@Component
public class AsynMonitor {
	
	public static final Logger logger = LoggerFactory.getLogger(AsynMonitor.class);
	
	private static Map<String, Integer> monitorMap = new HashMap<String, Integer>();
	@Autowired
	private CoreEngineProviderService coreEngineProviderServiceImpl;
	@Autowired
	private AsynNotice asynNotice;
	@Autowired
	private RedisCacheApi redisCacheApi;

	private static final String ORDER_MONITOR_KEY = "ORDER_MONITOR_";
	
	/**
	 * @描述:交易成功监控
	 * @时间:2018年6月1日 下午3:33:23
	 */
	public boolean orderMonitor(String key,int status){
		try {
			if(!"true".equals(coreEngineProviderServiceImpl.getCacheCfgKey(CacheConstants.ONLINE_SWITCH_MONITOR_AUTO))){
				return true;
			}
			if(status == 4){
				removeCount(key);
				return removeCount("market");
			}
			int num = coreEngineProviderServiceImpl.getIntCacheCfgKey(CacheConstants.ONLINE_SWITCH_MONITOR_FAILED);
			//所有交易监控
			int markCount = getCount("market");
			if(markCount >=num){
				return monitorNotice("market",markCount);
			}
			updateCount("market");
			//单商户号监控
			int keyCount = getCount(key);
			if(keyCount >=num){
				return	monitorNotice(key,keyCount);
			}
			return updateCount(key);

		} catch (Exception e) {
			logger.error("下单监控异常",e);
			return false;
		}
	}
	
	/**
	 * @描述:监控通知
	 * @时间:2018年6月1日 下午3:42:23
	 */
    private boolean monitorNotice(String key, int num){
		try {
			String msg = "核心交易警告:当前商户号"+key+"连续超过"+num+"笔未成功支付";
			if(StringUtil.isEmpty(key)){
				return false;
			}
			if("market".equals(key)){
				 msg = "交易系统连续超过"+num+"笔未成功支付";
			}
			boolean flag = asynNotice.asynWxMsgNotice("交易报警",msg);
			if(flag){
				removeCount(key);
			}
			return flag;
		} catch (Exception e) {
			logger.error("监控通知异常",e);
			return false;
		}
	}

	/** 更新计数*/
	private boolean updateCount(String key){
		int precount = getCount(key);
		removeCount(key);
		return redisCacheApi.set(ORDER_MONITOR_KEY + key, precount + 1);
	}
	/** 获取计数*/
	private int getCount(String key){
		if(redisCacheApi.exists (ORDER_MONITOR_KEY + key)){
			return (int)redisCacheApi.get(ORDER_MONITOR_KEY + key);
		}
		return 0;
	}
	/** 删除计数*/
	private boolean removeCount(String key){
		return redisCacheApi.remove(ORDER_MONITOR_KEY + key);
	}
}

