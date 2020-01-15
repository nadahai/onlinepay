/**
 * @类名称:Basekey.java
 * @时间:2017年7月5日下午7:32:30
 * @作者:nada
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.enums;

 /**
 * @描述:业务响应码枚举
 * @作者:nada
 * @时间:2017年7月5日 下午7:32:30 
 */
public enum BaseResultEnum {
     code("code", "code"),
     msg("msg", "msg"),
     timestamp("timestamp", "timestamp"),
     method("reqCmd", "reqCmd"),
     sign("sign", "sign"),
    ;

    public static BaseResultEnum getEnum(String key) {
        for (BaseResultEnum e : values()) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No element matches " + key);
    }

    BaseResultEnum (String key, String value) {
        this.key = key;
        this.value = value;
    }
    private String key;
	private String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

