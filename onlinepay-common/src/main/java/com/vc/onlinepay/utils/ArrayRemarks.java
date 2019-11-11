package com.vc.onlinepay.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class ArrayRemarks {
	
	/** 所有标记 */
	public static final String[] ALL_REMARKS = {
	        "LUCKYQQ", "LUCKYJD", "LUCKYYL", "TOLLCHANNELWXP", "TOLLCHANNELALP",
			"TOLLCHANNELYL", "MAGICQUICK", "SKYPEQUICK", "MAGICYL", "BIGMERCHANTALPH5", "BIGMERCHANTGATE", "INPAYWXP",
			"INPAYALP", "INPAYQQ", "NINEPIEGATE", "BIGMERCHANTWXP", "BIGMERCHANTALP", "BIGMERCHANTQQH5",
			"BIGMERCHANTQQ", "SAFETYWXP", "SAFETYALP", "SAFETYQQ", "UNIMPEDEDGATE", "NINEPIEQUICK", "AUSPICIOUSGATE",
			"AUSPICIOUSQUICK", "MAGICYL", "TONGLIANWXP", "TONGLIANQQ", "PERMANENTWXP", "PERMANENTALP", "REMITTANCEQQH5",
			"REMITTANCEALPH5", "NINEPIEWXP", "NINEPIEALP", "REMITTANCEWXPH5" ,"FAMOUSQUICK","NINEGATE","NINEQUICK"};
    /** 通道id */
	private static final Long[] CHANNEL_ID = { 62L, 64L, 63L, 72L, 73L, 74L, 66L, 76L, 75L, 27L, 28L, 42L, 43L, 44L, 41L,
			23L, 24L, 26L, 30L, 46L, 47L, 48L, 29L, 33L, 60L, 61L, 65L, 70L, 71L, 35L, 36L, 57L, 56L, 51L, 52L, 59L,66L,
			41L,33L};
	
	/** 银行标记 */
	private static final String[] BANK_NAME = { "中国工商银行", "建设银行", "中国农业银行", "中国邮政储蓄银行", "中国银行", "交通银行", "招商银行", "光大银行",
			"浦发银行", "华夏银行", "广东发展银行", "中信银行", "兴业银行", "民生银行", "杭州银行", "上海银行", "宁波银行", "平安银行", "江苏银行", "浙江泰隆商业银行",
			"济宁银行", "台州银行", "汉口银行", "安徽省农村信用社联合社", "郑州银行", "中原银行", "宜宾商业银行", "莱商银行", "日照银行", "常熟农商银行", "北京农商银行",
			"福建省农村信用社联合社", "齐商银行", "云南省农村信用社联合社", "山东省农村信用社联合社", "广东华兴银行", "江西银行", "东营银行", "浙江稠州商业银行", "重庆农村商业银行",
			"晋城银行", "秦农银行", "长安银行", "成都银行", "工商银行", "中国建设银行", "农业银行", "邮政储蓄银行", "中国民生银行" };
	
	/** 银行编号 */
	private static final int[] BANK_CODE = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 26, 38, 39,
			40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 1, 2, 3, 4, 14 };

	private static Map<String,String> bankCodeMap = new LinkedHashMap<String ,String>();
	private static Map<String,String> bankShortId = new LinkedHashMap<String ,String>();
	private static Map<String,String> Shuangqian_bankShortId = new LinkedHashMap<String ,String>();

	static {
		bankCodeMap.put("上海农村商业","SRCB");
		bankCodeMap.put("中国银行","BOC");
		bankCodeMap.put("深圳发展","SDB");
		bankCodeMap.put("北京农商","BJRCB");
		bankCodeMap.put("工商","ICBC");
		bankCodeMap.put("农业","ABC");
		bankCodeMap.put("建设","CCB");
		bankCodeMap.put("交通","BCOM");
		bankCodeMap.put("招商","CMB");
		bankCodeMap.put("广发","GDB");
		bankCodeMap.put("中信","CITIC");
		bankCodeMap.put("民生","CMBC");
		bankCodeMap.put("光大","CEB");
		bankCodeMap.put("平安","PABC");
		bankCodeMap.put("邮政","PSBC");
		bankCodeMap.put("华夏","HXB");
		bankCodeMap.put("兴业","CIB");
		bankCodeMap.put("浦发","SPDB");
		bankCodeMap.put("浦东","SPDB");
		bankCodeMap.put("北京","BOB");
		bankCodeMap.put("渤海","CBHB");
		bankCodeMap.put("南京","NJCB");
		bankCodeMap.put("东亚","BEA");
		bankCodeMap.put("宁波","NBCB");
		bankCodeMap.put("杭州","HZB");
		bankCodeMap.put("徽商","HSB");
		bankCodeMap.put("浙商","CZB");
		bankCodeMap.put("上海","SHB");
		bankCodeMap.put("广州","GZCB");
		bankCodeMap.put("大连","DLB");
		bankCodeMap.put("江苏","JSB");
		bankCodeMap.put("银联","UPOP");


		bankShortId.put("工商", "102");
		bankShortId.put("农业", "103");
		bankShortId.put("中国银行", "104");
		bankShortId.put("建设", "105");
		bankShortId.put("交通", "301");
		bankShortId.put("中信", "302");
		bankShortId.put("光大", "303");
		bankShortId.put("华夏", "304");
		bankShortId.put("民生", "305");
		bankShortId.put("广发", "306");
		bankShortId.put("平安", "307");
		bankShortId.put("招商", "308");
		bankShortId.put("兴业", "309");
		bankShortId.put("浦发", "310");
		bankShortId.put("北京银行", "313");
		bankShortId.put("恒丰银行", "315");
		bankShortId.put("浙商银行", "316");
		bankShortId.put("渤海银行", "318");
		bankShortId.put("上海银行", "325");
		bankShortId.put("南京", "390");
		bankShortId.put("邮政", "403");
		bankShortId.put("徽商", "440");
		bankShortId.put("商业", "441");
		bankShortId.put("东亚", "502");

		Shuangqian_bankShortId.put("工商", "1001");
		Shuangqian_bankShortId.put("农业", "1002");
		Shuangqian_bankShortId.put("中国银行", "1003");
		Shuangqian_bankShortId.put("建设", "1004");
		Shuangqian_bankShortId.put("交通", "1005");
		Shuangqian_bankShortId.put("中信", "1006");
		Shuangqian_bankShortId.put("光大", "1007");
		Shuangqian_bankShortId.put("华夏", "1008");
		Shuangqian_bankShortId.put("民生", "1009");
		Shuangqian_bankShortId.put("广发", "1010");
		Shuangqian_bankShortId.put("平安", "1011");
		Shuangqian_bankShortId.put("招商", "1012");
		Shuangqian_bankShortId.put("兴业", "1013");
		Shuangqian_bankShortId.put("浦发", "1014");
		Shuangqian_bankShortId.put("北京银行", "1015");
		Shuangqian_bankShortId.put("天津银行", "1016");
		Shuangqian_bankShortId.put("恒丰银行", "1118");
		Shuangqian_bankShortId.put("浙商银行", "1119");
		Shuangqian_bankShortId.put("渤海银行", "1121");
		Shuangqian_bankShortId.put("上海银行", "1040");
		Shuangqian_bankShortId.put("南京银行", "1041");
		Shuangqian_bankShortId.put("邮政", "1141");
		Shuangqian_bankShortId.put("徽商银行", "1122");
		Shuangqian_bankShortId.put("东亚", "1142");
	}


	/**
	 * remark
	 * @param s
	 * @return
	 */
	public static Long getChannelId(String s,int num){
		String suffix="";
		switch (num) {
	        case 1:
	        	suffix = "WXP";
	            break;
	        case 2:
	        	suffix = "ALP";
	            break;
	        case 3:
	        	suffix = "QQ";
	            break;
	        case 4:
	        	suffix = "JD";
	            break;
	        case 5:
	        	suffix = "WXPOP";
	            break;
	        case 6:
	        	suffix = "ALPOP";
	            break;
	        case 7:
	        	suffix = "QUICK";
	            break;
	        case 8:
	        	suffix = "YL";
	            break;
	        case 9:
	        	suffix = "GATE";
	            break;
	        case 10:
	        	suffix = "ALPH5";
	            break;
	        case 11:
	        	suffix = "QQH5";
	            break;
	        case 12:
	        	suffix = "WXPH5";
	            break;
	        default:
	        	suffix = "";
	            break;
	           
		}
		String remark=s+suffix;
		int k=0;
		/*此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串 
		* */
		for(int i=0;i<ALL_REMARKS.length;i++){ 
			//循环查找字符串数组中的每个字符串中是否包含所有查找的内容
			if(ALL_REMARKS[i].contains(remark)){
				//查找到了就返回真，不在继续查询
				k=i;
				break;
			} 
		} 
		return CHANNEL_ID[k];//没找到返回false 
	}
	
	/**
	 * @param remark
	 * @return
	 */
	public static Long getSimpleChannelId(String remark){
		
		int k=0;
        //此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串
        for(int i=0;i<ALL_REMARKS.length;i++){
			//循环查找字符串数组中的每个字符串中是否包含所有查找的内容
			if(ALL_REMARKS[i].contains(remark)){
				//查找到了就返回真，不在继续查询
				k=i;
				break;
			} 
		} 
		//没找到返回false
		return CHANNEL_ID[k];
	}
	
	/**获取银行id编码
	 * @param remark
	 * @return
	 */
	public static int getBankCode(String remark){
		
		int k=0;
		/*此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串 
		* */
		for(int i=0;i<BANK_NAME.length;i++){ 
			if(BANK_NAME[i].contains(remark)){//循环查找字符串数组中的每个字符串中是否包含所有查找的内容
				k=i;//查找到了就返回真，不在继续查询 
				break;
			} 
		} 
		return BANK_CODE[k];//没找到返回false 
	}

	/**获取银行名称
	 * @param code
	 * @return
	 */
	public static String getBankName(String code){

		int k=0;
		/*此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串
		* */
		for(int i=0;i<BANK_CODE.length;i++){
			if(code.equals(String.valueOf(BANK_CODE[i]))){//循环查找字符串数组中的每个字符串中是否包含所有查找的内容
				k=i;//查找到了就返回真，不在继续查询
				break;
			}
		}
		return BANK_NAME[k];//没找到返回false
	}
	/**
	 * @描述:获取银行英文简称
	 * @作者:ChaiJing THINK
	 * @时间:2018/9/4 16:37
	 */
	public static String getbankCodeByName(String bankName){
		for(String key:bankCodeMap.keySet()){
			if(bankName.contains(key)){
				System.out.println("银行编码:"+bankName+"------>"+bankCodeMap.get(key));
				return bankCodeMap.get(key);
			}
		}
		return "UPOP";
	}
    /**
     * @描述:获取银行简码
     * @作者:ChaiJing THINK
     * @时间:2018/9/5 9:44
     */
	public static String getbankShortIdByName(String bankName){
		for(String key:bankShortId.keySet()){
			if(bankName.contains(key)){
				return bankShortId.get(key);
			}
		}
		return "102";
	}

	/**
	 * 双乾获取银行编码
	 * @param bankName
	 * @return
	 */
	public static String getShuangqianBankShortIdByName(String bankName){
		for(String key:Shuangqian_bankShortId.keySet()){
			if(bankName.contains(key)){
				return Shuangqian_bankShortId.get(key);
			}
		}
		return "1003";
	}
}
