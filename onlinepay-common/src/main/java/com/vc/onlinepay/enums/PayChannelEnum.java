package com.vc.onlinepay.enums;

public enum PayChannelEnum {
    /**支付通道枚举 */
	H5("h5","H5支付"),
    SCAN("sc","扫码支付"),
    GATEWAY("gw","网关支付"),
    QUICK("qk","快捷支付"),
    UNION("union","快捷支付"),
    YL("yl","银联直冲支付"),
    ;
	
	public static PayChannelEnum getEnum(String key) {  
        for(PayChannelEnum e : PayChannelEnum.values()) {  
            if(e.getKey().equals(key)) {
                return e;
            }  
        }  
        throw new IllegalArgumentException("No element matches " + key);
    }
	public static boolean isExist(String key) {  
        for(PayChannelEnum e : PayChannelEnum.values()) {  
            if(e.getKey().equals(key)) {
                return true;
            }  
        }  
        return false;
    }
    private String key;
    private String value;
    PayChannelEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
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
