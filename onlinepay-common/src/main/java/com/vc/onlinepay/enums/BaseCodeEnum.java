/**
 * @类名称:Basekey.java
 * @时间:2017年7月5日下午7:32:30
 * @作者:lihai 
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.enums;

 /**
 * @描述:业务响应码枚举
 * @作者:lihai 
 * @时间:2017年7月5日 下午7:32:30 
 */
public enum BaseCodeEnum {
     /**统一成功编码 */
	SUCCESS("10000", "成功"),
     /**统一失败编码 */
    FAIL("10001", "失败"),
     /**统一异常编码 */
    ERROR("10003", "异常"),
     /**统一处理中编码 */
    UNKNOWN("10004", "处理中"),
    ;

     /* 成功码 */
     public static final String SUCCESSS="10000";
     /* 失败码 */
     public static final String FAILED="10001";
     /* 异常码 */
     public static final String error="10003";
     /* 受理中 */
     public static final String UNKNOW="10004";
    
    public static BaseCodeEnum getEnum(String key) {
        for (BaseCodeEnum e : values()) {  
            if (e.getKey().equals(key)) {  
                return e;  
            }  
        }  
        throw new IllegalArgumentException("No element matches " + key);
    }  
	
    BaseCodeEnum(String key, String value) {
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

