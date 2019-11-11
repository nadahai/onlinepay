/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.online;

import java.math.BigDecimal;

/**
 * 在线交易财富信息Entity
 * @author 李海
 * @version 2017-06-30
 */
public class VcOnlineWallet{
	
	private static final long serialVersionUID = 1L;
	private BigDecimal id;
	private Long merchId;		// 商户ID
	private String merchNo;		// 商户编号
	private BigDecimal traTotalAmount;		// 交易总额
	private BigDecimal disabledTotalAmount;		// 占用资金
	private BigDecimal cashdTotalAmount;		// 已出款总额
	private BigDecimal lastCashAmount;		// 最后提款金额
	private BigDecimal minCashAmount;		// 出款最低限额
	private BigDecimal maxCashAmount;		// 出款最高限额
	private BigDecimal waitTotalAmount;		// 等待入账金额
	private BigDecimal usableTotalAmount;		// 可用总额
	private Integer status;		// 资金状态
	private String reason;		// 冻结原因
	private String remark;
	private String remarks;		// 备注
    private Integer delflag;
    private String pOrderId;    //下游ID
    private BigDecimal serviceCharge=new BigDecimal(2);//手续费默认2元
    private Integer timing;   //是否刷定时：1刷定时，其它不刷
    private BigDecimal d0WaitAmount;
    private BigDecimal d0UsableAmount;
    private BigDecimal d0DisabledAmount;
    private BigDecimal  wealthAmount;
    private BigDecimal tsTraTotalAmount;		// 直清交易总额
    private BigDecimal waitAmount; //待入账金额：显示影响总额之后的金额
	 /**
     * @描述:代付时修改财富金额
     * @时间:2017年12月6日 上午11:02:53
     */
    public static VcOnlineWallet buildWalletCashAmount(Long merchId,BigDecimal amount,BigDecimal poundAmount) {
        VcOnlineWallet newWallet = new VcOnlineWallet();
        newWallet.setMerchId(merchId);
        newWallet.setLastCashAmount(amount);
        newWallet.setServiceCharge(poundAmount);
        return newWallet;
    }
    
	public VcOnlineWallet() {
		super();
	}

	public Long getMerchId() {
		return merchId;
	}

	public VcOnlineWallet setMerchId(Long merchId) {
		this.merchId = merchId;
		 return this;
	}
	
	public String getMerchNo() {
		return merchNo;
	}

	public VcOnlineWallet setMerchNo(String merchNo) {
		this.merchNo = merchNo;
		 return this;
	}
	
	public BigDecimal getTraTotalAmount() {
		return traTotalAmount;
	}

	public VcOnlineWallet setTraTotalAmount(BigDecimal traTotalAmount) {
		this.traTotalAmount = traTotalAmount;
		 return this;
	}
	
	public BigDecimal getDisabledTotalAmount() {
		return disabledTotalAmount;
	}

	public VcOnlineWallet setDisabledTotalAmount(BigDecimal disabledTotalAmount) {
		this.disabledTotalAmount = disabledTotalAmount;
		 return this;
	}
	
	public BigDecimal getCashdTotalAmount() {
		return cashdTotalAmount;
	}

	public VcOnlineWallet setCashdTotalAmount(BigDecimal cashdTotalAmount) {
		this.cashdTotalAmount = cashdTotalAmount;
		 return this;
	}
	
	public BigDecimal getLastCashAmount() {
		return lastCashAmount;
	}

	public VcOnlineWallet setLastCashAmount(BigDecimal lastCashAmount) {
		this.lastCashAmount = lastCashAmount;
		 return this;
	}
	
	public BigDecimal getMinCashAmount() {
		return minCashAmount;
	}

	public VcOnlineWallet setMinCashAmount(BigDecimal minCashAmount) {
		this.minCashAmount = minCashAmount;
		 return this;
	}
	
	public BigDecimal getMaxCashAmount() {
		return maxCashAmount;
	}

	public VcOnlineWallet setMaxCashAmount(BigDecimal maxCashAmount) {
		this.maxCashAmount = maxCashAmount;
		 return this;
	}
	
	public BigDecimal getWaitTotalAmount() {
		return waitTotalAmount;
	}

	public VcOnlineWallet setWaitTotalAmount(BigDecimal waitTotalAmount) {
		this.waitTotalAmount = waitTotalAmount;
		 return this;
	}
	
	public BigDecimal getUsableTotalAmount() {
		return usableTotalAmount;
	}

	public VcOnlineWallet setUsableTotalAmount(BigDecimal usableTotalAmount) {
		this.usableTotalAmount = usableTotalAmount;
		 return this;
	}
	
	public Integer getStatus() {
		return status;
	}

	public VcOnlineWallet setStatus(Integer status) {
		this.status = status;
		 return this;
	}
	
	public String getReason() {
		return reason;
	}

	public VcOnlineWallet setReason(String reason) {
		this.reason = reason;
		 return this;
	}
	
	public String getRemark() {
		return remark;
	}

	public VcOnlineWallet setRemark(String remark) {
		this.remark = remark;
		 return this;
	}

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
        
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
	public String getRemarks() {
		return remarks;
	}
	public VcOnlineWallet setRemarks(String remarks) {
		this.remarks = remarks;
		 return this;
	}

	public Integer getDelflag() {
		return delflag;
	}

	public VcOnlineWallet setDelflag(Integer delflag) {
		this.delflag = delflag;
		 return this;
	}

	public String getpOrderId() {
		return pOrderId;
	}

	public void setpOrderId(String pOrderId) {
		this.pOrderId = pOrderId;
	}

	public BigDecimal getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(BigDecimal serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public Integer getTiming() {
		return timing;
	}

	public void setTiming(Integer timing) {
		this.timing = timing;
	}

	public BigDecimal getD0WaitAmount() {
		return d0WaitAmount;
	}

	public void setD0WaitAmount(BigDecimal d0WaitAmount) {
		this.d0WaitAmount = d0WaitAmount;
	}

	public BigDecimal getD0UsableAmount() {
		return d0UsableAmount;
	}

	public void setD0UsableAmount(BigDecimal d0UsableAmount) {
		this.d0UsableAmount = d0UsableAmount;
	}

	public BigDecimal getD0DisabledAmount() {
		return d0DisabledAmount;
	}

	public void setD0DisabledAmount(BigDecimal d0DisabledAmount) {
		this.d0DisabledAmount = d0DisabledAmount;
	}
    public BigDecimal getWealthAmount() {
        return wealthAmount;
    }
    public void setWealthAmount(BigDecimal wealthAmount) {
        this.wealthAmount = wealthAmount;
    }
	public BigDecimal getTsTraTotalAmount() {
		return tsTraTotalAmount;
	}
	public void setTsTraTotalAmount(BigDecimal tsTraTotalAmount) {
		this.tsTraTotalAmount = tsTraTotalAmount;
	}

	public BigDecimal getWaitAmount() {
		return waitAmount;
	}

	public void setWaitAmount(BigDecimal waitAmount) {
		this.waitAmount = waitAmount;
	}
}