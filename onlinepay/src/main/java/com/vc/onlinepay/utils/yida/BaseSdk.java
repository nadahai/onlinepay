package com.vc.onlinepay.utils.yida;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author: dingzhiwei
 * @date: 2018/4/16
 * @description:
 */
public class BaseSdk {

    /**
     * 发起HTTP/HTTPS请求(method=POST)
     * @param url
     * @return
     */
    public static String call4Post(String url) {
        try {
            URL url1 = new URL(url);
            if("https".equals(url1.getProtocol())) {
                return HttpClient.callHttpsPost(url);
            }else if("http".equals(url1.getProtocol())) {
                return HttpClient.callHttpPost(url);
            }else {
                return "";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

}
