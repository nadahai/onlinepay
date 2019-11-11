package com.vc.onlinepay.pay.order.scan;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.StaticLog;
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
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:安徽收钱吧
 * @时间:2018年5月15日 22:14:30
 */
@SuppressWarnings ("deprecation")
@Service
@Component
public class AnHuiShouQianBaScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (AnHuiShouQianBaScanServiceImpl.class);
    @Autowired
    private ChannelSubNoServiceImpl channelSubNoServiceImpl;
    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;
    @Autowired
    private RedisCacheApi redisCacheApi;

    private static String API_URL = "https://mapi.shouqianba.com/V2/";
    private static String API_DOMAIN = "https://api.shouqianba.com";
    private static String PAY_URL = "https://qr.shouqianba.com/gateway";
    private static String precreateUrl = API_DOMAIN + "/upay/v2/precreate";

    /**
     * @描述:扫码支付下单
     * @时间:2018年5月15日 22:14:30
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("扫码接口入参{}", reqData);
            String channelKey = reqData.containsKey ("channelDesKey") ? reqData.getString ("channelDesKey").trim () : "";
            String upMerchNo = reqData.containsKey ("channelKey") ? reqData.getString ("channelKey").trim () : "";
            String urlCallback = reqData.getString ("projectDomainUrl") + "/shouQianBaPayCallbackApi";
            String vcOrderNo = reqData.containsKey ("vcOrderNo") ? reqData.getString ("vcOrderNo").trim () : "";
            channelSubNoServiceImpl.updateLastOrderTime(new ChannelSubNo (upMerchNo));
            BigDecimal money = reqData.getBigDecimal ("amount");
            if (StringUtils.isAnyEmpty (upMerchNo, vcOrderNo)) {
                return listener.failedHandler (Constant.failedMsg ("账号为空"));
            }
            Integer type = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
            String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
            //payWay  1:支付宝 3:微信 4:百付宝 5:京东钱包 6:QQ钱包 18:翼支付
            String payWay = "3";
            int openType = reqData.getIntValue ("channelSource");
            if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
                payWay = "1";
                openType = 92;
            }
            Date beginDate = new Date ();
            JSONObject terminalInfoJson = this.getTerminalInfo (upMerchNo, channelKey);
            if (terminalInfoJson == null || terminalInfoJson.isEmpty ()) {
                return listener.failedHandler (Constant.failedMsg ("设备信息失败"));
            }
            if(!terminalInfoJson.getString ("code").equalsIgnoreCase (Constant.SUCCESSS)){
                String mgs = terminalInfoJson.containsKey ("msg")?terminalInfoJson.getString ("msg"):"设备登录失败";
                return listener.failedHandler (Constant.failedMsg (mgs));
            }
            String terminalInfo = terminalInfoJson.getString ("msg");
            String payUrl = preCreate (terminalInfo, vcOrderNo, String.valueOf (Constant.changeBranch (money).intValue ()), urlCallback + "/returnWap", urlCallback, payWay);
            long between = DateUtil.between (beginDate, new Date (), DateUnit.SECOND);
            StaticLog.info ("收钱吧下单总耗时：{}s,响应:{}", between, payUrl);
            if (StringUtil.isEmpty (payUrl)) {
                return listener.failedHandler (Constant.failedMsg ("扫码下单超时"));
            }
            VcOnlineOrderMade made = VcOnlineOrderMade.buildCommonMade (reqData);
            made.setOpenType (openType);
            made.setQrcodeUrl (payUrl);
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
     * @描述:获取终端设置信息
     * @作者:nada
     * @时间:2019/4/19
     **/
    public JSONObject getTerminalInfo (String username, String password) {
        String redisKey = CacheConstants.REDIS_SHOUQIANBA_TERMINANINFO + username;
        String terminalInfo = "";
        if(redisCacheApi !=null){
            terminalInfo = (String) redisCacheApi.get (redisKey);
            if (StringUtil.isNotEmpty (terminalInfo)) {
                return Constant.successMsg (terminalInfo);
            }
        }
        String terminalSn = "";
        String terminalKey = "";
        // 1 登录接口 获得 获取设备接口的token
        String loginUrl = API_URL + "Account/login";
        JSONObject dataJson = new JSONObject ();
        dataJson.put ("client_version", "4.3.5");
        dataJson.put ("device_model", "1");
        dataJson.put ("os_type", "1");
        dataJson.put ("username", username);
        dataJson.put ("password", DigestUtil.md5Hex ((password)));
        String loginResult = HttpUtil.post (loginUrl, dataJson);
        JSONObject loginResultJson = JSONObject.parseObject (loginResult);
        logger.info ("获取设备登录信息:{}",loginResultJson);

        // 2 获取设备
        if(loginResultJson == null || loginResultJson.isEmpty () || !loginResultJson.containsKey ("data")){
            return  Constant.failedMsg (loginResultJson.toString ());
        }
        JSONObject  loginResultJsonData = loginResultJson.getJSONObject ("data");
        if(loginResultJsonData == null || loginResultJsonData.isEmpty ()){
            return  Constant.failedMsg (loginResultJson.toString ());
        }
        JSONObject store = loginResultJsonData.containsKey ("store")?loginResultJsonData.getJSONObject ("store"):null;
        if(store == null){
            store = loginResultJsonData.containsKey ("cash_store")?loginResultJsonData.getJSONObject ("cash_store"):null;
        }
        if(store == null){
            return  Constant.failedMsg (loginResultJson.toString ());
        }
        String wosaiStoreId = store.getString ("sn");
        String storeName = store.getString ("name");
        dataJson.remove ("username");
        dataJson.remove ("password");
        String token = loginResultJson.getJSONObject ("data").getJSONObject ("admin").getString ("newToken");
        dataJson.put ("deviceId", username);
        dataJson.put ("wosaiStoreId", wosaiStoreId);
        dataJson.put ("token", token);

        String terminalUrl = API_URL + "Terminal/getTerminal";
        String terminalResult = HttpUtil.post (terminalUrl, dataJson);
        JSONObject terminalResultJson = JSONObject.parseObject (terminalResult);
        terminalSn = terminalResultJson.getJSONObject ("data").getString ("sn");
        terminalKey = terminalResultJson.getJSONObject ("data").getString ("current_secret");
        if (StringUtils.isAnyEmpty (terminalSn, terminalKey, username)) {
            StaticLog.info ("收钱吧获取设备信息为空：{},{},{}", terminalSn, terminalKey, username);
            return  Constant.failedMsg (terminalResult.toString ());
        }
        terminalInfo = terminalSn + ";" + terminalKey + ";" + username+";"+storeName;
        if(redisCacheApi !=null){
            redisCacheApi.set (redisKey, terminalInfo, CacheConstants.DEFAULT_INVALID_TIMER_1H);
        }
        return Constant.successMsg (terminalInfo);
    }

    /**
     * @描述:收钱吧下单接口
     * @作者:nada
     * @时间:2019/5/2
     **/
    public String preCreate (String terminalInfo, String orderNo, String amount, String urlRetuanback, String urlCallback, String payWay) {
        JSONObject params = new JSONObject ();
        try {
            String[] terminalInfos = terminalInfo.split (";");
            if(terminalInfos == null || terminalInfos.length<1){
                logger.info ("收钱吧下单信息不完整:{}",terminalInfo);
                return "";
            }
            String terminalSn = terminalInfos[0];
            String terminalKey = terminalInfos[1];
            String username = terminalInfos[2];
            String storeName = "在线支付";
            if(terminalInfos.length>3){
                storeName = terminalInfo.split (";")[3];
            }

            if (StringUtils.isAnyEmpty (terminalSn, terminalKey, username)) {
                logger.info ("收钱吧下单信息不完整:{},{},{}", terminalSn, terminalKey, username);
                return "";
            }
            params.put ("terminal_sn", terminalSn);
            params.put ("operator", username);
            params.put ("client_sn", orderNo);
            params.put ("total_amount", amount);
            params.put ("subject", storeName);
            params.put ("notify_url", urlCallback);
            params.put ("return_url", urlRetuanback);
            //params.put ("payway", payWay);
            String param = Md5CoreUtil.getSignStr (params) + "&key=" + terminalKey;
            String sign = DigestUtil.md5Hex (param);
            param = Md5CoreUtil.getSignStr (params) + "&sign=" + sign.toUpperCase ();
            logger.info ("收钱吧下单入参:{}", params);
            return PAY_URL + "?" + param;

           /*String sign = DigestUtil.md5Hex(params.toString() + terminalInfo.split(";")[1]);
            String result = HttpRequest.post(url)
                .header("Authorization",terminalInfo.split(";")[0] + " " + sign)
                .header("Content-Type","application/json")
                .body(params.toJSONString())
                .execute()
                .body();
            return  JSONObject.parseObject(result);*/
        } catch (Exception e) {
            return null;
        }
    }

    public static void main (String[] args) {
        AnHuiShouQianBaScanServiceImpl anHuiShouQianBaScanService = new AnHuiShouQianBaScanServiceImpl();
        //payWay  1:支付宝 3:微信 4:百付宝 5:京东钱包 6:QQ钱包 18:翼支付
        String payWay = "1";
        String url = "https://www.shouqianba.com";
        Date beginDate = new Date ();
        JSONObject terminalInfoJson = anHuiShouQianBaScanService.getTerminalInfo ("17626024585", "aa147258");
        String terminalInfo = terminalInfoJson.getString ("msg");
        StaticLog.info ("收钱吧终端信息：{}", terminalInfo);
        String response = anHuiShouQianBaScanService.preCreate (terminalInfo, RandomUtil.randomString (16), "200", "3", url,payWay);
        StaticLog.info ("收钱吧下单结果：{}", response);
        long between = DateUtil.between (beginDate, new Date (), DateUnit.SECOND);
        StaticLog.info ("收钱吧下单总耗时：{}s", between);
    }
}

