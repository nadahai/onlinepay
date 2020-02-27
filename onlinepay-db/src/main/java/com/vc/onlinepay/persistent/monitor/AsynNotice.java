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
import com.vc.onlinepay.persistent.entity.merch.MessageModel;
import com.vc.onlinepay.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

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

    @Value("${cms.project.pushUrl:}")
	private String PRODUCT_EVN;

	private static final String sendWxURL = "http://boss.mall51.top/cms/f/wechat/sendMsgApi";
    //private static final String PRODUCT_EVN = "http://taobao.huashuo2020.com/cms/ws/pushAlert";

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

    /**
     * @desc 异步通知MessageType类消息到CMS
     * @author Hiutung
     * @create 2019/5/13 17:19
     * @param model
     * @return
     */
    public void asyncMsgNotice(final MessageModel model) {
        if(null == model) {
            return;
        }
        try {
            CompletableFuture.runAsync(() -> {
                logger.info("异步发送通知到DF管理端,接口:{}, 消息内容:{}",PRODUCT_EVN, model.toJSONString());
                try {
                    JSONObject sendJson = new JSONObject();
                    sendJson.put("pushTitle",model.getTitle());
                    sendJson.put("pushMsg",model.getMsg());
                    String response = HttpClientTools.httpSendPostForm(PRODUCT_EVN, sendJson);
                    logger.info("异步发送通知到DF管理端,接口:{}, 类型:{}, 返回:{}",PRODUCT_EVN, model.getType().getDesc(), response);
                } catch (Exception e) {
                    logger.error("异步发送通知到DF管理端异常",e);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            logger.error("异步发送通知到DF管理端异常", e);
        }
    }
}

