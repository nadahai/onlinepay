package com.vc.onlinepay.utils.sand;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class JsonApiPayDTO implements Serializable {

	private static final long serialVersionUID = -3644382331360638220L;

	// 客戶訂單號
	private String orderNo;

	// 金額 分
	private String orderPrice;

	// 銀行編碼
	private String bankCode;

	// 版本V1
	private String version;

	// 異步通知地址
	private String noticeUrl;

	// 同步通知地址 可不填
	private String returnUrl;

	// 令牌
	private String token;

	// 商户号
	private String tradeNo;

	// 時間戳
	private String timestamp;

	// 秘鑰
	private String sign;

	public JsonApiPayDTO(Map<String, String> requestMap) {
		this.orderNo = requestMap.get("orderNo");
		this.orderPrice = requestMap.get("orderPrice");
		this.bankCode = requestMap.get("bankCode");
		this.version = requestMap.get("version");
		this.noticeUrl = requestMap.get("noticeUrl");
		this.returnUrl = requestMap.get("returnUrl");
		this.token = requestMap.get("token");
		this.tradeNo = requestMap.get("tradeNo");
		this.timestamp = requestMap.get("timestamp");
		this.sign = requestMap.get("sign");
	}

	public String validateBody() {
		if (StrKit.isBlank(orderNo)) {
			return "orderNo 不能為空";
		} else if (StrKit.isBlank(orderPrice)) {
			return "orderPrice 不能為空";
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
		} else if (Double.valueOf(orderPrice) < 3000.00) {
			return "orderPrice 不能少於3000分";
		} else if (StrKit.isBlank(timestamp)) {
			return "timestamp 不能為空";
		}
		return "";
	}

	public String sign() {
		String keyStr = "orderNo=" + orderNo + "&orderPrice=" + orderPrice + "&bankCode=" + bankCode + "&version="
				+ version + "&tradeNo=" + tradeNo + "&timestamp=" + timestamp + "&token=" + token;
		try {
			return MD5.md5(keyStr).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSign() {
		return this.sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(String orderPrice) {
		this.orderPrice = orderPrice;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
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

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

}
