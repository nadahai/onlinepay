package com.vc.onlinepay.utils.http;

import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Description: httpClient工具类
 *
 * @author shadow
 * @date Created on 2018年4月19日
 */
public class HttpClientUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtils.class);

    // 编码格式。发送编码格式统一用UTF-8
    private static final String ENCODING = "UTF-8";

    // 设置连接超时时间，单位毫秒。
    private static final int CONNECT_TIMEOUT = 6000;

    /** 请求获取数据的超时时间(即响应时间)，单位毫秒。 **/
    private static final int SOCKET_TIMEOUT = 6000;

    /**配置超时**/
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();

    /**static修饰的属性强调它们只有一个，final修饰的属性表明是一个常数（创建后不能被修改）。static final修饰的属性表示一旦给值，就不可修改，并且可以通过类名访问**/
    /** 创建httpClient对象 **/
    private static CloseableHttpClient httpClient;

    static {
        try {
            // 忽略证书
            SSLContextBuilder sslBuilder = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy());
            //不进行主机名验证
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslBuilder.build(), NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslConnectionSocketFactory)
                    .build();
            PoolingHttpClientConnectionManager poolManager = new PoolingHttpClientConnectionManager(registry);
            poolManager.setMaxTotal(100);
            httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .setDefaultCookieStore(new BasicCookieStore())
                    .setConnectionManager(poolManager).build();
        } catch (Exception e) {
            LOG.error("HttpClient配置异常:",e);
            httpClient = HttpClients.custom().build();
        }
    }


    /**
     * 发送get请求；不带请求头和请求参数
     *
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static HttpClientResult doGet(String url){
        return doGet(url, null, null);
    }

    /**
     * 发送get请求；带请求参数
     *
     * @param url    请求地址
     * @param params 请求参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doGet(String url, Map<String, String> params) throws Exception {
        return doGet(url, null, params);
    }

    /**
     * 发送get请求；带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @param params  请求参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doGet(String url, Map<String, String> headers, Map<String, String> params){
        /*
        // 创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        */
        /**
         * setConnectTimeout：设置连接超时时间，单位毫秒。
         * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
         * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
         * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
         */
        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;
        HttpGet httpGet = null;
        try {
            // 创建访问的地址
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            // 创建http对象
            httpGet = new HttpGet(uriBuilder.build());

            httpGet.setConfig(REQUEST_CONFIG);
            // 设置请求头
            packageHeader(headers, httpGet);
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpGet);
        }catch (Exception e){
            LOG.error("GET 请求出错啦~",e);
            return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }finally {
            // 释放资源
            // release2(httpResponse, httpClient);
            release2(httpGet, httpResponse);
        }
    }

    /**
     *  发送Post请求,表单形式
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPostForm(String url) throws Exception {
        return doPostFormMap(url, null, null);
    }
    public static <M extends Map<String,String>> HttpClientResult doPostFormMap(String url, M params){
        return doPostFormMap(url, null, params);
    }
    public static HttpClientResult doPostFormStr(String url, String params) throws Exception {
        return doPostFormStr(url, null,params);
    }
    public static <M extends Map<String,String>> HttpClientResult doPostFormMap(String url, Map<String, String> headers, M params) {
        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        // 请求设置
        httpPost.setConfig(REQUEST_CONFIG);
        // 设置请求头
        packageFormHeader(headers, httpPost);
        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;
        try {
        	LOG.info("postForm请求参数{}",params);
            // 封装请求参数
            packageParam(params, httpPost);
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpPost);
        }catch (Exception e){
            LOG.error("请求出错啦~",e);
            return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }finally {
            // 释放资源
            release(httpPost, httpResponse);
        }
    }

    public static HttpClientResult doPostFormStr(String url, Map<String, String> headers, String params) throws Exception {
        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        // 请求设置
        httpPost.setConfig(REQUEST_CONFIG);
        // 设置请求头
        packageFormHeader(headers, httpPost);
        // 封装请求参数
        packageFormParam(params, httpPost);
        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;
        try {
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpPost);
        } finally {
            // 释放资源
            release(httpPost, httpResponse);
        }
    }

    /**
     * 发送post请求；不带请求头和请求参数
     *
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPostJson(String url) throws Exception {
        return doPostJsonMap(url, null, null);
    }

    /**
     * 发送post请求；带请求参数
     *
     * @param url    请求地址
     * @param params 参数集合
     * @return
     * @throws Exception
     */
    public static <M extends Map> HttpClientResult doPostJsonMap(String url, M params)  {
        return doPostJsonMap(url, null, params);
    }

    /**
     *  发送Post请求；reqJson 形式
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPostJsonStr(String url, String params){
        return doPostJsonStr(url, null,params);
    }

    /**
     * 发送post请求；带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @param params  请求参数集合
     * @return
     * @throws Exception
     */
    public static <M extends Map<String,String>> HttpClientResult doPostJsonMap(String url, Map<String, String> headers, M params){

        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        /**
         * setConnectTimeout：设置连接超时时间，单位毫秒。
         * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
         * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
         * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
         */
        httpPost.setConfig(REQUEST_CONFIG);
        // 设置请求头
        packageHeader(headers, httpPost);
        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;
        try {
            // 封装请求参数
            packageParam(params, httpPost);
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpPost);
        }catch (Exception e){
            LOG.error("请求出错啦: {}",params,e);
            return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // 释放资源
            release(httpPost, httpResponse);
        }
    }

    /**
     *  发送Post请求
     * @param url
     * @param headers
     * @param jsonParams
     * @param <M>
     * @return
     * @throws Exception
     */
    public static <M extends Map> HttpClientResult doPostJsonStr(String url,M headers, String jsonParams) {
        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        // 设置请求参数
        httpPost.setConfig(REQUEST_CONFIG);
        // 设置请求头
        packageHeader(headers, httpPost);
        // 封装请求参数
        packageJsonParam(jsonParams, httpPost);
        // 创建httpResponse对象
        CloseableHttpResponse httpResponse = null;
        try {
            // 执行请求并获得响应结果
            return getHttpClientResult(httpResponse, httpClient, httpPost);
        }catch (Exception e){
            LOG.error("请求出错啦: {}",jsonParams,e);
            return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }finally {
            // 释放资源
            release(httpPost, httpResponse);
        }
    }

    /**
     * 发送put请求；不带请求参数
     *
     * @param url 请求地址
     * @param
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPut(String url) throws Exception {
        return doPut(url);
    }

    /**
     * 发送put请求；带请求参数
     *
     * @param url    请求地址
     * @param params 参数集合
     * @return
     * @throws Exception
     */
    /*
    public static HttpClientResult doPut(String url, Map<String, String> params) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpPut.setConfig(requestConfig);

        packageParam(params, httpPut);

        CloseableHttpResponse httpResponse = null;

        try {
            return getHttpClientResult(httpResponse, httpClient, httpPut);
        } finally {
            release2(httpResponse, httpClient);
        }
    }
    */

    /**
     * 发送delete请求；不带请求参数
     *
     * @param url 请求地址
     * @param
     * @return
     * @throws Exception
     */
    /*
    public static HttpClientResult doDelete(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpDelete.setConfig(requestConfig);

        CloseableHttpResponse httpResponse = null;
        try {
            return getHttpClientResult(httpResponse, httpClient, httpDelete);
        } finally {
            release2(httpResponse, httpClient);
        }
    }
    */

    /**
     * 发送delete请求；带请求参数
     *
     * @param url    请求地址
     * @param params 参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doDelete(String url, Map<String, String> params) throws Exception {
        if (params == null) {
            params = new HashMap<String, String>();
        }

        params.put("_method", "delete");
        return doPostJsonMap(url, params);
    }

    /**
     * Description: 封装请求头
     *
     * @param params
     * @param httpMethod
     */
    public static void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
        /*
        httpPost.setHeader("Cookie", "");
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
		httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
		*/
        // 封装请求头
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                httpMethod.setHeader(entry.getKey(), entry.getValue());
            }
        }else{
            httpMethod.setHeader("Connection", "keep-alive");
            httpMethod.setHeader("CONTENT-TYPE","application/json; charset=UTF-8;");
            httpMethod.setHeader("User-Agent","Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Mobile Safari/537.36");
        }
    }

    public static <M extends Map<String,String>> void packageFormHeader(M params, HttpRequestBase httpMethod) {
        // 封装请求头
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                httpMethod.setHeader(entry.getKey(), entry.getValue());
            }
        }else{
            httpMethod.setHeader("Connection", "keep-alive");
            httpMethod.setHeader("CONTENT-TYPE","application/x-www-form-urlencoded; charset=UTF-8;");
            httpMethod.setHeader("User-Agent","Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Mobile Safari/537.36");
        }
    }


    /**
     * Description: 封装请求参数
     *
     * @param params
     * @param httpMethod
     * @throws UnsupportedEncodingException
     */
    public static void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod)
            throws UnsupportedEncodingException {
        // 封装请求参数
        if (params != null) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            // 设置到请求的http对象中
            httpMethod.setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
        }
    }

    /**
     * 字符串请求参数封装
     * @param jsonParams
     * @param httpMethod
     * @throws UnsupportedEncodingException
     */
    public static void packageJsonParam(String jsonParams, HttpEntityEnclosingRequestBase httpMethod){
        // 封装请求参数
        if (jsonParams != null) {
            // 设置到请求的http对象中
            httpMethod.setEntity(new ByteArrayEntity(jsonParams.getBytes(Consts.UTF_8)));
        }
    }

    public static void packageFormParam(String jsonParams, HttpEntityEnclosingRequestBase httpMethod){
        // 封装请求参数
        if (jsonParams != null) {
            // 设置到请求的http对象中
            httpMethod.setEntity(new ByteArrayEntity(jsonParams.getBytes(Consts.UTF_8)));
        }
    }

    /**
     * Description: 获得响应结果
     * @param httpResponse
     * @param httpClient
     * @param httpMethod
     * @return
     * @throws Exception
     */
    public static HttpClientResult getHttpClientResult(CloseableHttpResponse httpResponse,
                                                       CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
        // 执行请求
        httpResponse = httpClient.execute(httpMethod);
        // 获取返回结果
        if (httpResponse != null && httpResponse.getStatusLine() != null) {
            String content = null;
            // Header[] headers = null;
            if (httpResponse.getEntity() != null) {
                // headers = httpResponse.getAllHeaders();
                content = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
            }
            return new HttpClientResult(httpResponse.getStatusLine().getStatusCode(),httpResponse.getAllHeaders(),content);
        }
        return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Description: 释放资源
     *
     * @param httpResponse
     * @param httpGet
     * @throws
     */
    public static void release2(HttpGet httpGet, CloseableHttpResponse httpResponse){
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }catch (Exception e){
            LOG.error("请求释放出错啦: {}",e);
        }
    }
    /*
    public static void release2(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient){
        // 释放资源
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        }catch (Exception e){
            LOG.error("释放资源出错啦~",e);
        }
    }
    */

    /**
     * 释放资源
     *
     * @param httpResponse
     * @param httpPost
     * @throws IOException
     */
    public static void release(HttpPost httpPost, CloseableHttpResponse httpResponse){
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }catch (Exception e){
            LOG.error("请求释放出错啦: {}",e);
        }
    }

}