package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.enums.MessageTypeEnum;
import com.vc.onlinepay.pay.common.NotifyServiceImpl;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.entity.merch.MessageModel;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/jiuJiuPayCallBackApi")
public class JiuJiuScanCallBackController extends BaseController {

    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    @Autowired
    private CommonPayService commonPayService;
    
    @Autowired
    private NotifyServiceImpl notifyService;

    /**
     * @描述:久久支付回调接口
     * @时间:2020年6月5日17:18:05
     */
    @Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        setHttpServletContent(request, response);
        try {
            response.setContentType("text/html");
            String result = this.invokeCallback(request);
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("久久支付回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        try {
        	JSONObject requestMap = HttpRequestTools.getRequestJson(request);
            logger.info("久久支付回调接口接收参数:{}", requestMap);

            if (requestMap == null || requestMap.isEmpty()){
                logger.error("久久支付回调接口获取参数为空");
                return "failed";
            }
            String vcOrderNo = requestMap.getString("orderid");
            if (StringUtil.isEmpty(vcOrderNo)) {
                logger.error("久久支付回调接口订单号为空{}", vcOrderNo);
                return "failed";
            }
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(null == vcOnlineOrder){
                logger.error("回调接口订单不存在:{}",vcOrderNo);
                return "failed";
            }
            //回调金额判断
            Double traAmount = vcOnlineOrder.getTraAmount().doubleValue();
            Double callBackAmount = Double.valueOf(requestMap.getString("amount"));//回调返回交易金额
            if(Math.abs(traAmount-callBackAmount)>2){
            	logger.error("久久支付回调接口金额浮动超过2元:{}", vcOrderNo);
                return "failed";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            if(!checkSign(vcOrderNo,requestMap, StringUtils.deleteWhitespace(key))){
                logger.error("久久支付回调接口验签失败:{}", vcOrderNo);
                return "failed";
            }
            String checkIpStatus  = notifyService.checkIpAddressForTrade(vcOnlineOrder, request);
            if("error".equals(checkIpStatus)){
            	logger.error("久久支付回调接口,回调ip校验失败:{}", vcOrderNo);
            	return "failed";
            }
            boolean checkNotifyDateStatus  = notifyService.checkNotifyDate(vcOnlineOrder);
            if(!checkNotifyDateStatus){
            	logger.error("久久支付回调接口,回调时间校验失败:{}", vcOrderNo);
            	return "failed";
            }
            String returncode = requestMap.getString("returncode");
            returncode = StringUtils.isBlank(returncode)?"":returncode;
            int status = 1 ;
            if ("00".equals(returncode)) {
                status = 4;
            } else{
                status = 1;
            }
            boolean isOk = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk) {
            	return "OK";
            } else {
                logger.error("久久支付回调接口更新失败{}", requestMap);
                return "failed";
            }
        } catch (Exception e) {
            logger.error("久久支付回调接口业务处理异常", e);
            return "failed";
        }
    }

    private boolean checkSign(String orderNo, JSONObject reqData, String md5Key) {
        try {
        	String pSign = reqData.getString("sign");
            //验签参数
        	JSONObject signJson = new JSONObject();
        	signJson.put("amount", reqData.getString("amount"));
        	signJson.put("datetime", reqData.getString("datetime")); 
            signJson.put("memberid", reqData.getString("memberid"));  
            signJson.put("orderid", reqData.getString("orderid"));             
            signJson.put("transaction_id", reqData.getString("transaction_id")); 
            signJson.put("returncode", reqData.getString("returncode")); 
            String sign = Md5Util.MD5(getSignStr(signJson)+"&key="+md5Key);
            if(sign.equalsIgnoreCase(pSign)){
                return true;
            }
            logger.error("久久支付订单回调验签失败:{},sign原串{}",orderNo,reqData);
            CompletableFuture.runAsync(()-> commonPayService.saveLog("久久支付回调验签失败",orderNo,null,signJson.toString(),pSign));
            asynNotice.asyncMsgNotice(new MessageModel(MessageTypeEnum.WINDOW,"久久支付回调验签失败!",orderNo));
        } catch (Exception e) {
            logger.error("久久支付订单回调验签异常:{}",orderNo,e);
            CompletableFuture.runAsync(()-> commonPayService.saveLog("久久支付回调验签失败",orderNo,null,reqData.toString(),reqData.getString("sign")));
            asynNotice.asyncMsgNotice(new MessageModel(MessageTypeEnum.WINDOW,"久久支付回调验签失败!",orderNo));
            return false;
        }
        return false;
    }
    
    public String getSignStr(JSONObject reqData){
        String signStr = "";
        try {
        	 SortedMap<String, Object> sortedMap = new TreeMap<String, Object>();
             for (Object key : reqData.keySet()) {
                 sortedMap.put(key.toString(), reqData.get(key));
             }
        	
            StringBuilder buffer = new StringBuilder();
            for (String key : sortedMap.keySet()){
                if(!sortedMap.containsKey(key) || sortedMap.get(key) == null || "".equals(sortedMap.get(key))){
                    continue;
                }
                buffer.append(key).append("=").append(sortedMap.get(key).toString()).append("&");
            }
            signStr = buffer.toString();
            return signStr.substring(0,signStr.length()-1);
        } catch (Exception e) {
            logger.error("根据字母排序验签异常",e);
            return signStr;
        }
    }

}
