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
@RequestMapping("/trans_demo")
public class TransDemoController {

    private static final Logger _log = LoggerFactory.getLogger(TransDemoController.class);

    //@Value("${config.payUrl}")
    public String payUrl;
    //@Value("${config.mchId}")
    public Long mchId;
   // @Value("${config.privateKey}")
    public String privateKey;
    //@Value("${config.appId}")
    public String appId;
    //@Value("${config.passageId}")
    public String passageId;

    /**
     * 创建代付订单
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/create")
    @ResponseBody
    public JSONObject create(HttpServletRequest request) {
        String accountName = request.getParameter("accountName");
        String accountNo = request.getParameter("accountNo");
        String amount = request.getParameter("amount");
        String notifyUrl = request.getParameter("notifyUrl");
        JSONObject retObj = createTransOrder(this.mchId, this.privateKey, this.appId, this.passageId, accountName,
                accountNo, amount, notifyUrl);
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
                object.put("msg", "创建订单失败,验证代付中心返回签名失败");
                return object;
            }
        }
        object.put("code", 1003);
        object.put("msg", "创建订单失败," + retObj.getString("retMsg"));
        return object;
    }

    /**
     * 接收代付中心通知
     * 
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/notify.htm")
    @ResponseBody
    public String notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        _log.info("====== 开始处理代付中心通知 ======");
        Map<String, Object> paramMap = request2payResponseMap(request,
                new String[] { "payOrderId", "mchId", "appId", "productId", "mchOrderNo", "amount", "status",
                        "channelOrderNo", "channelAttach", "param1", "param2", "paySuccTime", "backType", "sign" });
        _log.info("代付中心通知请求参数,paramMap={}", paramMap);
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
        _log.info("响应代付中心通知结果:{},payOrderId={},mchOrderNo={}", resStr, payOrderId, mchOrderNo);
        _log.info("====== 代付中心通知处理完成 ======");
        return resStr;
    }

    /**
     * 调用XxPay代付系统
     * 
     * @param mchId
     * @param key
     * @param appId
     * @param passageId
     * @param accountName
     * @param accountNo
     * @param amount
     * @param notifyUrl
     * @return
     */
    JSONObject createTransOrder(Long mchId, String key, String appId, String passageId, String accountName,
            String accountNo, String amount, String notifyUrl) {

        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId); // 商户ID                                               必填
        paramMap.put("appId", appId); // 应用ID                                               必填
        paramMap.put("mchTransNo", System.currentTimeMillis()); // 商户转账单号                必填
        paramMap.put("passageId", passageId); // 通道ID                                       必填
        paramMap.put("amount", amount); // 代付金额,单位分                                     必填
        paramMap.put("currency", "cny"); // 币种, cny-人民币                                   必填
        paramMap.put("clientIp", "211.94.116.218"); // 用户地址,微信H5代付时要真实的             必填
        paramMap.put("device", ""); // 设备,WEB
        paramMap.put("extra", ""); // 特定渠道发起时额外参数
        paramMap.put("param1", ""); // 扩展参数1
        paramMap.put("param2", ""); // 扩展参数2
        paramMap.put("notifyUrl", notifyUrl); // 转账结果回调URL
        paramMap.put("channelUser", ""); // 渠道用户标识,如微信openId,支付宝账号
        paramMap.put("accountAttr", "0"); // 账户属性:0-对私,1-对公,默认对私                    必填
        paramMap.put("accountType", ""); // 账户类型
        paramMap.put("accountName", accountName); // 账户名                                    必填
        paramMap.put("accountNo", accountNo); // 账户号                                        必填
        paramMap.put("province", ""); // 开户行所在省份
        paramMap.put("city", ""); // 开户行所在市
        paramMap.put("bankName", ""); // 开户行名称
        paramMap.put("bankType", ""); // 联行号
        paramMap.put("bankCode", ""); // 银行代码
        paramMap.put("remarkInfo", ""); // 备注

        // 生成签名数据
        String reqSign = PayDigestUtil.getSign(paramMap, key);
        paramMap.put("sign", reqSign); // 签名
        String reqData = "params=" + paramMap.toJSONString();
        _log.info("[xxpay] req:{}", reqData);
        String url = this.payUrl + "/trans/create_order?";

        // 发起Http请求下单
        String result = call4Post(url + reqData);
        _log.info("[xxpay] res:{}", result);
        JSONObject retObj = JSON.parseObject(result);
        return retObj;
    }

    public Map<String, Object> request2payResponseMap(HttpServletRequest request, String[] paramArray) {
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

    public boolean verifyPayResponse(Map<String, Object> map) {
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

    public boolean verifySign(Map<String, Object> map) {
        String localSign = PayDigestUtil.getSign(map, this.privateKey, "sign");
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
