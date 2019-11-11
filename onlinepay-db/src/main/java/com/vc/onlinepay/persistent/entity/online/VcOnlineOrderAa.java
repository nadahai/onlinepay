/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.online;

import java.math.BigDecimal;
import java.util.List;

/**
 * aa订单收款Entity
 * @author nada
 * @version 2019-04-24
 */
public class VcOnlineOrderAa {

	private BigDecimal id;
	private String upMerchNo;		// 收款账号
	private String tradeno;		// 获取订单号
	private String token;		// token值
	private int amount;		// 单笔金额
	private int totalAmount;		// 总金额
	private int showTimesTotal;		// 总次数
	private int status;		// 状态 1下单成功 2下单失败 3下单中 4支付成功 5支付失败 6:处理中 7:代付中 8:代付失败 9交易退款
	private String zhOrder;		// 中文订单号

	private int type; // 类型1:自己 2:好友用户
	private String orderNo;		// 订单号
	private String userid;		// 用户ID
	private String aaOrderNo;		//AA订单号
	private String remark;		// 描述
	private String remarks;		// 描述
	private List<String> userIds;		// 描述
	
	public VcOnlineOrderAa() {
		super();
	}

	public VcOnlineOrderAa(int amount) {
		this.amount = amount;
	}

	public VcOnlineOrderAa(String aaOrderNo, String orderNo) {
		this.aaOrderNo = aaOrderNo;
		this.orderNo = orderNo;
	}

	public VcOnlineOrderAa(String upMerchNo, String tradeno, String userid, int status) {
		this.upMerchNo = upMerchNo;
		this.tradeno = tradeno;
		this.userid = userid;
		this.status=status;
	}

	public VcOnlineOrderAa(String upMerchNo, int amount) {
		this.upMerchNo = upMerchNo;
		this.amount = amount;
	}

	public static VcOnlineOrderAa buildZhOrder(String zhOrder, String userid, String upMerchNo) {
		VcOnlineOrderAa vcaa = new VcOnlineOrderAa();
		vcaa.setZhOrder(zhOrder);
		vcaa.setToken(upMerchNo);
		vcaa.setUserid(userid);
		vcaa.setUpMerchNo(upMerchNo);
		return vcaa;
	}

	public static VcOnlineOrderAa buildAAAATradNo(String tradeno, int amount, List<String> userIds) {
		VcOnlineOrderAa vcaa = new VcOnlineOrderAa();
		vcaa.setTradeno (tradeno);
		vcaa.setAmount (amount);
		vcaa.setUserIds(userIds);
		return vcaa;
	}

	public static VcOnlineOrderAa buildAAOneAATradNo(String tradeno, int status) {
		VcOnlineOrderAa vcaa = new VcOnlineOrderAa();
		vcaa.setTradeno (tradeno);
		vcaa.setStatus (status);
		return vcaa;
	}


	public static VcOnlineOrderAa buildAA(VcOnlineOrderMade vcOnlineOrder) {
		VcOnlineOrderAa vcaa = new VcOnlineOrderAa();
		vcaa.setUpMerchNo (vcOnlineOrder.getUpMerchNo ());
		return vcaa;
	}


	public String getUpMerchNo() {
		return upMerchNo;
	}

	public void setUpMerchNo(String upMerchNo) {
		this.upMerchNo = upMerchNo;
	}
	
	public String getTradeno() {
		return tradeno;
	}

	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	
	public String getZhOrder() {
		return zhOrder;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public void setZhOrder(String zhOrder) {
		this.zhOrder = zhOrder;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getAaOrderNo() {
		return aaOrderNo;
	}

	public void setAaOrderNo(String aaOrderNo) {
		this.aaOrderNo = aaOrderNo;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getId () {
		return id;
	}

	public void setId (BigDecimal id) {
		this.id = id;
	}

	public int getAmount () {
		return amount;
	}

	public void setAmount (int amount) {
		this.amount = amount;
	}

	public int getTotalAmount () {
		return totalAmount;
	}

	public void setTotalAmount (int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getShowTimesTotal () {
		return showTimesTotal;
	}

	public void setShowTimesTotal (int showTimesTotal) {
		this.showTimesTotal = showTimesTotal;
	}

	public int getType () {
		return type;
	}

	public void setType (int type) {
		this.type = type;
	}

	public String getRemarks () {
		return remarks;
	}

	public void setRemarks (String remarks) {
		this.remarks = remarks;
	}
}