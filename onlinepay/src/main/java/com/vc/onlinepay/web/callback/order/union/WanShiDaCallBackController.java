package com.vc.onlinepay.web.callback.order.union;

import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
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
@RequestMapping("/wanShiDaPayCallBackApi")
public class WanShiDaCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:万事达银联扫码回调接口
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
            logger.error("万事达银联扫码回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:执行万事达银联扫码回调接口
     * @作者:nada
     * @时间:2018年1月5日 上午10:08:05
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        Map<String, String> requestMap = new HashMap<>();
        try {
            requestMap = HttpRequestTools.getRequest(request);
            logger.info("万事达银联回调接口接收参数:{}", requestMap);

            if (requestMap == null || requestMap.isEmpty()){
                logger.error("万事达银联回调接口获取参数为空");
                return "ERROR";
            }
            String vcOrderNo = requestMap.get("sn");
            if (StringUtil.isEmpty(vcOrderNo)) {
                logger.error("万事达银联回调接口订单号为空{}", vcOrderNo);
                return "ERROR";
            }
            int status = 0 ;
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(null==vcOnlineOrder){
                logger.error("万事达回调接口订单号未找到{}", vcOrderNo);
                return "ERROR";
            }
            vcOnlineOrder.setpOrder(vcOrderNo);
            String payStatus = requestMap.get("code");
            if ("200".equals(payStatus)) {
                status = 4;
            } else {
                status = 5;
            }
            boolean isOk  = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk) {
                return "SUCCESS";
            } else {
                logger.error("万事达银联扫码回调结果{}更新失败{}",isOk,requestMap);
                return "ERROR";
            }
        } catch (Exception e) {
            logger.error("万事达银联扫码回调接口业务处理异常", e);
            return "ERROR";
        }
    }
}
