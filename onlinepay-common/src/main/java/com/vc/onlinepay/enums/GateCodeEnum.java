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
public enum GateCodeEnum {
    //网关响应码定义
    SUCCESS(10000, "success"),
    FAIL(10001, "failed"),
    ERROE(10002, "error"),
    EXCEPTION(10003, "exception"),
    ING(10004,"processing");

     public static  final int success = 10000;
     public static  final int failed = 10001;
     public static  final int error = 10002;
     public static  final int exception = 10003;
     public static  final int processing = 10004;


    public static GateCodeEnum getEnum(int key) {
        for (GateCodeEnum e : values()) {
            if ( e.getKey() == key) {
                return e;  
            }  
        }  
        throw new IllegalArgumentException("No element matches " + key);
    }  
	
    GateCodeEnum (int key, String value) {
        this.key = key;
        this.value = value;
    }
    private int key;
	private String value;
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

