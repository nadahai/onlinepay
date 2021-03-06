package com.vc.onlinepay.web.query;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.pay.api.query.QueryServiceApi;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 代付订单查询（开发给下游）
 * @类名称:AmalgamateTransferQueryApi.java
 * @时间:2017年12月27日下午2:20:54
 * @作者:nada
 * @版权:公司 Copyright (c) 2017
 */
@Controller
@RestController
@RequestMapping("/replaceQuery")
public class ReplaceQueryController extends BaseController {

    @Autowired
    private QueryServiceApi queryService;

    /**
     * @描述:统一代付查询接口入口
     * @作者:nada
     * @时间:2017年12月27日 下午4:38:47
     */
    @Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        setHttpServletContent(request, response);
        try {
            JSONObject result = this.invoke(request);
            if(!result.containsKey("paymentStatus")){
                result.put("paymentStatus", 6);
            }
            logger.info ("统一代付查询接口入口响应{}",result);
            response.getWriter().write(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("统一代付查询接口入口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:统一代付查询接口业务处理
     * @作者:Alan
     * @时间:2017年6月6日 下午10:24:30
     */
    public JSONObject invoke(HttpServletRequest request) throws IOException {
        JSONObject result =  new JSONObject();
        try {
            JSONObject params = HttpRequestTools.getRequestJson(request);
            logger.info ("统一代付查询接口业务处理入参{}",params);
            result = queryService.checkReqPrms(params,request);
            if(!result.getString("code").equals(Constant.SUCCESSS)){
                logger.error("下游查询接口参数验证失败:{}",result);
                return result;
            }
            
            String orderNo = params.getString("orderNo");
            VcOnlinePayment vcOnlinePayment = payBusService.findVcOnlinePaymentByPorderNo(orderNo);
            if (vcOnlinePayment == null) {
                result.put("code", Constant.FAILED);
                result.put("msg", "订单号不存在！");
                return result;
            }
            if (vcOnlinePayment.getStatus() == 1) {
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "查询成功:代付成功");
            } else {
                result.put("code", Constant.FAILED);
                result.put("msg", "查询成功:"+vcOnlinePayment.getRemark());
            }
            result.put("paymentStatus",vcOnlinePayment.getStatus());
            result.put("sign", Md5CoreUtil.md5ascii(result,params.getString("password")).toUpperCase());
            return result;
        } catch (Exception e) {
            logger.error("统一代付查询接口业务处理异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "代付查询失败");
            return result;
        }

    }
}
