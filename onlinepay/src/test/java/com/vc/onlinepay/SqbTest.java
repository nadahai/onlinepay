package com.vc.onlinepay;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.Md5CoreUtil;

import java.util.Date;

/**
 * @author nada
 * @description
 * @time 2019-04-13 22:18
 */
public class SqbTest {
    static String API_URL="https://mapi.shouqianba.com/V2/";
    static String PAY_URL="https://qr.shouqianba.com/gateway";

    public static void main(String[] args) {
        Date beginDate = new Date();
        String terminalInfo = getTerminalInfo("13155423101","lx041700");
        // payWay  1:支付宝 3:微信 4:百付宝 5:京东钱包 6:QQ钱包 18:翼支付
        System.out.println(preCreate(terminalInfo, RandomUtil.randomString(16), "1", "3"));
        long between = DateUtil.between(beginDate, new Date(), DateUnit.SECOND);
        StaticLog.info("收钱吧2.0下单总耗时：{}s",between);
    }

    private static String getTerminalInfo(String username, String password){
        String mobile="17162086606";
        String pwd="q123456";
        String LOGIN_URL="https://smzf.yjpal.com/payQRCode/loginCheck.htm?mobileNo=MOBILE_NO&password=PASSWORD";
        pwd = DigestUtil.md5Hex(pwd+"superpw1234_!QAZ");
        //String url = LOGIN_URL.replace("MOBILE_NO",mobile).replace("PASSWORD",pwd);
        String url=API_URL+"Account/login";
        JSONObject dataJson = new JSONObject();
        dataJson.put("mobileNo",mobile);
        dataJson.put("password",pwd);
        String result = HttpUtil.post(url, dataJson);
        return result;
    }

    /*private static String getTerminalInfo(String username, String password){
        // 1 登录接口 获得 获取设备接口的token
        String loginUrl=API_URL+"Account/login";
        JSONObject dataJson = new JSONObject();
        dataJson.put("client_version","4.3.5");
        dataJson.put("device_model","1");
        dataJson.put("os_type","1");
        dataJson.put("username",username);
        dataJson.put("password", DigestUtil.md5Hex((password)));
        String loginResult = HttpUtil.post(loginUrl, dataJson);
        JSONObject loginResultJson = JSONObject.parseObject(loginResult);
        // 2 获取设备
        String terminalUrl=API_URL+"Terminal/getTerminal";
        dataJson.remove("username");
        dataJson.remove("password");
        String wosaiStoreId = loginResultJson.getJSONObject("data").getJSONObject("store").getString("sn");
        String token = loginResultJson.getJSONObject("data").getJSONObject("admin").getString("newToken");
        dataJson.put("deviceId",username);
        dataJson.put("wosaiStoreId",wosaiStoreId);
        dataJson.put("token",token);
        String terminalResult = HttpUtil.post(terminalUrl, dataJson);
        JSONObject terminalResultJson = JSONObject.parseObject(terminalResult);
        String terminalSn = terminalResultJson.getJSONObject("data").getString("sn");
        String terminalKey = terminalResultJson.getJSONObject("data").getString("current_secret");
        StaticLog.info("收钱吧2.0获取设备信息返回：{};{}",terminalSn,terminalKey);
        return terminalSn+";"+terminalKey+";"+username;
    }*/

    private static String preCreate(String terminalInfo,String orderNo,String amount,String payWay){
        JSONObject params = new JSONObject();
        try{
            params.put("terminal_sn",terminalInfo.split(";")[0]);           //收钱吧终端ID
            params.put("client_sn", orderNo);  //商户系统订单号,必须在商户系统内唯一；且长度不超过32字节
            params.put("total_amount",amount);               //交易总金额
            params.put("subject","goods");	                 //交易简介
            params.put("payway",payWay);	                     //支付方式
            params.put("operator",terminalInfo.split(";")[2]);	                 //门店操作员
            params.put("notify_url","https://www.shouqianba.com");	                 //异步通知
            params.put("return_url","https://www.shouqianba.com");	                 //同步通知
            String param = Md5CoreUtil.getSignStr(params)+"&key="+terminalInfo.split(";")[1];
            String sign = DigestUtil.md5Hex(param);
            param = Md5CoreUtil.getSignStr(params)+"&sign="+sign.toUpperCase();
            System.out.printf("收钱吧下单参数：%s\n",param);
            return  PAY_URL+"?"+param;
        }catch (Exception e){
            return null;
        }
    }
}