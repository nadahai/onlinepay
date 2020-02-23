package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.enums.MessageTypeEnum;
import com.vc.onlinepay.pay.common.NotifyServiceImpl;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.entity.merch.MessageModel;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @类名称:MagicUnionCallBackApi.java
 * @时间:2018年1月4日下午8:07:35
 * @作者:nada
 * @版权:公司 Copyright (c) 2018
 */
@Controller
@RestController
@RequestMapping("/zuYongCallBackController")
public class ZuYongCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;

    @Autowired
    private NotifyServiceImpl notifyService;

    @Autowired
    private CommonPayService commonPayService;
    
    /**
     * @描述:租用回调接口
     * @作者:nada
     * @时间:2018年1月4日 下午8:04:18
     */
    @Override
    @RequestMapping(value = "")
    public void doPost(HttpServletRequest request, HttpServletResponse response)  {
        setHttpServletContent(request, response);
        try {
            response.setContentType("text/html");
            String result = this.invokeCallback(request);
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("租用回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:执行租用回调接口
     * @作者:nada
     * @时间:2018年1月5日 上午10:08:05
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        Map<String, String> requestMap = new HashMap<>();
        try {
            requestMap = HttpRequestTools.getRequest(request);
            logger.info("租用回调接口接收参数:{}", requestMap);

            if (requestMap == null || requestMap.isEmpty()){
                logger.error("租用回调接口获取参数为空");
                return "ERROR";
            }
            String vcOrderNo = requestMap.get("tradeNo");
            if (StringUtil.isEmpty(vcOrderNo)) {
                logger.error("租用回调接口订单号为空{}", vcOrderNo);
                return "ERROR";
            }
            int status = 0 ;
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(null==vcOnlineOrder){
                logger.error("租用回调接口订单号未找到{}", vcOrderNo);
                return "ERROR";
            }
            String checkIpStatus  = notifyService.checkIpAddressForTrade(vcOnlineOrder, request);
            if("error".equals(checkIpStatus)){
                logger.error("租用回调接口,回调ip校验失败:{}", vcOrderNo);
                //return "ERROR";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            if(!checkSign(vcOrderNo,requestMap, StringUtils.deleteWhitespace(key))){
                logger.error("租用回调接口验签失败:{}", vcOrderNo);
                //return "failed";
            }
            vcOnlineOrder.setpOrder(vcOrderNo);
            String payStatus = requestMap.get("status");
            if ("4".equals(payStatus)) {
                status = 4;
            } else if ("2".equals(payStatus) || "5".equals(payStatus)) {
                status = 5;
            } else {
                status = 3;
            }
            boolean isOk  = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk) {
                return "success";
            } else {
                logger.error("租用回调结果{}更新失败{}",isOk,requestMap);
                return "ERROR";
            }
        } catch (Exception e) {
            logger.error("租用回调接口业务处理异常", e);
            return "ERROR";
        }
    }

    private boolean checkSign(String vcOrderNo,Map<String,String> reqDataMap, String key) {
        String upSign = reqDataMap.get("sign");
        JSONObject signData = new JSONObject();
        try {
            for(String mapKey:reqDataMap.keySet()){
                if(mapKey.equals("sign")){
                    continue;
                }
                signData.put(mapKey,reqDataMap.get(mapKey));
            }
            String sign = Md5CoreUtil.md5ascii(signData,key);
            if(sign.equalsIgnoreCase(upSign)){
                return true;
            }
            logger.error("租用回调接口验签失败:{}",vcOrderNo);
            CompletableFuture.runAsync(()-> commonPayService.saveLog("租用回调验签失败",vcOrderNo,null,signData.toString(),upSign));
            asynNotice.asyncMsgNotice(new MessageModel(MessageTypeEnum.WINDOW,"回调验签失败!",vcOrderNo+"回调验签失败"));
        } catch (Exception e) {
            logger.error("租用回调验签异常:{}",vcOrderNo,e);
            CompletableFuture.runAsync(()-> commonPayService.saveLog("租用回调验签异常",vcOrderNo,null,signData.toString(),upSign));
            asynNotice.asyncMsgNotice(new MessageModel(MessageTypeEnum.WINDOW,"租用回调验签异常!",vcOrderNo+"回调验签异常"));
            return false;
        }
        return false;
    }
}
