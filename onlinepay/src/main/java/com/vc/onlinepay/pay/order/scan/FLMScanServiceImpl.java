package com.vc.onlinepay.pay.order.scan;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.channel.ChannelSubNo;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.channel.ChannelSubNoServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.UrlUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @描述:付临门
 * @时间:2018年5月15日 22:14:30
 */
@SuppressWarnings("deprecation")
@Service
@Component
public class FLMScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (FLMScanServiceImpl.class);
    @Autowired
    private ChannelSubNoServiceImpl channelSubNoServiceImpl;
    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;
    @Autowired
    private RedisCacheApi redisCacheApi;

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
    /** 交易查询 */
    private static String ORDER_QUERY_URL="https://smzf.yjpal.com/payQRCode/newQRcodeTransjnlsMessage.htm?mobileNo=MOBILE_NO&orderNo=ORDER_NO";

    /**
     * @描述:扫码支付下单
     * @时间:2018年5月15日 22:14:30
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("扫码接口入参{}", reqData);
            String pwd = reqData.containsKey ("channelDesKey") ? reqData.getString ("channelDesKey").trim () : "";
            String mobile = reqData.containsKey ("channelKey") ? reqData.getString ("channelKey").trim () : "";
            String vcOrderNo = reqData.containsKey ("vcOrderNo") ? reqData.getString ("vcOrderNo").trim () : "";
            channelSubNoServiceImpl.updateLastOrderTime(new ChannelSubNo(mobile));
            if (StringUtils.isAnyEmpty (mobile, vcOrderNo)) {
                return listener.failedHandler (Constant.failedMsg ("账号为空"));
            }
            String amount = reqData.getString ("amount");
            Integer type = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
            String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
            //payType 1:微信 2:支付宝 5:qq支付
            String payType = "1";
            int openType = reqData.getIntValue ("channelSource");
            if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
                payType = "2";
                openType = 92;
            }
            Date beginDate = new Date ();
            String token = this.getUserToken(mobile,pwd);
            if (StringUtil.isEmpty(token)) {
                return listener.failedHandler (Constant.failedMsg ("获取token失败"));
            }
            JSONObject qrCodeJson = getQrCodeId(mobile,token);
            logger.info("获取交易二维码:{}",qrCodeJson);
            String qrCodeUrl = qrCodeJson.getJSONObject("pqm").getString("ylxwQrcodeurl");
            if(StringUtil.isEmpty(qrCodeUrl)){
                return listener.failedHandler (Constant.failedMsg ("获取交易码失败"));
            }
            String realUrl = getReal(qrCodeUrl);
            String qrCodeId = realUrl.split("params=")[1];
            JSONObject payInfo = getPayData(qrCodeId);
            logger.info("获取下单参数:{}",payInfo);
            JSONObject orderJson = this.createOrder(payType,amount,qrCodeId,payInfo.getString("channelCode"),payInfo.getString("appId"));
            long between = DateUtil.between (beginDate, new Date (), DateUnit.SECOND);
            StaticLog.info ("下单結果耗时：{}s,响应:{}", between, orderJson);

            if (orderJson == null || orderJson.isEmpty() || !orderJson.containsKey("url") ) {
                return listener.failedHandler (Constant.failedMsg ("扫码下单超时"));
            }
            VcOnlineOrderMade made = VcOnlineOrderMade.buildCommonMade (reqData);
            made.setOpenType (openType);
            made.setQrcodeUrl (orderJson.getString("url"));
            JSONObject response = onlineOrderMadeService.getOrderMadePayUrl (made);
            logger.info ("扫码支付响应{}", response);
            if (response == null || response.isEmpty () || !response.containsKey ("openUrl")) {
                return listener.failedHandler (Constant.failedMsg ("扫码支付超时"));
            }
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "下单成功");
            result.put ("realAmount", reqData.getString ("amount"));
            result.put ("bankUrl", response.getString ("openUrl"));
            result.put ("redirectUrl", response.getString ("openUrl"));
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("扫码支付异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码支付失败"));
        }
    }

    /**
     * 获取用户token
     * @param mobile
     * @param pwd
     * @return
     */
    private String getUserToken(String mobile,String pwd){
        String redisKey = CacheConstants.REDIS_FULINGMEN_TOKEN + mobile;
        String token = "";
        if(redisCacheApi !=null){
            token = (String) redisCacheApi.get (redisKey);
            if (StringUtil.isNotEmpty (token)) {
                return token;
            }
        }
        JSONObject loginJson = login(mobile,pwd);
        if (loginJson == null || loginJson.isEmpty () || !loginJson.containsKey("token")) {
            return "";
        }
        token = loginJson.getString("token");
        if(StringUtil.isEmpty(token)){
            return "";
        }
        if(redisCacheApi !=null){
            redisCacheApi.set (redisKey, token, CacheConstants.DEFAULT_INVALID_TIMER_30);
        }
        return  token;
    }

    /**
     * 账号登陆
     */
    private static JSONObject login(String mobile,String pwd){
        pwd = DigestUtil.md5Hex(pwd+"superpw1234_!QAZ");
        String url = LOGIN_URL.replace("MOBILE_NO",mobile).replace("PASSWORD",pwd);
        String result = UrlUtil.httpPostHtml200(url,new JSONObject());
        logger.info("登录信息:{}",result);
        return JSON.parseObject(result);
    }

    private static JSONObject getQrCodeId(String mobile,String token){
        String url = QR_CORD_URL.replace("MOBILE_NO",mobile);
        JSONObject prms =  new JSONObject();
        prms.put("AccessToken",token);
        String result = UrlUtil.httpPostHtml200(url,prms);
        return JSON.parseObject(result);
    }

    private static JSONObject getPayData(String qrCodeId){
        JSONObject jsonObject = new JSONObject();
        String url = WX_APP_ID_URL.replace("QR_CODE_ID",qrCodeId);
        String result = UrlUtil.httpPostHtml200(url,new JSONObject());
        jsonObject.put("appId",result.split("\\|")[1]);
        jsonObject.put("channelCode",result.split("\\|")[2]);
        return jsonObject;
    }

    private static JSONObject createOrder(String payType,String amount,String qrCodeId,String channelCode,String appId) {
        String url = PRE_PAY_URL.replace("QR_CODE_ID",qrCodeId).replace("PAY_MONEY",amount).replace("APP_ID",appId).replace("CHANNEL_CODE",channelCode).replace("PAY_TYPE",payType);
        String result =  UrlUtil.httpPostHtml200(url,new JSONObject());
        return JSON.parseObject(result);
    }

    /**
     * 订单查询
     */
    public JSONObject orderMadeQuery(String mobile,String pwd,String qrUrl){
        String pOrderNo = this.getOrderNo(qrUrl);
        if(StringUtils.isEmpty(pOrderNo)){
            return Constant.failedMsg("未唤醒支付或未支付");
        }
        String token = this.getUserToken(mobile,pwd);
        if(StringUtils.isEmpty(token)){
            return Constant.failedMsg("获取token为空");
        }
        String url = ORDER_QUERY_URL.replace("MOBILE_NO",mobile).replace("ORDER_NO",pOrderNo);
        JSONObject prms =  new JSONObject();
        prms.put("AccessToken",token);
        String result = UrlUtil.httpPostHtml200(url,prms);
        logger.info("订单查询相应:{}",result);
        if(StringUtils.isEmpty(result)){
            return Constant.failedMsg("订单查询为空");
        }
        JSONObject queryJson = JSON.parseObject(result);
        if(queryJson == null || queryJson.isEmpty()){
            return Constant.failedMsg("订单查询解析为空");
        }
        JSONObject transjnls = queryJson.getJSONObject("transjnls");
        if(transjnls == null || transjnls.isEmpty() || !transjnls.containsKey("status")){
            return Constant.failedMsg("订单查询transjnls为空");
        }
        String status = transjnls.getString("status");
        if(StringUtils.isEmpty(status)){
            return Constant.failedMsg("订单查询status为空");
        }
        JSONObject resultStatus = Constant.successMsg("查询成功");
        resultStatus.put("status",status);
        resultStatus.put("pOrderNo",pOrderNo);
        return resultStatus;
    }

    private static JSONObject orderQuery(String mobile,String token,String orderNo){
        String url = ORDER_QUERY_URL.replace("MOBILE_NO",mobile).replace("ORDER_NO",orderNo);
        JSONObject prms =  new JSONObject();
        prms.put("AccessToken",token);
        String result = UrlUtil.httpPostHtml200(url,prms);
        return JSON.parseObject(result);
    }

    /**
     * 只有在支付完成后才能拿到这个单号
     * @param url
     * @return
     */
    private String getOrderNo(String url) {
        String result = HttpUtil.get(url);
        Pattern p= Pattern.compile("color:red\">(.*) 无");
        Matcher m=p.matcher(result);
        if (m.find()) {
            result = m.group(1);
        } else {
            result = "";
        }
        return result;
    }

    public static String getReal(String url){
        try {
            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(url);
            HttpParams params = client.getParams();
            params.setParameter("http.protocol.handle-redirects", false);
            method.setRequestHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.3(0x17000321) NetType/WIFI Language/zh_CN");
            client.executeMethod(method);
            String realUrl = method.getURI().getURI();
            System.out.printf("获取真实的地址成功：%s,\n原地址是：%s",realUrl,url);
            return realUrl;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.printf("获取地址失败，原来的地址是：%s",url);
            return "";
        }
    }
}

