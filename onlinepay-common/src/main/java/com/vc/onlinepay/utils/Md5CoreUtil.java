package com.vc.onlinepay.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Md5CoreUtil {
	
	private static Logger logger = LoggerFactory.getLogger(Md5CoreUtil.class);
	
	/**
     * @描述：按照ascii码排序根据MD5加密
     * @作者:nada
     * @时间:2018年3月8日 下午5:11:18
     */
    public static String md5ascii(JSONObject sourceObj,String md5Key) {
        try {
            String text = getSignStr(sourceObj,md5Key);
            String sign = Md5Util.md5(text);
            logger.info ("加密前参数{}加密后sign{}",text,sign);
            return sign;
        } catch (Exception e) {
            logger.error("根据字母排序验签异常",e);
            return "";
        }
    }

    public static String getSignStr(JSONObject sourceObj,String md5Key) {
        try {
            String signStr = getSignStr(sourceObj) + "&key=" + md5Key;
            return signStr;
        } catch (Exception e) {
            logger.error("根据字母排序验签异常",e);
            return "";
        }
    }
    
    /**
     * @描述:拼接签名串 不去除空值
     * @作者:ChaiJing THINK
     * @时间:2018/4/23 16:28
     */
    public static String getSignStrNoNull(JSONObject jsonData){
        String signStr = "";
        try {
            SortedMap<String, Object> sortedMap = new TreeMap<String, Object>();
            for (Object key : jsonData.keySet()) {
                sortedMap.put(key.toString(), jsonData.get(key));
            }
            return getSignStrNoNull(sortedMap);
        } catch (Exception e) {
            logger.error("根据字母排序验签异常",e);
            return signStr;
        }
    }

    /**
     * @描述:拼接签名串
     * @作者:ChaiJing THINK
     * @时间:2018/4/23 16:28
     */
    public static String getSignStr(JSONObject jsonData){
        String signStr = "";
        try {
            SortedMap<String, Object> sortedMap = new TreeMap<String, Object>();
            for (Object key : jsonData.keySet()) {
                sortedMap.put(key.toString(), jsonData.get(key));
            }
            return getSignStr(sortedMap);
        } catch (Exception e) {
            logger.error("根据字母排序验签异常",e);
            return signStr;
        }
    }
    public static String getSignStr(Map<String,String> map){
        String signStr = "";
        try {
            SortedMap<String, Object> sortedMap = new TreeMap<String, Object>();
            for (String key : map.keySet()) {
                sortedMap.put(key, map.get(key));
            }
            return getSignStr(sortedMap);
        } catch (Exception e) {
            logger.error("根据字母排序验签异常",e);
            return signStr;
        }
    }

    public static String getlinkSignStr(JSONObject jsonData,String linkStr){
        String signStr = "";
        try {
            SortedMap<String, Object> sortedMap = new TreeMap<String, Object>();
            for (Object key : jsonData.keySet()) {
                sortedMap.put(key.toString(), jsonData.get(key));
            }
            StringBuilder buffer = new StringBuilder();
            for (String key : sortedMap.keySet()){
                if(!sortedMap.containsKey(key) || sortedMap.get(key) == null || "".equals(sortedMap.get(key))){
                    continue;
                }
                buffer.append(key).append("=").append(sortedMap.get(key).toString());
                if(StringUtil.isNotEmpty(linkStr)){
                    buffer .append("&");
                }
            }
            signStr = buffer.toString();
            return signStr.substring(0,signStr.length()-1);
        } catch (Exception e) {
            logger.error("根据字母排序验签异常",e);
            return signStr;
        }
    }

    public static String getSignStr(SortedMap<String, Object> sortedMap){
        String signStr = "";
        try {
            StringBuilder buffer = new StringBuilder();
            for (String key : sortedMap.keySet()){
                if(!sortedMap.containsKey(key) || sortedMap.get(key) == null || "".equals(sortedMap.get(key))){
                    continue;
                }
                if("sign".equalsIgnoreCase(key) || "signature".equalsIgnoreCase(key) || "signData".equalsIgnoreCase(key)){
                    continue;
                }
                if("amount".equalsIgnoreCase(key)){
                    buffer.append(key).append("=").append(sortedMap.get(key).toString()).append("&");
                    continue;
                }
                buffer.append(key).append("=").append(sortedMap.get(key).toString()).append("&");
            }
            signStr = buffer.toString();
            return signStr.substring(0,signStr.length()-1);
        } catch (Exception e) {
            logger.error("根据字母排序验签异常",e);
            return signStr;
        }
    }
    
    //不去除空字符串
    
    public static String getSignStrNoNull(SortedMap<String, Object> sortedMap){
        String signStr = "";
        try {
            StringBuilder buffer = new StringBuilder();
            for (String key : sortedMap.keySet()){
                
                if("sign".equalsIgnoreCase(key) || "signature".equalsIgnoreCase(key) || "signData".equalsIgnoreCase(key)){
                    continue;
                }
                if("amount".equalsIgnoreCase(key)){
                    buffer.append(key).append("=").append(sortedMap.get(key).toString()).append("&");
                    continue;
                }
                buffer.append(key).append("=").append(sortedMap.get(key).toString()).append("&");
            }
            signStr = buffer.toString();
            return signStr.substring(0,signStr.length()-1);
        } catch (Exception e) {
            logger.error("根据字母排序验签异常",e);
            return signStr;
        }
    }
    
    /**
     * @描述:参数签名 不包含参数名 参数直接拼接
     * @作者:ChaiJing THINK
     * @时间:2018/4/16 11:09
     */
    public static String getRequestSign (JSONObject jsonPrams, String signKey) throws Exception {
        SortedMap<String, String> sortedMap = new TreeMap<String, String>();
        for (String key : jsonPrams.keySet()) {
            sortedMap.put(key, jsonPrams.getString(key));
        }
        StringBuilder builder = new StringBuilder();
        for (String key : sortedMap.keySet()){
            builder.append(sortedMap.get(key));
        }
        builder.append(signKey);
        String signStr = builder.toString();
        System.out.println("signStr = " + signStr);
        return Md5Util.md5(signStr);
    }
}
