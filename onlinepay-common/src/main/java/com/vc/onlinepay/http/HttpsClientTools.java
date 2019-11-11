package com.vc.onlinepay.http;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpsClientTools {
	
	private static Logger logger = LoggerFactory.getLogger(HttpsClientTools.class);


	public static String sendHttpSSL_textjson(JSONObject params, String resquestUrl) {
		return sendHttpSSL(params,resquestUrl,"text/json");
	}
	public static String sendHttpSSL_textjson(String params, String resquestUrl) {
		return sendHttpSSL(params,resquestUrl,"text/json");
	}

	public static String sendHttpSSL_appljson(JSONObject params,String resquestUrl) {
		return sendHttpSSL(params,resquestUrl,"application/json;charset=UTF-8");
	}
	public static String sendHttpSSL_appljson(String params,String resquestUrl) {
		return sendHttpSSL(params,resquestUrl,"application/json;charset=UTF-8");
	}
	 /**
     * SSL发送
     * @param params 参数
     * @return 响应结果
     */
    private static String sendHttpSSL(JSONObject params, String resquestUrl, String contentType) {
		try {
			SslContextUtils sslContextUtils = new SslContextUtils();
			StringBuilder sb = new StringBuilder();
			try {
				URL url = new URL(resquestUrl);
				HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
				if (httpConn instanceof HttpsURLConnection) {
					sslContextUtils.initHttpsConnect((HttpsURLConnection) httpConn);
				}
				httpConn.setRequestProperty("accept", "*/*");
				httpConn.setRequestProperty("connection", "Keep-Alive");
				httpConn.setRequestMethod("POST");
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				httpConn.setRequestProperty("Content-type", contentType);
				httpConn.setConnectTimeout(15000);
				httpConn.setReadTimeout(15000);
				httpConn.getOutputStream().write(params.toString().getBytes(StandardCharsets.UTF_8));
				httpConn.getOutputStream().flush();
				httpConn.getOutputStream().close();
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), StandardCharsets.UTF_8));
				char[] buf = new char[1024];
				int length = 0;
				while ((length = reader.read(buf)) > 0) {
					sb.append(buf, 0, length);
				}
			} catch (IOException e) {
				logger.error("异常",e);
			}
            // 响应参数
            //logger.info("响应：{}",mydata);
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("异常",e);
			return null;
		}
	}
    
    private static String sendHttpSSL(String params, String resquestUrl, String contentType) {
		try {
			SslContextUtils sslContextUtils = new SslContextUtils();
			StringBuilder sb = new StringBuilder();
			try {
				URL url = new URL(resquestUrl);
				HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
				if (httpConn instanceof HttpsURLConnection) {
					sslContextUtils.initHttpsConnect((HttpsURLConnection) httpConn);
				}
				httpConn.setRequestProperty("accept", "*/*");
				httpConn.setRequestProperty("connection", "Keep-Alive");
				httpConn.setRequestMethod("POST");
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				httpConn.setRequestProperty("Content-type", contentType);
				httpConn.setConnectTimeout(15000);
				httpConn.setReadTimeout(15000);
				httpConn.getOutputStream().write(params.toString().getBytes(StandardCharsets.UTF_8));
				httpConn.getOutputStream().flush();
				httpConn.getOutputStream().close();
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), StandardCharsets.UTF_8));
				char[] buf = new char[1024];
				int length = 0;
				while ((length = reader.read(buf)) > 0) {
					sb.append(buf, 0, length);
				}
			} catch (IOException e) {
				logger.error("异常",e);
			}
            // 响应参数
            //logger.info("响应：{}",mydata);
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("异常",e);
			return null;
		}
	}
    
	/**
     * @description: SSL
     * 2017年11月21日 下午12:26:31
     */
    public static class SslContextUtils {
		private TrustManager trustAllManager;
		SSLContext sslcontext;
		HostnameVerifier allHostsValid;

		public SslContextUtils() {
			initContext();
		}

		private void initContext() {
			trustAllManager = new X509TrustManager() {

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1) {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1) {
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

			};
			try {
				sslcontext = SSLContext.getInstance("TLS");
				sslcontext.init(null, new TrustManager[] { trustAllManager },
						null);
			} catch (Exception e) {
				logger.error("异常",e);
			}
			allHostsValid = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
		}
		public void initHttpsConnect(HttpsURLConnection httpsConn) {
			httpsConn.setSSLSocketFactory(sslcontext.getSocketFactory());
			httpsConn.setHostnameVerifier(allHostsValid);
		}
	}
}
