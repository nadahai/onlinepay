package com.vc.onlinepay.pay.order.gateway;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.remittance.SmallTools;
import java.net.URLEncoder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class RemitGateway2ServiceImpl   {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String payurl = "https://c.heepay.com/quick/pc/index.do";

    /**
     * @描述:汇付宝网关支付
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

            String dominame = "";
            if(channelKeyDes.contains("###")){
                dominame = Constant.getChannelKeyDes(channelKeyDes,1);
                channelKeyDes = Constant.getChannelKeyDes(channelKeyDes,0);
            }
            String agent_id = channelKey; // 商户id
            String notifyUrl = reqData.getString("projectDomainUrl")+"/remittVer2CallBackApi"; // 异步通知地址
            String callBackUrl = reqData.getString("projectDomainUrl")+"/success";
            if(StringUtils.isNotBlank(dominame)){
                notifyUrl = Constant.buildUrl(dominame,"onlinepay/remittVer2CallBackApi");
                callBackUrl =  Constant.buildUrl(dominame,"onlinepay/success");
            }
            String goods_note = URLEncoder.encode(reqData.getString("goodsName"), "gbk");// 商品说明
            if (reqData.containsKey("goodsDesc")) {
                goods_note = URLEncoder.encode(reqData.getString("goodsDesc"), "gbk");// 商品说明
            }

            String merchantId = agent_id;//"103685";//商户号
            String merchantOrderNo = reqData.getString("vcOrderNo");//订单号
            String merchantUserId = "123";//商户用户id
            String productCode = "HY_B2CEBANKPC";//产品编码
            String payAmount =  reqData.getString("amount");//金额
            String requestTime = reqData.getString("orderTime");//请求时间
            String version = "1.0";//版本号

            String description = goods_note;//商品信息
            String clientIp = reqData.getString("ipaddress");//用户IP
            String reqHyTime = System.currentTimeMillis()+"";//防钓鱼时间
            String sign = "";//签名字符串
            String onlineType = "hard";//选择银行方式
            String bankId = "";//银行id
            String bankName = "";//银行名称
            String bankCardType = "";//银行卡类型
            String key = channelKeyDes;//"1e034dde23da9f152abdaf0f738413bb";//秘钥

            String sign1 = "merchantId="+merchantId+
                    "&merchantOrderNo="+merchantOrderNo+
                    "&merchantUserId="+merchantUserId+
                    "&notifyUrl="+notifyUrl+
                    "&payAmount="+payAmount+
                    "&productCode="+productCode+
                    "&version="+version+
                    "&key="+key;
//            System.out.println("签名参数："+sign1);
            sign = SmallTools.md5(sign1);

            JSONObject obj = new JSONObject();
            obj.put("merchantId",merchantId);
            obj.put("merchantOrderNo",merchantOrderNo);
            obj.put("merchantUserId",merchantUserId);
            obj.put("productCode",productCode);
            obj.put("payAmount",payAmount);
            obj.put("requestTime",requestTime);
            obj.put("version",version);
            obj.put("notifyUrl",notifyUrl);
            obj.put("callBackUrl",callBackUrl);
            obj.put("description",description);
            obj.put("clientIp",clientIp);
            obj.put("reqHyTime",reqHyTime);
            obj.put("sign",sign);
            obj.put("onlineType",onlineType);
            obj.put("bankId",bankId);
            obj.put("bankName",bankName);
            obj.put("bankCardType",bankCardType);
//            obj.put("sign1",sign1);
            String parameter = obj.toString();
            System.out.println("请求参数："+parameter);
            
            result.put("code", Constant.SUCCESSS);

            result.put("viewPath","auto/autoSubmit2");
                JSONObject formdata = new JSONObject();
                formdata.put("actionUrl", payurl);
                formdata.put("map", obj );
                formdata.put("refer", "http://www.xinxianmaoyi.com" );
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
