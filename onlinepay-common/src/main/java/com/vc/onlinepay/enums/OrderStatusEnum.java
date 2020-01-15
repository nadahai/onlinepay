/**
 * @类名称:BaseCode.java
 * @时间:2017年7月5日下午7:32:30
 * @作者:nada
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.enums;

 /**
 * @描述:请求响应状态码枚举
 * @作者:nada
 * @时间:2017年7月5日 下午7:32:30 
 */
public enum OrderStatusEnum {
     /**交易状态枚举 */
    ORDER_ok(1,"下单成功"),
    ORDER_FAILED(2,"下单失败"),
    ORDER_ING(3,"下单中"),
    PAY_OK(4,"支付成功"),
    PAY_FAILED(5,"支付失败"),
    FAILED(6,"状态不详"),
    DRAWBACK(9,"交易退款"),
    ;
	
	public static OrderStatusEnum getEnum(int key) {  
        for(OrderStatusEnum e : OrderStatusEnum.values()) {  
            if(e.getKey() == key) {  
                return e;
            }  
        }  
        throw new IllegalArgumentException("No element matches " + key);
    }  
	
    OrderStatusEnum(int key, String value) {
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

