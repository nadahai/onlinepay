package com.vc.onlinepay.pay.api.replace;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ReplaceServiceImpl;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.pay.replace.ErYuanZHReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.GaoYangReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.HuiYunReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.InsteadReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.JiaLiangReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.KuaiBaoReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.NaTieReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.PinDuoduoReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.QuickReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.SandReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.WanShiDaReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.YouFuReplaceServiceImpl;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.entity.online.VcOnlineWallet;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @类名称:ReplaceServiceImpl.java
 * @时间:2017年12月19日下午5:31:01
 * @作者:lihai 
 * @版权:公司 Copyright (c) 2017
 */
@Service
@Component
public class ReplaceServiceApi{

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private VcOnlinePaymentServiceImpl vcOnlinePaymentService;
    @Autowired
    private ReplaceServiceImpl commonCallBackServiceImpl;
    @Autowired
    private InsteadReplaceServiceImpl insteadReplaceService;
    @Autowired
    private SandReplaceServiceImpl sandReplaceServiceImpl;
    @Autowired
    private JiaLiangReplaceServiceImpl jiaLiangReplaceServiceImpl;
    @Autowired
    private HuiYunReplaceServiceImpl huiYunReplaceServiceImpl;
    @Autowired
    private QuickReplaceServiceImpl quickReplaceServiceImpl;
    @Autowired
    private WanShiDaReplaceServiceImpl wanShiDaReplaceService;
    @Autowired
    private GaoYangReplaceServiceImpl gaoYangReplaceService;
    @Autowired
    private PinDuoduoReplaceServiceImpl pinDuoduoReplaceServiceImpl;
    @Autowired
    private YouFuReplaceServiceImpl youFuReplaceServiceImpl;
    @Autowired
    private NaTieReplaceServiceImpl naTieReplaceServiceImpl;
    @Autowired
    private ErYuanZHReplaceServiceImpl erYuanZHReplaceServiceImpl;
    @Autowired
    private KuaiBaoReplaceServiceImpl kuaiBaoReplaceServiceImpl;

    /**
     * @描述:第三方接口代付提现接口
     * @时间:2017年12月14日 下午4:29:05
     */
    public JSONObject doRestReplace(JSONObject reqData, VcOnlineWallet vcOnlineWallet) {
        try {
            int route = reqData.getIntValue("channelSource");
            switch (route) {
                case 0:
                    JSONObject failRoutejson = new JSONObject();
                    failRoutejson.put("code",Constant.UNKNOW);
                    failRoutejson.put("msg",reqData.get("failRouteMsg"));
                    return getResultListener(reqData).failedHandler(failRoutejson);
                case 1:
                    return insteadReplaceService.replaceOrder(reqData,getResultListener(reqData));
	            case 10:
	                return quickReplaceServiceImpl.replaceOrder(reqData,getResultListener(reqData));
                case 11:
                    return wanShiDaReplaceService.replaceOrder(reqData,getResultListener(reqData));
                case 13:
                    return gaoYangReplaceService.replaceOrder(reqData,getResultListener(reqData));
	            case 27:
	                return sandReplaceServiceImpl.replaceOrder(reqData,getResultListener(reqData));
	            case 28:
	                return jiaLiangReplaceServiceImpl.replaceOrder(reqData,getResultListener(reqData));
	            case 92:
	                return huiYunReplaceServiceImpl.replaceOrder(reqData,getResultListener(reqData));
	            case 93:
	                return pinDuoduoReplaceServiceImpl.replaceOrder(reqData,getResultListener(reqData));
	            case 99:
	                return youFuReplaceServiceImpl.replaceOrder(reqData,getResultListener(reqData));
	            case 134:
	                return naTieReplaceServiceImpl.replaceOrder(reqData,getResultListener(reqData));
	            case 201:
	                return erYuanZHReplaceServiceImpl.replaceOrder(reqData,getResultListener(reqData));
	            case 137:
	                return kuaiBaoReplaceServiceImpl.replaceOrder(reqData,getResultListener(reqData));
                
	            default:
                	return this.replaceResponse(reqData,Constant.FAILED,"不存在代付通道"+route);
            }
        } catch (Exception e) {
            logger.error("代入提现接口异常{}",reqData, e);
            return this.replaceResponse(reqData,Constant.FAILED,"代付提现接口异常,请联系运维人员");
        }
    }
    
    /**
     * @描述:代付失败响应下游
     * @时间:2018年6月21日 上午11:38:48
     */
    public JSONObject replaceFailedResponse(JSONObject reqData,JSONObject resultData){
    	try {
    		logger.info("代付失败响应下游{}",reqData);
    		JSONObject result = new JSONObject();
            /*result.put("code", Constant.FAILED);
            result.put("msg", resultData.getOrDefault("msg", "代付下单失败"));
            result.put("random",reqData.containsKey("vcOrderNo")?reqData.getString("vcOrderNo"):"");
            result.put("amount",Constant.convert(reqData.containsKey("amount")?reqData.getString("amount"):"0"));
            result.put("sign",Md5CoreUtil.md5ascii(result,reqData.getString("password")));
            result.put("paymentStatus",3);
            result.put("merchantId",reqData.getString("merchantId"));
            result.put("orderNo",reqData.getString("orderNo"));*/

            BigDecimal amount = Constant.format2BigDecimal (reqData.containsKey("amount")?reqData.getBigDecimal ("amount"):Constant.zeroDecimal);
            result.put ("code",Constant.FAILED);
            result.put ("msg",resultData.getOrDefault("msg", "代付下单失败"));
            result.put ("merchNo",reqData.getString("merchantId"));
            result.put ("amount", amount);
            result.put ("tradeNo",reqData.containsKey("tradeNo")?reqData.getString("tradeNo"):"");
            result.put ("orderNo",reqData.containsKey("vcOrderNo")?reqData.getString("vcOrderNo"):"");
            result.put ("status",3);
            result.put ("remark", amount);
            result.put ("sign", Md5CoreUtil.md5ascii (result,reqData.getString("password")));
            return result;
		} catch (Exception e) {
			logger.error("代付响应异常{}",reqData,e);
			return Constant.failedMsg("代付响应失败异常,请联系运维人员");
		}
    }
    
    /**
     * @描述:响应下游
     * @时间:2018年6月21日 上午11:38:48
     */
    private JSONObject replaceResponse(JSONObject reqData, String code, String message){
    	try {
    		int status = 6;
        	VcOnlinePayment onlinePayment = vcOnlinePaymentService.findVcOnlinePaymentByOrderNo(reqData.getString("vcOrderNo"));
        	if(onlinePayment != null && onlinePayment.getStatus() != null){
        		status = onlinePayment.getStatus();
        	}
        	if(status < 1 || status == 6){
        		status = 6;
        		code = Constant.UNKNOW;
        	}else if(status == 2){
        		code = Constant.SUCCESSS;
        	}else if(status == 1){
        		code = Constant.SUCCESSS;
        	}else if(status == 3){
        		code = Constant.FAILED;
        	}
        	JSONObject result = new JSONObject();
            /*result.put("code",StringUtils.isNotEmpty(code)?code:Constant.UNKNOW);
            result.put("msg",StringUtils.isNotEmpty(message)?message:"代付处理中");
            result.put("amount",Constant.convert(reqData.getString("amount")));
            result.put("random", reqData.getString("vcOrderNo"));
            result.put("sign",Md5CoreUtil.md5ascii(result,reqData.getString("password")));
            result.put("paymentStatus",status);
            result.put("merchantId",reqData.getString("merchantId"));
            result.put("orderNo",reqData.getString("orderNo"));*/

            result.put ("code",Constant.SUCCESSS);
            result.put ("msg", onlinePayment.getReason ());
            result.put ("merchNo", onlinePayment.getMerchNo ());
            result.put ("amount", Constant.format2BigDecimal (onlinePayment.getCashAmount ()));
            result.put ("tradeNo", onlinePayment.getCashOrderNo ());
            result.put ("orderNo", onlinePayment.getOrderNo ());
            result.put ("status",onlinePayment.getStatus ());
            result.put ("remark", Constant.format2BigDecimal (onlinePayment.getCashAmount ()));
            result.put ("sign", Md5CoreUtil.md5ascii (result, reqData.getString("password")));
            return result;
		} catch (Exception e) {
			logger.error("代付响应下游异常{}",reqData,e);
			return Constant.failedMsg("代付响应异常,请联系运维人员");
		}
    }
    
    /**
     * @描述:获取代付监听
     * @作者:lihai 
     * @时间:2017年12月19日 下午3:42:31
     */
    private ResultListener getResultListener(JSONObject reqData){
        return new ResultListener() {
            @Override
            public JSONObject successHandler(JSONObject resultData) {
                logger.info("获取代付监听successHandler结果:{}",resultData);
                //JSONObject result = replaceServiceImpl.updateSuccessListener(resultData);
                JSONObject result = commonCallBackServiceImpl.callBackPayment(reqData.getString("vcOrderNo"),1, resultData);
                return replaceResponse(reqData,Constant.SUCCESSS,result.getString("msg"));
            }
            @Override
            public JSONObject paddingHandler(JSONObject resultData) {
                logger.info("获取代付监听paddingHandler结果:{}",resultData);
                //JSONObject result = replaceServiceImpl.updatePaddingListener(resultData);
                JSONObject result = commonCallBackServiceImpl.callBackPayment(reqData.getString("vcOrderNo"),2, resultData);
                return replaceResponse(reqData,Constant.UNKNOW,result.getString("msg"));
            }
            @Override
            public JSONObject failedHandler(JSONObject resultData) {
                logger.info("获取代付监听failedHandler结果:{}",resultData);
                //JSONObject result = replaceServiceImpl.updateFailedOrder(resultData);
                JSONObject result = commonCallBackServiceImpl.callBackPayment(reqData.getString("vcOrderNo"),3, resultData);
                return replaceResponse(reqData,Constant.FAILED,result.getString("msg"));
            }
        };
    }
}
