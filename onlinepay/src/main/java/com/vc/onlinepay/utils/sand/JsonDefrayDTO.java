package com.vc.onlinepay.utils.sand;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class JsonDefrayDTO implements Serializable {

	private static final long serialVersionUID = 6980240249612348144L;

	private String amt;
	private String tradeOrderNo;
	private String receiveName;
	private String openProvince;
	private String openCity;
	private String bankCode;
	private String bankBranchName;
	private String cardNo;
	private String acctNo;
	private String bankLinked;
	private String phone;
	private String bankClearNo;
	private String bankBranchNo;
	private String timestamp;
	private String sign;
	private String tradeNo;
	private String version;
	private String noticeUrl;
	private String token;
	private String cardAccountType;
	private String gwType;

	public JsonDefrayDTO(Map<String, String> requestMap) {
		this.amt = requestMap.get("amt");
		this.tradeOrderNo = requestMap.get("tradeOrderNo");
		this.receiveName = requestMap.get("receiveName");
		this.openProvince = requestMap.get("openProvince");
		this.openCity = requestMap.get("openCity");
		this.bankCode = requestMap.get("bankCode");
		this.bankBranchName = requestMap.get("bankBranchName");
		this.cardNo = requestMap.get("cardNo");
		this.acctNo = requestMap.get("acctNo");
		this.bankLinked = requestMap.get("bankLinked");
		this.phone = requestMap.get("phone");
		this.bankClearNo = requestMap.get("bankClearNo");
		this.bankBranchNo = requestMap.get("bankBranchNo");
		this.timestamp = requestMap.get("timestamp");
		this.sign = requestMap.get("sign");
		this.tradeNo = requestMap.get("tradeNo");
		this.version = requestMap.get("version");
		this.noticeUrl = requestMap.get("noticeUrl");
		this.token = requestMap.get("token");
		this.cardAccountType = requestMap.get("cardAccountType");
		this.gwType =  requestMap.get("gwType");
	}

	public String sign() {
		String keyStr = "tradeOrderNo=" + tradeOrderNo + "&amt=" + amt + "&receiveName=" + receiveName
				+ "&openProvince=" + openProvince + "&openCity=" + openCity + "&bankCode=" + bankCode + "&cardNo="
				+ cardNo + "&acctNo=" + acctNo + "&phone=" + phone + "&version=" + version + "&tradeNo=" + tradeNo
				+ "&timestamp=" + timestamp + "&token=" + token;
		System.out.println ("天成支付加密:"+keyStr);
		try {
			return MD5.md5(keyStr).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String validateBody() {
		if (StrKit.isBlank(tradeOrderNo)) {
			return "tradeOrderNo 不能為空";
		} else if (StrKit.isBlank(receiveName)) {
			return "receiveName 不能為空";
		} else if (StrKit.isBlank(openProvince)) {
			return "openProvince 不能為空";
		} else if (StrKit.isBlank(openCity)) {
			return "openCity 不能為空";
		} else if (StrKit.isBlank(bankBranchName)) {
			return "bankBranchName 不能為空";
		} else if (StrKit.isBlank(cardNo)) {
			return "cardNo 不能為空";
		} else if (StrKit.isBlank(acctNo)) {
			return "acctNo 不能為空";
		} else if (StrKit.isBlank(phone)) {
			return "phone 不能為空";
		} else if (StrKit.isBlank(bankCode)) {
			return "bankCode 不能為空";
		} else if (bankCode(bankCode) == null) {
			return "bankCode 錯誤,請輸入正確值";
		} else if (StrKit.isBlank(version)) {
			return "version 不能為空";
		} else if (StrKit.isBlank(noticeUrl)) {
			return "noticeUrl 不能為空";
		} else if (StrKit.isBlank(token)) {
			return "token 不能為空";
		} else if (StrKit.isBlank(tradeNo)) {
			return "tradeNo 不能為空";
		} else if (StrKit.isBlank(sign)) {
			return "sign 不能為空";
		} else if (Double.valueOf(amt) < 1.00) {
			return "amt 不能少於1元";
		} else if (StrKit.isBlank(timestamp)) {
			return "timestamp 不能為空";
		}
		return "";
	}

	public Boolean bankCode(String code) {
		HashMap<String, Boolean> m = new HashMap<String, Boolean>();
		// 农业银行
		m.put("ABC", true);
		// 华夏银行
		m.put("HXB", true);
		// 交通银行
		m.put("BOCO", true);
		// 广发银行
		m.put("CGB", true);
		// 中国邮政银行
		m.put("POST", true);
		// 中国银行
		m.put("BOC", true);
		// 兴业银行
		m.put("CIB", true);
		// 中信银行
		m.put("ECITIC", true);
		// 招商银行
		m.put("CMBCHINA", true);
		// 光大银行
		m.put("CEB", true);
		// 建设银行
		m.put("CCB", true);
		// 平安银行
		m.put("PINGANBANK", true);
		// 浦发银行
		m.put("SPDP", true);
		// 北京银行
		m.put("BCCB", true);
		// 民生银行
		m.put("CMBC", true);
		// 上海银行
		m.put("SHB", true);
		// 工商银行
		m.put("ICBC", true);
		return m.get(code);
	}

	public String getCardAccountType() {
		return cardAccountType;
	}

	public void setCardAccountType(String cardAccountType) {
		this.cardAccountType = cardAccountType;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getNoticeUrl() {
		return noticeUrl;
	}

	public void setNoticeUrl(String noticeUrl) {
		this.noticeUrl = noticeUrl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getTradeOrderNo() {
		return tradeOrderNo;
	}

	public void setTradeOrderNo(String tradeOrderNo) {
		this.tradeOrderNo = tradeOrderNo;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getOpenProvince() {
		return openProvince;
	}

	public void setOpenProvince(String openProvince) {
		this.openProvince = openProvince;
	}

	public String getOpenCity() {
		return openCity;
	}

	public void setOpenCity(String openCity) {
		this.openCity = openCity;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankBranchName() {
		return bankBranchName;
	}

	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public String getBankLinked() {
		return bankLinked;
	}

	public void setBankLinked(String bankLinked) {
		this.bankLinked = bankLinked;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBankClearNo() {
		return bankClearNo;
	}

	public void setBankClearNo(String bankClearNo) {
		this.bankClearNo = bankClearNo;
	}

	public String getBankBranchNo() {
		return bankBranchNo;
	}

	public void setBankBranchNo(String bankBranchNo) {
		this.bankBranchNo = bankBranchNo;
	}

	public String getGwType() {
		return gwType;
	}

	public void setGwType(String gwType) {
		this.gwType = gwType;
	}
	

}
