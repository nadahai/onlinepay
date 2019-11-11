package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jauyang
 * @描述:收钱吧支付宝回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/shouQianBaPayCallbackApi")
public class ShouQianBaCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    
    /**
     * @描述:收钱吧回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    @Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request, HttpServletResponse response)  {
        setHttpServletContent(request, response);
        try {
            response.setContentType("text/html");
            String result = this.invokeCallback(request);
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("收钱吧回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:收钱吧返回页面
     * @时间:2018年12月26日 下午15:24:18
     */
    @RequestMapping(value = "/returnWap", produces = "text/html;charset=UTF-8")
    public ModelAndView returnWap(HttpServletRequest request, HttpServletResponse response)  {
        response.setContentType ("text/html;charset=utf-8");
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("收钱吧返回页面响应:{}", requestJson);
            if(requestJson!=null && !requestJson.isEmpty () && requestJson.containsKey ("status") && requestJson.getString ("status").equalsIgnoreCase ("SUCCESS")){
                ModelAndView mode = new ModelAndView("success");
                mode.addObject("msg","支付成功");
                return mode;
            }else{
                logger.info("收钱吧支付失败响应:{}", requestJson);
                ModelAndView mode = new ModelAndView("failure");
                mode.addObject("msg","支付失败");
                String error_message = requestJson.containsKey ("error_message")?requestJson.getString ("error_message"):"";
                String is_success = requestJson.containsKey ("is_success")?requestJson.getString ("is_success"):"";
                if(StringUtil.isEmpty (error_message)){
                    mode.addObject("msg","取消支付");
                    return mode;
                }
                String vcOrderNo = requestJson.containsKey ("client_tsn")?requestJson.getString("client_tsn"):"";
                if(StringUtil.isEmpty (vcOrderNo)){
                    vcOrderNo = requestJson.containsKey ("client_sn")?requestJson.getString("client_sn"):"";
                }
                VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
                if (vcOnlineOrder !=null) {
                    Integer oldStatus = vcOnlineOrder.getStatus ();
                    if(oldStatus== 4 || oldStatus == 2 || oldStatus == 5){
                        mode.addObject("msg","取消支付");
                        return mode;
                    }
                    vcOnlineOrder.setStatus (5);
                    vcOnlineOrder.setOrderDes (error_message);
                    vcOnlineOrderService.updateOrderStatus(vcOnlineOrder);
                }
                mode.addObject("msg","支付失败"+error_message);
                return mode;
            }
        } catch (Exception e) {
            logger.error("收钱吧返回页面异常", e);
            super.writeErrorResponse(response);
            return null;
        }
    }

    /**
     * @描述:收钱吧回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("收钱吧回调接口接收参数:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("收钱吧回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestJson.containsKey ("client_tsn")?requestJson.getString("client_tsn"):"";
            if(StringUtil.isEmpty (vcOrderNo)){
                vcOrderNo = requestJson.containsKey ("client_sn")?requestJson.getString("client_sn"):"";
            }
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("收钱吧回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            Integer oldStatus = vcOnlineOrder.getStatus ();
            if( oldStatus== 4 || oldStatus == 2 || oldStatus == 5){
                logger.error("收钱吧订单已经成功:{}", vcOrderNo);
                return "success";
            }
            String payStatus = requestJson.getString ("status");
            int status =0;
            if ("SUCCESS".equalsIgnoreCase(payStatus)) {
                status = 4;
            }else if ("FAIL_CANCELED".equalsIgnoreCase(payStatus)) {
                status = 3;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "success";
            } else {
                logger.error("收钱吧回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("收钱吧回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        } 
    }
}
