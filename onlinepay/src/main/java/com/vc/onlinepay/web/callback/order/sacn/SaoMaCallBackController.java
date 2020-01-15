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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nada
 * @描述:扫码回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/saoMaCallBackController")
public class SaoMaCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:扫码回调处理
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
            logger.error("扫码回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:扫码回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
    	try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("扫码回调接口接收参数2:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("扫码回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            
            JSONObject orderInfo = new JSONObject();
            orderInfo.put("uid", requestJson.getString("uid"));
            orderInfo.put("channel", requestJson.getString("channel"));
            orderInfo.put("tradeNo", requestJson.getString("tradeNo"));
            orderInfo.put("outTradeNo", requestJson.getString("outTradeNo"));
            orderInfo.put("money", requestJson.getString("money"));
            orderInfo.put("realMoney", requestJson.getString("realMoney"));
            orderInfo.put("token", "e6dfb1ba6e6f4bd097a2b16b4c98d176");
            
            String sourctxt1 = Md5CoreUtil.getSignStr(orderInfo);
	        logger.info("排序后{}",sourctxt1);
	        String ressign = Md5Util.md5(sourctxt1).toUpperCase();
	        
	        String sign = requestJson.getString("sign").toUpperCase();
            
            String vcOrderNo = requestJson.getString("outTradeNo");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("扫码回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            int status =0;
            String amout = null;
            if (sign.equals(ressign)) {
                status = 4;
   	         	amout = requestJson.getString("realMoney");
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder2(vcOnlineOrder, status,requestJson.toString(),amout);
            if (isOK) {
                return "SUCCESS";
            } else {
                logger.error("扫码回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("扫码回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        }
    }
}
