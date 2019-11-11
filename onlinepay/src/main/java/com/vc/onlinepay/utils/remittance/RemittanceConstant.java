package com.vc.onlinepay.utils.remittance;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;

public class RemittanceConstant {
	//H5支付请求URL
	public static final String WEBPAGE_REQUESTURL="https://pay.heepay.com/Payment/Index.aspx";
	//支付查询请求URL
	public static final String PAY_QUERYURL="https://query.heepay.com/Payment/Query.aspx";
	//代付查询请求URL
	public static final String TRANSFER_QUERYURL="https://Pay.heepay.com/API/PayTransit/QueryTransfer.aspx";
	//小额代付请求URL
	public static final String REPLACELOWPAY_URL="https://Pay.heepay.com/API/PayTransit/PayTransferWithSmallAll.aspx";
	//大额代付请求URL
	public static final String REPLACEUPPAY_URL="https://Pay.heepay.com/API/PayTransit/PayTransferWithLargeWork.aspx";
	//对接返回URL	
	public static final String BALANCE_URL="https://www.heepay.com/API/Merchant/QueryBank.aspx";
	//商户号
	public static final String MERCHANID="2106233";
	//秘钥
	public static final String MERCHANID_KEY="DD1303970B364B22A5596E38";
	//代付秘钥
	public static final String TRANSFER_MERCHANID_KEY="DAFE69504F5A41EC861C5B07";
	//3DES代付秘钥
	public static final String TRANSFER_3DES_KEY="A508B81333C94B43B5481FF3";


	//汇付宝商户号
	public static final String[] REMIT_MERCH={"2106233","2111541","2113106","2114064"};
	//汇付宝秘钥
	public static final String[] REMIT_KEY={"DD1303970B364B22A5596E38","4CD07C8C4B204C6F921F574C","FE881782502F4BE691DC9E67","1D400D2A2F4B4E6ABB92156B"};
	//汇付宝代付V3秘钥
	public static final String[] TRANSFER_REMIT_KEY={"DAFE69504F5A41EC861C5B07","47FAEF0083444F0690D9FF76","DE84B85EE4674D82AF6BA6B3",""};
	//汇付宝代付3DES秘钥
	public static final String[] TRANSFER_REMIT_3DES_KEY={"A508B81333C94B43B5481FF3","95843972D5F94593B335AAA6","70ED00A37E3249AEB2E53A50",""};
	//汇付宝代付理由
	public static final String[] TRANSFER_REMIT_REASON={"上游厂商结算款","厂家结算款"};
	//代付查询请求URL
	public static final String TRANSFER_QUERYURL_UNION="https://open.heepay.com/transferQuery.do";
	//代付请求URL
	public static final String REPLACELOWPAY_URL_UNION="https://open.heepay.com/transferApply.do";
	//商户账户查询
	public static final String BALANCEAMOUNT_QUERYURL_UNION="https://open.heepay.com/merchantAccountQuery.do";


	//汇付宝银联直冲代付账号
	public static final String MERCHANID_UNION="100719";
	//汇付宝银联直冲代付秘钥
	public static final String MERCHANID_KEY_UNION="7b12317bc198b206b0a59c4e1ddb2c3f";



	private static String SIGNATURE_RCV = "sign";
	private static String SIGNATURE_SEND = "signature";
	private static String SIGNATURE_METHOD = "signMethod";
	/***
	 * @描述:MD5 签名
	 * @作者:nada
	 * @时间:2018/12/20
	 **/
	public static String md5 (String text, String charset) throws UnsupportedEncodingException {

		byte[] bs = text.getBytes (charset);
		String signature = DigestUtils.md5Hex (bs);

		return signature;
	}

	/**
	 * @描述:MD5 签名签名
	 * @作者:nada
	 * @时间:2018/12/20
	 **/
	public static String signatueContainKey (Map<String, String> queryMap, String md5Key) throws UnsupportedEncodingException {
		StringBuilder stringBuilder = new StringBuilder ();
		for (Map.Entry<String, String> entry : queryMap.entrySet ()) {
			String key = entry.getKey ();
			if ((!key.equals (SIGNATURE_SEND)) && (!key.equals (SIGNATURE_RCV)) && (!key.equals (SIGNATURE_METHOD))) {
				stringBuilder.append (entry.getKey () + "=" + entry.getValue () + "&");
			}
		}
		stringBuilder.append ("key=").append (md5Key);
		String buildString = stringBuilder.toString ();
		String signature = md5 (buildString, "utf-8").toUpperCase ();
		return signature;
	}


}
