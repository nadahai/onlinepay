package com.vc.onlinepay.pay.order.h5;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.delay.DelayTask;
import com.vc.onlinepay.delay.RetMessage;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.merch.XkPddBuyer;
import com.vc.onlinepay.persistent.mapper.merch.XkPddBuyerMapper;
import com.vc.onlinepay.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nada
 * @description  拼多多H5 支持微信H5、支付宝H5、支付宝扫码
 */
@Service
@Component
public class PddH5ServiceImpl {

    private static Pattern URL_PATTERN= Pattern.compile("var url=\"(.*)\";");

    private static Logger logger = LoggerFactory.getLogger(PddH5ServiceImpl.class);

    @Value("${onlinepay.project.domainName:}")
    private String domainName;

    @Value("${onlinepay.project.shortName:}")
    private String shortName;

//    public static Map<String, Boolean> PDD_ORDER_LOOP_NUM = new ConcurrentHashMap<>();

    @Autowired
    private RedisCacheApi redisCacheApi;

    @Autowired
    private XkPddBuyerMapper xkPddBuyerMapper;

    /**
     * @描述: 拼多多H5 下单
     */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info ("PDD订单信息payOrder:reqData{}", reqData);
            String orderNo = reqData.getString("vcOrderNo");
            String orderJson = reqData.getString("pdd_order_json");
            String accessToken = reqData.getString("accessToken");
            boolean loginFlag = checkLogin(accessToken);
            String loginName = reqData.getString("pdd_buyer_login_name");
            if(!loginFlag){
                xkPddBuyerMapper.closePddBuyer(new XkPddBuyer(loginName));
                return listener.failedHandler(Constant.failedMsg("店铺账号已过期"));
            }
            String skuNum = new BigDecimal(reqData.getString("amount")).divide(new BigDecimal(100)).toBigInteger().toString();
            if(Integer.valueOf(skuNum)<1){
                return listener.failedHandler(Constant.failedMsg("库存为空0个"));
            }
            orderJson = orderJson.replace("SKU_NUMBER",skuNum);
            // 9（支付宝wap），38（微信wap）
            String payWay = "";
            Integer type = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
            String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
            if (type == 2 || type == 10 || Constant.service_alipay.equals (service) || type == 22) {
                payWay = "9";
            }else{
                payWay = "38";
            }
            logger.info("拼多多H5接口上送入参：{}，支付方式为：{}",orderJson,payWay);
            JSONObject resJson = preCreate(orderJson,accessToken,payWay);
            logger.info("拼多多H5接口返参{}",resJson);
            if(resJson == null){
                return listener.failedHandler(Constant.failedMsg("下单为空"));
            }
            String orderSn = resJson.containsKey("orderSn")?resJson.getString("orderSn"):"";
            String payUrl = resJson.containsKey("payUrl")?resJson.getString("payUrl"):"";
            if(StringUtils.isNotEmpty(orderSn) && StringUtils.isEmpty(payUrl)){
                xkPddBuyerMapper.closePddBuyer(new XkPddBuyer(loginName));
                return listener.failedHandler(Constant.failedMsg("买家号异常"));
            }
            if(StringUtils.isNotEmpty(payUrl)){
                result.put("orderNo", orderNo);
                result.put("code", Constant.SUCCESSS);
                result.put("redirectUrl",resJson.getString("payUrl"));
                result.put("bankUrl",resJson.getString("payUrl"));
                result.put("payUrl",resJson.getString("payUrl"));
                result.put("pOrderNo",resJson.getString("orderSn"));
                result.put("orderJson",orderJson);
                result.put("msg", "下单成功");
                // 开始timer进行订单查询
                //PDD_ORDER_LOOP_NUM.put(orderNo,true);
                redisCacheApi.set(orderNo,true,660000L);
                DelayTask.getInstance().put(orderNo,  new RetMessage(reqData.getString ("projectDomainUrl")+"/onlineOrderQueryApi/queryAndUpdate"));
                //ThreadUtil.execute(()-> asyncLoopGetOrderResult(orderNo));
                return listener.successHandler(result);
            }else{
                return listener.failedHandler(Constant.failedMsg( "下单失败"));
            }
        } catch (Exception e) {
            logger.error("拼多多H5支付下单异常", e);
            return listener.paddingHandler(Constant.failedMsg("下单异常"));
        }
    }

    /**
     * pay_type 1支付宝 ，2 微信
     * 查询订单
     */
    public JSONObject orderQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            logger.info("PDD ，xpay查询入参：{}",reqData);
            result.put("vcOrderNo", reqData.get("vcOrderNo"));
            result.put("amount", reqData.get("amount"));
            result.put("merchantId", reqData.get("merchantId"));
            result.put("orderNo", reqData.get("orderNo"));
            String accessToken = StringUtils.deleteWhitespace(reqData.getString("upMerchKey"));
            String orderSn = StringUtils.deleteWhitespace(reqData.getString("plOrderNo"));
            boolean res = orderQuery(accessToken,orderSn);
            if(!res){
                result.put("code", Constant.UNKNOW);
                result.put("msg", "待支付");
                result.put("status", 1);
                return listener.paddingHandler(result);
            }
//            // 调用接口自动确认收货
//            ThreadUtil.execute(()-> confirmReceiving(orderSn,accessToken,false));
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "支付成功");
            result.put("pOrderNo",orderSn);
            result.put("status", 4);
//            PDD_ORDER_LOOP_NUM.remove(reqData.getString("vcOrderNo"));
            redisCacheApi.remove(reqData.getString("vcOrderNo"));
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("PDD订单查询异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "查询异常");
            result.put("status", 6);
            return listener.failedHandler(result);
        }
    }

//    private void confirmReceiving(String orderSN,String accessToken,boolean flag){
//        if(flag){
//            logger.info("PDD确认收货接口调用入参：{}----{}",orderSN,accessToken);
//            String result = HttpRequest.post("https://api.pinduoduo.com/order/"+orderSN+"/received").header("accesstoken",accessToken).execute().body();
//            logger.info("PDD确认收货接口调用返回：{}",result);
//        } else {
//            ScheduledExecutorService executorService =
//                    new ScheduledThreadPoolExecutor(1,
//                            new BasicThreadFactory.Builder().namingPattern("asyncConfirmPddOrder-schedule-pool-%d").daemon(true).build());
//            executorService.schedule(() -> confirmReceiving(orderSN,accessToken,true), 3, TimeUnit.MINUTES);
//        }
//    }

    /**
     *  PDD 登录校验
     * */
    private boolean checkLogin(String accessToken){
        try{
            String url = "https://apiv2.pinduoduo.net/api/galilei/refresh/token";
            logger.info("PDD校验登录信息：{}，地址：{}",accessToken,url);
            String result = HttpRequest.post(url).header("accesstoken",accessToken).execute().body();
            JSONObject jsonObject = JSON.parseObject(result);
            logger.info("PDD校验登录信息：{}",jsonObject);
            return jsonObject != null && jsonObject.containsKey("access_token") && StringUtils.isNotEmpty(jsonObject.getString("access_token"));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     *  PDD 查询上送
     * */
    private boolean orderQuery(String accessToken,String orderSn){
        try{
            String url = "https://api.pinduoduo.com/order/"+orderSn;
            logger.info("PDD查询订单信息：{}，地址：{}",accessToken,url);
            String result = HttpRequest.post(url).header("accesstoken",accessToken).execute().body();
            JSONObject jsonObject = JSON.parseObject(result);
            logger.info("PDD查询订单返回：{}",jsonObject);
            return jsonObject != null && jsonObject.containsKey("pay_status") && "2".equals(jsonObject.getString("pay_status"));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

//    private void asyncLoopGetOrderResult(String vcOrderNo){
//        if(PDD_ORDER_LOOP_NUM.containsKey(vcOrderNo)) {
//            int loopNum = PDD_ORDER_LOOP_NUM.get(vcOrderNo);
//            StaticLog.info("第{}次异步查询PDD订单，订单信息为{}", 7-loopNum, vcOrderNo);
//            PDD_ORDER_LOOP_NUM.put(vcOrderNo, (loopNum - 1));
//            if (loopNum >= 0) {
//                VcOnlineOrder vcOnlineOrder = vcOnlineOrderService.findOrderByOrderNo(vcOrderNo);
//                if (vcOnlineOrder != null && vcOnlineOrder.getStatus() == 1) {
//                    doQueryOrder(vcOrderNo);
//                } else {
//                    StaticLog.info("PDD，订单为{}处理成功", vcOrderNo);
//                }
//                ScheduledExecutorService executorService =
//                        new ScheduledThreadPoolExecutor(1,
//                                new BasicThreadFactory.Builder().namingPattern("asyncLoopGetPddOrder-schedule-pool-%d").daemon(true).build());
//                executorService.schedule(() -> asyncLoopGetOrderResult(vcOrderNo), 40, TimeUnit.SECONDS);
//            }
//        } else {
//            PDD_ORDER_LOOP_NUM.remove(vcOrderNo);
//        }
//    }
//
//    private void doQueryOrder(String vcOrderNo){
//        String noticeUrl = domainName+"/"+shortName+"/onlineOrderQueryApi/queryAndUpdate";
//        JSONObject prms = new JSONObject();
//        prms.put("orderNo",vcOrderNo);
//        try {
//            HttpClientTools.baseHttpSendPost(noticeUrl, prms);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * @desc 创建订单
     * @author nada
     * @create 2019/6/11 17:43
    */
    private JSONObject preCreate(String orderJson, String accessToken,String payWay){
        JSONObject params = new JSONObject();
        try{
            logger.info("创建订单入参:{}",orderJson);
            String result = HttpRequest.post("https://api.pinduoduo.com/order")
                    .header("Content-Type","application/json")
                    .header("accesstoken",accessToken).body(orderJson).execute().body();
            logger.info("创建订单1店铺原因:{}",result);
            JSONObject jsonObject = JSON.parseObject(result);
            if(!jsonObject.containsKey("order_sn")){
                return Constant.failedMsg("店铺异常");
            }
            String orderSn = jsonObject.getString("order_sn");
            params.put("orderSn",orderSn);
            jsonObject = JSON.parseObject("{\n" +"\t\"order_sn\": \""+orderSn+"\",\n" +"\t\"app_id\": "+payWay+"\n" +"}");
            result = HttpRequest.post("https://api.pinduoduo.com/order/prepay")
                    .header("Content-Type","application/json")
                    .header("accesstoken",accessToken).body(jsonObject.toJSONString()).execute().body();
            logger.info("创建订单2买家原因:{}",result);
            jsonObject = JSON.parseObject(result);
            if(jsonObject.containsKey("error_code")){
                JSONObject res = Constant.failedMsg("买家异常");
                res.put("orderSn",orderSn);
                return res;
            }
            if("38".equals(payWay)){
                String wxUrl = jsonObject.getString("mweb_url");
                params.put("payUrl",getWxUrl(wxUrl));
            }else{
                params.put("payUrl",getAliUrl(jsonObject.getString("gateway_url")+"?",jsonObject.getJSONObject("query")));
            }
            return params;
        }catch (Exception e){
            logger.error("创建订单异常异常：{}",orderJson,e);
            return Constant.failedMsg("创建订单异常异常");
        }
    }

    /** 获取微信支付链接 */
    private static String getWxUrl(String url){
        String result = HttpRequest.get(url)
                .header("Referer","https://mobile.yangkeduo.com")
                .execute().body();
        Matcher m=URL_PATTERN.matcher(result);
        if (m.find()) {
            result = m.group(1);
        } else {
            return "";
        }
        return result;
    }

    /** 获取支付宝支付链接 */
    private static String getAliUrl(String gatewayUrl,JSONObject paramJson) throws UnsupportedEncodingException {
        StringBuilder result= new StringBuilder(gatewayUrl);
        for (Map.Entry<String, Object> entry : paramJson.entrySet()) {
            result.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue()==null?"":entry.getValue().toString(),"UTF-8")).append("&");
        }
        return result.toString();
    }

}
