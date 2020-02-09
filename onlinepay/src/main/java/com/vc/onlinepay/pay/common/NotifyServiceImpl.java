package com.vc.onlinepay.pay.common;

import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.channel.MerchChannel;
import com.vc.onlinepay.persistent.entity.merch.MessageModel;
import com.vc.onlinepay.persistent.entity.online.VcOnlineLog;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.entity.online.VcOnlineThirdBalance;
import com.vc.onlinepay.persistent.monitor.AsynNotice;
import com.vc.onlinepay.persistent.service.channel.MerchChannelServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineLogServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineThirdBalanceServiceImpl;
import com.vc.onlinepay.utils.DateUtils;
import com.vc.onlinepay.utils.http.HttpBrowserTools;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Service
public class NotifyServiceImpl{
    private static final Logger logger = LoggerFactory.getLogger(NotifyServiceImpl.class);
    
    @Autowired
    private MerchChannelServiceImpl merchChannelService;
    
    @Autowired
    private VcOnlineThirdBalanceServiceImpl vcOnlineThirdBalanceService;
    
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;
    
    @Autowired
    private AsynNotice asynNotice;
    
    @Autowired
    private VcOnlineLogServiceImpl onlineLogService;
    
    /**
     *  交易回调IP验证
     * @return
     */
    public String checkIpAddressForTrade(VcOnlineOrder vcOnlineOrder, HttpServletRequest request) {
    	String orderNo = "";
    	String ipAddress = "";
    	try {
    		ipAddress = HttpBrowserTools.getIpAddr (request);
    		long channelId = vcOnlineOrder.getChannelId(); 		
    		MerchChannel merchChannel = merchChannelService.getAccessIpByChannelId((int)channelId);
    		String accessIp = StringUtils.isNotBlank(merchChannel.getAccessIp())?merchChannel.getAccessIp():"";
    		orderNo = vcOnlineOrder.getOrderNo();
    		String type = coreEngineProviderService.getCacheCfgKey (CacheConstants.ONLINE_NOTIFY_CHECK_TYPE);
    		if(StringUtils.isNoneBlank(type) && type.equals("checkIp")){
    			return checkIpAddress(ipAddress, String.valueOf(channelId), accessIp, orderNo,"trade");
    		}else if(StringUtils.isNotBlank(type) && type.equals("addIp")){
    			return addIpAddress(ipAddress, "trade", accessIp, orderNo, (int)channelId);
    		}else{
    			return "success";
    		}
        }catch (Exception e){
        	logger.info("交易回调IP验证异常,orderNo:{},ipAddress:{},异常{}",orderNo,ipAddress,e);
        	return "error";
        }
    }
    
    
    /**
     *  代付回调IP验证
     * @return
     */
    public String checkIpAddressForTransfer(VcOnlinePayment vcOnlinePayment, HttpServletRequest request) {
    	String orderNo = "";
    	String ipAddress = "";
    	try {
    		ipAddress = HttpBrowserTools.getIpAddr (request);
    		int channelId = vcOnlinePayment.getChannelId();
    		long sourceId = vcOnlinePayment.getChannelSource();
    		VcOnlineThirdBalance vcOnlineThirdBalance =
    				vcOnlineThirdBalanceService.getAccessIpByChannelId(new VcOnlineThirdBalance((int)sourceId,channelId));
    		String accessIp = StringUtils.isNotBlank(vcOnlineThirdBalance.getAccessIp())?vcOnlineThirdBalance.getAccessIp():"";
    		orderNo = vcOnlinePayment.getOrderNo();
    		String type = coreEngineProviderService.getCacheCfgKey (CacheConstants.ONLINE_NOTIFY_CHECK_TYPE);
    		if(StringUtils.isNotBlank(type) && type.equals("checkIp")){
    			return checkIpAddress(ipAddress, String.valueOf(channelId), accessIp, orderNo,"transfer");
    		}else if(StringUtils.isNotBlank(type) && type.equals("addIp")){
    			return addIpAddress(ipAddress, "transfer", accessIp, orderNo, vcOnlineThirdBalance.getId().intValue());
    		}
    		else{
    			return "success";
    		}
        }catch (Exception e){
        	logger.info("代付回调IP验证异常,orderNo:{},ipAddress:{},异常{}",orderNo,ipAddress,e);
        	return "error";
        }
    }
    
    
    
    /**
     *  验证回调IP
     * @return
     */
    public String checkIpAddress(String ipAddress,String channelId,String accessIp,String orderNo,String type) {
    	try {
    		type=type.equals("trade")?"交易":"代付";
        	if(StringUtils.isBlank(ipAddress)){
        		logger.info("回调IP为空,orderNo:{},ipAddress:{}",accessIp,ipAddress);
        		//异步发送消息到CMS+日志保存回调IP
            	sendMsgAndSaveLog(type+"回调IP为空", orderNo, channelId, "", "");
        		return "error";
        	}
        	if(StringUtils.isNotBlank(accessIp) && accessIp.contains(ipAddress)){
        		return "success";
        	}
        	logger.info("通道:{},回调IP列表:{},orderNo:{}",channelId,accessIp,orderNo);
        	
        	//异步发送消息到CMS+日志保存回调IP
        	sendMsgAndSaveLog(type+"回调IP验证失败", orderNo, channelId, ipAddress, "");
               
    		return "error";
        }catch (Exception e){
        	logger.info("回调IP验证异常,orderNo:{},ipAddress:{},异常{}",orderNo,ipAddress,e);
        	return "error";
        }
    }
    
    /**
     *  添加验证IP
     * @return
     */
    public String addIpAddress(String ipAddress,String type,String accessIp,String orderNo,int keyId) {
    	try {
        	if(StringUtils.isBlank(ipAddress)){
        		logger.info("回调IP为空,orderNo:{},ipAddress:{}",orderNo,ipAddress);
        		return "error";
        	}
        	if(StringUtils.isNotBlank(accessIp)){
        		if(!accessIp.contains(ipAddress)){
        			accessIp=accessIp+","+ipAddress;
        		}
        	}else{
        		accessIp=ipAddress;
        	} 
        	int nums = 0;
        	if(type.equals("trade")){
        		nums = merchChannelService.updateAccessIpByChannelId(new MerchChannel(keyId, accessIp));
        	}else{
        		nums = vcOnlineThirdBalanceService.updateAccessIpByChannelId(new VcOnlineThirdBalance(BigDecimal.valueOf(keyId),accessIp));
        	}
        	if(nums<1){
        		return "error";
        	}
        	return "success";
        }catch (Exception e){
        	logger.info("添加回调IP异常,orderNo:{},ipAddress:{},异常{}",orderNo,ipAddress,e);
        	return "error";
        }
    }
    
    /**
     *  异步通知CMS,log日志保存
     * @return
     */
    public void sendMsgAndSaveLog(String title,String orderNo,String channelId,String ipAddress,String accessIp) {
    	try {
    		//异步发送消息到CMS
    		String message = "订单号: "+orderNo+"  通道ID: "+channelId+" 回调IP: "+ipAddress;
        	MessageModel model = new MessageModel(title,message);
        	asynNotice.asyncMsgNotice(model);
        	//保存当前回调IP
        	onlineLogService.save(new VcOnlineLog(title,"回调Ip:"+ipAddress,"accessIp: "+accessIp,"订单号: "+orderNo));
        }catch (Exception e){
        	logger.info("异步通知CMS,log日志保存,orderNo:{},异常{}",orderNo,e);
        }
    }

	/**
	 * @描述 验证回调时间是否超过2h
	 * @return
	 */
	public boolean checkNotifyDate(VcOnlineOrder vcOnlineOrder) {
		try {
			long msec = System.currentTimeMillis()-vcOnlineOrder.getCreateDate().getTime();
			if(msec>7200000){
				long paramMiao = msec/1000;
				long h = paramMiao/3600;
				long paramMiao2 = paramMiao%3600;
				long m = paramMiao2/60;
				long s = paramMiao2%60;
				String diffDate = h+"小时"+m+"分钟"+s+"秒";
				logger.info("回调时间超过2小时 订单号{},距离下单时间{}",diffDate);
				//异步发送消息到CMS
				String message = "订单号: "+vcOnlineOrder.getOrderNo()+"  通道ID: "+vcOnlineOrder.getChannelId()+" 距离下单时间: "+diffDate;
				MessageModel model = new MessageModel("验证回调时间",message);
				asynNotice.asyncMsgNotice(model);
				//保存当前回调IP
				onlineLogService.save(new VcOnlineLog("验证回调时间","回调时间:"+ DateUtils.getTimeForY_M_D_H_m_s(),"距离下单时间: "+diffDate,"订单号: "+vcOnlineOrder.getOrderNo()));
				return false;
			}
			return true;
		}catch (Exception e){
			logger.info("验证回调时间异常,orderNo:{},异常{}",vcOnlineOrder.getOrderNo(),e);
			return false;
		}
	}
}
