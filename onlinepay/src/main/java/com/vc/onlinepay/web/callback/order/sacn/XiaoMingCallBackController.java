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
 * @描述:小明支付宝回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/xiaomingPayCallbackApi")
public class XiaoMingCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:小明回调接口
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
     * @描述:小明回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("小明ZFB回调接口接收参数:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("小明ZFB回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestJson.getString("sn");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("小明ZFB回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            /*String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            if(!checkSign(requestJson,key)){
                logger.error("小明ZFB回调接口验签失败:{}", vcOrderNo);
                return Constant.res_FAILED;
            }*/
            String payStatus = requestJson.getString("code");
            int status =0;
            if ("200".equalsIgnoreCase(payStatus)) {
                status = 4;
            }else if ("300".equalsIgnoreCase(payStatus)) {
                status = 5;
            }else if ("400".equalsIgnoreCase(payStatus)) {
                status = 5;
            }else if ("500".equalsIgnoreCase(payStatus)) {
                status = 5;
            }else {
                status = 6;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return Constant.res_OK;
            } else {
                logger.error("小明ZFB回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("小明ZFB回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        } 
    }

    /**
     * @描述:小明ZFB回调验签
     * @作者:jauyang
     * @时间:2018/12/26
     **/
    private boolean checkSign(JSONObject requestJson, String key) {
        try {
            logger.info ("小明ZFB回调验签{},{}",requestJson,key);
            String psign = requestJson.getString("signData");
            requestJson.remove("signData");
            String signStr = Md5CoreUtil.getSignStr(requestJson) + "&key=" + key;
            String sign = Md5Util.md5(signStr).toUpperCase();
            logger.info("回调验签：{}，收到签名:{}",sign,psign);
            if(sign.equals(psign)){
                return true;
            }
        } catch (Exception e) {
            logger.error("小明ZFB接口验签异常:{}", requestJson);
        }
        return false;
    }
}
