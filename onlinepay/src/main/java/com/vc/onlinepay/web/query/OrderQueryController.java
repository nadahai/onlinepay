package com.vc.onlinepay.web.query;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.pay.api.query.QueryServiceApi;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 订单交易查询（开发给下游）
 * @类名称:AmalgamatePayQueryApi.java
 * @时间:2017年12月27日下午2:20:35
 * @作者:lihai 
 * @版权:公司 Copyright (c) 2017
 */
@Controller
@RestController
@RequestMapping("/orderQuery")
public class OrderQueryController extends BaseController {
	
    @Autowired
    private QueryServiceApi queryService;
	
	/**
	 * @描述:统一交易查询接口入口
	 * @作者:lihai 
	 * @时间:2017年12月27日 下午4:38:26
	 */
    @Override
	@RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request,HttpServletResponse response){
    	setHttpServletContent(request, response);
        try {
    		JSONObject result = this.invoke(request);
    		if(!result.containsKey("status")){
    		    result.put("status", 6);
    		}
    		response.getWriter().write(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("统一交易查询接口入口异常", e);
            super.writeErrorResponse(response);
        }
    }
    
    /**
     * @描述:统一交易查询接口业务处理
     * @作者:lihai 
     * @时间:2017年12月27日 下午4:25:18
     */
    public JSONObject invoke(HttpServletRequest request) throws IOException {
        JSONObject result =  new JSONObject();
        try {
            JSONObject params = HttpRequestTools.getRequestJson(request);
            JSONObject checkResult = queryService.checkReqPrms(params,request);
            if(!checkResult.getString("code").equals(Constant.SUCCESSS)){
                logger.error("下游查询接口业务参验证失败:{}",result);
                result.put("code", Constant.FAILED);
                result.put("msg", "下游查询接口业务参验证失败！");
                return result;
            }
            String orderId = params.getString("orderId");
			List<VcOnlineOrder> vcOnlineOrders = payBusService.findOrderByCOrderNo(orderId);
    		if (vcOnlineOrders == null ||  vcOnlineOrders.size() < 1) {
    		    result.put("code", Constant.FAILED);
    		    result.put("msg", "订单号不存在！");
    			return result;
			}
			VcOnlineOrder vcOnlineOrder = vcOnlineOrders.get(0);
			if(vcOnlineOrder !=null && vcOnlineOrder.getStatus() == 4){
			     result.put("code", Constant.SUCCESSS);
			}else{
			    result.put("code", Constant.FAILED);
			}
            result.put("msg",vcOnlineOrder.getOrderDes());
            result.put("status", vcOnlineOrder.getStatus());
            result.put("sign", Md5CoreUtil.md5ascii(result,params.getString("password")).toUpperCase());
            return result;
		}catch(Exception e){
			logger.error("监听到查询结果处理异常", e);
			result.put("code",Constant.FAILED);
			result.put("msg", "系统异常");
			return result;
		}
      
    }
   
}
