/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.online;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单转换Entity
 * @author 订单转换
 * @version 2018-08-21
 */
public class VcOnlineOrderMade{
	
	private BigDecimal id;
	private String orderNo;		// 订单号
	private String merchNo;		// 商户编号
	private BigDecimal traAmount;		// 交易金额
	private String openUrl;		// 对外开放链接
	private String qrcodeUrl;		// 支付二维码链接
	private int channelId;		// 通道ID
	private int paySource;		// 通道来源
	private long expiredTime;		// 有效期（秒）
	private String upMerchNo;		// 交易上游商户号
	private String upMerchKey;		// 交易上游key
	private Date createDate;		//
	private Date updateDate;
	private String remarks;
	private int openType;	//打开支付方式 91 微信 92 支付宝 99其他
	private int openNum;
	private BigDecimal payAmount;
	private String userId;
	private String payUserId;
	private String userName;
	private String netOrder;
	private String payType;

	public VcOnlineOrderMade() {
		super();
	}

	public static VcOnlineOrderMade buildAlipayMade(JSONObject reqData,int funType){
		VcOnlineOrderMade made = buildAlipayMade (reqData);
		made.setOpenType (funType);
		return made;
	}

	public static VcOnlineOrderMade buildAlipayMade(JSONObject reqData,String openUrl){
		VcOnlineOrderMade made = buildAlipayMade (reqData);
		made.setOpenUrl(openUrl);
		return made;
	}

	/**
	 * @描述:构建支付宝参数，默认5分钟有效
	 * @作者:nada
	 * @时间:2019/3/26
	 **/
	public static VcOnlineOrderMade buildAlipayMade(JSONObject reqData){
		VcOnlineOrderMade made = new VcOnlineOrderMade();
		made.setChannelId(reqData.getIntValue("channelLabel"));
		made.setExpiredTime(CacheConstants.EXPIRED_TIME_5);
		made.setUserId (reqData.getString ("appUserId"));
		made.setMerchNo(reqData.getString("merchantNo"));
		made.setOrderNo(reqData.getString("vcOrderNo"));
		made.setNetOrder (reqData.getString ("netOrder"));
		made.setPaySource(reqData.getIntValue("channelSource"));
		made.setTraAmount(reqData.getBigDecimal("amount"));
		made.setUpMerchKey(reqData.getString("channelDesKey").trim());
		made.setUpMerchNo(reqData.getString("channelKey").trim());
		made.setUserName (reqData.getString("appUserName"));
		made.setQrcodeUrl(reqData.getString("channelDesKey").trim());
		String orderNo = reqData.getString("vcOrderNo");
		String openurl = reqData.getString("projectDomainUrl") + "/api/" + HiDesUtils.desEnCode(orderNo);
		made.setOpenUrl(openurl);
		return made;
	}


	/**
	 * @描述:构建支付宝参数，默认5分钟有效
	 * @作者:nada
	 * @时间:2019/3/26
	 **/
	public static VcOnlineOrderMade buildCommonMade(JSONObject reqData){
		String channelKey = reqData.containsKey ("channelDesKey") ? reqData.getString ("channelDesKey").trim () : "";
		String upMerchNo = reqData.containsKey ("channelKey") ? reqData.getString ("channelKey").trim () : "";
		String vcOrderNo = reqData.containsKey ("vcOrderNo") ? reqData.getString ("vcOrderNo").trim () : "";

		VcOnlineOrderMade made = new VcOnlineOrderMade();
		made.setOpenUrl (reqData.getString ("projectDomainUrl") + "/code/" + HiDesUtils.desEnCode (vcOrderNo));
		made.setPaySource (reqData.getIntValue ("channelSource"));
		made.setChannelId (reqData.getIntValue ("channelLabel"));
		made.setOpenType (reqData.getIntValue ("channelSource"));
		made.setExpiredTime (CacheConstants.EXPIRED_TIME_5);
		made.setMerchNo (reqData.getString ("merchantNo"));
		made.setTraAmount(reqData.getBigDecimal("amount"));
		made.setOrderNo (vcOrderNo);
		made.setUpMerchKey(channelKey);
		made.setUpMerchNo(upMerchNo);
		made.setRemarks (channelKey);
		made.setQrcodeUrl (channelKey);
		return made;
	}

	public VcOnlineOrderMade(String orderNo) {
		this.orderNo = orderNo;
	}

	public VcOnlineOrderMade(String orderNo,String payUserId) {
		this.payUserId = payUserId;
		this.orderNo = orderNo;
	}

	public static VcOnlineOrderMade bulidMadeQr(String orderNo,String qrcodeUrl) {
		VcOnlineOrderMade made = new VcOnlineOrderMade();
		made.setOrderNo (orderNo);
		made.setQrcodeUrl (qrcodeUrl);
		return made;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	public String getMerchNo() {
		return merchNo;
	}

	public void setMerchNo(String merchNo) {
		this.merchNo = merchNo;
	}
	
	public BigDecimal getTraAmount() {
		return traAmount;
	}

	public void setTraAmount(BigDecimal traAmount) {
		this.traAmount = traAmount;
	}

	public String getOpenUrl() {
		return openUrl;
	}

	public void setOpenUrl(String openUrl) {
		this.openUrl = openUrl;
	}
	
	public String getQrcodeUrl() {
		return qrcodeUrl;
	}

	public void setQrcodeUrl(String qrcodeUrl) {
		this.qrcodeUrl = qrcodeUrl;
	}
	
	public String getUpMerchNo() {
		return upMerchNo;
	}

	public void setUpMerchNo(String upMerchNo) {
		this.upMerchNo = upMerchNo;
	}
	
	public String getUpMerchKey() {
		return upMerchKey;
	}

	public void setUpMerchKey(String upMerchKey) {
		this.upMerchKey = upMerchKey;
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public int getPaySource() {
		return paySource;
	}

	public void setPaySource(int paySource) {
		this.paySource = paySource;
	}
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getOpenType() {
		return openType;
	}

	public void setOpenType(int openType) {
		this.openType = openType;
	}

	public int getOpenNum() {
		return openNum;
	}

	public void setOpenNum(int openNum) {
		this.openNum = openNum;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(long expiredTime) {
		this.expiredTime = expiredTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPayUserId () {
		return payUserId;
	}

	public void setPayUserId (String payUserId) {
		this.payUserId = payUserId;
	}

	public String getUserName () {
		return userName;
	}

	public void setUserName (String userName) {
		this.userName = userName;
	}

	public String getNetOrder () {
		return netOrder;
	}

	public void setNetOrder (String netOrder) {
		this.netOrder = netOrder;
	}
}