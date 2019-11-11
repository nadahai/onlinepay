package com.vc.onlinepay.config;

import com.vc.onlinepay.utils.http.HttpBrowserTools;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.web.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class OnlineInterceptor extends BaseController implements HandlerInterceptor{

    private final Logger logger = LoggerFactory.getLogger(OnlineInterceptor.class);

    @Autowired
    private CoreEngineProviderService engineProviderService;

    /**
     * 拦截器
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String url = request.getRequestURL().toString();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String ip = HttpBrowserTools.getIpAddr(request);
//            logger.info("拦截器请求参数,url:{},method:{},uri:{},ip:{},", url, method, uri,ip);
            boolean isSecurityAccess = engineProviderService.isAllowedAccessIp(uri,ip);
            if(!isSecurityAccess){
                return false;
            }
            //ip风控处理
            boolean isBlackMarkHost  = engineProviderService.accessFilter("blackMarkHost",ip);
            if(isBlackMarkHost){
                logger.error("IP备案风险控制拦截成功:{}",ip);
                return false;
            }
            //域名风控处理
            String referer = HttpBrowserTools.getRefer(request);
            boolean isBlackMarkReffer  = engineProviderService.accessFilter("blackMarkReferer",referer);
            if(isBlackMarkReffer){
                logger.error("域名备案风险控制拦截成功:{}",referer);
                return false;
            }
        } catch (Exception e) {
            logger.error("拦截器异常",e);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView)
            throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e)
            throws Exception {

    }
}
