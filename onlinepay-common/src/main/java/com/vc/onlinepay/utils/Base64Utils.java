package com.vc.onlinepay.utils;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.Map.Entry;


public class Base64Utils {
	/**
	 * 文件读取缓冲区大小
	 */
	private static final int CACHE_SIZE = 1024;

	/**
	 * <p>
	 * BASE64字符串解码为二进制数据
	 * </p>
	 *
	 * @param base64
	 * @return
	 * @throws Exception
	 */
	public static byte[] decode(String base64) throws Exception {
		return Base64.decode(base64.getBytes());
	}

	/**
	 * <p>
	 * 二进制数据编码为BASE64字符串
	 * </p>
	 *
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static String encode(byte[] bytes) throws Exception {
		return new String(Base64.encode(bytes));
	}

	/**
	 * <p>
	 * 将文件编码为BASE64字符串
	 * </p>
	 * <p>
	 * 大文件慎用，可能会导致内存溢出
	 * </p>
	 *
	 * @param filePath
	 *            文件绝对路径
	 * @return
	 * @throws Exception
	 */
	public static String encodeFile(String filePath) throws Exception {
		byte[] bytes = fileToByte(filePath);
		return encode(bytes);
	}

	/**
	 * <p>
	 * BASE64字符串转回文件
	 * </p>
	 *
	 * @param filePath
	 *            文件绝对路径
	 * @param base64
	 *            编码字符串
	 * @throws Exception
	 */
	public static void decodeToFile(String filePath, String base64)
			throws Exception {
		byte[] bytes = decode(base64);
		byteArrayToFile(bytes, filePath);
	}

	/**
	 * <p>
	 * 文件转换为二进制数组
	 * </p>
	 *
	 * @param filePath
	 *            文件路径
	 * @return
	 * @throws Exception
	 */
	public static byte[] fileToByte(String filePath) throws Exception {
		byte[] data = new byte[0];
		File file = new File(filePath);
		if (file.exists()) {
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
			byte[] cache = new byte[CACHE_SIZE];
			int nRead = 0;
			while ((nRead = in.read(cache)) != -1) {
				out.write(cache, 0, nRead);
				out.flush();
			}
			out.close();
			in.close();
			data = out.toByteArray();
		}
		return data;
	}

	/**
	 * <p>
	 * 二进制数据写文件
	 * </p>
	 *
	 * @param bytes
	 *            二进制数据
	 * @param filePath
	 *            文件生成目录
	 */
	public static void byteArrayToFile(byte[] bytes, String filePath)
			throws Exception {
		InputStream in = new ByteArrayInputStream(bytes);
		File destFile = new File(filePath);
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		destFile.createNewFile();
		OutputStream out = new FileOutputStream(destFile);
		byte[] cache = new byte[CACHE_SIZE];
		int nRead = 0;
		while ((nRead = in.read(cache)) != -1) {
			out.write(cache, 0, nRead);
			out.flush();
		}
		out.close();
		in.close();
	}
	public static void main(String[] args) {
		String str = "{\"s\":\"WAP\",\"n\":\"京东官网\",\"id\":\"https://m.jd.com\"}";
		try {
			System.out.println(java.net.URLEncoder.encode(encode(str.getBytes("GBK")),"GBK"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	protected static String bytesToString(byte[] encrytpByte) {
		String result = "";
		for (Byte bytes : encrytpByte) {
			result += bytes.toString() + " ";
		}
		return result;
	}

	protected static byte[] splitToString(String tmp) {
		String[] strArr = tmp.split(" ");
		int len = strArr.length;
		System.out.println("len=" + len);
		byte[] clone = new byte[len];
		for (int i = 0; i < len; i++) {
			clone[i] = Byte.parseByte(strArr[i]);
		}
		return clone;
	}

	public static String stringToHexString(String strPart) {
		String hexString = "";
		for (int i = 0; i < strPart.length(); i++) {
			int ch = (int) strPart.charAt(i);
			String strHex = Integer.toHexString(ch);
			hexString = hexString + strHex;
		}
		return hexString;
	}

	private static String hexString = "0123456789ABCDEF";

	/*
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/*
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public static String hexToString(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2) {
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        }
		return new String(baos.toByteArray());
	}

	private static byte uniteBytes(byte src0, byte src1) {
		byte byte1 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		byte1 = (byte) (byte1 << 4);
		byte byte2 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (byte1 | byte2);
		return ret;
	}

	public static byte[] hexString2Bytes(String src) {
		byte[] ret = new byte[6];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < 6; ++i) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * MD5加base64加密
	 * @param parameters
	 * @param key
	 * @return
	 */
	public static String sign64(SortedMap<String, Object> parameters, String key) {
		List<String> sb = new ArrayList<>();
		Set<Entry<String, Object>> es = parameters.entrySet();
        for (Entry<String, Object> entry : es) {
            String k = entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"signMethod".equals(k) && !"signature".equals(k)) {
                sb.add(k + "=" + v);
            }
        }
		byte[] digest = null;
		String str = StringUtils.join(sb, "&") + key;
		System.out.println(str);
		try {
	        byte[] data = (str).getBytes(StandardCharsets.UTF_8);
	        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
	        messageDigest.reset();
	        messageDigest.update(data);
	        digest = messageDigest.digest();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(digest));
	}

	/**
	 * SortedMap转成String
	 * @param parameters
	 * @return
	 */
	public static String sortedToString(SortedMap<String, Object> parameters) {
		List<String> sb = new ArrayList<>();
		Set<Entry<String, Object>> es = parameters.entrySet();
        for (Entry<String, Object> entry : es) {
            String k = entry.getKey();
            Object v = entry.getValue();
            sb.add(k + "=" + v);
        }
        return StringUtils.join(sb, "&");
	}

}
