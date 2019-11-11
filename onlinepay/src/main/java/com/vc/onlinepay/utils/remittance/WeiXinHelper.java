package com.vc.onlinepay.utils.remittance;

import com.vc.onlinepay.utils.Md5Util;
import java.net.URLEncoder;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;

public class WeiXinHelper {

	//MD5字符串拼接加密
	public static String signMd5(String key,WeiXinPayModel model){
		StringBuilder sbString=new StringBuilder();
		sbString.append("version="+model.getVersion())
			.append("&agent_id="+model.getAgentId())
			.append("&agent_bill_id="+model.getAgentBillId())
			.append("&agent_bill_time="+model.getAgentBillTime())
			.append("&pay_type="+model.getPayType())
			.append("&pay_amt="+model.getPayAmt())
			.append("&notify_url="+model.getNotifyUrl())
			.append("&return_url="+model.getReturnUrl())
			.append("&user_ip="+model.getUserIp())
			.append("&key="+key);
		return Md5Util.MD5(sbString.toString()).toLowerCase();
	}
	//MD5字符串拼接加密
	public static String queryMd5(String key,WeiXinPayModel model){
		StringBuilder sbString=new StringBuilder();
		sbString.append("version="+model.getVersion())
			.append("&agent_id="+model.getAgentId())
			.append("&agent_bill_id="+model.getAgentBillId())
			.append("&agent_bill_time="+model.getAgentBillTime())
			.append("&return_mode="+model.getReturnMode())
			.append("&key="+key);
		return Md5Util.MD5(sbString.toString()).toLowerCase();
	}
	//MD5字符串拼接加密
	public static String getsignString(String key,WeiXinPayModel model){
		StringBuilder sbString=new StringBuilder();
		sbString.append("version="+model.getVersion())
			.append("&agent_id="+model.getAgentId())
			.append("&agent_bill_id="+model.getAgentBillId())
			.append("&agent_bill_time="+model.getAgentBillTime())
			.append("&pay_type="+model.getPayType())
			.append("&pay_amt="+model.getPayAmt())
			.append("&amp;notify_url="+model.getNotifyUrl())
			.append("&return_url="+model.getReturnUrl())
			.append("&user_ip="+model.getUserIp())
			.append("&key="+key);
		return sbString.toString().toLowerCase();
	}
	//提交地址
	public static String gatewaySubmitUrl(String sign,WeiXinPayModel model){
		StringBuilder sbString=new StringBuilder();
		sbString.append(RemittanceConstant.WEBPAGE_REQUESTURL);
		sbString.append("?version="+model.getVersion())
		.append("&agent_id="+model.getAgentId())
		.append("&agent_bill_id="+model.getAgentBillId())
		.append("&agent_bill_time="+model.getAgentBillTime())
		.append("&pay_type="+model.getPayType())
		.append("&pay_amt="+model.getPayAmt())
		.append("&notify_url="+model.getNotifyUrl())
		.append("&return_url="+model.getReturnUrl())
		.append("&user_ip="+model.getUserIp())
		.append("&goods_name="+model.getGoodsName())
		.append("&goods_num="+model.getGoodsNum())
		.append("&goods_note="+model.getGoodsNote())
		.append("&remark="+model.getRemark())
		.append("&meta_option="+model.getMetaOption())
		.append("&is_phone="+model.getIsPhone())
		.append("&is_frame="+model.getIsFrame())
		.append("&sign="+sign);
		return sbString.toString();
	}
	//提交地址
	public static String alipaySubmitUrl(String sign,WeiXinPayModel model){
		StringBuilder sbString=new StringBuilder();
		sbString.append(RemittanceConstant.WEBPAGE_REQUESTURL);
		sbString.append("?version="+model.getVersion())
		.append("&agent_id="+model.getAgentId())
		.append("&agent_bill_id="+model.getAgentBillId())
		.append("&agent_bill_time="+model.getAgentBillTime())
		.append("&pay_type="+model.getPayType())
		.append("&pay_amt="+model.getPayAmt())
		.append("&notify_url="+model.getNotifyUrl())
		.append("&return_url="+model.getReturnUrl())
		.append("&user_ip="+model.getUserIp())
		.append("&goods_name="+model.getGoodsName())
		.append("&goods_num="+model.getGoodsNum())
		.append("&goods_note="+model.getGoodsNote())
		.append("&remark="+model.getRemark())
		.append("&is_phone="+model.getIsPhone())
		.append("&sign="+sign);
		return sbString.toString();
	}
	//提交地址
	public static String querySubmitUrl(String sign,WeiXinPayModel model){
		StringBuilder sbString=new StringBuilder();
		sbString.append("version="+model.getVersion())
		.append("&agent_id="+model.getAgentId())
		.append("&agent_bill_id="+model.getAgentBillId())
		.append("&agent_bill_time="+model.getAgentBillTime())
		.append("&return_mode="+model.getReturnMode())
		.append("&remark="+model.getRemark())
		.append("&sign="+sign);
		return sbString.toString();
	}

	//扫码提交地址
	public static String scanPaySubmitUrl(WeiXinPayModel model) throws Exception{
		String merch = model.getAgentId();
		String key = model.getMd5Key();
		String meta_option = model.getMetaOption()==null?"":model.getMetaOption();
		if(StringUtils.isNotEmpty(meta_option)){
			meta_option = URLEncoder.encode(Base64.getEncoder().encodeToString(meta_option.getBytes("GBK")),"UTF-8");
		}
		String goods = URLEncoder.encode(model.getGoodsName(),"GB2312");

		StringBuilder sbString = new StringBuilder();
		sbString.append(RemittanceConstant.WEBPAGE_REQUESTURL)
				.append("?version="+model.getVersion())
				.append("&pay_type="+model.getPayType())
				.append("&agent_id="+merch)
				.append("&agent_bill_id="+model.getAgentBillId())
				.append("&agent_bill_time="+model.getAgentBillTime())
				.append("&pay_amt="+model.getPayAmt())
				.append("&notify_url="+model.getNotifyUrl())
				.append("&return_url="+model.getReturnUrl())
				.append("&user_ip="+model.getUserIp())
				.append("&goods_name="+goods)
				.append("&goods_num="+model.getGoodsNum())
				.append("&goods_note="+goods)
				.append("&meta_option="+meta_option)
				.append("&remark="+model.getAgentBillId());
		if(!StringUtils.isAnyEmpty(model.getIsPhone(),model.getIsFrame())){
			sbString.append("&is_phone="+model.getIsPhone()).append("&is_frame="+model.getIsFrame());
		}

		StringBuilder sign_sb = new StringBuilder();
		sign_sb.append("version").append("=").append(model.getVersion()).append("&")
				.append("agent_id").append("=").append(merch).append("&")
				.append("agent_bill_id").append("=").append(model.getAgentBillId()) .append("&")
				.append("agent_bill_time").append("=").append(model.getAgentBillTime()) .append("&")
				.append("pay_type").append("=").append(model.getPayType()).append("&")
				.append("pay_amt").append("=").append(model.getPayAmt()).append("&")
				.append("notify_url").append("=").append(model.getNotifyUrl()).append("&")
				.append("return_url").append("=").append(model.getReturnUrl()).append("&")
				.append("user_ip").append("=").append(model.getUserIp()).append("&")
				.append("key").append("=").append(key);
		String   sign =  SmallTools.md5(sign_sb.toString());
		sbString.append("&sign="+sign);
		return sbString.toString();
	}


}
