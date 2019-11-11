
package com.vc.onlinepay.utils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Set;

/**
 * MD5工具类
 * 
 */
public class Md5Util {
	
	public static final Logger logger = LoggerFactory.getLogger(Md5Util.class);

   //0的ASCII�?
    private static final int ASCII_0=48;
    //9的ASCII�?
    private static final int ASCII_9=57;
    //A的ASCII�?
    private static final int ASCII_A=65;
    //F的ASCII�?
    private static final int ASCII_F=70;
    //a的ASCII�?
    private static final int ASCII_a=97;
    //f的ASCII�?
    private static final int ASCII_f=102;

    private static final String HASH_MD5 = "MD5";

    /** 签名属性名 sign **/
    private static final String SIGN_KEY = "sign";

    /** 密钥属性名key**/
    private static final String SECRET_KEY = "key";

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f'};

    /** 字符串MD5加密，结果大写**/
    public static String MD5(String sourceStr) {
        return encode2MD5(sourceStr,StandardCharsets.UTF_8).toUpperCase();
    }
    /** 字符串MD5加密，结果小写**/
    public static String md5(String sourceStr) {
        return encode2MD5(sourceStr,StandardCharsets.UTF_8).toLowerCase();
    }
    /**
     * 字符串MD5加密
     * @param sourceStr 加密原始串
     * @param charset 字符集
     * @return String 结果小写
     */
    private static String encode2MD5(String sourceStr, Charset charset) {
        try {
            if(StringUtils.isEmpty(sourceStr)){
                return "";
            }
            byte[] btInput = null;
            if(null == charset){
                btInput = sourceStr.getBytes();
            }else{
                btInput = sourceStr.getBytes(charset);
            }
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance(HASH_MD5);
            // 使用指定的字节更新摘要 获得密文
            byte[] md = mdInst.digest(btInput);
            // 把密文转换成十六进制的字符串形式
            return toHexString(md);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取字符串MD5
     */
    public static String encoding(String text) {
        if( text==null ){
            return null;
        }
        try {           
            return encoding( text.getBytes(StandardCharsets.UTF_8) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取字节数组MD5
     */
    public static String encoding(byte[] bs) {
        String encodingStr = null;
        try {
            MessageDigest mdTemp = MessageDigest.getInstance(HASH_MD5);
            mdTemp.update(bs);
            return toHexString( mdTemp.digest() );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodingStr;
    }
    /**
     * 获取文件内容MD5
     */
    public static String encodingFile(String filePath) throws IOException {
         InputStream fis=null;
         try{
             fis = new FileInputStream(filePath);
             return encoding(fis);
         }catch( Exception ee){
             return null;
         }finally{
             if(fis!=null ){
                 fis.close();
             }
         }
    }
    
    /**
     * 获取输入流MD5
     */
    public static String encoding(InputStream fis) throws Exception{
         byte[] buffer = new byte[1024];  
         MessageDigest md5 = MessageDigest.getInstance(HASH_MD5);  
         int numRead = 0;  
         while ((numRead = fis.read(buffer)) > 0) {  
             md5.update(buffer, 0, numRead);  
         }
         return toHexString(md5.digest());
    }
    
    /**
     * 判断是否是合法的MD5
     * @param md5Str
     * @return
     */
    public static boolean validate(String md5Str){
        if(md5Str==null || md5Str.length()!=32 ){
            return false;
        }
        byte[] by = md5Str.getBytes();
        for (byte b : by) {
            int asciiValue = (int) b;
            if (asciiValue < ASCII_0
                    || (asciiValue > ASCII_9 && asciiValue < ASCII_A)
                    || (asciiValue > ASCII_F && asciiValue < ASCII_a)
                    || asciiValue > ASCII_f) {
                return false;
            }
        }
        return true;
    }

    /**
     * 转换为用16进制字符表示的MD5
     * @param b byte[]
     * @return 大写的MD5
     */
    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte b1 : b) {
            sb.append(HEX_CHAR[(b1 & 0xf0) >>> 4]);
            sb.append(HEX_CHAR[b1 & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 计算签名
     * @param jsonObj 要参与签名的json数据
     * @param md5Key  密钥
     * @return 签名
     */
    public static String getSign(JSONObject jsonObj, String md5Key) {
        if (jsonObj == null || jsonObj.isEmpty()) {
            return null;
        }
        String str2Sign = buildParam4Sign(jsonObj, SIGN_KEY, md5Key);
        System.err.println("str2Sign:"+str2Sign);
        return DigestUtils.md5Hex(str2Sign).toUpperCase();
    }
    /**
     * 拼接用于签名的参数
     * @param jsonObj
     * @return
     */
    private static String buildParam4Sign(JSONObject jsonObj, String signKey, String md5Key) {
        Set<String> keySet = jsonObj.keySet();
        StringBuilder param = new StringBuilder(20 * keySet.size());
        String[] keys = keySet.toArray(new String[0]);
        Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);
        for (String key : keys) {
            // 排除sign
            if (signKey.equals(key)) {
                continue;
            }
            Object value = jsonObj.get(key);
            // 排除值为null的情况
            if (value != null) {
                param.append(key).append("=").append(value).append("&");
            }
        }
        param.append(SECRET_KEY).append("=").append(md5Key);
        return param.toString();
    }


}
