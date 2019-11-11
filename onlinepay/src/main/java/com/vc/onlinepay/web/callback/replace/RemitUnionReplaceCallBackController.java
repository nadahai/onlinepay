package com.vc.onlinepay.web.callback.replace;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ReplaceServiceImpl;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @类名称:RemitUnionReplaceCallBackApi.java
 * @时间:2018年1月4日下午8:07:35
 * @作者:Alan
 * @版权:公司 Copyright (c) 2018
 */
@Controller
@RestController
@RequestMapping("/replaceCallbackApi/remittUnion")
public class RemitUnionReplaceCallBackController extends BaseController {
    
	 @Autowired
     private ReplaceServiceImpl commonCallBackServiceImpl;

    @Autowired
    private VcOnlinePaymentServiceImpl vcOnlinePaymentService;

    /**
     * @描述:汇付宝代付回调接口
     * @作者:Alan
     * @时间:2018年1月4日 下午8:04:18
     */
    @Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request, HttpServletResponse response)  {
        setHttpServletContent(request, response);
        try {
            String result = this.invokeCallback(request);
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("汇付宝代付回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:汇付宝代付回调接口
     * @作者:Alan
     * @时间:2018年1月5日 上午10:08:05
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        try {
            Map<String, String> requestMap = HttpRequestTools.getRequest(request);
            logger.info("汇付宝代付回调接收参数:{}", requestMap);
            if (requestMap == null || requestMap.isEmpty()){
                logger.error("汇付宝代付回调参数为空");
                return "ERROR";
            }
            String vcOrderNo = requestMap.get("merchantBatchNo");
            String transferDetails = requestMap.get("transferDetails")
                .replace("[","").replace("]","");

            if (StringUtil.isEmpty(vcOrderNo) || StringUtil.isEmpty(transferDetails)) {
                logger.error("汇付宝代付回调接口订单号为空{}", vcOrderNo);
                return "ERROR";
            }
            VcOnlinePayment onlinePayment = vcOnlinePaymentService.findVcOnlinePaymentByOrderNo(vcOrderNo);
            if(null == onlinePayment){
                logger.error("汇付宝代付回调接口订单未找到{}", vcOrderNo);
                return "ERROR";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(onlinePayment.getpSign());
            if(!checkSign(requestMap,key)){
                logger.error("回调接口验签失败:{}", vcOrderNo);
                return "ERROR";
            }
            JSONObject orderjson = JSONObject.parseObject(transferDetails);
            String status = orderjson.getString("status");

            JSONObject reqData = new JSONObject();
            reqData.put("orderNo",vcOrderNo);
            if ("1000".equals(status)) {
                reqData.put("code", Constant.SUCCESSS);
                reqData.put("msg", "代付回调成功");
                JSONObject result =  commonCallBackServiceImpl.callBackPayment(vcOrderNo,1, reqData);
                if(result!=null && result.get("code").equals(Constant.SUCCESSS)){
                    return "OK";
                }
                return "ERROR";
            }
            if("1002".equals(status) || "3001".equals(status)){
                reqData.put("code", Constant.FAILED);
                reqData.put("msg", "代付回调失败");
                JSONObject result =  commonCallBackServiceImpl.callBackPayment(vcOrderNo,3, reqData);
                if(result!=null && result.get("code").equals(Constant.SUCCESSS)){
                    return "OK";
                }
                return "ERROR";
            }
            if("2001".equals(status) && orderjson.containsKey("subStatus")){
                String subStatus = orderjson.getString("subStatus");
                if("20012".equals(subStatus) || "20013".equals(subStatus)){
                    reqData.put("code", Constant.FAILED);
                    reqData.put("msg", "代付回调失败");
                    JSONObject result =  commonCallBackServiceImpl.callBackPayment(vcOrderNo,3, reqData);
                    if(result!=null && result.get("code").equals(Constant.SUCCESSS)){
                        return "OK";
                    }
                    return "ERROR";
                }
            }
            logger.info("汇付宝代付回调未处理",requestMap);
            return "ERROR";
        } catch (Exception e) {
            logger.error("汇付宝代付回调接口处理异常", e);
            return "ERROR";
        }
    }

    private boolean checkSign(Map<String, String> requestMap,String key){
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("hyBatchNo=").append(requestMap.get("hyBatchNo"))
                    .append("&merchantBatchNo=").append(requestMap.get("merchantBatchNo"))
                    .append("&merchantId=").append(requestMap.get("merchantId"))
                    .append("&successAmount=").append(requestMap.get("successAmount"))
                    .append("&successNum=").append(requestMap.get("successNum"))
                    .append("&transferDetails=").append(requestMap.get("transferDetails"))
                    .append("&key=").append(key);
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
