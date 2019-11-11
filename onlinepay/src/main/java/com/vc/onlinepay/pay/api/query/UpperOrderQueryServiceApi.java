package com.vc.onlinepay.pay.api.query;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.pay.order.h5.PddH5ServiceImpl;
import com.vc.onlinepay.pay.query.order.MPocketOrderQueryImpl;
import com.vc.onlinepay.pay.query.order.RemitOrderQueryImpl;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.merch.MerchInfo;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.JsonValidator;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @类名称:ReplaceServiceImpl.java
 * @时间:2017年12月19日下午5:31:01
 * @作者:Alan
 * @版权:公司 Copyright (c) 2017
 */
@Service
@Component

public class UpperOrderQueryServiceApi {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CommonPayService commonPayService;
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    @Autowired
    private RemitOrderQueryImpl remitOrderQuery;
    @Autowired
    private PddH5ServiceImpl pddH5Service;
    @Autowired
    private CoreEngineProviderService engineProviderService;


    /**
     * @描述:订单统一查询接口
     * @作者:lihai
     * @时间:2018年1月18日 上午11:39:52
     */
    public JSONObject doRestOrderQuery(JSONObject params, VcOnlineOrder vcOnlineOrder) {
        JSONObject result = new JSONObject();
        JSONObject apiResult = new JSONObject();
        try {
            logger.info("上游交易订单查询通道 入参:{}", params);
            params.put("upMerchNo",vcOnlineOrder.getUpMerchNo());
            params.put("upMerchKey",engineProviderService.getDecodeChannlKey(vcOnlineOrder.getUpMerchKey()));
            if (vcOnlineOrder.getStatus() == 4) {
                result.put("status", vcOnlineOrder.getStatus());
                result.put("amount", vcOnlineOrder.getTraAmount());
                result.put("merchantId", vcOnlineOrder.getMerchNo());
                result.put("orderNo", vcOnlineOrder.getCOrder());
                result.put("code", Constant.SUCCESSS);
                result.put("msg", vcOnlineOrder.getOrderDes());
                result.put("sign", Md5CoreUtil.md5ascii(result, params.getString("password")));
                return result;
            }
            // 通道来源 9.10 121319汇付宝20吉店 21通联 23摩宝(福建)24爱农25天下支付 27杉德
            switch (vcOnlineOrder.getPaySource()) {
                case 19:
                    apiResult = remitOrderQuery.orderQuery(params, this.getResultListener());
                    break;
                case 111:
                    apiResult = pddH5Service.orderQuery(params, this.getResultListener());
                    break;
                default:
                    result.put("code", Constant.FAILED);
                    result.put("msg", "不存在的上游交易订单查询通道:" + vcOnlineOrder.getPaySource());
                    return result;
            }
            if (apiResult == null || !apiResult.containsKey("code") || !apiResult.containsKey("msg")) {
                result.put("code", Constant.FAILED);
                result.put("msg", "上游代付订单查询失败");
                return result;
            }
        } catch (Exception e) {
            logger.error("上游代付订单查询接口异常", e);
        } finally {
            if (apiResult != null && !apiResult.isEmpty()) {
                result.put("status", apiResult.get("status"));
                result.put("amount", apiResult.get("amount"));
                result.put("merchantId", apiResult.get("merchantId"));
                result.put("orderNo", apiResult.get("orderNo"));
                result.put("code", apiResult.get("code"));
                result.put("msg", apiResult.get("msg"));
                result.put("sign", Md5CoreUtil.md5ascii(result, params.getString("password")));
            }
            if (params.containsKey("isAutoNotice") && "true".equals(params.get("isAutoNotice"))) {
                this.asynNotice(vcOnlineOrder.getCNotifyUrl(), result);
            }
        }
        return result;
    }

    /**
     * @描述:下游回调处理
     * @作者:lihai
     * @时间:2018年1月18日 下午2:55:44
     */
    public JSONObject asynNotice(String notifyUrl, JSONObject reqData) {
        JSONObject result = new JSONObject();
        try {
            logger.info("下游商户请求url{}参数{}", notifyUrl, reqData);
            String respContent = HttpClientTools.baseHttpSendPost(notifyUrl, reqData.toString(),Constant.CHART_UTF);
            logger.info("下游商户响应参数{}", respContent);

            if (StringUtil.isNotEmpty(respContent) && ("success".equals(respContent) || "SUCCESS".equals(respContent))) {
                result.put("code", Constant.SUCCESSS);
                result.put("msg", "通知下游成功");
                return result;
            } else {
                result.put("code", Constant.FAILED);
                result.put("msg", "通知下游失败");
                return result;
            }
        } catch (Exception e) {
            logger.error("回调接口通知下游异常{}", reqData, e);
            result.put("code", Constant.FAILED);
            result.put("msg", "回调通知下游失败");
            return result;
        }
    }

    /**
     * @描述:通用查询监听处理
     * @作者:lihai
     * @时间:2018年1月18日 下午12:34:06
     */
    private ResultListener getResultListener() {
        return new ResultListener() {
            @Override
            public JSONObject successHandler(JSONObject resultData) {
                logger.info("获取交易查询监听successHandler结果:{}", resultData);
                if (resultData.containsKey("vcOrderNo") && resultData.get("code").equals(Constant.SUCCESSS) && resultData.getIntValue("status")==4) {
                    String orderNo = resultData.getString("vcOrderNo");
                    VcOnlineOrder vcOnlineOrder = vcOnlineOrderService.findOrderByOrderNo(orderNo);
                    boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, 4, null);
                    if (isOK) {
                        resultData.put("status", vcOnlineOrder.getStatus());
                    } else {
                        resultData.put("status", 6);// 更新失败，状态不明
                    }
                }
                return resultData;
            }
            @Override
            public JSONObject paddingHandler(JSONObject resultData) {
                logger.info("获取交易查询监听paddingHandler结果:{}", resultData);
                return resultData;
            }

            @Override
            public JSONObject failedHandler(JSONObject resultData) {
                logger.info("获取交易查询监听failedHandler结果:{}", resultData);
                String orderNo = resultData.getString("vcOrderNo");
                VcOnlineOrder vcOnlineOrder = vcOnlineOrderService.findOrderByOrderNo(orderNo);
                if (vcOnlineOrder != null) {
                    resultData.put("status", vcOnlineOrder.getStatus());
                } else {
                    resultData.put("status", 6);// 更新失败，状态不明
                }
                return resultData;
            }
        };
    }

    /**
     * @描述:查询上游订单参数验证
     * @作者:lihai
     * @时间:2018年1月17日 下午4:11:53
     */
    public JSONObject checkReqPrms(JSONObject params) {
        JSONObject result = new JSONObject();
        try {
            if (!params.containsKey("orderNo") || StringUtil.isEmpty(params.getString("orderNo"))) {
                result.put("code", Constant.FAILED);
                result.put("msg", "orderNo订单号为空");
                return result;
            }
            if (!params.containsKey("isAutoNotice") || StringUtil.isEmpty(params.getString("isAutoNotice"))) {
                result.put("code", Constant.FAILED);
                result.put("msg", "isAutoNotice参数为空");
                return result;
            }
            result.put("code", Constant.SUCCESSS);
            return result;
        } catch (Exception e) {
            logger.error("下游查询检查异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "验证参数失败");
            return result;
        }
    }

    /**
     * @描述:检查订单
     * @作者:lihai
     * @时间:2018年1月17日 下午4:33:23
     */
    public JSONObject checkOrder(JSONObject params, VcOnlineOrder vcOnlineOrder) {
        JSONObject result = new JSONObject();
        try {
            if (vcOnlineOrder == null) {
                result.put("code", Constant.FAILED);
                result.put("msg", "未找到订单！");
                return result;
            }
            MerchInfo merchInfo = commonPayService.getCacheMerchInfo(vcOnlineOrder.getMerchNo());
            if (merchInfo == null) {
                result.put("code", Constant.FAILED);
                result.put("msg", "商户不存在");
                return result;
            }
            JsonValidator validator = new JsonValidator();
            if (!validator.validate(vcOnlineOrder.getCSign())) {
                params.put("dateTime", StringUtil
                    .getCommadRight(vcOnlineOrder.getCSign(), "agent_bill_time"));
                params.put("remark", StringUtil.getCommadRight(vcOnlineOrder.getCSign(), "remark"));
            } else {
                JSONObject cSignObject = JSONObject.parseObject(vcOnlineOrder.getCSign());
                if (cSignObject.containsKey("goodsDesc")) {
                    params.put("remark", cSignObject.getString("goodsDesc"));
                }
            }
//            if (validator.validate(vcOnlineOrder.getpAllRes())) {
//                JSONObject pAllRes = JSONObject.parseObject(vcOnlineOrder.getpAllRes());
//                if (pAllRes != null && pAllRes.containsKey("plOrderNo")) {
//                    params.put("plOrderNo", pAllRes.get("plOrderNo"));
//                }
//            }
            if (vcOnlineOrder.getPOrder() != null) {
                params.put("plOrderNo", vcOnlineOrder.getPOrder());
            }
            params.put("transCode", "002");
            params.put("reqDate", vcOnlineOrder.getCreateDate());
            params.put("requestIp","127.0.0.1");
            params.put("orderId", vcOnlineOrder.getOrderNo());
            params.put("orderNo", vcOnlineOrder.getCOrder());
            params.put("payType", vcOnlineOrder.getPayType());
            params.put("vcOrderNo", vcOnlineOrder.getOrderNo());
            params.put("amount", String.valueOf(vcOnlineOrder.getTraAmount()));
            params.put("vcService", vcOnlineOrder.getRemarks());
            params.put("password", merchInfo.getPassword());
            int channelId = vcOnlineOrder.getChannelId().intValue();
            params.put("vcChannelId", channelId);
            result.put("code", Constant.SUCCESSS);
            return result;
        } catch (Exception e) {
            logger.error("下游查询检查异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "验证参数失败");
            return result;
        }
    }

    public JSONObject checkPaymentOrder(JSONObject params, VcOnlinePayment onlinePayment) {
        return null;
    }
    //查询需要更新订单
    public List<VcOnlineOrder> findPaddingOrder(String orderNo, List<Long> list,String remarks) {
        VcOnlineOrder onlineOrder = new VcOnlineOrder(orderNo,list,remarks);
        if(StringUtils.isNotEmpty(orderNo)){
            return vcOnlineOrderService.findPaddingOrder(onlineOrder);
        }
        Date begin = new Date();
        Date end = new Date();
        long timeLong = end.getTime()-3*60*1000;
        end.setTime(timeLong);
        timeLong = timeLong-15*60*1000;//默认更新15分钟
        if("1".equals(remarks)){
            timeLong = timeLong-30*60*1000;//更新半小时
        }else if("2".equals(remarks)){
            timeLong = timeLong-60*60*1000;//更新1小时
        }else if("3".equals(remarks)){
            timeLong = timeLong-3*60*60*1000;//更新3小时
        }
        begin.setTime(timeLong);
        onlineOrder.setBeginCreateDate(begin);
        onlineOrder.setEndCreateDate(end);
        return vcOnlineOrderService.findPaddingOrder(onlineOrder);
    }
}
