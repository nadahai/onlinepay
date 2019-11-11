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
 * @author jauyang
 * @描述:佩奇支付宝支付回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/peiQiCallBackController")
public class PeiQiCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:佩奇支付宝支付回调处理
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
            logger.error("佩奇支付宝回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:佩奇支付宝回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
    	try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("佩奇支付宝支付回调接口接收参数2:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("佩奇支付宝支付回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestJson.getString("mOrderId");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("佩奇支付宝支付回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            
            
            String body = requestJson.getString("body");
            String mOrderId = requestJson.getString("mOrderId");
            String merchantId = requestJson.getString("merchantId");
            String orderAmount = requestJson.getString("orderAmount");
            String pOrderId = requestJson.getString("pOrderId");
            String payAmount = requestJson.getString("payAmount");
            String payType = requestJson.getString("payType");
            String signType = requestJson.getString("signType");
            String version = requestJson.getString("version");
            
            JSONObject parms = new JSONObject();
            parms.put ("body",body);
            parms.put ("mOrderId",mOrderId);
            parms.put ("merchantId",merchantId);
            parms.put ("orderAmount", orderAmount);
            parms.put ("pOrderId", pOrderId);
            parms.put ("payAmount",payAmount);
            parms.put ("payType",payType);
            parms.put ("signType",signType);
            parms.put ("version",version);
            
            String keys = "893a37ea08a445288379e6e6136b31c0";
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+keys;
            logger.info("排序后{}",sourctxt1);
            String newSign = Md5Util.md5(sourctxt1).toUpperCase();
            
            String sign = requestJson.getString("sign").toUpperCase();
            
            logger.info("newSign,sing{},{}",newSign,sign);
            
            int status =0;
            if (Integer.parseInt(payAmount) > 0 && newSign.equals(sign)) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "success";
            } else {
                logger.error("佩奇支付宝支付回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("佩奇支付宝支付回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        }
    }
}
