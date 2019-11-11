package com.vc.onlinepay.utils.honour;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @描述:
 * @时间:2018/6/20 16:20
 * @作者:ChaiJing
 * @版权:微辰金服有限公司 Copyright (c) 2017
 */
public class EncryptUtil {
    private static Logger logger = LoggerFactory.getLogger(EncryptUtil.class);
    //商户编号
    public static final String ORG_NO = "10000000049";
    //上游公钥
    private static final String PUBLICKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApz0RQaGwBzFUvQm/YC9cvu7wfJ/74nVf8Nby+BXw2+yzMx8Cbe8vHT8Mua+srnOwb27Sa7tQTH8R3H/L7TFppjqaLH3w8Eb6PEwz4zviYkJMycO4El9vKdfMEMJ2NhSzPi4THf7XsEjgEHqgkIAQKdnJA58JSIs8vSR6aBpcdziSwPDsfsi1QW0g/v1zk36c49VmXusyjo72z8XZIdXDWKw1O+ImaixCAvC7Bs64I6auIDnsd7yoxYoXaO8EoMaf2HDTvvOKFu1NEPhBYryL7oM5x0v22rZb754609LOb8AJpYQYKdoi6XpQBj2uTEO+OY3W3fLT+epxo8CsYnCPVwIDAQAB";
    //我方私钥
    private static final String PRIVATEKEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJMkSV2kSQFeABphVbEFgBzd6/ncSkn30oy2iImVZrJym/rVHgueseafVl+N36jib9tiasZN2OBMK9QUjAxTutffrQ/uorMKaipDohNVnMCr1l+7SG6pQRhJN5MA/1fao+0LlnRDkzLW/GECHUPwhuur/tCJPPRqZFNfuYOiObQdAgMBAAECgYByP/+zdSe2pw1/bkqCPFogj/R1uyX3zzSuS+Eyq03li3YvyqEctqk+AESpx1h+IuxltUJhHfsjFiIF97pSrATsAOMYP8UR+E9AcnxeKlJiSTn3IT4vJGnrlouu2jjUng61omMsNyis7hqzaQRtcXqd3K2EwFGtgJLE1KxNlonZRQJBANce1mgypKLK1NHA2SAowViMSrmMLoeFpHlQ45uG/cNve/PLvDwr3NEpqKwN9cnx+TzVB1ArQugq62nAwolnIvcCQQCvGm0WBh61IU8/jt7jF4TbzsV9WC1DcB8ykBqWTiUKuhhqU9Be/qKxSxAssAuuCNqXjaEWoFyIb5SL0nV4bgiLAkBhy/EpWayjHZb27LdI+X48pTrrc6FLlyQYyv9Om2YOTUaKpRqkOEShClFWNtqAvnLShv5WIfS+25Q+dZV7JdotAkBpRcD5sbo6eYk0lAeqjhRBNmIDRsUHu1v2QS7K2LZqaZGOM0+eHQbhAlj6FOkgu4/39502hji7iiHLoj8fpT+bAkEAh5vyYpO8QEX23wpVgX4VCmCumNOdXOKsneq8GaPYM2YSe7NwWOi/Ki6Q2t2KGtW3KCRgEbkbbUGguzag/r8mDA==";

    private static final String API_URL = "http://trx.fjyunlu.com/trx/bankPay";

    public static String getPUBLICKEY(String merchantCode) {
        return PUBLICKEY;
    }
    public static String getPRIVATEKEY(String merchantCode) {
        return PRIVATEKEY;
    }
    public static String getApiUrl(String merchantCode) {
        return API_URL;
    }
    //加密发送报文
    public static JSONObject encryptHttp(JSONObject contextjson, String merchantCode,String apikey){
        try {
            String url = getApiUrl(merchantCode);
            logger.info("荣耀代付请求Url:{},请求报文:{}",url,contextjson);
            byte[] plainBytes = contextjson.toString().getBytes("utf-8");
            //生成AES秘钥16位
            byte[] keyBytes = generateLenString(16).getBytes("utf-8");
			/*AES密钥加密请求报文*/
            String encryptData = Base64.encodeBase64String(Key.jdkAES(plainBytes, keyBytes));
			/*RSA公钥加密AES密钥*/
            String encrtptKey = Base64.encodeBase64String(Key.jdkRSA(keyBytes, getPUBLICKEY(merchantCode)));
			/*RSA密钥签名请求报文*/
            String signData = Base64.encodeBase64String(Key.rsaSign(plainBytes, getPRIVATEKEY(merchantCode)));
            List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
            nvps.add(new BasicNameValuePair("context",encryptData));
            nvps.add(new BasicNameValuePair("encrtpKey",encrtptKey));
            nvps.add(new BasicNameValuePair("signData",signData));
            nvps.add(new BasicNameValuePair("orgNo", merchantCode));
            //发送请求
            String respStr = HttpClientTools.httpClientSendPost(url,nvps,"UTF-8");
            if(respStr==null){
                return null;
            }
			/*处理返回数据*/
            JSONObject jsonObject = JSONObject.parseObject(respStr);
            if (null==jsonObject.get("respCode")) {
                jsonObject = decode(jsonObject,merchantCode);
            }
            logger.info("荣耀代付返回报文:{}",jsonObject);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //生成 AES秘钥
    public static String generateLenString(int length) {
        char[] cResult = new char[length];
        int[] flag = { 0, 0, 0 }; // A-Z, a-z, 0-9
        int i = 0;
        while (flag[0] == 0 || flag[1] == 0 || flag[2] == 0 || i < length) {
            i = i % length;
            int f = (int) (Math.random() * 3 % 3);
            if (f == 0) {
                cResult[i] = (char) ('A' + Math.random() * 26);
            }else if (f == 1) {
                cResult[i] = (char) ('a' + Math.random() * 26);
            }else {
                cResult[i] = (char) ('0' + Math.random() * 10);
            }
            flag[f] = 1;
            i++;
        }
        return new String(cResult);
    }
    //解析返回参数
    public static JSONObject decode(JSONObject jsonObject,String merchantCode) {
        try {
            /*取出用RSA加密的AES*/
            String resEncryptKey = jsonObject.getString("encrtpKey");
            byte[] KeyBytes = Base64.decodeBase64(resEncryptKey.getBytes("UTF-8"));
			/*对加密的AES进行解密*/
            byte[] merchantAESKeyBytes = Key.jdkRSA_(KeyBytes, getPRIVATEKEY(merchantCode));
			/*用解密后的AES密钥解密银行响应报文*/
            String resEncryptData = jsonObject.getString("context");

            byte[] DataBytes = Base64.decodeBase64(resEncryptData.getBytes("UTF-8"));
            byte[] merchantXmlDataBytes = Key.jdkAES_(DataBytes, merchantAESKeyBytes);
            //验证签名
            boolean verify = Key.rsaSign_(merchantXmlDataBytes, getPUBLICKEY(merchantCode), jsonObject.getString("signData"));
            if (!verify) {
                return null;
            }
            String resjson = new String(merchantXmlDataBytes, "utf-8");
            return JSONObject.parseObject(resjson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
