package com.vc.onlinepay.delay;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.vc.onlinepay.cache.RedisCacheApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;

/**
 * @author yang
 */
@Service
@Component
public class DelayTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelayTask.class);

    @Autowired
    protected RedisCacheApi redisCacheApi;

    private static DelayTask  delayTask ;

    @PostConstruct
    public void init() {
        delayTask = this;
    }

    /**
     * 时间单元：30s
     * 实际为纳秒单位
     * */
    private static final long TIME_UNIT = 30000000000L;

    private static DelayTask instance = new DelayTask();

    public static DelayTask getInstance() {
        return instance;
    }

    /**
     * DelayQueue队列没有大小限制，因此向队列插数据不会阻塞
     * DelayQueue中的元素只有当其指定的延迟时间到了，才能够从队列中获取到该元素。否则线程阻塞
     * */
    private static DelayQueue<DelayItem<Pair<String, RetMessage>>> queue = new DelayQueue<>();

    private DelayTask() {
        ThreadUtil.execute(this::execute);
    }

    private void execute() {
        for (;;) {
            try {
                DelayItem<Pair<String, RetMessage>> delayItem = queue.take();
                // 到期处理
                Pair<String, RetMessage> pair = delayItem.getItem();
                RetMessage msg = pair.getSecond();
                if (!msg.isSuccess() && msg.getTimes() <= 20 && delayTask.redisCacheApi.exists(pair.getFirst())) {
                    try {
                        String json ="{\"orderNo\":\""+pair.getFirst()+"\"}";
                        LOGGER.info("PDD订单查询：参数url{},data{}", msg.getUrl(),pair.getFirst());
                        String httpResult = HttpUtil.post(msg.getUrl(),json);
                        LOGGER.info("PDD订单查询：订单号：{}，第{}次,返回结果{}", pair.getFirst(),msg.getTimes(), httpResult);
                        if (delayTask.redisCacheApi.exists(pair.getFirst())) {
                            msg.setTimes(msg.getTimes() + 1);
                            msg.setSuccess(false);
                            DelayTask.getInstance().put(pair.getFirst(), msg);
                        }
                        queue.remove(delayItem);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.warn(e.getMessage(), e);
                        LOGGER.error("DelayTask任务执行异常", e);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                LOGGER.error("DelayTask功能执行异常", e);
                LOGGER.error(e.getMessage(), e);
                break;
            }
        }
    }

    /**
     * 添加通知对象
     *
     * @param key
     *            唯一性key值，建议为：orderNo
     * @param msg
     *            响应体
     */
    public void put(String key, RetMessage msg) {
        // 30s延迟一次
        queue.put(new DelayItem<>(new Pair<>(key, msg), TIME_UNIT));
    }

    public static void main(String[] args){
		/*
		 * String orderNo = "h50529153826647185"; DelayTask.getInstance().put(orderNo,
		 * new RetMessage()); String url = "https://api.pinduoduo.com/order/"+orderSn;
		 */
        //logger.info("PDD查询订单信息：{}，地址：{}",accessToken,url);
        String result = HttpRequest.post("https://api.pinduoduo.com/order/190529-119034675972278").header("accesstoken","5ZFSS2NTZO5MDFXKTMXWUL7RJ2UVVW3EQZTEFLQINTEK5KBSYWJQ100e62d").execute().body();
        System.out.println(result);
    }
}
