package com.vc.onlinepay.utils.hanyin;/**
 * @描述:
 * @作者: nada
 * @日期: $
 */

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.utils.BankCaseUtil;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @描述
 * @作者: nada
 * @时间: 2019-05-27 19:39
 */
public class HanYinUtils {
    private static Logger logger = LoggerFactory.getLogger(HanYinUtils.class);
    private static final Pattern REGEX_CARD_NUMBER = Pattern.compile("cardNumber\":\"(.*?)\"");
    private static final Pattern REGEX_EXPONENT = Pattern.compile("exponent\":\"(.*?)\"");
    private static final Pattern REGEX_MODULUS = Pattern.compile("modulus\":\"(.*?)\"");
    private static final Pattern REGEX_RULES = Pattern.compile("rules\":(.*?)},\"");

    // private static final Pattern REGEX_INIT_STR = Pattern.compile("window.initObjectStr(.*?)}';");

    /**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param param  请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static JSONObject sendGet2Settsion(String url, String param) {
        JSONObject response = new JSONObject();
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            if(StringUtil.isNotEmpty(param)){
                urlNameString = url + "?" + param;
            }
            JSONObject session = new JSONObject();
            URL realUrl = new URL(urlNameString);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.addRequestProperty("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.addRequestProperty("Cookie","default-cookie-name=ALwojqZ6nsfE7KmuZwGD3XbK; up_b7=lxqnjbw1ixvf; up_b4=AC10190531103019973d0046743190");
            connection.setRequestProperty("user-agent","Mozilla/5.0 (Linux; U; Android 5.1.1; en-us; KIW-AL10 Build/HONORKIW-AL10) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.100 U3/0.8.0 Mobile Safari/534.30 AlipayDefined(nt:WIFI,ws:360|592|3.0) AliApp(AP/9.5.3.030408) AlipayClient/9.5.3.030408 Language/zh-Hans");
            //connection.setRequestProperty("Cookie", "default-cookie-name=ALwojqZ6nsfE7KmuZwGD3XbK; up_b7=lxqnjbw1ixvf; up_b4=AC10190531103019973d0046743190");
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 302) {
                Map<String, List<String>> headers = connection.getHeaderFields();
                logger.info("提交订单所有headers:{}",headers);
                String Location =  connection.getHeaderField("Location");
                logger.info("提交订单获取Location:{}",Location);
                if(StringUtil.isEmpty (Location)){
                    return Constant.failedMsg("提交订单解析302新URL失败");
                }
                String  newuri = new String(Location.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                logger.info ("打开订单收银台地址:{}",newuri);
                return  Constant.failedMsg("未知URL:"+newuri);
            }
            Map<String, List<String>> map = connection.getHeaderFields();
            logger.info("getHeaderFields:{}",map);
            for (String key : map.keySet()) {
                session.put(key,map.get(key));
            }
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            response.put("session",session);

            response.put("visiablepan", BankCaseUtil.getContent(result, REGEX_CARD_NUMBER));
            response.put("exponent", BankCaseUtil.getContent(result,REGEX_EXPONENT));
            response.put("publicKey", BankCaseUtil.getContent(result,REGEX_MODULUS));
            response.put("rules", BankCaseUtil.getContent(result,REGEX_RULES));
            logger.info("===>提取参数{}",response.toJSONString());
            return  response;
        } catch (Exception e) {
            logger.error("发送GET请求出现异常",e);
            return Constant.failedMsg("获取失败");
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static JSONObject test302(String url, JSONObject params){
        HttpURLConnection conn = null;
        try {
            logger.info("提交订单地址:{},入参:{}",url,params);
            conn = getHttpConnection(conn,url,params);
            int code = conn.getResponseCode();
            if (code == 302) {
                Map<String, List<String>> headers = conn.getHeaderFields();
                logger.info("提交订单所有headers:{}",headers);
                String Location =  conn.getHeaderField("Location");
                logger.info("提交订单获取Location:{}",Location);
                if(StringUtil.isEmpty (Location)){
                    return Constant.failedMsg("提交订单解析302新URL失败");
                }
                String  newuri = new String(Location.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                logger.info ("打开订单收银台地址:{}",newuri);
                return  Constant.failedMsg("未知URL:"+newuri);
            }else if(code == 200){
                String responseHtml = HanYinUtils.getConnectionContent(conn);
                logger.info ("提交订单响应html:{}",StringUtils.deleteWhitespace(responseHtml));
                return Constant.successMsg("success");
            }else{
                return Constant.failedMsg("提交订单响应异常:"+code);
            }
        } catch (Exception e) {
            logger.info("提交订单异常", e);
            return Constant.failedMsg("提交订单异常");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * @描述 获取SESSIOIN信息
     * @作者 nada
     * @时间 2019/5/23 17:31
     */
    public static final Pattern CALLBACK = Pattern.compile("\\((.*?)\\)");
   public static JSONObject getHanyinSessionId(String callback,String encryptData,String orderNo) {
        try {
            String getSessIonUrl = "https://device.95516.com/dcs_svc/rest/outer/dcs/dcsCollector"+"?callback="+callback+"&encryptData="+encryptData;
            logger.info("获取DfpSessionId订单号:{}入参:{}",orderNo,getSessIonUrl);
            String response = HanYinUtils.sendGet(getSessIonUrl,"");
            logger.info("获取SESSIOIN订单号:{},响应:{}",orderNo,response);
            String result = BankCaseUtil.getContent(response, CALLBACK);
            if(StringUtil.isEmpty(result)){
                return Constant.failedMsg("获取SESSIOIN信息失败");
            }
            JSONObject callData = Constant.stringToJson(result);
            logger.info("获取SESSIOIN响应callData:{}",response,callData);
            if(callData == null || callData.isEmpty() || !callData.containsKey("data")){
                return Constant.failedMsg("获取SESSIOIN信息失败");
            }
            JSONObject data = callData.getJSONObject("data");
            if(data == null || data.isEmpty() || !data.containsKey("dfpSessionId")){
                return Constant.failedMsg("获取SESSIOIN信息失败");
            }
            String dfpSessionId = data.getString("dfpSessionId");
            if(StringUtil.isEmpty(dfpSessionId)){
                return Constant.failedMsg("获取SESSIOIN信息失败");
            }
            JSONObject resultData = Constant.successMsg("OK");
            resultData.put("dfpSessionId",dfpSessionId);
            return resultData;
        } catch (Exception e) {
            logger.info("提交订单异常", e);
            return Constant.failedMsg("提交订单异常");
        }
    }

    /**
     * @描述 提交银联订单接口
     * @作者 nada
     * @时间 2019/5/23 17:31
     */
    public static JSONObject postUnionSubmitOrder(String url, JSONObject params) throws IOException {
        HttpURLConnection conn = null;
        try {
            logger.info("提交订单地址:{},入参:{}",url,params);
            conn = getHttpConnection(conn,url,params);
            int code = conn.getResponseCode();
            if (code == 302) {
                Map<String, List<String>> headers = conn.getHeaderFields();
                String Location =  conn.getHeaderField("Location");
                if(StringUtil.isEmpty (Location)){
                    return Constant.failedMsg("提交订单解析302新URL失败");
                }
                String  newuri = new String(Location.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                logger.info ("打开订单收银台地址:{}",newuri);
                if(newuri.contains("cashier.95516.com/b2c/showCard.action?transNumber")){
                    //第二次重定向到defaultForm resp=5100037 自动提交
                    String responseHtml = getConnectionContent(conn);
                    JSONObject result = Constant.successMsg(responseHtml);
                    result.put("action", newuri);
                    result.put("params", responseHtml);
                    return result;
                }else if(newuri.contains("cashier.95516.com/b2c/api/unifiedOrder.action?tn")){
                    String response = HttpClientTools.sendGet(newuri, "");
                    if(StringUtil.isEmpty(response)){
                        return Constant.failedMsg("提交订单新地址响应为空");
                    }
                    Document document = Jsoup.parse(response);
                    Element defaultFormElement = document.getElementById("defaultForm");
                    if(defaultFormElement==null){
                        logger.info("银联defaultFormElement{}",defaultFormElement);
                        return Constant.failedMsg("银联"+defaultFormElement);
                    }
                    Elements inputs = defaultFormElement.getElementsByTag("input");
                    JSONObject resultData = new JSONObject();
                    for (Element input : inputs) {
                        String key = input.attr("name");
                        String value = input.attr("value");
                        resultData.put(key, value);
                    }
                    String defaultFormUrl = defaultFormElement.attr("action");
                    if(StringUtil.isEmpty(defaultFormUrl)){
                        return Constant.failedMsg("提交订单解析action为空");
                    }
                    String getSessIonUrl2 = "https://mcashier.95516.com/mobile/api/unifiedOrderIndex.action"+newuri.substring(newuri.indexOf("?"));
                    logger.info("sessionUrl:\r\n{}",getSessIonUrl2);
                    JSONObject sessionRes = sendGet2Settsion(getSessIonUrl2.trim(), "");
                    //logger.info("提交订单解析成功URL:{},结果:{},session:{}",defaultFormUrl,resultData,sessionRes);
                    if(defaultFormUrl.contains("cashier.95516.com/b2c/showCard.action?transNumber")){
                        JSONObject result = Constant.successMsg("OK");
                        resultData.put("transNumber", defaultFormUrl.split("=")[1]);
                        result.put("action", defaultFormUrl);
                        result.putAll(resultData);
                        // 银行卡编号,加密用
                        result.put("visiablepan", null == sessionRes.get("visiablepan")?"":sessionRes.get("visiablepan"));
                        // 加密指数
                        result.put("exponent", null == sessionRes.get("exponent")?"":sessionRes.get("exponent"));
                        // 公钥
                        result.put("publicKey", null == sessionRes.get("publicKey")?"":sessionRes.get("publicKey"));
                        // 规则
                        result.put("rules",null == sessionRes.get("rules")?"":sessionRes.getString("rules").concat("}"));
                        return result;
                    }
                    return postUnionSubmitOrder(defaultFormUrl,resultData);
                }else{
                    return  Constant.failedMsg("未知URL:"+newuri);
                }
            }else if(code == 200){
                String responseHtml = HanYinUtils.getConnectionContent(conn);
                logger.info ("提交订单响应html:{}",StringUtils.deleteWhitespace(responseHtml));
                Document document = Jsoup.parse(responseHtml);
                if (document.select("div[class=alert_box]").size()>0) {
                    Elements fail = document.select("p[class=main_word end_error]");
                    String msg = fail.text();
                    logger.error("提交订单失败:{}", msg);
                    return Constant.failedMsg(msg);
                }
                Element form = document.getElementById("autoForm");
                if(form == null){
                    form = document.getElementById("pay_form");
                }
                if(form == null){
                    Element pay_msg = document.selectFirst(".pay_paycards");
                    logger.info("银联",pay_msg);
                    return Constant.failedMsg("银联"+pay_msg);
                }
                Elements inputs = form.getElementsByTag("input");
                JSONObject resultData = new JSONObject();
                for (Element input : inputs) {
                    String key = input.attr("name");
                    String value = input.attr("value");
                    resultData.put(key, value);
                }
                String action = form.attr("action");
                if(StringUtil.isEmpty(action)){
                    return Constant.failedMsg("提交订单解析action为空");
                }
                JSONObject result = Constant.successMsg("success");
                result.put("action", action);
                result.put("params", resultData);
                logger.info("提交订单瀚银解析成功:{}", result);
                return result;
            }else{
                return Constant.failedMsg("提交订单响应异常:"+code);
            }
        } catch (Exception e) {
            logger.info("提交订单异常", e);
            return Constant.failedMsg("提交订单异常");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * @描述 提交翰银接口
     * @作者 nada
     * @时间 2019/5/23 17:31
     */
    public static JSONObject postHanyinCreateOrder(String actionUrl, JSONObject params) {
        try {
            logger.info("创建订单请求入参:{}",params);
            String response = HttpClientTools.httpSendPostForm(actionUrl, params);
            //logger.info("创建订单响应结果:{}", response);
            if (StringUtils.isNotEmpty(response)) {
                Document document = Jsoup.parse(response);
                if (document.select("div[class=paydone payfail]").size()>0) {
                    Elements fail = document.select("div[class=paydone payfail]");
                    Element payFail = fail.get(0).select("span").first();
                    String msg = payFail.text();
                    logger.error("创建订单失败:{}", msg);
                    return Constant.failedMsg(msg);
                }
                if (document.select("div[class=pay_topay pay_failed]").size()>0) {
                    Elements fail = document.select("div[class=pay_msg pay_fail]");
                    Element payFail = fail.get(0).select("span").first();
                    String msg = payFail.text();
                    logger.error("创建订单失败:{}", msg);
                    return Constant.failedMsg(msg);
                }
                Element form = document.getElementById("pay_form");
                if(form==null){
                    Element pay_msg = document.selectFirst(".pay_paycards");
                    logger.info("创建订单结果异常",pay_msg);
                    return Constant.failedMsg("创建订单解析异常"+pay_msg);
                }
                Elements inputs = form.getElementsByTag("input");
                JSONObject resultData = new JSONObject();
                for (Element input : inputs) {
                    String key = input.attr("name");
                    String value = input.attr("value");
                    resultData.put(key, URLEncoder.encode(value,"utf-8"));
                }
                String action = form.attr("action");
                if(StringUtil.isEmpty(action)){
                    return Constant.failedMsg("创建订单解析action为空");
                }
                JSONObject result = Constant.successMsg("success");
                result.put("action", action);
                result.put("params", resultData);
                return result;
            }
            return  Constant.failedMsg("创建订单失败，请重新下单");
        } catch (Exception e) {
            logger.error("创建订单异常地址:{},参数:{}", actionUrl, params);
            return  Constant.failedMsg("创建订单异常");
        }
    }

    public static String getDecodeChannlKey (String channelDesKey) {
        try {
            String encodedKey = StringUtils.deleteWhitespace (channelDesKey);
            String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDWiyt6N1zS49wsTiox6EO9EPSVTFqAuXhwL8QTECbMzh9JyOdQFM2VmACrBpEaxR2oOON+SP3LnN30mSJvpGzMaE9jzUuX+qsR2KWvBFpUzwt4E10OCdlUpmc5atUGC5moTDwjIWO/wHd+aIoBEqOIudfu5/hjB1R8V20jyLw+xQIDAQAB";
            return ConfigTools.decrypt (publicKey, encodedKey);
        } catch (Exception e) {
            e.printStackTrace();
            return channelDesKey;
        }
    }
    /**
     * @描述 获取数据流内容
     * @作者 nada
     * @时间 2019/5/24 14:18
     */
    public static HttpURLConnection getHttpConnection(HttpURLConnection conn, String url, JSONObject params)throws  Exception{
        StringBuffer param = new StringBuffer();
        int num = 0;
        for (String key : params.keySet()) {
            if (num == 0) {
                param.append(key).append("=").append(params.getString(key));
            } else {
                param.append("&").append(key).append("=").append(params.getString(key));
            }
            num++;
        }
        URL realUrl = new URL(url);
        conn = (HttpURLConnection) realUrl.openConnection();
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("charset", "UTF-8");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        conn.setRequestProperty("Cookie","up_b4=AC10190529160016970b0040632766; Version=1; Path=/; Secure; HttpOnly, up_b7=1d1owk9ode4ah; Version=1; Path=/; Secure; HttpOnly, default-cookie-name=d3nDJH2RrBoVL4OKfEMOmnoi; Path=/b2c");
        conn.setRequestProperty("Set-Cookie","up_b4=AC10190529160016970b0040632766; Version=1; Path=/; Secure; HttpOnly, up_b7=1d1owk9ode4ah; Version=1; Path=/; Secure; HttpOnly, default-cookie-name=d3nDJH2RrBoVL4OKfEMOmnoi; Path=/b2c");

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(param);
        out.flush();
        out.close();
        return  conn;
    }

    /**
     * @描述 获取数据流内容
     * @作者 nada
     * @时间 2019/5/24 14:18
     */
    public static String getConnectionContent( HttpURLConnection conn)throws  Exception{
        StringBuffer response = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        for (String line = null; (line = br.readLine()) != null;) {
            response.append((new StringBuilder()).append(line));
        }
        br.close();
        if(response == null || response.length()<1){
            return "";
        }
        return  response.toString();
    }

    /** * 向指定URL发送GET方法的请求,默认编码UTF-8 * * @param url * 发送请求的URL * @param param * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。 * @return URL 所代表远程资源的响应结果 */
    public static String sendGet(String url, String param) {
        return sendGet(url, param, "utf-8");
    }

    /** * 向指定URL发送GET方法的请求 * * @param url * 发送请求的URL * @param param * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。 * @param charset * 网页编码 * @return URL 所代表远程资源的响应结果 */
    public static String sendGet(String url, String param,String charSet) {
        String result = "";
        BufferedReader in = null;
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0],
                    new TrustManager[] { new cn.hutool.http.ssl.DefaultTrustManager() },
                    new SecureRandom());
            SSLContext.setDefault(ctx);
            String urlNameString = url;
            if(StringUtil.isNotEmpty(param)){
                urlNameString = url + "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/5.0 (Linux; U; Android 5.1.1; en-us; KIW-AL10 Build/HONORKIW-AL10) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.100 U3/0.8.0 Mobile Safari/534.30 AlipayDefined(nt:WIFI,ws:360|592|3.0) AliApp(AP/9.5.3.030408) AlipayClient/9.5.3.030408 Language/zh-Hans");

            connection.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            int code = connection.getResponseCode();
            if (code == 302) {
                String Location =  connection.getHeaderField("Location");
                logger.info("提交订单获取Location:{}",Location);
                String  newuri = new String(Location.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                logger.info ("打开订单收银台地址:{}",newuri);
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),charSet));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            logger.info("响应信息:{}",result);
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                logger.error("关闭出错！",e2);
            }
        }
        return result;
    }


    /** * 向指定 URL 发送POST方法的请求，默认编码UTF-8 * * @param url * 发送请求的 URL * @param param * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。 * @return 所代表远程资源的响应结果 */
    public static String sendPost(String url, String param){
        return sendPost(url, param, "utf-8");
    }

    /** * 向指定 URL 发送POST方法的请求 * * @param url * 发送请求的 URL * @param param * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。 * @param charSet * 网页编码 * @return 所代表远程资源的响应结果 */
    public static String sendPost(String url, String param , String charSet) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0],
                    new TrustManager[] { new DefaultTrustManager() },
                    new SecureRandom());
            SSLContext.setDefault(ctx);


            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpsURLConnection conn = (HttpsURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Cookie","default-cookie-name=84c5L4NjLdm2RFFnzBi8wsAf; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2216af99e3d75103-00257ca89ddac-6353160-1327104-16af99e3d7641c%22%2C%22%24device_id%22%3A%2216af99e3d75103-00257ca89ddac-6353160-1327104-16af99e3d7641c%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC%22%7D%7D; newInjectAttr=01WAyhKFnY7V8/BcSWOvztLESPc3eowmc0BeA1JDWjNyhkd0sPTboSXyfY9Tb4Pl/u12; up_b7=bxt87wpoamhd; up_b4=AC10190529105103ce2e0021276616; dfp_t_c=1559098407569; dvs_v_t=1559098407530; dfpSessionId=110006D006LUy8VCEJphpc6N7TaEe1559098411542");

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),charSet));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }



    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }

    /**
     * @描述 是否为json
     * @作者 nada
     * @时间 2019/5/29 9:42
     */
    public static boolean isJSON2(String str) {
        boolean result = false;
        try {
            Object obj= JSON.parse(str);
            result = true;
        } catch (Exception e) {
            result=false;
        }
        return result;
    }
}