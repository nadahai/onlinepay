package com.vc.onlinepay.utils.http;

import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @描述: 浏览器解析识别工具类
 * @作者:nada
 * @时间:2018/12/20
 **/
public class HttpBrowserTools {

    /***
     * @描述:获取客户端Ip地址
     * @作者:nada
     * @时间:2018/12/6
     **/
    public static String getIpAddr (HttpServletRequest request) {
        // 通过请求头获取ip地址
        String ip = request.getHeader ("x-forwarded-for");
        // 判断ip地址是否是代理地址
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getHeader ("Proxy-Client-IP");
        }
        // 判断ip地址是否是代理地址
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getHeader ("WL-Proxy-Client-IP");
        }
        // 判断ip地址是否是代理地址
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getRemoteAddr ();
        }
        if ("0:0:0:0:0:0:0:1".equals (ip)) {
            return "127.0.0.1";
        }
        if (StringUtil.isNotEmpty (ip) && ip.indexOf (",") > 0) {
            return ip.split (",")[0];
        }
        return ip;
    }

    /**
     * @描述:判断是否微信进入
     * @时间:2018/6/5 17:58
     */
    public static boolean isWeChat (HttpServletRequest request) {
        String userAgent = request.getHeader ("user-agent").toLowerCase ();
        return userAgent.contains ("micromessenger");
    }

    /**
     * @描述:判断是否支付宝打开
     * @时间:2018/6/26 14:54
     */
    public static boolean isAlipay (HttpServletRequest request) {
        String userAgent = request.getHeader ("user-agent").toLowerCase ();
        return userAgent.contains ("alipay");
    }

    /**
     * @描述:判断是否IPhone打开
     * @时间:2018/6/26 14:54
     */
    public static boolean isIOS (HttpServletRequest request) {
        String userAgent = request.getHeader ("user-agent") == null ? "" : request.getHeader ("user-agent").toLowerCase ();
        return userAgent.contains ("iphone");
    }

    /**
     * @描述:判断浏览器Referer
     * @作者:nada
     * @时间:2018/12/20
     **/
    public static String getRefer (HttpServletRequest request) {
        try {
            String reffer = "";
            String[] list = {"referer", "Referer", "referrer", "REFERER"};
            for (String reKey : list) {
                if (StringUtils.isNotBlank (request.getHeader (reKey))) {
                    reffer = request.getHeader (reKey);
                    break;
                }
            }
            reffer = reffer.toLowerCase ();
            if (!reffer.contains ("http://") && !reffer.contains ("https://")) {
                return "";
            }
            reffer = reffer.substring (reffer.indexOf ("://") + 3);
            if (!reffer.contains ("/")) {
                return reffer;
            }
            reffer = reffer.substring (0, reffer.indexOf ("/"));
            if (Constant.getSubCountStr (reffer, ".") == 2) {
                reffer = reffer.substring (reffer.indexOf ("."));
            }
            if (!reffer.startsWith (".")) {
                reffer = "." + reffer;
            }
            return reffer;
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return "";
    }
}
