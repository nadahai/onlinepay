/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.merch;

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商账号管理Entity
 *
 * @author 李海
 * @version 2018-09-12
 */
public class SupplierSubno implements Serializable {

    /**
     * @描述:TODO
     */
    private static final long serialVersionUID = -1088339858829165311L;
    private long id;
    private int merchId; // 商户ID
    private int status; // 子商户号状态 1:正常 2:禁用 3:失效
    private String upMerchNo; // 子商户号
    private String upMerchKey; // 子商户号秘钥
    private double dayQuotaAmount; // 子商户日限额
    private double quotaAmount; // 子商户总限额
    private double dayTraAmount; // 子商户日交易额
    private double traAmount; // 子商户总交易额
    private int seq; // 排序
    private String remark; // 备注
    private String remarks; // 备注
    private double minPrice; // 单笔最小
    private double maxPrice; // 单笔最大
    private Date lastOrderTime; // 最后下单时间
    private Date lastDay;
    private Date beginUpdateDate; // 开始 更新时间
    private Date endUpdateDate; // 结束 更新时间
    private Date beginCreateDate; // 开始 注册时间
    private Date endCreateDate; // 结束 注册时间
    private BigDecimal actualAmount; //
    private Data updateDate;
    // 权重
    private int weight;
    private String appId; //应用ID
    private String privateKey; //私钥
    private String publicKey; //公钥
    private String userId; //
    private double settleAmount; //
    private int payNum;
    private String name;
    private String bankNo;
    private String bankMark;
    private long channelId;
    private int type;
    private int channelSource;
    private int lastOrderExpiredTime;
    private String cardIdx;
    // 归集账号
    private Integer settleSubNoId;

    public static SupplierSubno getSupplierSubno (int channelSource) {
        SupplierSubno supplierSubno = new SupplierSubno();
        supplierSubno.setChannelSource (channelSource);
        return  supplierSubno;
    }

    public SupplierSubno (String upMerchNo, String upMerchKey, BigDecimal actualAmount) {
        this.upMerchKey = upMerchKey;
        this.actualAmount = actualAmount;
        this.upMerchNo = upMerchNo;
    }

    public SupplierSubno (String upMerchNo) {
        this.upMerchNo = upMerchNo;
    }

    public SupplierSubno (String upMerchNo, String upMerchKey) {
        this.upMerchNo = upMerchNo;
        this.upMerchKey = upMerchKey;
    }

    public SupplierSubno (String upMerchKey, long channelId) {
        this.upMerchKey = upMerchKey;
        this.channelId = channelId;
    }

    public SupplierSubno () {
        super ();
    }

    public SupplierSubno (String upMerchNo,BigDecimal actualAmount) {
        this.upMerchNo = upMerchNo;
        this.actualAmount = actualAmount;
    }

    public int getMerchId () {
        return merchId;
    }

    public void setMerchId (int merchId) {
        this.merchId = merchId;
    }

    public int getStatus () {
        return status;
    }

    public void setStatus (int status) {
        this.status = status;
    }

    public String getUpMerchNo () {
        return upMerchNo;
    }

    public void setUpMerchNo (String upMerchNo) {
        this.upMerchNo = upMerchNo;
    }

    public String getUpMerchKey () {
        return upMerchKey;
    }

    public void setUpMerchKey (String upMerchKey) {
        this.upMerchKey = upMerchKey;
    }

    public double getDayQuotaAmount () {
        return dayQuotaAmount;
    }

    public void setDayQuotaAmount (double dayQuotaAmount) {
        this.dayQuotaAmount = dayQuotaAmount;
    }

    public double getQuotaAmount () {
        return quotaAmount;
    }

    public void setQuotaAmount (double quotaAmount) {
        this.quotaAmount = quotaAmount;
    }

    public double getDayTraAmount () {
        return dayTraAmount;
    }

    public void setDayTraAmount (double dayTraAmount) {
        this.dayTraAmount = dayTraAmount;
    }

    public double getTraAmount () {
        return traAmount;
    }

    public void setTraAmount (double traAmount) {
        this.traAmount = traAmount;
    }

    public int getSeq () {
        return seq;
    }

    public void setSeq (int seq) {
        this.seq = seq;
    }

    public String getRemark () {
        return remark;
    }

    public void setRemark (String remark) {
        this.remark = remark;
    }

    public double getMinPrice () {
        return minPrice;
    }

    public void setMinPrice (double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice () {
        return maxPrice;
    }

    public void setMaxPrice (double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Date getLastOrderTime () {
        return lastOrderTime;
    }

    public void setLastOrderTime (Date lastOrderTime) {
        this.lastOrderTime = lastOrderTime;
    }

    public Date getLastDay () {
        return lastDay;
    }

    public void setLastDay (Date lastDay) {
        this.lastDay = lastDay;
    }

    public Date getBeginUpdateDate () {
        return beginUpdateDate;
    }

    public void setBeginUpdateDate (Date beginUpdateDate) {
        this.beginUpdateDate = beginUpdateDate;
    }

    public Date getEndUpdateDate () {
        return endUpdateDate;
    }

    public void setEndUpdateDate (Date endUpdateDate) {
        this.endUpdateDate = endUpdateDate;
    }

    public Date getBeginCreateDate () {
        return beginCreateDate;
    }

    public void setBeginCreateDate (Date beginCreateDate) {
        this.beginCreateDate = beginCreateDate;
    }

    public Date getEndCreateDate () {
        return endCreateDate;
    }

    public void setEndCreateDate (Date endCreateDate) {
        this.endCreateDate = endCreateDate;
    }

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public int getWeight () {
        return weight;
    }

    public void setWeight (int weight) {
        this.weight = weight;
    }

    public BigDecimal getActualAmount () {
        return actualAmount;
    }

    public void setActualAmount (BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public static long getSerialversionuid () {
        return serialVersionUID;
    }

    public Data getUpdateDate () {
        return updateDate;
    }

    public void setUpdateDate (Data updateDate) {
        this.updateDate = updateDate;
    }

    public String getAppId () {
        return appId;
    }

    public void setAppId (String appId) {
        this.appId = appId;
    }

    public String getPrivateKey () {
        return privateKey;
    }

    public void setPrivateKey (String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey () {
        return publicKey;
    }

    public void setPublicKey (String publicKey) {
        this.publicKey = publicKey;
    }

    public String getUserId () {
        return userId;
    }

    public void setUserId (String userId) {
        this.userId = userId;
    }

    public double getSettleAmount () {
        return settleAmount;
    }

    public void setSettleAmount (double settleAmount) {
        this.settleAmount = settleAmount;
    }

    public int getPayNum () {
        return payNum;
    }

    public void setPayNum (int payNum) {
        this.payNum = payNum;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getBankMark() {
        return bankMark;
    }

    public void setBankMark(String bankMark) {
        this.bankMark = bankMark;
    }

    public Integer getSettleSubNoId () {
        return settleSubNoId;
    }

    public void setSettleSubNoId (Integer settleSubNoId) {
        this.settleSubNoId = settleSubNoId;
    }

    public int getType () {
        return type;
    }

    public void setType (int type) {
        this.type = type;
    }

    public long getChannelId () {
        return channelId;
    }

    public void setChannelId (long channelId) {
        this.channelId = channelId;
    }

    public int getLastOrderExpiredTime () {
        return lastOrderExpiredTime;
    }

    public void setLastOrderExpiredTime (int lastOrderExpiredTime) {
        this.lastOrderExpiredTime = lastOrderExpiredTime;
    }

	public int getChannelSource() {
		return channelSource;
	}

	public void setChannelSource(int channelSource) {
		this.channelSource = channelSource;
	}

    public String getCardIdx () {
        return cardIdx;
    }

    public String getRemarks () {
        return remarks;
    }

    public void setRemarks (String remarks) {
        this.remarks = remarks;
    }

    public void setCardIdx (String cardIdx) {
        this.cardIdx = cardIdx;
    }
}