package com.vc.onlinepay;

import cn.hutool.http.HttpUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;

public class Phone {
    private static final String account = "qjhf";
    //private static final String  pwd = "qjhf0425";
    private static  final  String api = "90f8811cb7dc43269652bdbfb346dd48";
    private static  final  String url = "http://120.26.108.216:8080/telapi.aspx";

    public static void main (String[] args) {
        //createRecharge ();
        //getPackageInfo ();
        //getBalance ();
        //3.6.获取状态
        getReports ();
        //queryReport ();
        //(account=qjhf&mobile=15118135523&package=10&key=90f8811cb7dc43269652bdbfb346dd48)=77cf0f8cd43eecf90aee030a88b0b32a
        //action=charge&v=1.1&account=qjhf&mobile=15118135523&package=10&sign=77cf0f8cd43eecf90aee030a88b0b32a
        //{"TaskID":45314297,"Mobile":"15118135523","Status":4,"ReportTime":"2019-04-26 11:07:58","ReportCode":"True:ok","OutTradeNo":"1556248070996","Price":10.0000}
    }

    /**
     * @描述:动状态查询(提交超时后用来确认订单使用)
     * @作者:nada
     * @时间:2019/4/25
     **/
    private static void queryReport () {
        try {
            JSONObject reqData = new JSONObject ();
            reqData.put ("account", account);
            String sign = Md5CoreUtil.getSignStr (reqData) + "&key=" + api;
            reqData.put ("sendTime", "2019-04-26");
            reqData.put ("taskID", "45314297");
            reqData.put ("outTradeNo", "1556248070996");
            StaticLog.info ("签名串:{}", sign);
            reqData.put ("sign", Md5Util.md5 (sign));
            reqData.put ("action", "queryReport");
            reqData.put ("v", "H1.1");
            StaticLog.info ("请求入参:{}", reqData);
            String response = HttpUtil.post (url, reqData);
            StaticLog.info ("响应结果:{}", response);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    /**
     * @描述:获取状态 就是获取订单充值的最终状态  充值成功或者充值失败
     * @作者:nada
     * @时间:2019/4/25
     **/
    private static void getReports () {
        try {
            JSONObject reqData = new JSONObject ();
            reqData.put ("account", account);
            //查询接口   就是你们一次查询多少个订单
            reqData.put ("count", "1");
            String sign = Md5CoreUtil.getSignStr (reqData) + "&key=" + api;
            StaticLog.info ("签名串:{}", sign);
            reqData.put ("sign", Md5Util.md5 (sign));
            reqData.put ("v", "H1.1");
            reqData.put ("action", "getReports");
            StaticLog.info ("请求入参:{}", reqData);
            String response = HttpUtil.post (url, reqData);
            StaticLog.info ("响应结果:{}", response);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    /**
     * @描述:获取话费包定义
     * @作者:nada
     * @时间:2019/4/25
     **/
    private static void getPackageInfo () {
        try {
            JSONObject reqData = new JSONObject ();
            reqData.put ("account", account);
            reqData.put ("type", "1");//0:不指定, 1:移动, 2:联通, 3:电信
            String sign = Md5CoreUtil.getSignStr (reqData) + "&key=" + api;
            StaticLog.info ("签名串:{}", sign);
            reqData.put ("sign", Md5Util.md5 (sign));
            reqData.put ("v", "H1.1");
            reqData.put ("action", "getPackage");
            StaticLog.info ("请求入参:{}", reqData);
            String response = HttpUtil.post (url, reqData);
            StaticLog.info ("响应结果:{}", response);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    /**
     * @描述:单号码充话费
     * @作者:nada
     * @时间:2019/4/25
     **/
    private static void createRecharge () {
        try {
            //{"Code":"0","Message":"充值提交成功","TaskID":45314297}
            String mobile = "15118135523";
            String amount = "10";
            JSONObject reqData = new JSONObject ();
            reqData.put ("account", account);
            reqData.put ("mobile", mobile);
            reqData.put ("package", amount);
            String sign = Md5CoreUtil.getSignStr (reqData) + "&key="+api;
            reqData.put ("v", "H1.1");
            reqData.put ("OutTradeNo",System.currentTimeMillis ());
            reqData.put ("action", "charge");
            reqData.put ("sign", Md5Util.md5 (sign));
            StaticLog.info ("签名串:{}", sign);
            StaticLog.info ("请求入参:{}", reqData);
            String response = HttpUtil.post (url, reqData);
            StaticLog.info ("响应结果:{}", response);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    /**
     * @描述:获取话费包定义
     * @作者:nada
     * @时间:2019/4/25
     **/
    private static void getBalance () {
        try {
            JSONObject reqData = new JSONObject ();
            reqData.put ("account", account);
            String sign = Md5CoreUtil.getSignStr (reqData) + "&key=" + api;
            reqData.put ("sign", Md5Util.md5 (sign));
            reqData.put ("v", "H1.1");
            reqData.put ("action", "getBalance");
            StaticLog.info ("签名串:{}", sign);
            StaticLog.info ("请求入参:{}", reqData);
            String response = HttpUtil.post (url, reqData);
            StaticLog.info ("响应结果:{}", response);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
