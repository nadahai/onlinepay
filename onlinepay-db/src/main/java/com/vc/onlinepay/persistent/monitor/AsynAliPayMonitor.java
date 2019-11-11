/**
 * @类名称:AsynMonitorNotice.java
 * @时间:2018年6月1日下午3:12:25
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.persistent.monitor;

import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.service.channel.MerchChannelServiceImpl;
import com.vc.onlinepay.utils.StringUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:支付宝下单监控
 * @时间:2018年6月1日 下午3:12:25
 */
@Service
@Component
public class AsynAliPayMonitor {

	public static final Logger logger = LoggerFactory.getLogger(AsynAliPayMonitor.class);
    @Autowired
    private CoreEngineProviderService coreEngineProviderServiceImpl;
    @Autowired
    private MerchChannelServiceImpl merchChannelServiceImpl;
    @Autowired
    private AsynNotice asynNotice;

	private static Map<String, Integer> monitorMap = new ConcurrentHashMap<>();


	/**
	 * @描述:支付宝下单监控
	 * @时间:2018年6月1日 下午3:33:23
	 */
	public boolean orderMonitor(String key,String supplierNo, int status) {
		try {
		    String isOff = coreEngineProviderServiceImpl.getCacheCfgKey(CacheConstants.ONLINE_SWITCH_MONITOR_AUTO);
            logger.info ("支付宝下单监控:{},渠道:{},状态:{},{}",key,supplierNo,status,isOff);
            /*if (!"true".equals(isOff)) {
                return true;
            }*/
            if(StringUtil.isEmpty (key)){
                return true;
            }
            if(monitorMap == null || monitorMap.isEmpty()){
                monitorMap = new ConcurrentHashMap<>();
            }
            if(!monitorMap.containsKey(key) || monitorMap.get(key) == null || monitorMap.get(key) == 0){
                monitorMap.put(key, 1);
                return true;
            }
            int num = coreEngineProviderServiceImpl.getIntCacheCfgKey("online.monitor.alipay.failed.order");
            if (monitorMap.get(key) >= 0 && monitorMap.get(key) < num && status == 2) {
                monitorMap.put(key, monitorMap.get(key) + 1);
            }
            if (monitorMap.get(key) >= num) {
                boolean isOK = this.monitorNotice(key, monitorMap.get(key),supplierNo);
                monitorMap.put(key, 0);
                return isOK;
            }
            return true;
		} catch (Exception e) {
			logger.error("下单监控异常", e);
			return false;
		}
	}

	/**
	 * @描述:监控通知
	 * @时间:2018年6月1日
	 */
    private boolean monitorNotice(String key, int num, String supplierNo) {
		try {
		    StringBuffer msg = new StringBuffer();
            msg.append ("交易报警:");
            if(StringUtil.isNotEmpty (supplierNo)){
                msg.append ("渠道:").append (supplierNo);
            }
            msg.append ("支付宝:").append (key);
            msg.append ("失败").append (num).append ("次");
			String isDeleteNo = coreEngineProviderServiceImpl.getCacheCfgKey("online.monitor.alipay.failed.delete.no");
			logger.info ("监控通知删除账户:{},{}",key,isDeleteNo);
			if ("true".equals(isDeleteNo)) {
                msg.append ("已踢除");
				merchChannelServiceImpl.updateSubNoStatus(key, supplierNo,num+"笔失败踢除");
			}
			monitorMap.put(key, 0);
			return asynNotice.asynWxMsgNotice("支付宝报警", msg.toString ());
		} catch (Exception e) {
			logger.error("监控通知异常", e);
			return false;
		}
	}
}
