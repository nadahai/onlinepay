/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.online;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.entity.channel.MerchChannel;

/**
 * @version 2017-06-30
 */
public class VcOnlineOrderDetail {
	private String orderNo;	
	private Long mangerId;	
	private Long agentId;	
	private BigDecimal agentRate;	
	private BigDecimal mangerRate;
	private String remarks;
	private BigDecimal traAmount;		// 交易金额
	private BigDecimal actualAmount;		// 实收金额
	
	 public static VcOnlineOrderDetail buildOrder(VcOnlineOrder onlineOrder,JSONObject reqData,MerchChannel merchChannel) throws IllegalArgumentException{
		 VcOnlineOrderDetail orderDetail = new VcOnlineOrderDetail();
		 orderDetail.setOrderNo(reqData.getString("vcOrderNo"));
		 orderDetail.setAgentId(merchChannel.getAgentId());
		 orderDetail.setMangerId(merchChannel.getMangerId());
		 orderDetail.setAgentRate(merchChannel.getAgentRate());
		 orderDetail.setMangerRate(merchChannel.getMangerRate());
		 orderDetail.setTraAmount(onlineOrder.getTraAmount());
		 orderDetail.setActualAmount(onlineOrder.getActualAmount());
		 return orderDetail;
    }
	public VcOnlineOrderDetail(){}

    public VcOnlineOrderDetail(String orderNo,BigDecimal realAmount){
	 	this.orderNo = orderNo;
	 	this.actualAmount = realAmount;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Long getMangerId() {
		return mangerId;
	}
	public void setMangerId(Long mangerId) {
		this.mangerId = mangerId;
	}
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public BigDecimal getAgentRate() {
		return agentRate;
	}
	public void setAgentRate(BigDecimal agentRate) {
		this.agentRate = agentRate;
	}
	public BigDecimal getMangerRate() {
		return mangerRate;
	}
	public void setMangerRate(BigDecimal mangerRate) {
		this.mangerRate = mangerRate;
	}
	public BigDecimal getTraAmount() {return traAmount;}
	public void setTraAmount(BigDecimal traAmount) {this.traAmount = traAmount;}
	public BigDecimal getActualAmount() {return actualAmount;}
	public void setActualAmount(BigDecimal actualAmount) {this.actualAmount = actualAmount;}
}