package com.vc.onlinepay.persistent.entity.online;

import java.math.BigDecimal;
import java.util.Date;

public class VcOnlineContact {
	
	private BigDecimal id;
    private String memberId; //商户生成的用户ID
    private String orderId; // 商户上送订单号
    private String idType; // 证件类型
    private String idNo; // 证件号
    private String userName; // 姓名
    private String phone; // 手机号码
    private String cardNo; // 银行卡号
    private String cardType; // 卡类型
    private String expireDate; // 有效期
    private String cvn2; // 银行卡cvn2【贷记卡用】
    private String contractId; // 协议号
    private String bankName; // 银行名称
    private String bankAbbr; // 银行缩写
    private String extension; // 返回json
    private Long status; // 状态
    private Long merchantNo; // 商户号
    private Date createDate; // 创建时间
    private Long smsCount;   //发送短信的次数

    private Long merchType; //商户类型：1vc_a1_merch_info,2vc_online_merch
    private String notifyUrl; //下游异步回调地址
    private Long activateStatus; //签约状态 1 等待签约中 2 开通成功 3 开通失败
    private String cardId; //上游系统中的 银行卡id
    private Integer upperType;//是哪个上游1:九派500，2：摩宝 ，27:杉德
    private Long userVcId;//我们生成的用户ID
    private String bindSno;//上游返回申请绑卡流水号
    private Integer delFlag;  //删除标注

    public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public VcOnlineContact(){
    	
    }

	public VcOnlineContact(String memberId){
		this.memberId = memberId;
	}

    public VcOnlineContact(Long merchantNo, String memberId){
    	this.merchantNo = merchantNo;
    	this.memberId = memberId;
    }
    
    public VcOnlineContact(Long merchantNo, String memberId, String cardNo){
    	this.merchantNo = merchantNo;
    	this.memberId = memberId;
    	this.cardNo = cardNo;
    }

	public VcOnlineContact(Long merchantNo, String memberId, String cardNo, String idNo){
		this.merchantNo = merchantNo;
		this.memberId = memberId;
		this.cardNo = cardNo;
		this.idNo = idNo;
	}
    
	
	public VcOnlineContact(Long merchantNo, String memberId, String cardNo, String idNo, String phone, String cardType){
		this.merchantNo = merchantNo;
		this.memberId = memberId;
		this.cardNo = cardNo;
		this.idNo = idNo;
		this.phone = phone;
		this.cardType = cardType;
	}
	
	public VcOnlineContact(Long merchantNo, String memberId, String cardNo, String idNo, String phone, String cardType, Long payType){
		this.merchantNo = merchantNo;
		this.memberId = memberId;
		this.cardNo = cardNo;
		this.idNo = idNo;
		this.phone = phone;
		this.cardType = cardType;
		this.activateStatus = payType;
	}
    
	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	public String getCvn2() {
		return cvn2;
	}
	public void setCvn2(String cvn2) {
		this.cvn2 = cvn2;
	}
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankAbbr() {
		return bankAbbr;
	}
	public void setBankAbbr(String bankAbbr) {
		this.bankAbbr = bankAbbr;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public Long getStatus() {
		return status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}
	public Long getMerchantNo() {
		return merchantNo;
	}
	public void setMerchantNo(Long merchantNo) {
		this.merchantNo = merchantNo;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Long getSmsCount() {
		return smsCount;
	}
	public void setSmsCount(Long smsCount) {
		this.smsCount = smsCount;
	}

    public Long getMerchType() {
        return merchType;
    }

    public void setMerchType(Long merchType) {
        this.merchType = merchType;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public Long getActivateStatus() {
        return activateStatus;
    }

    public void setActivateStatus(Long activateStatus) {
        this.activateStatus = activateStatus;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
	public Integer getUpperType() {
		return upperType;
	}
	public void setUpperType(Integer upperType) {
		this.upperType = upperType;
	}
    public Long getUserVcId() {
        return userVcId;
    }
    public void setUserVcId(Long userVcId) {
        this.userVcId = userVcId;
    }
	public String getBindSno() {
		return bindSno;
	}
	public void setBindSno(String bindSno) {
		this.bindSno = bindSno;
	}
}
