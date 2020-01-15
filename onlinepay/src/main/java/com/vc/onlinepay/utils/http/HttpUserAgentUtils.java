package com.vc.onlinepay.utils.http;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 * 用户代理字符串识别工具
 *
 * @author ThinkGem
 * @version 2014-6-13
 */
public class HttpUserAgentUtils {

    //浏览器类型枚举定义
    public enum BrowserType {
        /**
         * 微信
         */
        WEChAT ("wechat"),
        /**
         * 支付宝
         */
        ALIPAY ("alipay"),
        /**
         * qq钱包
         */
        QQWALLET ("qqwallet");

        BrowserType (String name) {
            this.name = name;
        }

        private String name;

        public String getName () {
            return name;
        }
    }

    public static List<String> browserList = new ArrayList<String> (45);// list大小

    //browserList里面的值表示的是移动端（非PC端）
    static {
        browserList.add ("nokia");
        browserList.add ("samsung");
        browserList.add ("midp-2");
        browserList.add ("cldc1.1");
        browserList.add ("symbianos");
        browserList.add ("maui");
        browserList.add ("untrusted/1.0");
        browserList.add ("windows ce");
        browserList.add ("iphone");
        browserList.add ("ipad");
        browserList.add ("android");
        browserList.add ("blackberry");
        browserList.add ("ucweb");
        browserList.add ("brew");
        browserList.add ("j2me");
        browserList.add ("yulong");
        browserList.add ("coolpad");
        browserList.add ("tianyu");
        browserList.add ("ty-");
        browserList.add ("k-touch");
        browserList.add ("haier");
        browserList.add ("dopod");
        browserList.add ("lenovo");
        browserList.add ("mobile");
        browserList.add ("huaqin");
        browserList.add ("aigo-");
        browserList.add ("ctc/1.0");
        browserList.add ("ctc/2.0");
        browserList.add ("cmcc");
        browserList.add ("daxian");
        browserList.add ("mot-");
        browserList.add ("sonyericsson");
        browserList.add ("gionee");
        browserList.add ("htc");
        browserList.add ("zte");
        browserList.add ("huawei");
        browserList.add ("webos");
        browserList.add ("gobrowser");
        browserList.add ("iemobile");
        browserList.add ("wap2.0");
        browserList.add ("ucbrowser");
        browserList.add ("ipod");
    }

    /**
     * @描述:获取浏览器支付类型
     * @作者:nada
     * @时间:2017年6月8日 下午10:02:31
     */
    public static BrowserType getBrowserType (HttpServletRequest request) {
        String agentContent = request.getHeader ("user-agent").toLowerCase ();
        if (agentContent.contains ("aliapp") || agentContent.contains ("alipayclient") || agentContent.contains ("alipay")) {
            return BrowserType.ALIPAY;
        }
        if (agentContent.contains ("micromessenger")) {
            return BrowserType.WEChAT;
        }
        if (agentContent.contains ("qbwebviewtype") || agentContent.contains ("qq")) {
            return BrowserType.QQWALLET;
        }
        return BrowserType.ALIPAY;
    }

    /**
     * 获取用户代理对象
     */
    public static UserAgent getUserAgent (HttpServletRequest request) {
        return UserAgent.parseUserAgentString (request.getHeader ("User-Agent"));
    }

    /**
     * 获取设备类型
     */
    public static DeviceType getDeviceType (HttpServletRequest request) {
        return getUserAgent (request).getOperatingSystem ().getDeviceType ();
    }

    /**
     * 是否是PC
     */
    public static boolean isComputer (HttpServletRequest request) {
        return DeviceType.COMPUTER.equals (getDeviceType (request));
    }

    /**
     * 是否是手机
     */
    public static boolean isMobile (HttpServletRequest request) {
        return DeviceType.MOBILE.equals (getDeviceType (request));
    }

    public static boolean isMobileBrowser (HttpServletRequest request) {
        String userAgent = request.getHeader ("User-Agent").toLowerCase ();
        if (StringUtils.isEmpty (userAgent)) {
            userAgent = request.getHeader ("USER-AGENT").toLowerCase ();
        }
//		System.out.println("userAgent = " + userAgent);
        for (String mobileKey : browserList) {
            if (userAgent.indexOf (mobileKey) != -1) {
                return true;
            }
        }
        return false;
    }


    /**
     * 是否是平板
     */
    public static boolean isTablet (HttpServletRequest request) {
        return DeviceType.TABLET.equals (getDeviceType (request));
    }

    /**
     * 是否是手机和平板
     */
    public static boolean isMobileOrTablet (HttpServletRequest request) {
        DeviceType deviceType = getDeviceType (request);
        return DeviceType.MOBILE.equals (deviceType) || DeviceType.TABLET.equals (deviceType);
    }

    /**
     * 获取浏览类型
     */
    public static Browser getBrowser (HttpServletRequest request) {
        return getUserAgent (request).getBrowser ();
    }

    /**
     * 是否IE版本是否小于等于IE8
     */
    public static boolean isLteIE8 (HttpServletRequest request) {
        Browser browser = getBrowser (request);
        return Browser.IE5.equals (browser) || Browser.IE6.equals (browser) || Browser.IE7.equals (browser) || Browser.IE8.equals (browser);
    }

}
