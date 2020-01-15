package com.vc.onlinepay.web.callback.order.h5;

import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import java.net.URLDecoder;
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
 * @作者:Alan
 * @版权:公司 Copyright (c) 2018
 */
@Controller
@RestController
@RequestMapping("/remitth5callbackapi")
public class RemitH5CallBackController extends BaseController {

    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:汇付宝h5回调接口
     * @作者:Alan
     * @时间:2018年1月4日 下午8:04:18
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
            logger.error("汇付宝h5回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:执行汇付宝h5回调接口
     * @作者:nada
     * @时间:2018年1月5日 上午10:08:05
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        Map<String, String> requestMap = new HashMap<>();
        try {
            requestMap = HttpRequestTools.getRequest(request);
            logger.info("汇付宝h5回调接口接收参数:{}", requestMap);

            if (requestMap == null || requestMap.isEmpty()){
                logger.error("汇付宝h5回调接口获取参数为空");
                return "ERROR";
            }
            String vcOrderNo = requestMap.get("agent_bill_id");
            if (StringUtil.isEmpty(vcOrderNo)) {
                logger.error("汇付宝h5回调接口订单号为空{}", vcOrderNo);
                return "ERROR";
            }
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(vcOnlineOrder == null){
                logger.error("订单未找到{}",requestMap);
                return "ERROR";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            if(!checkSign(requestMap,key)){
                logger.error("回调接口验签失败:{}", vcOrderNo);
                return "ERROR";
            }
            String payStatus = requestMap.get("result");
            int status = 0 ;
            if ("1".equals(payStatus)) {
                status = 4;
            } else {
                status = 6;
            }
            boolean isOk = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk) {
                return "ok";
            } else {
                logger.error("汇付宝h5回调接口更新失败{}", requestMap);
                return "ERROR";
            }
        } catch (Exception e) {
            logger.error("汇付宝h5回调接口业务处理异常", e);
            return "ERROR";
        }
    }

    private boolean checkSign(Map<String, String> requestMap, String key) {
        try {
            String md5key = Constant.getChannelKeyDes(key,0);
            StringBuffer buffer = new StringBuffer();
            buffer.append("result=").append(requestMap.get("result"))
                    .append("&agent_id=").append(requestMap.get("agent_id"))
                    .append("&jnet_bill_no=").append(requestMap.get("jnet_bill_no"))
                    .append("&agent_bill_id=").append(requestMap.get("agent_bill_id"))
                    .append("&pay_type=").append(requestMap.get("pay_type"))
                    .append("&pay_amt=").append(requestMap.get("pay_amt"))
                    .append("&remark=").append(URLDecoder.decode(requestMap.get("remark"), "GBK"))
                    .append("&key=").append(md5key);
//            System.out.println("buffer = " + buffer.toString());
            String sign = Md5Util.md5(buffer.toString());
//            System.out.println("sign = " + sign);
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
