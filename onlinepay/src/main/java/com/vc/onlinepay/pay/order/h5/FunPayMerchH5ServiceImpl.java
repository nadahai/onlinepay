package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundAuthOrderAppFreezeModel;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayFundAuthOrderAppFreezeRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayFundAuthOrderAppFreezeResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineOrderMadeMapper;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:企业支付宝下单
 * @时间:2018年5月15日 22:14:30
 */
@SuppressWarnings ("deprecation")
@Service
@Component
public class FunPayMerchH5ServiceImpl {

    private Logger logger = LoggerFactory.getLogger (getClass ());

    @Autowired
    private FunPayH5ServiceImpl funPayH5Service;

    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;

    @Autowired
    private VcOnlineOrderMadeMapper vcOnlineOrderMadeMapper;

    private static Map<String, Integer> appIdMaps = new HashMap<String, Integer>();

    private static final String ALIPAY_API_URL = "https://openapi.alipay.com/gateway.do";

    /**
     * @描述:企业支付宝下单
     * @时间:2018年5月15日 22:14:30
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
//            logger.info ("企业支付宝下单入参{}", reqData);
            //企业转账开关
            /*String alipayAppSwitch = coreEngineProviderService.getCacheCfgKey ("online.alipay.merchApp.switch");
            if ("true".equals (alipayAppSwitch)) {
                VcOnlineOrderMade made = VcOnlineOrderMade.buildAlipayMade (reqData);
                return  funPayH5Service.buildAliPayApp (reqData,made, listener);
            }*/
            //收款类型 1:当面付 2:收款码 3：公平轮询 4 口碑 5 手机网站H5
            int appType = reqData.containsKey ("appType")?reqData.getIntValue ("appType"):0;
            String APP_ID = reqData.getString ("appId");
            if(appType == 2){
                if(!reqData.containsKey("channelKey") || StringUtils.isEmpty(reqData.getString("channelKey"))){
                    return listener.failedHandler (Constant.failedMsg ("下单失败"));
                }
                VcOnlineOrderMade made = VcOnlineOrderMade.buildAlipayMade (reqData);
                return  funPayH5Service.buildAliPayUrl (2,made, listener);
            }

            if(appIdMaps == null || appIdMaps.isEmpty ()){
                appIdMaps =  new HashMap<String, Integer>();
                appIdMaps.put(APP_ID,1);
            }
            //收款类型 1:仅企业 2:仅收款码 3：公平轮询收款码和企业账号 4:口碑 5 网站支付 6 预授权
            if(appType == 3 && appIdMaps.get (APP_ID) != null && appIdMaps.get (APP_ID) == 1){
                appIdMaps.put(APP_ID,2);
                if(!reqData.containsKey("channelKey") || StringUtils.isEmpty(reqData.getString("channelKey"))){
                    return listener.failedHandler (Constant.failedMsg ("下单失败"));
                }
                VcOnlineOrderMade made = VcOnlineOrderMade.buildAlipayMade (reqData);
                result = funPayH5Service.buildAliPayUrl (2,made, listener);
                //验证自研支付获取链接是否成功
                if(result.containsKey ("redirectUrl") && StringUtil.isNotEmpty (result.getString ("redirectUrl"))){
                    return  result;
                }
            }
            if (StringUtils.isEmpty (APP_ID)) {
                result.put ("code", Constant.FAILED);
                result.put ("msg", "没有可用账号");
                result.put ("redirectUrl", "");
                return listener.failedHandler (result);
            }
            if (StringUtils.isAnyEmpty(APP_ID , reqData.getString ("privateKey") , reqData.getString ("publicKey"))) {
                result.put ("code", Constant.FAILED);
                result.put ("msg", "当前账号信息不完整");
                result.put ("redirectUrl", "");
                return listener.failedHandler (result);
            }
            if(appType==5){
                return alipayTradeWapPay(reqData,listener);
            }
            if(appType==6){
                return alipayAuthOrderFreeze(reqData,listener);
            }
            appIdMaps.put(APP_ID,1);
            return alipayTradePrecreate(reqData,listener);
        } catch (Exception e) {
            logger.error ("自研企业支付宝支付异常", e);
            return listener.paddingHandler (Constant.failedMsg ("下单失败"));
        }
    }


    /**
     * 支付宝统一下单接口 （当面付 口碑）
     * @param reqData
     * @param listener
     * @throws Exception
     */
    private JSONObject alipayTradePrecreate(JSONObject reqData, ResultListener listener) throws Exception{
        JSONObject result = new JSONObject ();
        String APP_ID = reqData.getString ("appId");
        String APP_PRIVATE_KEY = reqData.getString ("privateKey");
        String ALIPAY_PUBLIC_KEY = reqData.getString ("publicKey");
        AlipayClient alipayClient = new DefaultAlipayClient (ALIPAY_API_URL, APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest ();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel ();
        request.setBizModel (model);
        String baseUrl = reqData.getString ("projectDomainUrl");
        request.setReturnUrl (baseUrl + "/success");
        request.setNotifyUrl (baseUrl + "/funpMerchH5CallBackApi");
        model.setOutTradeNo (reqData.getString ("vcOrderNo"));
        model.setTotalAmount (reqData.getString ("amount"));
        model.setSubject (reqData.getString ("goodsName"));
        model.setTimeoutExpress("5m");
        model.setQrCodeTimeoutExpress("5m");
        model.setEnablePayChannels("balance,moneyFund,bankPay,debitCardExpress");
        AlipayTradePrecreateResponse tradeResponse = alipayClient.execute (request);

        if (tradeResponse == null || StringUtils.isEmpty (tradeResponse.getBody ())) {
            result.put ("code", Constant.FAILED);
            result.put ("msg", "获取支付链接超时");
            result.put ("redirectUrl", "");
            return listener.failedHandler (result);
        }
        logger.info ("自研企业支付宝支付响应{}", tradeResponse.getBody ());
        result.put ("upperParams", tradeResponse.getQrCode ());
        if ("10000".equals (tradeResponse.getCode ()) && StringUtils.isNotEmpty (tradeResponse.getQrCode ())) {
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "下单成功");
            String alipayUrl = StringEscapeUtils.unescapeJava (tradeResponse.getQrCode ());
            result.put ("redirectUrl",alipayUrl.replace("https://qr.alipay.com/",baseUrl+"/alipayQr/"));
            result.put ("bankUrl", StringEscapeUtils.unescapeJava (tradeResponse.getQrCode ()));
            return listener.successHandler (result);
        } else {
            result.put ("code", Constant.FAILED);
            result.put ("msg", tradeResponse.getMsg ());
            result.put ("redirectUrl", "");
            return listener.failedHandler (result);
        }
    }

    /**
     * 支付宝统一下单接口 （手机网站支付）
     * @param reqData
     * @param listener
     * @throws Exception
     */
    private JSONObject alipayTradeWapPay(JSONObject reqData, ResultListener listener) throws Exception{
        JSONObject result = new JSONObject ();
        String APP_ID = reqData.getString ("appId");
        String APP_PRIVATE_KEY = reqData.getString ("privateKey");
        String ALIPAY_PUBLIC_KEY = reqData.getString ("publicKey");
        AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_API_URL, APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        request.setBizModel(model);
        String baseUrl = reqData.getString ("projectDomainUrl");
        request.setReturnUrl (baseUrl + "/success");
        request.setNotifyUrl (baseUrl + "/funpMerchH5CallBackApi");
        model.setSubject(reqData.getString ("goodsName"));
        model.setOutTradeNo(reqData.getString ("vcOrderNo"));
        model.setTotalAmount(reqData.getString ("amount"));
        model.setTimeoutExpress("5m");
        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
        if (null==response || StringUtils.isEmpty (response.getBody())){
            result.put ("code", Constant.FAILED);
            result.put ("msg", "获取支付链接超时");
            result.put ("redirectUrl", "");
            return listener.failedHandler (result);
        }
        logger.info ("自研企业支付宝支付响应{}", response.getBody());
        result.put ("code", Constant.SUCCESSS);
        result.put ("msg", "下单成功");
        result.put ("redirectHtml",response.getBody());
        return listener.successHandler (result);
    }

    /**
     * 支付宝支付预授权接口
     */
    private JSONObject alipayAuthOrderFreeze(JSONObject reqData, ResultListener listener) throws Exception{
        JSONObject result = new JSONObject ();
        String APP_ID = reqData.getString ("appId");
        String APP_PRIVATE_KEY = reqData.getString ("privateKey");
        String ALIPAY_PUBLIC_KEY = reqData.getString ("publicKey");
        AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_API_URL, APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");

        AlipayFundAuthOrderAppFreezeRequest request = new AlipayFundAuthOrderAppFreezeRequest();
        AlipayFundAuthOrderAppFreezeModel model = new AlipayFundAuthOrderAppFreezeModel();
        String baseUrl = reqData.getString ("projectDomainUrl");
        request.setNotifyUrl (baseUrl + "/funpMerchH5CallBackApi/authFreeze");
        request.setBizModel(model);
        String orderNo = reqData.getString ("vcOrderNo");
        model.setOutOrderNo(orderNo);
        model.setOutRequestNo(orderNo);
        model.setOrderTitle(orderNo);
        model.setAmount(reqData.getString ("amount"));
        model.setProductCode("PRE_AUTH_ONLINE");
        model.setPayeeUserId(reqData.getString ("appUserId"));
        model.setPayTimeout("5m");
        model.setExtraParam("{\"category\":\"CHARGE_PILE_CAR\"}");
        model.setEnablePayChannels("[{\"payChannelType\":\"PCREDIT_PAY\"},{\"payChannelType\":\"MONEY_FUND\"}]");
        AlipayFundAuthOrderAppFreezeResponse response = alipayClient.sdkExecute(request);
        if(null == response ||!response.isSuccess()){
            result.put ("code", Constant.FAILED);
            result.put ("msg", "下单失败");
            result.put ("redirectUrl", "");
            return listener.failedHandler (result);
        }
        VcOnlineOrderMade made = VcOnlineOrderMade.buildAlipayMade (reqData);
        made.setOpenType(120);
        made.setRemarks(reqData.getString ("appUserId"));
        made.setQrcodeUrl(response.getBody());
        int r = vcOnlineOrderMadeMapper.save (made);
        if (r < 1) {
            result.put ("code", Constant.FAILED);
            result.put ("msg", "链接获取失败");
            return listener.failedHandler (result);
        }
        logger.info ("自研订单:{},支付预授权下单成功{}", orderNo,made.getOpenUrl());
        result.put ("code", Constant.SUCCESSS);
        result.put ("msg", "下单成功");
        result.put ("redirectUrl",made.getOpenUrl());
        result.put ("bankUrl", made.getOpenUrl());
        return listener.successHandler (result);
    }

    /**
     * 预授权解冻转支付
     * @return
     */
    public boolean alipayTradePay(VcOnlineOrder onlineOrder,int repeat){
        try {
            logger.info("预授权解冻转支付开始cAllData:{},pAllData:{},repeat:{}",onlineOrder.getCSign(),onlineOrder.getpAllRes(),repeat);
            JSONObject cAllData = JSONObject.parseObject(onlineOrder.getCSign());
            JSONObject pAllData = JSONObject.parseObject(onlineOrder.getpAllRes());

            String APP_ID = cAllData.getString ("appId");
            String APP_PRIVATE_KEY = cAllData.getString ("privateKey");
            String ALIPAY_PUBLIC_KEY = cAllData.getString ("publicKey");
            AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_API_URL, APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");

            AlipayTradePayRequest request = new AlipayTradePayRequest();
            AlipayTradePayModel model = new AlipayTradePayModel();
            // 预授权转支付商户订单号，为新的商户交易流水号；如果重试发起扣款，商户订单号不要变；
            model.setOutTradeNo(onlineOrder.getOrderNo());
            model.setProductCode("PRE_AUTH_ONLINE");
            model.setAuthNo(pAllData.getString("authNo"));
            // 解冻转支付标题，用于展示在支付宝账单中
            model.setSubject(onlineOrder.getOrderNo());
            model.setTotalAmount(pAllData.getString("amount"));
            model.setSellerId(pAllData.getString("payeeUser"));
            model.setBuyerId(pAllData.getString("payerUser"));
            model.setBody("PAY:"+onlineOrder.getOrderNo());
            //必须使用COMPLETE,传入该值用户剩余金额会自动解冻
            model.setAuthConfirmMode("COMPLETE");
            request.setBizModel(model);
            request.setNotifyUrl(cAllData.getString("projectDomainUrl") + "/funpMerchH5CallBackApi/tradePay");
            AlipayTradePayResponse response = alipayClient.execute(request);
            logger.info("预授权解冻转支付订单:{},结果:{}",onlineOrder.getOrderNo(),response.getBody());
            if(!response.isSuccess()){
                logger.info("预授权解冻转支付订单:{},失败:{}",onlineOrder.getOrderNo(),response.getSubMsg());
                if(repeat == 0){
                    return false;
                }
                return alipayTradePay(onlineOrder,repeat - 1 );
            }
            VcOnlineOrder temp = new VcOnlineOrder(onlineOrder.getOrderNo());
            temp.setpOrder(response.getTradeNo());
            int result = vcOnlineOrderService.updateByOrderNo(temp);
            if(result>0){
                logger.info("预授权解冻转支付成功:{},更新支付宝订单成功:{}",onlineOrder.getOrderNo());
                return true;
            }
            logger.info("预授权解冻转支付成功:{},更新支付宝订单失败:{}",onlineOrder.getOrderNo(),response.getTradeNo());
            return false;
        } catch (AlipayApiException e) {
            logger.error ("预授权转支付异常", e);
            if(repeat == 0){
                return false;
            }
            logger.info("预授权转支付订单:{},重试:{}",onlineOrder.getOrderNo(),repeat);
            return alipayTradePay(onlineOrder,repeat-1);
        }catch (JSONException je){
            logger.error ("预授权转支付订单:{},数据CSign或pAllRes异常:{}",onlineOrder.getOrderNo(),je);
            return false;
        }
    }


}

