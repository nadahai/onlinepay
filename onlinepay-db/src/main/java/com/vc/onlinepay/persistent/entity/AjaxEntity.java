package com.vc.onlinepay.persistent.entity;

public class AjaxEntity {
	
	private String code;
    private String message;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public AjaxEntity(String code, String message) {
		this.code = code;
		this.message = message;
	}
	public static AjaxEntity customReturnRes(String code,String message){
        return new AjaxEntity(code, message);
    }
}
