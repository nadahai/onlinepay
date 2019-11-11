package com.vc.onlinepay.pay.order.gateway;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.remittance.SmallTools;
import com.vc.onlinepay.utils.remittance.RemittanceConstant;
import java.net.URLEncoder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class RemitGatewayServiceImpl {

    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * @描述:汇付宝网关支付
     * @作者:Alan
     * @时间:2018年1月22日 下午3:22:51
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("orderNo", reqData.getString("vcOrderNo"));
//            logger.error("汇付宝网关接口入参{}", reqData);
            // 请求地址
            String channelKey = StringUtils.deleteWhitespace(reqData.getString("channelKey"));
            String channelKeyDes = StringUtils.deleteWhitespace(reqData.getString("channelDesKey"));

            String dominame = "http://pay.juruib.cn";
            String md5Key = channelKeyDes;
            if(channelKeyDes.contains("###")){
                dominame = Constant.getChannelKeyDes(channelKeyDes,1);
                md5Key = Constant.getChannelKeyDes(channelKeyDes,0);
            }
            // 请求参数
            String version = "3"; // 当前接口版本号
            String is_phone = ""; // 是否使用手机端支付，1=是，不传为pc端支付
            if ("1106".equals(reqData.getString("payType"))) {
                is_phone = "1";
            }
            String agent_id = channelKey; // 商户id
            String agent_bill_id = reqData.getString("vcOrderNo");// 商户内部订单号
            String agent_bill_time = Constant.getDateString();// 提交单据的时间yyyyMMddHHmmss
            String pay_type = "20"; // 支付类型 20
            String pay_amt = reqData.getString("amount"); // 订单总金额
            String notify_url = dominame+"/onlinepay/remitth5callbackapi"; // 异步通知地址
            String return_url = dominame+"/onlinepay/success";  // 支付完成同步跳转地址
            String user_ip = reqData.getString("ipaddress"); // 用户IP
            user_ip = user_ip.replace(".","_");
            String bank_card_type = "0"; // 银行卡类型：未知=-1；储蓄卡=0；信用卡=1
            String goods_name = URLEncoder.encode(reqData.getString("goodsName"), "gbk");// 商品名称
            String goods_num = "1"; // 商品数量
            String goods_note = URLEncoder.encode(reqData.getString("goodsName"), "gbk");// 商品说明
            if (reqData.containsKey("goodsDesc")) {
                goods_note = URLEncoder.encode(reqData.getString("goodsDesc"), "gbk");// 商品说明
            }
            String pay_code = "0"; // 银行编码
            String remark = "wp"+agent_id; // 商户自定义字段，异步通知时原样返回
            String sign_type = "MD5"; // 签名方式MD5
            String sign = ""; // MD5签名结果
            // 组织签名串
            StringBuilder sign_sb = new StringBuilder();
            sign_sb.append("version").append("=").append(version).append("&")
                    .append("agent_id").append("=").append(agent_id).append("&")
                    .append("agent_bill_id").append("=").append(agent_bill_id).append("&")
                    .append("agent_bill_time").append("=").append(agent_bill_time).append("&")
                    .append("pay_type").append("=").append(pay_type).append("&")
                    .append("pay_amt").append("=").append(pay_amt).append("&")
                    .append("notify_url").append("=").append(notify_url).append("&")
                    .append("return_url").append("=").append(return_url).append("&")
                    .append("user_ip").append("=").append(user_ip).append("&")
                    .append("bank_card_type").append("=").append(bank_card_type).append("&")
                    .append("key").append("=").append(md5Key);
//            logger.info("签名参数：" + sign_sb.toString());
            sign = SmallTools.md5(sign_sb.toString());
//            logger.info("签名结果：" + sign);
            // 请求参数
            StringBuilder requestParams = new StringBuilder();
            requestParams.append("version").append("=").append(version).append("&")
                    .append("agent_id").append("=").append(agent_id).append("&")
                    .append("agent_bill_id").append("=").append(agent_bill_id).append("&")
                    .append("agent_bill_time").append("=").append(agent_bill_time).append("&")
                    .append("pay_type").append("=").append(pay_type).append("&")
                    .append("pay_amt").append("=").append(pay_amt).append("&")
                    .append("notify_url").append("=").append(notify_url).append("&")
                    .append("return_url").append("=").append(return_url).append("&")
                    .append("user_ip").append("=").append(user_ip).append("&")
                    .append("goods_name").append("=").append(goods_name).append("&")
                    .append("goods_num").append("=").append(goods_num).append("&")
                    .append("goods_note").append("=").append(goods_note).append("&")
                    .append("remark").append("=").append(remark).append("&")
                    .append("is_phone").append("=").append(is_phone).append("&")
                    .append("bank_card_type").append("=").append(bank_card_type).append("&")
                    .append("pay_code").append("=").append(pay_code).append("&")
                    .append("sign_type").append("=").append(sign_type).append("&")
                    .append("sign").append("=")
                    .append(sign);
            String payUrl = RemittanceConstant.WEBPAGE_REQUESTURL + "?" + requestParams.toString();
            System.out.println("payUrl:"+payUrl);

            result.put("code", Constant.SUCCESSS);

            result.put("viewPath","auto/autoSubmit");
                JSONObject formdata = new JSONObject();
                formdata.put("actionUrl", payUrl);
                formdata.put("map", new JSONObject());
                formdata.put("refer", "http://pay.juruib.cn" );
                formdata.put("charset", Constant.CHART_UTF);
            result.put("data",formdata);
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("汇付宝网关统一支付异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "网关支付获取链接失败");
            return listener.failedHandler(result);
        }
    }
}
