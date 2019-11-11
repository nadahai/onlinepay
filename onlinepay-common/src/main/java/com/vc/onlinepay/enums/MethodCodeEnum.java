package com.vc.onlinepay.enums;

/**
 * @ClassName:  PayCommandEnum   
 * @Description: 接口编码枚举  
 * @author: lihai 
 * @date: 2018年4月16日 下午3:45:08  
 * @Copyright: 2018 www.guigu.com Inc. All rights reserved. 
 * 注意：本内容仅限于本信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public enum MethodCodeEnum {
	trade("req.trade.order","交易下单"),
	transfer("req.transfer.order","代付下单"),
	tradeQuery("req.query.trade","交易查询"),
	transferQuery("req.query.transfer","交易查询"),
	walletQuery("req.query.wallet","余额查询"),
	upperWalletQuery("req.query.wallet","上游余额查询"),
	upperTradeQuery("req.query.wallet","上游交易查询"),
	upperTransferQuery("req.query.wallet","上游代付查询");

	public static MethodCodeEnum getEnum(String value) {
        for(MethodCodeEnum e : MethodCodeEnum.values()) {
            if(e.getKey().equals(value)) {  
                return e;
            }  
        }
        throw new IllegalArgumentException("No element matches " + value);
    }  
	public static boolean isExist(String value) {  
        for(MethodCodeEnum e : MethodCodeEnum.values()) {
            if(e.getKey().equals(value)) {  
                return true;
            }  
        }
        return false;
    }  
	MethodCodeEnum (String key,String value) {
		this.key = key;
		this.value = value;
	}
	private String key;
	private String value;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
