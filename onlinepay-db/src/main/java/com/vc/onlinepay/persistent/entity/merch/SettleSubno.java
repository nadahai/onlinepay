/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.merch;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 账号归集Entity
 * @author 李海
 * @version 2018-10-23
 */
public class SettleSubno implements Serializable{
	
	private static final long serialVersionUID = -4153555090993339661L;
	private int merchId;		// 商户ID
	private int status;		// 状态：1:正常 2:禁用 3:失效
	private String upMerchNo;		// 归集账号
	private String upMerchKey;		// 归集渠道
	private BigDecimal dayQuotaAmount;		// 日限额
	private BigDecimal quotaAmount;		// 总限额
	private BigDecimal dayTraAmount;		// 日归集额
	private BigDecimal traAmount;		// 总归集额
	private String remark;		// 备注
	
	
	private int minPrice;		// 单笔最小
	private int maxPrice;		// 单笔最大
	private BigDecimal settleAmount;
	private BigDecimal daySettleAmount;
	private BigDecimal settleRate;
	
	private Date lastDay;		// 最后修改日
	private Date lastOrderTime;		// 最后下单时间
	private String appid;		// appID
	private String privateKey;		// 支付宝私钥
	private String publicKey;		// 支付宝公钥
	private String userId;		// 用户ID
	private String merchName;
	
	private Date beginUpdateDate; // 开始 更新时间
	private Date endUpdateDate; // 结束 更新时间
	private Date beginCreateDate; // 开始 注册时间
	private Date endCreateDate; // 结束 注册时间
	private int seq;
	private String name;

	public SettleSubno(){
	}

	public SettleSubno (String upMerchNo,BigDecimal traAmount) {
		this.upMerchNo = upMerchNo;
		this.traAmount = traAmount;
	}

	public SettleSubno(BigDecimal traAmount){
		this.traAmount = traAmount;
	}
	
	public int getMerchId() {
		return merchId;
	}
	public void setMerchId(int merchId) {
		this.merchId = merchId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public BigDecimal getDayQuotaAmount() {
		return dayQuotaAmount;
	}
	public void setDayQuotaAmount(BigDecimal dayQuotaAmount) {
		this.dayQuotaAmount = dayQuotaAmount;
	}
	public BigDecimal getQuotaAmount() {
		return quotaAmount;
	}
	public void setQuotaAmount(BigDecimal quotaAmount) {
		this.quotaAmount = quotaAmount;
	}
	public BigDecimal getDayTraAmount() {
		return dayTraAmount;
	}
	public void setDayTraAmount(BigDecimal dayTraAmount) {
		this.dayTraAmount = dayTraAmount;
	}
	public BigDecimal getTraAmount() {
		return traAmount;
	}
	public void setTraAmount(BigDecimal traAmount) {
		this.traAmount = traAmount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}
	public int getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}
	public BigDecimal getSettleAmount() {
		return settleAmount;
	}
	public void setSettleAmount(BigDecimal settleAmount) {
		this.settleAmount = settleAmount;
	}
	public BigDecimal getDaySettleAmount() {
		return daySettleAmount;
	}
	public void setDaySettleAmount(BigDecimal daySettleAmount) {
		this.daySettleAmount = daySettleAmount;
	}
	public Date getLastDay() {
		return lastDay;
	}
	public void setLastDay(Date lastDay) {
		this.lastDay = lastDay;
	}
	public Date getLastOrderTime() {
		return lastOrderTime;
	}
	public void setLastOrderTime(Date lastOrderTime) {
		this.lastOrderTime = lastOrderTime;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMerchName() {
		return merchName;
	}
	public void setMerchName(String merchName) {
		this.merchName = merchName;
	}
	public Date getBeginUpdateDate() {
		return beginUpdateDate;
	}
	public void setBeginUpdateDate(Date beginUpdateDate) {
		this.beginUpdateDate = beginUpdateDate;
	}
	public Date getEndUpdateDate() {
		return endUpdateDate;
	}
	public void setEndUpdateDate(Date endUpdateDate) {
		this.endUpdateDate = endUpdateDate;
	}
	public Date getBeginCreateDate() {
		return beginCreateDate;
	}
	public void setBeginCreateDate(Date beginCreateDate) {
		this.beginCreateDate = beginCreateDate;
	}
	public Date getEndCreateDate() {
		return endCreateDate;
	}
	public void setEndCreateDate(Date endCreateDate) {
		this.endCreateDate = endCreateDate;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getSettleRate() {
		return settleRate;
	}
	public void setSettleRate(BigDecimal settleRate) {
		this.settleRate = settleRate;
	}
	
}