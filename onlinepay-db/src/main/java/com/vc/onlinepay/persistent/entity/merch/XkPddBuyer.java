/**
 * @类名称:ChannelSubNo1.java
 * @时间:2018年5月17日下午5:48:23
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.persistent.entity.merch;

import java.io.Serializable;
import java.util.Date;

/**
 * @描述:TODO
 * @时间:2019年4月17日 上午11:48:23 
 */
public class XkPddBuyer implements Serializable{
	/**
	 * @描述:TODO
	 */
	private static final long serialVersionUID = -8611552559766702222L;
	private long id;
	private long parentId;
	private String merchName;
	private String loginName;
	private String accessToken;
	private String addressId;
	private double dayTradeAmount;
	private double totalTradeAmount;
	private String extendInfo;
	private int status;
	private String remarks;
	private String createBy;
	private Date createDate;
	private String updateBy;
	private Date updateDate;
	private int delFlag;

    public XkPddBuyer() {
    }

    public XkPddBuyer(long id, double dayTradeAmount) {
        this.id = id;
        this.dayTradeAmount = dayTradeAmount;
    }

	public XkPddBuyer(String loginName) {
		this.loginName = loginName;
	}

    public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public String getMerchName() {
		return merchName;
	}
	public void setMerchName(String merchName) {
		this.merchName = merchName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public double getDayTradeAmount() {
		return dayTradeAmount;
	}

	public void setDayTradeAmount(double dayTradeAmount) {
		this.dayTradeAmount = dayTradeAmount;
	}

	public double getTotalTradeAmount() {
		return totalTradeAmount;
	}

	public void setTotalTradeAmount(double totalTradeAmount) {
		this.totalTradeAmount = totalTradeAmount;
	}

	public String getExtendInfo() {
		return extendInfo;
	}
	public void setExtendInfo(String extendInfo) {
		this.extendInfo = extendInfo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public int getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}

}

