/**
 * @类名称:AsynMonitorNotice.java
 * @时间:2018年6月1日下午3:12:25
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.persistent.monitor;

import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.service.channel.MerchChannelServiceImpl;
import com.vc.onlinepay.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @描述:异步监控处理
 * @时间:2018年6月1日 下午3:12:25
 */
@Service
@Component
public class AsynAliPayMerchMonitor {

	public static final Logger logger = LoggerFactory.getLogger(AsynAliPayMerchMonitor.class);

	private static Map<String, Integer> monitorMerchMap = new HashMap<String, Integer>();
	@Autowired
	private CoreEngineProviderService coreEngineProviderServiceImpl;
	@Autowired
	private AsynNotice asynNotice;
	@Autowired
	private MerchChannelServiceImpl merchChannelServiceImpl;

	/**
	 * @描述:自研企业支付宝分润监控
	 * @时间:2018年6月1日 下午3:33:23
	 */
	public boolean orderMonitor(String key,String supplierNo,boolean isClean) {
		try {
			logger.info ("分润监控信息{},{},{}",key,supplierNo,isClean);
			/*if (!"true".equals(coreEngineProviderServiceImpl.getCacheConfigKeyProvider(CacheConstants.ONLINE_SWITCH_MONITOR_AUTO))) {
				return true;
			}*/
			if(StringUtil.isEmpty(key)){
				return true;
			}
			if(monitorMerchMap == null || monitorMerchMap.isEmpty() || !monitorMerchMap.containsKey(key) || monitorMerchMap.get(key) == null){
				monitorMerchMap = new HashMap<String, Integer>();
				monitorMerchMap.put(key, 0);
			}
			if(isClean){
				monitorMerchMap.put(key, 1);
				return true;
			}
			int num = coreEngineProviderServiceImpl.getIntCacheCfgKey("online.monitor.merchalipay.failed.order");
			logger.info("支付宝分润监控key:{},getKey{},num:{}",key,monitorMerchMap.get(key),num);
			if (monitorMerchMap.get(key) >= 0 && monitorMerchMap.get(key) < num ) {
				monitorMerchMap.put(key, monitorMerchMap.get(key) + 1);
			}
			if (monitorMerchMap.get(key) >= num) {
				boolean isOK = monitorNotice(key, monitorMerchMap.get(key),supplierNo);
				monitorMerchMap.put(key, 0);
				return isOK;
			}
			return true;
		} catch (Exception e) {
			logger.error("分润监控异常", e);
			return false;
		}
	}

	/**
	 * @描述:监控通知
	 * @时间:2018年6月1日 下午3:42:23
	 */
    private boolean monitorNotice(String key, int num, String supplierNo) {
		try {
			logger.info ("监控通知{},supplierNo{}",key,supplierNo);
			if (StringUtil.isEmpty(key)) {
				return false;
			}
			String msg = "交易报警:渠道商:"+supplierNo+",企业支付宝:" + key + "连续超过" + num + "笔分润失败";
			String isDeleteNo = coreEngineProviderServiceImpl.getCacheCfgKey("online.monitor.merchalipay.failed.delete.no");
			if (StringUtil.isNotEmpty(isDeleteNo) && "true".equals(isDeleteNo)) {
				msg += ",已自动踢除";
				merchChannelServiceImpl.updateSubNoStatus(key, supplierNo,"连续" + num + "笔分润失败踢除");
			}
			monitorMerchMap.put(key,0);
			return asynNotice.asynWxMsgNotice("企业支付宝报警", msg);
		} catch (Exception e) {
			logger.error("监控通知异常", e);
			return false;
		}
	}

	public boolean monitorSettleNotice(String key,String supplierNo,String orderNo) {
		try {
			logger.info ("监控分润超时通知{},supplierNo{}",key,supplierNo);
			if (StringUtil.isEmpty(key)) {
				return false;
			}
			String msg = "交易报警:渠道商:"+supplierNo+",企业支付宝:" + key + "分润超时"+orderNo;
			String isDeleteNo = coreEngineProviderServiceImpl.getCacheCfgKey("online.monitor.merchalipay.failed.delete.no");
			if (StringUtil.isNotEmpty(isDeleteNo) && "true".equals(isDeleteNo)) {
				msg += ",已自动踢除";
				merchChannelServiceImpl.updateSubNoStatus(key, supplierNo,orderNo+"分润超时踢除");
			}
			monitorMerchMap.put(key,0);
			return asynNotice.asynWxMsgNotice("企业支付宝报警", msg);
		} catch (Exception e) {
			logger.error("监控通知异常", e);
			return false;
		}
	}
}
