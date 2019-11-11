package com.vc.onlinepay.utils.yida;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: dingzhiwei
 * @date: 2018/6/7
 * @description:
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    private static final Logger _log = LoggerFactory.getLogger(DemoController.class);

   // @Value("${config.payUrl}")
   static public String payUrl;
   // @Value("${config.mchId}")
   static public Long mchId = 20000069L;
   // @Value("${config.privateKey}")
    static public String privateKey = "M60RGFVRILYOXCH13CBMJTOVF77JU3WPYDG23HCIUEKRP8KK1GERPVRNMLKYRAUH9MW3BXVYO6C7IWQNU0ZPMQ0KK1DBB1CKMQJOO5QMAWQCVMBV9ARAKJSTBJAGUOB5";
   // @Value("${config.appId}")
   static public String appId ="41c1755f7f644ae6aaea5397876d9d6c";

    /**
     * 创建支付订单
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/create")
    @ResponseBody
    public JSONObject create(HttpServletRequest request) {
        // type目前两种类型,recharge:api充值,cashier:收银台充值
        String type = request.getParameter("type");
        String productId = request.getParameter("productId");
        String amount = request.getParameter("amount");
        String subject = request.getParameter("subject");
        String body = request.getParameter("body");
        String notifyUrl = request.getParameter("notifyUrl");
        String extra = request.getParameter("extra");
        JSONObject retObj = createPayOrder(type, mchId, privateKey, appId, productId, amount, subject,
                body, notifyUrl, extra);
        JSONObject object = new JSONObject();
        if (retObj == null) {
            object.put("code", 1001);
            object.put("msg", "创建订单失败,没有返回数据");
            return object;
        }
        if ("SUCCESS".equals(retObj.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retObj, privateKey, "sign");
            String retSign = (String) retObj.get("sign");
            if (checkSign.equals(retSign)) {
                object.put("code", 0);
                object.put("msg", "下单成功");
                object.put("data", retObj);
                return object;
            } else {
                object.put("code", 1002);
                object.put("msg", "创建订单失败,验证支付中心返回签名失败");
                return object;
            }
        }
        object.put("code", 1003);
        object.put("msg", "创建订单失败," + retObj.getString("retMsg"));
        return object;
    }

    /**
     * 接收支付中心通知
     * 
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/notify.htm")
    @ResponseBody
    public String notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        _log.info("====== 开始处理支付中心通知 ======");
        Map<String, Object> paramMap = request2payResponseMap(request,
                new String[] { "payOrderId", "mchId", "appId", "productId", "mchOrderNo", "amount", "status",
                        "channelOrderNo", "channelAttach", "param1", "param2", "paySuccTime", "backType", "sign" });
        _log.info("支付中心通知请求参数,paramMap={}", paramMap);
        if (!verifyPayResponse(paramMap)) {
            String errorMessage = "verify request param failed.";
            _log.warn(errorMessage);
            return errorMessage;
        }
        String payOrderId = (String) paramMap.get("payOrderId");
        String mchOrderNo = (String) paramMap.get("mchOrderNo");
        String resStr;
        try {
            // 业务处理代码,根据订单号,得到业务系统交易数据.
            // 对交易数据进行处理,如修改状态,发货等操作
            resStr = "success";
        } catch (Exception e) {
            resStr = "fail";
            _log.error("处理通知失败", e);
        }
        _log.info("响应支付中心通知结果:{},payOrderId={},mchOrderNo={}", resStr, payOrderId, mchOrderNo);
        _log.info("====== 支付中心通知处理完成 ======");
        return resStr;
    }

    /**
     * 调用XxPay支付系统,创建支付订单
     * 
     * @param type
     * @param mchId
     * @param key
     * @param appId
     * @param productId
     * @param amount
     * @param subject
     * @param body
     * @param notifyUrl
     * @return
     */
    JSONObject createPayOrder(String type, Long mchId, String key, String appId, String productId, String amount,
            String subject, String body, String notifyUrl, String extra) {
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId); // 商户ID
        paramMap.put("appId", appId); // 应用ID
        paramMap.put("mchOrderNo", System.currentTimeMillis()); // 商户订单号
        paramMap.put("productId", productId); // 支付产品
        paramMap.put("amount", amount); // 支付金额,单位分
        paramMap.put("currency", "cny"); // 币种, cny-人民币
        paramMap.put("clientIp", "211.94.116.218"); // 用户地址,微信H5支付时要真实的
        paramMap.put("device", "WEB"); // 设备
        paramMap.put("subject", subject); // 商品标题
        paramMap.put("body", body); // 商品内容
        paramMap.put("notifyUrl", notifyUrl); // 回调URL
        paramMap.put("param1", ""); // 扩展参数1
        paramMap.put("param2", ""); // 扩展参数2
        paramMap.put("extra", extra); // 附加参数
        // 生成签名数据
        String reqSign = PayDigestUtil.getSign(paramMap, key);
        paramMap.put("sign", reqSign); // 签名
        String reqData = "params=" + paramMap.toJSONString();
        _log.info("[xxpay] req:{}", reqData);
        String url;
        if ("recharge".equalsIgnoreCase(type)) {
            url = payUrl + "/pay/create_order?";
        } else if ("cashier".equalsIgnoreCase(type)) {
            url = payUrl + "/cashier/pc_build?";
        } else {
            return null;
        }
        // 发起Http请求下单
        String result = call4Post(url + reqData);
        _log.info("[xxpay] res:{}", result);
        JSONObject retObj = JSON.parseObject(result);
        return retObj;
    }

    public static Map<String, Object> request2payResponseMap(HttpServletRequest request, String[] paramArray) {
        Map<String, Object> responseMap = new HashMap<>();
        for (int i = 0; i < paramArray.length; i++) {
            String key = paramArray[i];
            String v = request.getParameter(key);
            if (v != null) {
                responseMap.put(key, v);
            }
        }
        return responseMap;
    }

    public static boolean verifyPayResponse(Map<String, Object> map) {
        String mchId = (String) map.get("mchId");
        String payOrderId = (String) map.get("payOrderId");
        String amount = (String) map.get("amount");
        String sign = (String) map.get("sign");

        if (StringUtils.isEmpty(mchId)) {
            _log.warn("Params error. mchId={}", mchId);
            return false;
        }
        if (StringUtils.isEmpty(payOrderId)) {
            _log.warn("Params error. payOrderId={}", payOrderId);
            return false;
        }
        if (StringUtils.isEmpty(amount) || !NumberUtils.isDigits(amount)) {
            _log.warn("Params error. amount={}", amount);
            return false;
        }
        if (StringUtils.isEmpty(sign)) {
            _log.warn("Params error. sign={}", sign);
            return false;
        }

        // 验证签名
        if (!verifySign(map)) {
            _log.warn("verify params sign failed. payOrderId={}", payOrderId);
            return false;
        }

        // 此处需要写业务逻辑
        // 校验数据,订单是否一致,金额是否一致

        return true;
    }

    public static boolean verifySign(Map<String, Object> map) {
        String localSign = PayDigestUtil.getSign(map, privateKey, "sign");
        String sign = (String) map.get("sign");
        return localSign.equalsIgnoreCase(sign);
    }

    /**
     * 发起HTTP/HTTPS请求(method=POST)
     * 
     * @param url
     * @return
     */
    public static String call4Post(String url) {
        try {
            URL url1 = new URL(url);
            if ("https".equals(url1.getProtocol())) {
                return HttpClient.callHttpsPost(url);
            } else if ("http".equals(url1.getProtocol())) {
                return HttpClient.callHttpPost(url);
            } else {
                return "";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

}
