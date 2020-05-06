package com.vc.onlinepay.web.callback.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.enums.MessageTypeEnum;
import com.vc.onlinepay.pay.common.NotifyServiceImpl;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.entity.merch.MessageModel;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;

/**
 * @描述:貔貅回调处理
 * @时间:2020年5月6日21:21:35
 */
@RestController
@RequestMapping("/piXiuCallBackController")
public class PiXiuCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;

    @Autowired
    private NotifyServiceImpl notifyService;

    @Autowired
    private CommonPayService commonPayService;
    
    @Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request, HttpServletResponse response)  {
        setHttpServletContent(request, response);
        try {
            response.setContentType("text/html");
            String result = this.invokeCallback(request);
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("貔貅回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    public String invokeCallback(HttpServletRequest request) {
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("貔貅回调接口接收参数:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("貔貅回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestJson.getString("out_trade_no");
            
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("貔貅回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            String checkIpStatus  = notifyService.checkIpAddressForTrade(vcOnlineOrder, request);
            if("error".equals(checkIpStatus)){
                logger.error("貔貅回调接口,回调ip校验失败:{}", vcOrderNo);
                return "ERROR";
            }
            boolean signStatus  = checkSign(vcOrderNo,requestJson,vcOnlineOrder.getUpMerchKey());
            if(!signStatus){
                logger.error("貔貅回调接口验签失败{}",vcOrderNo);
                return Constant.res_FAILED;
            }
            int status =0;
            String callbacks = requestJson.getString("callbacks");
            if ("CODE_SUCCESS".equals(callbacks)) {
                status = 4;
            }else if("CODE_FAILURE".equalsIgnoreCase(callbacks)){
                status = 5;
            }else {
                status = 1;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "success";
            } else {
                logger.error("貔貅回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("貔貅回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        } 
    }


    private boolean checkSign(String vcOrderNo, JSONObject jsonData, String key) {
        String upSign = jsonData.getString("sign");
        JSONObject signData = new JSONObject();
        try {
            jsonData.remove("sign");
            String sign = Md5CoreUtil.md5ascii(signData,key);
            if(sign.equalsIgnoreCase(upSign)){
                return true;
            }
            logger.error("貔貅回调接口验签失败:{}",vcOrderNo);
            CompletableFuture.runAsync(()-> commonPayService.saveLog("貔貅回调验签失败",vcOrderNo,null,signData.toString(),upSign));
            asynNotice.asyncMsgNotice(new MessageModel(MessageTypeEnum.WINDOW,"貔貅回调验签失败!",vcOrderNo));
        } catch (Exception e) {
            logger.error("貔貅回调验签异常:{}",vcOrderNo,e);
            CompletableFuture.runAsync(()-> commonPayService.saveLog("貔貅回调验签异常",vcOrderNo,null,signData.toString(),upSign));
            asynNotice.asyncMsgNotice(new MessageModel(MessageTypeEnum.WINDOW,"貔貅回调验签异常!",vcOrderNo));
            return false;
        }
        return false;
    }
}
