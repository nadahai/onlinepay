package com.vc.onlinepay.utils.ninepie;

public class NineConstant {
	
	//编码
	public static final String CHARSET="02";
	//版本号
	public static final String VERSION="1.0";
	//验签算法
	public static final String SIGNTYPE="RSA256";
	//证书路径
	public static final String MERCHANTCERPATH="cert/nine/";
	//证书后缀
	public static final String MERCHANTCERPATH_SUFFIX=".p12";
	//证书
	public static final String ROOTCERTPATH="cert/nine/rootca.cer";

	//商户号----新商户号----旧商户号----美智程商户号
//	,"800001305920004","800059200020002"
	public static final String[] MERCHANIDS = {"800001300020092"};
	//对应证书密码
//	,"pTNvHf","kxfUmh"
	public static final String[] MERCHANIDS_PASS = {"KoWBDM"};
	//通道类型 WXP:微信支付; ALP:支付宝; JDB: 借贷宝; QQP: QQ支付 ;UPOP:银联活码
	public static final String[] SCAN_PAYTYPES = {"WXP","ALP","JDB","QQP","UPOP"};
	//接口编码
	public static final String[] SERVICES = {"qrcodeSpdbPreOrder","singleTransfer","capOrderQuery","merchantAccountQuery"};



	//子商户号
	//public static final String[] SON_MERCHANID={"9000000000028","9000000000029","9000000000030","9000000000032","9000000000033","9000000000034"};
	public static final String[] SON_MERCHANID={"9000000000029","9000000000030","9000000000032","9000000000033","9000000000034","9000000000061",
			"9000000000062","9000000000063","9000000000064","9000000000065","9000000000066","9000000000067","9000000000068","9000000000069"
			,"9000000000070"};
	//请求URL
	public static final String REQUESTURL="https://jd.jiupaipay.com/paygateway/mpsGate/mpsTransaction";
	//请求URL
	public static final String GATEWAYURL="https://jd.jiupaipay.com/paygateway/paygateway/bankPayment";
	//证件类型
	public static final String IDTYPE="00";
	//卡类型,0:储蓄卡1.信用卡
	public static final String CARDTYPR="0";

	public static final String[] RSP_CODE={"IPS00000","MPS00000","MMC00000","RPM00000"};//定义字符串数组
	
	public static final String[] SUCCESS = { "SUCCESS", "success", "OK", "ok" };
	
	public static boolean isHave(String[] strs,String s){ 
		/*此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串 
		* */ 
		for(int i=0;i<strs.length;i++){ 
			if(strs[i].indexOf(s)!=-1){//循环查找字符串数组中的每个字符串中是否包含所有查找的内容 
				return true;//查找到了就返回真，不在继续查询 
			} 
		} 
		return false;//没找到返回false 
	} 
	
}
