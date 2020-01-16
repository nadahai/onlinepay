/**
 * @类名称:ChannelSubNo1.java
 * @时间:2018年5月17日下午5:48:23
 * @版权:公司 Copyright (c) 2018
 */
package com.vc.onlinepay.persistent.entity.channel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @描述:TODO
 * @时间:2018年5月17日 下午5:48:23 
 */
public class ChannelSubNo implements Serializable {

    /**
     * @描述:TODO
     */
    private static final long serialVersionUID = -8611552559766702203L;
    private long id;
    private long channelId;
    private long merchId;
    private String upMerchNo;
    private String upMerchKey;
    private BigDecimal dayTraAmount;
    private BigDecimal traAmount;
    private BigDecimal actualAmount;
    private double minPrice;
    private double maxPrice;
    //自研支付宝供应商轮询算法
    private int loopRobin;
    //支付宝订单有效间期
    private long orderExpiredTime;
    //支付宝账号轮询间隔
    private long accountExpiredTime;
    private long lastOrderExpiredTime;
    private Date lastOrderTime;
    //权重
    private int weight;
    private String clientIp;
    private String orderNo;
    private String remarks;
    private String remark;
    private long channelSource;
    private int payType; //支持类型 0:全部 3:微信 1:支付宝

    private long type;//0 : 全部,1 : 个人转账,2 : 协议转账,3 : AA收款,4 : 转卡浮动,5 : 企业红包,6 : 转卡不浮动,7 : 好友转账 ,8 : 个人红包,10 : 主动收款,100 :当面付,200 :转账,400 :口碑 /
    private String name;
    private String userId;
    private String bankNo;
    private String bankMark;
    private String cardIdx;

    public ChannelSubNo () {
    }

    public ChannelSubNo (long channelId) {
        this.channelId = channelId;
    }

    public ChannelSubNo (long channelId, BigDecimal traAmount, int loopRobin) {
        this.channelId = channelId;
        this.traAmount = traAmount;
        this.loopRobin = loopRobin;
    }

    public ChannelSubNo (String orderNo, long channelId, long channelSource, BigDecimal traAmount, int loopRobin, String clientIp) {
        this.channelId = channelId;
        this.channelSource = channelSource;
        this.traAmount = traAmount;
        this.loopRobin = loopRobin;
        this.orderNo = orderNo;
        this.clientIp = clientIp;
    }

    public ChannelSubNo (long channelId, BigDecimal traAmount) {
        this.channelId = channelId;
        this.traAmount = traAmount;
    }

    public ChannelSubNo (long channelSource,Integer payType,BigDecimal traAmount) {
        this.channelSource = channelSource;
        this.traAmount = traAmount;
        this.payType = payType;
    }


    public ChannelSubNo (BigDecimal traAmount,long merchId) {
        this.merchId = merchId;
        this.traAmount = traAmount;
    }

    public ChannelSubNo (String upMerchNo) {
        this.upMerchNo = upMerchNo;
    }

    public ChannelSubNo (String upMerchNo, String upMerchKey) {
        this.upMerchNo = upMerchNo;
        this.upMerchKey = upMerchKey;
    }

    public ChannelSubNo (long channelId, String upMerchNo, BigDecimal actualAmount) {
        this.channelId = channelId;
        this.actualAmount = actualAmount;
        this.upMerchNo = upMerchNo;
    }
    public ChannelSubNo (long channelId,long merchId, String upMerchNo, BigDecimal actualAmount) {
        this.channelId = channelId;
        this.merchId = merchId;
        this.actualAmount = actualAmount;
        this.upMerchNo = upMerchNo;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankNo() {
        return bankNo;
    }

    public long getMerchId() {
        return merchId;
    }

    public void setMerchId(long merchId) {
        this.merchId = merchId;
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

    public String getCardIdx() {
        return cardIdx;
    }

    public void setCardIdx(String cardIdx) {
        this.cardIdx = cardIdx;
    }

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public long getChannelId () {
        return channelId;
    }

    public void setChannelId (long channelId) {
        this.channelId = channelId;
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

    public BigDecimal getDayTraAmount () {
        return dayTraAmount;
    }

    public void setDayTraAmount (BigDecimal dayTraAmount) {
        this.dayTraAmount = dayTraAmount;
    }

    public BigDecimal getTraAmount () {
        return traAmount;
    }

    public void setTraAmount (BigDecimal traAmount) {
        this.traAmount = traAmount;
    }

    public BigDecimal getActualAmount () {
        return actualAmount;
    }

    public void setActualAmount (BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
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

    public long getOrderExpiredTime () {
        return orderExpiredTime;
    }

    public void setOrderExpiredTime (long orderExpiredTime) {
        this.orderExpiredTime = orderExpiredTime;
    }

    public long getAccountExpiredTime () {
        return accountExpiredTime;
    }

    public void setAccountExpiredTime (long accountExpiredTime) {
        this.accountExpiredTime = accountExpiredTime;
    }

    public int getWeight () {
        return weight;
    }

    public void setWeight (int weight) {
        this.weight = weight;
    }

    public int getLoopRobin () {
        return loopRobin;
    }

    public void setLoopRobin (int loopRobin) {
        this.loopRobin = loopRobin;
    }

    public String getClientIp () {
        return clientIp;
    }

    public void setClientIp (String clientIp) {
        this.clientIp = clientIp;
    }

    public static long getSerialversionuid () {
        return serialVersionUID;
    }

    public String getOrderNo () {
        return orderNo;
    }

    public void setOrderNo (String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRemarks () {
        return remarks;
    }

    public void setRemarks (String remarks) {
        this.remarks = remarks;
    }

    public long getChannelSource () {
        return channelSource;
    }

    public void setChannelSource (long channelSource) {
        this.channelSource = channelSource;
    }

    public long getLastOrderExpiredTime () {
        return lastOrderExpiredTime;
    }

    public Date getLastOrderTime () {
        return lastOrderTime;
    }

    public void setLastOrderTime (Date lastOrderTime) {
        this.lastOrderTime = lastOrderTime;
    }

    public int getPayType () {
        return payType;
    }

    public void setPayType (int payType) {
        this.payType = payType;
    }

    public void setLastOrderExpiredTime (long lastOrderExpiredTime) {
        this.lastOrderExpiredTime = lastOrderExpiredTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

