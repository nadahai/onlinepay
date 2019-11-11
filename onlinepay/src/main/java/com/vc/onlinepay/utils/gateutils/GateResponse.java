package com.vc.onlinepay.utils.gateutils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vc.onlinepay.enums.GateCodeEnum;
import java.io.Serializable;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

public class GateResponse implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(GateResponse.class);
    //*************公共响应参数*************
    //业务响应码
    private int code;
    //业务响应描述
    private String msg;
    //时间戳
    private long timestamp;
    //请求接口名称
    private String method;
    //响应签名串
    private String sign;
    //*************公共响应参数*************

    /**
     * @描述:网关响应报文
     * @作者:nada
     * @时间:2019/3/15
     **/
    public static ModelAndView writeResponse(HttpServletResponse response,JSONObject result) {
        try {
            GateRequest.initHttpServletRequest (null, response);
            response.setContentType("application/json;charset=utf-8");
            if(result == null || result.isEmpty ()){
                response.getWriter().write(new Gson ().toJson(initGateResult ()));
                return null;
            }
            response.getWriter().write(new Gson ().toJson(result));
        } catch (Exception e) {
            logger.error("Gateway write response exception", e);
        }
        return null;
    }

    /**
     * @描述:网关响应报文
     * @作者:nada
     * @时间:2019/3/15
     **/
    public static ModelAndView writeRedirect(HttpServletResponse response,String redirectUrl) {
        try {
            response.sendRedirect (redirectUrl);
            return null;
        } catch (Exception e) {
            logger.error("Gateway write redirect exception", e);
        }
        return null;
    }


    /**
     * @描述:组装响应
     * @作者:nada
     * @时间:2019/4/2
     **/
    public static ModelAndView writeViewPath(JSONObject result,String viewPath ) {
        try {
            JSONObject data = result.getJSONObject("data");
            ModelAndView mode = new ModelAndView(viewPath);
            for(String key : data.keySet()){
                if(data.get(key) instanceof JSONObject){
                    mode.addObject(key,data.getJSONObject(key));
                    continue;
                }
                mode.addObject(key,data.get(key));
            }
            return mode;
        } catch (Exception e) {
            logger.error("Gateway write response exception", e);
        }
        return null;
    }


    /**
     * @描述: 网关响应html
     * @作者:nada
     * @时间:2019/3/18
     **/
    public static ModelAndView writeHTMLtoPage(HttpServletResponse response,String html) {
        try {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.getWriter().append(html).close();
        } catch (Exception e) {
            logger.error("同步网关响应html异常", e);
        }
        return null;
    }
    /**
     * @描述: 1,初始化
     * @作者:nada
     * @时间:2019/3/15
     **/
    public static JSONObject initGateResult(){
        JSONObject result = new JSONObject ();
        result.put ("code", GateCodeEnum.failed);
        result.put ("msg","in init failed");
        result.put ("timestamp",GateUtils.getTimestamp ());
        result.put ("reqCmd","unknown");
        result.put ("content",new JSONObject ());
        return  result;
    }

    /**
     * @描述: 1,网关响应报文
     * @作者:nada
     * @时间:2019/3/15
     **/
    public static JSONObject BuildGateResult(GateCodeEnum codeEnum, String msg, String method, JSONObject content){
        JSONObject result = new JSONObject ();
        result.put ("code",codeEnum.getKey ());
        result.put ("msg",msg);
        result.put ("reqCmd",method);
        return  result;
    }

    /**
     * @描述: 2,网关失败响应报文
     * @作者:nada
     * @时间:2019/3/15
     **/
    public static JSONObject BuildFailedResult(String msg){
        return BuildGateResult (GateCodeEnum.FAIL,msg,"failed",new JSONObject());
    }

    /**
     * @描述: 3,网关错误响应报文
     * @作者:nada
     * @时间:2019/3/15
     **/
    public static JSONObject BuildErrorResult(String msg){
        return BuildGateResult (GateCodeEnum.ERROE,msg,"error",new JSONObject());
    }

    /**
     * @描述: 4,网关异常响应报文
     * @作者:nada
     * @时间:2019/3/15
     **/
    public static JSONObject buildExceptionResult(String msg){
        return BuildGateResult (GateCodeEnum.EXCEPTION,msg,"exception",new JSONObject());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
