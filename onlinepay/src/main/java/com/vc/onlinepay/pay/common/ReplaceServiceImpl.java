package com.vc.onlinepay.pay.common;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.pay.api.query.UpperAccountServiceApi;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.common.CommonWalletService;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.merch.MerchInfo;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.entity.online.VcOnlineThirdBalance;
import com.vc.onlinepay.persistent.entity.online.VcOnlineWallet;
import com.vc.onlinepay.persistent.monitor.AsynNotice;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentCardServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineThirdBalanceServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpBrowserTools;
import java.math.BigDecimal;
import java.rmi.ServerException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ReplaceCheckServiceImpl
 * @Description: 代付接口验证业务服务
 * @author: lihai
 * @date: 2018年4月18日 上午11:19:28
 * @Copyright: 2018 www.guigu.com Inc. All rights reserved. 注意：本内容仅限于本信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
@Component
public class ReplaceServiceImpl {

    public static final Logger logger = LoggerFactory.getLogger (ReplaceServiceImpl.class);
    @Autowired
    private VcOnlineThirdBalanceServiceImpl vcOnlineThirdBalanceService;
    @Autowired
    private VcOnlinePaymentServiceImpl vcOnlinePaymentService;
    @Autowired
    private CommonPayService commonPayService;
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;
    @Autowired
    private VcOnlinePaymentCardServiceImpl paymentCardServiceImpl;
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    @Autowired
    public AsynNotice asynNotice;
    @Autowired
    private UpperAccountServiceApi upperAccountService;
    @Autowired
    private CommonWalletService commonWalletService;
    @Value ("${onlinepay.project.actualName:}")
    private String actualName;
    @Value ("${onlinepay.project.domainName:}")
    private String domainName;
    @Value ("${spring.datasource.url:}")
    private String datasourceUrl;

    /**
     * @描述:代付通道路由
     * @时间:2017年12月18日 下午6:46:19
     */
    public JSONObject replaceRoute (JSONObject reqData, String mode) throws ServerException {
        logger.info ("代付提现路由代付通道 入参:{}", reqData);
        int cashMode = Constant.getMode (mode);
        String micro = reqData.containsKey ("isMemo") ? reqData.getString ("isMemo") : "";
        String balanceLabel = reqData.containsKey ("vcService") ? reqData.getString ("vcService") : "";
        String replaceChannel = reqData.containsKey ("replaceChannel") ? reqData.getString ("replaceChannel") : "";
        //第一种:测试接口过来无法路由
        if ("isMemo".equals (micro) && !StringUtil.isEmpty (balanceLabel)) {
            VcOnlineThirdBalance thirdBalance = vcOnlineThirdBalanceService.findLoadBalance (new VcOnlineThirdBalance (0, balanceLabel));
            if (thirdBalance == null || (thirdBalance.getBalanceAmount ().doubleValue () < new BigDecimal (reqData.getString ("amount")).doubleValue ())) {
                String channel = thirdBalance == null ? "" : "(" + thirdBalance.getMerchName () + ")";
                return Constant.failedMsg ("当前选择代付通道" + channel + "余额不足!");
            }
            return this.replacePrm (reqData, thirdBalance, mode);
        }
        //第二种:商户独立代付通道
        if (StringUtil.isNotEmpty (replaceChannel)) {
            List<VcOnlineThirdBalance> thirdBalances = vcOnlineThirdBalanceService.findAllBalance (new VcOnlineThirdBalance (0, replaceChannel));
            return this.replaceSearchRoute (reqData, thirdBalances, mode, "独立配置" + replaceChannel);
        }
        //第三种模式:金额配置路由
        BigDecimal paymentAmount = new BigDecimal (reqData.getString ("amount"));
        JSONObject cfgResult = this.replaceConfigRoute (reqData, paymentAmount, mode);
        if (cfgResult != null && cfgResult.containsKey ("code") && cfgResult.getString ("code").equals (Constant.SUCCESSS)) {
            return cfgResult;
        }

        //第四种模式:商户自动路由
        List<VcOnlineThirdBalance> thirdBalances = vcOnlineThirdBalanceService.findLoadBalancelist (new VcOnlineThirdBalance (0, paymentAmount));
        return this.replaceSearchRoute (reqData, thirdBalances, mode, "自动路由");
    }

    /**
     * @描述:获取配置路由
     * @作者:nada
     * @时间:2018/12/10
     **/
    public JSONObject replaceConfigRoute (JSONObject reqData, BigDecimal paymentAmount, String mode) {
        try {
            String routeConfig = coreEngineProviderService.getCacheCfgKey ("online.payment.route");
            logger.info ("获取配置路由{}", routeConfig);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty (routeConfig)) {
                JSONObject routeJson = Constant.stringToJson (StringEscapeUtils.unescapeHtml4 (routeConfig));
                logger.info ("获取配置路由解析{}", routeJson);
                if (routeJson.containsKey ("minAmount1") && routeJson.containsKey ("maxAmount1") && routeJson.containsKey ("replaceChannel1")) {
                    BigDecimal minAmount1 = new BigDecimal (routeJson.getString ("minAmount1"));
                    BigDecimal maxAmount1 = new BigDecimal (routeJson.getString ("maxAmount1"));
                    String replaceChannel1 = routeJson.getString ("replaceChannel1");
                    if (maxAmount1.doubleValue () >= paymentAmount.doubleValue () && minAmount1.doubleValue () <= paymentAmount.doubleValue ()) {
                        VcOnlineThirdBalance thirdBalance = vcOnlineThirdBalanceService.findLoadBalance (new VcOnlineThirdBalance (0, replaceChannel1));
                        if (thirdBalance != null && (thirdBalance.getBalanceAmount ().doubleValue () >= paymentAmount.doubleValue ())) {
                            logger.info ("获取配置路由结果{}", thirdBalance);
                            return this.replacePrm (reqData, thirdBalance, mode);
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            logger.error ("代付通道配置路由异常，寻找自动路由", e);
            return null;
        }
    }

    /**
     * @描述:自动搜索代付通道
     * @时间:2018年6月20日 上午11:55:52
     */
    public JSONObject replaceSearchRoute (JSONObject reqData, List<VcOnlineThirdBalance> thirdBalances, String mode, String msg) throws ServerException {
        double amount = new BigDecimal (reqData.getString ("amount")).doubleValue ();
        if (thirdBalances == null || thirdBalances.size () < 1) {
            asynNotice.asynWxMsgNotice ("代付报警", "当前" + msg + "没有一条可用通道需紧急处理" + reqData.getString ("merchantId"));
            return Constant.failedMsg ("当前" + msg + "暂无可用通道,已经通知运维人员");
        }
        VcOnlineThirdBalance prmBalance = null;
        if (thirdBalances.size () == 1) {
            prmBalance = thirdBalances.get (0);
            if (prmBalance != null && prmBalance.getBalanceAmount ().doubleValue () >= amount) {
                return this.replacePrm (reqData, prmBalance, mode);
            } else {
                asynNotice.asynWxMsgNotice ("代付报警", "当前" + msg + "没有可用通道需紧急处理" + reqData.getString ("merchantId"));
                return Constant.failedMsg ("当前" + msg + "没有合适通道,已经通知运维人员");
            }
        }
        for (VcOnlineThirdBalance balance : thirdBalances) {
            if (balance.getChannelSource () == 19 && paymentCardServiceImpl.isExistBankNo (reqData.getString ("bankCard"), 1)) {
                continue;
            }
            prmBalance = balance;
            break;
        }
        if (prmBalance != null) {
            return this.replacePrm (reqData, prmBalance, mode);
        }
        asynNotice.asynWxMsgNotice ("代付报警", "当前" + msg + "没有可用通道需紧急处理" + reqData.getString ("merchantId"));
        return Constant.failedMsg ("当前" + msg + "没有合适通道,已经通知运维人员");
    }

    /**
     * @描述:代付标识
     * @时间:2018年6月13日 下午6:06:31
     */
    public JSONObject replacePrm (JSONObject reqData, VcOnlineThirdBalance thirdBalance, String mode) throws ServerException {
        if (thirdBalance == null || thirdBalance.getChannelSource () < 1) {
            asynNotice.asynWxMsgNotice ("代付报警", reqData.getString ("msg"));
            return Constant.failedMsg ("没有合适代付通道,已经通知运维人员");
        }
        reqData.put ("balanceLabel", thirdBalance.getBalanceLabel ());
        reqData.put ("balanceMode", thirdBalance.getCashMode ());
        reqData.put ("replacePoundage", thirdBalance.getReplacePoundage ());
        reqData.put ("channelSource", thirdBalance.getChannelSource ());
        reqData.put ("channelId", thirdBalance.getChannelId ());
        reqData.put ("channelMerchNo", StringUtils.deleteWhitespace (thirdBalance.getMerchNo ()));
        reqData.put ("channelKeyDes", StringUtils.deleteWhitespace (thirdBalance.getMerchKey ()));
        reqData.put ("channelMerchKey", StringUtils.deleteWhitespace (thirdBalance.getMerchKey ()));
        reqData.put ("code", Constant.SUCCESSS);
        return reqData;
    }

    /**
     * @描述:代付前数据持久化
     * @作者:nada
     * @时间:2017年12月20日 下午4:10:00
     */
    public JSONObject persistentReplaceBefore (JSONObject reqData, VcOnlineWallet vcOnlineWallet) {
        String orderNo = reqData.getString ("vcOrderNo");
        try {
            VcOnlinePayment onlinePayment = VcOnlinePayment.buildVcOnlinePayment (reqData, vcOnlineWallet);
            int res = vcOnlinePaymentService.save (onlinePayment);
            if (res < 1) {
                logger.error ("保存代付订单失败{}", reqData);
                return Constant.failedMsg ("代付下单保存失败,请联系运维人员");
            }
            boolean isOk = true;
            if (onlinePayment.getPaymentType () == 1) {
                isOk = commonWalletService.syncUpdatePaddingPaymentWallet (onlinePayment, vcOnlineWallet);
            }
            if (!isOk) {
                logger.error ("同步更新代付订中单失败{}", reqData);
                VcOnlinePayment payment = new VcOnlinePayment ();
                payment.setpAllRes (reqData.toString ());
                payment.setStatus (3);
                payment.setRemark ("代付失败");
                payment.setOrderNo (orderNo);
                payment.setReason ("代付账务保存失败");
                vcOnlinePaymentService.updatePaymentByPnum (payment);
                return Constant.failedMsg ("代付下单账务更新失败,请联系运维人员");
            }
            //路由失败处理
            if ("RouteFail".equalsIgnoreCase (onlinePayment.getRemarks ())) {
                return Constant.successMsg ("代付下单成功");
            }
            int b = vcOnlineThirdBalanceService.cashSuccessUpdateBalance (new VcOnlineThirdBalance (onlinePayment.getCashMode (), onlinePayment.getRemarks (), onlinePayment.getCashAmount ()));
            if (b < 1) {
                logger.error ("提现开始更新上游账户信息失败可忽略,模式:{},通道标记:{}", onlinePayment.getCashMode (), onlinePayment.getRemarks ());
            }
            //解密
            decodeChannelKey (reqData);
            return Constant.successMsg ("代付下单成功");
        } catch (Exception e) {
            logger.error ("代付前数据持久化异常{}", reqData, e);
            return Constant.failedMsg ("代付下单异常,请联系运维人员");
        }
    }

    /**
     * @描述:验证代付入参信息
     * @时间:2017年12月19日 上午10:32:49
     */
    public JSONObject checkReqPrms (JSONObject reqData, HttpServletRequest request) {
        try {
            logger.info ("下游代付请求入参:{}", reqData);
            if (reqData == null || reqData.isEmpty ()) {
                return Constant.failedMsg ("解析代付报文为空,请检查是否json+post提交");
            }
            //验证商户号
            String merchantId = reqData.getString ("merchantId").trim ();
            if (StringUtils.isBlank (merchantId)) {
                return Constant.failedMsg ("请求商户号为空,请检查商户号");
            }
            //验证环境
            if (datasourceUrl.contains ("test")) {
                String testReplaceAllowMerch = coreEngineProviderService.getCacheCfgKey (CacheConstants.ORDER_REPLACE_MERCH);
                if (!testReplaceAllowMerch.contains (merchantId)) {
                    return Constant.failedMsg ("测试环境禁止提现,请检查使用环境");
                }
            }
            //日切时间
            String onlineOrderLimit = coreEngineProviderService.getCacheCfgKey (CacheConstants.ONLINE_LIMIT_TIME_ORDER);
            if (Constant.isEffectiveTimeNow (onlineOrderLimit)) {
                return Constant.failedMsg ("代付日切时间,请稍后再尝试代付");
            }
            //验证金额
            String amount = reqData.containsKey ("amount") ? reqData.getString ("amount").trim () : "";
            if (StringUtils.isEmpty (amount)) {
                return Constant.failedMsg ("请求代付金额为空,请检查代付金额");
            }
            int r0 = new BigDecimal (amount).compareTo (new BigDecimal ("0"));
            if (r0 == -1 || r0 == 0) {
                return Constant.failedMsg ("请求代付金额过小:" + amount + ",请检查代付金额");
            }
            //代付单笔最高限额
            String maxAmountLimit = coreEngineProviderService.getCacheCfgKey (CacheConstants.CASH_MAX_AMOUNT);
            if (StringUtils.isNotBlank (maxAmountLimit) && new BigDecimal (amount).compareTo (new BigDecimal (maxAmountLimit)) == 1) {
                return Constant.failedMsg ("代付金额超限，最大单笔限额:[" + maxAmountLimit + "]");
            }
            //验证订单号
            String cOrderNo = reqData.containsKey ("orderNo") ? reqData.getString ("orderNo").trim () : "";
            if (StringUtils.isBlank (cOrderNo)) {
                return Constant.failedMsg ("请求订单号为空,请检查订单号");
            }
            if (cOrderNo.length () < 12 || cOrderNo.length () > 30) {
                return Constant.failedMsg ("请求订单长度不符,请检查订单号12-30长度");
            }
            String mobile = StringUtils.deleteWhitespace (reqData.containsKey ("mobile") ? reqData.getString ("mobile") : "");
            String accountName = StringUtils.deleteWhitespace (reqData.containsKey ("accountName") ? reqData.getString ("accountName") : "");
            String bankCard = StringUtils.deleteWhitespace (reqData.containsKey ("bankCard") ? reqData.getString ("bankCard") : "");
            String idCardNo = StringUtils.deleteWhitespace (reqData.containsKey ("idCardNo") ? reqData.getString ("idCardNo") : "");
            String bankName = StringUtils.deleteWhitespace (reqData.containsKey ("bankName") ? reqData.getString ("bankName") : "");

            if (accountName.length () < 2 || accountName.length () > 20) {
                return Constant.failedMsg ("收款人户名长度错误！");
            }
            if (bankCard.length () < 16 || bankCard.length () > 19) {
                return Constant.failedMsg ("银行卡号长度错误！");
            }
            if (idCardNo.length () > 18) {
                return Constant.failedMsg ("身份证号长度错误！");
            }
            //代付手机号黑名单
            if (StringUtil.isNotEmpty (mobile) && coreEngineProviderService.getCacheCfgKey (CacheConstants.CASH_BLACK_PHONE).contains (mobile)) {
                return Constant.failedMsg ("温馨提示:代付手机号黑名单:" + mobile);
            }
            //提现姓名黑名单
            if (StringUtil.isNotEmpty (accountName) && coreEngineProviderService.getCacheCfgKey (CacheConstants.CASH_BLACK_NAME).contains (accountName)) {
                return Constant.failedMsg ("温馨提示:代付姓名黑名单:" + accountName);
            }
            //提现卡号黑名单
            if (StringUtil.isNotEmpty (bankCard) && coreEngineProviderService.getCacheCfgKey (CacheConstants.CASH_BLACK_BANKNO).contains (bankCard)) {
                return Constant.failedMsg ("温馨提示:代付卡号黑名单:" + bankCard);
            }
            //提现总开关
            String cashSwitch = coreEngineProviderService.getCacheCfgKey (CacheConstants.CASH_SWITCH_SERVICE);
            cashSwitch = StringUtil.isEmpty (cashSwitch) ? "false" : cashSwitch;
            if (!"true".equals (cashSwitch)) {
                return Constant.failedMsg ("温馨提示：提现通道已经关闭");
            }
            MerchInfo merchInfo = commonPayService.getCacheMerchInfo (merchantId);
            if (merchInfo == null) {
                return Constant.failedMsg ("请求商户不存在,请检查开户信息");
            }
            if (merchInfo.getStatus () != 1L) {
                return Constant.failedMsg ("请求商户被禁用,请核实商户状态");
            }
            String ipaddress = HttpBrowserTools.getIpAddr (request);
            if (!reqData.containsKey ("isMemo") || !"isMemo".equals (reqData.getString ("isMemo"))) {
                if (merchInfo.getIsSecurity () == 2 || merchInfo.getIsSecurity () == 4) {
                    if (StringUtil.isEmpty (merchInfo.getIpAddress ())) {
                        return Constant.failedMsg ("温馨提示：请备案IP白名单认证");
                    }
                    if (!merchInfo.getIpAddress ().contains (ipaddress) && !"127.0.0.1".equals (ipaddress)) {
                        return Constant.failedMsg ("温馨提示：" + ipaddress + "IP不在备案白名单");
                    }
                }
            }
            String pSign = reqData.getString ("sign");
            String signStr = Md5CoreUtil.getSignStr (reqData, merchInfo.getPassword ());
            String sign = Md5Util.md5 (signStr);
            if (!sign.equals (pSign)) {
                logger.error ("验签失败,平台sign:{}下游sign:{}", sign, pSign);
                commonPayService.saveSignLog ("代付验签失败", cOrderNo, merchantId, signStr, sign);
                return Constant.failedMsg ("代付验签失败,请检查验签信息");
            }
            if (merchInfo.getReplaceMode () > 0) {
                switch (merchInfo.getReplaceMode ()) {
                    case 1:
                    case 3:
                        reqData.put ("mode", "T0");
                        break;
                    case 2:
                    case 4:
                        reqData.put ("mode", "T1");
                        break;
                    default:
                }
            }
            //校验订单信息
            if (commonPayService.verifyCacheReplaceOrderExist (cOrderNo)) {
                logger.error ("订单号重复{}", cOrderNo);
                return Constant.failedMsg ("温馨提示:代付订单号重复,需保证唯一");
            }
            String preDayAmountForCard = coreEngineProviderService.getCacheCfgKey (CacheConstants.CASH_AMOUNT_PREDAY_FORCARD);
            if (StringUtils.isNotBlank (preDayAmountForCard) && vcOnlinePaymentService.overFlowCardLimitAmount (bankCard, preDayAmountForCard, amount)) {
                logger.error ("当日单卡代付卡号:{},金额超限:{}", bankCard, preDayAmountForCard);
                return Constant.failedMsg ("温馨提示:当日单卡代付金额超限");
            }
            if (StringUtil.isEmpty (mobile)) {
                reqData.put ("mobile", "15188335522");//填充固定手机号
            }
            String bankLink = reqData.containsKey ("bankLinked") ? reqData.getString ("bankLinked") : "";
            if (StringUtil.isEmpty (bankLink) || !Constant.isNumberChars (bankLink)) {
                reqData.put ("bankLinked", "305584018192");//填充固定联行号
            }
            String vcOrderNo = "df" + Constant.getAutoOrderNo ();
            if (reqData.containsKey ("isReDo") && "true".equals (reqData.getString ("isReDo"))) {
                vcOrderNo = "rd" + Constant.getAutoOrderNo ();
            } else {
                reqData.put ("isReDo", "false");
            }
            reqData.put ("bankCard", bankCard);
            reqData.put ("ipaddress", ipaddress);
            reqData.put ("merchId", merchInfo.getId ());
            reqData.put ("password", merchInfo.getPassword ());
            reqData.put ("vcOrderNo", vcOrderNo);
            StringBuilder projectDomainUrl = new StringBuilder ();
            projectDomainUrl.append (domainName).append ("/").append (actualName);
            reqData.put ("projectDomainUrl", projectDomainUrl.toString ());
            reqData.put ("localPort", request.getLocalPort ());
            reqData.put ("replaceChannel", merchInfo.getReplaceChannel ());
            String dateNo = Constant.getDateString ();
            //商户交易流水
            reqData.put ("mcSequenceNo", dateNo);
            //商户交易时间
            reqData.put ("mcTransDateTime", dateNo);
            //交易日期
            reqData.put ("transDate", DateUtils.getTimeYMD ());
            //交易时间
            reqData.put ("transTime", DateUtils.getTimeforHms ());
            //付款方式1-余额支付2-企业网银3-垫资支付缺损值为1（暂时只支持3）
            reqData.put ("payType", "3");
            //默认填20101（业务往来款项）
            reqData.put ("businessType", "20101");
            return Constant.successMsg ("代付验证参数通过");
        } catch (Exception e) {
            logger.error ("下游代付检查异常{}", reqData, e);
            return Constant.failedMsg ("代付参数验证异常,请联系运维人员");
        }
    }

    /**
     * @描述:校验账户信息
     * @时间:2017年12月19日 上午9:46:30
     */
    public JSONObject checkAccount (JSONObject reqData, VcOnlineWallet vcOnlineWallet) {
        try {
            if (vcOnlineWallet == null) {
                return Constant.failedMsg ("商户账户不存在,请核实开户信息");
            }
            if (vcOnlineWallet.getStatus () == null || vcOnlineWallet.getStatus () != 1) {
                String reason = StringUtils.isNotBlank (vcOnlineWallet.getReason ()) ? vcOnlineWallet.getReason () : "此商户已被禁止提现";
                return Constant.failedMsg ("温馨提示：" + reason);
            }
            //手续费
            BigDecimal poundage = vcOnlineWallet.getServiceCharge ();
            BigDecimal paymentAmount = new BigDecimal (StringUtils.deleteWhitespace (reqData.getString ("amount")));
            if (poundage == null) {
                return Constant.failedMsg ("商户代付手续费未配置,请联系运维人员");
            }
            if (paymentAmount.compareTo (poundage) < 1) {
                return Constant.failedMsg ("温馨提示:代付金额需大于手续费" + poundage);
            }
            if (paymentAmount.compareTo (vcOnlineWallet.getMinCashAmount ()) < 0) {
                return Constant.failedMsg ("温馨提示:商户最低提现金额:" + vcOnlineWallet.getMinCashAmount ());
            }
            if (paymentAmount.compareTo (vcOnlineWallet.getMaxCashAmount ()) > 0) {
                return Constant.failedMsg ("温馨提示:商户最高提现金额:" + vcOnlineWallet.getMaxCashAmount ());
            }
            //默认从资金池大的提现
            String mode = reqData.containsKey ("mode") ? reqData.getString ("mode") : "";
            mode = StringUtils.deleteWhitespace (mode);
            if (StringUtil.isEmpty (mode)) {
                if (vcOnlineWallet.getUsableTotalAmount ().doubleValue () > vcOnlineWallet.getD0UsableAmount ().doubleValue ()) {
                    reqData.put ("mode", "T1");
                    mode = "T1";
                } else {
                    reqData.put ("mode", "T0");
                    mode = "T0";
                }
            }
            //重发代付不需要验证账户余额
            if (reqData.containsKey ("isReDo") && "true".equals (reqData.getString ("isReDo"))) {
                if (isReDoReplace (reqData)) {
                    reqData.put ("cashMode", Constant.getMode (mode));
                    reqData.put ("poundage", "0");
                    return Constant.successMsg ("重发代付账户验证通过");
                }
                return Constant.failedMsg ("当前代付订单不可重发！");
            }
            //验证账户余额
            BigDecimal subtract = new BigDecimal ("0");
            double usable = 0d;
            if ("T0".equals (mode)) {
                usable = vcOnlineWallet.getD0UsableAmount ().doubleValue ();
                subtract = vcOnlineWallet.getD0UsableAmount ().subtract (poundage.add (paymentAmount));
            } else {
                usable = vcOnlineWallet.getUsableTotalAmount ().doubleValue ();
                subtract = vcOnlineWallet.getUsableTotalAmount ().subtract (poundage.add (paymentAmount));
            }
            if (subtract.compareTo (new BigDecimal (0)) < 0) {
                return Constant.failedMsg ("温馨提示:您当前" + mode + "可用余额不足,可提现金额" + usable);
            }
            //代付开关时间控制
            int cashMode = Constant.getMode (mode);
            String passMerchs = coreEngineProviderService.getCacheCfgKey (CacheConstants.REPLACE_PASS_MERCH);
            if (StringUtil.isEmpty (passMerchs) || !passMerchs.contains (reqData.getString ("merchantId").trim ())) {
                String cashSwitchT1 = coreEngineProviderService.getCacheCfgKey (CacheConstants.CASH_SWITCH_T1);
                String cashSwitchT0 = coreEngineProviderService.getCacheCfgKey (CacheConstants.CASH_SWITCH_T0);
                if ((cashMode == 2 || cashMode == 4) && "false".equals (cashSwitchT1)) {
                    return Constant.failedMsg ("温馨提示:当前T1代付通道已关闭");
                }
                if ((cashMode == 1 || cashMode == 3) && "false".equals (cashSwitchT0)) {
                    return Constant.failedMsg ("温馨提示:当前T0代付通道已关闭");
                }
                String begin = coreEngineProviderService.getCacheCfgKey (CacheConstants.CASH_TIME_BEGIN);
                String end = coreEngineProviderService.getCacheCfgKey (CacheConstants.CASH_TIME_END);
                if (!Constant.checkRfTime (begin, end)) {
                    return Constant.failedMsg ("温馨提示：提现时间为" + begin + "-" + end + ",节假日提现接口不开放");
                }
            }
            reqData.put ("cashMode", cashMode);
            reqData.put ("poundage", poundage);
            return Constant.successMsg ("账户验证通过");
        } catch (Exception e) {
            logger.error ("校验账户信息异常{}", reqData, e);
            return Constant.failedMsg ("检查账户信息异常,请联系运维人员");
        }
    }

    /**
     * @描述:检查是否重发代付
     * @时间:2018/9/11 12:18
     */
    private boolean isReDoReplace (JSONObject reqData) throws Exception {
        String corderNo = reqData.getString ("orderNo");
        //原单号
        String origOrder = corderNo.substring (0, corderNo.indexOf ("_"));
        List<VcOnlinePayment> list = vcOnlinePaymentService.findFailPaddingOrder (origOrder);
        if (list == null || list.isEmpty ()) {
            return false;
        }
        VcOnlinePayment onlinePayment = new VcOnlinePayment ();
        onlinePayment.setRedoResult ("2");
        onlinePayment.setOrderNo (origOrder);
        int isok = vcOnlinePaymentService.updatePaymentByPnum (onlinePayment);
        return isok > 0;
    }

    /**
     * @描述:代付通道解密
     * @作者:nada
     * @时间:2019/4/1
     **/
    public void decodeChannelKey (JSONObject reqData) throws Exception {
        String decodeKey = coreEngineProviderService.getDecodeChannlKey (reqData.getString ("channelKeyDes"));
        reqData.put ("channelKeyDes", decodeKey);
        reqData.put ("channelMerchKey", decodeKey);
    }


    /**
     * @描述:同步更新代付失败财富信息
     * @时间:2018年3月5日 下午5:32:28
     */
    public JSONObject callBackPayment (String orderNo, int status, JSONObject resultData) {
        try {
            JSONObject resutl = commonCallBackServiceImpl.commonCallBackPayment (orderNo, status, resultData);
            if (resutl != null && resutl.containsKey ("callChannelLabel") && StringUtil.isNotEmpty (resutl.getString ("callChannelLabel"))) {
                this.upperAccountUpdate (resutl.getString ("callChannelLabel"));
            }
            return resutl;
        } catch (Exception e) {
            logger.error ("统一更新失败回调监听处理异常:{}", resultData, e);
            return Constant.failedMsg ("代付账户信息更新异常,请联系运维人员");
        }
    }

    /**
     * @描述:代付刷新可用余额
     * @作者:ChaiJing THINK
     * @时间:2018/8/30 14:27
     */
    private void upperAccountUpdate (String channelLabel) {
        ThreadUtil.execute (() -> {
            JSONObject queryData = new JSONObject ();
            queryData.put ("isMemo", "isMemo");
            queryData.put ("vcService", channelLabel);
            upperAccountService.doUpperQuery (queryData);
        });
    }

    /**
     * @描述:解密秘钥
     * @时间:2018/10/27 16:42
     */
    public String getDecodeKey (String channelDesKey) throws Exception {
        return coreEngineProviderService.getDecodeChannlKey (channelDesKey);
    }


    /**
     * @描述:通用查询监听处理
     * @作者:nada
     * @时间:2018年1月18日 下午12:34:06
     */
    public ResultListener getResultListener (JSONObject reqData) {
        return new ResultListener () {
            @Override
            public JSONObject successHandler (JSONObject resultData) {
                logger.info ("获取代付查询监听successHandler结果:{}", resultData);
                JSONObject result = new JSONObject ();
                resultData.put ("orderNo", reqData.getString ("vcOrderNo"));
                if (reqData.containsKey ("update") && reqData.getBoolean ("update")) {
                    int status = resultData.containsKey ("status") ? resultData.getIntValue ("status") : 0;
                    if (status == 1) {
                        String vcOrderNo = reqData.getString ("vcOrderNo");
                        JSONObject result2 = callBackPayment (vcOrderNo, 1, resultData);
                        logger.info ("上游代付订单更新结果:{}", result2);
                    }
                }
                result.put ("status", resultData.get ("status"));
                result.put ("amount", resultData.get ("amount"));
                result.put ("merchantId", resultData.get ("merchantId"));
                result.put ("orderNo", resultData.get ("orderNo"));
                result.put ("code", resultData.get ("code"));
                result.put ("msg", resultData.get ("msg"));
                result.put ("sign", Md5CoreUtil.md5ascii (result, reqData.getString ("password")));
                return result;
            }

            @Override
            public JSONObject paddingHandler (JSONObject resultData) {
                logger.info ("获取代付查询监听paddingHandler结果:{}", resultData);
                return resultData;
            }

            @Override
            public JSONObject failedHandler (JSONObject resultData) {
                logger.info ("获取代付查询监听failedHandler结果:{}", resultData);
                return resultData;
            }
        };
    }

}
