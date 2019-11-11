/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.channel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 微信账号Entity
 * @author nada
 * @version 2018-12-14
 */
public class ChannelWxno {
	
	private static final long serialVersionUID = 1L;
	private Long status;		// 状态 1:正常 2:禁用 3:失效
	private String upMerchNo;		// 子账号
	private String upMerchKey;		// 秘钥
	private Double dayQuotaAmount;		// 日限额
	private Double quotaAmount;		// 总限额
	private Double dayTraAmount =0d;		// 日交易额
	private Double traAmount = 0d;		// 总交易额
	private Long minPrice;		// 单笔最小
	private Long maxPrice;		// 单笔最大
	private String traStartTime;		// 开始时间8:30 全时00:00
	private String traEndTime;		// 结束时间22:30全时00:00
	private String name;		// 账号名称
	private String bankNo;		// 银行卡号
	private Integer weight = 1;		// 权重
	private Double succRate;		// 当日成功率
	private Integer seq;		// 排序
	private Date lastOrderTime;		// 最后下单时间
	private String remark;		// 备注
	private Date lastDay;		// 最后修改日
	private Long channelId;
	private BigDecimal actualAmount;
	private int lastOrderExpiredTime;

	public ChannelWxno () {
		super();
	}

	public ChannelWxno (String upMerchNo, String upMerchKey, BigDecimal actualAmount) {
		this.upMerchKey = upMerchKey;
		this.actualAmount = actualAmount;
		this.upMerchNo = upMerchNo;
	}

	public ChannelWxno(Long channelId,Double traAmount){
		this.channelId = channelId;
		this.traAmount = traAmount;
	}

	public ChannelWxno(Long channelId,Double traAmount,String upMerchKey){
		this.channelId = channelId;
		this.traAmount = traAmount;
		this.upMerchKey = upMerchKey;
	}

	public ChannelWxno(String upMerchNo){
		this.upMerchNo = upMerchNo;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
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
	
	public Double getDayQuotaAmount() {
		return dayQuotaAmount;
	}

	public void setDayQuotaAmount(Double dayQuotaAmount) {
		this.dayQuotaAmount = dayQuotaAmount;
	}
	
	public Double getQuotaAmount() {
		return quotaAmount;
	}

	public void setQuotaAmount(Double quotaAmount) {
		this.quotaAmount = quotaAmount;
	}
	
	public Double getDayTraAmount() {
		return dayTraAmount;
	}

	public void setDayTraAmount(Double dayTraAmount) {
		this.dayTraAmount = dayTraAmount;
	}
	
	public Double getTraAmount() {
		return traAmount;
	}

	public void setTraAmount(Double traAmount) {
		this.traAmount = traAmount;
	}
	
	public Long getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Long minPrice) {
		this.minPrice = minPrice;
	}
	
	public Long getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Long maxPrice) {
		this.maxPrice = maxPrice;
	}
	
	public String getTraStartTime() {
		return traStartTime;
	}

	public void setTraStartTime(String traStartTime) {
		this.traStartTime = traStartTime;
	}
	
	public String getTraEndTime() {
		return traEndTime;
	}

	public void setTraEndTime(String traEndTime) {
		this.traEndTime = traEndTime;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	public Double getSuccRate() {
		return succRate;
	}

	public void setSuccRate(Double succRate) {
		this.succRate = succRate;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public Date getLastOrderTime() {
		return lastOrderTime;
	}

	public void setLastOrderTime(Date lastOrderTime) {
		this.lastOrderTime = lastOrderTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public Date getLastDay() {
		return lastDay;
	}

	public void setLastDay(Date lastDay) {
		this.lastDay = lastDay;
	}

	public Long getChannelId () {
		return channelId;
	}

	public void setChannelId (Long channelId) {
		this.channelId = channelId;
	}

	public BigDecimal getActualAmount () {
		return actualAmount;
	}

	public void setActualAmount (BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public int getLastOrderExpiredTime () {
		return lastOrderExpiredTime;
	}

	public void setLastOrderExpiredTime (int lastOrderExpiredTime) {
		this.lastOrderExpiredTime = lastOrderExpiredTime;
	}
}