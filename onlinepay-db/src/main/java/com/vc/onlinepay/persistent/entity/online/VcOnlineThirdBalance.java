package com.vc.onlinepay.persistent.entity.online;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

//上游商户可代付余额和下游控制账户表
public class VcOnlineThirdBalance {
	
	private BigDecimal id;  //主键id
    private String merchNo; //上游商户号
    private String merchName; //上游商户名称
    private BigDecimal balanceAmount; // 上游可待付余额
    private String pAllRes; // 查询返回数据
    private Long status; //状态：0可用，1禁用
    private String merchKey;//上游秘钥
    private Date updateDate;//更新时间
    private BigDecimal payTotal;//支付金额
    private BigDecimal reduceTotal;//代付总额
    private Timestamp lastDate;//上一次刷新金额时间
    
    private int cashMode;// 模式1：T0，2：T1
    private int seq;//提款通道优先级
    private String balanceLabel;//上游通道标记
    private Integer isTransfer;
    private int channelSource;
    private int channelId;
    private BigDecimal replacePoundage;
    
    public static VcOnlineThirdBalance geThirdBalance(String vcService) {
	    VcOnlineThirdBalance balance = new VcOnlineThirdBalance();
	    if(StringUtils.isNotBlank(vcService)){
	        balance.setBalanceLabel(vcService);
	    }
	    return balance;
	}
	
	public static VcOnlineThirdBalance geThirdBalance(BigDecimal id,String vcService,BigDecimal amount,String pAllRes) {
        VcOnlineThirdBalance balance = new VcOnlineThirdBalance();
        balance.setId(id);
        balance.setBalanceLabel(vcService);
        balance.setBalanceAmount(amount);
        balance.setpAllRes(pAllRes);
        return balance;
    }

	public VcOnlineThirdBalance() {
		super();
	}
	
	public VcOnlineThirdBalance(String balanceLabel) {
        this.balanceLabel = balanceLabel;
    }
	
	public VcOnlineThirdBalance(int cashMode,String balanceLabel) {
        this.cashMode = cashMode;
        this.balanceLabel = balanceLabel;
    }
	
	public VcOnlineThirdBalance(int cashMode,String balanceLabel,BigDecimal balanceAmount) {
        this.cashMode = cashMode;
        this.balanceLabel = balanceLabel;
        this.balanceAmount = balanceAmount;
    }
	
	public VcOnlineThirdBalance(int cashMode,BigDecimal balanceAmount) {
        this.cashMode = cashMode;
        this.balanceAmount = balanceAmount;
    }
	
	public VcOnlineThirdBalance(String merchNo, BigDecimal balanceAmount, BigDecimal reduceTotal, BigDecimal payTotal) {
		this.merchNo = merchNo;
		this.balanceAmount = balanceAmount;
		this.reduceTotal = reduceTotal;
		this.payTotal=payTotal;
	}

	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public String getMerchNo() {
		return merchNo;
	}
	public void setMerchNo(String merchNo) {
		this.merchNo = merchNo;
	}
	public String getMerchName() {
		return merchName;
	}
	public void setMerchName(String merchName) {
		this.merchName = merchName;
	}
	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}
	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	public String getpAllRes() {
		return pAllRes;
	}
	public void setpAllRes(String pAllRes) {
		this.pAllRes = pAllRes;
	}
	public Long getStatus() {
		return status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}
	public String getMerchKey() {
		return merchKey;
	}
	public void setMerchKey(String merchKey) {
		this.merchKey = merchKey;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public BigDecimal getPayTotal() {
		return payTotal;
	}
	public void setPayTotal(BigDecimal payTotal) {
		this.payTotal = payTotal;
	}
	public BigDecimal getReduceTotal() {
		return reduceTotal;
	}
	public void setReduceTotal(BigDecimal reduceTotal) {
		this.reduceTotal = reduceTotal;
	}
	public Timestamp getLastDate() {
		return lastDate;
	}
	public void setLastDate(Timestamp lastDate) {
		this.lastDate = lastDate;
	}
	public Integer getIsTransfer() {
		return isTransfer;
	}
	public void setIsTransfer(Integer isTransfer) {
		this.isTransfer = isTransfer;
	}

    public void setCashMode(int cashMode) {
        this.cashMode = cashMode;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getBalanceLabel() {
        return balanceLabel;
    }

    public void setBalanceLabel(String balanceLabel) {
        this.balanceLabel = balanceLabel;
    }

    public int getCashMode() {
        return cashMode;
    }

    public int getChannelSource() {
        return channelSource;
    }

    public void setChannelSource(int channelSource) {
        this.channelSource = channelSource;
    }

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public BigDecimal getReplacePoundage() {
		return replacePoundage;
	}

	public void setReplacePoundage(BigDecimal replacePoundage) {
		this.replacePoundage = replacePoundage;
	}

}
