package com.vc.onlinepay.pay.api.query;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.GaoYangReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.HuiYunReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.JiaLiangReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.KuaiBaoReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.NaTieReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.PinDuoduoReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.QuickReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.SandReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.YouFuReplaceServiceImpl;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.entity.merch.MerchInfo;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 上游代付订单查询业务处理
 * @类名称:UpperReplaceQueryServiceImpl.java
 * @时间:2018年1月11日下午3:57:31
 * @作者:lihai
 * @版权:公司 Copyright (c) 2018
 */
@Service
@Component

public class UpperReplaceQueryServiceApi {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CommonPayService commonPayService;
    @Autowired
    private ReplaceServiceImpl replaceService;
    @Autowired
    private SandReplaceServiceImpl sandReplaceServiceImpl;
    @Autowired
    private JiaLiangReplaceServiceImpl jiaLiangReplaceServiceImpl;
    @Autowired
    private HuiYunReplaceServiceImpl huiYunReplaceServiceImpl;
    @Autowired
    private QuickReplaceServiceImpl quickReplaceServiceImpl;
    @Autowired
    private GaoYangReplaceServiceImpl gaoYangReplaceService;
    @Autowired
    private PinDuoduoReplaceServiceImpl pinDuoduoReplaceServiceImpl;
    @Autowired
    private YouFuReplaceServiceImpl youFuReplaceServiceImpl;
    @Autowired
    private NaTieReplaceServiceImpl naTieReplaceServiceImpl;
    @Autowired
    private KuaiBaoReplaceServiceImpl kuaiBaoReplaceServiceImpl;

    /**
     * @描述:上游代付订单查询
     * @作者:nada
     * @时间:2019/4/1
     **/
    public JSONObject doRestReplaceQuery(JSONObject reqData,VcOnlinePayment onlinePayment){
        JSONObject result = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            logger.info("上游代付订单查询入参:{}", reqData);
            replaceService.decodeChannelKey(reqData);
            if(onlinePayment.getStatus() == 1 || onlinePayment.getStatus() == 3){
                result.put("status",onlinePayment.getStatus());
                result.put("amount",onlinePayment.getCashAmount());
                result.put("merchantId",onlinePayment.getMerchNo());
                result.put("orderNo",onlinePayment.getOrderNo());
                result.put("code",Constant.SUCCESSS);
                result.put("msg", onlinePayment.getReason());
                result.put("sign",Md5CoreUtil.md5ascii(result,reqData.getString("password")));
                return result;
            }

            int route = onlinePayment.getChannelSource().intValue();
            reqData.put("channelSource",route);
            switch (route) {
	            case 10:
                    return quickReplaceServiceImpl.replaceQuery(reqData,replaceService.getResultListener(reqData));
                case 13:
                    return gaoYangReplaceService.replaceQuery(reqData,replaceService.getResultListener(reqData));
                //case 27:
                //    return sandReplaceServiceImpl.replaceQuery(reqData,replaceService.getResultListener(reqData));
                case 28:
                    return jiaLiangReplaceServiceImpl.replaceQuery(reqData,replaceService.getResultListener(reqData));
                case 92:
                    return huiYunReplaceServiceImpl.replaceQuery(reqData,replaceService.getResultListener(reqData));
                case 93:
                    return pinDuoduoReplaceServiceImpl.replaceQuery(reqData,replaceService.getResultListener(reqData));
                case 99:
                    return youFuReplaceServiceImpl.replaceQuery(reqData,replaceService.getResultListener(reqData));
                case 134:
                    return naTieReplaceServiceImpl.replaceQuery(reqData,replaceService.getResultListener(reqData));
                case 137:
                    return kuaiBaoReplaceServiceImpl.replaceQuery(reqData,replaceService.getResultListener(reqData));
                
                default:
                    return Constant.failedMsg ("不存在查询通道:"+route);
            }
        } catch (Exception e) {
            logger.error("代入提现接口异常", e);
            return Constant.failedMsg ("上游代付订单查询失败");
        }
    }

    public JSONObject checkReqPrms(JSONObject params, HttpServletRequest request) {
        JSONObject result = new JSONObject();
        try {
            result.put("code", Constant.SUCCESSS);
            if(!params.containsKey("orderNo") || StringUtil.isEmpty(params.getString("orderNo"))){
                result.put("code", Constant.FAILED);
                result.put("msg", "orderNo参数为空");
                return result;
            }
            if(!params.containsKey("isMemo") || !"isMemo".equals(params.getString("isMemo"))){
                result.put("code", Constant.FAILED);
                result.put("msg", "参数为空");
                return result;
            }
            return result;
        } catch (Exception e) {
            logger.error("查询检查异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "验证参数失败");
            return result;
        }
    }

    /**
     * @描述:代付查询验证参数
     * @作者:nada
     * @时间:2019/4/1
     **/
    public JSONObject checkPaymentOrder(JSONObject params, VcOnlinePayment onlinePayment) {
        JSONObject result =  new JSONObject();
        try {
            MerchInfo merchInfo = commonPayService.getCacheMerchInfo(onlinePayment.getMerchNo());
            if(merchInfo == null || onlinePayment == null){
                return Constant.failedMsg ("商户不存在");
            }
            if (merchInfo.getUpperLicense()!=null && merchInfo.getUpperLicense().contains("##")) {
                params.put("upperMerchant", merchInfo.getUpperLicense().substring(0, merchInfo.getUpperLicense().indexOf("##")));
                params.put("upperKey", merchInfo.getUpperLicense().substring(merchInfo.getUpperLicense().indexOf("##")+2));
            }
            if(StringUtil.isNotEmpty(onlinePayment.getCashSign())){
                try {
                    JSONObject object = JSONObject.parseObject(onlinePayment.getCashSign());
                    params.put("transDate", object.getString("transDate"));
                    params.put("amount", object.getString("amount"));
                    params.put("mcSequenceNo", object.getString("mcSequenceNo"));
                    params.put("mcTransDateTime", object.getString("mcTransDateTime"));
                    if (params.containsKey("transTime")) {
                    	params.put("transTime", object.getString("transTime"));
					}else {
						params.put("transTime", object.getString("mcTransDateTime").substring(8, 14));
					}
                } catch (Exception e) {
                    logger.error("存储值不对，获取json参数不正确");
                }
            }
            if(StringUtil.isNotEmpty(onlinePayment.getpAllRes())){
                try {
                    JSONObject object = JSONObject.parseObject(onlinePayment.getpAllRes());
                    params.put("pOrderNo", object.getString("pOrderNo"));
                    params.put("balanceMode", object.containsKey("balanceMode")?object.getString("balanceMode"):"1");
                } catch (Exception e) {
                    logger.error("存储值不对，获取json参数不正确");
                }
            }
            params.put("balanceLabel","MAGIC_BJ");
            params.put("orderId",onlinePayment.getpOrderNo());
            params.put("orderNo", onlinePayment.getOrderNo());
            params.put("vcOrderNo",onlinePayment.getOrderNo());
            params.put("password", merchInfo.getPassword());
            params.put("cashMode", onlinePayment.getCashMode());
            params.put("channelMerchNo", onlinePayment.getpKey());
            params.put("channelKeyDes", onlinePayment.getpSign());
            result.put("code", Constant.SUCCESSS);
            return result;
        } catch (Exception e) {
            logger.error("下游查询检查异常", e);
            return Constant.failedMsg ("验证参数异常");
        }
    }
}
