/**
 * @类名称:GateListner.java
 * @时间:2017年7月12日上午11:00:14
 * @作者:lihai
 * @版权:公司 Copyright (c) 2017
 */
package com.vc.onlinepay.pay.common;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @描述:同步阻塞处理Handler
 * @作者:lihai
 * @时间:2017年7月12日 上午11:00:14
 */
public abstract class ResultListener{

    public Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @描述:接口成功响应
     * @作者:lihai 
     * @时间:2017年12月14日 下午4:29:05
     */
    public abstract JSONObject successHandler(JSONObject resultData);
    
    /**
     * @描述:接口进行中响应
     * @作者:lihai
     * @时间:2017年12月14日 下午4:29:05
     */
    public abstract JSONObject paddingHandler(JSONObject resultData);
    
    /**
     * @描述:接口失败响应
     * @作者:lihai 
     * @时间:2017年12月14日 下午4:29:05
     */
    public abstract JSONObject failedHandler(JSONObject resultData);

}
