/**
 * @类名称:InpayReplaceServiceImpl.java
 * @时间:2017年12月19日上午11:09:35
 * @作者:nada
 * @版权:公司 Copyright (c) 2017
 */
package com.vc.onlinepay.pay.replace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.sand.HttpKit;
import com.vc.onlinepay.utils.sand.JsonDefrayDTO;
import com.vc.onlinepay.utils.sand.JsonDefrayQueryDTO;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


/**
 * @描述:杉德
 * @时间:2017年12月19日 上午11:09:35
 */
@Service
@Component

public class SandReplaceServiceImpl {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static String url = "http://lepay.hfzc888.com:8083/api/defray/v1";
    //银行简称
  	public static final Map<String, String> bankAbbr = new HashMap<>();
    public static final Map<String, String> bankCodeMap = new HashMap<>();
  	static {
  		bankAbbr.put("01000000","POST");
		bankAbbr.put("01020000","ICBC");
		bankAbbr.put("03080000","CMBCHINA");
		bankAbbr.put("01030000","ABC");
		bankAbbr.put("01050000","CCB");
		bankAbbr.put("04031000","BCCB");
		bankAbbr.put("03010000","BOCO");
		bankAbbr.put("03090000","CIB");
		bankAbbr.put("03050000","CMBC");
		bankAbbr.put("03030000","CEB");
		bankAbbr.put("01040000","BOC");
		bankAbbr.put("04100000","PINGANBANK");
		bankAbbr.put("03020000","ECITIC");
		bankAbbr.put("03070000","SDB");
		bankAbbr.put("03060000","CGB");
		bankAbbr.put("04012900","SHB");
		bankAbbr.put("03100000","SPDP");
		bankAbbr.put("03040000","HXB");
		bankAbbr.put("14181000","BRCB");
		bankAbbr.put("04243010","BON");
		bankAbbr.put("04083320","NBCB");
  	}

    static {
        bankCodeMap.put("农业","ABC");
        bankCodeMap.put("华夏","HXB");
        bankCodeMap.put("交通","BOCO");
        bankCodeMap.put("广发","CGB");
        bankCodeMap.put("邮政","POST");
        bankCodeMap.put("中国银行","BOC");
        bankCodeMap.put("兴业","CIB");
        bankCodeMap.put("中信","ECITIC");
        bankCodeMap.put("招商","CMBCHINA");
        bankCodeMap.put("光大","CEB");
        bankCodeMap.put("建设","CCB");
        bankCodeMap.put("平安","PINGANBANK");
        bankCodeMap.put("浦发","SPDP");
        bankCodeMap.put("北京","BCCB");
        bankCodeMap.put("民生","CMBC");
        bankCodeMap.put("上海","SHB");
        bankCodeMap.put("工商","ICBC");
    }

    /**
     * @描述:杉德代付接口
     * @时间:2017年12月20日 下午5:27:52
     */
    public JSONObject replaceOrder(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	logger.info("杉德代付接口reqData:{}",reqData);
        	
            result.put("orderNo",reqData.getString("vcOrderNo"));
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));

            String orderNo = reqData.getString("vcOrderNo");
            String notifyUrl = reqData.getString("projectDomainUrl")+"/sandReplaceCallbackApi";
            String idCardNo = reqData.containsKey("idCardNo")?reqData.getString("idCardNo"):"其它";
            String bankName = reqData.containsKey("bankName")?reqData.getString("bankName"):"其它";
            String openBankName = reqData.containsKey("openBankName")?reqData.getString("openBankName"):"其它";
            String city = reqData.containsKey("city")?reqData.getString("city"):"其它";
            String province = reqData.containsKey("province")?reqData.getString("province"):"其它";
            String accountName = reqData.containsKey("accountName")?reqData.getString("accountName"):"其它";
            String bankCard = reqData.containsKey("bankCard")?reqData.getString("bankCard"):"其它";
            String bankLinked = reqData.containsKey("bankLinked")?reqData.getString("bankLinked"):"其它";
            String mobile = reqData.containsKey("mobile")?reqData.getString("mobile"):"其它";
            String bankCode = reqData.containsKey("bankCode")?reqData.getString("bankCode"):"";
            if(StringUtils.isEmpty (bankCode)){
                for (Map.Entry<String, String> m : bankCodeMap.entrySet()) {
                    if(bankName.contains (m.getKey ())){
                        bankCode = m.getValue();  break;
                    }
                }
            }else{
                if(bankAbbr.containsKey(bankCode)){
                    bankCode = bankAbbr.get(bankCode);
                }
            }

    		Map<String, String> requestMap = new HashMap<String, String>();
    		requestMap.put("amt",reqData.getString("amount"));
    		requestMap.put("tradeOrderNo",orderNo);
    		requestMap.put("receiveName", Base64.encodeBase64String(accountName.getBytes("UTF-8")));
    		requestMap.put("openProvince", Base64.encodeBase64String(province.getBytes("UTF-8")));
    		requestMap.put("openCity", Base64.encodeBase64String(city.getBytes("UTF-8")));
    		requestMap.put("bankBranchName", Base64.encodeBase64String(openBankName.getBytes("UTF-8")));
    		requestMap.put("bankCode", bankCode);
    		requestMap.put("cardNo", bankCard);
    		requestMap.put("acctNo", idCardNo);
    		requestMap.put("bankLinked", bankLinked);
    		requestMap.put("phone", mobile);
    		requestMap.put("bankClearNo",bankCard);
    		requestMap.put("bankBranchNo",bankCard);
    		requestMap.put("timestamp", Constant.ymdhms.format(new Date()));
    		requestMap.put("version", "V1");// 常量V1
    		requestMap.put("noticeUrl", notifyUrl);
    		requestMap.put("token",md5Key); 
    		requestMap.put("tradeNo",merchantCode);
    		requestMap.put("gwType","5"); //纯代付通道
    		JsonDefrayDTO apiPay = new JsonDefrayDTO(requestMap);
    		requestMap.put("sign", apiPay.sign());
    		requestMap.remove("token"); //token不传递
    		apiPay = new JsonDefrayDTO(requestMap);
    		Map<String, String> head = new HashMap<String, String>();
    		head.put("Content-Type", "application/json");
    		
    		logger.info("杉德代付接口入参:{}",JSON.toJSONString(apiPay));
    		String response = HttpKit.post(url, JSON.toJSONString(apiPay), head);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            logger.info("杉德代付接口返参:{}",jsonData);
            if("-1".equals(jsonData.getString("code"))){
                result.put("status", 3);
                result.put("code", Constant.FAILED);
                result.put("msg",jsonData.get("msg")==null?"代付失败":jsonData.getString("msg"));
                return listener.failedHandler(result);
            }else{
            	result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "订单处理中或待处理");
                return listener.paddingHandler(result);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("杉德代付接口异常",e);
            result.put("code", "10001");
            result.put("msg", "代付失败系统异常.");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }
    }
    /**
     * @描述:杉德代付订单查询接口
     * @时间:2018/6/15 11:22
     */
    @SuppressWarnings("all")
    public JSONObject replaceQuery(JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject();
        try {
        	String url = "http://lepay.hfzc888.com:8083/api/defray/query";
            result.put("orderNo",reqData.getString("vcOrderNo"));

            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));

    		Map<String, String> requestMap = new HashMap<String, String>();
    		requestMap.put("tradeOrderNo",reqData.getString("vcOrderNo"));
    		requestMap.put("version", "V1");
    		requestMap.put("tradeNo",merchantCode);
    		requestMap.put("timestamp", Constant.ymdhms.format(new Date()));
    		requestMap.put("token", md5Key);//
    		JsonDefrayQueryDTO apiPay = new JsonDefrayQueryDTO(requestMap);
    		requestMap.put("sign", apiPay.sign(requestMap.get("token")));
    		requestMap.remove("token"); // token参与加密后删除不传递
    		String response = HttpKit.get(url, requestMap);
    		logger.info("杉德代付订单查询接口响应{}",response);
            if(StringUtils.isEmpty(response)){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "发送超时");
                return listener.paddingHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if(jsonData == null){
                result.put("status", 2);
                result.put("code", Constant.FAILED);
                result.put("msg", "查询失败");
                return listener.paddingHandler(result);
            }
            if(jsonData.containsKey("t")){
            	JSONObject tData = jsonData.getJSONObject("t");
            	String status = tData.getString("status");
                if("4".equals(status)){
                    result.put("code", Constant.SUCCESSS);
                    result.put("msg", "代付成功");
                    result.put("status", 1);
                    return listener.successHandler(result);
                }else if("5".equals(status) || "3".equals(status) || "-1".equals(status)){
                    result.put("status", 3);
                    result.put("code", Constant.FAILED);
                    result.put("msg",jsonData.get("remark")==null?"代付失败":jsonData.getString("remark"));
                    return listener.failedHandler(result);
                }
            }
            result.put("status", 2);
            result.put("code", Constant.FAILED);
            result.put("msg", "处理中");
            return listener.paddingHandler(result);
        }catch (Exception e){
            logger.error("杉德查询接口异常",e);
            result.put("code", Constant.FAILED);
            result.put("msg","查询异常");
            result.put("status", 2);
            return listener.paddingHandler(result);
        }finally {
            result.put("vcOrderNo",reqData.get("vcOrderNo"));
            result.put("amount",reqData.get("amount"));
            result.put("merchantId",reqData.get("merchantId"));
            result.put("orderNo", reqData.get("orderId"));
            result.put("password",reqData.get("password"));
        }
    }
    
    /**
     * @描述:余额查询
     * @时间:2018/6/15 11:23
     */
    public JSONObject walletQuery(JSONObject reqData,ResultListener listener){
        JSONObject result = new JSONObject();
        result.put("code", Constant.FAILED);
        try {
            String merchantCode = StringUtils.deleteWhitespace(reqData.getString("channelMerchNo"));
            String md5Key = StringUtils.deleteWhitespace(reqData.getString("channelKeyDes"));

            String url = "http://lepay.hfzc888.com:8083/api/user/balance";
            Map<String, String> requestMap = new HashMap<String, String>();
    		requestMap.put("version", "V1");
    		requestMap.put("tradeNo",merchantCode);
    		requestMap.put("timestamp", Constant.ymdhms.format(new Date()));
    		requestMap.put("token", md5Key);
    		JsonDefrayQueryDTO apiPay = new JsonDefrayQueryDTO(requestMap);
    		requestMap.put("sign", apiPay.sign(requestMap.get("token")));
    		requestMap.remove("token");
    		String response = HttpKit.get(url, requestMap);
    		logger.info ("杉德余额查询:{}",response);
            if(StringUtils.isEmpty(response)){
                result.put("balance", "0");
                result.put("code", Constant.FAILED);
                result.put("msg", "查询超时");
                return listener.failedHandler(result);
            }
            JSONObject jsonData = JSONObject.parseObject(response);
            if(jsonData == null || !"0".equals(jsonData.getString("code"))){
                result.put("balance", "0");
                result.put("code", Constant.FAILED);
                result.put("msg", jsonData.get("msg"));
                return listener.failedHandler(result);
            }
            String balance = jsonData.getString("t");
            result.put("code", Constant.SUCCESSS);
            result.put("balance", balance);
            result.put("msg", "查询成功！");
            return listener.successHandler(result);
        } catch (Exception e) {
            logger.error("杉德余额查询接口异常.",e);
            result.put("code", "10001");
            result.put("msg", "余额查询异常.");
            return listener.failedHandler(result);
        }
    }
}
