package com.vc.onlinepay.web.callback.order.sacn;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.pay.order.scan.FLMScanServiceImpl;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author jauyang
 * @描述:付临门回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/flmCallbackApi")
public class FlmBaCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Autowired
    private FLMScanServiceImpl flmScanService;
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;


    /**
     * @描述:付临门回调接口
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
            logger.error("付临门回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:付临门回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("付临门回调接口接收参数:{}", requestJson);
            if (requestJson == null || requestJson.isEmpty()){
                logger.error("付临门回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            synchronized (requestJson) {
                ThreadUtil.execute(() -> {
                    try {
                        List<VcOnlineOrderMade> lists = vcOnlineOrderMadeService.findFlmOrders(null);
                        if(lists == null || lists.size()<1){
                            return;
                        }
                        for (VcOnlineOrderMade made : lists) {
                            String orderNo = made.getOrderNo();
                            JSONObject result = flmScanService.orderMadeQuery(made.getUpMerchNo(),made.getUpMerchKey(),made.getQrcodeUrl());
                            logger.info("付临门回调数据:{},result:{}",orderNo,result);
                            if(result == null || result.isEmpty()){
                                continue;
                            }
                            String code = result.containsKey("code")?result.getString("code"):"";
                            String msg = result.containsKey("msg")?result.getString("msg"):"";
                            String status = result.containsKey("status")?result.getString("status"):"";
                            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(orderNo);
                            if (vcOnlineOrder==null) {
                                logger.error("付临门回调接口订单号为空{}", orderNo);
                                continue;
                            }
                            Integer oldStatus = vcOnlineOrder.getStatus ();
                            if( oldStatus== 4 || oldStatus == 2 || oldStatus == 5){
                                logger.error("付临门订单已经成功:{}", orderNo);
                                continue;
                            }
                            String pOrderNo = result.containsKey("pOrderNo")?result.getString("pOrderNo"):"";
                            if(StringUtil.isNotEmpty(pOrderNo)){
                                vcOnlineOrder.setpOrder(pOrderNo);
                            }
                            
                            if("交易成功".equalsIgnoreCase(status)){
                                boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, 4,requestJson.toString(),status);
                                continue;
                            }else if(StringUtil.isEmpty(status)){
                                boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, 3,requestJson.toString(),msg);
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        logger.error("付临门回调接口业务处理异常", e);
                        e.printStackTrace();
                    }
                });
            }
            return  Constant.successMsg("申请已经提交").toString();
        } catch (Exception e) {
            logger.error("付临门回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        } 
    }
    
    
}
