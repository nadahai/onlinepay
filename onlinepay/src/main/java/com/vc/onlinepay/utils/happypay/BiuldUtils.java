package com.vc.onlinepay.utils.happypay;


import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.RSAUtils;
import com.vc.onlinepay.utils.http.HttpReqestUtils;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringUtils;

/**
 * @描述:
 * @时间:2018/5/17 10:04
 * @版权:微辰金服有限公司 Copyright (c) 2017
 */
public class BiuldUtils {
    //快乐购请求地址
    private static final String BASEURL = "http://api.kuailefpay.cn";
    public static final String BASEURL_H5URL = BASEURL+"/interface/aliNativePay";
    public static final String BASEURL_QUREY_URL = BASEURL+"/interface/transquery";
    public static final String BASEURL_WITHDRAW_URL = BASEURL+"/interface/merwithdraw";
    public static final String BASEURL_WITHQUERY_URL = BASEURL+"/interface/withquery";
    public static final String BASEURL_JSAPIURL = BASEURL+"/interface/jsApiPay";


    /** 淘宝API定位接口*/
    private static String TAOBAO_URL = "http://ip.taobao.com/service/getIpInfo.php?ip=IPADD";
    /** 支付宝API接口*/
    public static final String ALIPAY_API_URL = "https://openapi.alipay.com/gateway.do";
    /** 支付宝API*/
    public static final String APP_ID = "2018061460387371";
    public static final String APP_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDZ/MWM3V66xjYv8ptZhBPDKKeUfHAByxL0GuGxAs72Smuf8PFbkHourYlP3hF19hWhBBofqojPDvdxD5UBP61PvsXCpqRX/8vjgOWaG7Iz/PxG5w0O8QmT70SGRUNvz1eSPshviB1fXnKkm1CQJgymFw/ftvqPgdpHo6mKjNISyddolB57QJkMSNuixFQidiSgHh0fD/6GoJ+X7RDOJi97RktwBZv2m7r+NOmLyfsVntvUgUk4kDDVBNWEuhQ+X4IAwr4u64diCNcTKNIM88ZuSyuoeTbmm9jnsQ1cwe27u2lDpJoeq8hkAUVIvbj4uPAQdeHS6wb4sJa5cG74EQFzAgMBAAECggEAdpMh3oMYIV7qYOCGEoZevZzis0mRH9iYAcKRm9jcPWqz6neEwnrvi26IL7KrKtBmaYSytSDtdDw+6vg/5EMAAk3SgxRkdx3EiYc23cJNCCyICgVqvALvY9IWIzeP/ET77KhMHSccWyEkGVgG1bJs1PfcgaOl3eQTmT50XdJF/0Nc8hbPxaHvXUbZmzwmpzzGyvyGM/UQc5A190yYQmlbxScWIcW5LRCkv4muN8W0TCTRXTIcBfLSTZu25r3CxF3NfW8fhL+qM8phnMp0D1OE7yFKSHKFu1bEJ56S1AkosT37dtVH1VnYKtpd7FWb2OVtCE05XPQpgqfQ1BWpWhsGYQKBgQDsdOP+b5EnnBN5N3DqRdbgea2YAcn56giESOn3svdK+fXlvKFVgsvoUX7Yg7tr+wDpbsy3LaIEkCliNfyUt5P95O+dy2E+cFLeX/uGqLlWQSkJWGjjcuJqlNuy7eoAhmBpDbShdP7wAyNQhWJJxmlhVN7wuLdur9DiPx+FFkOA4wKBgQDsARjOMvuIzAg1dSToyukPK2RpRt8yGYFPWpHXK0WKYrAE5JrhgGp9zLM1z1kbFGAxVSRsEYfrRtT4KY8MBj3VXIeJzwal5o3pihCDfCFNaGXG/CzczJ2t9b8U1EFj/RJ3Y9dKzZyiOklCE8gvHZ63M0wS42YJ+Gvc3Xvh4HMyMQKBgD2WnKPzD03P20qhZCnBExzY1JxZKvCLQrih/T7lQIAo5yF1plgNf2r9fxqKBVE0yaIDmBLGMgMaQY0xHp7lyghBjx/8j1GiFBOT0IHchJmw0y1596f2jn7QUwEh8uc8GPSD+15qiNSfHJ2mgFlS8rPFVWRB2JVd+fxUELOrft2dAoGAP9NcHgfztt9XKP3xaXPW699UXJRqMGZtbkSURJjTScW+zNP2fx33ruX5YYgeFRDBoxXfr8pd8+dIGYVDxoC5oEZR8ZcnuR5NKufH54deiky9mb8BcwVzb2SStNNii+QZZmh+BSDuR4Fz5obrELL2BZ296S3OnsusgCL2KfRFs0ECgYBAQbK0ssFXH59SLrL4SjBUEbg7QviRgxv06axd4ScRASo6hDWRqBAPZ0JIFTxUtfuWl+ny/lkm5wthn0GpdAtWBaGImckxFcnapDJXGmsRGO/6zBqtZedl3fHUxKXmVVM8ob3Zgh0X9EVk5+WrJqBylyohgpbUMvqcj1aa9sCkfg==";
    //
    public static final String GRANT_TYPE = "authorization_code";
    public static final String METHOD_AUTH_TOKEN = "alipay.system.oauth.token";


    //获取淘宝城市编码
    public static JSONObject getcityByIpTaobao(String ipaddress){
        JSONObject result = new JSONObject();
        result.put("provinceId","");
        result.put("cityId","");
        try {
            String url = TAOBAO_URL;
            url = url.replace("IPADD",ipaddress);
            String respMsg = HttpReqestUtils.postRequestByFormUrl(url, "");
            System.out.println(respMsg);
            JSONObject json = JSONObject.parseObject(respMsg);
            if(!"0".equals(json.getString("code"))){
                return result;
            }
            JSONObject data = json.getJSONObject("data");
            System.out.println("解析结果："+data);
            if(!"CN".equalsIgnoreCase(data.getString("country_id"))){
                return result;
            }
            String provinceId = data.getString("region_id");
            String cityId = data.getString("city_id");
            if(StringUtils.isEmpty(provinceId)||StringUtils.isEmpty(cityId)||cityId.length()!=6||provinceId.length()!=6||!provinceId.endsWith("0")||!cityId.endsWith("0")){
                return result;
            }
            result.put("provinceId",provinceId);
            result.put("cityId",cityId);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    //获取支付宝UserId
    public static String getAlipayUserId(String authCode){
        return getAlipayUserId(authCode,APP_ID,APP_PRIVATE_KEY);
    }
    //获取支付宝UserId
    public static String getAlipayUserId(String authCode,String appId,String appPrivateKey){
        String userId = "";
        try {
            JSONObject json = new JSONObject(true);
            json.put("app_id",appId);
            json.put("charset","UTF-8");
            json.put("code",authCode);
            json.put("format","JSON");
            json.put("grant_type",GRANT_TYPE);
            json.put("reqCmd",METHOD_AUTH_TOKEN);
            json.put("sign_type","RSA2");
            json.put("timestamp", DateUtils.getDateTime());
            json.put("version","1.0");
            String signStr = Md5CoreUtil.getSignStr(json);
            String sign = RSAUtils.sign_256(signStr,appPrivateKey,"utf-8");
            json.put("sign",sign);
            StringBuffer buffer = new StringBuffer();
            for (String key : json.keySet()){
                String val = URLEncoder.encode(json.getString(key),"utf-8");
                buffer.append(key).append("=").append(val).append("&");
            }
            String parmsStr = buffer.toString();
//            System.out.println("支付宝授权入参:{}"+parmsStr);
            String resMsg = HttpReqestUtils.postRequestByFormUrl(ALIPAY_API_URL,parmsStr);
//            System.out.println("支付宝授权返参:{}"+resMsg);
            JSONObject result = JSONObject.parseObject(resMsg);
            System.out.println("支付宝result = " + result);
            JSONObject data = result.getJSONObject("alipay_system_oauth_token_response");
            System.out.println("支付宝response = " + data);
            if(data.containsKey("user_id")&& StringUtils.isNotEmpty(data.getString("user_id"))){
                userId = data.getString("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("获取到的支付宝UserId:{}"+userId);
        return userId;
    }
}
