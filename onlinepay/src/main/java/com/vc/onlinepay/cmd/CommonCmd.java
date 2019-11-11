package com.vc.onlinepay.cmd;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.enums.GateCodeEnum;
import com.vc.onlinepay.enums.MethodCodeEnum;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.common.CommonBusService;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.merch.MerchInfo;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.entity.online.VcOnlineWallet;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.gateutils.GateResponse;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author lihai
 */
@Service
@Component
public class CommonCmd {

    public static final Logger logger = LoggerFactory.getLogger (CommonCmd.class);
    @Autowired
    private CommonPayService commonPayService;
    @Autowired
    public CommonBusService payBusService;
    @Autowired
    private CoreEngineProviderService coreProviderService;

    @Value ("${onlinepay.project.domainName:}")
    private String domainName;

    @Value ("${onlinepay.project.shortName:}")
    private String shortName;

    @Value ("${onlinepay.project.actualName:}")
    private String actualName;

    @Value ("${onlinepay.project.successUrl:}")
    private String successUrl;


    /**
     * @描述: 验证交易网关公共报文
     * @作者:nada
     * @时间:2019/3/15
     **/
    public JSONObject checkCommonReqData (JSONObject reqData) {
        try {
            logger.info ("交易网关公共报文:{}", reqData);
            if (reqData == null || reqData.isEmpty ()) {
                return Constant.failedMsg ("公共报文解析为空,请检查是否json+post提交");
            }
            String reqCmd = reqData.containsKey ("reqCmd") ? reqData.getString ("reqCmd") : "";
            String merchNo = reqData.containsKey ("merchNo") ? reqData.getString ("merchNo") : "";
            String reqIp = reqData.containsKey ("reqIp") ? reqData.getString ("reqIp") : "";
            String charset = reqData.containsKey ("charset") ? reqData.getString ("charset") : "";
            String signType = reqData.containsKey ("signType") ? reqData.getString ("signType") : "";
            
            String sign = reqData.containsKey ("sign") ? reqData.getString ("sign") : "";
            if (StringUtils.isEmpty (reqCmd) || !MethodCodeEnum.isExist (reqCmd)) {
                return Constant.failedMsg ("请求报文method为空或非法" + reqCmd);
            }
            if (StringUtils.isEmpty (merchNo) || !Constant.isNumeric (merchNo)) {
                return Constant.failedMsg ("请求报文merchNo参数为空,仅为数字");
            }
            if (StringUtils.isEmpty (charset) || !charset.equalsIgnoreCase ("utf-8")) {
                return Constant.failedMsg ("请求报文charset参数为空,仅支持utf-8");
            }
            if (StringUtils.isEmpty (signType) || !signType.equalsIgnoreCase ("MD5")) {
                return Constant.failedMsg ("请求报文signType参数为空,仅支持MD5");
            }
            if (StringUtils.isEmpty (reqIp)) {
                return Constant.failedMsg ("请求报文reqIp参数为空");
            }
            if (StringUtils.isEmpty (sign)) {
                return Constant.failedMsg ("请求报文sign参数为空");
            }
            
            MerchInfo merchInfo = commonPayService.getCacheMerchInfo (merchNo);
            logger.info ("获取商户信息:{}", merchInfo);
            if (merchInfo == null) {
                return Constant.failedMsg ("请求商户号" + merchNo + "不存在");
            }
            if (merchInfo.getStatus () != 1L) {
                return Constant.failedMsg ("请求商户号" + merchNo + "已禁用");
            }
            reqData.remove ("sign");
            //String createSign = Md5CoreUtil.md5ascii (reqData, merchInfo.getPassword ());
            String signStr = Md5CoreUtil.getSignStr (reqData, merchInfo.getPassword ());
            logger.info("系统signStr结果:{}",signStr);
            String createSign = Md5Util.MD5 (signStr);
            //测试账号过滤sign校验
            String testMerch = coreProviderService.getCacheCfgKey("online.replace.test.merch");
            boolean isTest = Boolean.FALSE;
            if(StringUtil.isNotEmpty(testMerch) && testMerch.contains(merchNo)){
                isTest = Boolean.TRUE;
            }
            if (!isTest) {
                if (!createSign.toUpperCase().equals(sign.toUpperCase())) {
                    logger.error ("验签失败,平台createSign:{},下游sign:{}", createSign, sign);
                    logger.error("失败加密:{}",signStr);
                    String tradeNo = reqData.containsKey("tradeNo")?reqData.getString("tradeNo"):reqCmd;
                    commonPayService.saveSignLog ("下单验签失败", tradeNo, merchNo, signStr, sign);
                    return Constant.failedMsg ("验签失败,平台验签为" + sign);
                }
            }
            Long merchType = merchInfo.getMerchType();
            //租用系统验证资金配置
            if(merchType !=null && merchType == 8 ){
            	BigDecimal amount = reqData.getBigDecimal("amount");
                VcOnlineWallet vcOnlineWallet = payBusService.findVcOnlineWalletBymerchNo(merchNo);
                if(vcOnlineWallet == null){
                    return Constant.failedMsg("温馨提示:账户信息未配置");
                }
                if(amount.compareTo(merchInfo.getUsableTraMoney().subtract(merchInfo.getTra_total_amount()))>=0){
                    return Constant.failedMsg("温馨提示:账户余额不足请充值");
                }
            }
            reqData.put ("mode", MerchInfo.getReplaceMode (merchInfo));
            reqData.put ("merchantNo", merchNo);
            reqData.put ("replaceChannel", merchInfo.getReplaceChannel ());
            reqData.put ("merchId", merchInfo.getId ());
            reqData.put ("password", merchInfo.getPassword ());
            reqData.put ("merchName", merchInfo.getName ());
            reqData.put ("bankNo", merchInfo.getBankNo ());
            reqData.put ("projectDomainUrl", domainName + "/" + actualName);
            reqData.put ("vcOrderNo", Constant.getAutoOrderNo ());
            reqData.put ("merchType",merchType);
            reqData.put ("successUrl", successUrl);
            reqData.put ("checkRate", merchInfo.getCheckRate ());
            reqData.put ("isSecurity", merchInfo.getIsSecurity ());
            reqData.put ("merchCaseIps", merchInfo.getIpAddress ());
            reqData.put("levelNo", merchInfo.getLevelNo());
            reqData.put("levelViewNo", merchInfo.getLevelViewNo());
            return Constant.successMsg ("公共报文参数验证通过");
        } catch (Exception e) {
            logger.error ("公共报文参数验证异常", e);
            return Constant.failedMsg ("公共报文参数验证失败");
        }
    }

    /**
     * @描述: 构建响应数据
     * @时间:2019/3/25
     **/
    public JSONObject bulidResponseData (JSONObject reqData, JSONObject respData) {
        int code = respData.containsKey ("code") ? respData.getIntValue ("code") :0;
        String msg = respData.containsKey ("msg") ? respData.getString ("msg") : "响应失败";
        if(GateCodeEnum.success != code){
            return Constant.failedMsg (msg);
        }
        String reqCmd = reqData.containsKey ("reqCmd") ? reqData.getString ("reqCmd") : "";
        if(StringUtil.isEmpty (reqCmd)){
            return Constant.failedMsg ("响应分发失败");
        }
        String merchNo = reqData.getString ("merchNo");
        String password = reqData.getString ("password");
        String tradeNo = reqData.containsKey ("tradeNo") ? reqData.getString ("tradeNo") : "";
        String remark = reqData.containsKey ("remark") ? reqData.getString ("remark") : "";
        String bankUrl = respData.containsKey ("bankUrl") ? respData.getString ("bankUrl") : "";
        MethodCodeEnum method = MethodCodeEnum.getEnum (reqCmd);
        JSONObject result = new JSONObject ();
        switch (method) {
            case trade:
                if(StringUtils.isEmpty (bankUrl)){
                    return respData;
                }
            case tradeQuery:
                VcOnlineOrder vcOnlineOrder = payBusService.findOrderStatus(tradeNo);
                if(vcOnlineOrder == null){
                    return Constant.failedMsg ("订单不存在");
                }
                result.put ("code", Constant.SUCCESSS);
                result.put ("msg", vcOnlineOrder.getOrderDes ());
                result.put ("merchNo", vcOnlineOrder.getMerchNo ());
                result.put ("amount", Constant.format2BigDecimal (vcOnlineOrder.getTraAmount ()));
                result.put ("tradeNo", vcOnlineOrder.getcOrder ());
                result.put ("orderNo", vcOnlineOrder.getOrderNo ());
                result.put ("status", vcOnlineOrder.getStatus ());
                result.put ("remark", remark);
                result.put ("sign", Md5CoreUtil.md5ascii (result, password));
                if(StringUtils.isNotEmpty (bankUrl)){
                    result.put ("bankUrl", bankUrl);
                }
                return result;
            case transfer:case transferQuery:
                VcOnlinePayment vcOnlinePayment = payBusService.findVcOnlinePaymentByPorderNo(tradeNo);
                if(vcOnlinePayment == null){
                    return Constant.failedMsg ("订单不存在");
                }
                result.put ("code",Constant.SUCCESSS);
                result.put ("msg", vcOnlinePayment.getReason ());
                result.put ("merchNo", vcOnlinePayment.getMerchNo ());
                result.put ("amount", Constant.format2BigDecimal (vcOnlinePayment.getCashAmount ()));
                result.put ("tradeNo", vcOnlinePayment.getCashOrderNo ());
                result.put ("orderNo", vcOnlinePayment.getOrderNo ());
                result.put ("status",vcOnlinePayment.getStatus ());
                result.put ("remark", remark);
                result.put ("sign", Md5CoreUtil.md5ascii (result, password));
                return result;
            case walletQuery:
                VcOnlineWallet onlineWallet = payBusService.findVcOnlineWalletBymerchNo(merchNo);
                if(onlineWallet == null){
                    return Constant.failedMsg ("账户为空");
                }
                result.put ("code",Constant.SUCCESSS);
                result.put ("msg", "查询成功");
                result.put ("merchNo", onlineWallet.getMerchNo ());
                result.put ("amount", Constant.format2BigDecimal (onlineWallet.getD0UsableAmount ()));
                result.put ("remark", remark);
                result.put ("sign", Md5CoreUtil.md5ascii (result, password));
                return result;
            default:
                return GateResponse.BuildFailedResult ("构建响应分发错误" + method);
        }
    }


    /**
     * @描述:交易下单监听
     * @作者:lihai
     * @时间:2017年12月19日 下午3:42:31
     */
    public ResultListener getResultListener(JSONObject reqData){
        return new ResultListener() {
            @Override
            public JSONObject successHandler(JSONObject resultData) {
                logger.info("交易下单监听successHandler结果:{}",resultData);
                String orderNo = reqData.getString("vcOrderNo");
                if(resultData == null || resultData.isEmpty() || !resultData.containsKey ("code") || !resultData.getString ("code").equals (String.valueOf(GateCodeEnum.success))){
                    commonPayService.updateOrderStatus(orderNo, 2, "下单失败");
                    return Constant.failedMsg("交易下单失败");
                }
                String realAmount = resultData.containsKey("realAmount") ? resultData.getString("realAmount") : null;
                String pOrder = resultData.containsKey("pOrderNo") ? resultData.getString("pOrderNo") : null;
                commonPayService.updateOrderStatus(reqData.getString("vcOrderNo"), 1, "下单成功",pOrder,realAmount);
                //commonPayService.updateLastTime (reqData);
                return resultData;
            }
            @Override
            public JSONObject paddingHandler(JSONObject resultData) {
                logger.info("交易下单监听paddingHandler结果:{}",resultData);
                return resultData;
            }
            @Override
            public JSONObject failedHandler(JSONObject resultData) {
                logger.info("交易下单监听failedHandler入参:{},结果:{}",reqData,resultData);
                String message = (resultData !=null && resultData.containsKey("msg"))?resultData.getString("msg"):"下单失败";
                String orderNo = reqData.getString("vcOrderNo");
                commonPayService.updateOrderStatus(orderNo, 2, message);
                return Constant.failedMsg("下单失败,请稍后重试");
            }
        };
    }
}
