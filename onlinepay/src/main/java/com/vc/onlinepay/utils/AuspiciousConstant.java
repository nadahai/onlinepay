package com.vc.onlinepay.utils;

import com.jnewsdk.util.Base64;
import com.jnewsdk.util.StringUtils;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuspiciousConstant {


	// 需内容做Base64加密
	private static final String[] base64Keys = new String[]{"subject", "body", "remark"};
	// 需内容做Base64加密,并所有子域采用json数据格式
	private static final String[] base64JsonKeys = new String[]{"customerInfo", "accResv", "riskRateInfo", "billQueryInfo", "billDetailInfo"};
	private static final String URL_PARAM_CONNECT_FLAG = "&";
	public static final Set<String> base64Key = new HashSet<String> ();

	static {
		base64Key.add ("subject");
		base64Key.add ("body");
		base64Key.add ("remark");
		base64Key.add ("customerInfo");
		base64Key.add ("accResv");
		base64Key.add ("riskRateInfo");
		base64Key.add ("billpQueryInfo");
		base64Key.add ("billDetailInfo");
		base64Key.add ("respMsg");
		base64Key.add ("resv");
	}

	/**
	 * 转换特殊字段
	 */
	public static void converData (Map<String, String> paramMap) {
		for (int i = 0; i < base64Keys.length; i++) {
			String key = base64Keys[i];
			String value = paramMap.get (key);
			if (StringUtils.isNotEmpty (value)) {
				try {
					String text = new String (Base64.encode (value.getBytes (StandardCharsets.UTF_8)));
					// 更新请求参数
					paramMap.put (key, text);
				} catch (Exception e) {
				}
			}
		}
		for (int i = 0; i < base64JsonKeys.length; i++) {
			String key = base64JsonKeys[i];
			String value = paramMap.get (key);
			if (StringUtils.isNotEmpty (value)) {
				try {
					String text = new String (Base64.encode (value.getBytes (StandardCharsets.UTF_8)));
					// 更新请求参数
					paramMap.put (key, text);
				} catch (Exception e) {
				}
			}
		}
	}

	//IP地址
	public static final String[] REQUESTIP={"103.236.255.203","103.27.4.182"};
	//超时返回信息
	public static final String TIMEOUT_MESSAGE="银行返回数据超时，订单设置为代付中，已经启用定时查询抓取状态,有结果我们会马上反馈！";
	//微辰商户id key
	public static final String vcMerchId="vcMerchId";
	//微辰订单号key
	public static final String vcOrderNo="vcOrderNo";
	//微辰商户手续费key
	public static final String vcServiceCharge="vcServiceCharge";
	//微辰商户可用金额key
	public static final String usableAmount="usableAmount";
	//微辰商户代付金额key
	public static final String amount="amount";
	//微辰商户订单key
	public static final String orderNo="orderNo";

	//爱农网银
	public static final String GATEWAY_MERCHANID="929020095022782";
	//爱农网银 MD5 Key
	public static final String GATEWAY_KEY="mGZNh2b6NfXApbRU3aAg3kxWR666wzgN";
//	//爱农网银
//	public static final String GATEWAY_MERCHANID="929020095023089";
//	//爱农网银 MD5 Key
//	public static final String GATEWAY_KEY="tkezfG6frTvJaRNNzVcbZH4GCkZuHZKV";

	//支付请求URL
	public static final String GATEWAYURL="http://gpay.chinagpay.com/bas/FrontTrans";
	//快捷支付请求URL
	public static final String QUICKURL="http://api.chinagpay.com/bas/BgTrans";
	//代付请求URL
	public static final String TRANSFERURL="http://remit.chinagpay.com/bas/BgTrans";
	//爱农快捷跳转地址
	public static final String  AUSPICIOUS_SKIP_URL="/jsp/third/auspicious/auspiciousQuick.jsp";
	//网关标识
	public static final String GATEREMARK="AUSPICIOUSGATE";
	//吉店扫码商户号
	public static final String SACNCODE_MERCHANID="929060095072593";
	//吉店扫码 Key
	public static final String SACNCODE_KEY="FA27171E8C4B77EF00AB2186CF514810";
	//吉店扫码 渠道号
	public static final String SACNCODE_CHANNEL="600000000240";
	//吉店扫码 地址
	public static final String SACNCODE_URL="https://front.jlishop.com/org.ac";
	//银行简称
	public static final String[] bankAbbr={"PSBC","ICBC","CMB","ABC","CCB","BOB","BCOM","CIB","CMBC",
			"CEB","BOC","PABC","CITIC","SDB","GDB","BOFS","SPDB","HXB","BRCB","BON","NBCB"};
	//银行编号
	public static final String[] bankId={"01000000","01020000","03080000","01030000","01050000","04031000",
			"03010000","03090000","03050000","03030000","01040000","04100000","03020000","03070000","03060000",
			"04012900","03100000","03040000","14181000","04243010","04083320"};
}
