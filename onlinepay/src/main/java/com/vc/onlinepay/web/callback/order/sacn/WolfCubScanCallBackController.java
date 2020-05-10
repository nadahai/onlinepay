package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.enums.MessageTypeEnum;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.common.CommonPayService;
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
@RequestMapping("/wolfCubScanCallBackApi")
public class WolfCubScanCallBackController extends BaseController {
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    /**
     * @描述:小狼支付回调接口
     * @时间:2019年12月16日18:20:22
     */
    @Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        try {
            this.setHttpServletContent(request, response);
            response.setContentType("text/html");
            String result = this.invokeCallback(request);
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("小狼支付回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:执行小狼支付回调接口
     * @时间:2019年12月16日18:24:36
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        try {
            JSONObject requestMap = HttpRequestTools.getRequestJson(request);
            logger.info("小狼支付回调接口接收参数:{}", requestMap);

            if (requestMap == null || requestMap.isEmpty()){
                logger.error("小狼支付回调接口获取参数为空");
                return "failed";
            }
            String vcOrderNo = requestMap.getString("out_trade_no");
            if (StringUtil.isEmpty(vcOrderNo)) {
                logger.error("小狼支付回调接口订单号为空{}", vcOrderNo);
                return "failed";
            }
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(null == vcOnlineOrder){
                logger.error("回调接口订单不存在:{}",vcOrderNo);
                return "failed";
            }
            //回调金额判断
            Double traAmount = vcOnlineOrder.getTraAmount().doubleValue();
            Double callBackAmount = Double.valueOf(requestMap.getString("pay_money"));//回调返回交易金额
            if(Math.abs(traAmount-callBackAmount)>2){
            	logger.error("小狼支付回调接口金额浮动超过2元:{}", vcOrderNo);
                return "failed";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            /*if(!checkSign(vcOrderNo,requestMap,StringUtils.deleteWhitespace(key))){
                logger.error("小狼支付回调接口验签失败:{}", vcOrderNo);
                return "failed";
            }*/
            String payStatus = requestMap.getString("status");
            payStatus = StringUtils.isBlank(payStatus)?"":payStatus;
            int status = 1 ;
            if ("1".equals(payStatus)) {
                status = 4;
            } else{
                status = 1;
            }
            boolean isOk = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk) {
            	return "success";
            } else {
                logger.error("小狼支付回调接口更新失败{}", requestMap);
                return "failed";
            }
        } catch (Exception e) {
            logger.error("小狼支付回调接口业务处理异常", e);
            return "failed";
        }
    }
    /*private boolean checkSign(String orderNo,JSONObject reqData,String md5Key) {
        try {
        	String pSign = reqData.getString("sign");
            reqData.remove("sign");
            reqData.remove("sign_type");
            //验签参数
        	String sourceStr = Md5CoreUtil.getSignStr(reqData)+md5Key;
        	logger.info("加密前参数{}",sourceStr);
            String sign = Md5Util.md5(sourceStr);
            if(sign.equalsIgnoreCase(pSign)){
                return true;
            }
            logger.error("小狼支付订单回调验签失败:{},sign原串{}",orderNo,sourceStr);
            CompletableFuture.runAsync(()-> commonPayService.saveCallBackSignLog("小狼支付回调验签失败",orderNo,null,reqData.toJSONString(),sign));
            asynNotice.asyncMsgNotice(new MessageModel(MessageTypeEnum.WINDOW,"小狼支付订单回调验签失败!",orderNo));
        } catch (Exception e) {
            logger.error("小狼支付订单回调验签异常:{}",orderNo,e);
            CompletableFuture.runAsync(()-> commonPayService.saveCallBackSignLog("小狼支付回调验签异常",orderNo,null,reqData.toJSONString(),reqData.getString("sign")));
            asynNotice.asyncMsgNotice(new MessageModel(MessageTypeEnum.WINDOW,"小狼支付订单回调验签异常!",orderNo));
            return false;
        }
        return false;
    }*/
}
