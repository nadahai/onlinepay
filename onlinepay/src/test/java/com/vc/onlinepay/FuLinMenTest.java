package com.vc.onlinepay;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.http.UrlUtil;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.Md5CoreUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuLinMenTest {

    /** 登录 */
    private static String LOGIN_URL="https://smzf.yjpal.com/payQRCode/loginCheck.htm?mobileNo=MOBILE_NO&password=PASSWORD";
    /** 下单 */
    private static String PRE_PAY_URL="https://smzf.yjpal.com/payQRCode/newPay.htm?qrcodeId=QR_CODE_ID&payMoney=PAY_MONEY&appId=APP_ID&channelCode=CHANNEL_CODE&payType=PAY_TYPE";
    /** 获取支付参数 */
    private static String WX_APP_ID_URL="https://smzf.yjpal.com/payQRCode/newPayMess.htm?qrcodeId=QR_CODE_ID&channelCode=wx";
    /** 交易列表 */
    private static String ORDER_LIST_URL="https://smzf.yjpal.com/payQRCode/newQrcodeTransjnlsList.htm?mobileNo=MOBILE_NO&pageNo=0&time=201905";
    /** 收银台地址 */
    private static String QR_CORD_URL="https://smzf.yjpal.com/payQRCode/qrcodeCardPage.htm?mobileNo=MOBILE_NO";
    /** 交易查询 */
    private static String ORDER_QUERY_URL="https://smzf.yjpal.com/payQRCode/newQRcodeTransjnlsMessage.htm?mobileNo=MOBILE_NO&orderNo=ORDER_NO";

    public static void main(String[] args){
        String mobile="17162086606";
        String pwd="q123456";
        String amount="0.01";
        String payType="1";
        // 1登录
        JSONObject loginJson = login(mobile,pwd);
        System.out.println(loginJson);
       /* // 2获取交易二维码：
        JSONObject qrCodeJson = getQrCodeId(mobile,loginJson.getString("token"));
        String qrCodeUrl = qrCodeJson.getJSONObject("pqm").getString("ylxwQrcodeurl");
        String realUrl = getReal(qrCodeUrl);
        // 下单用
        String qrCodeId = realUrl.split("params=")[1];
        // 3.获取下单参数
        JSONObject payInfo = getPayData(qrCodeId);
        // 4.下单
        JSONObject orderJson = createOrder(payType,amount,qrCodeId,payInfo.getString("channelCode"),payInfo.getString("appId"));
        if(orderJson!=null&&!orderJson.isEmpty()&&orderJson.containsKey("code")&&orderJson.getString("code").equals("0000")){
            System.out.printf("\n下单成功：%s\n",orderJson.getString("url"));
        }*/
        String orderNo = getOrderNo("https://smzf2.yjpal.com/payQRCode/yjpayGatewayPubPage.htm?prepay_id=000697F6FD19D5FDA7FD24C20BB3F665A69EB110F9BCE73B25E786BD8599297C364828FCC2F40DEAD9D2F32F7A9CAB3C");
        if(StringUtils.isEmpty(orderNo)){
            System.out.println("未唤醒支付或未支付");
            return;
        }
        // 5.查询
        JSONObject queryJson = orderQuery(mobile,loginJson.getString("token"),orderNo);
        System.out.println(queryJson);
        JSONObject transjnls = queryJson.getJSONObject("transjnls");
        System.out.println(transjnls.getString("status"));
    }

    private static JSONObject login(String mobile,String pwd){
        pwd = DigestUtil.md5Hex(pwd+"superpw1234_!QAZ");
        String url = LOGIN_URL.replace("MOBILE_NO",mobile).replace("PASSWORD",pwd);
        String result = UrlUtil.httpPostHtml200(url,new JSONObject());
        return JSON.parseObject(result);
    }

    private static JSONObject getQrCodeId(String mobile,String token){
        String url = QR_CORD_URL.replace("MOBILE_NO",mobile);
        //String result = HttpRequest.post(url).header("AccessToken",token).execute().body();
        JSONObject prms =  new JSONObject();
        prms.put("AccessToken",token);
        String result = UrlUtil.httpPostHtml200(url,prms);
        return JSON.parseObject(result);
    }

    private static JSONObject getPayData(String qrCodeId){
        JSONObject jsonObject = new JSONObject();
        String url = WX_APP_ID_URL.replace("QR_CODE_ID",qrCodeId);
        String result = HttpRequest.post(url).execute().body();
        jsonObject.put("appId",result.split("\\|")[1]);
        jsonObject.put("channelCode",result.split("\\|")[2]);
        return jsonObject;
    }

    private static JSONObject createOrder(String payType,String amount,String qrCodeId,String channelCode,String appId) {
        String url = PRE_PAY_URL.replace("QR_CODE_ID",qrCodeId).replace("PAY_MONEY",amount).replace("APP_ID",appId).replace("CHANNEL_CODE",channelCode).replace("PAY_TYPE",payType);
        String result = HttpRequest.post(url).execute().body();
        return JSON.parseObject(result);
    }

    private static String getOrderNo(String url) {
        String result = HttpUtil.get(url);
        Pattern p= Pattern.compile("color:red\">(.*) 无");
        Matcher m=p.matcher(result);
        if (m.find()) {
            result = m.group(1);
        } else {
            result = "";
        }
        System.out.println(result);
        return result;
    }

    private static JSONObject orderQuery(String mobile,String token){
        ORDER_LIST_URL="https://smzf.yjpal.com/payQRCode/newQrcodeTransjnlsList.htm?mobileNo=MOBILE_NO&pageNo=0&time=201905&localdate=20190507&orderNo=PA0112019050716213848506207";
        String url = ORDER_LIST_URL.replace("MOBILE_NO",mobile);
       // String result = HttpRequest.post(url).header("AccessToken",token).execute().body();
        JSONObject prms =  new JSONObject();
        prms.put("AccessToken",token);
        String result = UrlUtil.httpPostHtml200(url,prms);
        return JSON.parseObject(result);
    }

    private static JSONObject orderQuery(String mobile,String token,String orderNo){
        String url = ORDER_QUERY_URL.replace("MOBILE_NO",mobile).replace("ORDER_NO",orderNo);
        //String result = HttpRequest.post(url).header("AccessToken",token).execute().body();
        JSONObject prms =  new JSONObject();
        prms.put("AccessToken",token);
        String result = UrlUtil.httpPostHtml200(url,prms);
        return JSON.parseObject(result);
    }

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
}
