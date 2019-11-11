package com.vc.onlinepay.utils.yida;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Map;

/**
 * 统一下单demo
 */
public class PayOrderDemo extends BaseSdk {

    // 商户ID
    static final String mchId = "20000069";//20001223,20001245
    static final String appId = "41c1755f7f644ae6aaea5397876d9d6c";
    static final String productId = "8007";
    // 私钥
    static final String key = "M60RGFVRILYOXCH13CBMJTOVF77JU3WPYDG23HCIUEKRP8KK1GERPVRNMLKYRAUH9MW3BXVYO6C7IWQNU0ZPMQ0KK1DBB1CKMQJOO5QMAWQCVMBV9ARAKJSTBJAGUOB5";

    //static final String payUrl = "https://pay.b.xxpay.org/api";
    static final String payUrl = "https://do.rn6.net/api";
    String url = "https://do.rn6.net/api/pay/create_order";
    static final String notifyUrl = "http://www.baidu.com"; // 本地环境测试,可到ngrok.cc网站注册

    public static void main(String[] args) {
        payOrderTest();
    }

    // 统一下单
    static String payOrderTest() {
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId);                               // 商户ID
        //paramMap.put("appId", appId);                             // 应用ID,非必填
        paramMap.put("mchOrderNo", System.currentTimeMillis());     // 商户订单号
        paramMap.put("productId", productId);                       // 支付产品
        paramMap.put("amount", 100);                                // 支付金额,单位分
        paramMap.put("currency", "cny");                            // 币种, cny-人民币
        paramMap.put("clientIp", "211.94.116.218");                 // 用户地址,微信H5支付时要真实的
        paramMap.put("device", "WEB");                              // 设备
        paramMap.put("subject", "XXPAY支付测试");
        paramMap.put("body", "XXPAY支付测试");
        paramMap.put("notifyUrl", notifyUrl);                       // 回调URL
        paramMap.put("param1", "");                                 // 扩展参数1
        paramMap.put("param2", "");                                 // 扩展参数2
        //paramMap.put("extra", "{\"productId\":\"100\"}");  // 附加参数
        paramMap.put("extra", "{\"cardCode\":\"MOBILE\",\"cardAmt\": \"10\",\"cardNo\": \"17329198803183425\",\"cardPwd\": \"353687724098502509\"}");

        //{"h5_info": {"type":"Wap","wap_url": "https://pay.qq.com","wap_name": "腾讯充值"}}
        //

        String reqSign = PayDigestUtil.getSign(paramMap, key);
        paramMap.put("sign", reqSign);                              // 签名
        String reqData = "params=" + paramMap.toJSONString();
        System.out.println("请求支付中心下单接口,请求数据:" + reqData);
        String url = payUrl + "/pay/create_order?";
        String result = call4Post(url + reqData);
        System.out.println("请求支付中心下单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if("SUCCESS".equals(retMap.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, key, "sign");
            String retSign = (String) retMap.get("sign");
            if(checkSign.equals(retSign)) {
                System.out.println("=========支付中心下单验签成功=========");
            }else {
                System.err.println("=========支付中心下单验签失败=========");
                return null;
            }
        }
        return retMap.get("payOrderId")+"";
    }

    static String quryPayOrderTest(String mchOrderNo, String payOrderId) {
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId);                               // 商户ID
        paramMap.put("appId", appId);
        paramMap.put("mchOrderNo", mchOrderNo);                     // 商户订单号
        paramMap.put("payOrderId", payOrderId);                     // 支付订单号
        paramMap.put("executeNotify", "true");                      // 是否执行回调,true或false,如果为true当订单状态为支付成功(2)时,支付中心会再次回调一次业务系统

        String reqSign = PayDigestUtil.getSign(paramMap, key);
        paramMap.put("sign", reqSign);                              // 签名
        String reqData = "params=" + paramMap.toJSONString();
        System.out.println("请求支付中心查单接口,请求数据:" + reqData);
        String url = payUrl + "/pay/query_order?";
        String result = call4Post(url + reqData);
        System.out.println("请求支付中心查单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if("SUCCESS".equals(retMap.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, key, "sign");
            String retSign = (String) retMap.get("sign");
            if(checkSign.equals(retSign)) {
                System.out.println("=========支付中心查单验签成功=========");
            }else {
                System.err.println("=========支付中心查单验签失败=========");
                return null;
            }
        }
        return retMap.get("payOrderId")+"";
    }



}
