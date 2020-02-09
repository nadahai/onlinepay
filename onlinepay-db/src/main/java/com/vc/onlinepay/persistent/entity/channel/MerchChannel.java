/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.entity.channel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vc.onlinepay.enums.PayChannelEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 商户通道配置表Entity
 * @author chaijing
 * @version 2017-05-19
 */
public class MerchChannel{
	public static final Logger logger = LoggerFactory.getLogger(MerchChannel.class);
    private String id;
	private Long merchId;		// 商户ID
	private Long channelId;		// 通道ID
	private String rateName;		// 费率名称
	private Long currency;		// 币种
	private Long settleType;		// 到账类型
	private BigDecimal poundageRate;		// 扣除手续费率
	private BigDecimal tranRate;		// 交易费率
	private Date updateDate;		// 开始 更新时间
	private Date createDate;		// 结束 更新时间
	private long payType;
	private String merchTradeNo;
	private String merchTradeKey;
	private long tradeMode;
	private long status;
	
	private String channelName;
    private String channelName1;
    private String channelNickName;
	private long channelCode;
	private long channelSource;
	private long channelNo;
	private long tradeType;
	private long seq;
    private String payUrl;
    private String serviceCallbackUrl;
    //通道表属性
    private String channelKey;
    private String channelDesKey;
    private BigDecimal maxTraPrice;
    private BigDecimal minTraPrice;
    private BigDecimal dayQuota;
    private BigDecimal channelCost;
    private BigDecimal settleRate;
    private String traStartTime;
    private String traEndTime;
    private int channelType;
    private int subNoStatus;//子商户状态：1：启用 2：禁用
    private List<Long> payTypes = new ArrayList<Long>();
    private Long mangerId;	
	private Long agentId;	
	private BigDecimal agentRate;	
	private BigDecimal mangerRate;

    private long routeChannel;
    private BigDecimal routeMinAmount;
    private BigDecimal routeMaxAmount;
    private String routeRegularEx;
    private String includeMerchno;
    private String excludeMerchno;
    private String accessIp;
    private int keyId;

    public MerchChannel(int keyId,String accessIp) {
        this.keyId=keyId;
        this.accessIp = accessIp;
    }

    public MerchChannel(Long merchId,List<Long> payTypes) {
        this.merchId=merchId;
        this.payTypes = payTypes;
    }

    /**
     * @描述:获取支付方式
     * @作者:nada
     * @时间:2017年12月21日 下午7:03:25
     */
    public static MerchChannel  getMerchChannel(JSONObject reqData) {
        List<Long> list = new ArrayList<>();
        int payType = reqData.getIntValue("payType");
        list.add(Long.valueOf(payType));
        MerchChannel channel = new MerchChannel();
        channel.setPayTypes(list);
        channel.setMerchId(Long.valueOf(reqData.getLong("merchId")));
        return channel;
    }


    public MerchChannel(Long merchId,long payType) {
        this.merchId=merchId;
        this.payType= payType;
    }

    public MerchChannel(long payType,Long settleType,Long channelSource) {
        this.payType = payType;
        this.settleType = settleType;
        this.channelSource = channelSource;
    }
    
    /**
     * @描述:获取网关支付方式工具类
     * @时间:2017年12月21日 下午7:03:25
     */
    public static MerchChannel  getGatewayMerchChannel(JSONObject params) {
        try {
            List<Long> list = new ArrayList<Long>();
            if(params.containsKey("payType") && "1106".equals(params.getString("payType"))){
                list.add(15L);
            }else{
                list.add(9L);
            }
            MerchChannel channel = new MerchChannel();
            channel.setMerchId(params.getLong("merchId"));
            channel.setPayTypes(list);
            return channel;
        } catch (Exception e) {
        	logger.error("获取网关支付方式工具类异常{}",params,e);
            return null;
        }
    }
    
    /**
     * @描述:获取快捷支付方式
     * @时间:2017年12月21日 下午7:03:25
     */
    public static MerchChannel  getQuickMerchChannel(JSONObject requestMap) {
    	try {
	        MerchChannel channel = new MerchChannel();
	        List<Long> list = new ArrayList<Long>();
	        list.add(7L);
	        channel.setPayTypes(list);
	        channel.setMerchId(requestMap.getLong("merchId"));
	        return channel;
		} catch (Exception e) {
			logger.error("获取快捷支付方式工具类异常{}",requestMap,e);
			return null;
		}
    }
    
    /**
     * @描述:获取H5支付方式
     * @时间:2017年12月21日 下午7:03:25
     */
    public static MerchChannel getH5MerchChannel(JSONObject params) {
        try {
        	MerchChannel channel = new MerchChannel();
        	channel.setMerchId(params.getLongValue("merchId"));
            List<Long> list = new ArrayList<Long>();
            if (params.getLong("payType") == 30) {
                list.add(12L);//微信H5
            }else if(params.getLong("payType") == 31){
                list.add(11L);//QQH5
            }else if(params.getLong("payType") == 12){
                list.add(21L);//支付宝服务窗
            }else if(params.getLong("payType") == 22||params.getLong("payType") == 42){
                list.add(10L);//支付宝H5
            }else if(params.getLong("payType") == 34||params.getLong("payType") == 44){
                list.add(16L);//京东H5
            }else{
            	return null;
            }
            channel.setPayTypes(list);
            return channel;
        } catch (Exception e) {
        	logger.error("获取H5支付方式工具类异常{}",params,e);
            return null;
        }
    }
    
    /**
     * @描述:获取扫码通道配置
     * @时间:2018年1月16日 下午12:16:05
     */
    public static MerchChannel getScanMerchChannel(JSONObject reqParams) {
        try {
            String merchId = reqParams.getString("merchId");
            List<Long> list = new ArrayList<Long>();
            if (reqParams.containsKey("service") && "0002".equals(reqParams.getString("service"))) {
                list.add(1L);
            } else if (reqParams.containsKey("service") && "0010".equals(reqParams.getString("service"))) {
                list.add(2L);
            } else if (reqParams.containsKey("service") && "0015".equals(reqParams.getString("service"))) {
                list.add(3L);
                list.add(5L);
            } else if (reqParams.containsKey("service") && "010700".equals(reqParams.getString("service"))) {
                list.add(4L);
            }else if (reqParams.containsKey("service") && "010800".equals(reqParams.getString("service"))) {
                list.add(8L);
            }else if (reqParams.containsKey("service") && "010900".equals(reqParams.getString("service"))){//微信直清扫码:010900
                list.add(20L);
            }else {
                throw new IllegalArgumentException("此服务暂未开通"+reqParams.getString("service"));
            }
            MerchChannel channel = new MerchChannel();
            channel.setPayTypes(list);
            channel.setMerchId(Long.valueOf(merchId));
            return channel;
        } catch (Exception e) {
        	logger.error("获取扫码通道配置工具类异常{}",reqParams,e);
            return null;
        }
    }

    public static MerchChannel  biuldMerchChannel(JSONObject params,PayChannelEnum channelEnum) {
        try {
            List<Long> list = new ArrayList<Long>();
            String payType = "00";
            if(PayChannelEnum.SCAN.getKey().equals(channelEnum.getKey())) {
                payType = params.containsKey("service") ? params.getString("service") : "";
            }else{
                payType = params.containsKey("payType") ? params.getString("payType") : "";
            }
            switch (payType){
                //扫码支付
                case "0002":list.add(1L);break;
                case "0010":list.add(2L);break;
                case "0015":list.add(3L);list.add(5L);break;
                case "010700":list.add(4L);break;
                case "010800":list.add(8L);break;
                case "010900":list.add(20L);break;
                //H5支付
                case "30":list.add(12L);break;
                case "31":list.add(11L);break;
                case "12":list.add(21L);break;
                case "22":
                case "42":list.add(10L);break;
                case "34":
                case "44":list.add(16L);break;
                //网关支付
                case "1106":list.add(15L);break;
                case "1107":
                case "1108":list.add(9L);break;
                //快捷支付
                case "1006":list.add(19L);break;
                case "2006":
                case "1005":list.add(13L);break;
                default:
                    throw new IllegalArgumentException("此服务暂未开通"+payType);
            }
            String merchId = params.getString("merchId");
            MerchChannel channel = new MerchChannel();
            channel.setPayTypes(list);
            channel.setMerchId(Long.valueOf(merchId));
            return channel;
        } catch (Exception e) {
            logger.error("获取支付通道服务编码异常:{}",params,e);
            return null;
        }
    }
   
    /**
     * @描述:获取快捷直联
     * @时间:2017年12月21日 下午7:03:25
     */
    public static MerchChannel  getUnionMerchChannel(JSONObject params) {
        try {
            List<Long> list = new ArrayList<Long>();
            String payType = params.getString("payType");
            if("1006".equals(payType)){
                list.add(19L);//银联直冲Wap
            }else{
                list.add(13L);
            }
            String merchId = params.getString("merchId");
            MerchChannel channel = new MerchChannel();
            channel.setPayTypes(list);
            channel.setMerchId(Long.valueOf(merchId));
            return channel;
        } catch (Exception e) {
            logger.error("获取支付通道服务编码异常:{}",params,e);
            return null;
        }
    }

	public MerchChannel() {
		super();
	}

	public MerchChannel(String id){
	}

    public MerchChannel(Long merchId, long channelSource, long payType, BigDecimal Rate) {
        this.merchId = merchId;
        this.channelSource = channelSource;
        this.payType = payType;
        this.tranRate = Rate;
    }

    public Long getMerchId() {
		return merchId;
	}

	public void setMerchId(Long merchId) {
		this.merchId = merchId;
	}
	
	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}
	
	public String getRateName() {
		return rateName;
	}

	public void setRateName(String rateName) {
		this.rateName = rateName;
	}
	
	public Long getCurrency() {
		return currency;
	}

	public void setCurrency(Long currency) {
		this.currency = currency;
	}
	
	public Long getSettleType() {
		return settleType;
	}

	public void setSettleType(Long settleType) {
		this.settleType = settleType;
	}
	
	
    public BigDecimal getPoundageRate() {
        return poundageRate;
    }

    public void setPoundageRate(BigDecimal poundageRate) {
        this.poundageRate = poundageRate;
    }

    public BigDecimal getTranRate() {
        return tranRate;
    }

    public void setTranRate(BigDecimal tranRate) {
        this.tranRate = tranRate;
    }

    public long getPayType() {
        return payType;
    }

    public void setPayType(long payType) {
        this.payType = payType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMerchTradeNo() {
        return merchTradeNo;
    }

    public void setMerchTradeNo(String merchTradeNo) {
        this.merchTradeNo = merchTradeNo;
    }

    public long getTradeMode() {
        return tradeMode;
    }

    public void setTradeMode(long tradeMode) {
        this.tradeMode = tradeMode;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public long getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(long channelCode) {
        this.channelCode = channelCode;
    }

    public long getChannelSource() {
        return channelSource;
    }

    public void setChannelSource(long channelSource) {
        this.channelSource = channelSource;
    }

    public long getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(long channelNo) {
        this.channelNo = channelNo;
    }

    public long getTradeType() {
        return tradeType;
    }

    public void setTradeType(long tradeType) {
        this.tradeType = tradeType;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public String getServiceCallbackUrl() {
        return serviceCallbackUrl;
    }

    public void setServiceCallbackUrl(String serviceCallbackUrl) {
        this.serviceCallbackUrl = serviceCallbackUrl;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

	public List<Long> getPayTypes() {
        return payTypes;
    }

    public void setPayTypes(List<Long> payTypes) {
        this.payTypes = payTypes;
    }

    public String getChannelName1() {
        return channelName1;
    }

    public void setChannelName1(String channelName1) {
        this.channelName1 = channelName1;
    }
    public BigDecimal getMaxTraPrice() {
        return maxTraPrice;
    }

    public void setMaxTraPrice(BigDecimal maxTraPrice) {
        this.maxTraPrice = maxTraPrice;
    }

    public BigDecimal getMinTraPrice() {
        return minTraPrice;
    }

    public void setMinTraPrice(BigDecimal minTraPrice) {
        this.minTraPrice = minTraPrice;
    }

    public String getChannelNickName() {
        return channelNickName;
    }

    public void setChannelNickName(String channelNickName) {
        this.channelNickName = channelNickName;
    }

    public BigDecimal getSettleRate() {
        return settleRate;
    }

    public void setSettleRate(BigDecimal settleRate) {
        this.settleRate = settleRate;
    }

    public String getTraStartTime() {
        return traStartTime;
    }

    public void setTraStartTime(String traStartTime) {
        this.traStartTime = traStartTime;
    }

    public String getTraEndTime() {
        return traEndTime;
    }

    public void setTraEndTime(String traEndTime) {
        this.traEndTime = traEndTime;
    }

    public String getChannelKey() {
        return channelKey;
    }

    public void setChannelKey(String channelKey) {
        this.channelKey = channelKey;
    }

    public String getChannelDesKey() {
        return channelDesKey;
    }

    public void setChannelDesKey(String channelDesKey) {
        this.channelDesKey = channelDesKey;
    }

    public BigDecimal getDayQuota() {
        return dayQuota;
    }

    public void setDayQuota(BigDecimal dayQuota) {
        this.dayQuota = dayQuota;
    }

    public BigDecimal getChannelCost() {
        return channelCost;
    }

    public void setChannelCost(BigDecimal channelCost) {
        this.channelCost = channelCost;
    }

	public int getChannelType() {
		return channelType;
	}

	public void setChannelType(int channelType) {
		this.channelType = channelType;
	}

	public int getSubNoStatus() {
		return subNoStatus;
	}

	public void setSubNoStatus(int subNoStatus) {
		this.subNoStatus = subNoStatus;
	}

	public String getMerchTradeKey() {
		return merchTradeKey;
	}

	public void setMerchTradeKey(String merchTradeKey) {
		this.merchTradeKey = merchTradeKey;
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

    public long getRouteChannel() {
        return routeChannel;
    }

    public void setRouteChannel(long routeChannel) {
        this.routeChannel = routeChannel;
    }

    public BigDecimal getRouteMinAmount() {
        return routeMinAmount;
    }

    public void setRouteMinAmount(BigDecimal routeMinAmount) {
        this.routeMinAmount = routeMinAmount;
    }

    public BigDecimal getRouteMaxAmount() {
        return routeMaxAmount;
    }

    public void setRouteMaxAmount(BigDecimal routeMaxAmount) {
        this.routeMaxAmount = routeMaxAmount;
    }

    public String getRouteRegularEx() {
        return routeRegularEx;
    }

    public void setRouteRegularEx(String routeRegularEx) {
        this.routeRegularEx = routeRegularEx;
    }

    public String getIncludeMerchno() {
        return includeMerchno;
    }

    public void setIncludeMerchno(String includeMerchno) {
        this.includeMerchno = includeMerchno;
    }

    public String getExcludeMerchno() {
        return excludeMerchno;
    }

    public void setExcludeMerchno(String excludeMerchno) {
        this.excludeMerchno = excludeMerchno;
    }

    public String getAccessIp() {
        return accessIp;
    }

    public void setAccessIp(String accessIp) {
        this.accessIp = accessIp;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }
}