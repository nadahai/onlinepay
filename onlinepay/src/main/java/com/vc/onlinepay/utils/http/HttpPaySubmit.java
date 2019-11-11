package com.vc.onlinepay.utils.http;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;

import java.util.Map;

public class HttpPaySubmit {

    private static final String POST = "post";
    private static final String GET = "get";

    /**
     * @Title: buildRequest
     * @Description: 用于下订单后自动提交 交易表单到支付系统
     */
    public static String buildPostRequest (String actionUrl, JSONObject params) {
        return buildPostRequest (POST, actionUrl, null, params);
    }

    public static String buildPostRequest (String actionUrl, Map<String, String> params) {
        return buildPostRequest (POST, actionUrl, params, null);
    }

    public static String buildGetRequest (String actionUrl, JSONObject params) {
        return buildPostRequest (GET, actionUrl, null, params);
    }

    public static String buildGetRequest (String actionUrl) {
        try {
            JSONObject params = new JSONObject ();
            if (actionUrl.indexOf ("?") != -1) {
                String str = actionUrl.substring (actionUrl.indexOf ("?") + 1);
                actionUrl = actionUrl.substring (0, actionUrl.indexOf ("?"));
                params = HttpClientTools.StringToJson (str);
            }
            return buildGetRequest (actionUrl, params);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return buildPostRequest (POST, actionUrl, null, null);
    }

    /**
     * @描述:自动提交Html
     * @作者:ChaiJing THINK
     * @时间:2018/8/16 12:32
     */
    public static String buildPostRequest (String method, String actionUrl, Map<String, String> mapData, JSONObject jsonData) {
        StringBuilder html = new StringBuilder ();
        html.append ("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append ("<form id=\"pay_form\" name=\"pay_form\" action=\"").append (actionUrl).append ("\" method=\"").append (method).append ("\" >\n");
        if (mapData != null) {
            for (String key : mapData.keySet ()) {
                if (mapData.get (key) != null) {
                    html.append ("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + mapData.get (key) + "\">\n");
                }
            }
        }
        if (jsonData != null) {
            for (String key : jsonData.keySet ()) {
                if (jsonData.get (key) != null) {
                    html.append ("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + jsonData.getString (key) + "\">\n");
                }
            }
        }
        html.append ("</form>\n");
        return html.toString ();
    }

    /**
     * @描述:支付宝Jsp 订单提交
     * @作者:ChaiJing THINK
     * @时间:2018/8/16 12:32
     */
    public static String buildAlipayJSApi (String tradeNO) {
        StringBuilder html = new StringBuilder ();
        html.append ("<script type=\"application/javascript\">\n");
        html.append ("window.onload=function(){tradePay('" + tradeNO + "');}\n");
        html.append ("function ready(callback) {if (window.AlipayJSBridge) { callback && callback();} else {document.addEventListener('AlipayJSBridgeReady', callback, false);}}\n" + "function tradePay(tradeNO) {ready(function(){AlipayJSBridge.call(\"tradePay\", {tradeNO: tradeNO},function (data) {console.log(data)});});}\n");
        html.append ("</script>\n");
        return html.toString ();
    }
}
