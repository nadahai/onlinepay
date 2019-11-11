/**
 * @类名称:BaseCode.java
 * @时间:2017年7月5日下午7:32:30
 * @作者:lihai 
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.enums;


/**
 * @描述:代付响应状态码枚举
 * @作者:lihai 
 * @时间:2017年7月5日 下午7:32:30 
 */
public enum TransferStatusEnum {
    ORDER_ok(1,"代付成功"),
    REPLACE_ING(2,"代付中"),
    REPLACE_FAILED(3,"代付失败"),
    FAILEDN(6,"状态不详"),
    DRAWBACK(9,"代付退汇"),
    ;

	public static TransferStatusEnum getEnum(int value) {
        for(TransferStatusEnum e : TransferStatusEnum.values()) {
            if(e.getKey() == value) {  
                return e;
            }  
        }  
        throw new IllegalArgumentException("No element matches " + value);
    }

     public static boolean isExist(int value) {
         for(TransferStatusEnum e : TransferStatusEnum.values()) {
             if(e.getKey() == value) {
                 return true;
             }
         }
         return false;
     }

    private TransferStatusEnum (int key,String value) {
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

