package com.vc.onlinepay.utils.remittance;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/27.
 */
public class SmallTools {

    /**
     * MD5加密
     * @param str 需要加密的值
     * @return 加密完成的值(小写)
     */
    public static String md5(String str){
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] b = md.digest();

            int i;

            StringBuffer buf = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }

            md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     * Json格式数据验签
     * 传入返回数据和KEY，返回验签结果（验签成功返回true，失败返回false）
     * 如果请求报错，然后的参数中不会有sign字段也就不存在验签问题，直接提示参数中无sign字段无需验签，方法返回false
     */
    public static Boolean checkSign(String parameter, String key){
        try {
            if (!parameter.contains("sign")){
                System.out.println("参数中无sign字段无需验签");
                return false;
            }
            JSONObject jo = new JSONObject(parameter);
            Iterator<String> a = jo.keys();
            a.toString();
            ArrayList<String> keys = new ArrayList<String>();
            while (true){
                String k = a.next();
                if (!"sign".equals(k)){
                    keys.add(k);
                }
                if (!a.hasNext()){
                    break;
                }
            }
            // 进行升序排列
            Collections.sort(keys, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            StringBuilder mSign = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                mSign.append(keys.get(i));
                mSign.append("=");
                mSign.append(jo.get(keys.get(i)));
                mSign.append("&");
            }
            mSign.append("key="+key);
            System.out.println("返回签名参数："+mSign.toString());
            String msign = md5(mSign.toString());
            System.out.println("本地签名结果："+msign);
            String rsign = jo.getString("sign");
            System.out.println("返回签名结果："+rsign);
            if (msign.equals(rsign)){
                System.out.println("签名验证成功");
                return true;
            }else {
                System.out.println("签名验证失败");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * aa=11&bb=22 格式数据转 Json格式数据
     * @throws JSONException
     */
    public static String toJson(String parameter) throws JSONException{
        JSONObject jsobj = new JSONObject();
        if (!parameter.contains("&")){
            if (!parameter.contains("=")){
                System.out.println("参数格式不正确，无法转换");
            }else{
                String[] list = parameter.split("=");
                jsobj.put(list[0],list[1]);
                return jsobj.toString();
            }
        }else {
            String[] strings = parameter.split("&");
            for (int i=0;i<strings.length;i++){
                String[] list = strings[i].split("=");
                jsobj.put(list[0],list[1]);
            }
            return jsobj.toString();
        }
        return "";
    }
}
