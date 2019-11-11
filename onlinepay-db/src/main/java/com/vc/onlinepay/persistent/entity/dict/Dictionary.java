/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.dict;

/**
 * 字典表Entity
 * @author 李海
 * @version 2017-05-26
 */
public class Dictionary  {
	
	private String dKey;		// KEY标识
	private String strValue;		// 字符值
	private Long numberValue;		// 数字值
	private String describle;		// 描述
	private Long dType;		// 类型
	private String label;		// 字典标签
	private Long dSort;		// 字典排序
	private String beginCreateDate;		// 开始 创建时间
	private String endCreateDate;		// 结束 创建时间
	
	public Dictionary() {
		super();
	}
	
	public Dictionary(String dKey,Long numberValue,Long dType) {
	    this.dKey = dKey;
	    this.numberValue = numberValue;
	    this.dType=dType;
	}
	
	public Dictionary(String dKey,String strValue) {
        this.dKey = dKey;
        this.strValue = strValue;
    }

	public Dictionary(String dKey){
	    this.dKey = dKey;
	}
	
	public Dictionary(Long dType){
        this.dType = dType;
    }

	public String getDKey() {
		return dKey;
	}

	public void setDKey(String dKey) {
		this.dKey = dKey;
	}
	
	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	
	public Long getNumberValue() {
		return numberValue;
	}

	public void setNumberValue(Long numberValue) {
		this.numberValue = numberValue;
	}
	public String getDescrible() {
		return describle;
	}

	public void setDescrible(String describle) {
		this.describle = describle;
	}
	
	public Long getDType() {
		return dType;
	}

	public void setDType(Long dType) {
		this.dType = dType;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public Long getDSort() {
		return dSort;
	}

	public void setDSort(Long dSort) {
		this.dSort = dSort;
	}
	
	public String getBeginCreateDate() {
		return beginCreateDate;
	}

	public void setBeginCreateDate(String beginCreateDate) {
		this.beginCreateDate = beginCreateDate;
	}
	
	public String getEndCreateDate() {
		return endCreateDate;
	}

	public void setEndCreateDate(String endCreateDate) {
		this.endCreateDate = endCreateDate;
	}
		
}