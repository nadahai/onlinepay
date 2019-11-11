package com.vc.onlinepay.pay.common;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.enums.PayChannelEnum;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.channel.MerchChannel;
import com.vc.onlinepay.persistent.entity.merch.MerchInfo;
import com.vc.onlinepay.persistent.service.channel.MerchChannelServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpBrowserTools;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @ClassName: OrderCheckServiceImpl
 * @Description: 对外提供下订单，交易的服务service
 * @date: 2018年4月18日 上午11:19:07
 * @Copyright: 2018 www.guigu.com Inc. All rights reserved. 注意：本内容仅限于本信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
@Component
public class OrderServiceImpl {

    public static final Logger logger = LoggerFactory.getLogger (OrderServiceImpl.class);

    @Autowired
    private MerchChannelServiceImpl merchChannelService;

    @Autowired
    private CommonPayService commonPayService;
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;

    @Value ("${onlinepay.project.domainName:}")
    private String domainName;

    @Value ("${onlinepay.project.actualName:}")
    private String actualName;

    @Value ("${onlinepay.project.successUrl:}")
    private String successUrl;
    @Value ("${spring.datasource.username:}")
    private String datasourceUsername;


    /**
     * @描述:下单报文验证
     * @时间:2018年1月16日 上午10:42:51
     */
    public JSONObject checkReqPrms (JSONObject reqData, PayChannelEnum channelEnum, HttpServletRequest request) {
        try {
            logger.info ("验证{}下单报文{}", channelEnum.getValue (), reqData);
            if (reqData == null || reqData.isEmpty ()) {
                return Constant.failedMsg ("解析交易报文为空,请检查是否json+post提交");
            }
            //验证商户号
            String merchNo = reqData.containsKey ("merchantNo") ? reqData.getString ("merchantNo") : "";
            if (StringUtils.isBlank (merchNo)) {
                merchNo = reqData.containsKey ("merchantId") ? reqData.getString ("merchantId") : "";
            }
            if (StringUtils.isBlank (merchNo)) {
                return Constant.failedMsg ("请求商户号为空,请检查商户号");
            }
            //日切时间
            String onlineOrderLimit = coreEngineProviderService.getCacheCfgKey (CacheConstants.ONLINE_LIMIT_TIME_ORDER);
            if (Constant.isEffectiveTimeNow (onlineOrderLimit)) {
                return Constant.failedMsg ("交易日切时间,请稍后再尝试交易");
            }
            //验证金额
            String amount = reqData.containsKey ("amount") ? reqData.getString ("amount").trim () : "";
            if (StringUtil.isEmpty (amount) && reqData.containsKey ("totalAmount")) {
                amount = reqData.getString ("totalAmount").trim ();
            }
            if (StringUtil.isEmpty (amount)) {
                return Constant.failedMsg ("请求金额为空,请检查交易金额");
            }
            //验证订单号
            if (!reqData.containsKey ("orderId") || StringUtil.isEmpty (reqData.getString ("orderId"))) {
                return Constant.failedMsg ("请求订单号为空,请检查订单号");
            }
            String cOrderNo = reqData.getString ("orderId");
            if (cOrderNo.length () < 12 || cOrderNo.length () > 30) {
                return Constant.failedMsg ("请求订单长度不符,请检查单号长度(12-30)");
            }
            //验证商户
            MerchInfo merchInfo = commonPayService.getCacheMerchInfo (merchNo);
            if (merchInfo == null) {
                return Constant.failedMsg ("请求商户" + merchNo + "不存在,请检查开户信息");
            }
            if (merchInfo.getStatus () != 1L) {
                return Constant.failedMsg ("请求商户已禁用,请核实商户状态");
            }
            //交易IP白名单
            String ipaddress = HttpBrowserTools.getIpAddr (request);
            //过滤测试服务器
            boolean isSecurityHost = coreEngineProviderService.isAllowedAccessIp ("payTestHost", ipaddress);
            if (!isSecurityHost) {
                if (merchInfo.getIsSecurity () == 3 || merchInfo.getIsSecurity () == 4) {
                    if (StringUtil.isEmpty (merchInfo.getIpAddress ())) {
                        return Constant.failedMsg ("温馨提示：请备案IP白名单认证");
                    }
                    if (!merchInfo.getIpAddress ().contains (ipaddress)) {
                        return Constant.failedMsg ("温馨提示：" + ipaddress + "IP不在备案白名单");
                    }
                }
            }
            //验签
            String cSign = reqData.containsKey ("sign") ? reqData.getString ("sign") : "";
            String signStr = Md5CoreUtil.getSignStr (reqData, merchInfo.getPassword ());
            String sign = Md5Util.MD5 (signStr);
            if (!sign.equals (cSign)) {
                logger.error ("交易验签失败,平台sign:{}下游sign:{}", sign, cSign);
                commonPayService.saveSignLog ("下单验签失败", cOrderNo, merchNo, signStr, sign);
                return Constant.failedMsg ("交易验签失败,请检查验签信息");
            }
            //验证订单号
            if (commonPayService.verifyCacheMerchOrderExist (cOrderNo)) {
                return Constant.failedMsg ("温馨提示:交易订单号重复,需保证唯一");
            }

            //信用卡可用标志 :1 可用 2禁用
            String notifyUrl = reqData.containsKey ("notifyUrl") ? reqData.getString ("notifyUrl") : "";
            String goodsDesc = reqData.containsKey ("goodsDesc") ? reqData.getString ("goodsDesc") : "收款";
            String goodsName = reqData.containsKey ("goodsName") ? reqData.getString ("goodsName") : "收款";
            if (StringUtil.isEmpty (goodsDesc)) {
                goodsDesc = merchInfo.getName () + "收款";
            }
            if (StringUtil.isEmpty (goodsName)) {
                goodsName = merchInfo.getName () + "收款";
            }
            if (StringUtil.isEmpty (notifyUrl) && reqData.containsKey ("offlineNotifyUrl") && StringUtils.isNotBlank (reqData.getString ("offlineNotifyUrl"))) {
                notifyUrl = reqData.getString ("offlineNotifyUrl");
            }
            String returnUrl = reqData.containsKey ("returnUrl") ? reqData.getString ("returnUrl") : "";
            if (StringUtil.isEmpty (returnUrl) || (returnUrl.indexOf ("http://") == -1 && returnUrl.indexOf ("https://") == -1) || returnUrl.length () < 8) {
                reqData.put ("returnUrl", successUrl);
            }
            reqData.put ("notifyUrl", notifyUrl);
            reqData.put ("successUrl", successUrl);
            reqData.put ("goodsName", goodsName);
            reqData.put ("goodsDesc", goodsDesc);
            StringBuilder projectDomainUrl = new StringBuilder ();
            projectDomainUrl.append (domainName).append ("/").append (actualName);
            reqData.put ("orderId", cOrderNo);
            reqData.put ("merchantNo", merchInfo.getMerchNo ());
            reqData.put ("merchId", merchInfo.getId ());
            reqData.put ("password", merchInfo.getPassword ());
            
            reqData.put ("levelNo", merchInfo.getLevelNo());
            reqData.put ("levelViewNo", merchInfo.getLevelViewNo());
            
            reqData.put ("projectDomainUrl", projectDomainUrl.toString ());
            reqData.put ("merchName", merchInfo.getName ());
            reqData.put ("bankNo", merchInfo.getBankNo ());
            reqData.put ("amount", amount);
            reqData.put ("merchNo", merchNo);
            reqData.put ("checkRate", merchInfo.getCheckRate ());
            if ("h5".equals (channelEnum.getKey ()) || "sc".equals (channelEnum.getKey ())) {
                reqData.put ("vcOrderNo", channelEnum.getKey () + Constant.getAutoOrderNo ());
            } else {
                reqData.put ("vcOrderNo", channelEnum.getKey () + Constant.getAutoOrderNo ());
            }
            reqData.put ("userVcId", Constant.getRandomString (6));
            return Constant.successMsg ("请求参数验证通过");
        } catch (Exception e) {
            logger.error ("下单报文验证异常{}", reqData, e);
            return Constant.failedMsg ("下单报文验证异常,请联系运维人员");
        }
    }

    /**
     * @描述: 通道路由
     * @作者:nada
     * @时间:2018/12/24
     **/
    public MerchChannel autoRouteChannel (MerchChannel channel, String amount, String merchantId) throws ServletException {
        List<MerchChannel> findchannel = merchChannelService.findMerchChannelPayTypes (channel);
        if (findchannel == null || findchannel.size () < 1) {
            logger.error ("获取商户{}通道配置列表为空", channel.getMerchId ());
            return null;
        }
        BigDecimal traAmount = new BigDecimal (amount);
        for (MerchChannel merchChannel : findchannel) {
            //获取路由通道
            MerchChannel temp = merchChannelService.getRouteChannel (merchChannel, traAmount, merchantId);
            if (null != temp) {
                logger.info ("路由通道成功:{}", temp.getChannelSource ());
                return temp;
            }
        }
        return findchannel.get (0);
    }
}
