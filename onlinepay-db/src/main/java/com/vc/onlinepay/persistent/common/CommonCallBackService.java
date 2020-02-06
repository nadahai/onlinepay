/**
 * @类名称:CommonCallBackService.java
 * @时间:2018年1月9日下午3:04:58
 * @作者:nada
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.persistent.common;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.http.HttpsClientTools;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @描述:通用回调业务处理
 * @时间:2018年1月9日 下午3:04:58 
 */
@Service
public class CommonCallBackService{
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Autowired
    private VcOnlinePaymentServiceImpl vcOnlinePaymentService;
    @Autowired
    private CommonWalletService commonWalletService;

    /**
     * @描述:代付完成业务处理
     * @时间:2018年1月9日 下午3:10:23
     */
    public JSONObject commonCallBackPayment(String orderNo,int status,JSONObject resultData) {
        try {
            if(StringUtils.isEmpty(orderNo)){
                logger.error("代付完成业务处理订单号接收为空{},结果{}",orderNo,resultData);
                return Constant.failedMsg("代付订单号为空,请核实订单号"+orderNo);
            }
            if(status < 1){
                logger.error("代付完成业务处理订单{}状态{}未知",orderNo,status);
                return Constant.failedMsg("代付订单状态未知,请核实订单状态"+orderNo);
            }
            VcOnlinePayment onlinePayment = vcOnlinePaymentService.findVcOnlinePaymentByOrderNo(orderNo);
            if(onlinePayment == null){
                logger.error("代付完成业务处理订单信息为空{}",orderNo);
                return Constant.failedMsg("代付订单信息为空,请核实订单信息"+orderNo);
            }
            if(onlinePayment.getStatus() == 3){
                return Constant.successMsg("代付订单已经失败状态,请核实订单状态"+orderNo);
            }
            if(onlinePayment.getStatus() == 1){
                return Constant.successMsg("代付订单已经成功状态,请核实订单状态"+orderNo);
            }
            if(resultData!=null && !resultData.isEmpty()){
                onlinePayment.setpAllRes(resultData.toString());
                String message = resultData.containsKey("msg")?resultData.getString("msg"):null;
                if(null == message || "null".equals(message)||StringUtils.isEmpty(message)){
                    message = null;
                }
                if(null == message && status==2){
                    message = "代付中";
                }
                onlinePayment.setReason(message);
                if(resultData.containsKey("keepOldMsg") && resultData.getBoolean("keepOldMsg")){
                    onlinePayment.setReason(null);
                }
            }else{
                onlinePayment.setReason("代付描述丢失");
            }
            int paymentType = onlinePayment.getPaymentType();
            //状态 1:代付成功 2:代付中3:代付失败 4 代付完成6状态不明 9代付退汇

            if (status == 1) {
            	 onlinePayment.setStatus(1);
                 onlinePayment.setRemark("代付成功");
            } else if (status == 3 ) {
            	 onlinePayment.setStatus(3);
                 onlinePayment.setRemark("代付失败");
            }else if (status == 2 ) {
	           	 onlinePayment.setStatus(2);
	             onlinePayment.setRemark("代付中");
            } else {
            	 onlinePayment.setStatus(2);
                 onlinePayment.setRemark("代付状态不明");
            }
            if(status==3 && paymentType ==1){
                //更新原订单
                onlinePayment.setStatus(2);
                onlinePayment.setRemark("代付处理中");
                onlinePayment.setRedoResult("3");
                resultData.put("msg","代付审核处理中");
            }else if(status==1 && paymentType ==2){
                //补发代付成功,更新原订单
                VcOnlinePayment onlinePayment_old = new VcOnlinePayment();
                onlinePayment_old.setRedoResult("1");
                onlinePayment_old.setOrderNo(onlinePayment.getRelateOrder());
                onlinePayment_old.setRelateOrder(onlinePayment.getOrderNo());
                int isok = vcOnlinePaymentService.updatePaymentByPnum(onlinePayment_old);
                resultData.put("keepOldMsg",true);

                JSONObject result = this.commonCallBackPayment(onlinePayment.getRelateOrder(),1,resultData);
                if(!result.getString("code").equals(Constant.SUCCESSS)){
                    logger.error("原代付订单:{},更新失败:{}",onlinePayment.getRelateOrder(),result);
                }
            }else if(status==3 && paymentType ==2){
                //补发代付失败,更新原订单,可重新补发
                VcOnlinePayment onlinePayment_old = new VcOnlinePayment();
                onlinePayment_old.setRedoResult("3");
                onlinePayment_old.setOrderNo(onlinePayment.getRelateOrder());
                int isok = vcOnlinePaymentService.updatePaymentByPnum(onlinePayment_old);
            }
            int res = vcOnlinePaymentService.updatePaymentStatus(onlinePayment);
            if(res < 1){
                onlinePayment.setStatus(6);
                logger.error("代付失败订单{}更新异常:{}",orderNo,resultData);
                return Constant.failedMsg("代付订单更新失败,请联系运维人员");
            }
            if(onlinePayment.getStatus() == 1 && paymentType ==1){
            	boolean isOk = commonWalletService.syncUpdateOkPaymentWallet(onlinePayment,orderNo);
                if(!isOk){
                	logger.error("账户信息{}更新异常:{}",onlinePayment.getMerchId(),resultData);
                    return Constant.failedMsg("代付成功财富信息更新失败,请联系运维人员");
                }
            }
            if(onlinePayment.getStatus() == 3 && paymentType ==1){
            	boolean isOk = commonWalletService.syncUpdateFailedPaymentWallet(onlinePayment,orderNo);
                if(!isOk){
                	logger.error("账户信息{}更新异常:{}",onlinePayment.getMerchId(),resultData);
                    return Constant.failedMsg("代付失败财富信息更新失败,请联系运维人员");
                }else{
                    resultData = Constant.successMsg ("代付订单刷新执行成功"+orderNo);
                    resultData.put ("callChannelLabel",onlinePayment.getRemarks());
                    return resultData;
                }
            }
            return resultData;
        } catch (Exception e) {
            logger.error("统一更新失败回调监听处理异常:{}",resultData,e);
            return Constant.failedMsg("代付账户信息更新异常,请联系运维人员");
        }
    }

    /**
     * @描述:统一交易回调业务处理
     * @时间:2018年1月9日 下午3:10:23
     */
    public boolean callBackOrder(VcOnlineOrder vcOnlineOrder,int status,String reqData) {
          return  this.callBackOrder(vcOnlineOrder,status,reqData,"");
    }
    
    /**
     * @描述:统一交易回调业务处理 更新实际支付金额
     * @时间:2018年1月9日 下午3:10:23
     */
    public boolean callBackOrder2(VcOnlineOrder vcOnlineOrder,int status,String reqData,String realAmount) {
          return  this.callBackOrder(vcOnlineOrder,status,reqData,"",realAmount);
    }
    
    /**
     * @描述:统一交易回调业务处理
     * @时间:2018年1月9日 下午3:10:23
     */
    public boolean callBackOrder(VcOnlineOrder vcOnlineOrder,int status,String reqData,String orderDes,String realAmount) {
        try {
            if (null == vcOnlineOrder || StringUtil.isEmpty(vcOnlineOrder.getOrderNo())) {
                logger.error("标准交易回调未找到订单号{}",vcOnlineOrder);
                return false;
            }
            if(status < 1){
                logger.error("标准交易回调订单状态未知{}",status);
                return false;
            }
            String vcOrderNo = vcOnlineOrder.getOrderNo();
            if (vcOnlineOrder.getStatus() == 4L) {
                logger.error("订单已经成功{}",vcOrderNo);
                return true;
            }
            //状态 1下单成功 2下单失败 3下单中 4支付成功 5支付失败 6:状态不详 7:代付中 8:代付失败 9交易退款
            vcOnlineOrder.setPayCode(realAmount);
            if (status == 1 ) {
                vcOnlineOrder.setOrderDes(StringUtil.isEmpty(orderDes)?"回调下单成功":orderDes);
                vcOnlineOrder.setStatus(1);
            }else if (status == 2 ) {
                vcOnlineOrder.setOrderDes(StringUtil.isEmpty(orderDes)?"回调下单失败":orderDes);
                vcOnlineOrder.setStatus(2);
            } else if (status == 3 ) {
                vcOnlineOrder.setOrderDes(StringUtil.isEmpty(orderDes)?"回调下单中":orderDes);
                vcOnlineOrder.setStatus(3);
            }else if (status == 4) {
                vcOnlineOrder.setOrderDes("回调支付成功");
                vcOnlineOrder.setStatus(4);
            } else if (status == 5 ) {
                vcOnlineOrder.setOrderDes(StringUtil.isEmpty(orderDes)?"回调支付失败":orderDes);
                vcOnlineOrder.setStatus(5);
            } else {
                vcOnlineOrder.setOrderDes(StringUtil.isEmpty(orderDes)?"受理中":orderDes);
                vcOnlineOrder.setStatus(1);
            }
            if(!StringUtil.isEmpty(reqData)){
            	if(reqData.length()> 520){
            		 vcOnlineOrder.setPAllRes(reqData.substring(0, 500));
            	}else{
            		 vcOnlineOrder.setPAllRes(reqData);
            	}
            }
            vcOnlineOrder.setRemark("回调状态:"+status);
            int result = vcOnlineOrderService.updateByOrderNo(vcOnlineOrder);
            if (result < 1) {
                vcOnlineOrder.setStatus(6);
                logger.error("回调交易成功更新订单失败{}", vcOnlineOrder.getOrderNo());
                return false;
            }
            if(vcOnlineOrder.getStatus() == 4){
                logger.info("订单成功异步线程池更新财富信息{}", vcOnlineOrder.getOrderNo());
                this.asynOrderWalletPoolEngineProvider(vcOnlineOrder.getMerchId(),vcOnlineOrder.getOrderNo());
                //支付成功，调分润接口
                if(vcOnlineOrder.getPaySource() == 65 || vcOnlineOrder.getPaySource() == 81 || vcOnlineOrder.getPaySource() == 83){
                    if(StringUtils.isNotEmpty(reqData) && reqData.contains("alipayAuthFreeze")){
                        return true;
                    }
                    logger.info("订单成功异步线程池分润信息{}", vcOnlineOrder.getOrderNo());
                    this.asynOrderProfitPoolEngineProvider (vcOnlineOrder,vcOnlineOrder.getOrderNo());
                }
            }

            return true;
        } catch (Exception e) {
            logger.error("标准代付回调业务处理异常",e);
            return false;
        }finally {
        	this.asynOrderNotice(vcOnlineOrder,reqData);
        }
    }
    
    /**
     * @描述:统一交易回调业务处理
     * @时间:2018年1月9日 下午3:10:23
     */
    public boolean callBackOrder(VcOnlineOrder vcOnlineOrder,int status,String reqData,String orderDes) {
        try {
            if (null == vcOnlineOrder || StringUtil.isEmpty(vcOnlineOrder.getOrderNo())) {
                logger.error("标准交易回调未找到订单号{}",vcOnlineOrder);
                return false;
            }
            if(status < 1){
                logger.error("标准交易回调订单状态未知{}",status);
                return false;
            }
            String vcOrderNo = vcOnlineOrder.getOrderNo();
            if (vcOnlineOrder.getStatus() == 4L) {
                logger.error("订单已经成功{}",vcOrderNo);
                return true;
            }
            //状态 1下单成功 2下单失败 3下单中 4支付成功 5支付失败 6:状态不详 7:代付中 8:代付失败 9交易退款
            if (status == 1 ) {
                vcOnlineOrder.setOrderDes(StringUtil.isEmpty(orderDes)?"回调下单成功":orderDes);
                vcOnlineOrder.setStatus(1);
            }else if (status == 2 ) {
                vcOnlineOrder.setOrderDes(StringUtil.isEmpty(orderDes)?"回调下单失败":orderDes);
                vcOnlineOrder.setStatus(2);
            } else if (status == 3 ) {
                vcOnlineOrder.setOrderDes(StringUtil.isEmpty(orderDes)?"回调下单中":orderDes);
                vcOnlineOrder.setStatus(3);
            }else if (status == 4) {
                vcOnlineOrder.setOrderDes("回调支付成功");
                if("手工补单成功".equals(orderDes)){
                    vcOnlineOrder.setOrderDes(orderDes);
                }
                vcOnlineOrder.setStatus(4);
            } else if (status == 5 ) {
                vcOnlineOrder.setOrderDes(StringUtil.isEmpty(orderDes)?"回调支付失败":orderDes);
                vcOnlineOrder.setStatus(5);
            } else {
                vcOnlineOrder.setOrderDes(StringUtil.isEmpty(orderDes)?"受理中":orderDes);
                vcOnlineOrder.setStatus(1);
            }
            if(!StringUtil.isEmpty(reqData)){
            	if(reqData.length()> 520){
            		 vcOnlineOrder.setPAllRes(reqData.substring(0, 500));
            	}else{
            		 vcOnlineOrder.setPAllRes(reqData);
            	}
            }
            vcOnlineOrder.setRemark("回调状态:"+status);
            int result = vcOnlineOrderService.updateByOrderNo(vcOnlineOrder);
            if (result < 1) {
                vcOnlineOrder.setStatus(6);
                logger.error("回调交易成功更新订单失败{}", vcOnlineOrder.getOrderNo());
                return false;
            }
            if(vcOnlineOrder.getStatus() == 4){
                logger.info("订单成功异步线程池更新财富信息{}", vcOnlineOrder.getOrderNo());
                this.asynOrderWalletPoolEngineProvider(vcOnlineOrder.getMerchId(),vcOnlineOrder.getOrderNo());
                //支付成功，调分润接口
                if(vcOnlineOrder.getPaySource() == 65 || vcOnlineOrder.getPaySource() == 81 || vcOnlineOrder.getPaySource() == 83){
                    if(StringUtils.isNotEmpty(reqData) && reqData.contains("alipayAuthFreeze")){
                        return true;
                    }
                    logger.info("订单成功异步线程池分润信息{}", vcOnlineOrder.getOrderNo());
                    this.asynOrderProfitPoolEngineProvider (vcOnlineOrder,vcOnlineOrder.getOrderNo());
                }
            }

            return true;
        } catch (Exception e) {
            logger.error("标准代付回调业务处理异常",e);
            return false;
        }finally {
        	this.asynOrderNotice(vcOnlineOrder,reqData);
        }
    }

    /**
     * @描述:异步财富池子引擎提供者
     * @作者:nada
     * @时间:2018年3月9日 下午5:12:24
     */
    public boolean asynOrderWalletPoolEngineProvider(final Long merchId, final String orderNo) {
        try {
            synchronized (orderNo) {
                ThreadUtil.execute(() -> {
                    boolean isOk = commonWalletService.asynUpdateOkOrderWallet(orderNo);
                    logger.info("商户{}异步更新成功订单{}财富信息结果{}", merchId, orderNo, isOk);
                });
            }
            return true;
        } catch (Exception e) {
            logger.error("异步财富池子引擎提供者异常", e);
            return false;
        }
    }

    /**
     * @描述:异步分润池子引擎提供者
     * @作者:nada
     * @时间:2018年3月9日 下午5:12:24
     */
    public boolean asynOrderProfitPoolEngineProvider(final VcOnlineOrder vcOnlineOrder, final String orderNo) {
        try {
            synchronized (orderNo) {
                ThreadUtil.execute(() -> {
                    JSONObject result = coreEngineProviderService.aliPaySettleProfit(vcOnlineOrder,2,true,true);
                    logger.info("异步订单分润结果{},{}", orderNo, result);
                });
            }
            return true;
        } catch (Exception e) {
            logger.error("异步财富池子引擎提供者异常", e);
            return false;
        }
    }

    /**
     * @描述:交易异步通知下游（签名大写）
     * @时间:2018年1月9日 下午3:10:33
     */
    public boolean asynOrderNotice(VcOnlineOrder oldOnlineOrder,String reqData) {
    	String notifyUrl = "";
        VcOnlineOrder  vcOnlineOrder = null;
        JSONObject noticePrms = new JSONObject();
        try {
        	if(oldOnlineOrder == null) {
                logger.error("交易异步通知信息为空{}", reqData);
                return false;
            }
            if(oldOnlineOrder.getStatus() !=4 && oldOnlineOrder.getStatus() !=5){
                logger.error("交易异步通知获取订单状态{},信息{}",oldOnlineOrder.getStatus(),reqData);
                return false;
            }
            if(oldOnlineOrder.getStatus() ==5 ){
                logger.error("订单失败，异步回调无需通知下游{}",reqData);
                return true;
            }
            noticePrms.put ("code", Constant.SUCCESSS);
            noticePrms.put ("msg", oldOnlineOrder.getOrderDes ());
            noticePrms.put ("merchNo", oldOnlineOrder.getMerchNo ());
            noticePrms.put ("amount", Constant.format2BigDecimal (oldOnlineOrder.getTraAmount ()));
            noticePrms.put ("tradeNo", oldOnlineOrder.getcOrder ());
            noticePrms.put ("orderNo", oldOnlineOrder.getOrderNo ());
            noticePrms.put ("status", oldOnlineOrder.getStatus ());
            noticePrms.put ("remark", Constant.format2BigDecimal (new BigDecimal (oldOnlineOrder.getPayCode ())));
            noticePrms.put ("sign", Md5CoreUtil.md5ascii (noticePrms, oldOnlineOrder.getPayKey()));
            
            notifyUrl = oldOnlineOrder.getcNotifyUrl().toLowerCase();
            vcOnlineOrder = new VcOnlineOrder(oldOnlineOrder.getOrderNo());
            logger.info("订单{}回调给下游请求地址{}参数{}",oldOnlineOrder.getOrderNo(), oldOnlineOrder.getcNotifyUrl(), noticePrms);
            if(StringUtil.isEmpty(notifyUrl) || (notifyUrl.indexOf("http://")==-1 && notifyUrl.indexOf("https://")==-1) || notifyUrl.length()<12){
                vcOnlineOrder.setcNotifyResult("ErrorUrl");
                return true;
            }
            vcOnlineOrder.setcNotifyResult("bftStar");
            String respContent = "";
            if(notifyUrl.startsWith("https://")){
                respContent = HttpsClientTools.sendHttpSSL_appljson(noticePrms,oldOnlineOrder.getcNotifyUrl());
            }else{
                respContent = HttpClientTools
                    .baseHttpSendPost(oldOnlineOrder.getcNotifyUrl(), noticePrms.toString(),Constant.CHART_UTF);
            }
            logger.info("回调给下游商户响应参数{}", respContent);
            if(StringUtil.isEmpty(respContent)){
                vcOnlineOrder.setcNotifyResult("Null");
                return false;
            }
            respContent = StringUtils.deleteWhitespace(respContent);
            if("success".equalsIgnoreCase(respContent) || "ok".equalsIgnoreCase(respContent)){
                vcOnlineOrder.setcNotifyResult("success");
                return true;
            }else{
                respContent = (respContent.contains("<") && respContent.contains(">")) ? "errorHtml":respContent;
                respContent = respContent.length()> 32 ? respContent.substring(0,32) : respContent;
                vcOnlineOrder.setcNotifyResult(respContent);
                return false;
            }
        } catch (Exception e) {
            logger.error("回调接口通知下游异常{}",reqData, e);
            vcOnlineOrder.setcNotifyResult("ErrorUrl:"+notifyUrl);
            return false;
        }finally{
            vcOnlineOrderService.updateOrderNotify(vcOnlineOrder);
        }
    }

    /**
     * @描述:解密秘钥
     * @时间:2018/10/27 16:42
     */
    public String getDecodeKey(String channelDesKey) throws Exception{
        return coreEngineProviderService.getDecodeChannlKey(channelDesKey);
    }
}

