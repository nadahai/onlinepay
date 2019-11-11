package com.vc.onlinepay.persistent.entity.dict;

public class Bank {
	
	private Long bankId;
	private String bankName;
	private String bankCode;
	private String sxBankNo;
	public Long getBankId() {
		return bankId;
	}
	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getSxBankNo() {
		return sxBankNo;
	}
	public void setSxBankNo(String sxBankNo) {
		this.sxBankNo = sxBankNo;
	}

}
