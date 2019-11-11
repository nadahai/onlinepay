package com.vc.onlinepay.pay.order.h5;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.monitor.AsynNotice;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.BankCaseUtil;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.hanyin.HanYinUtils;
import com.vc.onlinepay.utils.http.HttpClientResult;
import com.vc.onlinepay.utils.http.HttpClientUtils;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * @author shadow
 */
@Service
public class UnionH5CommonServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(UnionH5CommonServiceImpl.class);

    @Autowired
    private RedisCacheApi redisCacheApi;
    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;
    @Autowired
    private AsynNotice asynNotice;

    private static final String SEND_SMS_PROCESS = "{\"p\":{\"qn\":\"${1}\"},\"t\":\"${2}\",\"s\":\"2\",\"d\":\"${3}\",\"l\":\"zh_CN\"}";
    private static final String SEND_PROCESS_URL = "https://mcashier.95516.com/mobile/sendSMSProcessing.action";
    private static final String SEND_SMS_URL = "https://mcashier.95516.com/mobile/sendSMS.action";
    private static final String SUBMIT_ORDER_URL = "https://mcashier.95516.com/mobile/cardPay.action";
    private static final String PAY_RESULT_URL = "https://mcashier.95516.com/mobile/cardPayResult.action";
    private static final String PAY_RESULT_DATA = "{\"p\":{},\"t\":\"${1}\",\"s\":\"2\",\"d\":\"${2}\",\"l\":\"zh_CN\"}";
    private static final String CHECK_PAY_URL = "https://mcashier.95516.com/mobile/cardPayProcessing.action";
    private static final String CHECK_PAY_DATA = "{\"p\":{},\"t\":\"${1}\",\"s\":\"2\",\"d\":\"${2}\",\"l\":\"zh_CN\"}";

    private static final BigDecimal MULTL_VAL = new BigDecimal(100);

    private static final String STEP_01_URL = "https://pay.clpay-ec.com/clpay/easypayweb?data=";
    private static final String STEP_02_URL = "https://pay.clpay-ec.com:443/clpay/easypaynewlocal";
    private static final String STEP_031_URL = "https://www.unspay.com/unspay/page/linkbank/payRequest.do";
    private static final String STEP_032_URL = "https://gateway.handpay.cn/hpayTransGatewayWeb/trans/debit.htm";
    private static final String STEP_041_URL = "https://gateway.95516.com/gateway/api/frontTransReq.do";


    private static final List<Integer> REDIRECT_CODE = new ArrayList<Integer>(7){{
        add(HttpStatus.SC_MULTIPLE_CHOICES);
        add(HttpStatus.SC_MOVED_PERMANENTLY);
        add(HttpStatus.SC_MOVED_TEMPORARILY);
        add(HttpStatus.SC_SEE_OTHER);
        add(HttpStatus.SC_NOT_MODIFIED);
        add(HttpStatus.SC_USE_PROXY);
        add(HttpStatus.SC_TEMPORARY_REDIRECT);
    }};

    private static final List<String> PAY_ERROR_CODE = new ArrayList<String>(4){{
        add("G9");
        add("64");
        add("61");
        add("65");
        add("67");
    }};

    /**
     * @描述 银联H5公共通用方法
     * @作者 shadow
     * @时间 2019/06/13 11:03
     */
    public VcOnlineOrderMade unionCommon(String no) throws IllegalArgumentException {
        if (StringUtils.isEmpty(no)) {
            throw new IllegalArgumentException("订单号为空");
        }
        String orderNo = HiDesUtils.desDeCode(no);
        if (orderNo == null || StringUtils.isEmpty(orderNo) || "0".equals(orderNo)) {
            throw new IllegalArgumentException("订单异常");
        }
        VcOnlineOrderMade made = onlineOrderMadeService.findOrderByOrderNo(orderNo);
        if (made == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        return made;
    }

    /**
     * 发送短信验证码
     *
     * @param transNumber 交易码
     * @param cardNumber  银行卡号
     * @param orderNo     订单号
     * @param mobile      手机号
     * @return
     */
    public JSONObject sendSmsCode(String transNumber,String cardNumber,String orderNo,String mobile) {
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
            String response = HanYinUtils.sendPost(SEND_SMS_URL, params.toJSONString());
            logger.info("获取短信验证码结果:{},订单号:{}",response,orderNo);
            if(!HanYinUtils.isJSON2(response)){
                return Constant.failedMsg("发送短信失败");
            }
            JSONObject smsRes = Constant.stringToJson(response);

            //存在QN 重发两次短信
            if(response.contains("\"qn\"") && null != smsRes.getJSONObject("p")){
                logger.info("\r\n=====>qn01");
                Object qn = smsRes.getJSONObject("p").get("qn");
                if(null != qn && !"".equals(qn) && !"null".equals(qn)) {
                    logger.info("\r\n=====>qn02:",qn);
                    /*${1}:qn, ${2}:transNumber ${3}:dfpSessionId */
                    String smsProcessJson = SEND_SMS_PROCESS
                            .replace("${1}", qn.toString())
                            .replace("${2}", transNumber)
                            .replace("${3}", dfpSessionId);
                    logger.info("\r\n=====>qn03: \r\n{}",smsProcessJson);
                    String result01 = HanYinUtils.sendPost(SEND_PROCESS_URL,smsProcessJson);
                    CompletableFuture.runAsync(()->{
                        // String result02 = "";
                        HttpClientResult result02 = null;
                        try {
                            Thread.sleep(500);
                            result02 = HttpClientUtils.doPostJsonStr(SEND_PROCESS_URL,smsProcessJson);
                            // result02 = HanYinUtils.sendPost(SEND_PROCESS_URL,smsProcessJson);
                        }catch(InterruptedException ex){
                            Thread.currentThread().isInterrupted();
                            logger.error("===>qn073 异常04 {}",orderNo,ex);
                        }catch (Exception e){
                            logger.error("===>qn072 ERROR:{}",orderNo,e);
                        }
                        logger.info("=====>qn071: {}|{}",orderNo,result02.toString());
                    });
                    logger.info("=====>qn06: {} | {}",orderNo,result01);
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


    /**
     * @描述 获取getDfpSessionId
     * @作者 nada
     * @时间 2019/5/23 17:25
     */
    public JSONObject getDfpSessionId(String transNumber,String callback,String encryptData,String orderNo) {
        String dfpSessionId = "";
        try {
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
     * @描述 支付订单
     * @作者 shadow
     * @时间 2019/5/27 9:57
     */
    public JSONObject paymentSubmit(String transNumber,String smsCode,String idNo,String no,String mobile,String password){
        String orderNo = HiDesUtils.desDeCode(no);
        try {
            String dfpSessionId = (String) redisCacheApi.get(transNumber);
            if(StringUtils.isAnyEmpty(transNumber,smsCode,dfpSessionId)){
                logger.error("支付订单参数不全,订单号：{}",orderNo);
                return Constant.failedMsg("支付订单参数不全");
            }
            // 构造参数P;卡密支付参数与无卡密支付参数区别
            JSONObject p = new JSONObject();
            p.put("smsCode",smsCode);
            p.put("discountInfo",new JSONObject());
            if(StringUtils.isNotBlank(password)){
                p.put("password",password);
            }else {
                p.put("credentialType", "01");
                if(StringUtil.isNotEmpty(idNo)){
                    p.put("credential", idNo);
                }
            }
            if (StringUtil.isNotEmpty(mobile)) {
                p.put("mobile", mobile);
            }
            JSONObject params = new JSONObject();
            params.put("p",p);
            params.put("t",transNumber);
            params.put("s","2");
            params.put("d",dfpSessionId);
            params.put("l","zh_CN");
            // logger.info("提交订单订单号:{},入参:{},地址:{}",orderNo,params,SUBMIT_ORDER_URL);
            String orderPayData = HanYinUtils.sendPost(SUBMIT_ORDER_URL, params.toJSONString());
            logger.info("支付订单订单号{},结果:{}",orderNo,orderPayData);
            if(StringUtil.isEmpty(orderPayData)){
                logger.error("支付失败,订单号：{}",orderNo);
                return Constant.failedMsg("支付响应为空");
            }
            JSONObject result = Constant.stringToJson(orderPayData);
            if(result !=null && result.containsKey("r") && "00".equalsIgnoreCase(result.getString("r"))){
                String reqData = CHECK_PAY_DATA.replace("${1}",transNumber).replace("${2}",dfpSessionId);
                String payResultReqData = PAY_RESULT_DATA.replace("${1}",transNumber).replace("${2}",dfpSessionId);
                String checkPayUrl = CHECK_PAY_URL.concat("?r=0.").concat(String.valueOf(RandomUtils.nextInt(0,-999)+System.currentTimeMillis()));
                HttpClientResult clientResult = HttpClientUtils.doPostJsonStr(checkPayUrl,reqData);
                logger.info("===>pay 支付进度查询01 {},reqData:{},clientResult:{}",orderNo,reqData,clientResult);
                JSONObject payResult = null;
                try {
                    Thread.sleep(500+RandomUtils.nextInt(50,200));
                    clientResult = HttpClientUtils.doPostJsonStr(checkPayUrl,reqData);
                    // logger.info("===>pay 支付进度查询02 {} | {}",orderNo,clientResult);
                    // InProcess状态的订单 需重新发送进度查询
                    if(clientResult.getContent().contains("InProcess")) {
                        CompletableFuture.runAsync(() -> {
                            try {
                                Thread.sleep(3000);
                                HttpClientResult clientResultRetry = HttpClientUtils.doPostJsonStr(checkPayUrl, reqData);
                                logger.info("===>pay 支付进度重试02+ {} | {}", orderNo, clientResultRetry);
                                // 重新查询支付结果
                                String payResultUrl = PAY_RESULT_URL.concat("?r=0.").concat(String.valueOf(RandomUtils.nextInt(0,999)+System.currentTimeMillis()));
                                clientResultRetry = HttpClientUtils.doPostJsonStr(payResultUrl,payResultReqData);
                                logger.info("===>pay 支付结果重试03+ {} | {} | {}",orderNo,payResultReqData,clientResultRetry);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().isInterrupted();
                                logger.error("===>pay 支付进度重试02+ {}", orderNo, ie);
                            } catch (Exception e) {
                                logger.info("===>pay 支付进度重试02+ {}", orderNo, e);
                            }
                        });
                        return Constant.successMsg("受理成功");
                    }

                    // 处理成功的进入支付结果查询
                    String payResultUrl = PAY_RESULT_URL.concat("?r=0.").concat(String.valueOf(RandomUtils.nextInt(0,999))).concat(String.valueOf(System.currentTimeMillis()));
                    clientResult = HttpClientUtils.doPostJsonStr(payResultUrl,payResultReqData);
                    logger.info("\r\n===>pay 支付结果查询03 {} | {} | {}",orderNo,payResultReqData,clientResult);
                    if(clientResult.getCode().equals(HttpStatus.SC_OK) && null != clientResult.getContent() && clientResult.getContent().contains("\"r\"")){
                        payResult = JSONObject.parseObject(clientResult.getContent());
                        //G9 64 61 65
                        if(PAY_ERROR_CODE.contains(payResult.get("r"))){
                            return Constant.failedMsg(payResult.getString("m"));
                        }
                        if("00".equals(payResult.get("r")) && "".equals(payResult.getString("m"))){
                            return Constant.successMsg("付款成功!");
                        }
                    }
                }catch(InterruptedException ex){
                    Thread.currentThread().isInterrupted();
                    logger.error("===>pay 支付进度查询异常04 {}",orderNo,ex);
                    return Constant.successMsg("受理成功");
                }catch (Exception e){
                    logger.error("===>pay 支付进度查询异常05 {}",orderNo,e);
                    return Constant.successMsg("受理成功");
                }
                return Constant.successMsg(null == payResult?"受理成功":(payResult.getString("r").concat(":").concat(payResult.getString("m"))));
            }
            return Constant.failedMsg(result.getString("m"));
        } catch (Exception e) {
            logger.error("支付订单异常,订单号{}",orderNo,e);
            return Constant.failedMsg("支付异常");
        }
    }

    /**
     * @描述 提交翰银接口
     * @作者 shadow
     * @时间 2019/06/13 17:31
     */
    public <M extends Map<String,String>> JSONObject payOrderProcess(String url, M params) {
        JSONObject returnJson = Constant.successMsg("ok");
        Map<String, String> stepParams = new HashMap<String, String>(32);
        HttpClientResult clientResult = null;
        String html = null;
        try {
            //STEP01
            clientResult = HttpClientUtils.doGet(url);

            while (true){
                // 判断是否请求错误
                if(clientResult.getCode().equals(HttpStatus.SC_INTERNAL_SERVER_ERROR)){
                    return Constant.commonError("解析出错:".concat(null == clientResult.getContent()?"":clientResult.getContent()));
                }
                // 判断是否是最终页面
                if(null != clientResult.getContent() && clientResult.getContent().contains("window.initObjectStr")){
                    break;
                }
                // 判断是否是form提交
                if(clientResult.getCode().equals(HttpStatus.SC_OK) && clientResult.getContent().trim().startsWith("<")){
                    html = clientResult.getContent();
                    if(html.contains(STEP_02_URL)){
                        clientResult = this.htmlValueProcess(clientResult.getContent(),STEP_02_URL,stepParams,params);
                        continue;
                    }
                    if(html.contains(STEP_031_URL)){
                        clientResult = this.htmlValueProcess(clientResult.getContent(),STEP_031_URL,stepParams,params);
                        continue;
                    }
                    if(html.contains(STEP_032_URL)){
                        clientResult = this.htmlValueProcess(clientResult.getContent(),STEP_032_URL,stepParams,params);
                        continue;
                    }
                    if(html.contains(STEP_041_URL)){
                        clientResult = this.htmlValueProcess(clientResult.getContent(),STEP_041_URL,stepParams,params);
                        continue;
                    }
                    // 异常
                    logger.error("订单请求错误01:{} | {} | {}",params.get("cardNo"),params.get("vcOrderNo"),clientResult.getContent());
                    return Constant.failedMsg("订单请求失败，请重新下单。");
                }
                // 判断是否是重定向
                if(REDIRECT_CODE.contains(clientResult.getCode())){
                    clientResult = this.redirectUrlProcess(clientResult.getHeaders());
                    continue;
                }
                logger.error("订单请求错误02:{} | {} | {}",params.get("cardNo"),params.get("vcOrderNo"),clientResult.getContent());
                return Constant.failedMsg("订单请求失败，请重新下单。");
            }

            //STEP_07 : 解析html最终参数
            this.finalParamsProcess(clientResult.getContent(),returnJson);
            // returnJson.put("content",clientResult.getContent());
            return returnJson;
        } catch (Exception e) {
            logger.error("请求错误:", e);
            return Constant.commonError("请求出错了!");
        }
    }

    private static final Pattern CALLBACK = Pattern.compile("\\((.*?)\\)");
    public  JSONObject getHanyinSessionId(String callback,String encryptData,String orderNo) {
        try {
            String getSessIonUrl = "https://device.95516.com/dcs_svc/rest/outer/dcs/dcsCollector"+"?callback="+callback+"&encryptData="+encryptData;
            logger.info("获取DfpSessionId订单号:{}入参:{}",orderNo,getSessIonUrl);
            String response = HanYinUtils.sendGet(getSessIonUrl,"");
            logger.info("获取SESSIOIN订单号:{},响应:{}",orderNo,response);
            String result = BankCaseUtil.getContent(response, CALLBACK);
            if(StringUtil.isEmpty(result)){
                return Constant.failedMsg("获取SESSIOIN信息失败");
            }
            JSONObject callData = Constant.stringToJson(result);
            logger.info("获取SESSIOIN响应callData:{}",response,callData);
            if(callData == null || callData.isEmpty() || !callData.containsKey("data")){
                return Constant.failedMsg("获取SESSIOIN信息失败");
            }
            JSONObject data = callData.getJSONObject("data");
            if(data == null || data.isEmpty() || !data.containsKey("dfpSessionId")){
                return Constant.failedMsg("获取SESSIOIN信息失败");
            }
            String dfpSessionId = data.getString("dfpSessionId");
            if(StringUtil.isEmpty(dfpSessionId)){
                return Constant.failedMsg("获取SESSIOIN信息失败");
            }
            JSONObject resultData = Constant.successMsg("OK");
            resultData.put("dfpSessionId",dfpSessionId);
            return resultData;
        } catch (Exception e) {
            logger.info("提交订单异常", e);
            return Constant.failedMsg("提交订单异常");
        }
    }

    private <M extends Map<String,String>> HttpClientResult htmlValueProcess(String html, String postUrl, Map<String,String> mapParams, M params){
        mapParams.clear();
        Document document = Jsoup.parse(html);

       switch (postUrl){
           case STEP_02_URL:
               mapParams.put("goodsName", document.select("input[name=goodsName]").val());
               mapParams.put("cardNo",params.get("cardNo")/*document.select("input[name=cardNo]").val()*/);
               mapParams.put("mercNo", document.select("input[name=mercNo]").val());
               String agtNo = document.select("input[name=agtNo]").val();
               if (null != agtNo && !"".equals(agtNo)) {
                   mapParams.put("agtNo", document.select("input[name=agtNo]").val());
               }
               mapParams.put("cardType", document.select("input[name=cardType]").val());
               mapParams.put("transType", document.select("input[name=transType]").val());
               mapParams.put("userId", document.select("input[name=userId]").val());
               mapParams.put("notifyUrl", document.select("input[name=notifyUrl]").val());
               mapParams.put("pageReturnUrl", document.select("input[name=pageReturnUrl]").val());
               mapParams.put("orderAmt", String.valueOf(new BigDecimal(params.get("orderAmt")).multiply(MULTL_VAL).intValue()));
               mapParams.put("orderNo", document.select("input[name=orderNo]").val());
               mapParams.put("orderIdIn", document.select("input[name=orderIdIn]").val());
               mapParams.put("orderTime", document.select("input[name=orderTime]").val());
               mapParams.put("phoneNo", document.select("input[name=phoneNo]").val());
               break;
           case STEP_031_URL:
               mapParams.put("currencyType", document.select("input[name=currencyType]").val());
               mapParams.put("responseMode", document.select("input[name=responseMode]").val());
               mapParams.put("commodity", document.select("input[name=commodity]").val());
               mapParams.put("amount", document.select("input[name=amount]").val());
               mapParams.put("orderId", document.select("input[name=orderId]").val());
               mapParams.put("bankCardType", document.select("input[name=bankCardType]").val());
               mapParams.put("remark", document.select("input[name=remark]").val());
               mapParams.put("frontURL", document.select("input[name=frontURL]").val());
               mapParams.put("accessMode", document.select("input[name=accessMode]").val());
               mapParams.put("version", document.select("input[name=version]").val());
               mapParams.put("cardNo", document.select("input[name=cardNo]").val());
               mapParams.put("mac", document.select("input[name=mac]").val());
               mapParams.put("assuredPay", document.select("input[name=assuredPay]").val());
               mapParams.put("merchantId", document.select("input[name=merchantId]").val());
               mapParams.put("time", document.select("input[name=time]").val());
               mapParams.put("merchantUrl", document.select("input[name=merchantUrl]").val());
               break;
           case STEP_032_URL:
               mapParams.put("insCode", document.select("input[name=insCode]").val());
               mapParams.put("orderNo", document.select("input[name=orderNo]").val());
               mapParams.put("backUrl", document.select("input[name=backUrl]").val());
               mapParams.put("signature", document.select("input[name=signature]").val());
               mapParams.put("accNo", document.select("input[name=accNo]").val());
               mapParams.put("insMerchantCode", document.select("input[name=insMerchantCode]").val());
               mapParams.put("frontUrl", document.select("input[name=frontUrl]").val());
               mapParams.put("idNumber", document.select("input[name=idNumber]").val());
               mapParams.put("telNo", document.select("input[name=telNo]").val());
               mapParams.put("nonceStr", document.select("input[name=nonceStr]").val());
               mapParams.put("hpMerCode", document.select("input[name=hpMerCode]").val());
               mapParams.put("paymentType", document.select("input[name=paymentType]").val());
               mapParams.put("orderTime", document.select("input[name=orderTime]").val());
               mapParams.put("orderAmount", document.select("input[name=orderAmount]").val());
               mapParams.put("name", document.select("input[name=name]").val());
               mapParams.put("currencyCode", document.select("input[name=currencyCode]").val());
               mapParams.put("merGroup", document.select("input[name=merGroup]").val());
               mapParams.put("productType", document.select("input[name=productType]").val());
               break;
           case STEP_041_URL:
               mapParams.put("channelType", document.select("input[name=channelType]").val());
               mapParams.put("subMerAbbr", document.select("input[name=subMerAbbr]").val());
               mapParams.put("acqInsCode", document.select("input[name=acqInsCode]").val());
               mapParams.put("txnSubType", document.select("input[name=txnSubType]").val());
               mapParams.put("txnAmt", document.select("input[name=txnAmt]").val());
               mapParams.put("version", document.select("input[name=version]").val());
               mapParams.put("signMethod", document.select("input[name=signMethod]").val());
               mapParams.put("backUrl", document.select("input[name=backUrl]").val());
               mapParams.put("merAbbr", document.select("input[name=merAbbr]").val());
               mapParams.put("encoding", document.select("input[name=encoding]").val());
               mapParams.put("subMerId", document.select("input[name=subMerId]").val());
               mapParams.put("merCatCode", document.select("input[name=merCatCode]").val());
               mapParams.put("signature", document.select("input[name=signature]").val());
               mapParams.put("orderId", document.select("input[name=orderId]").val());
               mapParams.put("txnType", document.select("input[name=txnType]").val());
               mapParams.put("frontUrl", document.select("input[name=frontUrl]").val());
               mapParams.put("currencyCode", document.select("input[name=currencyCode]").val());
               mapParams.put("merId", document.select("input[name=merId]").val());
               mapParams.put("subMerName", document.select("input[name=subMerName]").val());
               mapParams.put("accType", document.select("input[name=accType]").val());
               mapParams.put("accNo", document.select("input[name=accNo]").val());
               mapParams.put("certId", document.select("input[name=certId]").val());
               mapParams.put("merName", document.select("input[name=merName]").val());
               mapParams.put("bizType", document.select("input[name=bizType]").val());
               mapParams.put("accessType", document.select("input[name=accessType]").val());
               mapParams.put("txnTime", document.select("input[name=txnTime]").val());
               break;
           default:
               break;
       }
       return HttpClientUtils.doPostFormMap(postUrl,mapParams);
    }

    public HttpClientResult redirectUrlProcess(Header[] headers){
        HeaderElement[] headerElements = null;
        String redirectUrl = null;
        if(null != headers && headers.length>0){
            for(Header header:headers){
                headerElements = header.getElements();
                if(null != headerElements && headerElements.length>0){
                    for(HeaderElement element:headerElements){
                        if(element.getName().startsWith("https") || element.getName().startsWith("http")){
                            redirectUrl = element.getName().concat("=").concat(element.getValue());
                        }
                        if(null != redirectUrl){break;}
                    }
                }else{
                    continue;
                }
                if(null != redirectUrl){break;}
            }
        }
        if(StringUtils.isEmpty(redirectUrl)){
            logger.error("获取重定向出错啦~");
            return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        logger.info("重定向至：{}",redirectUrl);
        return HttpClientUtils.doGet(redirectUrl);
    }

    private static final Pattern REGEX_INIT_STR = Pattern.compile("window.initObjectStr(.*?)';");
    /**
     *  解析html值
     * @param html
     * @param respJson 值封装
     */
    public void finalParamsProcess(String html,JSONObject respJson){
        if(StringUtils.isEmpty(html)){
            return;
        }
        html = BankCaseUtil.getContent(html, REGEX_INIT_STR);
        // 剔除空格 和 “=‘” 字符
        html = html.replaceAll(" ","").substring(2);

        JSONObject paramJson = JSON.parseObject(html);
        // 交易码
        respJson.put("transNumber",paramJson.get("t"));
        paramJson = paramJson.getJSONObject("p");
        // exponent
        respJson.put("exponent",paramJson.get("exponent"));
        // 公钥
        respJson.put("publicKey",paramJson.get("modulus"));
        paramJson = paramJson.getJSONObject("postInfo").getJSONObject("displayCardInfo");
        // 银行卡编号
        respJson.put("visiablepan",paramJson.get("cardNumber"));
        // 填写规则
        respJson.put("rules",paramJson.get("rules"));
        // 卡号显示
        respJson.put("cardNumberDisplay",paramJson.get("cardNumberDisplay"));
        // 银行显示
        // respJson.put("bankName",paramJson.get("bankName"));
        // 手机显示
        respJson.put("phoneNumberDisplay",paramJson.get("phoneNumberDisplay"));
    }

    /**
     * @描述 翰银公共通用方法
     * @作者 nada
     * @时间 2019/5/28 11:03
     */
    public VcOnlineOrderMade h5PayCommon(String no) throws IllegalArgumentException{
        if (StringUtils.isEmpty(no)) {
            logger.error("订单号为空!");
            return null;
        }
        String orderNo = HiDesUtils.desDeCode(no);
        if (orderNo == null || StringUtils.isEmpty(orderNo) || "0".equals(orderNo)) {
            logger.error("解析订单号非法");
            return null;
        }
        VcOnlineOrderMade made = onlineOrderMadeService.findOrderByOrderNo(orderNo);
        if (made == null) {
            logger.error("订单号不存在");
            return null;
        }
        return made;
    }
}