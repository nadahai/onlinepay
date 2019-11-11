package com.vc.onlinepay.utils.remittance;

import java.io.DataInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class WeiXinDataHelper {

	public final static String UTF8_ENCODE="UTF-8";
	public final static String GBK_ENCODE="GBK";
	
	
	//将获取的map数据转换成字符串拼接
		public static String getQueryString(Map<String , String> map){
			
			Iterator<Entry<String,String>> iter=map.entrySet().iterator();
			StringBuilder sb=new StringBuilder();
			while(iter.hasNext()){
				Entry<String, String> entry=iter.next();
				Object key=entry.getKey();
				Object value=entry.getValue();
				sb.append(key+"="+value+"&");					
			}
			if(sb.length()==0) {
				return "";
			}
			return sb.substring(0,sb.length()-1);
		}
		//对值进行转码，转换成汇付宝的编码格式、
		public static void transferCharsetEncode(Map<String, String> map) throws UnsupportedEncodingException{
			for(Entry<String, String> entry:map.entrySet()){
				if(entry.getValue()==null) {
					continue;
				}
				String utf8=URLEncoder.encode(entry.getValue(),UTF8_ENCODE);
				entry.setValue(utf8);
			}
		}
		//得到排序后的值并转换为小写
		public static String getSortQueryToLowerString(Map<String, String> map){
			List<Map.Entry<String, String>> keyValues=new ArrayList<Map.Entry<String,String>>(map.entrySet());
			Collections.sort(keyValues,new Comparator<Map.Entry<String, String>>() {
				@Override
				public int compare(Map.Entry<String, String> o1, Map.Entry<String,String> o2){
					return (o1.getKey()).toString().compareTo(o2.getKey());
				}
			});
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<keyValues.size();i++){
				if(keyValues.get(i).getValue()==null){
					sb.append(keyValues.get(i).getKey()+"= ");
				}else{
					sb.append(keyValues.get(i).getKey()+"="+keyValues.get(i).getValue().toLowerCase());
				}
				sb.append("&");
			}
			return sb.substring(0,sb.length()-1);
		}
		
		//得到排序后的值并转换为小写
		public static String getSortQueryString(Map<String, String> map){
			List<Map.Entry<String, String>> keyValues=new ArrayList<Map.Entry<String,String>>(map.entrySet());
			Collections.sort(keyValues,new Comparator<Map.Entry<String, String>>() {
				@Override
				public int compare(Map.Entry<String, String> o1, Map.Entry<String,String> o2){
					return (o1.getKey()).toString().compareTo(o2.getKey());
				}
			});
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<keyValues.size();i++){
				sb.append(keyValues.get(i).getKey()+"="+keyValues.get(i).getValue());
				sb.append("&");
			}
			return sb.substring(0,sb.length()-1);
		}
		
		public static String getPostUrl(String postData,String postUrl,String submitMethod){
			URL url=null;
			HttpURLConnection urlConn=null;
			try{
				url=new URL(postUrl);
				urlConn=(HttpURLConnection) url.openConnection();
				urlConn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
				urlConn.setRequestMethod(submitMethod.toUpperCase());
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				if("post".equalsIgnoreCase(submitMethod)){
					urlConn.getOutputStream().write(postData.getBytes(WeiXinDataHelper.GBK_ENCODE));
					urlConn.getOutputStream().flush();
					urlConn.getOutputStream().close();
				}
				int code=urlConn.getResponseCode();
				if(code==200){
					DataInputStream in=new DataInputStream(urlConn.getInputStream());
					int len=in.available();
					byte[] by=new byte[len];
					in.readFully(by);
					String rev=new String(by,GBK_ENCODE);
					in.close();
					return rev;
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(urlConn!=null){
					urlConn.disconnect();
				}
			}
			return null;
		}
	
}













