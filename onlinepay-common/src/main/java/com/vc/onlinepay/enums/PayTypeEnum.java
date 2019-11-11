package com.vc.onlinepay.enums;

public enum PayTypeEnum {
    /**系统内部PayType枚举 */
	WeChatScan(1,"微信扫码支付"),
	AlipayScan(2,"支付宝扫码支付"),
    QQWalletScan(3,"QQ钱包扫码支付"),
    JDWalletScan(4,"京东钱包扫码支付"),
    WeChatPublic(5,"微信公众号支付"),
    AlipayWindow(6,"支付宝公众号支付"),
    QuickPay(7,"快捷支付"),
    UnionScan(8,"银联二维码支付"),
    GateWayWap(9,"网关支付"),
    AlipayH5(10,"支付宝H5支付"),
    QQWalletH5(11,"QQH5支付"),
    WeChatH5(12,"微信H5支付"),
    UnionFast(13,"银联直冲支付"),
    QuickFast(14,"快捷直冲支付"),
    GateWayMobile (15,"手机网关支付"),
    JDh5(16,"京东h5支付"),
    ;

	public static PayTypeEnum getEnum(int key) {  
        for(PayTypeEnum e : PayTypeEnum.values()) {  
            if(e.key == key) {  
                return e;
            }  
        }  
        throw new IllegalArgumentException("No element matches " + key);
    }
	public static boolean isExist(int key) {  
        for(PayTypeEnum e : PayTypeEnum.values()) {  
            if(e.key == key) {  
                return true;
            }  
        }  
        return false;
    }
	 PayTypeEnum(int key, String value) {
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
