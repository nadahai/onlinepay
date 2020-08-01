package com.vc.onlinepay.web.callback.order.h5;

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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/heZongPayCallBackApi")
public class HeZongPayCallBackController extends BaseController {

    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    @Autowired
    private CommonPayService commonPayService;
    
    @Autowired
    private NotifyServiceImpl notifyService;

    /**
     * @描述:合纵支付回调接口
     * @2020年8月1日18:33:37
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
            logger.error("合纵支付回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        try {
        	JSONObject requestMap = HttpRequestTools.getRequestJson(request);
            logger.info("合纵支付回调接口接收参数:{}", requestMap);

            if (requestMap == null || requestMap.isEmpty()){
                logger.error("合纵支付回调接口获取参数为空");
                return "failed";
            }
            String vcOrderNo = requestMap.getString("orderId");
            if (StringUtil.isEmpty(vcOrderNo)) {
                logger.error("合纵支付回调接口订单号为空{}", vcOrderNo);
                return "failed";
            }
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(null == vcOnlineOrder){
                logger.error("回调接口订单不存在:{}",vcOrderNo);
                return "failed";
            }
            //回调金额判断
            Double traAmount = vcOnlineOrder.getTraAmount().doubleValue()*100;
            Double callBackAmount = Double.valueOf(requestMap.getString("amount"));//回调返回交易金额
            if(Math.abs(traAmount-callBackAmount)>200){
            	logger.error("合纵支付回调接口金额浮动超过2元:{}", vcOrderNo);
                return "failed";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            if(!checkSign(vcOrderNo,requestMap, StringUtils.deleteWhitespace(key))){
                logger.error("合纵支付回调接口验签失败:{}", vcOrderNo);
                return "failed";
            }
            String checkIpStatus  = notifyService.checkIpAddressForTrade(vcOnlineOrder, request);
            if("error".equals(checkIpStatus)){
            	logger.error("合纵支付回调接口,回调ip校验失败:{}", vcOrderNo);
            	return "failed";
            }
            boolean checkNotifyDateStatus  = notifyService.checkNotifyDate(vcOnlineOrder);
            if(!checkNotifyDateStatus){
            	logger.error("合纵支付回调接口,回调时间校验失败:{}", vcOrderNo);
            	return "failed";
            }
            String returncode = requestMap.getString("payStatus");
            returncode = StringUtils.isBlank(returncode)?"":returncode;
            int status = 1 ;
            if ("1".equals(returncode)) {
                status = 4;
            } else{
                status = 1;
            }
            boolean isOk = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk) {
            	return "SUCCESS";
            } else {
                logger.error("合纵支付回调接口更新失败{}", requestMap);
                return "FAIL";
            }
        } catch (Exception e) {
            logger.error("合纵支付回调接口业务处理异常", e);
            return "FAIL";
        }
    }

    private boolean checkSign(String orderNo, JSONObject reqData, String md5Key) {
        try {
            String pSign = reqData.getString("sign");
            //验签参数
            reqData.remove("sign");
            String signStr = "orderId="+reqData.getString("orderId")+"&"+
                    "payTime="+reqData.getString("payTime")+"&"+
                    "payStatus="+reqData.getString("payStatus")+"&"+
                    "amount="+reqData.getString("amount")+"&"+
                    "secreyKey="+md5Key;
            logger.info("合纵支付回调加密前:{}"+signStr);
            String sign = Md5Util.MD5(signStr);
            if (sign.equalsIgnoreCase(pSign)) {
                return true;
            }
            logger.error("合纵支付订单回调验签失败:{},sign原串{}", orderNo, reqData);
            CompletableFuture.runAsync(() -> commonPayService.saveLog("合纵支付回调验签失败", orderNo, null, reqData.toString(), pSign));
            asynNotice.asyncMsgNotice(new MessageModel(MessageTypeEnum.WINDOW, "合纵支付回调验签失败!", orderNo));
        } catch (Exception e) {
            logger.error("合纵支付订单回调验签异常:{}", orderNo, e);
            CompletableFuture.runAsync(() -> commonPayService.saveLog("合纵支付回调验签失败", orderNo, null, reqData.toString(), reqData.getString("sign")));
            asynNotice.asyncMsgNotice(new MessageModel(MessageTypeEnum.WINDOW, "合纵支付回调验签失败!", orderNo));
            return false;
        }
        return false;
    }

}
