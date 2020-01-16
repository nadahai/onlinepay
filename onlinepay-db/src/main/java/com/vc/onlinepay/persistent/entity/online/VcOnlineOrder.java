/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.online;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.entity.channel.MerchChannel;
import com.vc.onlinepay.utils.Constant;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.vc.onlinepay.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 李海在线交易订单Entity
 *
 * @author 李海
 * @version 2017-06-30
 */
public class VcOnlineOrder {

    private BigDecimal id;
    private Long merchId;        // 商户ID
    private String merchNo;        // 商户编号
    private String merchName;        // 商户名称
    private String orderNo;        // 订单编号
    private BigDecimal traAmount;        // 交易金额
    private BigDecimal actualAmount;        // 实收金额
    private String payCode;        // 付款码
    private String bankNo;        // 收款账号
    private Integer traType;        // 交易类型
    private Integer payType;        // 支付方式
    private Integer paySource;        // 支付来源
    private Integer payMode;        // 支付模式
    private String payKey;        // 支付所用key
    private BigDecimal settlePoundage;        // 结算手续费
    private BigDecimal traRate;        // 交易费率
    private Date settleDate;        // 结算日期
    private Integer settleStatus;        // 结算状态
    private Integer status;        // 状态
    private String remark;        // 备注
    private Date payDate;        // 支付时间
    private String cOrder;        // 下游平台订单号
    private String cProductDes;        // 下游平台产品描述
    private String cRequestId;        // 下游平台唯一标识
    private String cNotifyUrl;        // 下游平台回调地址
    private String cSign;        // 下游平台sign
    private String pOrder;        // 上游平台订单号
    private String pSign;        // 上游平台返回sign
    private String pRescode;        // 上游平台返回码
    private String pAllRes;        // 上游平台所有返回结果
    private String orderDes;        // 订单描述
    private String createBy;
    private Date createDate;
    private String updateBy;
    private Date updateDate;
    private Integer currency;    //货币
    private String remarks;
    private String smstrxid;
    private BigDecimal sumMoney;
    private BigDecimal sumTotal;
    private Integer isAccounted = 0;//是否已入账，1已入账
    private Long channelId;//通道id
    private BigDecimal waitAmount;
    private int cNotifyNum;
    private String cNotifyResult;
    private String upMerchNo;
    private String upMerchKey;

    private BigDecimal sumTraAmount;
    private BigDecimal sumTraNum;
    private BigDecimal sumCashAmount;
    private BigDecimal sumCashNum;
    private BigDecimal sumProfitAmount;
    private List<Long> channelList;//通道来源数组
    private Date beginCreateDate;
    private Date endCreateDate;

    public VcOnlineOrder (String orderNo, List<Long> list, String remarks) {
        this.orderNo = orderNo;
        this.channelList = list;
        this.remarks = remarks;
    }

    /**
     * @描述:银行卡转账
     * @作者:nada
     * @时间:2019/1/4
     **/
    public static VcOnlineOrder buildBankVcOnlineOrder (VcOnlineOrder vcOnlineOrder, String amount, String accountNo, String tradeNO, String card4EndNo, JSONObject reqData) throws IllegalArgumentException {
        vcOnlineOrder = new VcOnlineOrder ();
        vcOnlineOrder.setTraAmount (new BigDecimal (amount));
        vcOnlineOrder.setPayType (10);
        vcOnlineOrder.setPaySource (51);
        vcOnlineOrder.setUpMerchNo (accountNo);
        vcOnlineOrder.setpOrder (tradeNO);
        vcOnlineOrder.setRemark (reqData.toJSONString ());
        vcOnlineOrder.setRemarks (card4EndNo + "银行卡匹配错误\n" + accountNo);
        vcOnlineOrder.setTraType (2);
        return vcOnlineOrder;
    }

    public static VcOnlineOrder biuldEmptyCopyOrder (String pOrderNo, String amount, String upMerchNo, String msg, String remark) {
        if(StringUtil.isNotEmpty(remark)){
            remark = remark.length ()>3000?remark.substring (0,2900):remark;
        }
        VcOnlineOrder vcOnlineOrder = new VcOnlineOrder ();
        vcOnlineOrder.setMerchId (1L);
        vcOnlineOrder.setMerchNo ("1");
        vcOnlineOrder.setOrderNo ("");
        vcOnlineOrder.setTraAmount (new BigDecimal (amount));
        vcOnlineOrder.setPayType (10);
        vcOnlineOrder.setPaySource (51);
        vcOnlineOrder.setRemarks (msg);
        vcOnlineOrder.setTraType (2);
        vcOnlineOrder.setcOrder ("");
        vcOnlineOrder.setpOrder (pOrderNo);
        vcOnlineOrder.setUpMerchNo (upMerchNo);
        vcOnlineOrder.setOrderDes (remark);
        return vcOnlineOrder;
    }

    public static VcOnlineOrder biuldEmptyCopyOrder(String pOrderNo,String amount,String upMerchNo,String msg,String remark,int source){
        return new VcOnlineOrder(){{
            setMerchId(1L);
            setMerchNo("1");
            setOrderNo("");
            setTraAmount(new BigDecimal(StringUtil.isEmpty (amount)?"0":amount));
            setPayType(10);
            setPaySource(source);
            setRemarks(msg);
            setTraType(2);
            setcOrder("");
            setpOrder(pOrderNo);
            setUpMerchNo(upMerchNo);
            setOrderDes(remark);
        }};
    }

    /**
     * @描述:构建大商户订单参数
     * @作者:nada
     * @时间:2017年12月21日 下午5:43:03
     */
    public static VcOnlineOrder buildVcOnlineOrder (JSONObject reqData, MerchChannel merchChannel) throws IllegalArgumentException {
        BigDecimal traAmount = new BigDecimal (reqData.getString ("amount"));
        BigDecimal actualAmount = Constant.getActualMoney (traAmount, merchChannel.getTranRate ());
        BigDecimal waitAmount = new BigDecimal ("0");
        long payMode = merchChannel.getSettleType ();
        //是否已入账：0:T1未入账 1:已经全部入账  2: 40%或 60%或80%待入账  3:T0已全部入账 4:默认未结算
        int isAccounted = 1;
        if ((payMode == 1 || payMode == 3) && Constant.zeroAndOneDecimal (merchChannel.getSettleRate ())) {
            isAccounted = 2;
            waitAmount = actualAmount.multiply (Constant.oneDecimal.subtract (merchChannel.getSettleRate ()));
        }
        String cSign = reqData.toString ();
        String cOrderNo = reqData.containsKey ("orderId") ? reqData.getString ("orderId") : reqData.getString ("tradeNo");
        if (StringUtils.isNoneBlank (cSign) && cSign.length () > 3200) {
            JSONObject prams = new JSONObject ();
            prams.put ("orderId", cOrderNo);
            prams.put ("channelKey", reqData.getString ("channelKey"));
            prams.put ("channelDesKey", reqData.getString ("channelDesKey"));
            prams.put ("projectDomainUrl", reqData.getString ("projectDomainUrl"));
            if (reqData.containsKey ("appId")) {
                prams.put ("appId", reqData.getString ("appId"));
            }
            if (reqData.containsKey ("publicKey")) {
                prams.put ("publicKey", reqData.getString ("publicKey"));
            }
            if (reqData.containsKey ("privateKey")) {
                prams.put ("privateKey", reqData.getString ("privateKey"));
            }
            if (reqData.containsKey ("appUserId")) {
                prams.put ("appUserId", reqData.getString ("appUserId"));
            }
            cSign = prams.toString ();
        }
        VcOnlineOrder vcOnlineOrder = new VcOnlineOrder ();
        vcOnlineOrder.setMerchNo (reqData.getString ("merchantNo")).
            setMerchName (reqData.getString ("merchName")).
            setTraAmount (traAmount).
            setActualAmount (actualAmount).
            setBankNo (reqData.getString ("bankNo")).
            setPayMode ((int) payMode).
            setPayKey (reqData.getString ("password")).
            setMerchId (merchChannel.getMerchId ()).
            setPayType ((int) merchChannel.getPayType ()).
            setPaySource ((int) merchChannel.getChannelSource ()).
            setSettlePoundage (merchChannel.getChannelCost ()).
            setTraRate (merchChannel.getTranRate ()).
            setChannelId (merchChannel.getChannelId ())
            .setCProductDes (reqData.getString ("goodsDesc")).
            setCNotifyUrl (reqData.getString ("notifyUrl")).
            setpSign (reqData.getString ("channelKey")).
            setpRescode ("0").
            setIsAccounted (isAccounted).
            setWaitAmount (waitAmount).
            setCSign (cSign).setPAllRes (reqData.toString ()).
            setOrderNo (reqData.getString ("vcOrderNo")).
            setCOrder (cOrderNo).setPOrder (cOrderNo).
            setRemarks (reqData.getString ("channelLabel")).
            setSmstrxid (reqData.getString ("vcOrderNo")).
            setPayCode (Constant.format2BigDecimal (traAmount).toString ()).
            setTraType (merchChannel.getChannelType ()).setStatus (3).
            setCurrency (1).setSettleStatus (1).
            setRemark ("下单中").setOrderDes ("下单中").
            setUpMerchNo (reqData.getString ("channelKey")).
            setUpMerchKey (reqData.getString ("channelDesKey"));
        if (merchChannel.getChannelSource () == 78L) {
            vcOnlineOrder.setpSign (reqData.getString ("channelDesKey"));
        }
        //支付宝个人保存
        if (merchChannel.getChannelSource () == 51L) {
            if (reqData.containsKey ("oldOrderNo")) {
                vcOnlineOrder.setSmstrxid (reqData.getString ("oldOrderNo"));
            } else {
                vcOnlineOrder.setSmstrxid (reqData.getString ("vcOrderNo"));
            }
        } else if (merchChannel.getChannelSource () == 65L || merchChannel.getChannelSource () == 81L || merchChannel.getChannelSource () == 83L) {
            if (reqData.containsKey ("appType")) {
                vcOnlineOrder.setSmstrxid (reqData.getString ("appType"));
            } else {
                vcOnlineOrder.setSmstrxid ("0");
            }
        }
        if(merchChannel.getChannelSource() == 111L){
            vcOnlineOrder.setUpMerchNo(reqData.getString("pdd_buyer_login_name"));
            vcOnlineOrder.setUpMerchKey(reqData.getString("accessToken"));
            vcOnlineOrder.setSmstrxid(reqData.getString("pdd_order_json"));
            vcOnlineOrder.setpRescode(reqData.getString("pdd_info"));
            vcOnlineOrder.setpAllRes(reqData.getString("pdd_order_info"));
            vcOnlineOrder.setpSign(reqData.getString("pdd_chanel_name"));
        }
        return vcOnlineOrder;
    }


    /**
     * @描述:构建大商户订单参数
     * @时间:2017年12月21日 下午5:43:03
     */
    public static VcOnlineOrder buildFailedOnlineOrder (JSONObject reqData, MerchChannel merchChannel) throws IllegalArgumentException {
        BigDecimal traAmount = new BigDecimal (reqData.getString ("amount"));
        BigDecimal actualAmount = Constant.getActualMoney (traAmount, merchChannel.getTranRate ());
        BigDecimal waitAmount = new BigDecimal ("0");
        int payMode = merchChannel.getSettleType().intValue();
        int isAccounted = 1;
        if ((payMode == 1 || payMode == 3) && Constant.zeroAndOneDecimal (merchChannel.getSettleRate ())) {
            isAccounted = 2;
            waitAmount = actualAmount.multiply (Constant.oneDecimal.subtract (merchChannel.getSettleRate ()));
        }
        String cSign = reqData.toString ();
        String bankNo = reqData.getString ("bankNo");
        String merchName = reqData.getString("merchName");
        String password = reqData.getString ("password");
        String merchantNo = reqData.getString("merchantNo");
        String vcOrderNo = reqData.getString("vcOrderNo");
        String msg = reqData.containsKey("msg")?reqData.getString("msg"):"下单失败";

        String cOrderNo = reqData.containsKey ("orderId") ? reqData.getString ("orderId") : reqData.getString ("tradeNo");
        VcOnlineOrder vcOnlineOrder = new VcOnlineOrder ();
        vcOnlineOrder.setMerchNo (merchantNo).
                setMerchName(merchName).
                setTraAmount(traAmount).
                setActualAmount(actualAmount).
                setBankNo(bankNo).
                setPayMode(payMode).
                setPayKey(password).
                setMerchId(merchChannel.getMerchId()).
                setPayType((int)merchChannel.getPayType ()).
                setPaySource((int)merchChannel.getChannelSource()).
                setTraRate(merchChannel.getTranRate()).
                setTraType(merchChannel.getChannelType()).
                setChannelId(merchChannel.getChannelId ()).
                setCProductDes(reqData.getString("goodsDesc")).
                setCNotifyUrl(reqData.getString("notifyUrl")).
                setpSign(reqData.getString("channelKey")).
                setIsAccounted(isAccounted).
                setWaitAmount(waitAmount).
                setCSign(cSign).
                setOrderNo(vcOrderNo).
                setCOrder(cOrderNo).
                setPOrder (cOrderNo).
                setSmstrxid(vcOrderNo).
                setCurrency(1).
                setSettleStatus(1).
                setStatus (2).//状态 1下单成功 2下单失败 3下单中 4支付成功 5支付失败 6:处理中 7:代付中 8:代付失败 9交易退款
                setOrderDes(msg).
                setRemark("下单失败").
                setRemarks("下单失败").
                setUpMerchNo(reqData.getString ("channelKey")).
                setUpMerchKey(reqData.getString ("channelDesKey"));
        return vcOnlineOrder;
    }

    public VcOnlineOrder () {
    }

    public VcOnlineOrder (String orderNo) {
        this.orderNo = orderNo;
    }

    public VcOnlineOrder (String orderNo,String orderDes,Integer payType) {
        this.orderNo = orderNo;
        this.orderDes = orderDes;
        this.payType = payType;
    }

    public String getcOrder () {
        return cOrder;
    }

    public VcOnlineOrder setcOrder (String cOrder) {
        this.cOrder = cOrder;
        return this;
    }

    public String getcProductDes () {
        return cProductDes;
    }

    public VcOnlineOrder setcProductDes (String cProductDes) {
        this.cProductDes = cProductDes;
        return this;
    }

    public String getcRequestId () {
        return cRequestId;
    }

    public VcOnlineOrder setcRequestId (String cRequestId) {
        this.cRequestId = cRequestId;
        return this;
    }

    public String getcNotifyUrl () {
        return cNotifyUrl;
    }

    public VcOnlineOrder setcNotifyUrl (String cNotifyUrl) {
        this.cNotifyUrl = cNotifyUrl;
        return this;
    }

    public String getcSign () {
        return cSign;
    }

    public VcOnlineOrder setcSign (String cSign) {
        this.cSign = cSign;
        return this;
    }

    public String getpOrder () {
        return pOrder;
    }

    public VcOnlineOrder setpOrder (String pOrder) {
        this.pOrder = pOrder;
        return this;
    }

    public String getpSign () {
        return pSign;
    }

    public VcOnlineOrder setpSign (String pSign) {
        this.pSign = pSign;
        return this;
    }

    public String getpRescode () {
        return pRescode;

    }

    public VcOnlineOrder setpRescode (String pRescode) {
        this.pRescode = pRescode;
        return this;
    }

    public String getpAllRes () {
        return pAllRes;
    }

    public VcOnlineOrder setpAllRes (String pAllRes) {
        this.pAllRes = pAllRes;
        return this;
    }

    public String getCreateBy () {
        return createBy;
    }

    public VcOnlineOrder setCreateBy (String createBy) {
        this.createBy = createBy;
        return this;
    }

    public String getUpdateBy () {
        return updateBy;
    }

    public VcOnlineOrder setUpdateBy (String updateBy) {
        this.updateBy = updateBy;
        return this;
    }

    public Date getCreateDate () {
        return createDate;
    }

    public void setCreateDate (Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate () {
        return updateDate;
    }

    public void setUpdateDate (Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemarks () {
        return remarks;
    }

    public VcOnlineOrder setRemarks (String remarks) {
        this.remarks = remarks;
        return this;
    }

    public Long getMerchId () {
        return merchId;
    }

    public VcOnlineOrder setMerchId (Long merchId) {
        this.merchId = merchId;
        return this;
    }

    public String getMerchNo () {
        return merchNo;
    }

    public VcOnlineOrder setMerchNo (String merchNo) {
        this.merchNo = merchNo;
        return this;
    }

    public String getMerchName () {
        return merchName;
    }

    public VcOnlineOrder setMerchName (String merchName) {
        this.merchName = merchName;
        return this;
    }

    public String getOrderNo () {
        return orderNo;
    }

    public VcOnlineOrder setOrderNo (String orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public BigDecimal getTraAmount () {
        return traAmount;
    }

    public VcOnlineOrder setTraAmount (BigDecimal traAmount) {
        this.traAmount = traAmount;
        return this;
    }

    public BigDecimal getActualAmount () {
        return actualAmount;
    }

    public VcOnlineOrder setActualAmount (BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
        return this;
    }

    public String getPayCode () {
        return payCode;
    }

    public VcOnlineOrder setPayCode (String payCode) {
        this.payCode = payCode;
        return this;
    }

    public String getBankNo () {
        return bankNo;
    }

    public VcOnlineOrder setBankNo (String bankNo) {
        this.bankNo = bankNo;
        return this;
    }

    public Integer getTraType () {
        return traType;
    }

    public VcOnlineOrder setTraType (Integer traType) {
        this.traType = traType;
        return this;
    }

    public Integer getPayType () {
        return payType;
    }

    public VcOnlineOrder setPayType (Integer payType) {
        this.payType = payType;
        return this;
    }

    public Integer getPaySource () {
        return paySource;
    }

    public VcOnlineOrder setPaySource (Integer paySource) {
        this.paySource = paySource;
        return this;
    }

    public Integer getPayMode () {
        return payMode;
    }

    public VcOnlineOrder setPayMode (Integer payMode) {
        this.payMode = payMode;
        return this;
    }

    public String getPayKey () {
        return payKey;
    }

    public VcOnlineOrder setPayKey (String payKey) {
        this.payKey = payKey;
        return this;
    }

    public BigDecimal getSettlePoundage () {
        return settlePoundage;
    }

    public VcOnlineOrder setSettlePoundage (BigDecimal settlePoundage) {
        this.settlePoundage = settlePoundage;
        return this;
    }

    public BigDecimal getTraRate () {
        return traRate;
    }

    public VcOnlineOrder setTraRate (BigDecimal traRate) {
        this.traRate = traRate;
        return this;
    }

    public Date getSettleDate () {
        return settleDate;
    }

    public VcOnlineOrder setSettleDate (Date settleDate) {
        this.settleDate = settleDate;
        return this;
    }

    public Integer getSettleStatus () {
        return settleStatus;
    }

    public VcOnlineOrder setSettleStatus (Integer settleStatus) {
        this.settleStatus = settleStatus;
        return this;
    }

    public Integer getStatus () {
        return status;
    }

    public VcOnlineOrder setStatus (Integer status) {
        this.status = status;
        return this;
    }

    public String getRemark () {
        return remark;
    }

    public VcOnlineOrder setRemark (String remark) {
        this.remark = remark;
        return this;
    }

    public Date getPayDate () {
        return payDate;
    }

    public VcOnlineOrder setPayDate (Date payDate) {
        this.payDate = payDate;
        return this;
    }

    public Integer getCurrency () {
        return currency;
    }

    public VcOnlineOrder setCurrency (Integer currency) {
        this.currency = currency;
        return this;
    }

    public BigDecimal getId () {
        return id;
    }

    public VcOnlineOrder setId (BigDecimal id) {
        this.id = id;
        return this;
    }

    /////////////////////////////////////

    public String getCOrder () {
        return cOrder;
    }

    public VcOnlineOrder setCOrder (String cOrder) {
        this.cOrder = cOrder;
        return this;
    }

    public String getCProductDes () {
        return cProductDes;
    }

    public VcOnlineOrder setCProductDes (String cProductDes) {
        this.cProductDes = cProductDes;
        return this;
    }

    public String getCRequestId () {
        return cRequestId;
    }

    public VcOnlineOrder setCRequestId (String cRequestId) {
        this.cRequestId = cRequestId;
        return this;
    }

    public String getCNotifyUrl () {
        return cNotifyUrl;
    }

    public VcOnlineOrder setCNotifyUrl (String cNotifyUrl) {
        this.cNotifyUrl = cNotifyUrl;
        return this;
    }

    public String getCSign () {
        return cSign;
    }

    public VcOnlineOrder setCSign (String cSign) {
        this.cSign = cSign;
        return this;
    }

    public String getPOrder () {
        return pOrder;
    }

    public VcOnlineOrder setPOrder (String pOrder) {
        this.pOrder = pOrder;
        return this;
    }

    public String getPSign () {
        return pSign;
    }

    public VcOnlineOrder setPSign (String pSign) {
        this.pSign = pSign;
        return this;
    }

    public String getPRescode () {
        return pRescode;
    }

    public VcOnlineOrder setPRescode (String pRescode) {
        this.pRescode = pRescode;
        return this;
    }

    public String getPAllRes () {
        return pAllRes;
    }

    public VcOnlineOrder setPAllRes (String pAllRes) {
        this.pAllRes = pAllRes;
        return this;
    }

    public String getOrderDes () {
        return orderDes;
    }

    public VcOnlineOrder setOrderDes (String orderDes) {
        this.orderDes = orderDes;
        return this;
    }

    public BigDecimal getSumMoney () {
        return sumMoney;
    }

    public void setSumMoney (BigDecimal sumMoney) {
        this.sumMoney = sumMoney;
    }

    public BigDecimal getSumTotal () {
        return sumTotal;
    }

    public void setSumTotal (BigDecimal sumTotal) {
        this.sumTotal = sumTotal;
    }

    public Integer getIsAccounted () {
        return isAccounted;
    }

    public VcOnlineOrder setIsAccounted (Integer isAccounted) {
        this.isAccounted = isAccounted;
        return this;
    }

    public Long getChannelId () {
        return channelId;
    }

    public VcOnlineOrder setChannelId (Long channelId) {
        this.channelId = channelId;
        return this;
    }

    public BigDecimal getWaitAmount () {
        return waitAmount;
    }

    public VcOnlineOrder setWaitAmount (BigDecimal waitAmount) {
        this.waitAmount = waitAmount;
        return this;
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

    public String getSmstrxid () {
        return smstrxid;
    }

    public VcOnlineOrder setSmstrxid (String smstrxid) {
        this.smstrxid = smstrxid;
        return this;
    }

    public String getUpMerchNo () {
        return upMerchNo;
    }

    public VcOnlineOrder setUpMerchNo (String upMerchNo) {
        this.upMerchNo = upMerchNo;
        return this;
    }

    public String getUpMerchKey () {
        return upMerchKey;
    }

    public VcOnlineOrder setUpMerchKey (String upMerchKey) {
        this.upMerchKey = upMerchKey;
        return this;
    }


    public BigDecimal getSumTraAmount () {
        return sumTraAmount;
    }


    public void setSumTraAmount (BigDecimal sumTraAmount) {
        this.sumTraAmount = sumTraAmount;
    }


    public BigDecimal getSumTraNum () {
        return sumTraNum;
    }


    public void setSumTraNum (BigDecimal sumTraNum) {
        this.sumTraNum = sumTraNum;
    }


    public BigDecimal getSumCashAmount () {
        return sumCashAmount;
    }


    public void setSumCashAmount (BigDecimal sumCashAmount) {
        this.sumCashAmount = sumCashAmount;
    }


    public BigDecimal getSumCashNum () {
        return sumCashNum;
    }


    public void setSumCashNum (BigDecimal sumCashNum) {
        this.sumCashNum = sumCashNum;
    }


    public BigDecimal getSumProfitAmount () {
        return sumProfitAmount;
    }


    public void setSumProfitAmount (BigDecimal sumProfitAmount) {
        this.sumProfitAmount = sumProfitAmount;
    }

    public List<Long> getChannelList () {
        return channelList;
    }

    public void setChannelList (List<Long> channelList) {
        this.channelList = channelList;
    }

    public Date getBeginCreateDate () {
        return beginCreateDate;
    }

    public void setBeginCreateDate (Date beginCreateDate) {
        this.beginCreateDate = beginCreateDate;
    }

    public Date getEndCreateDate () {
        return endCreateDate;
    }

    public void setEndCreateDate (Date endCreateDate) {
        this.endCreateDate = endCreateDate;
    }
}