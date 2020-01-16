package com.vc.onlinepay.cmd;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.enums.PayCommandEnum;
import com.vc.onlinepay.enums.PayTypeEnum;
import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.pay.api.order.GatewayServiceApi;
import com.vc.onlinepay.pay.api.order.H5ServiceApi;
import com.vc.onlinepay.pay.api.order.ScanPayServiceApi;
import com.vc.onlinepay.pay.api.order.UnionServiceApi;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.channel.ChannelSubNo;
import com.vc.onlinepay.persistent.entity.channel.MerchChannel;
import com.vc.onlinepay.persistent.entity.merch.SupplierSubno;
import com.vc.onlinepay.persistent.entity.merch.XkPddBuyer;
import com.vc.onlinepay.persistent.entity.merch.XkPddGoods;
import com.vc.onlinepay.persistent.entity.merch.XkPddMerch;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderAa;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderDetail;
import com.vc.onlinepay.persistent.mapper.merch.XkPddBuyerMapper;
import com.vc.onlinepay.persistent.mapper.merch.XkPddGoodsMapper;
import com.vc.onlinepay.persistent.mapper.merch.XkPddMerchMapper;
import com.vc.onlinepay.persistent.monitor.AsynMonitor;
import com.vc.onlinepay.persistent.service.channel.MerchChannelServiceImpl;
import com.vc.onlinepay.persistent.service.channel.SupplierSubnoServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderAaService;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpBrowserTools;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName: OrderCheckServiceImpl
 * @Description: 对外提供下订单，交易的服务service
 * @author: lihai
 * @date: 2018年4月18日 上午11:19:07
 * @Copyright: 2018 www.guigu.com Inc. All rights reserved.
 *             注意：本内容仅限于本信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
@Component
public class TradeCmd {

	public static final Logger logger = LoggerFactory.getLogger(TradeCmd.class);

	@Autowired
	private VcOnlineOrderServiceImpl vcOnlineOrderService;
	@Autowired
	private MerchChannelServiceImpl merchChannelService;
	@Autowired
	private CommonPayService commonPayService;
	@Autowired
	private GatewayServiceApi gatewayServiceApi;
	@Autowired
	private H5ServiceApi h5ServiceApi;
	@Autowired
	private ScanPayServiceApi saScanPayServiceApi;
	@Autowired
	private UnionServiceApi unionServiceApi;
	@Autowired
	private CoreEngineProviderService coreEngineProviderService;
	@Autowired
	private AsynMonitor asynMonitor;
	@Autowired
	private VcOnlineOrderAaService vcOnlineOrderAaService;
	@Autowired
	private SupplierSubnoServiceImpl supplierSubnoService;
	@Autowired
	private XkPddMerchMapper xkPddMerchMapper;
	@Autowired
	private XkPddGoodsMapper xkPddGoodsMapper;
	@Autowired
	private XkPddBuyerMapper xkPddBuyerMapper;

	@Value ("${onlinepay.project.domainName:}")
	private String domainName;

	@Value ("${onlinepay.project.actualName:}")
	private String actualName;

	@Value ("${onlinepay.project.successUrl:}")
	private String successUrl;
	@Value ("${spring.datasource.username:}")
	private String datasourceUsername;

	/**
	 * @描述:交易下单业务处理
	 * @作者:nada
	 * @时间:2019/3/17
	 **/
	public JSONObject doAllTransOrder(JSONObject reqData) {
		try {
			//验证下单参数
			JSONObject result = this.checkReqData(reqData);
			if (!Constant.isOkResult (result)) {
				return result;
			}
			//商户通道
			MerchChannel merchChannel = this.autoRouteChannel(MerchChannel.getMerchChannel(reqData));
			result = this.checkChannel(reqData, merchChannel);
			if (!Constant.isOkResult (result)) {
				this.doRestFailedOrder(reqData, merchChannel);
				return result;
			}
			//保存订单
			result = this.doRestOrder(reqData, merchChannel);
			if (!Constant.isOkResult (result)) {
				return result;
			}
			//金额浮动处理
			//this.channelAmountfloat(reqData,merchChannel);
			//支付路由
			return this.doGatewayRoute(reqData);
		} catch (Exception e){
			logger.error("请求报文业务分发处理异常", e);
			return Constant.failedMsg("请求报文业务分发处理失败");
		}
	}

	/**
	 * @描述:交易支付方式分发
	 *  扫码支付方式： 1: 微信扫码  2: 支付宝扫码 3: QQ扫码 4: 京东钱包扫码 5: 微信公众号 6: 支付宝公众号 8: 银联二维码 17: 支付宝PC条形码
	 * 	银联支付方式： 13: 快捷直冲
	 * 	网关支付方式： 9: wap网关支付 15: 手机网关
	 * 	H5支付方式：  10: 支付宝H5 11: QQ H5 12: 微信H5 16: 京东h5
	 * 	快捷支付方式： 7: 快捷支付
	 * @时间:2019/3/17
	 **/
	public JSONObject doGatewayRoute(JSONObject reqData) {
		int payType = reqData.getIntValue ("payType");
		PayCommandEnum payTypeEnum = PayCommandEnum.getPayTypeEnum (payType);
		try {
			switch (payTypeEnum) {
				case scan:
					return saScanPayServiceApi.doRestPay(reqData);
				case h5:
					return h5ServiceApi.doRestPay(reqData);
				case gateway:
					return gatewayServiceApi.doRestPay(reqData);
				case union:
					return unionServiceApi.doRestPay(reqData);
				case quick:
					return unionServiceApi.doRestPay(reqData);
					//return null;
				default:
					return Constant.failedMsg("错误的支付类型" + payType);
			}
		} catch (Exception e) {
			logger.error("报文网关分发路由异常", e);
			return Constant.failedMsg("报文网关分发路由异常");
		}
	}

	/**
	 * @描述:验证交易报文
	 * @作者:nada
	 * @时间:2019/3/17
	 **/
	public JSONObject checkReqData(JSONObject reqData) {
		try {
			if (reqData == null || reqData.isEmpty ()) {
				return Constant.failedMsg ("交易报文解析为空,请检查是否json+post提交");
			}
			if (!reqData.containsKey("payType") || StringUtils.isEmpty(reqData.getString("payType"))) {
				return Constant.failedMsg("请求报文payType参数为空");
			}
			if (!PayTypeEnum.isExist(reqData.getIntValue("payType"))) {
				return Constant.failedMsg("请求报文payType参数非法" + reqData.getIntValue("payType"));
			}
			if (!reqData.containsKey("tradeNo") || StringUtils.isEmpty(reqData.getString("tradeNo"))) {
				return Constant.failedMsg("请求报文tradeNo参数为空");
			}
			if (reqData.getString("tradeNo").length() < 10 && reqData.getString("tradeNo").length() > 32) {
				return Constant.failedMsg("请求订单号10-32长度");
			}
			if (!reqData.containsKey("amount") || StringUtils.isEmpty(reqData.getString("amount"))) {
				return Constant.failedMsg("请求报文amount参数为空");
			}
			if (!reqData.containsKey("goodsName") || StringUtils.isEmpty(reqData.getString("goodsName"))) {
				return Constant.failedMsg("请求报文goodsName参数为空");
			}
			if (!reqData.containsKey("goodsDesc") || StringUtils.isEmpty(reqData.getString("goodsDesc"))) {
				return Constant.failedMsg("请求报文goodsDesc参数为空");
			}
			if (!reqData.containsKey("notifyUrl") || StringUtils.isEmpty(reqData.getString("notifyUrl"))) {
				return Constant.failedMsg("请求报文notifyUrl参数为空");
			}
			if (!reqData.containsKey("returnUrl") || StringUtils.isEmpty(reqData.getString("returnUrl"))) {
				reqData.put("returnUrl", successUrl);
			}
			if (commonPayService.verifyCacheMerchOrderExist(reqData.getString("tradeNo"))) {
				logger.error("请求订单号重复{}", reqData);
				return Constant.failedMsg("请求订单号重复" + reqData.getString("tradeNo"));
			}
			//交易IP白名单
			String ipaddress = reqData.getString ("netIpaddress");
			String isSecurity = reqData.getString ("isSecurity");
			String merchCaseIps = reqData.getString ("merchCaseIps");
			boolean isSecurityHost = coreEngineProviderService.isAllowedAccessIp ("payTestHost", ipaddress);
			if (!isSecurityHost) {
				if ("3".equals (isSecurity) || "4".equals (isSecurity)) {
					if (StringUtils.isEmpty (merchCaseIps)) {
						return Constant.failedMsg ("温馨提示：请备案IP白名单认证");
					}
					if (!merchCaseIps.contains (ipaddress)) {
						return Constant.failedMsg ("温馨提示：" + ipaddress + "IP不在备案白名单");
					}
				}
			}
			//日切时间
			String onlineOrderLimit = coreEngineProviderService.getCacheCfgKey (CacheConstants.ONLINE_LIMIT_TIME_ORDER);
			if (Constant.isEffectiveTimeNow (onlineOrderLimit)) {
				return Constant.failedMsg ("交易日切时间,请稍后再尝试交易");
			}
			String returnUrl = reqData.containsKey ("returnUrl") ? reqData.getString ("returnUrl") : "";
			if (StringUtil.isEmpty (returnUrl) || (returnUrl.indexOf ("http://") == -1 && returnUrl.indexOf ("https://") == -1) || returnUrl.length () < 8) {
				reqData.put ("returnUrl", successUrl);
			}
			String orderNo =  reqData.getString("vcOrderNo");
			int payType = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
			PayCommandEnum payTypeEnum = PayCommandEnum.getPayTypeEnum (payType);
			switch (payTypeEnum) {
				case scan:
					orderNo = "sc"+orderNo;break;
				case h5:
					orderNo = "h5"+orderNo;break;
				case union:
					orderNo = "yl"+orderNo;break;
				case gateway:
					orderNo = "wg"+orderNo;break;
				case quick:
					orderNo = "kj"+orderNo;break;
			}
			reqData.put("orderNo",orderNo);
			reqData.put("vcOrderNo",orderNo);
			return Constant.successMsg("下游上送请求报文参数验证通过");
		} catch (Exception e) {
			logger.error("下游上送请求报文参数验证异常", e);
			return Constant.failedMsg("下游上送请求报文参数验证失败");
		}
	}

	/**
	 * @描述: 交易通道路由
	 * @作者:nada
	 * @时间:2018年3月7日 下午5:02:54
	 */
	public MerchChannel autoRouteChannel(MerchChannel channel) throws RuntimeException {
		List<MerchChannel> merchInfos = merchChannelService.findMerchChannelPayTypes(channel);
		if (merchInfos == null || merchInfos.size() < 1) {
			logger.error("商户{}通道{}未配置", channel.getMerchId(), channel.getPayType());
			return null;
		}
		if (merchInfos.size() == 1) {
			return merchInfos.get(0);
		}
		for (MerchChannel merchChannel : merchInfos) {
			boolean isOK = Constant.checkRfTime(merchChannel.getTraStartTime(), merchChannel.getTraEndTime());
			if (isOK) {
				return merchChannel;
			}
		}
		logger.error("商户配置{}通道,默认T0,T1,优先级取一条", merchInfos.size());
		return merchInfos.get(0);
	}

	/**
	 * @描述:交易通道验证
	 * @时间:2018年5月10日 上午10:42:43
	 */
	@Transactional (readOnly = false)
	public JSONObject checkChannel (JSONObject reqData, MerchChannel merchChannel) {
		try {
			if (merchChannel == null || merchChannel.getChannelSource () < 1) {
				return Constant.failedMsg ("商户通道未配置,请核实开通支付类型");
			}
			if (merchChannel.getStatus () != 1) {
				return Constant.failedMsg ("商户通道状态禁用,请核实通道状态");
			}
			BigDecimal traAmount = new BigDecimal (reqData.getString ("amount"));
			BigDecimal actualAmount = Constant.getActualMoney (traAmount,merchChannel.getTranRate ());
			int r0 = actualAmount.compareTo (new BigDecimal ("0"));
			if (r0 < 1) {
				return Constant.failedMsg ("温馨提示:交易金额过小");
			}
			int r1 = traAmount.compareTo (merchChannel.getMinTraPrice ());
			if (r1 < 0) {
				return Constant.failedMsg ("温馨提示:此通道最低限额" + merchChannel.getMinTraPrice ());
			}
			int r2 = traAmount.compareTo (merchChannel.getMaxTraPrice ());
			if (r2 > 0) {
				return Constant.failedMsg ("温馨提示:此通道交易限额" + merchChannel.getMaxTraPrice ());
			}
			long channelSource = merchChannel.getChannelSource ();
			String channelKey = StringUtils.deleteWhitespace(merchChannel.getChannelKey());
			String channelDesKey = StringUtils.deleteWhitespace(merchChannel.getChannelDesKey());
			reqData.put ("tradeRate", merchChannel.getTranRate ());
			reqData.put ("channelSource", channelSource);
			reqData.put ("channelPayUrl", merchChannel.getPayUrl ());
			reqData.put ("channelSubMerchNo", merchChannel.getMerchTradeNo ());
			reqData.put ("channelSubMerchKey", merchChannel.getMerchTradeKey ());
			reqData.put ("channelLabel", merchChannel.getChannelId ());
			reqData.put ("serviceCallbackUrl", merchChannel.getServiceCallbackUrl());
			if (merchChannel.getSubNoStatus()!= 1) {
				reqData.put ("channelKey", channelKey);
				reqData.put ("channelDesKey", channelDesKey);
				return Constant.successMsg ("交易通道验证通过");
			}
			/*Long merchType = reqData.containsKey("merchType")?reqData.getLong("merchType"):0;
			if(merchType!=null && merchType ==8){
				return this.autoOpenRoute (reqData, merchChannel, traAmount);
			}
			if ("1".equals (reqData.getString ("checkRate"))) {
				if (merchChannel.getTranRate () == null || merchChannel.getChannelCost () == null || merchChannel.getTranRate ().compareTo (merchChannel.getChannelCost ()) < 1) {
					return Constant.failedMsg ("交易费率配置有误,请联系运维人员");
				}
			}*/
			switch ((int) channelSource) {
				case 51: case 65: case 81: case 83:
					this.alipayAutoRoute (reqData, merchChannel, traAmount);
					break;
				case 111:
					JSONObject result = this.autoPddSubnoRout(reqData,merchChannel,traAmount);
					if(!Constant.isOkResult(result)){
						reqData.putAll(result);
						return result;
					}
					break;
				case 93:
					JSONObject res = this.setSqbKey (reqData, merchChannel, traAmount);
					if(!res.getString ("code").equals (Constant.SUCCESSS)){
						return res;
					}
					break;
				case 125:
					this.setSuppNoData (reqData);
					break;
				default:
					result = this.setDefaultKey (reqData,merchChannel,traAmount);
					if(!Constant.isOkResult(result)){
						reqData.putAll(result);
						return result;
					}
					break;
			}
			return Constant.successMsg ("交易通道验证通过");
		} catch (Exception e) {
			logger.error ("交易通道验证异常", e);
			return Constant.failedMsg ("交易通道验证异常,请联系运维人员");
		}
	}

	/**
	 * @描述:路由pdd子商户号
	 * @时间:2019/5/2
	 **/
	private JSONObject autoPddSubnoRout(JSONObject reqData, MerchChannel merchChannel, BigDecimal traAmount){
		try {
			String orderNo = reqData.getString ("vcOrderNo");
			Long channelSource = merchChannel.getChannelSource();
			Integer type = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
			String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
			Integer payType = Constant.getPddPayType(type,service);
			ChannelSubNo channelSubNo = new ChannelSubNo (channelSource,payType,traAmount);
			List<ChannelSubNo> lists = merchChannelService.getChannelSubNoList(channelSubNo);
			if (lists == null || lists.size () < 1) {
				return Constant.failedMsg ("全部码商满额");
			}
			int index = (int) (Math.random()* lists.size());
			channelSubNo = lists.get (index);
			if(channelSubNo == null || StringUtil.isEmpty (channelSubNo.getUpMerchNo ())){
				return Constant.failedMsg ("码商配置错误");
			}
			String upMerchNo = channelSubNo.getUpMerchNo ();
			String upMerchKey = channelSubNo.getUpMerchKey();
			reqData.put ("channelKey",upMerchNo);
			reqData.put ("channelDesKey",upMerchKey);
			logger.info ("PDD路由订单:{},商户号:{},子账号:{}",orderNo,upMerchNo,upMerchKey);
			return Constant.successMsg("路由pdd子商户号成功");
		} catch (Exception e) {
			logger.error ("路由pdd子商户号异常");
			return Constant.failedMsg ("路由pdd子商户号异常");
		}
	}

	/**
	 * @描述:设置收钱吧信息
	 * @作者:nada
	 * @时间:2019/5/2
	 **/
	private JSONObject setSqbKey(JSONObject reqData, MerchChannel merchChannel, BigDecimal traAmount){
		try {
			Long channelId = merchChannel.getChannelId ();
			if(channelId == 232){
				channelId = 231L;
			}
			Integer type = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
			String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
			if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
				type = 2;
			}else if(type == 1 || type == 12 || Constant.service_weixin.equals (service)){
				type = 1;
			}else{
				type = 0;
			}
			ChannelSubNo channelSubNo = new ChannelSubNo (channelId,traAmount);
			channelSubNo.setPayType (type);
			List<ChannelSubNo> lists = merchChannelService.getChannelSubNoList(channelSubNo);
			if (lists == null || lists.size () < 1) {
				return Constant.failedMsg ("当前请求频繁，请稍后再试");
			}
			if (lists.size () == 1) {
				channelSubNo = lists.get (0);
			}else{
				for (ChannelSubNo no : lists) {
					boolean isOk = coreEngineProviderService.checkSqb (no.getUpMerchNo (),type);
					if(no.getPayType ()<1 && !isOk){
						continue;
					}
					if (no.getMinPrice () > 0 && no.getMaxPrice () > 0 && no.getMinPrice () <= traAmount.doubleValue () && no.getMaxPrice () > traAmount.doubleValue ()) {
						channelSubNo = lists.get (0);
						break;
					}
				}
			}
			if(channelSubNo == null || StringUtil.isEmpty (channelSubNo.getUpMerchNo ()) || channelSubNo.getLastOrderTime () == null){
				return Constant.failedMsg ("当前请求频繁，请稍后再试");
			}
			long diffTime = coreEngineProviderService.getIntCacheCfgKey ("online.sqb.order.diff.time");
			long diff = (System.currentTimeMillis ()-channelSubNo.getLastOrderTime ().getTime ())/1000;
			if(diff<diffTime){
				return Constant.failedMsg ("当前请求频繁，请稍后"+(diffTime-diff)+"秒后再试");
			}
			String upMerchNo = channelSubNo.getUpMerchNo ();
			coreEngineProviderService.setSqbNum (upMerchNo,type);
			reqData.put ("channelKey",upMerchNo);
			reqData.put ("channelDesKey", channelSubNo.getUpMerchKey ());
			logger.info ("默认轮询订单:{},商户号:{}",reqData.getString ("vcOrderNo"),reqData.getString("channelKey"));
			return Constant.successMsg ("设置成功");
		} catch (OnlineServiceException e) {
			logger.error ("设置收钱吧信息异常");
			return Constant.failedMsg ("设置收钱吧信息异常");
		}
	}
	
	/**
	 * @描述:设置默认信息
	 * @作者:nada
	 * @时间:2018/12/25
	 **/
	private JSONObject setDefaultKey(JSONObject reqData, MerchChannel merchChannel, BigDecimal traAmount){
		Long channelId = merchChannel.getChannelId ();
		if(channelId == 232){
			channelId = 231L;
		}
		ChannelSubNo channelSubNo = merchChannelService.getChannelSubNo (new ChannelSubNo (channelId), traAmount.doubleValue ());
		if (channelSubNo != null && StringUtil.isNotEmpty (channelSubNo.getUpMerchNo ()) && StringUtil.isNotEmpty (channelSubNo.getUpMerchKey ())) {
			reqData.put ("channelKey", channelSubNo.getUpMerchNo ());
			reqData.put ("channelDesKey", channelSubNo.getUpMerchKey ());
		}else{
			return Constant.failedMsg ("无默认子商户号");
		}
		logger.info ("默认轮询订单:{},商户号:{}",reqData.getString ("vcOrderNo"),reqData.getString("channelKey"));
		return Constant.successMsg ("设置成功");
	}

	/**
	 * @描述:设置支付宝账号
	 * @作者:nada
	 * @时间:2019/4/26
	 **/
	private JSONObject setSuppNoData (JSONObject reqData) {
		String orderNo =  reqData.getString("vcOrderNo");
		String amount2 = reqData.getString("amount").replace (".00","");
		if(!Constant.isNumeric (amount2)){
			logger.info ("金额不是整数:{},orderNo:{}",amount2,orderNo);
			reqData.put ("isOkSuppSubno", Constant.FAILED);
			return reqData;
		}
		BigDecimal traAmount = new BigDecimal (reqData.getString ("amount"));
		VcOnlineOrderAa vcOnlineOrderAa = vcOnlineOrderAaService.findOneOkAATradNo(new VcOnlineOrderAa(traAmount.intValue ()));
		if(vcOnlineOrderAa == null || vcOnlineOrderAa.getId () == null){
			logger.info ("没有可用库存:{},orderNo:{}",amount2,orderNo);
			reqData.put ("isOkSuppSubno", Constant.FAILED);
			return reqData;
		}
		vcOnlineOrderAa.setOrderNo (orderNo);
		int r = vcOnlineOrderAaService.updateStatus (vcOnlineOrderAa);
		if(r<1){
			logger.error ("更新失败{}",orderNo);
			vcOnlineOrderService.updateOrderDes(orderNo,"资金池更新失败",orderNo);
			reqData.put ("isOkSuppSubno", Constant.FAILED);
			return reqData;
		}
		reqData.put ("isOkSuppSubno", Constant.SUCCESSS);
		reqData.put ("aaDdPayUrlUrl", vcOnlineOrderAa.getRemarks());
		reqData.put ("aaDdPayAAOrderNo", vcOnlineOrderAa.getAaOrderNo());
		SupplierSubno supplierSubno = supplierSubnoService.getCacheSubNo (vcOnlineOrderAa.getToken());

		reqData.put ("isOkSuppSubno", Constant.SUCCESSS);
		reqData.put ("channelDesKey", supplierSubno.getUpMerchKey ());
		reqData.put ("channelKey", supplierSubno.getUpMerchNo ());
		reqData.put ("cardIdx", supplierSubno.getCardIdx ());
		reqData.put ("bankRemarks", supplierSubno.getRemarks ());
		reqData.put ("appType", supplierSubno.getType ());
		reqData.put ("appUserName", supplierSubno.getName ());
		reqData.put ("cardNo", supplierSubno.getBankNo ());
		reqData.put ("bankMark", supplierSubno.getBankMark ());
		reqData.put ("appUserId", supplierSubno.getUserId ());
		reqData.put ("appId", supplierSubno.getAppId ());
		reqData.put ("privateKey", supplierSubno.getPrivateKey ());
		reqData.put ("publicKey", supplierSubno.getPublicKey ());
		return reqData;
	}

	/**
	 * @描述:保存交易订单
	 * @时间:2018年5月17日 下午4:57:12
	 */
	public JSONObject doRestOrder(JSONObject reqData, MerchChannel merchChannel) {
		try {
			VcOnlineOrder vcOnlineOrder = VcOnlineOrder.buildVcOnlineOrder (reqData, merchChannel);
			int res = vcOnlineOrderService.save(vcOnlineOrder);
			if (res < 1) {
				return Constant.failedMsg("保存交易订单失败");
			}
			res = vcOnlineOrderService.saveDetail(VcOnlineOrderDetail.buildOrder (vcOnlineOrder, reqData, merchChannel));
			if (res < 1) {
				return Constant.failedMsg ("保存交易订单明细失败");
			}
			decodeChannelKey (reqData);
			return Constant.successMsg ("保存交易订单成功");
		} catch (Exception e) {
			logger.error ("保存交易订单处理异常", e);
			return Constant.failedMsg ("保存交易订单异常,请联系运维人员");
		}
	}

	/**
	 * @描述:保存失败交易订单
	 * @时间:2018年5月17日 下午4:57:12
	 */
	public JSONObject doRestFailedOrder(JSONObject reqData, MerchChannel merchChannel) {
		try {
			VcOnlineOrder vcOnlineOrder = VcOnlineOrder.buildFailedOnlineOrder(reqData,merchChannel);
			int res = vcOnlineOrderService.save(vcOnlineOrder);
			if (res < 1) {
				return Constant.failedMsg("保存订单失败");
			}
			return Constant.successMsg("保存订单成功");
		} catch (Exception e) {
			logger.error("保存订单处理异常", e);
			return Constant.failedMsg ("保存订单异常");
		}
	}

	/**
	 * @描述:租用系统账号路由
	 * @作者:nada
	 * @时间:2018/12/14
	 **/
	private JSONObject autoOpenRoute (JSONObject reqData, MerchChannel merchChannel, BigDecimal traAmount) {
		String accountNo = reqData.containsKey("accountNo")?reqData.getString("accountNo"):"";
		String orderNo = reqData.getString("vcOrderNo");
		ChannelSubNo channelSubNo = new ChannelSubNo(traAmount,merchChannel.getMerchId());
		if(StringUtil.isNotEmpty(accountNo)){
			channelSubNo.setUpMerchNo(accountNo);
		}
		channelSubNo = merchChannelService.getoneChannelSubNo(channelSubNo);
		if(channelSubNo == null){
			return Constant.failedMsg("没有可以用账号");
		}
		if(channelSubNo!=null){
			reqData.put ("findedAlipayAccount", Constant.SUCCESSS);
			reqData.put ("channelKey", channelSubNo.getUpMerchNo ());
			reqData.put ("channelDesKey", channelSubNo.getUpMerchKey ());
			reqData.put ("bankRemarks", channelSubNo.getRemarks ());
			reqData.put ("cardIdx", channelSubNo.getCardIdx ());
			reqData.put ("appType", channelSubNo.getType ());
			reqData.put ("appUserName", channelSubNo.getName ());
			reqData.put ("appUserId", channelSubNo.getUserId());
			reqData.put ("cardNo", channelSubNo.getBankNo ());
			reqData.put ("bankMark", channelSubNo.getBankMark ());
		}
		merchChannelService.updateLastOrderTime(channelSubNo);
		logger.info("轮询算法获取到订单:{},渠道号:{},支付宝账号{}",orderNo,channelSubNo.getUpMerchKey (),channelSubNo.getUpMerchNo ());
		return Constant.successMsg("路由成功");
	}

	/**
	 * @描述:自研支付宝的路由规则
	 * @作者:nada
	 * @时间:2018/12/14
	 **/
	private void alipayAutoRoute (JSONObject reqData, MerchChannel merchChannel, BigDecimal traAmount) {
		long channelId = merchChannel.getChannelId ();
		String orderNo = reqData.getString ("vcOrderNo");
		String clientIp = reqData.getString ("ipaddress");
		int loopRobin = coreEngineProviderService.getIntCacheCfgKey (CacheConstants.ONLINE_SUPPLIER_LOOP_ROBIN);
		ChannelSubNo channelSubNoEntity = new ChannelSubNo (orderNo, channelId, merchChannel.getChannelSource (), traAmount, loopRobin, clientIp);
		SupplierSubno supplierSubno = coreEngineProviderService.getLoopRobin (channelSubNoEntity);
		if (supplierSubno != null) {
			if (StringUtil.isNotEmpty (supplierSubno.getUpMerchNo ())) {
				reqData.put ("findedAlipayAccount", Constant.SUCCESSS);
				reqData.put ("channelKey", supplierSubno.getUpMerchNo ());
			}
			if (StringUtil.isNotEmpty (supplierSubno.getUpMerchKey ())) {
				reqData.put ("channelDesKey", supplierSubno.getUpMerchKey ());
			}
			reqData.put ("bankRemarks", supplierSubno.getRemarks ());
            reqData.put ("cardIdx", supplierSubno.getCardIdx ());
			reqData.put ("appType", supplierSubno.getType ());
			reqData.put ("appUserName", supplierSubno.getName ());
			reqData.put ("cardNo", supplierSubno.getBankNo ());
			reqData.put ("bankMark", supplierSubno.getBankMark ());
			reqData.put ("appUserId", supplierSubno.getUserId ());
			reqData.put ("appId", supplierSubno.getAppId ());
			reqData.put ("privateKey", supplierSubno.getPrivateKey ());
			reqData.put ("publicKey", supplierSubno.getPublicKey ());
		}else{
			reqData.put ("findedAlipayAccount", Constant.FAILED);
		}
		logger.info ("支付宝轮询订单:{},channelKey:{},appId:{},appUserId:{}", orderNo,reqData.get("channelKey"),reqData.get("appId"),reqData.get("appUserId"));
	}

	/**
	 * @描述:添加网络参数
	 * @时间:2018年7月17日 下午12:08:54
	 */
	public void addNetReqPrms (HttpServletRequest request, JSONObject reqData) {
		reqData.put ("localPort", request.getLocalPort ());
		reqData.put ("netIpaddress", HttpBrowserTools.getIpAddr (request));
		reqData.put ("reqDomain", request.getServerName ());
	}


	/**
	 * @描述: 解密key
	 * @作者:nada
	 * @时间:2018/12/20
	 **/
	public void decodeChannelKey (JSONObject reqData){
		try {
			String decodeKey = coreEngineProviderService.getDecodeChannlKey (reqData.getString ("channelDesKey"));
			reqData.put ("channelDesKey", decodeKey);
			if(reqData.containsKey("channelWxDesKey")){
				reqData.put ("channelWxDesKey", coreEngineProviderService.getDecodeChannlKey (reqData.getString ("channelWxDesKey")));
			}
		} catch (Exception e) {
			e.printStackTrace ();
			logger.error ("解密key异常",e);
		}
	}

	/**
	 * 订单存储后，订单整数金额浮动处理（+—0.05）
	 */
	public void channelAmountfloat (JSONObject reqData, MerchChannel merchChannel) {
		long channelSource = merchChannel.getChannelSource ();
		String amountFloatChannel = coreEngineProviderService.getCacheCfgKey (CacheConstants.ONLINE_ORDER_AMOUNT_FLOAT_CHANNEL);
		if(org.apache.commons.lang.StringUtils.isBlank (amountFloatChannel) || !amountFloatChannel.contains(String.valueOf (channelSource))){
			return;
		}
		BigDecimal realAmount = new BigDecimal (reqData.getString ("amount")).setScale (2, BigDecimal.ROUND_HALF_UP);
		if(realAmount.compareTo (realAmount.setScale (0, BigDecimal.ROUND_HALF_UP)) != 0){
			return;
		}
		BigDecimal amount = realAmount.multiply (new BigDecimal (100)).setScale (0, BigDecimal.ROUND_HALF_UP);
		Random r = new Random ();
		if (realAmount.compareTo (merchChannel.getMaxTraPrice ()) == 0) {
			amount = amount.subtract (new BigDecimal (r.nextInt (19)));
		} else {
			amount = amount.add (new BigDecimal (r.nextInt (19)));
		}
		amount = amount.divide (new BigDecimal (100)).setScale (2, BigDecimal.ROUND_HALF_UP);
		reqData.put ("amount", String.valueOf (amount));
	}

	/**
	 * @描述:获取监听
	 * @作者:nada
	 * @时间:2017年12月19日 下午3:42:31
	 */
	public ResultListener tradResultListener (JSONObject reqData) {
		return new ResultListener () {
			@Override
			public JSONObject successHandler (JSONObject resultData) {
				logger.info ("获取监听successHandler结果:{}", resultData);
				if (resultData == null || resultData.isEmpty ()) {
					return Constant.failedMsg ("下单响应为空,请稍后重试");
				}
				if (!resultData.containsKey ("code") || !Constant.SUCCESSS.equals (resultData.getString ("code"))) {
					String msg = resultData.containsKey ("msg")?resultData.getString ("msg"):"下单失败";
					commonPayService.updateOrderStatus (reqData.getString ("vcOrderNo"), 2, msg);
					return Constant.failedMsg (msg);
				}
				String msg = "下单成功";
				int source = reqData.getIntValue ("channelSource");
				if (source == 126) {
					msg = resultData.containsKey ("msg")?resultData.getString ("msg"):"下单成功";
				}
				String vcOrderNo = reqData.getString ("vcOrderNo");
				String realAmount = resultData.containsKey ("realAmount") ? resultData.getString ("realAmount") : null;
				String pOrder = resultData.containsKey ("pOrderNo") ? resultData.getString ("pOrderNo") : vcOrderNo;
				if (StringUtil.isNotEmpty (realAmount)) {
					commonPayService.updateOrderStatus (reqData.getString ("vcOrderNo"), 1,msg, pOrder, realAmount);
				} else {
					commonPayService.updateOrderStatus (reqData.getString ("vcOrderNo"), 1,msg,pOrder);
				}
				return resultData;
			}
			@Override
			public JSONObject paddingHandler (JSONObject resultData) {
				logger.info ("监听paddingHandler结果:{}", resultData);
				return resultData;
			}

			@Override
			public JSONObject failedHandler (JSONObject resultData) {
				logger.info ("监听failedHandler结果:{}", resultData);
				if (resultData == null || resultData.isEmpty ()) {
					return Constant.failedMsg ("下单响应为空,请稍后重试");
				}
				String message = resultData.containsKey ("msg") ? resultData.getString ("msg") : "下单失败,请稍后重试";
				commonPayService.updateOrderStatus (reqData.getString ("vcOrderNo"), 2, message);
				return Constant.failedMsg ( message);
			}
		};
	}
}
