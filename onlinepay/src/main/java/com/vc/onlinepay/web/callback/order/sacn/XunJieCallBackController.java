package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jauyang
 * @描述:迅捷支付宝回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/xunJieDaPayCallBackApi")
public class XunJieCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:迅捷回调接口
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
            logger.error("掌融回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:迅捷回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("迅捷ZFB回调接口接收参数:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("迅捷ZFB回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestJson.getString("m_order_code");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("迅捷ZFB回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            String order_status = requestJson.getString("order_status");
            String order_status_msg = requestJson.getString("order_status_msg");
            String sys_order_code = requestJson.getString("sys_order_code");
            String m_order_code = requestJson.getString("m_order_code");
            String total_fee = requestJson.getString("total_fee");
            String pay_time = requestJson.getString("pay_time");
            String passback_params = requestJson.getString("passback_params");
            String sign = requestJson.getString("sign");
            
            JSONObject prams = new JSONObject ();
            prams.put ("order_status", order_status);
            prams.put ("order_status_msg", order_status_msg);
            prams.put ("sys_order_code", sys_order_code);
            prams.put ("m_order_code", m_order_code);
            prams.put ("total_fee", total_fee);
            prams.put ("pay_time", pay_time);
            prams.put ("passback_params", passback_params);
            
            String reqSign =Md5Util.md5(Md5CoreUtil.getSignStr(prams)+"eIZn36ABM3qJP6GCOr4wkN3MU8BDxq0P").toUpperCase();
            
            int status =0;
            if (reqSign.equalsIgnoreCase(sign.toUpperCase()) && "2".equals(order_status)) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "success";
            } else {
                logger.error("迅捷ZFB回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("迅捷ZFB回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        } 
    }
}
