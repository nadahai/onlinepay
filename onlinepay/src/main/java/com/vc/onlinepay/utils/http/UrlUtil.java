package com.vc.onlinepay.utils.http;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.order.scan.ZongHengWSDDaScanServiceImpl;
import com.vc.onlinepay.utils.StringUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author jauyang
 * @description
 * @time 2019-04-17 18:54
 */
public class UrlUtil {

    private static final String CONTENT_TYPE_TEXT_JSON = "text/html;charset=utf-8";

    private static Logger logger = LoggerFactory.getLogger (UrlUtil.class);

    public static String getReal(String url){
        try {
            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(url);
            HttpParams params = client.getParams();
            params.setParameter("http.protocol.handle-redirects", false);
            method.setRequestHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.3(0x17000321) NetType/WIFI Language/zh_CN");
            client.executeMethod(method);
            String realUrl = method.getURI().getURI();
            System.out.printf("获取真实的地址成功：%s,\n原地址是：%s",realUrl,url);
            return realUrl;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.printf("获取地址失败，原来的地址是：%s",url);
            return "";
        }
    }

    public static JSONObject getUrlParam(String url) {
        JSONObject jsonObject = new JSONObject();
        if (url == null) {
            return jsonObject;
        }
        url = url.trim();
        if ("".equals(url)) {
            return jsonObject;
        }
        String[] urlParts = url.split("\\?");
        //没有参数
        if (urlParts.length == 1) {
            return jsonObject;
        }
        //有参数
        String[] params = urlParts[1].split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            try {
                jsonObject.put(keyValue[0], URLDecoder.decode(keyValue[1],"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     * @描述:302跳转解析
     * @作者:nada
     * @时间:2019/3/29
     **/
    public static String httpPostHtml302 (String url,JSONObject parms) throws Exception {
        try {
            org.apache.http.client.HttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost (url);
            httpPost.setHeader ("Content-Type", CONTENT_TYPE_TEXT_JSON);
            StringEntity se = new StringEntity (parms.toString ());
            se.setContentType (CONTENT_TYPE_TEXT_JSON);
            httpPost.setEntity (se);
            HttpResponse response = client.execute (httpPost);
            int code = response.getStatusLine ().getStatusCode ();
            if (code == 302) {
                Header header = response.getFirstHeader ("location");
                String newuri = header.getValue ();
                newuri = new String(newuri.getBytes("iso-8859-1"), "utf-8");
                if(StringUtil.isEmpty (newuri)){
                    return "解析302新URL失败";
                }
                if(newuri.contains ("https://qr.alipay.com")){
                    return newuri;
                }
                logger.info ("新地址Url{}",newuri);
                //return  httpPostHtml302 (newuri,parms);
                return  newuri;
            }else if (code == 200) {
                HttpEntity entity = response.getEntity ();
                String html = EntityUtils.toString (entity, "UTF-8");
                String temp = StringUtils.deleteWhitespace(html);
                logger.info ("响应html:{}",temp);
                Document doc = Jsoup.parse(html);
                Element element = doc.getElementById("wx");
                return element.val ();
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return "解析失败";
    }

    /**
     * @描述:302跳转解析
     * @作者:nada
     * @时间:2019/3/29
     **/
    public static String httpPostHtml200 (String url,JSONObject parms){
        try {
            org.apache.http.client.HttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost (url);
            httpPost.setHeader ("Content-Type", CONTENT_TYPE_TEXT_JSON);
            if(parms !=null && parms.containsKey("AccessToken")){
                httpPost.setHeader ("AccessToken",parms.getString("AccessToken"));
            }
            StringEntity se = new StringEntity (parms.toString ());
            se.setContentType (CONTENT_TYPE_TEXT_JSON);
            httpPost.setEntity (se);
            HttpResponse response = client.execute (httpPost);
            int code = response.getStatusLine ().getStatusCode ();
            if (code == 200) {
                HttpEntity entity = response.getEntity ();
                String html = EntityUtils.toString (entity, "UTF-8");
                String temp = StringUtils.deleteWhitespace(html);
                logger.info ("响应html:{}",temp);
                return temp;
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return "解析失败";
    }
}