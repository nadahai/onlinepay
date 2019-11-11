package com.vc.onlinepay.utils.gateutils;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class GateUtils {

    public static java.text.SimpleDateFormat yyyyMMddHHmmss = new java.text.SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * @描述: 构建MD5签名
     * @作者:nada
     * @时间:2019/3/15
     **/
    public static String bulidMd5Sign(JSONObject content,String signKey)throws  IllegalArgumentException{
        if(StringUtils.isEmpty(signKey)){
            throw  new IllegalArgumentException ("Check for sign key is Illegals!");
        }
        if(content == null || content.isEmpty ()){
            throw  new IllegalArgumentException ("Check for sign content is Illegals!");
        }
        String source = Md5CoreUtil.getSignStr(content,signKey);
        if(StringUtils.isEmpty(source)){
            throw  new IllegalArgumentException ("Check for sign source is error!");
        }
        return Md5Util.MD5(source);
    }

    /**
     * @描述:获取当前时间戳
     * @作者:nada
     * @时间:2019/3/15
     **/
    public static long getTimestamp(){
        return Long.valueOf (yyyyMMddHHmmss.format(new Date ()));
    }
}
