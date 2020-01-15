/**
 * @类名称:MainController.java
 * @时间:2017年10月27日下午5:55:40
 * @作者:nada
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.web.base;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @描述:TODO
 * @作者:nada
 * @时间:2017年10月27日 下午5:55:40
 */
@Controller
@RequestMapping("/error")
public class ErrorController extends BaseController {

    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public String index() {
        return "failure";
    }
    
    @RequestMapping(value = "500", produces = "text/html;charset=UTF-8")
    public String error500() {
        return "failure";
    }
    
    @RequestMapping(value = "404", produces = "text/html;charset=UTF-8")
    public String error404() {
        return "failure";
    }
}
