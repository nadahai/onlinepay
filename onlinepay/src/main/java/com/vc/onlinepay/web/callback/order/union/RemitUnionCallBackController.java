package com.vc.onlinepay.web.callback.order.union;

import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/remitScanCallBackApi")
public class RemitUnionCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:汇付宝银联扫码回调接口
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
            logger.error("汇付宝银联扫码回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:执行汇付宝银联扫码回调接口
     * @作者:nada
     * @时间:2018年1月5日 上午10:08:05
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        Map<String, String> requestMap = new HashMap<>();
        try {
            requestMap = HttpRequestTools.getRequest(request);
            logger.info("汇付宝银联回调接口接收参数:{}", requestMap);

            if (requestMap == null || requestMap.isEmpty()){
                logger.error("汇付宝银联回调接口获取参数为空");
                return "ERROR";
            }
            String vcOrderNo = requestMap.get("merchantPreId");
            if (StringUtil.isEmpty(vcOrderNo)) {
                logger.error("汇付宝银联回调接口订单号为空{}", vcOrderNo);
                return "ERROR";
            }
            int status = 0 ;
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(null==vcOnlineOrder){
                logger.error("汇付宝回调接口订单号未找到{}", vcOrderNo);
                return "ERROR";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            if(!checkSign(requestMap,key)){
                logger.error("回调接口验签失败:{}", vcOrderNo);
                return "ERROR";
            }
            String porderNo = requestMap.get("transNo");
            if(vcOnlineOrder.getStatus()==4 && !porderNo.equals(vcOnlineOrder.getpOrder())){
                vcOnlineOrder.setpOrder(porderNo);
                vcOnlineOrder.setRemarks("重复支付");
                vcOnlineOrder.setTraType(1);
                payBusService.saveCopyOrder(vcOnlineOrder);
                return "ERROR";
            }
            vcOnlineOrder.setpOrder(porderNo);
            String payStatus = requestMap.get("result");
            if ("1000".equals(payStatus)) {
                status = 4;
            } else if ("1002".equals(payStatus)) {
                status = 5;
            } else {
                status = 6;
            }
            boolean isOk  = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk) {
                return "OK";
            } else {
                logger.error("汇付宝银联扫码回调结果{}更新失败{}",isOk,requestMap);
                return "ERROR";
            }
        } catch (Exception e) {
            logger.error("汇付宝银联扫码回调接口业务处理异常", e);
            return "ERROR";
        }
    }

    private boolean checkSign(Map<String, String> requestMap, String key) {
        try {
            String md5key = Constant.getChannelKeyDes(key,0);
            StringBuffer buffer = new StringBuffer();
            buffer.append("amount=").append(requestMap.get("amount"))
                    .append("&bankCardType=").append(requestMap.get("bankCardType"))
                    .append("&invoiceInfo=").append(requestMap.get("invoiceInfo"))
                    .append("&merchantId=").append(requestMap.get("merchantId"))
                    .append("&merchantPreId=").append(requestMap.get("merchantPreId"))
                    .append("&payerComments=").append(requestMap.get("payerComments"))
                    .append("&result=").append(requestMap.get("result"))
                    .append("&transNo=").append(requestMap.get("transNo"))
                    .append("&key=").append(md5key);
            String sign = Md5Util.md5(buffer.toString());
            if(sign.equals(requestMap.get("sign"))){
                return true;
            }
        } catch (Exception e) {
            logger.error("回调验签失败!:{}",e);
            return false;
        }
        return false;
    }
}
