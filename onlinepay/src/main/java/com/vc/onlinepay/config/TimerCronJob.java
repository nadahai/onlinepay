/**
 * @类名称:OnlinePayJob.java
 * @时间:2017年11月14日下午12:06:46
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.config;

import com.vc.onlinepay.persistent.service.channel.ChannelSubNoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @描述:定时任务
 * @时间:2017年11月14日 下午12:06:46
 */
@Component
@Lazy(false)
public class TimerCronJob {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ChannelSubNoServiceImpl channelSubNoService;

    /**
     * @描述:定时更新子账号交易额
     * @时间:2018/10/16 10:16
     */
    @Scheduled(cron = "58 59 23 * * ?")
    public void autoSubnoBalanceTotal(){
        try {
            Integer result = channelSubNoService.cleanDayTradeAmount(null);
            logger.info("定时更新子账号交易额任务结果:{}",result);
        } catch (Exception e) {
            logger.error("定时更新子账号交易额任务异常", e);
        }
    }

    /**
     * @描述:定时更新的订单（3分钟/次）
     * @时间:2019年4月26日21:54:00
     */
    //@Scheduled(cron = "0 */1 * * * ?")
    /*public void taobaocleanOrderTrade(){
        try {
            logger.info("定时更新的订单{}");
        } catch (Exception e) {
            logger.error("定时回调，异步通知下游失败的订单", e);
        }
    }*/
}
