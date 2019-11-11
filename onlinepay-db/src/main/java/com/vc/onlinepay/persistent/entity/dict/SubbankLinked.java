package com.vc.onlinepay.persistent.entity.dict;

public class SubbankLinked {
	
	private String province;
	private String city;
	private String bankCode;
	private String bankNo;
	private Long subBankId;
	private String provinceId;
	private String cityId;
	private String bankName;
	private String subBankName;

	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getBankCode() {
		return bankCode;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public Long getSubBankId() {
		return subBankId;
	}
	public void setSubBankId(Long subBankId) {
		this.subBankId = subBankId;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getSubBankName() {
		return subBankName;
	}

	public void setSubBankName(String subBankName) {
		this.subBankName = subBankName;
	}
}
