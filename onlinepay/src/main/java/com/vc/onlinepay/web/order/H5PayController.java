package com.vc.onlinepay.web.order;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cmd.TradeCmd;
import com.vc.onlinepay.enums.PayChannelEnum;
import com.vc.onlinepay.pay.api.order.H5ServiceApi;
import com.vc.onlinepay.pay.common.OrderServiceImpl;
import com.vc.onlinepay.persistent.entity.channel.MerchChannel;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * H5下单暴露API接口
 * @类名称:RemittanceWebpagePayApi.java
 * @时间:2017年12月28日上午9:53:06
 * @版权:公司 Copyright (c) 2017
 */
@Controller
@RestController
@RequestMapping("/h5PayApi")
public class H5PayController extends BaseController {
    @Autowired
    private H5ServiceApi h5Service;
    @Autowired
    private OrderServiceImpl orderServiceImpl;
    @Autowired
    private TradeCmd tradeCmd;


    /**
     * @描述:H5下单暴露API接口
     * @时间:2017年12月28日 上午9:53:54
     */
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject result = this.invokePay(request);
            logger.info ("H5下单暴露API接口:{}",result);
            if (!result.getString("code").equals(Constant.SUCCESSS)) {
                return super.writeResponse(response, Constant.failedMsg(result.containsKey("msg")?result.getString("msg"):"下单失败"));
			}
            if(result.containsKey("redirectUrl")){
                response.sendRedirect(result.getString("redirectUrl"));
                return null;
            }
            if(result.containsKey("redirectHtml")){
                String html = result.getString("redirectHtml");
                return super.writeHTMLtoPage(request,response,html);
            }
            if(result.containsKey("bankUrl")){
                return super.writeResponse(response, result);
            }
            String viewPath = result.containsKey("viewPath")?result.getString("viewPath"):"auto/autoSubmit";
            if(StringUtils.isNotBlank(viewPath)){
                ModelAndView mode = new ModelAndView(viewPath);
                mode.addObject("map",result.getJSONObject("data"));
                mode.addObject("actionUrl",result.get("actionUrl"));
                return mode;
            }
            super.writeResponse(response, result);
            return null;
        } catch (Exception e) {
            logger.error("H5下单暴露API接口异常", e);
            super.writeErrorDispatcher(request, response);
            return null;
        }
    }

    /**
     * @描述:H5下单接口业务处理和返回处理结果
     * @时间:2017年12月28日 上午10:03:53
     */
    public JSONObject invokePay(HttpServletRequest request) {
        JSONObject reqData = null;
        String orderId = "";
        try {
            reqData = HttpRequestTools.getRequestJson(request);
            orderId = (null!=reqData && reqData.containsKey("orderId")) ?reqData.getString("orderId"):"";
            // 第一步：参数验证业务处理
            JSONObject result = orderServiceImpl.checkReqPrms(reqData,PayChannelEnum.H5,request);
            if (!result.getString("code").equals(Constant.SUCCESSS)) {
                logger.error("H5下单单号:{},参数验证失败:{}", orderId,result);
                return result;
            }
            tradeCmd.addNetReqPrms(request, reqData);

            MerchChannel preMerchChannel = MerchChannel.getH5MerchChannel(reqData);
            if(null == preMerchChannel){
                logger.error("H5下单单号:{},payType参数错误", orderId);
                return Constant.failedMsg("payType参数错误!");
            }

            //第二步：通道配置验证业务处理
            MerchChannel merchChannel = orderServiceImpl.autoRouteChannel(preMerchChannel,reqData.getString("amount"),reqData.getString("merchantId"));
            result = tradeCmd.checkChannel(reqData, merchChannel);
            if (!Constant.isOkResult(result)){
                tradeCmd.doRestFailedOrder(reqData, merchChannel);
                logger.error("H5下单单号:{},通道验证失败:{}",orderId,result);
                return result;
            }

            //第三步：订单业务处理
            result = tradeCmd.doRestOrder(reqData, merchChannel);
            if (!Constant.isOkResult(result)) {
                logger.error("H5下单单号:{},下单系统异常（下单保存失败）", orderId);
                return Constant.failedMsg("H5下单保存失败,请重新发起订单");
            }

            //金额浮动处理
            //tradeCmd.channelAmountfloat(reqData,merchChannel);

            // 第四步：支付业务处理
            return h5Service.doRestPay(reqData);
        } catch (Exception e) {
            logger.error("H5下单接口业务处理异常,订单号:{},入参:{}",orderId,reqData, e);
            return Constant.failedMsg("H5下单接口异常,请联系运维人员");
        }
    }
}
