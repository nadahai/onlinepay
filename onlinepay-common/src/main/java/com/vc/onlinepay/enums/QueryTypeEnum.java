/**
 * @类名称:BaseCode.java
 * @时间:2017年7月5日下午7:32:30
 * @作者:lihai 
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.enums;

/**
 * @描述:请求响应状态码枚举
 * @作者:lihai 
 * @时间:2017年7月5日 下午7:32:30 
 */
public enum QueryTypeEnum {
     trade("trade","交易订单查询"),
    transfer("transfer","代付订单查询"),
    ;
    public static QueryTypeEnum getEnum(String key) {
        for(QueryTypeEnum e : QueryTypeEnum.values()) {
            if(e.getKey().equals(key)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No element matches " + key);
    }

    QueryTypeEnum (String key, String value) {
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

