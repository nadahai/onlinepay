/* @(#) StringUtil.java
 * Copyright(c) 2008 HUATENG. All Right Reserver.
 * 2008-11-28 create by LEO.YAN
 */
package com.vc.onlinepay.utils;

import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;


/**
 * 字符串工具类 String Utilities.
 */
public class StringUtil {

	private static final String VC_ENCRYPTION = "vcEncryption";
	
	/**
	 * 判断object是否为空
	 * 
	 * @param object
	 *            Object对象
	 * @return 布尔倄1�7
	 */
	private static boolean isNull(Object object) {
		if (object instanceof String) {
			return StringUtil.isEmpty(object.toString());
		}
		return object == null;
	}
	/**
	 * 获取随即生成的32位UUID
	 * @return 32位UUID
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * Checks if string is null or empty.
	 * 
	 * @param value
	 *            The string to be checked
	 * @return True if string is null or empty, otherwise false.
	 */
	public static boolean isEmpty(final String value) {
		return value == null || value.trim().length() == 0
				|| "null".endsWith(value);
	}

	public static String null2String(Object obj) {
		return obj == null ? "" : obj.toString();
	}

	public static String null2String(String str) {
		return str == null ? "" : str;
	}

	/**
	 * 填充字符
	 * @param value
	 * @param len
	 * @param fillValue
	 * @return
	 */
	public static String fillValue(String value, int len, char fillValue) {
		String str = (value == null) ? "" : value.trim();
		StringBuilder result = new StringBuilder();
		result.append(str);
		int paramLen = str.length();
		if (paramLen < len) {
			for (int i = 0; i < len - paramLen; i++) {
				result.append(fillValue);
			}
		}
		return result.toString();
	}

	/**
	 * 在value后变插入count次appendValue
	 * 
	 * @param value
	 * @param count
	 *            插入的次敄1�7
	 * @param appendValue
	 * @return
	 */
	public static String appendValue(String value, int count, String appendValue) {
		if (count < 1) {
			return value;
		}
		StringBuilder result = new StringBuilder();
		result.append(value);
		for (int i = 0; i < count; i++) {
			result.append(appendValue);
		}
		return result.toString();
	}

	/**
	 * 填充字符
	 * 
	 * @param value
	 * @param len
	 * @param fillValue
	 * @return
	 */
	public static String beforFillValue(String value, int len, char fillValue) {
		String str = (value == null) ? "" : value.trim();
		StringBuilder result = new StringBuilder();
		int paramLen = str.length();
		if (paramLen < len) {
			for (int i = 0; i < len - paramLen; i++) {
				result.append(fillValue);
			}
		}
		result.append(str);
		return result.toString();
	}

	/**
	 * 格式化金预1�7
	 * 
	 * @param amount
	 *            金额
	 * @return
	 */
	public static String convertAmount(String amount) {
		String str = String.valueOf(Double.parseDouble(amount));
		int pos = str.indexOf(".");
		int len = str.length();
		if (len - pos < 3) {
			return str.substring(0, pos + 2) + "0";
		} else {
			return str.substring(0, pos + 3);
		}
	}

	/**
	 * currency fomate
	 * 
	 * @param currency
	 * @return
	 */
	public static String formatCurrency(String currency) {
		if ((null == currency) || "".equals(currency)
				|| "null".equals(currency)) {
			return "";
		}

		NumberFormat usFormat = NumberFormat.getCurrencyInstance(Locale.CHINA);
		try {
			return usFormat.format(Double.parseDouble(currency));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 根据separator拆分text
	 * 
	 * @param text
	 *            霄1�7拆分的字符串 String
	 * @param separator
	 *            拆分表达弄1�7 String
	 * @return String[]
	 */
	public static String[] split(String text, String separator) {
		return split(text, separator, -1);
	}

	/**
	 * Splits the provided text into a list, based on a given separator. The
	 * separator is not included in the returned String array. The maximum
	 * number of splits to perfom can be controlled. A null separator will cause
	 * parsing to be on whitespace.
	 * <p>
	 * <p>
	 * This is useful for quickly splitting a string directly into an array of
	 * tokens, instead of an enumeration of tokens (as
	 * <code>StringTokenizer</code> does).
	 * 
	 * @param str
	 *            The string to parse.
	 * @param separator
	 *            Characters used as the delimiters. If <code>null</code>,
	 *            splits on whitespace.
	 * @param max
	 *            The maximum number of elements to include in the list. A zero
	 *            or negative value implies no limit.
	 * @return an array of parsed Strings
	 */
	public static String[] split(String str, String separator, int max) {
		StringTokenizer tok = null;
		if (separator == null) {
			// Null separator means we're using StringTokenizer's default
			// delimiter, which comprises all whitespace characters.
			tok = new StringTokenizer(str);
		} else {
			tok = new StringTokenizer(str, separator);
		}

		int listSize = tok.countTokens();
		if (max > 0 && listSize > max) {
			listSize = max;
		}

		String[] list = new String[listSize];
		int i = 0;
		int lastTokenBegin = 0;
		int lastTokenEnd = 0;
		while (tok.hasMoreTokens()) {
			if (max > 0 && i == listSize - 1) {
				String endToken = tok.nextToken();
				lastTokenBegin = str.indexOf(endToken, lastTokenEnd);
				list[i] = str.substring(lastTokenBegin);
				break;
			}
			list[i] = tok.nextToken();
			lastTokenBegin = str.indexOf(list[i], lastTokenEnd);
			lastTokenEnd = lastTokenBegin + list[i].length();
			i++;
		}
		return list;
	}

	/**
	 * Replace all occurances of a string within another string.
	 * 
	 * @param text
	 *            text to search and replace in
	 * @param repl
	 *            String to search for
	 * @param with
	 *            String to replace with
	 * @return the text with any replacements processed
	 * @see #replace(String text, String repl, String with, int max)
	 */
	public static String replace(String text, String repl, String with) {
		return replace(text, repl, with, -1);
	}

	/**
	 * Replace a string with another string inside a larger string, for the
	 * first <code>max</code> values of the search string. A <code>null</code>
	 * reference is passed to this method is a no-op.
	 * 
	 * @param text
	 *            text to search and replace in
	 * @param repl
	 *            String to search for
	 * @param with
	 *            String to replace with
	 * @param max
	 *            maximum number of values to replace, or <code>-1</code> if
	 *            no maximum
	 * @return the text with any replacements processed
	 * @throws NullPointerException
	 *             if repl is null
	 */
	private static String replace(String text, String repl, String with, int max) {
		if (text == null) {
			return null;
		}
		StringBuilder buf = new StringBuilder(text.length());
		int start = 0;
		int end = text.indexOf(repl, start);
		while (end != -1) {
			buf.append(text.substring(start, end)).append(with);
			start = end + repl.length();

			if (--max == 0) {
				break;
			}
			end = text.indexOf(repl, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	public static String first2Upper(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static String arrayToDelimitedString(Object[] arr, String delim) {

		if (arr == null || arr.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append('\'');
			sb.append(arr[i]);
			sb.append('\'');
		}
		return sb.toString();
	}

	/**
	 * e.g: String[] result={"TYHR0001","TYHR0002"}; split=","; return:
	 * str="TYHR0001,TYHR0002";
	 * @param split
	 * @return String
	 */
	public static String arrayToStr(Object[] arr, char split) {
		if (arr == null || arr.length == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(split);
			}
			sb.append(arr[i]);

		}
		return sb.toString();
	}

	/**
	 * 将数组的每个元素后加入split，然后组成字符串返回
	 * 
	 * @param arr
	 *            字符串数组1�7
	 * @param split
	 *            插入字符
	 * @return
	 */
	public static String arrayToStr(Object[] arr, String split) {
		if (arr == null || arr.length == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(split);
			}
			sb.append(arr[i]);

		}
		return sb.toString();
	}

	/**
	 * ȡ�õ�ǰϵͳ获取本地时间
	 * 
	 * @param style
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getcurrdate(String style) {
		Date currDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(style); // "yyyy-MM-dd
		// HH:mm:ss"
        return sdf.format(currDate);
	}

	/**
	 * 得到字符串中某个字符出现的次敄1�7
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static int getCharCount(String str, char c) {

		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == c) {
                count++;
            }
		}
		return count;
	}

	public static boolean isNumeric(String str) {
		if(isNull(str)) {
            return false;
        }
		for (int i = 0; i<str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static String trim(String str) {
		if(str == null) {
			return "";
		}
		return str.trim().replaceAll(" ", "").replaceAll("　", "");
	}
	
	public static String substr(String str,int index) {
		if(str.length() < index) {
			return str;
		}
		str = trim(str).substring(index);
		return str;
	}
	
	public static String substr(String str,int beginIndex,int endIndex) {
		if(str.length() < beginIndex || str.length() < endIndex || beginIndex > endIndex) {
			return str;
		}
		return trim(str).substring(beginIndex, endIndex);
	}
	
	/**
	 * 判断信息是否为空
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		if(str != null && !"".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
	}
	/**
	 * 比较2个日期相差的天数
	 * @param fDate
	 * @param sDate
	 * @return fDate-sDate
	 */
	public static int daysOfTwo(String fDate, String sDate) {
	       Calendar aCalendar = Calendar.getInstance();
	       aCalendar.setTime(string2Date(fDate,"yyyyMMdd"));
	       int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       aCalendar.setTime(string2Date(sDate,"yyyyMMdd"));
	       int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       return day1-day2;
	    }
	/**
	 * 将字符串转换为日期
	 * @param date 字符串
	 * @param dayFormat 字符串格式
	 * @return
	 */
	public static Date string2Date(String date,String dayFormat) {
		DateFormat format = new SimpleDateFormat(dayFormat); 
		try {
			return format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}	
	
	/**
	 * 获取逗号右边的字符串
	 * @param res
	 * @param subString
	 * @return
	 */
	public static String getCommadRight(String res,String subString){
    	String[] merkk=res.split(",");
		String memberId="";
		String[] members;
        for (String s : merkk) {
            //循环查找字符串数组中的每个字符串中是否包含所有查找的内容
            if (s.contains(subString)) {
                members = s.split("=");
                memberId = members[1];
            }
        }
		return memberId;
    }

	/**
	 * 获取重定向参数
	 * @param params
	 * @return
	 */
	public static String jsonToMap(JSONObject params){
	    if(params!=null && params.containsKey(VC_ENCRYPTION)){
	        return VC_ENCRYPTION+"="+params.getString(VC_ENCRYPTION);
	    }
	    if(params!=null && (params.containsKey("merchantId") || params.containsKey("merchantNo"))){
	        return VC_ENCRYPTION+"="+ HiDesUtils.desEnCode(params.toString());
	    }
    	return params.toString();
    }
	
	/**
	 * String转map
	 * @param desStr
	 * @return
	 */
	public static Map<String, String> getMapParams(String desStr){
		Map<String, String> map=new HashMap<String, String>();
        String[] resp=desStr.split("&");
        for (String string : resp) {
        	String[] params=string.split("=");
        	if (params.length==2) {
        		map.put(params[0].trim(), params[1].trim());
			}else {
				map.put(params[0].trim(), "");
			}
		}
    	return map;
    }
    
    /**
     * 封装key=value&参数
     * @param proneMap
     * @return
     */
    public static String getString(Map<String,String> proneMap){
		StringBuilder builder=new StringBuilder();
		for (Map.Entry<String, String> entry : proneMap.entrySet()) { 
		  builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String params=builder.toString();
		params=params.substring(0, params.length()-1);
		return params;
	}
    
    /**根据数组和字符串获取另一个数组的值
	 * remark
	 * @param name
	 * @return
	 */
	public static String getName2(String name,String[] array1,String[] array2){
		try {
			int k=0;
			/*此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串
			* */
			for(int i=0;i<array1.length;i++){
				if(name.equals(String.valueOf(array1[i]))){//循环查找字符串数组中的每个字符串中是否包含所有查找的内容
					k=i;//查找到了就返回真，不在继续查询
					break;
				}
			}
			return array2[k];//没找到返回false
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * 中文数据
	 * @param source
	 * @return
	 */
	public static String encodeUnicode(String source){
		StringBuilder sb = new StringBuilder();
		char [] sourceCharArray = source.toCharArray();
		String unicode = null;
        for (char c : sourceCharArray) {
            unicode = Integer.toHexString(c);
            if (unicode.length() <= 2) {
                unicode = "00" + unicode;
            }
            sb.append("\\u").append(unicode);
        }
		return sb.toString();
	}
	/**
	 * unicode转中文数据
	 * @param unicode
	 * @return
	 */
	public static String decodeUnicode(String unicode) {
		StringBuilder sb = new StringBuilder();

		String[] hex = unicode.split("\\\\u");

		for (int i = 1; i < hex.length; i++) {
			int data = Integer.parseInt(hex[i], 16);
			sb.append((char) data);
		}
		return sb.toString();
	}

	/**
	 * @des 生成二维码 GoogleApi
	 *  //http://chart.apis.google.com/chart? ： 调用Google API
	 * 	//cht=qr ：选择生产QR码
	 * 	//chs=<width>x<height> ：需要生成的二维码的尺寸
	 * 	//chld=<error_correction_level>|<margin> ：纠错等级。QR码支持四个等级的纠错，用来恢复丢失的、读错的、模糊的、数据。
	 * 	// L-(默认)可以识别已损失7%的数据；M-可以识别已损失15%的数据；Q-可以识别已损失25%的数据；H-可以识别已损失30%的数据
	 * 	//choe=<output_encoding>：声明生成的二维码所包含信息的编码，默认是 UTF-8 ；其他可选编码是 Shift_JIS 、 ISO-8859-1
	 * 	//&chl= ：需要生成二维码的内容
	 * @return
	 */
	public static String getQrcodeFromGoogle(String qrcode,int width){
		int margin = 0;
		return "http://chart.apis.google.com/chart?cht=qr&chs=" + width + "x" + width + "&chld=H|" + margin + "&chl=" + qrcode;
	}
	public static String getQrcodeFromGoogle(String qrcode){
		return getQrcodeFromGoogle(qrcode,240);
	}
    /** 2019-01-22失效*/
	public static String getQrcodeFromTencent(String qrcode){
		return "http://mobile.qq.com/qrcode?url=" + qrcode;
	}
    public static String getQrcodeFromLiantu(String qrcode){
        return "http://qr.liantu.com/api.php?text=" + qrcode;
    }
    public static String getQrcodeFromQrserver(String qrcode){
        return "https://api.qrserver.com/v1/create-qr-code/?size=240x240&data=" + qrcode;
    }

	public static String getQrcodeBase64(String qrcode){
		try {
			String imgsrc = HttpClientTools.getBase64ByUrl(StringUtil.getQrcodeFromGoogle(qrcode));
			return "data:image/png;base64," + imgsrc;
		} catch (Exception e) {
            System.out.println("二维码转Base64编码失败");
            e.printStackTrace();
			return getQrcodeFromQrserver(qrcode);
		}
	}



    public static String buildAlipayUrl(String qrcode) {
//		String head = "https://render.alipay.com/p/s/i?scheme=";
		String head = "https://ds.alipay.com/?from=mobilecodec&scheme=";
//		String appUrl = "alipays://platformapi/startapp?appId=20000067&url=";
		String appUrl = "alipays://platformapi/startapp?appId=10000007&qrcode=";
		return head + URLUtil.encode(appUrl + URLUtil.encode(qrcode+"?_s=web-other"));
    }
}
