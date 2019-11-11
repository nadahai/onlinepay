package com.vc.onlinepay.persistent.entity.online;

import java.math.BigDecimal;
import java.util.Date;

public class VcOnlineWalletRecord {
	
	private BigDecimal id;  //
	private Long merchId;
    private String orderNo; // 订单编号
    private String cOrderNo; // 渠道单号
    private String bOrderNo; // 渠道单号
    private Integer orderType; //1交易订单，2代付订单，3手动调账，4交易退款，5代付退汇，6交易补单,7定时刷金
    private Integer symbol; // 运算符：1+，2-，3*，4/
    private BigDecimal amount; //影响金额
    private BigDecimal poundage; //手续费：对应交易表或代付表的手续费
    private Integer wealthType; //影响字段类型（1或2）：1.USABLE_TOTAL_AMOUNT(可代付余额)，2.WAIT_TOTAL_AMOUNT(等待入账金额)
    private BigDecimal wealthAmount; //可用金额：显示影响总额之后的金额
    private BigDecimal waitAmount; //待入账金额：显示影响总额之后的金额
    private String remarks; // 备注
    private String createBy;
    private String updateBy;
    private Date createDate;
    private Date updateDate;
    private Integer delflag;
    
    /**
     * @描述:交易支付成功入账历史记录
     * @时间:2017年12月20日 下午6:02:55
     */
    public static VcOnlineWalletRecord getOrderSuccessRecord(VcOnlineOrder  vcOnlineOrder,VcOnlineWallet oldWallet) {
    	VcOnlineWalletRecord record = new VcOnlineWalletRecord();
        record.setMerchId(oldWallet.getMerchId());
        record.setOrderNo(vcOnlineOrder.getOrderNo());
        record.setcOrderNo(vcOnlineOrder.getCOrder());
		//运算符：1+，2-，3*，4/ 5未知/
		record.setSymbol(1);
		//1交易订单，2代付订单，3手动调账，4交易退款，5代付退汇，6交易补单，7定时刷金 8监控刷金
		record.setOrderType(1);
		//手续费
        record.setPoundage(vcOnlineOrder.getTraAmount().subtract(vcOnlineOrder.getActualAmount()));
		//交易金额
        record.setAmount(vcOnlineOrder.getTraAmount());
        int mode = vcOnlineOrder.getPayMode();
        if(mode == 1 || mode == 3 ){
			//影响字段类型（1或2）：1.T1正常账务记录，2.T1定时刷入 3.T0正常账务记录，4.T0定时刷入
            record.setWealthType(3);
            BigDecimal settleAmount = vcOnlineOrder.getActualAmount().subtract(vcOnlineOrder.getWaitAmount()).setScale(3,BigDecimal.ROUND_HALF_DOWN);
            BigDecimal waitAmount = vcOnlineOrder.getWaitAmount().setScale(3,BigDecimal.ROUND_HALF_DOWN);
            record.setRemarks("T0结算+:"+settleAmount+";T0待入账+:"+waitAmount);
            record.setWealthAmount(oldWallet.getD0UsableAmount().add(settleAmount));
            record.setWaitAmount(oldWallet.getD0WaitAmount().add(waitAmount));
        }else{
			//影响字段类型（1或2）：1.T1正常账务记录，2.T1定时刷入 3.T0正常账务记录，4.T0定时刷入
			record.setWealthType(1);
            record.setRemarks("T1待入账+:"+vcOnlineOrder.getActualAmount());
            record.setWealthAmount(oldWallet.getUsableTotalAmount());
            record.setWaitAmount(oldWallet.getWaitTotalAmount().add(vcOnlineOrder.getActualAmount()));
        }
        return record;
    }
    
    /**
     * @描述:开始发起代付中账户账务历史记录
     * @时间:2017年12月20日 下午6:02:55
     */
    public static VcOnlineWalletRecord getPaymentPaddingRecord(VcOnlinePayment onlinePayment,VcOnlineWallet oldWallet) {
        VcOnlineWalletRecord record = new VcOnlineWalletRecord();
        record.setMerchId(onlinePayment.getMerchId());
        record.setOrderNo(onlinePayment.getOrderNo());
        record.setcOrderNo(onlinePayment.getpOrderNo());
        record.setSymbol(2);//运算符：1+，2-，3*，4/
        record.setOrderType(2);//1交易订单，2代付订单，3手动调账，4交易退款，5代付退汇，6交易补单，7定时刷金
        record.setPoundage(onlinePayment.getPoundageRate());//手续费
        record.setAmount(onlinePayment.getCashAmount());//提现金额
        BigDecimal cashAmount =  onlinePayment.getCashAmount().add(onlinePayment.getPoundageRate());
        if(onlinePayment.getCashMode() ==1 || onlinePayment.getCashMode() ==3){
            record.setWealthType(3);//影响字段类型（1或2）：1.T1正常账务记录，2.T1定时刷入 3.T0正常账务记录，4.T0定时刷入
            record.setRemarks("T0代付可用金额-:"+cashAmount);
            record.setWealthAmount(oldWallet.getD0UsableAmount().subtract(cashAmount));
            record.setWaitAmount(oldWallet.getD0WaitAmount());
        }else {
            record.setWealthType(1);//影响字段类型（1或2）：1.T1正常账务记录，2.T1定时刷入 3.T0正常账务记录，4.T0定时刷入
            record.setRemarks("T1代付可用金额-:"+cashAmount);
            record.setWealthAmount(oldWallet.getUsableTotalAmount().subtract(cashAmount));
            record.setWaitAmount(oldWallet.getWaitTotalAmount());
        }
        return record;
    }
    
    /**
     * @描述:代付账户账务历史记录
     * @时间:2017年12月20日 下午6:02:55
     */
    public static VcOnlineWalletRecord saveFailedWalletRecord(VcOnlineWallet oldWallet,VcOnlinePayment onlinePayment) {
        VcOnlineWalletRecord record = new VcOnlineWalletRecord();
        record.setMerchId(onlinePayment.getMerchId());
        record.setOrderNo(onlinePayment.getOrderNo());
        record.setcOrderNo(onlinePayment.getpOrderNo());
        record.setSymbol(1);//运算符：1+，2-，3*，4/
        record.setOrderType(2);//1交易订单，2代付订单，3手动调账，4交易退款，5代付退汇，6交易补单，7定时刷金
		record.setPoundage(onlinePayment.getPoundageRate());//手续费
		record.setAmount(onlinePayment.getCashAmount());//提现金额
		BigDecimal cashAmount =  onlinePayment.getCashAmount().add(onlinePayment.getPoundageRate());
        //影响字段类型（1或2）：1.T1正常账务记录，2.T1定时刷入 3.T0正常账务记录，4.T0定时刷入
        if(onlinePayment.getCashMode() == 1 || onlinePayment.getCashMode() == 3){
            record.setWealthType(3);
            record.setRemarks("T0代付失败回退金额+:"+cashAmount);
            record.setWealthAmount(oldWallet.getD0UsableAmount().add(cashAmount));
            record.setWaitAmount(oldWallet.getD0WaitAmount());
        }else{
        	record.setWealthType(1);
            record.setRemarks("T1代付失败回退金额+:"+cashAmount);
            record.setWealthAmount(oldWallet.getUsableTotalAmount().add(cashAmount));
            record.setWaitAmount(oldWallet.getWaitTotalAmount());
        }
        return record;
    }
    
    /**
     * @描述:代付账户账务历史记录
     * @时间:2017年12月20日 下午6:02:55
     */
    public static VcOnlineWalletRecord updateSuccessWalletRecord(VcOnlineWallet oldWallet,VcOnlinePayment onlinePayment) {
        VcOnlineWalletRecord record = new VcOnlineWalletRecord();
        record.setMerchId(onlinePayment.getMerchId());
        record.setOrderNo(onlinePayment.getOrderNo());
        record.setcOrderNo(onlinePayment.getpOrderNo());
        record.setSymbol(2);//运算符：1+，2-，3*，4/
        record.setOrderType(2);//1交易订单，2代付订单，3手动调账，4交易退款，5代付退汇，6交易补单，7定时刷金
        record.setPoundage(new BigDecimal("0"));
        record.setAmount(onlinePayment.getCashAmount());
        //影响字段类型（1或2）：1.T1正常账务记录，2.T1定时刷入 3.T0正常账务记录，4.T0定时刷入
        if(onlinePayment.getCashMode() ==1 || onlinePayment.getCashMode() ==3){
            record.setWealthType(3);
            record.setWealthAmount(oldWallet.getD0UsableAmount());
            record.setWaitAmount(oldWallet.getD0WaitAmount());
            record.setRemarks("T0代付成功扣款金额"+onlinePayment.getCashAmount());
        }else {
            record.setWealthType(1);
            record.setWealthAmount(oldWallet.getUsableTotalAmount());
            record.setWaitAmount(oldWallet.getWaitTotalAmount());
            record.setRemarks("T1代付成功扣款金额"+onlinePayment.getCashAmount());
        }
        return record;
    }
    
	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public Long getMerchId() {
		return merchId;
	}
	public void setMerchId(Long merchId) {
		this.merchId = merchId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getcOrderNo() {
		return cOrderNo;
	}
	public void setcOrderNo(String cOrderNo) {
		this.cOrderNo = cOrderNo;
	}
	public String getbOrderNo() {
		return bOrderNo;
	}
	public void setbOrderNo(String bOrderNo) {
		this.bOrderNo = bOrderNo;
	}
	public Integer getOrderType() {
		return orderType;
	}
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	public Integer getSymbol() {
		return symbol;
	}
	public void setSymbol(Integer symbol) {
		this.symbol = symbol;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getPoundage() {
		return poundage;
	}
	public void setPoundage(BigDecimal poundage) {
		this.poundage = poundage;
	}
	public Integer getWealthType() {
		return wealthType;
	}
	public void setWealthType(Integer wealthType) {
		this.wealthType = wealthType;
	}
	public BigDecimal getWealthAmount() {
		return wealthAmount;
	}
	public void setWealthAmount(BigDecimal wealthAmount) {
		this.wealthAmount = wealthAmount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Integer getDelflag() {
		return delflag;
	}
	public void setDelflag(Integer delflag) {
		this.delflag = delflag;
	}
	public VcOnlineWalletRecord() {
		super();
	}
	public VcOnlineWalletRecord( Long merchId, String orderNo, Integer orderType, Integer symbol,
			BigDecimal amount, BigDecimal poundage, Integer wealthType, BigDecimal wealthAmount, String remarks,String updateBy) {
		super();
		this.merchId = merchId;
		this.orderNo = orderNo;
		this.orderType = orderType;
		this.symbol = symbol;
		this.amount = amount;
		this.poundage = poundage;
		this.wealthType = wealthType;
		this.wealthAmount = wealthAmount;
		this.remarks = remarks;
		this.updateBy=updateBy;
	}

	public BigDecimal getWaitAmount() {
		return waitAmount;
	}

	public void setWaitAmount(BigDecimal waitAmount) {
		this.waitAmount = waitAmount;
	}

}
