package com.vc.onlinepay;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.order.scan.AnHuiShouQianBaScanServiceImpl;
import com.vc.onlinepay.utils.http.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
    private static Logger logger = LoggerFactory.getLogger (Test.class);
    /** 登录 */
    private static String LOGIN_URL="https://smzf.yjpal.com/payQRCode/loginCheck.htm?mobileNo=MOBILE_NO&password=PASSWORD";
    /** 下单 */
    private static String PRE_PAY_URL="https://smzf.yjpal.com/payQRCode/newPay.htm?qrcodeId=QR_CODE_ID&payMoney=PAY_MONEY&appId=APP_ID&channelCode=CHANNEL_CODE&payType=PAY_TYPE";
    /** 获取支付参数 */
    private static String WX_APP_ID_URL="https://smzf.yjpal.com/payQRCode/newPayMess.htm?qrcodeId=QR_CODE_ID&channelCode=wx";
    /** 交易列表 */
    private static String ORDER_LIST_URL="https://smzf.yjpal.com/payQRCode/newQrcodeTransjnlsList.htm?mobileNo=MOBILE_NO&pageNo=0&time=201905";
    /** 收银台地址 */
    private static String QR_CORD_URL="https://smzf.yjpal.com/payQRCode/qrcodeCardPage.htm?mobileNo=MOBILE_NO";

    public static void main(String[] args){
        String mobile="17162086606";
        String pwd="q123456";
        String amount="0.01";
        String payType="1";
        // 1登录
        JSONObject loginJson = login(mobile,pwd);
        System.out.println(loginJson);
       /* // 2获取交易二维码：
        JSONObject qrCodeJson = getQrCodeId(mobile,loginJson.getString("token"));
        String qrCodeUrl = qrCodeJson.getJSONObject("pqm").getString("ylxwQrcodeurl");
        String realUrl = UrlUtil.getReal(qrCodeUrl);
        // 下单用
        String qrCodeId = realUrl.split("params=")[1];
        // 3.获取下单参数
        JSONObject payInfo = getPayData(qrCodeId);
        // 4.下单
        JSONObject orderJson = createOrder(payType,amount,qrCodeId,payInfo.getString("channelCode"),payInfo.getString("appId"));
        if(orderJson!=null&&!orderJson.isEmpty()&&orderJson.containsKey("code")&&orderJson.getString("code").equals("0000")){
            System.out.printf("\n下单成功：%s\n",orderJson.getString("url"));
        }*/
        // 5.查询
        JSONObject queryJson = orderQuery(mobile,loginJson.getString("token"));
        System.out.println(queryJson);
    }

    private static JSONObject login(String mobile,String pwd){
        try {
            pwd = DigestUtil.md5Hex(pwd+"superpw1234_!QAZ");
            String url = LOGIN_URL.replace("MOBILE_NO",mobile).replace("PASSWORD",pwd);
            JSONObject dataJson = new JSONObject();
            String result = UrlUtil.httpPostHtml200(url,dataJson);
            logger.info("相應結果：{}",result);
            // String result = HttpUtil.post(url, dataJson);
            return JSON.parseObject(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject getQrCodeId(String mobile,String token){
        String url = QR_CORD_URL.replace("MOBILE_NO",mobile);
        String result = HttpRequest.post(url).header("AccessToken",token).execute().body();
        return JSON.parseObject(result);
    }

    private static JSONObject getPayData(String qrCodeId){
        JSONObject jsonObject = new JSONObject();
        String url = WX_APP_ID_URL.replace("QR_CODE_ID",qrCodeId);
        String result = HttpRequest.post(url).execute().body();
        jsonObject.put("appId",result.split("\\|")[1]);
        jsonObject.put("channelCode",result.split("\\|")[2]);
        return jsonObject;
    }

    private static JSONObject createOrder(String payType,String amount,String qrCodeId,String channelCode,String appId) {
        String url = PRE_PAY_URL.replace("QR_CODE_ID",qrCodeId).replace("PAY_MONEY",amount).replace("APP_ID",appId).replace("CHANNEL_CODE",channelCode).replace("PAY_TYPE",payType);
        String result = HttpRequest.post(url).execute().body();
        return JSON.parseObject(result);
    }

    private static JSONObject orderQuery(String mobile,String token){
        String url = ORDER_LIST_URL.replace("MOBILE_NO",mobile);
        String result = HttpRequest.post(url).header("AccessToken",token).execute().body();
        return JSON.parseObject(result);
    }
}
