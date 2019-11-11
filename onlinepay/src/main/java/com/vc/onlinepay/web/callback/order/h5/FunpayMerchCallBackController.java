package com.vc.onlinepay.web.callback.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.order.h5.FunPayMerchH5ServiceImpl;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/funpMerchH5CallBackApi")
public class FunpayMerchCallBackController extends BaseController {

    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    @Autowired
    private FunPayMerchH5ServiceImpl funPayMerchH5Service;
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;

    /**
     * @描述:支付宝企业账号回调接口
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
            logger.error("支付宝企业账号回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:执行支付宝企业账号回调接口
     * @时间:2018年1月5日 上午10:08:05
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        Map<String, String> requestMap = new HashMap<String, String>();
        try {
            requestMap = HttpRequestTools.getRequest(request);
            logger.info("支付宝企业账号回调接口接收参数:{}", requestMap);

            if (requestMap == null || requestMap.isEmpty()){
                logger.error("支付宝企业账号回调接口获取参数为空");
                return "failed";
            }
            String vcOrderNo = requestMap.get("out_trade_no");
            String pOrderNo = requestMap.get("trade_no");
            if (StringUtil.isEmpty(vcOrderNo)) {
                logger.error("支付宝企业账号回调接口订单号为空{}", vcOrderNo);
                return "failed";
            }
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(vcOnlineOrder == null){
            	logger.error("订单未找到{}",requestMap);
            	return "failed";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            if(!checkSign(requestMap,key)){
                logger.error("回调接口验签失败:{}", vcOrderNo);
                return "failed";
            }
            int status = 6 ;
            String tradStatus = requestMap.get("trade_status");
            if(StringUtil.isNotEmpty(tradStatus)){
            	if("TRADE_SUCCESS".equals(tradStatus)){
            		status = 4 ;
            	}else{
                    status = 1 ;
                }
            }else{
                status = 3;
            }
            logger.info("企业支付宝回调pOrderNo:{}",pOrderNo);
            if(StringUtil.isNotEmpty(pOrderNo)){
            	vcOnlineOrder.setpOrder(pOrderNo);
            }
            logger.info("订单查询响应{},{}",requestMap,status);
            boolean isOk = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk) {
            	logger.info("支付宝企业账号回调接口成功");
                return "success";
            } else {
                logger.error("支付宝企业账号回调接口更新失败{}",requestMap);
                return "failed";
            }
        } catch (Exception e) {
            logger.error("支付宝企业账号回调接口业务处理异常", e);
            return "failed";
        }
    }

    private boolean checkSign(Map<String, String> requestMap, String key) {
        //// TODO: 2018/10/27
        return true;
    }

    /**
     * 预授权结果通知
     * @param request
     * @param response
     */
    @RequestMapping(value = "/authFreeze", produces = "text/html;charset=UTF-8")
    public void authFreeze(HttpServletRequest request, HttpServletResponse response){
        setHttpServletContent(request, response);
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("支付宝预授权结果接口接收参数:{}", requestJson);
            if(null == requestJson ||requestJson.isEmpty()){
                logger.error("支付宝预授权结果接口获取参数为空");
                response.getWriter().write("failed");return;
            }
            String vcOrderNo = requestJson.containsKey("out_order_no")?requestJson.getString("out_order_no"):"";
            String authNo = requestJson.containsKey("auth_no")?requestJson.getString("auth_no"):"";
            String payerUser = requestJson.containsKey("payer_user_id")?requestJson.getString("payer_user_id"):"";
            String payeeUser = requestJson.containsKey("payee_user_id")?requestJson.getString("payee_user_id"):"";
            String status = requestJson.containsKey("status")?requestJson.getString("status"):"";
            String notifyId = requestJson.containsKey("notify_id")?requestJson.getString("notify_id"):"";

            if(StringUtils.isAnyEmpty(vcOrderNo,authNo,payeeUser,payerUser,status,notifyId) || !"SUCCESS".equalsIgnoreCase(status)){
                logger.error("支付宝预授权结果接口获取单号为空:{}",requestJson);
                response.getWriter().write("failed");return;
            }
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(vcOnlineOrder == null){
                logger.error("预授权结果订单未找到{}",requestJson);
                response.getWriter().write("failed");return;
            }
//            vcOnlineOrder.setpOrder(authNo);
            JSONObject callbackJson = new JSONObject();
            callbackJson.put("authNo",authNo);
            callbackJson.put("alipayAuthFreeze",true);
            callbackJson.put("payerUser",payerUser);
            callbackJson.put("payeeUser",payeeUser);
            callbackJson.put("amount",requestJson.getString("total_freeze_amount"));
            //回调订单成功
            boolean isOk = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, 4,callbackJson.toString());
            //授权冻结转支付
            vcOnlineOrder.setpAllRes(callbackJson.toString());
            boolean trade = funPayMerchH5Service.alipayTradePay(vcOnlineOrder,0);
            if (isOk) {
                logger.info("支付宝预授权回调接口成功:{},转支付:{}",vcOrderNo,trade);
                response.getWriter().write("success");return;
            } else {
                logger.error("支付宝预授权回调接口:{},更新失败:{}",vcOrderNo,callbackJson,trade);
                response.getWriter().write("failed");return;
            }
        } catch (Exception e) {
            logger.error("支付宝预授权回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * 订单授权成功，手动转支付
     * @param request
     * @param response
     */
    @RequestMapping(value = "/reDoTradePay", produces = "text/html;charset=UTF-8")
    public void reDoTradePay(HttpServletRequest request, HttpServletResponse response){
        setHttpServletContent(request, response);
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("预授权转支付接收参数:{}", requestJson);
            if(null == requestJson ||requestJson.isEmpty()){
                logger.error("预授权转支付获取参数为空");
                response.getWriter().write("failed");return;
            }
            String vcOrderNo = requestJson.containsKey("orderNo")?requestJson.getString("orderNo"):"";
            VcOnlineOrder onlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(null ==onlineOrder){
                logger.error("预授权转支付订单未找到:{}",vcOrderNo);
                response.getWriter().write("failed");return;
            }
            boolean trade = funPayMerchH5Service.alipayTradePay(onlineOrder,0);
            if (trade) {
                logger.info("预授权转支付申请成功:{}",vcOrderNo);
                response.getWriter().write("success");return;
            } else {
                logger.info("预授权转支付申请失败:{}",vcOrderNo);
                response.getWriter().write("failed");return;
            }
        } catch (Exception e) {
            logger.error("预授权转支付回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * 预授权转支付结果通知
     * @param request
     * @param response
     */
    @RequestMapping(value = "/tradePay", produces = "text/html;charset=UTF-8")
    public void tradePay(HttpServletRequest request, HttpServletResponse response){
        setHttpServletContent(request, response);
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("预授权转支付结果接口接收参数:{}", requestJson);
            if(null == requestJson ||requestJson.isEmpty()){
                logger.error("预授权转支付结果接口获取参数为空");
                response.getWriter().write("failed");return;
            }
            String vcOrderNo = requestJson.containsKey("out_order_no")?requestJson.getString("out_order_no"):"";
            String tradeNo = requestJson.containsKey("trade_no")?requestJson.getString("trade_no"):"";
            String status = requestJson.containsKey("trade_status")?requestJson.getString("trade_status"):"";
            String notifyId = requestJson.containsKey("notify_id")?requestJson.getString("notify_id"):"";

            if(StringUtils.isAnyEmpty(vcOrderNo,tradeNo,status,notifyId) || !"TRADE_SUCCESS".equalsIgnoreCase(status)){
                logger.error("预授权转支付结果接口获取单号为空:{}",requestJson);
                response.getWriter().write("failed");return;
            }
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(vcOnlineOrder == null){
                logger.error("预授权转支付订单未找到{}",requestJson);
                response.getWriter().write("failed");return;
            }
            vcOnlineOrder.setpOrder(tradeNo);
            boolean isOk = commonCallBackServiceImpl.asynOrderProfitPoolEngineProvider (vcOnlineOrder,vcOnlineOrder.getOrderNo());
            logger.info("预授权转支付成功:{},开始自动分润:{}", vcOnlineOrder.getOrderNo(),isOk);
            response.getWriter().write("success");
        } catch (Exception e) {
            logger.error("预授权转支付回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * 结算归集结果通知接口，只有成功状态才通知
     * @param request
     * @param response
     */
    @RequestMapping(value = "/settle")
    public void callback(HttpServletRequest request, HttpServletResponse response) {
        try {
            setHttpServletContent(request, response);
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("结算归集通知接收参数:{}", requestJson);
            if(null == requestJson || requestJson.isEmpty()){
                logger.error("结算归集通知接收参数为空");
                response.getWriter().write("FAILED");return;
            }
            String vcOrderNo = requestJson.containsKey("out_trade_no")?requestJson.getString("out_trade_no"):"";
            String tradeNo = requestJson.containsKey("trade_no")?requestJson.getString("trade_no"):"";
            String status = requestJson.containsKey("trade_status")?requestJson.getString("trade_status"):"";
            String notifyId = requestJson.containsKey("notify_id")?requestJson.getString("notify_id"):"";

            if(StringUtils.isAnyEmpty(vcOrderNo,tradeNo,status,notifyId) || !"TRADE_SUCCESS".equalsIgnoreCase(status)){
                logger.error("结算归集通知接收单号为空:{}",requestJson);
                response.getWriter().write("FAILED");return;
            }
            boolean flag = coreEngineProviderService.alipayOrderSettleListener(vcOrderNo,"","",false);
            if(flag){
                logger.error("结算归集通知处理成功:{}",requestJson);
                response.getWriter().write("SUCCESS");return;
            }
            logger.error("结算归集通知处理失败:{}",requestJson);
            response.getWriter().write("FAILED");
        } catch (Exception e) {
            logger.error("结算归集通知处理异常", e);
            super.writeErrorResponse(response);
        }
    }


}
