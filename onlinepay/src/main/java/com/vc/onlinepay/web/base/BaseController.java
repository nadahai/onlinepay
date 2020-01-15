package com.vc.onlinepay.web.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.persistent.monitor.AsynNotice;
import com.vc.onlinepay.utils.http.HttpPaySubmit;
import com.vc.onlinepay.persistent.common.CommonBusService;
import com.vc.onlinepay.persistent.common.CommonPayService;
import org.springframework.web.servlet.ModelAndView;

/**
 * servlet基础抽象类
 * 
 * @类名称:BaseHttpServlet.java
 * @时间:2017年12月6日上午10:33:56
 * @作者:nada
 * @版权:公司 Copyright (c) 2017
 */
public abstract class BaseController {

	public static final long ORDER_TIMEOUT = 1000 * 15;// 15秒时间差

	public static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	@Autowired
	public CommonBusService payBusService;
	@Autowired
	private CommonPayService commonPayService;
	@Autowired
	public AsynNotice asynNotice;
	@Value("${onlinepay.project.domainName:}")
	public String domainName;
	@Value("${onlinepay.project.actualName:}")
	public String actualName;
	@Value("${onlinepay.project.baseUrl}")
	public String base_url;
	/**
	 * @描述:页面直接打开html
	 * @作者:ChaiJing THINK
	 * @时间:2018/5/29 11:30
	 */
	public ModelAndView writeHTMLtoPage(HttpServletRequest request, HttpServletResponse response,String html) {
		try {
			request.setCharacterEncoding(Constant.CHART_UTF);
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			response.getWriter().append(html).close();
		} catch (Exception e) {
			logger.error("同步打开HTML页面异常", e);
		}
		return null;
	}
	/**
	 * @描述:页面form post提交
	 * @时间:2018/6/8 10:20
	 */
	public void pageFormSubmit(HttpServletRequest request, HttpServletResponse response,String actionUrl,JSONObject params) {
		try {
			this.writeHTMLtoPage(request,response,HttpPaySubmit.buildPostRequest(actionUrl, params));
		} catch (Exception e) {
			logger.error("同步页面form提交异常", e);
		}
	}
	/**
	 * @描述:页面form Get提交
	 * @作者:ChaiJing THINK
	 * @时间:2018/8/16 11:30
	 */
	public void pageFormSubmit(HttpServletRequest request, HttpServletResponse response,String actionUrl) {
		try {
			request.setCharacterEncoding(Constant.CHART_UTF);
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			response.getWriter().append(HttpPaySubmit.buildGetRequest(actionUrl)).close();
		} catch (Exception e) {
			logger.error("同步页面form提交异常", e);
		}
	}
	
	/**
	 * @描述:埋点日志
	 * @作者:nada
	 * @时间:2018年3月8日 上午11:11:04
	 */
	public void saveLog(String title, String... logDes) {
		try {
			commonPayService.saveLog(title, logDes);
		} catch (Exception e) {
			logger.error("埋点日志异常", e);
		}
	}
	
	/**
     * 初始化域名
     * @param request
     * @return
     */
    public String initDomain(HttpServletRequest request,String domainPath,String classPath){
        StringBuilder notifyUrl=new StringBuilder();
        notifyUrl.append(request.getScheme()).append("://").append(domainPath).append("/").append(actualName).append(classPath);
        return notifyUrl.toString();
    }
    
    /**
     * 守护进程监控超时订单
     * @param reqData
     */
    public void monitorOrder(ThreadLocal<Long> threadLocal,JSONObject reqData){
    	try {
    		if(threadLocal == null){
    			logger.error("守护进程监控为空");
    		}
    		long beginTime = threadLocal.get();
            long endTime = System.currentTimeMillis();
            threadLocal.remove();
            long diffTime = endTime-beginTime;
            if(diffTime > ORDER_TIMEOUT){
                this.saveLog("扫码支付运行线程超时",String.valueOf(diffTime),reqData.toString());
                logger.error("耗时:{}最大内存:{}m已分配内存:{}m已分配内存中的剩余空间:{}m最大可用内存:{}m",(diffTime),Runtime.getRuntime().maxMemory()/1024/1024, Runtime.getRuntime().totalMemory()/1024/1024, Runtime.getRuntime().freeMemory()/1024/1024,(Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory())/1024/1024); 
            }
		} catch (Exception e) {
			if(threadLocal!=null){
				threadLocal.remove();
			}
			logger.error("守护进程监控超时订单异常", e);
		}
    }
    
    /**
	 * @描述:post请求
	 * @作者:nada
	 * @时间:2018年2月28日 下午2:41:38
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		setHttpServletContent(request, response);
	}

	/**
	 * @描述:异常同步响应json报文
	 * @时间:2018年6月11日 下午6:21:25
	 */
	public void writeErrorResponse(HttpServletResponse response) {
		try {
			setHttpServletContent(null, response);
			response.getWriter().write(new Gson().toJson(Constant.exception("响应异常")));
		} catch (Exception e) {
			logger.error("异常同步响应json报文异常", e);
		}
	}

	/**
	 * @描述:异常转发到错误界面
	 * @时间:2018年6月11日 下午6:21:25
	 */
	public void writeErrorDispatcher(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.getRequestDispatcher("/failure.ftl").forward(request, response);
		} catch (Exception e) {
			logger.error("异常转发到错误界面异常", e);
		}
	}
	
    /**
	 * @描述:同步响应json报文
	 * @时间:2018年3月8日 上午10:26:14
	 */
	public ModelAndView writeResponse(HttpServletResponse response,JSONObject result) {
		try {
			setHttpServletContent(null, response);
			response.getWriter().write(new Gson().toJson(result));
		} catch (Exception e) {
			logger.error("同步响应json报文异常", e);
		}
		return  null;
	}

	public ModelAndView showErrorMsg(HttpServletResponse response,String msg)throws Exception {
		ModelAndView mode = new ModelAndView("failure");
		mode.addObject("msg",msg);
		return mode;
	}
    
    /**
	 * @描述:初始化设置报文请求响应编码格式
	 * @时间:2017年12月18日 下午5:45:02
	 */
	public void setHttpServletContent(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (request != null) {
				request.setCharacterEncoding(Constant.CHART_UTF);
			}
			if (response != null) {
				response.setCharacterEncoding(Constant.CHART_UTF);
				response.setContentType("application/json;charset=utf-8");
			}
		} catch (Exception e) {
			logger.error("初始化设置报文请求响应编码格式异常", e);
		}
	}
}
