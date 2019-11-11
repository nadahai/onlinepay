package com.vc.onlinepay.utils;
import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author none
 */
public class RSAUtils {
	private static final String UTF_8 = "utf-8";
	private static final String KEY_ALGORITHM_RSA = "RSA";
	//RSA最大加密明文大小
	private static final int MAX_ENCRYPT_BLOCK = 117;
	//RSA最大解密密文大小
	private static final int MAX_DECRYPT_BLOCK = 128;
	//签名算法
	private static final String SIGN_MD5WITHRSA = "MD5withRSA";
	private static final String SIGN_SHA1WITHRSA = "SHA1WithRSA";
	private static final String SIGN_SHA256WITHRSA = "SHA256WITHRSA";

// ---------------------生成RSA证书工具---------------------------------------
	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivateKey";
	/**
	 * @描述:生成密钥对(公钥和私钥) RSA 1024
	 * @作者:ChaiJing THINK
	 * @时间:2018/6/28 11:24
	 */
	public static Map<String, Object> genKeyPair(String algorithm,int length) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);
		keyPairGen.initialize(length);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	public static Map<String, Object> genKeyPair() throws Exception {
		return genKeyPair(KEY_ALGORITHM_RSA,1024);
	}
	/**
	 * @描述:获取私钥
	 * @作者:ChaiJing THINK
	 * @时间:2018/6/28 11:23
	 */
	public static String getPrivateKey(Map<String, Object> keyMap)throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return Base64Utils.encode(key.getEncoded());
	}
	/**
	 * @描述:获取公钥
	 * @作者:ChaiJing THINK
	 * @时间:2018/6/28 11:23
	 */
	public static String getPublicKey(Map<String, Object> keyMap)throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		return Base64Utils.encode(key.getEncoded());
	}
// --------------------生成RSA证书工具----------------------------------------


// --------------------RSA加密解密工具----------------------------------------

	public static byte[] encrypt(byte[] data,Key key)throws Exception {
		return encrypt(data,key,MAX_ENCRYPT_BLOCK);
	}
	/**
	 * 对数据加密
	 * @param data
	 * @param key
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data,Key key,int blocksize)throws Exception {
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_RSA);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > blocksize) {
				cache = cipher.doFinal(data, offSet, blocksize);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * blocksize;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}
	/**
	 * @公钥加密 数据分段长度117
	 * @param data 源数据
	 * @param publicKey 公钥(BASE64编码)
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data,byte[] publicKey)throws Exception {
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
		Key key = keyFactory.generatePublic(x509KeySpec);
		return encrypt(data,key);
	}
	/**
	 * @私钥加密 数据分段长度117
	 * @param data 源数据
	 * @param privateKey 私钥pkcs8(BASE64编码)
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String privateKey)throws Exception {
		byte[] keyBytes = Base64Utils.decode(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
		Key key = keyFactory.generatePrivate(pkcs8KeySpec);
		return encrypt(data,key);
	}

	public static byte[] encryptByPublicKey(byte[] data, String publicKey)throws Exception {
		//BASE64字符串解码转为二进制数据
		byte[] keyBytes = Base64Utils.decode(publicKey);
		return encryptByPublicKey(data,keyBytes);
	}

	public static byte[] encryptByPublicKey_base64(byte[] data, String publicKey)throws Exception {
		byte[] keyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(publicKey.getBytes());
		return encryptByPublicKey(data,keyBytes);
	}

	public static String encryptByPublicKey(String content, PublicKey publicKey) throws Exception{
		byte[] bytes = encrypt(content.getBytes(UTF_8),publicKey);
		return Base64Utils.encode(bytes);
	}
	public static String encryptByPrivateKey(String content, PrivateKey privateKey) throws Exception{
		byte[] bytes = encrypt(content.getBytes(UTF_8),privateKey);
		return Base64Utils.encode(bytes);
	}

    public static byte[] decrypt(byte[] data ,Key key,int blocksize) throws Exception{
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_RSA);
        cipher.init(Cipher.DECRYPT_MODE, key);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > blocksize) {
                cache = cipher.doFinal(data, offSet, blocksize);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * blocksize;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
	/**
	 * 解密数据
	 * @param data
	 * @param key
	 */
	public static byte[] decrypt(byte[] data ,Key key) throws Exception{
		return decrypt(data,key,MAX_DECRYPT_BLOCK);
	}
	/**
	 * @私钥解密
	 * @param data 已加密数据
	 * @param privateKey 私钥(BASE64编码)
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data,String privateKey) throws Exception {
		byte[] keyBytes = Base64Utils.decode(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);

		return decrypt(data,privateK);
	}

	/**
	 * @公钥解密
	 * @param encryptedData 已加密数据
	 * @param publicKey 公钥(BASE64编码)
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] encryptedData,String publicKey) throws Exception {
		byte[] keyBytes = Base64Utils.decode(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
		Key publicK = keyFactory.generatePublic(x509KeySpec);

		return decrypt(encryptedData,publicK);
	}
	/**
	 * 私钥解密 数据分段长度128
	 * @param encryptedData
	 * @param privateKey
	 * @throws Exception
	 */
	private static byte[] decryptByPrivateKey(byte[] encryptedData, PrivateKey privateKey) throws Exception {
		return decrypt(encryptedData,privateKey);
	}
	/**
	 * 私钥解密 数据分段长度128
	 * @param content
	 * @param privateKey
	 * @throws Exception
	 */
	public static String decryptByPrivateKey(String content,PrivateKey privateKey) throws Exception{
		byte[] encryptedData = Base64Utils.decode(content);
		byte[] bytes = decrypt(encryptedData,privateKey);
		return new String(bytes, UTF_8);
	}
	public static String decryptByPublicKey(String content,PublicKey publicKey) throws Exception{
		byte[] encryptedData = Base64Utils.decode(content);
		byte[] bytes = decrypt(encryptedData,publicKey);
		return new String(bytes, UTF_8);
	}
// ---------------------------------------RSA加密解密工具----------------------------------------

//-------------------------------------------签名验签--------------------------------------------
	//SHA1WITHRSA
	public static String sign_sha1(String text, String privateKey, String charset) throws Exception {
		return signRsa(text,privateKey,charset,SIGN_SHA1WITHRSA);
	}
	public static boolean verify_sha1(String text, String publicKey, String sign)throws Exception {
		return verifyRsa(text,publicKey,sign,SIGN_SHA1WITHRSA);
	}
	//SHA1WITHRSA
	public static String sign_sha1(String text, PrivateKey privateKey, String charset) throws Exception {
		return signRsa(text,privateKey,charset,SIGN_SHA1WITHRSA);
	}
	public static boolean verify_sha1(String text, PublicKey publicKey, String sign)throws Exception {
		return verifyRsa(text,publicKey,sign,SIGN_SHA1WITHRSA);
	}
	//SHA256WITHRSA
	public static String sign_256(String text, String privateKey, String charset) throws Exception {
		return signRsa(text,privateKey,charset,SIGN_SHA256WITHRSA);
	}
	public static String sign_256(String text, PrivateKey privateKey, String charset) throws Exception {
		return signRsa(text,privateKey,charset,SIGN_SHA256WITHRSA);
	}
	public static boolean verify_256(String text, String publicKey, String sign)throws Exception {
		return verifyRsa(text,publicKey,sign,SIGN_SHA256WITHRSA);
	}
	//MD5WITHRSA
	public static String sign_MD5(String text, PrivateKey privateKey, String charset) throws Exception {
		return signRsa(text,privateKey,charset,SIGN_MD5WITHRSA);
	}
	public static String sign_MD5(String text, String privateKey, String charset) throws Exception {
		return signRsa(text,privateKey,charset,SIGN_MD5WITHRSA);
	}
	public static boolean verify_MD5(String text, String publicKey, String sign)throws Exception {
		return verifyRsa(text,publicKey,sign,SIGN_MD5WITHRSA);
	}
	//签名
	public static String signRsa(String text, String privateKey, String charset,String signType) throws Exception {
		return signRsa(text,getPrivateKey(privateKey),charset,signType);
	}
	public static String signRsa(String text, PrivateKey privateKey, String charset,String signType) throws Exception {
		Signature signature = Signature.getInstance(signType);
		signature.initSign(privateKey);
		signature.update(text.getBytes(charset));
		byte[] result = signature.sign();
		return Base64Utils.encode(result);
	}
	public static byte[] sign_rsa(String text, String privateKey, String charset,String signType) throws Exception {
		PrivateKey privateK = getPrivateKey(privateKey);
		Signature signature = Signature.getInstance(signType);
		signature.initSign(privateK);
		signature.update(text.getBytes(charset));
		return signature.sign();
	}
	//验签
	public static boolean verifyRsa(String text, String publicKey, String sign,String signType)throws Exception {
		return verifyRsa(text,getPublicKey(publicKey),sign,signType);
	}
	public static boolean verifyRsa(String text, PublicKey publicKey, String sign,String signType)throws Exception {
		return verifyRsa(text,publicKey,Base64Utils.decode(sign),signType);
	}
	public static boolean verifyRsa(String text, PublicKey publicKey, byte[] sign,String signType)throws Exception {
		Signature signature = Signature.getInstance(signType);
		signature.initVerify(publicKey);
		signature.update(text.getBytes("UTF-8"));
		return signature.verify(sign);
	}
	//从base64Str 转成 key
	public static PrivateKey getPrivateKey(String privateKey)throws Exception {
		byte[] keyBytes = Base64Utils.decode(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		return privateK;
	}
	public static PublicKey getPublicKey(String publicKey)throws Exception {
		byte[] keyBytes = Base64Utils.decode(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
		PublicKey publicK = keyFactory.generatePublic(keySpec);
		return publicK;
	}
//-------------------------------------------签名验签--------------------------------------------

//-------------------------单一加密算法-------------------------------------
	private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	/** Takes the raw bytes from the digest and formats them correct.
	 */
	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		// 把密文转换成十六进制的字符串形式
        for (byte aByte : bytes) {
            buf.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[aByte & 0x0f]);
        }
		return buf.toString();
	}
    /** MD5\SHA1加密算法 */
	public static String encodeDigest(String str,String instance,String charset) {
		if (StringUtil.isEmpty(str)) {return null;}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(instance);
			messageDigest.update(StringUtil.isEmpty(charset) ? str.getBytes() : str.getBytes(charset));
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
//-------------------------单一加密算法-------------------------------------

}
