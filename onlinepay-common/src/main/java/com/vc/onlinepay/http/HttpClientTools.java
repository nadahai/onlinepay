package com.vc.onlinepay.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.Base64Utils;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import com.vc.onlinepay.utils.StringUtil;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * Http网络请求工具类
 */
public class HttpClientTools {

    private static Logger logger = LoggerFactory.getLogger(HttpClientTools.class);

    public static String CHARTSET_UTF_8 = "UTF-8";
    // 设置连接超时时间，单位毫秒
    private static int connectTimeout = 20000;
    // 设置从connectManager获取Connection超时时间，单位毫秒
    private static int connectionRequestTimeout = 20000;
    // 请求获取数据的超时时间，单位毫秒
    private static int setSocketTimeout = 20000;
    private static final int connTimeOut = 20000;
    
    private static ConnectionKeepAliveStrategy myStrategy = null;
    static {
        myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if ((value != null) && ("timeout".equalsIgnoreCase(param))) {
                        return Long.parseLong(value) * 1000L;
                    }
                }
                return connectTimeout;
            }
        };
    }

    private static CloseableHttpClient httpclient = HttpClientBuilder.create().setMaxConnTotal(1000).setMaxConnPerRoute(15)
            .setKeepAliveStrategy(myStrategy).build();

    public static String httpSendPostForm(String url, JSONObject params) throws IOException {
        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type","application/x-www-form-urlencoded");
//        header.put("Content-Type","application/json;charset=utf-8");
        return httpSendPostForm(url,params,header,"");
    }

    /**
     * @描述:模拟from发送POST方法的请求
     * @描述:请求参数应该是 name1=value1&name2=value2 的形式
     * @时间:2017年6月28日 下午5:07:32
     */
    public static String httpSendPostForm(String url, JSONObject params, Map<String, String> header,String connectTimeOut) throws IOException {
        HttpURLConnection conn = null;
        StringBuilder result = new StringBuilder();
        try {
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
//            logger.info("添加{}个参数", num);
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("charset", "UTF-8");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            for(String key:header.keySet()){
                conn.setRequestProperty(key,header.get(key));
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(connectTimeout);
            if(StringUtils.isNotBlank(connectTimeOut)){
                conn.setConnectTimeout(Integer.valueOf(connectTimeOut));
                conn.setReadTimeout(Integer.valueOf(connectTimeOut));
            }
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            for (String line = null; (line = br.readLine()) != null;) {
                result.append((new StringBuilder()).append(line));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("模拟form发送 POST 请求出现异常", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result.toString();
    }

    /**
     * @描述:   基础网络请求类http client post请求
     * @param   url  请求url
     * @param   valuePairs List<BasicNameValuePair>
     * @param   charset 字符集
     * @throws  SocketTimeoutException
     * @throws  Exception
     * @date    2017年9月11日 下午3:43:40
     */
    public static String httpClientSendPost(String url, List<BasicNameValuePair> valuePairs, String charset)throws SocketTimeoutException, Exception {
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        String respContent = null;
        try {
            httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(setSocketTimeout).build();
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new UrlEncodedFormEntity(valuePairs, charset));
            response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = response.getEntity();
                respContent = EntityUtils.toString(he, charset);
            } else {
                throw new SocketTimeoutException("httpClientPost连接异常["+ response.getStatusLine().getStatusCode() +"]");
            }
        } catch (Exception e) {
            logger.error("基础网络请求异常{}", url, e);
        } finally {
            if (response != null) {
                try {
                    response.getEntity().getContent().close();
                    response.close();
                } catch (Exception e) {
                    logger.error("关闭连接异常",e);
                }
            }
            if (httpPost != null) {
                try {
                    httpPost.releaseConnection();
                } catch (Exception e) {
                    logger.error("释放连接异常",e);
                }
            }
        }
        return respContent;
    }
    /**
     * @描述:基础网络请求类http client post请求
     * @param url 请求url
     * @param reqParam json字符串
     * @param charset 字符集
     * @throws SocketTimeoutException
     * @throws Exception
     * @date    2017年9月11日 下午3:43:40
     */
    public static String baseHttpSendPost(String url, String reqParam, String charset) throws SocketTimeoutException, Exception {
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        String respContent = null;
        try {
            httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(Integer.valueOf(setSocketTimeout)).build();
            httpPost.setConfig(requestConfig);
            StringEntity postParams = new StringEntity(reqParam, charset);
            postParams.setContentEncoding(charset);
            if(StringUtils.isEmpty(charset)){
            	charset = CHARTSET_UTF_8;
            }
            postParams.setContentType("application/json;charset="+charset);
            httpPost.setEntity(postParams);
            logger.error("基础网络请求网关url{},入参:{}", url, reqParam);
            response = httpclient.execute(httpPost);
            int code=response.getStatusLine().getStatusCode();
            logger.info("访问code:"+code);
            if (code == 200) {
                HttpEntity he = response.getEntity();
                respContent = EntityUtils.toString(he, charset);
            } else {
                throw new SocketTimeoutException("基础网络链接非200异常{}" + url);
            }
        } catch (ConnectTimeoutException e){
            logger.error("基础网络请求链接超时异常{},参数{}", url, reqParam);
        } catch (SocketTimeoutException e){
            logger.error("基础网络请求发送超时异常{},参数{}", url, reqParam);
        } catch (Exception e) {
            logger.error("基础网络请求异常{},参数{}", url, reqParam, e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    logger.error("关闭response链接异常", e);
                }
            }
            if (httpPost != null) {
                try {
                    httpPost.releaseConnection();
                } catch (Exception e) {
                    logger.error("关闭httpPost链接异常", e);
                }
            }
            logger.info("关闭链接OK");
        }
        return respContent;
    }
    /**
     * @描述:模拟from发送POST方法的请求
     * @描述:请求参数应该是 name1=value1&name2=value2 的形式
     * @作者:nada
     * @时间:2017年6月28日 下午5:07:32
     */
    public static String httpSendPostFrom(String url, JSONObject params,Map<String, String> header) throws IOException {
        HttpURLConnection conn = null;
        StringBuilder result = new StringBuilder();
        try {
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
//            logger.info("添加{}个参数", num);
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("charset", "UTF-8");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            for(String key:header.keySet()){
                conn.setRequestProperty(key,header.get(key));
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(connectTimeout);
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            for (String line = null; (line = br.readLine()) != null;) {
                result.append((new StringBuilder()).append(line));
            }
            br.close();
        } catch (Exception e) {
        	e.printStackTrace();
            logger.info("模拟form发送 POST 请求出现异常", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result.toString();
    }
    /**
     * @描述:PostMethod请求 application/x-www-form-urlencoded
     * @时间:2018/5/14 9:30
     */
    public static  String POSTReturnString(String url, JSONObject jsonObject,String charSet) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        String result = "";
        try {
//            client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);//            链接超时 10S
//            client.getHttpConnectionManager().getParams().setSoTimeout(10000);//            读取超时 10S
            method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=" + charSet);
            for (String key :jsonObject.keySet()) {
                method.setParameter(key, jsonObject.getString(key));
            }
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                logger.info("请求响应失败" + statusCode);
            } else {
                result = method.getResponseBodyAsString();
                logger.info("POST请求响应:{}",result);
            }
        } catch (HttpException e) {
            logger.error("POST请求异常",e);
        } catch (IOException e) {
            logger.error("POST请求IO异常",e);
        }finally {
            method.releaseConnection(); // 释放连接
        }
        return result;
    }

    /**
     * @描述: Post发送NameValuePair参数
     * @param Payurl
     * @param param
     * @return
     */

    public static String doPostMethod(String Payurl, NameValuePair[] param) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(Payurl);
        String result = "";
        try {
            client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);//            链接超时 10S
            client.getHttpConnectionManager().getParams().setSoTimeout(10000);//            读取超时 10S
            method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            method.setRequestBody(param);
            logger.info("doPostMethod请求入参:{}", JSON.toJSONString(param));
            int status = client.executeMethod(method);
            if (status == HttpStatus.SC_OK) {
                result = method.getResponseBodyAsString();
                logger.info("doPostMethod返回参数:{}",result);
            } else {
                logger.info("请求无响应!");
                throw new RuntimeException("请求无响应");
            }
        } catch (Exception ie) {
            logger.error("doPostMethod请求异常!",ie);
        } finally {
            method.releaseConnection(); // 释放连接
        }
        return result;
    }

    /**
     * @描述:netty推送信息
     * @作者:nada
     * @时间:2019/4/24
     **/
    public static String sendNettyUrlpost(String postUrl, String body) throws IOException {
        try {
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-type", "x-www-form-urlencoded;charset=UTF-8");
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            os.write(body);
            os.flush();
            os.close();
            String result = "";
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader bf = new BufferedReader(in);
                String recieveData = null;
                while ((recieveData = bf.readLine()) != null) {
                    result += recieveData + "\n";
                }
                in.close();
                conn.disconnect();
            }
            return result;
        }  catch (IOException io) {
            io.printStackTrace();
            return "";
        }
    }

    //基础网络请求二次包装 CHARTSET_UTF_8
    public static String baseHttpSendPost(String url, JSONObject reqParam) throws Exception {
        return baseHttpSendPost(url, reqParam, CHARTSET_UTF_8);
    }
    //基础网络请求二次包装 CHARTSET_UTF_8
    public static String baseHttpSendPost(String url, String jsonStr) throws Exception {
        return baseHttpSendPost(url, jsonStr, CHARTSET_UTF_8);
    }
    //基础网络请求二次包装 CHARTSET_UTF_8
    public static String baseHttpSendPost(String url, Map<String, String> reqMap) throws Exception {
        return baseHttpSendPost(url, reqMap.toString(), CHARTSET_UTF_8);
    }
    //基础网络请求二次包装 json
    public static String baseHttpSendPost(String url, JSONObject reqParam, String charset) throws Exception {
        return baseHttpSendPost(url,reqParam.toString(),charset);
    }

    public static String httpSendPostFrom(String url,JSONObject params) throws IOException {
        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type","application/x-www-form-urlencoded");
//        header.put("Content-Type","application/json;charset=utf-8");
        return httpSendPostFrom(url,params,header);
    }

    /**
     * @描述: post发送 Map<String, String> 转 NameValuePair参数
     * @时间:2018/6/6 14:25
     */
    public static String doPostMethodWithUrl(String postUrl,Map<String, String> parm) throws Exception {
        try {
            return doPostMethod(postUrl,generatNameValuePair(parm,null,false));
        } catch (Exception e) {
            logger.error("doPostMethod组装参数异常!",e);
            return "";
        }
    }
    
    
    /**
     * @描述: post发送 Map<String, String> 转 NameValuePair参数
     * @时间:2018/6/6 14:25
     */
    public static String doPostMethodWithUrlEncode(String postUrl,Map<String, String> parm, String charset) throws Exception {
        try {
            return doPostMethod(postUrl,generatNameValuePair(parm,charset,true));
        } catch (Exception e) {
            logger.error("doPostMethod组装参数异常!",e);
            return "";
        }
    }
    //MAP类型数组转换成NameValuePair类型
    private static NameValuePair[] generatNameValuePair(Map<String, String> properties, String charset, boolean urlEncode) throws Exception {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String value = urlEncode? URLEncoder.encode(entry.getValue(), charset):entry.getValue();
            nameValuePair[i++] = new NameValuePair(entry.getKey(),value);
        }
        return nameValuePair;
    }
    /**
     * @描述:发送 Map<String, String> 转 BasicNameValuePair数据 CHARTSET_UTF_8
     * @时间:2018/6/6 14:25
     */
    public static String sendBasicNameValueData(String url,Map<String, String> pamrs) throws Exception{
        return httpClientSendPost(url, getBasicNameValuePair(pamrs),CHARTSET_UTF_8);
    }

    /**
     * @描述:发送 Map<String, String> 转 BasicNameValuePair数据 CHARTSET_UTF_8
     * @时间:2018/6/6 14:25
     */
    public static String sendBasicNameValueData(String url,JSONObject pamrs) throws Exception{
        return httpClientSendPost(url, getBasicNameValuePair(pamrs),CHARTSET_UTF_8);
    }

    //MAP类型数组转换成BasicNameValuePair类型
    private static List<BasicNameValuePair> getBasicNameValuePair( Map<String, String> properties) throws Exception {
        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        return list;
    }

    //MAP类型数组转换成BasicNameValuePair类型
    private static List<BasicNameValuePair> getBasicNameValuePair( JSONObject properties) throws Exception {
        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        for (String key :properties.keySet()) {
            list.add(new BasicNameValuePair(key,properties.getString(key)));
        }
        return list;
    }

	/**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param param  请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送GET请求出现异常",e);
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
        return result;
    }

    public static String getBase64ByUrl(String imageUrl) throws Exception {
        // new一个URL对象
        URL url = new URL(imageUrl);
        // 打开链接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置请求方式为"GET"
        conn.setRequestMethod("GET");
        // 超时响应时间为5秒
        conn.setConnectTimeout(3 * 1000);
        // 通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();
        // 得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = readInputStream(inStream);
        return Base64Utils.encode(data);
    }

    private static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // 创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        // 每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        // 使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            // 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        // 关闭输入流
        inStream.close();
        // 把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    public static String sendPost(String url, String param) {
        return sendPost(url,param,"");
    }

    public static String sendPost(String url, String param,String contentType) {
        return sendPost(url,param,contentType,new HashMap<>());
    }
    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param,String contentType,Map<String,String> header) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            //打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Charsert", "UTF-8");
            if(StringUtils.isNotEmpty(contentType)){
                conn.setRequestProperty("Content-Type",contentType);
            }
            if(null != header && !header.isEmpty()){
                for(String headerKey:header.keySet()){
                    conn.setRequestProperty(headerKey,header.get(headerKey));
                }
            }
            //发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
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
	
	/**
	 * @描述:请求&拼接字符串转为JSONObject格式（通常get请求参数转为json）
	 * @作者:nada
	 * @时间:2018年3月5日 下午5:05:43
	 */
	public static JSONObject StringToJson(String str){
	    JSONObject notifyJson = new JSONObject();
        String[] param = str.split("&");
        for (String content : param) {
            if (content.indexOf("=") > 0) {
                String key = content.substring(0, content.indexOf("="));
                String value = content.substring(content.indexOf("=") + 1);
                notifyJson.put(StringUtils.deleteWhitespace(key), StringUtils.deleteWhitespace(value).replace("\"", ""));
            }else if (content.indexOf(":") > 0) {
                String key = content.substring(0, content.indexOf(":"));
                String value = content.substring(content.indexOf(":") + 1);
                notifyJson.put(StringUtils.deleteWhitespace(key),StringUtils.deleteWhitespace(value).replace("\"", ""));
            }
        }
        return notifyJson;
	}

    public static String sendUrlGet(String url, String param) throws IOException {
        Map<String, String> header = new HashMap<>();
        header.put("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        return sendUrlGet(url,param,header);
    }

	/**
     * 向指定URL发送GET方法的请求
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     */
    public static String sendUrlGet(String url, String param,Map<String, String> header) throws IOException {
        BufferedReader in = null;
        try {
        	String result = "";
            String urlNameString = url;
        	if(StringUtil.isNotEmpty(param)){
                urlNameString = url + "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            for(String key:header.keySet()){
                connection.setRequestProperty(key,header.get(key));
            }
            connection.connect();
            //获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) {
                //System.out.println(key+"="+map.get(key));
            }
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
        	logger.error("发送GET请求出现异常",e);
        	return null;
        } finally {
        	 if (in != null) {
                 in.close();
             }
        }
    }
    
    /**
     *  获取String XML
     */
    public static String getStringXML(Map<String, String> params) {
    	
    	StringBuffer sbf = new StringBuffer();
    	sbf.append("<?xml version='1.0' encoding='UTF-8'?><xml>");
        try {
            for (String key : params.keySet()) {
            	sbf.append("<"+key+">");
            	sbf.append(params.get(key));
            	sbf.append("</"+key+">");
            }
        } catch (Exception e) {
        	logger.error("获取String XML",e);
        	return null;
        }
        sbf.append("</xml>");
        return sbf.toString();
    }


    /**
     * @param url String 发送地址
     * @param nameValue String Map 发送数据
     * @return String 返回类型 FAIL 处理失败， EXCEPTION 系统异常 ，其他的为response结果
     * @Title: httpPost
     * @Description: HTTP POST 发送方式
     */
    public static String httpPost (String url, Map<String, String> nameValue) {
        // 构造HttpClient的实例
        HttpClient httpClient = new HttpClient ();
        PostMethod postMethod = new PostMethod (url);
        // 超时时间
        if (connTimeOut != 0) {
            httpClient.getHttpConnectionManager ().getParams ().setConnectionTimeout (connTimeOut);
        }
        if (connTimeOut != 0) {
            httpClient.getHttpConnectionManager ().getParams ().setSoTimeout (connTimeOut);
        }
        // 填入各个表单域的值
        if (null != nameValue) {
            Set<String> keys = nameValue.keySet ();
            for (String key : keys) {
                postMethod.setParameter (key, nameValue.get (key));
            }
        }
        // 设置字符编码
        postMethod.getParams ().setParameter (HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            // 执行postMethod
            int statusCode = httpClient.executeMethod (postMethod);
            // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
            // 301或者302
            if (statusCode == org.apache.commons.httpclient.HttpStatus.SC_OK) {
                byte[] responseBody = postMethod.getResponseBody ();
                return new String (responseBody, StandardCharsets.UTF_8);
            } else if (statusCode == org.apache.commons.httpclient.HttpStatus.SC_MOVED_PERMANENTLY || statusCode == org.apache.commons.httpclient.HttpStatus.SC_MOVED_TEMPORARILY) {
                // 需要自己处理转发
                // 从头中取出转向的地址
                Header locationHeader = postMethod.getResponseHeader("location");
                /*if (locationHeader != null) {
                    String location = locationHeader.getValue ();
                    if (count == 0) {// 只运行转发向一次，防止死循环
                        count++;
                        return httpPost (location, nameValue, charSet, connTimeOut, soTimeOut);
                    } else {
                        return "FAIL";
                    }
                } else {
                    return "FAIL";
                }*/
            }
        } catch (HttpException e) {
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        } finally {
            postMethod.releaseConnection ();
        }
        return null;
    }
    
    
    public static String httpPostWithXML(String url, String xml) throws Exception {
    	//创建httpclient工具对象 
    	org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
    	//创建post请求方法 
    	PostMethod myPost = new PostMethod(url); 
    	String responseString = null; 
    	try{
	    	//设置请求头部类型 
	    	myPost.setRequestHeader("Content-Type","text/xml"); 
	    	myPost.setRequestHeader("charset","utf-8"); 
	    	myPost.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36");
	
	    	myPost.setRequestEntity(new StringRequestEntity(xml,"text/xml","utf-8")); 
	    	int statusCode = client.executeMethod(myPost); 
	    	if(statusCode == HttpStatus.SC_OK){ 
		    	BufferedInputStream bis = new BufferedInputStream(myPost.getResponseBodyAsStream()); 
		    	byte[] bytes = new byte[1024]; 
		    	ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		    	int count = 0; 
		    	while((count = bis.read(bytes))!= -1){ 
		    		bos.write(bytes, 0, count); 
		    	} 
		    	byte[] strByte = bos.toByteArray(); 
		    	responseString = new String(strByte,0,strByte.length, StandardCharsets.UTF_8);
		    	bos.close(); 
		    	bis.close(); 
	    	} 
    	}catch (Exception e) { 
    		e.printStackTrace(); 
    	} 
    	myPost.releaseConnection(); 
    	return responseString;
    }
    
    
    /**
     * 获取服务器信任
     */
    private static void getTrust() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String httpSendHTTPSPostFrom(String url,JSONObject params) throws IOException {
        Map<String, String> header = new HashMap<>();
        return httpSendHTTPSPostFrom(url,params,header);
    }
    
    /**
     * @描述:模拟from发送HTTPS POST方法的请求
     * @描述:请求参数应该是 name1=value1&name2=value2 的形式
     * @作者:nada
     * @时间:2017年6月28日 下午5:07:32
     */
    public static String httpSendHTTPSPostFrom(String url, JSONObject params,Map<String, String> header) throws IOException {
        HttpURLConnection conn = null;
        StringBuilder result = new StringBuilder();
        getTrust();
        try {
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
//            logger.info("添加{}个参数", num);
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("charset", "UTF-8");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            for(String key:header.keySet()){
                conn.setRequestProperty(key,header.get(key));
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(connectTimeout);
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            for (String line = null; (line = br.readLine()) != null;) {
                result.append((new StringBuilder()).append(line));
            }
            br.close();
        } catch (Exception e) {
        	e.printStackTrace();
            logger.info("模拟form发送 POST 请求出现异常", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result.toString();
    }
    
}
