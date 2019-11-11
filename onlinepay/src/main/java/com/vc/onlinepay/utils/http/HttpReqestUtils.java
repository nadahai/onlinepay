/**
 * Copyright (c) 2013-2016 ShangHai Telemedias Culture Media Co., Ltd
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary of
 * ShangHai Telemedias Culture Media Co., Ltd. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Sun.
 */

package com.vc.onlinepay.utils.http;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Lee on 2017/10/15.
 */
public class HttpReqestUtils {
	
	public enum EHTTPMethod {
        /**请求方法 */
	    GET, POST, DELETE
	}
	
	public enum EHTTPContentType {
        /**ContentType枚举 */
	    TEXT_XML("text/xml"),
	    TEXT_HTML("text/html"),
	    APP_FORM_URLENCODED("application/x-www-form-urlencoded");

	    String value;

	    EHTTPContentType(String value) {
	        this.value = value;
	    }

	    public String getValue() {
	        return value;
	    }

	    public void setValue(String value) {
	        this.value = value;
	    }
	}

    /**
     * http请求
     *
     * @param urlStr  请求url
     * @param typeStr 请求类型 POST, GET, DELETE
     * @return
     * @throws IOException
     */
    public static String sendRequest(String urlStr, String typeStr) throws IOException {
        return sendRequest(urlStr, typeStr, null);
    }

    /**
     * http请求
     *
     * @param urlStr   请求url
     * @param typeStr  请求类型 POST, GET, DELETE
     * @param paramStr 请求参数字符串 如name=wang&pwd=123
     * @return
     * @throws IOException
     */
    public static String sendRequest(String urlStr, String typeStr, String paramStr) throws IOException {
        return sendRequest(urlStr, typeStr, paramStr, EHTTPContentType.TEXT_XML);
    }

    /**
     * http请求
     *
     * @param urlStr   请求url
     * @param paramStr 请求参数字符串 如name=wang&pwd=123
     * @return
     * @throws IOException
     */
    public static String postRequestByFormUrl(String urlStr, String paramStr) throws IOException {
        return postRequestByFormUrl(urlStr, paramStr, "UTF-8", "UTF-8");
    }

    /**
     * http请求
     *
     * @param urlStr   请求url
     * @param paramStr 请求参数字符串 如name=wang&pwd=123
     * @return
     * @throws IOException
     */
    public static String postRequestByFormUrl(String urlStr, String paramStr, String decoding, String encoding) throws IOException {
        return sendRequest(urlStr, EHTTPMethod.POST.name(), paramStr, EHTTPContentType.APP_FORM_URLENCODED, decoding, encoding);
    }

    /**
     * http请求
     *
     * @param urlStr   请求url
     * @param typeStr  请求类型 POST, GET, DELETE
     * @param paramStr 请求参数字符串 如name=wang&pwd=123
     * @return
     * @throws IOException
     */
    public static String sendRequest(String urlStr, String typeStr, String paramStr, EHTTPContentType contentType)
            throws IOException {
        return sendRequest(urlStr, typeStr, paramStr, contentType, "UTF-8", "UTF-8");
    }

    /**
     * @param urlStr
     * @param typeStr
     * @param paramStr
     * @param contentType
     * @param decoding
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String sendRequest(String urlStr, String typeStr, String paramStr, EHTTPContentType contentType,
                                     String decoding, String encoding) throws IOException {

        URL url = new URL(urlStr);
        // 打开url连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(typeStr.toUpperCase());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setAllowUserInteraction(true);
        // 输入参数
        contentType = contentType == null ? EHTTPContentType.APP_FORM_URLENCODED : contentType;
        connection.setRequestProperty("Content-Type", contentType.getValue());
        if (StringUtils.isNotBlank(paramStr)) {
            connection.getOutputStream().write(paramStr.getBytes(decoding));
        }else{
            connection.getOutputStream().write("0".getBytes(decoding));
        }
        // 发送请求
        return convertStreamToString(connection.getInputStream(), decoding, encoding);
    }

    public static String convertStreamToString(InputStream is) {
        return convertStreamToString(is, "UTF-8", "UTF-8");
    }

    public static String convertStreamToString(InputStream is, String decoding, String encoding) {
        StringBuilder buffer = new StringBuilder();
        byte[] bytes = new byte[4096];
        int size = 0;
        try {
            while ((size = is.read(bytes)) > 0) {
                String str = new String(bytes, 0, size, decoding);
                buffer.append(str);
            }
            byte[] allBytes = buffer.toString().getBytes(decoding);
            return new String(allBytes, encoding);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    public static String readData(HttpServletRequest servletRequest) {
        if (servletRequest == null) {
            return null;
        }
        try {
            InputStream inputStream = servletRequest.getInputStream();
            String data = HttpReqestUtils.convertStreamToString(inputStream);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }
}
