package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jauyang
 * @描述:花费充值2回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/huFeiCallBackController")
public class HuFeiCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:花费充值2回调处理
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
            logger.error("花费充值2回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:花费充值2回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
    	try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("花费充值2回调接口接收参数2:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("花费充值2回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            
            JSONObject orderInfo = new JSONObject();
            orderInfo.put("agentAcct", requestJson.getString("agentAcct"));
            orderInfo.put("agentOrderId", requestJson.getString("agentOrderId"));
            orderInfo.put("status", requestJson.getString("status"));
            orderInfo.put("amount", requestJson.getString("amount"));
            orderInfo.put("payAmount", requestJson.getString("payAmount"));
            orderInfo.put("charge_amount", requestJson.getString("charge_amount"));
            orderInfo.put("pay_amount", requestJson.getString("pay_amount"));
            
            
            String sign = Md5CoreUtil.md5ascii(orderInfo, "25d55ad283aa400af464c76d713c07ad").toUpperCase();
            String ressign = requestJson.getString("sign").toUpperCase();
            
            String vcOrderNo = requestJson.getString("agentOrderId");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("花费充值2回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            int status =0;
            String amout = null;
            if ("S".equals(requestJson.getString("status")) && sign.equals(ressign)) {
                status = 4;
                int resamount = Integer.valueOf (requestJson.getString("payAmount"));
   	         	BigDecimal realAmount = new BigDecimal(resamount).divide(new BigDecimal(100));
   	         	realAmount.multiply(new BigDecimal(100));
   	         	//realAmount.setScale(2, BigDecimal.ROUND_DOWN);
   	         	amout = realAmount+"";
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder2(vcOnlineOrder, status,requestJson.toString(),amout);
            if (isOK) {
                return "SUCCESS";
            } else {
                logger.error("花费充值2回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("花费充值2回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        }
    }
}
