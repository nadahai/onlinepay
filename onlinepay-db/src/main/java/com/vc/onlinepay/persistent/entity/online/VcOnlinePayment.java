package com.vc.onlinepay.persistent.entity.online;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.StringUtil;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class VcOnlinePayment {

    private BigDecimal id;

    private Long merchId;
    private String orderNo;
    private BigDecimal cashAmount;
    private BigDecimal actualAmount;
    private BigDecimal traTotalAmount;
    private BigDecimal disabledTotalAmount;
    private BigDecimal cashdTotalAmount;
    private BigDecimal usableTotalAmount;
    private BigDecimal poundageRate;
    private BigDecimal waitTotalAmount;
    private String bankAccount;
    private String bankName;
    private String bankCard;
    private String bankNo;
    private String subBankNo;
    private String merchNo;
    private String cashKey;
    private Short cashMode;
    private BigDecimal tranRate;
    private String cashSign;
    private Integer status;
    private String remark;
    private String reason;
    private String pOrderNo;
    private String pKey;
    private String pSign;
    private String pAllRes;
    private String createBy;
    private Date createDate;
    private String updateBy;
    private Date updateDate;
    private String remarks;
    private Short delFlag;
    private String cashOrderNo;
    private Long channelSource;
    private Long sumCount;
    private BigDecimal sumMoney;
    private String vcCallbackUrl;
    private Integer isAccounted;
    private Date lastDate;
    private int channelId;
    private int orderSource;
    private int cNotifyNum;
    private String cNotifyResult;
    private String cNotifyUrl;
    private List<Long> channelList;//通道组
    private String channelName;
    private Integer paymentType; //代付类型 1:商户代付 2:代付失败补发
    private String redoResult;  //补发结果
    private String relateOrder; //关联订单

    public VcOnlinePayment () {
    }

    public VcOnlinePayment (String pOrderNo) {
        this.pOrderNo = pOrderNo;
    }

    /**
     * @描述:代付订单明细
     * @作者:nada
     * @时间:2017年12月19日 下午4:20:04
     */
    public static VcOnlinePayment buildVcOnlinePayment (JSONObject params, VcOnlineWallet vcOnlineWallet) {
        String  password = params.getString ("password");
        BigDecimal amount = new BigDecimal (params.getString ("amount"));
        BigDecimal tranRate = params.getBigDecimal ("replacePoundage");
        short cashMode = Short.valueOf (params.getString ("cashMode"));
        String vcOrderNo = params.containsKey ("vcOrderNo") ? params.getString ("vcOrderNo") : "";

        String balanceLabel = params.containsKey ("balanceLabel")?params.getString ("balanceLabel"):"";
        long channelSource = params.containsKey ("channelSource")?params.getLong ("channelSource"):0L;
        int channelId = params.containsKey ("channelId")?params.getInteger ("channelId"):0;

        String isMemo = params.containsKey ("isMemo")?params.getString ("isMemo"):"";
        String cashOrderNo = params.containsKey ("tradeNo")?params.getString ("tradeNo"):"";
        String bankCard = params.containsKey ("bankCard") ? params.getString ("bankCard") : "";
        String bankName = params.containsKey ("bankName") ? params.getString ("bankName") : "";
        String bankLinked = params.containsKey ("bankLinked") ? params.getString ("bankLinked") : "";
        String accountName = params.containsKey ("accountName") ? params.getString ("accountName") : "";
        String bankSubName = params.containsKey ("bankSubName") ? params.getString ("bankSubName") : "";
        String channelMerchNo = params.containsKey ("channelMerchNo")?params.getString ("channelMerchNo"):"";
        String channelMerchKey = params.containsKey ("channelMerchKey")?params.getString ("channelMerchKey"):"";
        int orderSource = 1;
        int paymentType = 1;
        String relateOrder = cashOrderNo;
        if ("isMemo".equals (isMemo)) {
            orderSource = 2;
            paymentType = 2;
            relateOrder = params.getString ("orderNo").substring (0, params.getString ("orderNo").indexOf ("_"));
        }

        VcOnlinePayment vcOnlinePayment = new VcOnlinePayment ();
        vcOnlinePayment.
        setMerchId (vcOnlineWallet.getMerchId ()).
        setMerchNo (vcOnlineWallet.getMerchNo ()).
        setOrderNo (vcOrderNo).
        setpOrderNo (cashOrderNo).
        setCashOrderNo(cashOrderNo).
        setCashAmount (amount).
        setActualAmount (amount).
        setTranRate (tranRate).
        setCashMode (cashMode).
        setStatus (2).

        setTraTotalAmount (vcOnlineWallet.getTraTotalAmount ()).
        setCashdTotalAmount (vcOnlineWallet.getCashdTotalAmount ()).
        setDisabledTotalAmount (vcOnlineWallet.getD0DisabledAmount ()).
        setUsableTotalAmount (vcOnlineWallet.getD0UsableAmount ()).
        setPoundageRate (vcOnlineWallet.getServiceCharge ()).
        setWaitTotalAmount (vcOnlineWallet.getD0WaitAmount ()).

        setBankAccount (accountName).
        setBankCard (StringUtils.deleteWhitespace (bankCard)).
        setBankName (bankName).
        setSubBankNo (bankSubName).
        setBankNo (bankLinked).

        setCashKey (password).
        setCashSign (params.toString ()).
        setRemarks (balanceLabel).
        setRemark ("发起代付").
        setOrderSource (orderSource).
        setChannelId (channelId).
        setpKey (channelMerchNo).
        setpSign (channelMerchKey).
        setPaymentType (paymentType).
        setRelateOrder (relateOrder).
        setChannelSource(channelSource).
        setpAllRes ("").
        setIsAccounted (0);
        vcOnlinePayment.setcNotifyUrl ("");
        return vcOnlinePayment;
    }

    public BigDecimal getId () {
        return id;
    }

    public VcOnlinePayment setId (BigDecimal id) {
        this.id = id;
        return this;
    }

    public Long getMerchId () {
        return merchId;
    }

    public VcOnlinePayment setMerchId (Long merchId) {
        this.merchId = merchId;
        return this;
    }

    public String getOrderNo () {
        return orderNo;
    }

    public VcOnlinePayment setOrderNo (String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim ();
        return this;
    }

    public BigDecimal getCashAmount () {
        return cashAmount;
    }

    public VcOnlinePayment setCashAmount (BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
        return this;
    }

    public BigDecimal getActualAmount () {
        return actualAmount;
    }

    public VcOnlinePayment setActualAmount (BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
        return this;
    }

    public BigDecimal getTraTotalAmount () {
        return traTotalAmount;
    }

    public VcOnlinePayment setTraTotalAmount (BigDecimal traTotalAmount) {
        this.traTotalAmount = traTotalAmount;
        return this;
    }

    public BigDecimal getDisabledTotalAmount () {
        return disabledTotalAmount;
    }

    public VcOnlinePayment setDisabledTotalAmount (BigDecimal disabledTotalAmount) {
        this.disabledTotalAmount = disabledTotalAmount;
        return this;
    }

    public BigDecimal getCashdTotalAmount () {
        return cashdTotalAmount;
    }

    public VcOnlinePayment setCashdTotalAmount (BigDecimal cashdTotalAmount) {
        this.cashdTotalAmount = cashdTotalAmount;
        return this;
    }

    public BigDecimal getUsableTotalAmount () {
        return usableTotalAmount;
    }

    public VcOnlinePayment setUsableTotalAmount (BigDecimal usableTotalAmount) {
        this.usableTotalAmount = usableTotalAmount;
        return this;
    }


    public BigDecimal getPoundageRate () {
        return poundageRate;
    }

    public VcOnlinePayment setPoundageRate (BigDecimal poundageRate) {
        this.poundageRate = poundageRate;
        return this;
    }

    public BigDecimal getWaitTotalAmount () {
        return waitTotalAmount;
    }

    public VcOnlinePayment setWaitTotalAmount (BigDecimal waitTotalAmount) {
        this.waitTotalAmount = waitTotalAmount;
        return this;
    }

    public String getBankAccount () {
        return bankAccount;
    }

    public VcOnlinePayment setBankAccount (String bankAccount) {
        this.bankAccount = bankAccount == null ? null : bankAccount.trim ();
        return this;
    }

    public String getBankName () {
        return bankName;
    }

    public VcOnlinePayment setBankName (String bankName) {
        this.bankName = bankName == null ? null : bankName.trim ();
        return this;
    }

    public String getBankCard () {
        return bankCard;
    }

    public VcOnlinePayment setBankCard (String bankCard) {
        this.bankCard = bankCard == null ? null : bankCard.trim ();
        return this;
    }

    public String getBankNo () {
        return bankNo;
    }

    public VcOnlinePayment setBankNo (String bankNo) {
        this.bankNo = bankNo;
        return this;
    }

    public String getSubBankNo () {
        return subBankNo;
    }

    public VcOnlinePayment setSubBankNo (String subBankNo) {
        this.subBankNo = subBankNo;
        return this;
    }

    public String getMerchNo () {
        return merchNo;
    }

    public VcOnlinePayment setMerchNo (String merchNo) {
        this.merchNo = merchNo == null ? null : merchNo.trim ();
        return this;
    }

    public String getCashKey () {
        return cashKey;
    }

    public VcOnlinePayment setCashKey (String cashKey) {
        this.cashKey = cashKey == null ? null : cashKey.trim ();
        return this;
    }

    public Short getCashMode () {
        return cashMode;
    }

    public VcOnlinePayment setCashMode (Short cashMode) {
        this.cashMode = cashMode;
        return this;
    }

    public BigDecimal getTranRate () {
        return tranRate;
    }

    public VcOnlinePayment setTranRate (BigDecimal tranRate) {
        this.tranRate = tranRate;
        return this;
    }

    public String getCashSign () {
        return cashSign;
    }

    public VcOnlinePayment setCashSign (String cashSign) {
        this.cashSign = cashSign == null ? null : cashSign.trim ();
        return this;
    }

    public Integer getStatus () {
        return status;
    }

    public VcOnlinePayment setStatus (Integer status) {
        this.status = status;
        return this;
    }

    public String getRemark () {
        return remark;
    }

    public VcOnlinePayment setRemark (String remark) {
        this.remark = remark == null ? null : remark.trim ();
        return this;
    }

    public String getReason () {
        return reason;
    }

    public VcOnlinePayment setReason (String reason) {
        if(StringUtil.isNotEmpty (reason)){
            this.reason = reason.trim ();
        }else{
            this.reason = "";
        }
        return this;
    }

    public String getpOrderNo () {
        return pOrderNo;
    }

    public VcOnlinePayment setpOrderNo (String pOrderNo) {
        this.pOrderNo = pOrderNo == null ? null : pOrderNo.trim ();
        return this;
    }

    public String getpKey () {
        return pKey;
    }

    public VcOnlinePayment setpKey (String pKey) {
        this.pKey = pKey == null ? null : pKey.trim ();
        return this;
    }

    public String getpSign () {
        return pSign;
    }

    public VcOnlinePayment setpSign (String pSign) {
        this.pSign = pSign == null ? null : pSign.trim ();
        return this;
    }

    public String getpAllRes () {
        return pAllRes;
    }

    public VcOnlinePayment setpAllRes (String pAllRes) {
        this.pAllRes = pAllRes == null ? null : pAllRes.trim ();
        return this;
    }

    public String getCreateBy () {
        return createBy;
    }

    public VcOnlinePayment setCreateBy (String createBy) {
        this.createBy = createBy == null ? null : createBy.trim ();
        return this;
    }

    public Date getCreateDate () {
        return createDate;
    }

    public VcOnlinePayment setCreateDate (Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public String getUpdateBy () {
        return updateBy;
    }

    public VcOnlinePayment setUpdateBy (String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim ();
        return this;
    }

    public Date getUpdateDate () {
        return updateDate;
    }

    public VcOnlinePayment setUpdateDate (Date updateDate) {
        this.updateDate = updateDate;
        return this;
    }

    public String getRemarks () {
        return remarks;
    }

    public VcOnlinePayment setRemarks (String remarks) {
        this.remarks = remarks == null ? null : remarks.trim ();
        return this;
    }

    public Short getDelFlag () {
        return delFlag;
    }

    public VcOnlinePayment setDelFlag (Short delFlag) {
        this.delFlag = delFlag;
        return this;
    }

    public Long getChannelSource () {
        return channelSource;
    }

    public VcOnlinePayment setChannelSource (Long channelSource) {
        this.channelSource = channelSource;
        return this;
    }

    public String getCashOrderNo () {
        return cashOrderNo;
    }

    public VcOnlinePayment setCashOrderNo (String cashOrderNo) {
        this.cashOrderNo = cashOrderNo == null ? null : cashOrderNo.trim ();
        return this;
    }

    public String getVcCallbackUrl () {
        return vcCallbackUrl;
    }

    public void setVcCallbackUrl (String vcCallbackUrl) {
        this.vcCallbackUrl = vcCallbackUrl;
    }

    public Long getSumCount () {
        return sumCount;
    }

    public void setSumCount (Long sumCount) {
        this.sumCount = sumCount;
    }

    public BigDecimal getSumMoney () {
        return sumMoney;
    }

    public void setSumMoney (BigDecimal sumMoney) {
        this.sumMoney = sumMoney;
    }

    public Integer getIsAccounted () {
        return isAccounted;
    }

    public VcOnlinePayment setIsAccounted (Integer isAccounted) {
        this.isAccounted = isAccounted;
        return this;
    }

    public Date getLastDate () {
        return lastDate;
    }

    public void setLastDate (Date lastDate) {
        this.lastDate = lastDate;
    }

    public int getOrderSource () {
        return orderSource;
    }

    public VcOnlinePayment setOrderSource (int orderSource) {
        this.orderSource = orderSource;
        return this;
    }

    public List<Long> getChannelList () {
        return channelList;
    }

    public void setChannelList (List<Long> channelList) {
        this.channelList = channelList;
    }

    public int getcNotifyNum () {
        return cNotifyNum;
    }

    public void setcNotifyNum (int cNotifyNum) {
        this.cNotifyNum = cNotifyNum;
    }

    public String getcNotifyResult () {
        return cNotifyResult;
    }

    public void setcNotifyResult (String cNotifyResult) {
        this.cNotifyResult = cNotifyResult;
    }

    public String getcNotifyUrl () {
        return cNotifyUrl;
    }

    public void setcNotifyUrl (String cNotifyUrl) {
        this.cNotifyUrl = cNotifyUrl;
    }

    public String getChannelName () {
        return channelName;
    }

    public void setChannelName (String channelName) {
        this.channelName = channelName;
    }

    public int getChannelId () {
        return channelId;
    }

    public VcOnlinePayment setChannelId (int channelId) {
        this.channelId = channelId;
        return this;
    }

    public Integer getPaymentType () {
        return paymentType;
    }

    public VcOnlinePayment setPaymentType (Integer paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public String getRedoResult () {
        return redoResult;
    }

    public void setRedoResult (String redoResult) {
        this.redoResult = redoResult;
    }

    public String getRelateOrder () {
        return relateOrder;
    }

    public VcOnlinePayment setRelateOrder (String relateOrder) {
        this.relateOrder = relateOrder;
        return this;
    }
}