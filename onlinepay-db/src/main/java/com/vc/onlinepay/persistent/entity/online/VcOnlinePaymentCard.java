package com.vc.onlinepay.persistent.entity.online;

import java.math.BigDecimal;
import java.util.Date;

public class VcOnlinePaymentCard {
	
    private BigDecimal id;

    private Long merchId;

    private String orderNo;

    private String bankAccount;

    private String bankName;

    private String bankCard;

    private Integer status;

    private String remark;

    private Date createDate;

    private String remarks;
    
    public static VcOnlinePaymentCard  saveCard(String orderNo, String bankCard,String accountName,Long merchId,int status,String remark,String remarks) {
    	VcOnlinePaymentCard card = new VcOnlinePaymentCard(bankCard);
    	card.setMerchId(merchId);
    	card.setOrderNo(orderNo);
    	card.setBankAccount(accountName);
    	card.setBankName(bankCard);
    	card.setStatus(status);
    	card.setRemark(remark);
    	card.setRemarks(remarks);
    	return card;
	}

	public VcOnlinePaymentCard(){
    	super();
	}
    
    public VcOnlinePaymentCard(String bankCard) {
    	this.bankCard = bankCard;
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public Long getMerchId() {
		return merchId;
	}

	public void setMerchId(Long merchId) {
		this.merchId = merchId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

    
}