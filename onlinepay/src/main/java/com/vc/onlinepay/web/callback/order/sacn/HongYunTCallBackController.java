package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nada
 * @描述:鸿运通回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/hongYunTCallBackController")
public class HongYunTCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:鸿运通回调处理
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
            logger.error("花费充值回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:花费充值回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
    	try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("鸿运通回调接口接收参数2:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("鸿运通回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            
            JSONObject orderInfo = new JSONObject();
            orderInfo.put("memberid", requestJson.getString("memberid"));
            orderInfo.put("orderid", requestJson.getString("orderid"));
            orderInfo.put("amount", requestJson.getString("amount"));
            orderInfo.put("transaction_id", requestJson.getString("transaction_id"));
            orderInfo.put("datetime", requestJson.getString("datetime"));
            orderInfo.put("returncode", requestJson.getString("returncode"));
            
            String sign = Md5CoreUtil.md5ascii(orderInfo, "dovpztazvg5gubrfcv40bt0pqv5lleos").toUpperCase();
            String ressign = requestJson.getString("sign").toUpperCase();
            
            String vcOrderNo = requestJson.getString("orderid");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("花费充值回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            int status =0;
            if ("00".equals(requestJson.getString("returncode")) && sign.equals(ressign)) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "OK";
            } else {
                logger.error("鸿运通回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("鸿运通回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        }
    }
}
