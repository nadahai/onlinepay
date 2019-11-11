package com.vc.onlinepay.utils.remittance;


import java.io.UnsupportedEncodingException;

public class PiLiangService {

	//获取提交签名串
	public static String GetSignMd5(PiFuModel model) throws UnsupportedEncodingException{
		
		StringBuilder sbSign=new StringBuilder();
		sbSign.append("agent_id="+model.getAgent_id())
			.append("&batch_amt="+model.getBatch_amt())
			.append("&batch_no="+model.getBatch_no())
			.append("&batch_num="+model.getBatch_num())
			.append("&detail_data="+model.getDetail_data())
			.append("&ext_param1="+model.getExt_param1())
			.append("&key="+model.getKey())
			.append("&notify_url="+model.getNotify_url())
			.append("&version="+model.getVersion());
		return sbSign.toString().toLowerCase();
	}
	
	//获取查询MD5签名串
	public static String GetQueryM5D(PiFuModel model){
		StringBuilder sbSign=new StringBuilder();
		sbSign.append("agent_id=" + model.getAgent_id())
	        .append("&batch_no=" + model.getBatch_no()) 
	        .append("&key=" + model.getKey())
	        .append("&version=3");
		return sbSign.toString().toLowerCase();
	}
	//查询地址
	public static String QueryOrderUrl(PiFuModel model,String sign){
		StringBuilder queryUrl=new StringBuilder();
		queryUrl.append("version=3")
	        .append("&agent_id=" + model.getAgent_id())
	        .append("&batch_no=" + model.getBatch_no())
	        .append("&sign=" + sign);
		return queryUrl.toString();
	}
	public static String PostQueryOrderUrl(PiFuModel model,String sign){
		StringBuilder queryUrl=new StringBuilder();
		queryUrl.append("version=3")
	        .append("&agent_id=" + model.getAgent_id())
	        .append("&batch_no=" + model.getBatch_no())
	        .append("&sign=" + sign);
		return queryUrl.toString();
	}
}
