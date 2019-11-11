/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.merch;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商户报备Entity
 * @author liuxu
 * @version 2017-05-19
 */
public class MerchInfo implements Serializable{
    private static final long serialVersionUID = 2181675568436984621L;
    private Long id;
	private String uuid;		// UUID
    private String name;        // 商户名称
    private String shopName;        // 店铺名称
    private String shopAddr;        // 店铺地址
    private String busLicenseNo;    // 营业执照号
    private String loginName;       // 登陆名称
    private String password;        // 登陆密码
    private Long parentId;      // 所属上级
    private Long merchNo;       // 商户编号
    private String industry;        // 所属行业
    private String subIndustry;     // 所属子行业
    private Long merchLevel;        // 等级
    private String levelNo;     // 等级编号
    private String levelViewNo;     // 显示等级编号
    private Long merchSource;       // 商户来源
    private Long merchType;     // 商户类型
    private String legalPerson;     // 商户法人
    private String idCardNo;        // 身份证号
    private String phoneNo;     // 手机号
    private Long merchCategory;     // 商户类别
    private Long regionId;      // 所属大区ID
    private Long provinceId;        // 开户省ID
    private Long cityId;        // 开户市ID
    private String bankNo;      // 银行卡号
    private String linkNo;      // 银行卡号
    private String CardType;      // 银行卡号
    
    private Long accountType;       // 账户类型
    private String accountName;     // 开户名
    private Long bankId;        // 开户银行ID
    private Long subBankId;     // 开户支行ID
    private String bankPhoneNo;     // 银行绑定手机号
    private Long status;        // 代理商状态
    private String createById;      // 创建者
    private String updateById;      // 更新者
    private String shopAddress;     // 详细地址
    private Long countyId;      // 区县ID
	private String address;     // 详细地址
	private String no;          //BB卡号
	private Date createDate;
	private int isSecurity;
	private String isNotice;
    private BigDecimal usableTraMoney;
    private BigDecimal tra_total_amount;

	private String bankName;
	private String subBankName;
	private String orderNo;
	private BigDecimal payAmonut;
	private String paymoney;
	private List<Long> payTypes = new ArrayList<Long>();
	private BigDecimal actualAmonut;
	private String clientIp;
	
	//快捷支付
	private String quick;
	private String orderNum;//快捷支付订单号
	private String payBankNo;//支付银行卡号
	private String traType;
	private String mccId;

    private String cOrder;
    private String cNotifyUrl;//下游回调
    private String pNotifyUrl;//上游回调地址
	
	private Long starLevel;//级别
	private String upperLicense;//上游商户号和秘钥用##分离
	private String ipAddress;

    private String openid;

    private int isCashier;
    private int replaceMode;
    private String replaceChannel;
    private int checkRate;//是否验证费率 1:验证;2:不验证

	public MerchInfo(){
    }

    public static String getReplaceMode(MerchInfo merchInfo){
        switch (merchInfo.getReplaceMode()) {
            case 1:case 3:
                return  "T0";
            case 2:case 4:
                return "T1";
            default :
                return "";
        }
    }

	public MerchInfo(Long id){
	    this.id = id;
	}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddr() {
        return shopAddr;
    }

    public String getCardType() {
		return CardType;
	}

	public void setCardType(String cardType) {
		CardType = cardType;
	}

	public void setShopAddr(String shopAddr) {
        this.shopAddr = shopAddr;
    }

    public String getBusLicenseNo() {
        return busLicenseNo;
    }


    public String getPaymoney() {
        return paymoney;
    }

    public void setPaymoney(String paymoney) {
        this.paymoney = paymoney;
    }

    public void setBusLicenseNo(String busLicenseNo) {
        this.busLicenseNo = busLicenseNo;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getMerchNo() {
        return merchNo;
    }

    public void setMerchNo(Long merchNo) {
        this.merchNo = merchNo;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getSubIndustry() {
        return subIndustry;
    }

    public void setSubIndustry(String subIndustry) {
        this.subIndustry = subIndustry;
    }

    public Long getMerchLevel() {
        return merchLevel;
    }

    public void setMerchLevel(Long merchLevel) {
        this.merchLevel = merchLevel;
    }

    public String getLevelNo() {
        return levelNo;
    }

    public void setLevelNo(String levelNo) {
        this.levelNo = levelNo;
    }

    public String getLevelViewNo() {
        return levelViewNo;
    }

    public void setLevelViewNo(String levelViewNo) {
        this.levelViewNo = levelViewNo;
    }

    public Long getMerchSource() {
        return merchSource;
    }

    public void setMerchSource(Long merchSource) {
        this.merchSource = merchSource;
    }

    public Long getMerchType() {
        return merchType;
    }

    public void setMerchType(Long merchType) {
        this.merchType = merchType;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Long getMerchCategory() {
        return merchCategory;
    }

    public void setMerchCategory(Long merchCategory) {
        this.merchCategory = merchCategory;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public Long getAccountType() {
        return accountType;
    }

    public void setAccountType(Long accountType) {
        this.accountType = accountType;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getSubBankId() {
        return subBankId;
    }

    public void setSubBankId(Long subBankId) {
        this.subBankId = subBankId;
    }

    public String getBankPhoneNo() {
        return bankPhoneNo;
    }

    public void setBankPhoneNo(String bankPhoneNo) {
        this.bankPhoneNo = bankPhoneNo;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getCreateById() {
        return createById;
    }

    public void setCreateById(String createById) {
        this.createById = createById;
    }

    public String getUpdateById() {
        return updateById;
    }

    public void setUpdateById(String updateById) {
        this.updateById = updateById;
    }

    public Long getCountyId() {
        return countyId;
    }

    public void setCountyId(Long countyId) {
        this.countyId = countyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getSubBankName() {
        return subBankName;
    }

    public void setSubBankName(String subBankName) {
        this.subBankName = subBankName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getPayAmonut() {
        return payAmonut;
    }

    public void setPayAmonut(BigDecimal payAmonut) {
        this.payAmonut = payAmonut;
    }

    public List<Long> getPayTypes() {
        return payTypes;
    }

    public void setPayTypes(List<Long> payTypes) {
        this.payTypes = payTypes;
    }

    public BigDecimal getActualAmonut() {
        return actualAmonut;
    }

    public void setActualAmonut(BigDecimal actualAmonut) {
        this.actualAmonut = actualAmonut;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

	public String getQuick() {
		return quick;
	}

	public void setQuick(String quick) {
		this.quick = quick;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getPayBankNo() {
		return payBankNo;
	}

	public void setPayBankNo(String payBankNo) {
		this.payBankNo = payBankNo;
	}

	public String getTraType() {
		return traType;
	}

	public void setTraType(String traType) {
		this.traType = traType;
	}

    public String getLinkNo() {
        return linkNo;
    }

    public void setLinkNo(String linkNo) {
        this.linkNo = linkNo;
    }
	public Long getStarLevel() {
		return starLevel;
	}
	public void setStarLevel(Long starLevel) {
		this.starLevel = starLevel;
	}
	public String getUpperLicense() {
		return upperLicense;
	}
	public void setUpperLicense(String upperLicense) {
		this.upperLicense = upperLicense;
	}
    public String getMccId() { return mccId;
    }

    public void setMccId(String mccId) { this.mccId = mccId;
    }
    public String getcOrder() {
        return cOrder;
    }
    public void setcOrder(String cOrder) {
        this.cOrder = cOrder;
    }
    public String getcNotifyUrl() {
        return cNotifyUrl;
    }
    public void setcNotifyUrl(String cNotifyUrl) {
        this.cNotifyUrl = cNotifyUrl;
    }

    public String getpNotifyUrl() {
        return pNotifyUrl;
    }
    public void setpNotifyUrl(String pNotifyUrl) {
        this.pNotifyUrl = pNotifyUrl;
    }
    public int getIsSecurity() {
        return isSecurity;
    }
    public void setIsSecurity(int isSecurity) {
        this.isSecurity = isSecurity;
    }
    public String getOpenid() {
        return openid;
    }
    public void setOpenid(String openid) {
        this.openid = openid;
    }
    public int getIsCashier() {
        return isCashier;
    }
    public void setIsCashier(int isCashier) {
        this.isCashier = isCashier;
    }
    public String getIsNotice() {
        return isNotice;
    }
    public void setIsNotice(String isNotice) {
        this.isNotice = isNotice;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public int getReplaceMode() {
        return replaceMode;
    }
    public void setReplaceMode(int replaceMode) {
        this.replaceMode = replaceMode;
    }
	public String getReplaceChannel() {
		return replaceChannel;
	}
	public void setReplaceChannel(String replaceChannel) {
		this.replaceChannel = replaceChannel;
	}

    public int getCheckRate() {
        return checkRate;
    }

    public BigDecimal getUsableTraMoney() {
        return usableTraMoney;
    }

    public void setUsableTraMoney(BigDecimal usableTraMoney) {
        this.usableTraMoney = usableTraMoney;
    }

    public void setCheckRate(int checkRate) {
        this.checkRate = checkRate;
    }

	public BigDecimal getTra_total_amount() {
		return tra_total_amount;
	}

	public void setTra_total_amount(BigDecimal tra_total_amount) {
		this.tra_total_amount = tra_total_amount;
	}
    
}