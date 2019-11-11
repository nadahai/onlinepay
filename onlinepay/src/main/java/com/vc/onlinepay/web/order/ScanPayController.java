package com.vc.onlinepay.web.order;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cmd.TradeCmd;
import com.vc.onlinepay.enums.PayChannelEnum;
import com.vc.onlinepay.pay.api.order.ScanPayServiceApi;
import com.vc.onlinepay.pay.common.OrderServiceImpl;
import com.vc.onlinepay.persistent.entity.channel.MerchChannel;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 统一扫码下单api
 */
@Controller
@RestController
@RequestMapping("/scanPayApi")
@CrossOrigin("*")
public class ScanPayController extends BaseController {

    @Autowired
    private ScanPayServiceApi scanPayService;
	@Autowired
    private OrderServiceImpl orderServiceImpl;
    @Autowired
    private TradeCmd tradeCmd;

    /**
     * @描述:统一扫码下单api
     * @时间:2018年1月15日 下午5:12:02
     */
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response){
        try {
            JSONObject result = this.invokePay(request);
            logger.info ("统一扫码下单api响应:{}",result);
            if (!result.getString("code").equals(Constant.SUCCESSS)) {
                return super.writeResponse(response, Constant.failedMsg(result.containsKey("msg")?result.getString("msg"):"下单失败"));
            }
            if(result.containsKey("bankUrl")){
                return super.writeResponse(response, result);
            }
            if(result.containsKey("redirectUrl")){
                response.sendRedirect(result.getString("redirectUrl"));
                return null;
            }
            if(result.containsKey("redirectHtml")){
                return super.writeHTMLtoPage(request,response,result.getString("redirectHtml"));
            }
            String viewPath = result.containsKey("viewPath")?result.getString("viewPath"):"auto/autoSubmit";
            if(StringUtils.isNotBlank(viewPath)){
                ModelAndView mode = new ModelAndView(viewPath);
                mode.addObject("map",result.getJSONObject("data"));
                mode.addObject("actionUrl",result.get("actionUrl"));
                return mode;
            }
            return super.writeResponse(response, result);
        } catch (Exception e) {
            logger.error("扫码下单响应异常", e);
            return super.writeResponse(response, Constant.exception("扫码下单异常,请联系运维人员"));
        }
    }

    /**
     * @描述:扫码下单业务处理
     * @时间:2018年1月15日 下午5:12:24
     */
    public JSONObject invokePay(HttpServletRequest request) throws ServletException {
        JSONObject result = new JSONObject();
        JSONObject reqData  = null;
        String orderId = "";
        try {
        	reqData = HttpRequestTools.getRequestJson(request);
            
            //第一步：参数验证业务处理
            result =  orderServiceImpl.checkReqPrms(reqData,PayChannelEnum.SCAN,request);
            orderId = (null!=reqData && reqData.containsKey("orderId")) ?reqData.getString("orderId"):"";
            if(!result.getString("code").equals(Constant.SUCCESSS)){
                logger.error("扫码下单单号:{},参数验证失败:{}",orderId,result);
                return result;
            }
            tradeCmd.addNetReqPrms(request, reqData);

            MerchChannel preMerchChannel = MerchChannel.getScanMerchChannel(reqData);
            if(null == preMerchChannel){
                logger.error("扫码下单单号:{},service参数错误",orderId);
                return Constant.failedMsg("service参数错误!");
            }
            //第二步：通道配置验证业务处理
            MerchChannel merchChannel = orderServiceImpl.autoRouteChannel(preMerchChannel,reqData.getString("amount"),reqData.getString("merchantId"));

            //通道秘钥等参数填充
            result = tradeCmd.checkChannel(reqData, merchChannel);
            if(!result.getString("code").equals(Constant.SUCCESSS)){
                logger.error("扫码下单单号:{},通道验证失败:{}",orderId,result);
                return result;
            }

            //第三步：订单业务处理
            result = tradeCmd.doRestOrder(reqData, merchChannel);
            if(!result.getString("code").equals(Constant.SUCCESSS)){
                logger.error("扫码下单单号:{},下单系统异常（下单保存失败）", orderId);
                return Constant.failedMsg("扫码下单保存失败,请重新发起订单");
            }
            //金额浮动处理
            tradeCmd.channelAmountfloat(reqData,merchChannel);

           //第四步：下单业务处理
            return scanPayService.doRestPay(reqData);
        } catch (Exception e) {
            logger.error("扫码下单单号:{},业务处理异常{}",orderId,reqData, e);
            return Constant.failedMsg("扫码下单异常,请联系运维人员");
        }
    }
}
