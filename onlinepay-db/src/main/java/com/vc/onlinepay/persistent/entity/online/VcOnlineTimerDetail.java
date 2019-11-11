package com.vc.onlinepay.persistent.entity.online;

import java.math.BigDecimal;
import java.util.Date;

public class VcOnlineTimerDetail {
	
    private BigDecimal id;

    private Long batchNo;

    private BigDecimal traTotalAmount;

    private BigDecimal disabledTotalAmount;

    private BigDecimal cashdTotalAmount;

    private BigDecimal lastCashAmount;

    private BigDecimal minCashAmount;

    private BigDecimal maxCashAmount;

    private BigDecimal waitTotalAmount;

    private BigDecimal usableTotalAmount;

    private Short status;

    private String orderIds;

    private BigDecimal totalAmount;

    private Long totalCount;

    private String remark;

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

    private String remarks;

    private Short delFlag;

    public BigDecimal getId() {
        return id;
    }

    public VcOnlineTimerDetail setId(BigDecimal id) {
        this.id = id;
        return this;
    }

    public Long getBatchNo() {
        return batchNo;
    }

    public VcOnlineTimerDetail setBatchNo(Long batchNo) {
        this.batchNo = batchNo;
        return this;
    }

    public BigDecimal getTraTotalAmount() {
        return traTotalAmount;
    }

    public VcOnlineTimerDetail setTraTotalAmount(BigDecimal traTotalAmount) {
        this.traTotalAmount = traTotalAmount;
        return this;
    }

    public BigDecimal getDisabledTotalAmount() {
        return disabledTotalAmount;
    }

    public VcOnlineTimerDetail setDisabledTotalAmount(BigDecimal disabledTotalAmount) {
        this.disabledTotalAmount = disabledTotalAmount;
        return this;
    }

    public BigDecimal getCashdTotalAmount() {
        return cashdTotalAmount;
    }

    public VcOnlineTimerDetail setCashdTotalAmount(BigDecimal cashdTotalAmount) {
        this.cashdTotalAmount = cashdTotalAmount;
        return this;
    }

    public BigDecimal getLastCashAmount() {
        return lastCashAmount;
    }

    public VcOnlineTimerDetail setLastCashAmount(BigDecimal lastCashAmount) {
        this.lastCashAmount = lastCashAmount;
        return this;
    }

    public BigDecimal getMinCashAmount() {
        return minCashAmount;
    }

    public VcOnlineTimerDetail setMinCashAmount(BigDecimal minCashAmount) {
        this.minCashAmount = minCashAmount;
        return this;
    }

    public BigDecimal getMaxCashAmount() {
        return maxCashAmount;
    }

    public VcOnlineTimerDetail setMaxCashAmount(BigDecimal maxCashAmount) {
        this.maxCashAmount = maxCashAmount;
        return this;
    }

    public BigDecimal getWaitTotalAmount() {
        return waitTotalAmount;
    }

    public VcOnlineTimerDetail setWaitTotalAmount(BigDecimal waitTotalAmount) {
        this.waitTotalAmount = waitTotalAmount;
        return this;
    }

    public BigDecimal getUsableTotalAmount() {
        return usableTotalAmount;
    }

    public VcOnlineTimerDetail setUsableTotalAmount(BigDecimal usableTotalAmount) {
        this.usableTotalAmount = usableTotalAmount;
        return this;
    }

    public Short getStatus() {
        return status;
    }

    public VcOnlineTimerDetail setStatus(Short status) {
        this.status = status;
        return this;
    }

    public String getOrderIds() {
        return orderIds;
    }

    public VcOnlineTimerDetail setOrderIds(String orderIds) {
        this.orderIds = orderIds == null ? null : orderIds.trim();
        return this;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public VcOnlineTimerDetail setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public VcOnlineTimerDetail setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public VcOnlineTimerDetail setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public VcOnlineTimerDetail setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public VcOnlineTimerDetail setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public VcOnlineTimerDetail setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
        return this;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public VcOnlineTimerDetail setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
        return this;
    }

    public String getRemarks() {
        return remarks;
    }

    public VcOnlineTimerDetail setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
        return this;
    }

    public Short getDelFlag() {
        return delFlag;
    }

    public VcOnlineTimerDetail setDelFlag(Short delFlag) {
        this.delFlag = delFlag;
        return this;
    }

	public static VcOnlineTimerDetail buildVcOnlineTimerDetail(VcOnlineWallet vcOnlineWallet, BigDecimal sumAmount, long ordRes, String orderIds) {
		// TODO Auto-generated method stub
		
		VcOnlineTimerDetail vcOnlineTimerDetail=new VcOnlineTimerDetail();
		vcOnlineTimerDetail.setOrderIds(orderIds)
		.setBatchNo(new Long(1))
		.setTraTotalAmount(vcOnlineWallet.getTraTotalAmount()==null?new BigDecimal(0):vcOnlineWallet.getTraTotalAmount())
		.setDisabledTotalAmount(vcOnlineWallet.getDisabledTotalAmount()==null?new BigDecimal(0):vcOnlineWallet.getDisabledTotalAmount())
		.setCashdTotalAmount(vcOnlineWallet.getCashdTotalAmount()==null?new BigDecimal(0):vcOnlineWallet.getCashdTotalAmount())
		.setLastCashAmount(vcOnlineWallet.getLastCashAmount()==null?new BigDecimal(0):vcOnlineWallet.getLastCashAmount())
		.setMinCashAmount(vcOnlineWallet.getMinCashAmount())
		.setMaxCashAmount(vcOnlineWallet.getMaxCashAmount())
		.setWaitTotalAmount(vcOnlineWallet.getWaitTotalAmount()==null?new BigDecimal(0):vcOnlineWallet.getWaitTotalAmount())
		.setUsableTotalAmount(vcOnlineWallet.getUsableTotalAmount()==null?new BigDecimal(0):vcOnlineWallet.getUsableTotalAmount())
		.setTotalAmount(vcOnlineWallet.getWaitTotalAmount())
		.setTotalCount(ordRes)
		.setRemark(vcOnlineWallet.getMerchNo());		
		return vcOnlineTimerDetail;
	}
}