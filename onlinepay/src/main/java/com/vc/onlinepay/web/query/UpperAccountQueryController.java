package com.vc.onlinepay.web.query;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.pay.api.query.UpperAccountServiceApi;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 上游余额查询接口
 * @类名称:UpperAccountQueryApi.java
 * @时间:2018年1月4日上午11:36:15
 * @作者:nada
 * @版权:公司 Copyright (c) 2018
 */
@Controller
@RestController
@RequestMapping("/onlineAccountQuery")
public class UpperAccountQueryController extends BaseController{
	
	@Autowired
    private UpperAccountServiceApi upperAccountService;
	
	/**
	* @描述:上游余额查询接口入口
	* @时间:2017年6月6日 下午10:24:20
	*/
	@Override
	@RequestMapping(value = "", produces = "text/html;charset=UTF-8")
	public void doPost(HttpServletRequest request,HttpServletResponse response){
		setHttpServletContent(request, response);
		try {
			JSONObject result = this.invokeRestOrder(request);
			logger.info("下游上游余额查询完毕:{}",result);
			response.getWriter().write(new Gson().toJson(result));
		} catch (Exception e) {
			logger.error("下游上游余额查询接口异常", e);
			super.writeErrorResponse(response);
		}
	}

	/**
	 * @描述:调用上游余额查询订单业务处理
	 * @时间:2017年6月16日 下午6:19:27
	 */
	public JSONObject invokeRestOrder(HttpServletRequest request) {
		JSONObject result =  new JSONObject();
		try {
			JSONObject params = HttpRequestTools.getRequestJson(request);
			result = upperAccountService.checkReqPrms(params,request);
			if(!result.getString("code").equals(Constant.SUCCESSS)){
				logger.error("下游上游余额查询请求入参验证失败:{}",result);
				return result;
			}
			ThreadUtil.execute(() -> upperAccountService.doUpperQuery(params));
			return Constant.successMsg("提交成功！");
		} catch (Exception e) {
			logger.error("下游上游余额查询请求异常", e);
			return Constant.failedMsg("上游余额查询失败.");
		}
	}

}
