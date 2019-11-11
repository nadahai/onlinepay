/**
 * @类名称:RemitReplaceServiceImpl.java
 * @时间:2017年12月19日下午2:57:29
 * @作者:lihai 
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.pay.order.union;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.http.HttpPaySubmit;
import com.vc.onlinepay.utils.remittance.SmallTools;
import com.vc.onlinepay.utils.remittance.RemittanceConstant;
import java.net.URLEncoder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:汇付宝银联快捷
 * @作者:lihai 
 * @时间:2017年12月19日 下午2:57:29 
 */
@Service
@Component
public class RemitUnionServiceImpl{

    private Logger logger = LoggerFactory.getLogger(getClass());

    //自动提交地址
    private static final String ACTION_URL = "https://c.heepay.com/quick/pc/index.do";

    private static final String ACTION_URL_WAP = "https://c.heepay.com/newOnlineBank/paymentUnion.do";
    
    @Value("${onlinepay.project.successUrl:}")
    private String successUrl;
    
    @Value("${onlinepay.project.actualName:}")
    private String actualName;
    
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result = unionPayForPc(reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("汇付宝银联快捷统一支付异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "银联快捷支付获取链接失败");
            return listener.failedHandler(result);
        }
    }

    public JSONObject payOrderWap(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result = unionPayWap(reqData);
            result.put("orderNo", reqData.getString("vcOrderNo"));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("汇付宝银联快捷统一支付异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "银联快捷支付获取链接失败");
            return listener.failedHandler(result);
        }
    }

    //银联wap储蓄卡
    private JSONObject unionPayWap(JSONObject reqData) throws Exception{
        JSONObject result = new JSONObject();
        String merchantId = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
        String channelKeyDes     = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));

        String dominame = "http://pay.juruib.cn";
        if(channelKeyDes.contains("###")){
            dominame = Constant.getChannelKeyDes(channelKeyDes,1);
            channelKeyDes = Constant.getChannelKeyDes(channelKeyDes,0);
        }

        String merchantUserId = reqData.getString("merchantId");//商户用户id
        String merchantOrderNo = reqData.getString("vcOrderNo");//订单号
        String productCode = "HY_B2CUNIONWAP";//产品编码
        String payAmount = reqData.getString("amount");//金额
        String notifyUrl = Constant.buildUrl(dominame,"onlinepay/remittVer2CallBackApi");
        String callBackUrl = Constant.buildUrl(dominame,"onlinepay/success");
        String description = reqData.getString("goodsDesc");//商品信息
        String sign = "";//签名字符串
        String sign1 = "callBackUrl="+callBackUrl+
                "&description="+description+
                "&merchantId="+merchantId+
                "&merchantOrderNo="+merchantOrderNo+
                "&merchantUserId="+merchantUserId+
                "&notifyUrl="+notifyUrl+
                "&payAmount="+payAmount+
                "&key="+channelKeyDes;
        sign = SmallTools.md5(sign1);

        JSONObject obj = new JSONObject();
        obj.put("merchantId",merchantId);
        obj.put("merchantOrderNo",merchantOrderNo);
        obj.put("merchantUserId",merchantUserId);
        obj.put("productCode",productCode);
        obj.put("payAmount",payAmount);
        obj.put("notifyUrl",notifyUrl);
        obj.put("callBackUrl",callBackUrl);
        obj.put("description",description);
        obj.put("sign",sign);

        String html = HttpPaySubmit.buildPostRequest(ACTION_URL_WAP, obj);

        result.put("code", Constant.SUCCESSS);
        result.put("msg", "下单成功");
        result.put("redirectHtml", html);
        return result;
    }
    //银联Pc储蓄卡
    private JSONObject unionPayForPc(JSONObject reqData)throws Exception{
        JSONObject result = new JSONObject();
        String agentId = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
        String channelKeyDes = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));
        String dominame = "http://pay.juruib.cn";
        if(channelKeyDes.contains("###")){
            dominame = Constant.getChannelKeyDes(channelKeyDes,1);
            channelKeyDes = Constant.getChannelKeyDes(channelKeyDes,0);
        }
        // 请求参数
        String version = "1"; // 当前接口版本号
        String agentBillId = reqData.getString("vcOrderNo");// 商户内部订单号
        String agentBillTime = Constant.getDateString();// 提交单据的时间yyyyMMddHHmmss
        String payType = "19"; // 支付类型 20
        String payAmt = reqData.getString("amount"); // 订单总金额
        String notifyUrl = dominame+ "/onlinepay/remitth5callbackapi"; // 异步通知地址
        String returnUrl = dominame + "/onlinepay/success"; // 支付完成同步跳转地址
        String userIp = reqData.getString("ipaddress"); // 用户IP
        String goodsName = URLEncoder.encode(reqData.getString("goodsName"), "gbk");// 商品名称
        String goodsNum = "1"; // 商品数量
        String goodsNote = URLEncoder.encode(reqData.getString("goodsDesc"), "gbk");// 商品说明
        String remark = "wap"+agentBillId; // 商户自定义字段，异步通知时原样返回
        String key = channelKeyDes;// 密钥
        String sign = ""; // MD5签名结果
        // 组织签名串
        StringBuilder signSource = new StringBuilder();
        signSource.append("version").append("=").append(version).append("&")
                .append("agent_id").append("=").append(agentId).append("&")
                .append("agent_bill_id").append("=").append(agentBillId).append("&")
                .append("agent_bill_time").append("=").append(agentBillTime).append("&")
                .append("pay_type").append("=").append(payType).append("&")
                .append("pay_amt").append("=").append(payAmt).append("&")
                .append("notify_url").append("=").append(notifyUrl).append("&")
                .append("return_url").append("=").append(returnUrl).append("&")
                .append("user_ip").append("=").append(userIp).append("&")
                .append("key").append("=").append(key);
        sign = SmallTools.md5(signSource.toString());
        // 请求参数
        StringBuilder requestParams = new StringBuilder();
        requestParams.append("version").append("=").append(version).append("&")
                .append("agent_id").append("=").append(agentId).append("&")
                .append("agent_bill_id").append("=").append(agentBillId).append("&")
                .append("agent_bill_time").append("=").append(agentBillTime).append("&")
                .append("pay_type").append("=").append(payType).append("&")
                .append("pay_amt").append("=").append(payAmt).append("&")
                .append("notify_url").append("=").append(notifyUrl).append("&")
                .append("return_url").append("=").append(returnUrl).append("&")
                .append("user_ip").append("=").append(userIp).append("&")
                .append("goods_name").append("=").append(goodsName).append("&")
                .append("goods_num").append("=").append(goodsNum).append("&")
                .append("goods_note").append("=").append(goodsNote).append("&")
                .append("remark").append("=").append(remark).append("&")
                .append("sign").append("=").append(sign);
        String payUrl = RemittanceConstant.WEBPAGE_REQUESTURL + "?" + requestParams.toString();
        logger.error("汇付宝银联快捷支付链接:{}",payUrl);

        result.put("code", Constant.SUCCESSS);
        result.put("msg", "下单成功");
        result.put("redirectUrl",payUrl);
        return result;
    }
}

