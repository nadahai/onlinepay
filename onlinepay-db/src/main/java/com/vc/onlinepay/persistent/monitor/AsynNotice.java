/**
 * @类名称:AsynNotice.java
 * @时间:2018年6月14日上午10:25:01
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.persistent.monitor;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:监控异步通知
 * @时间:2018年6月14日 上午10:25:01 
 */
@Service
@Component
public class AsynNotice {
	public static final Logger logger = LoggerFactory.getLogger(AsynNotice.class);
	@Autowired
	private CoreEngineProviderService coreEngineService;
	private static final String sendWxURL = "http://boss.mall51.top/cms/f/wechat/sendMsgApi";

	/**
	 * @描述:微信消息推送
	 * @时间:2018年6月1日 下午3:18:10
	 */
	public boolean asynWxMsgNotice(String title,String msg){
		try {
            String userids = coreEngineService.getCacheCfgKey(CacheConstants.ONLINE_MONITOR_NOTICE_WECHATID_LIST);
            if(StringUtil.isEmpty(userids)){
                return true;
            }
            JSONObject prms = new JSONObject();
            prms.put("type", "1");
            prms.put("user",userids);
            prms.put("title",title);
            prms.put("errorMsg",msg);
            prms.put("linkUrl","http://boss.toxpay.com/cms");
            logger.info("异步微信推送消息:{}",prms);
            ThreadUtil.execute(() -> {
               try {
                   String result = HttpClientTools.baseHttpSendPost(sendWxURL,prms);
                   logger.info("异步微信消息推送:{}",result);
               } catch (Exception e) {
                   logger.info("异步微信推送消息发送异常",prms);
               }
            });
            return true;
		} catch (Exception e) {
			logger.error("异步监控微信通知异常",e);
			return false;
		}
	}
}

