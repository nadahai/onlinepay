package com.vc.onlinepay.web.gate;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.gateutils.GateRequest;
import com.vc.onlinepay.utils.gateutils.GateResponse;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 订单交易自动通知（开发给下游）
 * @版权:公司 Copyright (c) 2017
 */
@Controller
@RestController
@RequestMapping("/orderNoticeApi")
public class NoticeGateController extends BaseController {
	
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;

    @Autowired
    private VcOnlinePaymentServiceImpl onlinePaymentService;

    @Autowired
    private VcOnlineOrderServiceImpl onlineOrderService;
	
	/**
	 * @描述:自动通知接口
	 * @作者:nada
	 * @时间:2017年12月27日 下午4:38:26
	 */
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public ModelAndView noticeApi(HttpServletRequest request, HttpServletResponse response){
    	setHttpServletContent(request, response);
        try {
    		JSONObject result = this.invoke(request);
            return GateResponse.writeResponse (response,result);
        } catch (Exception e) {
            logger.error("自动通知接口异常", e);
            return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("自动通知异常"));
        }
    }
    
    /**
     * @描述:自动通知接口业务处理
     * @作者:nada
     * @时间:2017年12月27日 下午4:25:18
     */
    public JSONObject invoke(HttpServletRequest request){
        try {
            JSONObject reqData = HttpRequestTools.getRequestJson(request);
            if(reqData == null || reqData.isEmpty()){
                return Constant.failedMsg("入参为空");
            }
            if(!reqData.containsKey("orderNo") || StringUtil.isEmpty(reqData.getString("orderNo"))){
                return Constant.failedMsg("订单号为空");
            }
            String orderId = reqData.getString("orderNo");
			VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(orderId);
    		if (vcOnlineOrder == null) {
                return Constant.failedMsg(orderId+"订单号不存在");
			}
    		 if(StringUtil.isEmpty(vcOnlineOrder.getCNotifyUrl())){
                 return Constant.failedMsg("通知地址为空");
             }
            boolean isOk = commonCallBackServiceImpl.asynOrderNotice(vcOnlineOrder, reqData.toString());
            logger.info("自动通知:{},结果:{}", reqData, isOk);
            return Constant.successMsg("通知"+isOk);
		}catch(Exception e){
			logger.error("自动通知异常", e);
			return Constant.failedMsg("通知系统异常");
		}
    }

    /**
     * @描述:手工补单交易订单
     * @时间:2020年2月6日17:03:38
     */
    @RequestMapping(value = "repairTradOrder", produces = "text/html;charset=UTF-8")
    public ModelAndView repairTradOrder(HttpServletRequest request, HttpServletResponse response){
        GateRequest.initHttpServletRequest (request, response);
        try {
            JSONObject reqData = HttpRequestTools.getRequestJson(request);
            if(reqData == null || reqData.isEmpty()){
                return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("入参为空"));
            }
            if(!reqData.containsKey("orderNo") || StringUtils.isEmpty(reqData.getString("orderNo"))){
                return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("订单号为空"));
            }
            String vcOrderNo = reqData.getString("orderNo");
            VcOnlineOrder vcOnlineOrder = onlineOrderService.findOrderByOrderNo(vcOrderNo);
            if(vcOnlineOrder == null){
                return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("订单信息为空"));
            }
            boolean isOk = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, 4,reqData.toString(),"手工补单成功");
            return GateResponse.writeResponse (response,Constant.successMsg("手工补单"+isOk));
        } catch (Exception e) {
            logger.error("自动通知接口异常", e);
            return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("自动通知异常"));
        }
    }

    /**
     * @描述:手工补单代付订单
     * @时间:2020年2月6日17:31:42
     */
    @RequestMapping(value = "repairTransOrder", produces = "text/html;charset=UTF-8")
    public ModelAndView repairTransOrder(HttpServletRequest request, HttpServletResponse response){
        GateRequest.initHttpServletRequest (request, response);
        try {
            JSONObject reqData = HttpRequestTools.getRequestJson(request);
            if(reqData == null || reqData.isEmpty()){
                return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("入参为空"));
            }
            if(!reqData.containsKey("orderNo") || StringUtils.isEmpty(reqData.getString("orderNo"))){
                return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("订单号为空"));
            }
            if(!reqData.containsKey("status") || StringUtils.isEmpty(reqData.getString("status"))){
                return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("订单号状态为空"));
            }
            Integer status = reqData.getInteger("status");
            if(status!=1 && status!=3){
                return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("订单号状态非法"));
            }
            String vcOrderNo = reqData.getString("orderNo");
            reqData.put("msg","线下出款调整");
            JSONObject result = commonCallBackServiceImpl.commonCallBackPayment(vcOrderNo,status,reqData);
            return GateResponse.writeResponse (response,result);
        } catch (Exception e) {
            logger.error("自动通知接口异常", e);
            return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("自动通知异常"));
        }
    }

}
