package com.vc.onlinepay.utils.sand;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class JsonDefrayQueryDTO implements Serializable {

	private static final long serialVersionUID = 6980240249612348144L;

	private String tradeOrderNo;
	private String timestamp;
	private String sign;
	private String tradeNo;
	private String version;
	private String status;
	private String remark;

	public JsonDefrayQueryDTO(Map<String, String> requestMap) {
		this.timestamp = requestMap.get("timestamp");
		this.tradeOrderNo = requestMap.get("tradeOrderNo");
		this.tradeNo = requestMap.get("tradeNo");
		this.version = requestMap.get("version");
	}

	public String sign(String token) {
		String keyStr = "tradeOrderNo=" + tradeOrderNo + "&version=" + version + "&tradeNo=" + tradeNo + "&timestamp="
				+ timestamp + "&token=" + token;
		try {
			String md5 = MD5.md5(keyStr).toLowerCase();
			return md5;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTradeOrderNo() {
		return tradeOrderNo;
	}

	public void setTradeOrderNo(String tradeOrderNo) {
		this.tradeOrderNo = tradeOrderNo;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
