package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.pay.order.union.HanYinUnionServiceImpl;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineOrderMadeMapper;
import com.vc.onlinepay.persistent.service.channel.ChannelSubNoServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.hanyin.HanYinUtils;
import com.vc.onlinepay.utils.http.HttpClientResult;
import com.vc.onlinepay.utils.http.HttpClientUtils;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @描述:快捷包装支付H5快捷
 * @时间:2017年12月19日 下午2:57:29 
 */
@Service
public class CustomerH5ServiceImpl {

    private static Logger logger = LoggerFactory.getLogger(CustomerH5ServiceImpl.class);
    private static final String insCode = "80000932";
    private static final String insMerchantCode = "887581298603601";
    private static final String hpMerCode = "WCJRWDTFDIRWK2691@20190509104518";
    private static final String createOrderUrl = "https://gateway.handpay.cn/hpayTransGatewayWeb/trans/debit.htm";
    private static final String submitOrderUrl = "https://gateway.95516.com/gateway/api/frontTransReq.do";
    private static final String telMobileUrl = "https://mcashier.95516.com/mobile/sendSMS.action";
    private static final String signKey = "zY6ROFPqshB3mk4k5dzyPrgfpIZgEvp0yL23uoU3s/fVWD6qtaDTzIoGFJBrYuCF zkzyDW+UZmrRGRB1uUOxpkFf3v0D2rWfuIMixyTm8zh2Xfe6QD7pOkKRkgLyLrnk 7WCFJv+QruF7mOONxwLYIYoj+SfxCOcDhot7ua1PFQY=";
    private static final String SEND_SMS_PROCESS = "{\"p\":{\"qn\":\"${1}\"},\"t\":\"${2}\",\"s\":\"2\",\"d\":\"${3}\",\"l\":\"zh_CN\"}";
    private static final String SEND_PROCESS_URL = "https://mcashier.95516.com/mobile/sendSMSProcessing.action";
    @Autowired
    private RedisCacheApi redisCacheApi;
    @Autowired
    private VcOnlineOrderMadeMapper vcOnlineOrderMadeMapper;
	@Value("${onlinepay.project.baseUrl}")
	public String base_url;

    /**
    * @描述 快捷包装H5下单
    * @作者 nada
    * @时间 2019/5/28 10:09
    */
    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = Constant.successMsg("下单成功");
        try {
            result.put("orderNo", reqData.getString("vcOrderNo"));
            String notifyUrl = reqData.getString ("projectDomainUrl") + "/natieCallBackController";
            String openUrl = reqData.getString("projectDomainUrl") + "/hyh5api/cashier/" + HiDesUtils.desEnCode(reqData.getString("vcOrderNo"));
            VcOnlineOrderMade made = VcOnlineOrderMade.buildAlipayMade (reqData,openUrl);
            made.setQrcodeUrl (notifyUrl);
            int r = vcOnlineOrderMadeMapper.save(made);
            if (r < 1) {
                return listener.failedHandler (Constant.failedMsg ("保存链接失败"));
            }
            result.put("pOrder", reqData.getString("vcOrderNo"));
            result.put ("redirectUrl", StringEscapeUtils.unescapeJava (made.getOpenUrl ()));
            result.put ("bankUrl", StringEscapeUtils.unescapeJava (made.getOpenUrl ()));
            result.put ("isUpdateStatus",false);
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error("翰银H5下单异常", e);
            return listener.failedHandler(Constant.failedMsg("翰银H5下单失败"));
        }
    }

    /**
     * @描述 翰银下单接口
     * @作者 nada
     * @时间 2019/5/28 9:40
     */
     public JSONObject createOrder(String accNo,String orderNo,int orderAmount,int channelId){
         try {
         	 String channelKeyDes = HanYinUtils.getDecodeChannlKey(signKey);
             String signKey = Constant.getChannelKeyDes(channelKeyDes, 2);

             if(StringUtils.isAnyEmpty(insCode,insMerchantCode,signKey,hpMerCode)){
             	logger.info("下单参数不全,订单号：{}",orderNo);
                 return Constant.failedMsg("下单参数不全");
             }
             String notifyUrl = base_url + "/natieCallBackController";
             JSONObject prams = new JSONObject();
             prams.put("orderNo", orderNo);
             prams.put("insCode", insCode);
             prams.put("channelLabel", channelId);
             prams.put("backUrl",notifyUrl);
             prams.put("accNo", accNo);
             prams.put("insMerchantCode", insMerchantCode);
             prams.put("frontUrl", base_url+"/success");
             prams.put("nonceStr", Constant.getRandomString(6));
             prams.put("paymentType", "2008");
             prams.put("hpMerCode", hpMerCode);
             prams.put("orderAmount", orderAmount);
             prams.put("orderTime", Constant.getDateString());
             prams.put("currencyCode", "156");
             prams.put("merGroup", "1");
             prams.put("productType", "100000");

             //2 请求快捷包装，创建订单
             logger.info("创建订单请求orderNo:{},入参:{}",orderNo,prams);
             JSONObject orderReqData = HanYinUtils.postHanyinCreateOrder(createOrderUrl,prams);
             logger.info("创建订单请求orderNo,{}结果:{}",orderNo,orderReqData);
             if(orderReqData == null || orderReqData.isEmpty()){
             	logger.info("创建订单为空,订单号：{}",orderNo);
                 return Constant.failedMsg("创建订单为空");
             }
             String message = orderReqData.containsKey("message")?orderReqData.getString("message"):"创建订单失败";
             if(!orderReqData.containsKey("params") && !orderReqData.containsKey("action")){
             	logger.info("创建订单失败,未找到params，action。订单号：{}",orderNo);
                 return Constant.failedMsg(message);
             }
             JSONObject params = orderReqData.getJSONObject("params");
             JSONObject response = HanYinUtils.postUnionSubmitOrder(submitOrderUrl,params);
             logger.info("获取orderNo:{},response:{}",orderNo,response);
             if(response == null || response.isEmpty()){
             	logger.info("提交订单为空,订单号：{}",orderNo);
                 return Constant.failedMsg("提交订单为空");
             }
             if(response == null || response.isEmpty() || !response.containsKey("transNumber")){
                 logger.info("提交订单失败,订单号：{}",orderNo);
                 return Constant.failedMsg("提交订单失败");
             }
             String transNumber = response.getString("transNumber");
             if(StringUtils.isEmpty(transNumber)){
                 logger.info("提交订单失败,订单号：{}",orderNo);
                 return Constant.failedMsg("提交订单失败");
             }
             response.put("message","快捷订单成功");
             return response;
         } catch (Exception e) {
             logger.error("创建订单异常,订单号{}",orderNo);
             return Constant.failedMsg("创建订单异常");
         }
     }

    /**
     * @描述 获取getDfpSessionId
     * @作者 nada
     * @时间 2019/5/23 17:25
     */
    public JSONObject getDfpSessionId(String transNumber,String callback,String encryptData,String orderNo) {
        try {
            String dfpSessionId = "";
            if(redisCacheApi.get(transNumber) == null || !redisCacheApi.exists(transNumber)){
                JSONObject sessionData = HanYinUtils.getHanyinSessionId(callback,encryptData,orderNo);
                dfpSessionId = sessionData.getString("dfpSessionId");
                redisCacheApi.set(transNumber,dfpSessionId);
            }else{
                dfpSessionId = (String) redisCacheApi.get(transNumber);
            }
            if(StringUtil.isNotEmpty(dfpSessionId)){
                JSONObject result = Constant.successMsg("获取DfpSessionId成功");
                result.put("dfpSessionId",dfpSessionId);
                return result;
            }
            return Constant.failedMsg("获取DfpSessionId失败");
        } catch (Exception e) {
            logger.error("获取DfpSessionId异常",e);
            return  Constant.failedMsg("获取DfpSessionId失败");
        }
    }

    /**
     * @描述 获取短信验证码
     * @作者 shadow
     * @时间 2019/5/23 17:25
     */
    public JSONObject sendSms(String transNumber,String cardNumber,String orderNo,String mobile) {
        try {
            String dfpSessionId = (String) redisCacheApi.get(transNumber);
            if(StringUtils.isAnyEmpty(transNumber,cardNumber,dfpSessionId)){
                logger.error("短信参数不全，订单号:{}",orderNo);
                return Constant.failedMsg("短信参数不全");
            }
            JSONObject p = new JSONObject();
            p.put("smsType","UnionSMS");
            if(StringUtil.isNotEmpty(mobile)){
                p.put("mobile",mobile);
            }
            JSONObject params = new JSONObject();
            params.put("p",p);
            params.put("t",transNumber);
            params.put("s","2");
            params.put("d",dfpSessionId);
            params.put("l","zh_CN");
            logger.info("获取短信验证码:{},入参:{}",orderNo,params);
            String response = HanYinUtils.sendPost(telMobileUrl, params.toJSONString());
            logger.info("获取短信验证码结果:{},订单号:{}",response,orderNo);
            if(!HanYinUtils.isJSON2(response)){
                return Constant.failedMsg("发送短信失败");
            }
            JSONObject smsRes = Constant.stringToJson(response);

            //存在QN 重发两次短信
            if(response.contains("\"qn\"") && null != smsRes.getJSONObject("p")){
                // logger.info("\r\n=====>qn01");
                Object qn = smsRes.getJSONObject("p").get("qn");
                if(null != qn && !"".equals(qn) && !"null".equals(qn)) {
                    // logger.info("\r\n=====>qn02:",qn);
                    /*${1}:qn, ${2}:transNumber ${3}:dfpSessionId */
                    String smsProcessJson = SEND_SMS_PROCESS
                            .replace("${1}", qn.toString())
                            .replace("${2}", transNumber)
                            .replace("${3}", dfpSessionId);
                    // logger.info("\r\n=====>qn03: \r\n{}",smsProcessJson);
                    String result01 = HanYinUtils.sendPost(SEND_PROCESS_URL,smsProcessJson);
                    CompletableFuture.runAsync(()->{
                        // String result02 = "";
                        HttpClientResult result02 = null;
                        try {
                            Thread.sleep(500);
                            result02 = HttpClientUtils.doPostJsonStr(SEND_PROCESS_URL,smsProcessJson);
                        }catch(InterruptedException ex){
                            Thread.currentThread().isInterrupted();
                            logger.error("===>qn073 异常04 {}",orderNo,ex);
                        }catch (Exception e){
                            logger.error("===>qn072 ERROR:{}",orderNo,e);
                        }
                    });
                }
            }
            if(smsRes !=null && !smsRes.isEmpty() && smsRes.get("r").equals("00")){
                return Constant.successMsg("发送短信成功");
            }
            return Constant.failedMsg(smsRes.getString("m"));
        } catch (Exception e) {
            logger.error("获取短信验证码异常,订单号:{}",orderNo,e);
            return  Constant.failedMsg("发送短信失败");
        }
    }
}

