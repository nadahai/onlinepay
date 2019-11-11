/**
 * @类名称:CacheManagerApi.java
 * @时间:2018年3月2日下午5:18:36
 * @作者:lihai
 * @版权:公司 Copyright (c) 2018
 */
package com.vc.onlinepay.web.gate;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @描述:缓存管理api,redis缓存管理统一暴露api
 * @作者:lihai
 * @时间:2018年3月2日 下午5:18:36
 */
@Controller
@RestController
@RequestMapping("/cacheManagerApi")
@CrossOrigin("*")
public class CacheGateController extends BaseController {

	@Autowired
	private RedisCacheApi cacheService;

	/**
	 * @描述:缓存管理接口
	 * @时间:2018年3月2日 下午5:19:47
	 */
	@RequestMapping(value = "/flushkey", produces = "text/html;charset=UTF-8")
	public ModelAndView flushkey(HttpServletRequest request, HttpServletResponse response) {
		try {
			super.setHttpServletContent(request, response);
			JSONObject reqData = HttpRequestTools.getRequestJson(request);
			logger.info("刷新缓存入参:{}",reqData);
			if (reqData == null || reqData.isEmpty() || !reqData.containsKey("flushkey")) {
				return writeResponse(response,Constant.failedMsg("获取参数为空"));
			}
			if(reqData.containsKey("flushkey")){
				cacheService.cleanKey(reqData.getString("flushkey"));
			}
			if(reqData.containsKey("flushkey1")){
				cacheService.cleanKey(reqData.getString("flushkey1"));
			}
			if(reqData.containsKey("flushkey2")){
				cacheService.cleanKey(reqData.getString("flushkey2"));
			}
			if(reqData.containsKey("flushkey3")){
				cacheService.cleanKey(reqData.getString("flushkey3"));
			}
			return writeResponse(response,Constant.failedMsg("缓存管理成功"));
		} catch (Exception e) {
			logger.error("缓存管理异常", e);
			return super.writeResponse(response, Constant.failedMsg("缓存管理异常"));
		}
	}
}
