package com.vc.onlinepay.web.callback.order.h5;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;

@Controller
@RestController
@RequestMapping("/huiyunH5PayCallBackApi")
public class HuiYunPayCallBackController extends BaseController {

    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;

    /**
     * @描述:惠云h5回调接口
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
            logger.error("惠云h5回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:执行惠云h5回调接口
     * @时间:2018年1月5日 上午10:08:05
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        Map<String, String> requestMap = new HashMap<>();
        try {
            requestMap = HttpRequestTools.getRequest(request);
            logger.info("惠云h5回调接口接收参数:{}", requestMap);

            if (requestMap == null || requestMap.isEmpty()){
                logger.error("惠云h5回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestMap.get("sdorderno");
            if (StringUtil.isEmpty(vcOrderNo)) {
                logger.error("惠云h5回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(null == vcOnlineOrder){
                logger.error("回调接口订单不存在:{}",vcOrderNo);
                return Constant.res_FAILED;
            }
            String payAmount = requestMap.get("total_fee");
            String orderAmount = String.valueOf(vcOnlineOrder.getTraAmount().setScale(2, BigDecimal.ROUND_HALF_DOWN));
            logger.error("回调接口订单:{},下单金额:{},支付金额:{}",vcOrderNo,orderAmount,payAmount);
            if(!payAmount.equals(orderAmount)){
                logger.error("回调接口订单:{},下单:{},支付金额不匹配:{}",vcOrderNo,orderAmount,payAmount);
                return Constant.res_FAILED;
            }
            String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            if(!checkSign(requestMap,key)){
                logger.error("回调接口验签失败:{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            String payStatus = requestMap.get("status");
            int status = 6 ;
            if ("1".equalsIgnoreCase(payStatus)) {
                status = 4;
            }
            boolean isOk = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk) {
                return Constant.RES_SUCCESS;
            } else {
                logger.error("惠云h5回调接口更新失败{}", requestMap);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("惠云h5回调接口业务处理异常", e);
            return Constant.res_FAILED;
        }
    }

    private boolean checkSign(Map<String, String> prams, String signKey) {
        try {
            String signStr = "customerid="+prams.get("customerid")+"&status="+prams.get("status")+"&" +
                    "sdpayno="+prams.get("sdpayno")+"&sdorderno="+prams.get("sdorderno")+"&total_fee="+prams.get("total_fee")+"&paytype="+prams.get("paytype")+"&"+signKey;
            String sign = Md5Util.md5(signStr);
            logger.info("回调验签signStr:{},sign:{},Psign:{}",signStr,sign,prams.get("sign"));
            if(sign.equals(prams.get("sign"))){
                return true;
            }
        } catch (Exception e) {
            logger.error("回调验签失败!:{}",e);
            return false;
        }
        return false;
    }
}
