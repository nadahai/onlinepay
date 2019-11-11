package com.vc.onlinepay.pay.order.union;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.channel.ChannelSubNo;
import com.vc.onlinepay.persistent.entity.online.VcOnlineContact;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.monitor.AsynNotice;
import com.vc.onlinepay.persistent.service.channel.MerchChannelServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineContactServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @描述:瀚银支付银联快捷
 * @时间:2017年12月19日 下午2:57:29
 */
@Service
@Component
@Transactional
public class HanYinUnionServiceImpl {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String API_QUERY_URL  = "https://query.handpay.cn/hpayOperGatewayQueryWeb/trans/query.htm";
    @Autowired
    private VcOnlineContactServiceImpl vcOnlineContactService;
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;
    @Value("${onlinepay.project.baseUrl}")
    private String PROJECT_BASE_URL;

    public JSONObject payOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String vcOrderNo = reqData.getString("vcOrderNo");
            result.put("orderNo", vcOrderNo);
            VcOnlineOrder order = vcOnlineOrderService.findOrderByOrderNo(vcOrderNo);
            if (order == null) {
                return Constant.failedMsg("下单失败,请联系运维人员");
            }
            //将匹配的路由清空
            order.setUpMerchKey(null);
            order.setUpMerchNo(null);
            if (vcOnlineOrderService.updateUpInfoByOrderNo(order) <= 0) {
                return Constant.failedMsg("下单失败,请联系运维人员");
            }
            String api_url   = StringUtils.deleteWhitespace(reqData.getString("channelPayUrl"));
            int amount = new BigDecimal(reqData.getString("amount")).multiply(new BigDecimal("100")).intValue();
            //瀚银回调地址
            String notifyUrl = PROJECT_BASE_URL + "/natieCallBackController";
            String frontUrl = reqData.getString("projectDomainUrl")+"/success";
            JSONObject prams = new JSONObject();
            prams.put("orderNo",vcOrderNo);
            prams.put("orderTime", DateUtils.formatDate(order.getCreateDate(), "yyyyMMddHHmmss"));
            prams.put("currencyCode","156");
            prams.put("orderAmount",String.valueOf(amount));
            prams.put("name", "");
            prams.put("idNumber", "");
            prams.put("accNo", "");
            prams.put("telNo", "");
            prams.put("productType","100000");
            prams.put("paymentType","2008");
            prams.put("merGroup","1");
            prams.put("nonceStr", Constant.getRandomString(6));
            prams.put("frontUrl",frontUrl);
            prams.put("backUrl",notifyUrl);
            prams.put("channelLabel", reqData.getLong("channelLabel"));

            String queryStr = Md5CoreUtil.getSignStr(prams);
            JSONObject decodePrams = new JSONObject();
            String encrtpKey = "unionOrder";
            decodePrams.put("context", HiDesUtils.desEnCode(queryStr,encrtpKey));
            decodePrams.put("actionUrl", HiDesUtils.desEnCode(api_url,encrtpKey));

            JSONObject detail = new JSONObject();
            detail.put("orderNo", vcOrderNo);
            detail.put("goods", reqData.getString("goodsName"));
            detail.put("amount",  reqData.getString("amount"));
            detail.put("orderTime", DateUtils.getTimeForY_M_D_H_m_s());
            //用户银行卡信息缓存
            String userFlag = reqData.getString("userFlag");
            if(StringUtils.isNotBlank(userFlag)){
                VcOnlineContact vcOnlineContact = new VcOnlineContact(reqData.getLong("merchantId"),userFlag);
                List<VcOnlineContact> merchBankCardList = vcOnlineContactService.findContactByCondition(vcOnlineContact);
                detail.put("userFlag", userFlag);
                detail.put("merchNo", reqData.getLong("merchantId"));
                detail.put("merchBankCardList", merchBankCardList);
            }
            detail.put("projectBaseUrl", PROJECT_BASE_URL);
            logger.info("瀚银支付银联快捷入参:{}",prams);
            result.put("code", Constant.SUCCESSS);
            result.put("message","表单拼装成功");
            result.put("viewPath","union/hanyinUnionPay");
            JSONObject formdata = new JSONObject();
            formdata.put("actionUrl", reqData.getString("projectDomainUrl")+"/unionCashier/hanyin");
            formdata.put("map", decodePrams );
            formdata.put("details", detail);
            formdata.put("orderNo",vcOrderNo);

            result.put("data",formdata);
            //易迅返回支付url
            if(reqData.getString("merchantId").equals("999941001047")){
            	boolean saveSuccess = saveOrderMade(reqData, formdata, result);
            	if(!saveSuccess){
            		result.put("code", Constant.FAILED);
                    result.put("message", "银联快捷支付获取链接失败");
                    return listener.failedHandler(result);
            	}
            }
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("瀚银支付银联快捷统一支付异常", e);
            result.put("code", Constant.FAILED);
            result.put("message", "银联快捷支付获取链接失败");
            return listener.failedHandler(result);
        }
    }

    public JSONObject orderQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
            result.put("vcOrderNo", reqData.get("vcOrderNo"));
            result.put("amount", reqData.get("amount"));
            result.put("merchantId", reqData.get("merchantId"));
            result.put("orderNo", reqData.get("orderNo"));
            result.put("password", reqData.get("password"));
            //XXX:不做订单状态判断了
            VcOnlineOrder order = vcOnlineOrderService.findOrderByOrderNo(reqData.getString("vcOrderNo"));

            String hpMerCode = StringUtils.deleteWhitespace(reqData.getString("upMerchNo"));
            String channelDesKey  = StringUtils.deleteWhitespace(reqData.getString("upMerchKey"));
            //商户下单，收银台界面未提交(channelDesKey=null),无需查询上游
            if(StringUtils.isBlank(channelDesKey)){
                logger.info("瀚银支付订单查询请求,下单中:{}",reqData);
                result.put("code", Constant.FAILED);
                result.put("message", "下单中");
                result.put("status", 3);
                return listener.paddingHandler(result);
            }
            String insCode = Constant.getChannelKeyDes(channelDesKey, 0);
            String insMerchantCode = Constant.getChannelKeyDes(channelDesKey, 1);
            String signKey = Constant.getChannelKeyDes(channelDesKey, 2);

            JSONObject params = new JSONObject();

            params.put("insCode", insCode);
            params.put("insMerchantCode", insMerchantCode);
            params.put("hpMerCode", hpMerCode);
            params.put("orderNo", order.getOrderNo());
            params.put("transDate", DateUtils.formatDate(order.getCreateDate(), "yyyyMMddHHmmss"));
            params.put("productType", "100000");
            params.put("paymentType", "2008");
            params.put("nonceStr", Constant.getRandomString(6));
            StringBuilder sb = new StringBuilder();
            sb.append(params.get("insCode")).append("|")
                    .append(params.get("insMerchantCode")).append("|")
                    .append(params.get("hpMerCode")).append("|")
                    .append(params.get("orderNo")).append("|")
                    .append(params.get("transDate")).append("|")
                    .append(params.get("transSeq")).append("|")
                    .append("100000").append("|")
                    .append("2008").append("|")
                    .append(params.get("nonceStr")).append("|")
                    .append(signKey);
            String signStr = sb.toString().replace("null", "");
            String signature = Md5Util.MD5(signStr);
            params.put("signature", signature);
            logger.info("瀚银支付订单查询请求参数:{}", params);

            String response = HttpClientTools.httpSendPostForm(API_QUERY_URL, params);
            if(StringUtils.isBlank(response)){
                result.put("code", Constant.UNKNOW);
                result.put("message", "查询失败");
                result.put("status", 3);
                return listener.paddingHandler(result);
            }
            JSONObject resJson = JSONObject.parseObject(response);
            logger.info("瀚银支付订单查询返参{}",resJson);
            String transStatus = resJson.containsKey("transStatus")?resJson.getString("transStatus"):"";
            String statusCode = resJson.containsKey("statusCode")? resJson.getString("statusCode"):"";
            if(!"00".equals(statusCode)){
                result.put("code", Constant.UNKNOW);
                result.put("message", resJson.getString("statusMsg"));
                result.put("status", 3);
                return listener.paddingHandler(result);
            }
            //状态 00-交易成功 01:交易失败 02交易处理中 03未支付 99交易初始
            if ("00".equals(transStatus)) {
                result.put("code", Constant.SUCCESSS);
                result.put("message", "支付成功");
                result.put("status", 4);
                return listener.successHandler(result);
            } else if ("01".equals(transStatus)) {
                result.put("code", Constant.FAILED);
                result.put("message", resJson.getString("statusMsg"));
                result.put("status", 5);
                return listener.failedHandler(result);
            } else if ("02".equals(transStatus)) {
                result.put("code", Constant.FAILED);
                result.put("message",resJson.getString("statusMsg"));
                result.put("status", 1);
                return listener.paddingHandler(result);
            } else if ("03".equals(transStatus)){
                result.put("code", Constant.FAILED);
                result.put("message", resJson.getString("statusMsg"));
                result.put("status", 1);
                return listener.paddingHandler(result);
            } else {
                result.put("code", Constant.FAILED);
                result.put("message","支付处理中");
                result.put("status", 3);
                return listener.paddingHandler(result);
            }
        } catch (Exception e) {
            logger.error("查询异常:{}", e);
            result.put("code", Constant.FAILED);
            result.put("message", "查询异常");
            result.put("status", 6);
            return listener.paddingHandler(result);
        }
    }

    /**
     * @desc 提交瀚银请求信息并返回响应内容到前端
     * @author Tequila
     * @create 2019/5/29 15:07
     * @param params 相关请求参数
     * @param order 订单信息
     * @param actionUrl 请求地址
     * @return
    */
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public JSONObject doPay(JSONObject params, VcOnlineOrder order, String actionUrl) {
        ChannelSubNo querySubNo = new ChannelSubNo();
        String accNo = params.getString("accNo");
        querySubNo.setChannelId(params.getLong("channelLabel"));
        JSONObject result = null;
        try {
            order.setUpMerchNo("");
            order.setUpMerchKey("");
            order.setBankNo(accNo);
            order.setStatus(1);
            order.setOrderDes("下单成功");
            vcOnlineOrderService.updateUpInfoByOrderNo(order);
            logger.info("订单{}修改订单上游(瀚银)的商户信息{}-用户银行卡信息{}", order.getOrderNo(), accNo);
        } catch (Exception e) {
            logger.error("订单{}瀚银下单异常, {}", order.getOrderNo(), e);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @desc 将子商户信息加到params
     * @author Tequila
     * @create 2019/5/29 15:10
     * @param params
     * @param subNo
     * @return
    */
    private void setMerchantInfo(JSONObject params, ChannelSubNo subNo) {
        //参数添加上游商户信息 发起交易
        String channelKeyDes = StringUtils.deleteWhitespace(subNo.getUpMerchKey());
        if (!channelKeyDes.contains("###")) {
            channelKeyDes = coreEngineProviderService.getDecodeChannlKey(channelKeyDes);
        }
        String insCode = Constant.getChannelKeyDes(channelKeyDes, 0);
        String insMerchantCode = Constant.getChannelKeyDes(channelKeyDes, 1);
        String signKey = Constant.getChannelKeyDes(channelKeyDes, 2);
        params.put("insCode", insCode);
        params.put("insMerchantCode", insMerchantCode);
        params.put("hpMerCode", subNo.getUpMerchNo());
        params.put("signature", sign(params, signKey));
    }

    /**
     * @desc post请求到瀚银
     * @author Tequila
     * @create 2019/5/29 15:10
     * @param subNo
     * @param actionUrl
     * @param params
     * @return
    */
    private JSONObject postHanyinOrder(ChannelSubNo subNo, String actionUrl, JSONObject params) {
        JSONObject result = new JSONObject();
        try {
            setMerchantInfo(params, subNo);
            logger.info("订单号:{}瀚银收银台自动提交:{},参数:{}", params.getString("orderNo"), actionUrl, params);
            String response = HttpClientTools.httpSendPostForm(actionUrl, params);
            //logger.info("订单号{}:瀚银返回信息:{}", params.getString("orderNo"), response);
            //logger.info("订单号{}:瀚银返回信息:{}", params.getString("orderNo"));
            if (StringUtils.isNotEmpty(response)) {
                Document document = Jsoup.parse(response);
                if (document.select("div[class=paydone payfail]").size()>0) {
                    //请求失败
                    Elements fail = document.select("div[class=paydone payfail]");
                    Element payFail = fail.get(0).select("span").first();
                    String msg = payFail.text();
                    logger.error("订单号{}:请求瀚银支付接口失败,错误信息:{}", params.getString("orderNo"), msg);
                    return Constant.failedMsg(msg);
                }
                if (document.select("div[class=pay_topay pay_failed]").size()>0) {
                    Elements fail = document.select("div[class=pay_msg pay_fail]");
                    Element payFail = fail.get(0).select("span").first();
                    String msg = payFail.text();
                    logger.error("请求瀚银支付接口失败,错误信息:{}", msg);
                    return Constant.failedMsg(msg);
                }

                //其余情况仅做记录 暂不解析 直接返回response
                result = Constant.successMsg("success");
                result.put("response", response);
            }
        } catch (Exception e) {
            logger.error("请求瀚银支付接口失败,地址:{},参数:{}", actionUrl, params);
            result = Constant.failedMsg("发起支付失败");
            e.printStackTrace();
        }

        return result;
    }

    public boolean saveOrderMade(JSONObject reqData,JSONObject orderData,JSONObject result){
    	try {
    		String vcOrderNo = reqData.getString("vcOrderNo");
    		logger.info("易迅支付页面替换为url:订单号{},商户号:{}",vcOrderNo,reqData.getString("merchantId"));
        	result.remove("viewPath");
        	result.remove("data");
        	result.put("bankUrl", PROJECT_BASE_URL+"/hyh5api/unionUrl/"+vcOrderNo);
        	//保存页面参数
        	VcOnlineOrderMade vcOnlineOrderMade = new VcOnlineOrderMade();
			vcOnlineOrderMade.setOrderNo(vcOrderNo);
        	vcOnlineOrderMade.setMerchNo(reqData.getString("merchantId"));
        	vcOnlineOrderMade.setTraAmount(reqData.getBigDecimal("amount"));
        	vcOnlineOrderMade.setOpenUrl(reqData.getString("projectDomainUrl")+"/unionCashier/hanyin");//actionUrl
        	vcOnlineOrderMade.setQrcodeUrl(orderData.getString("map"));//map
        	vcOnlineOrderMade.setChannelId(reqData.getIntValue("channelLabel"));
        	vcOnlineOrderMade.setPaySource(0);
        	vcOnlineOrderMade.setExpiredTime(600000);
        	vcOnlineOrderMade.setUpMerchNo("");
        	vcOnlineOrderMade.setUpMerchKey("");
        	vcOnlineOrderMade.setCreateDate(DateUtils.getTodayDate());
        	vcOnlineOrderMade.setUpdateDate(DateUtils.getTodayDate());
        	vcOnlineOrderMade.setRemarks(orderData.getString("details"));//details
        	vcOnlineOrderMade.setOpenType(0);
        	int succNum = vcOnlineOrderMadeService.save(vcOnlineOrderMade);
        	return succNum>0;
		} catch (Exception e) {
			logger.info("易迅支付页面替换为url异常,单号:{},商户号:{}");
			return false;
		}
    }


    /**
     * @desc 瀚银签名
     * @author Tequila
     * @create 2019/5/29 15:11
     * @param params 请求参数
     * @param signKey 签名KEY
     * @return sign
    */
    private String sign(JSONObject params, String signKey) {
        //签名
        StringBuilder sb = new StringBuilder();
        sb.append(params.get("insCode")).append("|")
                .append(params.get("insMerchantCode")).append("|")
                .append(params.get("hpMerCode")).append("|")
                .append(params.get("orderNo")).append("|")
                .append(params.get("orderTime")).append("|")
                .append(params.get("orderAmount")).append("|")
                .append(params.get("name")).append("|")
                .append(params.get("idNumber")).append("|")
                .append(params.get("accNo")).append("|")
                .append(params.get("telNo")).append("|")
                .append(params.get("productType")).append("|")
                .append(params.get("paymentType")).append("|")
                .append(params.get("nonceStr")).append("|")
                .append(signKey);
        String signStr = sb.toString().replace("null", "");
        return Md5Util.MD5(signStr);
    }
}