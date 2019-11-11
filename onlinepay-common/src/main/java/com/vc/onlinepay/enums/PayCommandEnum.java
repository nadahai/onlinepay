package com.vc.onlinepay.enums;

/**
 * @ClassName:  PayCommandEnum   
 * @Description: 接口编码枚举  
 * @author: lihai 
 * @date: 2018年4月16日 下午3:45:08  
 * @Copyright: 2018 www.guigu.com Inc. All rights reserved. 
 * 注意：本内容仅限于本信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public enum PayCommandEnum {
	/**PayCommand枚举 */
	scan("scan101","扫码支付"),
	h5("h5102","h5支付"),
	union("union103","银联直冲支付"),
	quick("quick104","快捷支付"),
	gateway("gw105","网关支付"),
	replace("cmd201","代付接口"),
	query("cmd301","查询接口")
	;

	/**
	 * @描述:
	 * 1: 微信扫码
	 * 2: 支付宝扫码
	 * 3: QQ扫码
	 * 4: 京东钱包扫码
	 * 5: 微信公众号
	 * 6: 支付宝公众号
	 * 7: 快捷支付
	 * 8: 银联二维码
	 * 9: wap网关支付
	 * 10: 支付宝H5
	 * 11: QQ H5
	 * 12: 微信H5
	 * 13: 快捷直冲
	 * 15: 手机网关
	 * 16: 京东h5
	 * 17: 支付宝PC条形码
	 * @作者:nada
	 * @时间:2019/3/21
	 **/
	public static PayCommandEnum getPayTypeEnum(int payType) {
		switch (payType) {
			case 1:case 2:case 3:case 4:case 5:case 6:case 8:case 17:
				return PayCommandEnum.scan;
			case 10:case 11:case 12:case 16:
				return PayCommandEnum.h5;
			case 13:
				return PayCommandEnum.union;
			case 9:case 15:
				return PayCommandEnum.gateway;
			case 7:
				return PayCommandEnum.quick;
			default:
				return null;
		}
	}

	public static PayCommandEnum getEnum(String value) {  
        for(PayCommandEnum e : PayCommandEnum.values()) {  
            if(e.getKey().equals(value)) {  
                return e;
            }  
        }
        throw new IllegalArgumentException("No element matches " + value);
    }  
	public static boolean isExist(String value) {  
        for(PayCommandEnum e : PayCommandEnum.values()) {  
            if(e.getKey().equals(value)) {  
                return true;
            }  
        }
        return false;
    }  
	PayCommandEnum(String key,String value) {
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
