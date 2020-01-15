/**
 * @类名称:MainController.java
 * @时间:2017年10月27日下午5:55:40
 * @作者:nada
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.web.base;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @描述:TODO
 * @作者:nada
 * @时间:2017年10月27日 下午5:55:40
 */
@RestController
@RequestMapping("/")
public class IndexController extends BaseController {
    
	@Value("${onlinepay.project.shortName:}")
    private String shortName;
    @Value("${onlinepay.project.domainName:}")
    private String domainName;
    @Value("${onlinepay.project.actualName:}")
    private String actualName;

    /**
     * @Description: 进入系统首页
     * @throws
     */
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public ModelAndView  index(HttpServletRequest request,HttpServletResponse response) {
    	 ModelAndView result = new ModelAndView("index");
         result.addObject("shortName",shortName);
         return result;
    }
    
    /**
     * @Description: 进入系统api接口系统
     * @throws
     */
    @RequestMapping(value = "/api")
    public ModelAndView  api(HttpServletRequest request,HttpServletResponse response) {
          ModelAndView result = new ModelAndView("toxpay");
          result.addObject("shortName",shortName);
          return result;
    }
    
    /**
     * @Description: 进入失败页面
     */
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public ModelAndView to403(){
        ModelAndView model = new ModelAndView("failure");
        return model;
    }

    @RequestMapping(value = "/order", produces = "text/html;charset=UTF-8")
    public ModelAndView  order(HttpServletRequest request,HttpServletResponse response) {
        ModelAndView result = new ModelAndView("test/order");
        result.addObject("domainName",domainName);
        result.addObject("actualName",actualName);
        return result;
    }

    @RequestMapping(value = "/replace", produces = "text/html;charset=UTF-8")
    public ModelAndView  replace(HttpServletRequest request,HttpServletResponse response) {
        ModelAndView result = new ModelAndView("test/replace");
        result.addObject("domainName",domainName);
        result.addObject("actualName",actualName);
        return result;
    }

    @RequestMapping(value = "/search", produces = "text/html;charset=UTF-8")
    public ModelAndView  search(HttpServletRequest request,HttpServletResponse response) {
        ModelAndView result = new ModelAndView("test/search");
        result.addObject("domainName",domainName);
        result.addObject("actualName",actualName);
        return result;
    }

    @RequestMapping(value = "/notice", produces = "text/html;charset=UTF-8")
    public ModelAndView  notice(HttpServletRequest request,HttpServletResponse response) {
        ModelAndView result = new ModelAndView("test/notice");
        result.addObject("domainName",domainName);
        result.addObject("actualName",actualName);
        return result;
    }

    /**
     * @描述:测试回调接口
     */
    @RequestMapping(value = "notify")
    public void callback(HttpServletRequest request, HttpServletResponse response) {
        try {
            setHttpServletContent(request, response);
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("测试回调接口接收参数:{}", requestJson);
            response.getWriter().write("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
