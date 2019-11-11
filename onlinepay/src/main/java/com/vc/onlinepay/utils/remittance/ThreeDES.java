package com.vc.onlinepay.utils.remittance;

import org.apache.commons.codec.binary.Base64;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;



/**
 * 3DES算法
 *
 */
public class ThreeDES {
    public static void main(String[] args) throws Exception {  
//        byte[] key = "1067C602C7DF4E648B7ADD89".getBytes("GB2312");
//        byte[] data="周建华".getBytes("GB2312");
//
//        System.out.println("3DES加密:");
//        String hexString = encryptToHex(data,key);
//        System.out.println(hexString);
        
        
//        String spmeple ="644fd3c1437e3a34aa4adc65f0596daa0b4be8c36f0390fcb15c59cc1db435471155b32e19abfd2563d0ac34eae03f61ebb2b4caaf1bf8abd2f1015a43d883f5b15ede38fcb1ba627ff7ae8efb12bcb2f345ee98075178f5ab4152973ff4ad145d1367aa5ecbb1d749621d7199e167ea2fc8f409140b13a2336101134406202b5306bb282e62a6d8221d37edea5d1f6b6dab403a4bbe5e3f5355d7803c02d3b05f8faf3295149a9d62b1411feb4ce5d298a7c943fe9b2fb69521bcd8ec8f3ea6708b94f0c154ac073e94c183b1ec71cc6a202890e24baf667d8a5a90fe317a36fba5afe90fff423ce64b0f4d8113241996d34ea84e294a61cc5ca10d52b45a807141e821804c77ccb221727438c5b6e60f7ca699a0c70cc93d8cf37a3552c5513cf4a7b87ee7a9f2d37902356913c1cc07c6d0bcf909c6de13bc7388b62ff9982b40752547fb7a19c0b2de1b4444af0b955dd447f462bd60cf243a58e1f1978c31252376ca83df118e2f569b5a6c5155aefce068663a96a09521bcd8ec8f3ea65f24510b5f4555ac6708f7735a73cb5b1369e97db38dc4c1ab4152973ff4ad14d14a0680d8466fb1ba53a85c9ff87c72e501f2de34d9656f34c63e0ffe797b8fe904e5aff152a92ccdb629e38d92ad0112f1173c2781ec32032f2d0b66c3c2581fe993651c409d2a";
//        String keyStr = "721a783007c22b320c079a7f0033b563";
//        byte[] key = keyStr.substring(0,24).getBytes("utf-8");
//
////        String hexString2 = decryptToString(hexStringToBytes("f2f95f72b0c327bf"),key);
//        String hexString2 = new String(decrypt(hexStringToBytes(spmeple), key),"utf-8");
//        System.out.println(hexString2);
//
//        byte[] data= hexString2.getBytes("utf-8");
////
//        System.out.println("3DES加密:");
//        String hexString = encryptToHex(data,key);
//        System.out.println(hexString);
//
//        if(hexString.equals(spmeple)){
//            System.out.println("OK!");
//        }

        String desKey = "AE30A70F77A18AE3E74EE1E36C6EEB09";
        String data ="15685488052";
        String encode = encode(data,desKey);
        System.out.println(encode);
        String decode = decode(encode,desKey);
        System.out.println(decode);
    }
	
//    static {
//        Security.addProvider(new com.sun.crypto.provider.SunJCE());
//    }
  
    private static final String MCRYPT_TRIPLEDES = "DESede";  
    private static final String TRANSFORMATION = "DESede/ECB/PKCS5Padding";  
  
    /**
     * 加密
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
      public static byte[] encrypt(byte[] data, byte[] key) throws Exception {  
          DESedeKeySpec spec = new DESedeKeySpec(key);  
          SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MCRYPT_TRIPLEDES);  
          SecretKey sec = keyFactory.generateSecret(spec);  
          Cipher cipher = Cipher.getInstance(TRANSFORMATION);  
          cipher.init(Cipher.ENCRYPT_MODE, sec);  
          return cipher.doFinal(data);  
      }  
    /**
     * 解密
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {  
        DESedeKeySpec spec = new DESedeKeySpec(key);  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MCRYPT_TRIPLEDES);  
        SecretKey sec = keyFactory.generateSecret(spec);  
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);  
        cipher.init(Cipher.DECRYPT_MODE, sec);  
        return cipher.doFinal(data);  
    }
    /**
     * @描述:3des加密
     * @作者:ChaiJing THINK
     * @时间:2018/8/24 17:36
     */
    public static String decode(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes());
            SecretKey sk = SecretKeyFactory.getInstance(MCRYPT_TRIPLEDES).generateSecret(dks);
            cipher.init(Cipher.DECRYPT_MODE, sk);
            byte[] result = cipher.doFinal(Base64.decodeBase64(data));
            return new String(result, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * @描述:3des解密
     * @作者:ChaiJing THINK
     * @时间:2018/8/24 17:36
     */
    public static String encode(String data, String key) {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MCRYPT_TRIPLEDES);
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());
            byte[] bytes = cipher.doFinal(data.getBytes("utf-8"));
//            System.out.println(new String(bytes));
            return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
  
    /**
     * Convert byte[] string to hex
     * @param arrB
     * @return
     */
    public static String byteArr2HexStr(byte[] arrB) {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		// 最大128位
		String result = sb.toString();
		return result;
	}
    /** 
     * Convert hex string to byte[] 
     * @param hexString the hex string 
     * @return byte[] 
     */  
    public static byte[] hexStringToBytes(String hexString) {  
        if (hexString == null || "".equals(hexString)) {
            return null;  
        }  
        hexString = hexString.toUpperCase();  
        int length = hexString.length() / 2;  
        char[] hexChars = hexString.toCharArray();  
        byte[] d = new byte[length];  
        for (int i = 0; i < length; i++) {  
            int pos = i * 2;  
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
        }  
        return d;  
    }
    /** 
     * Convert char to byte 
     * @param c char 
     * @return byte 
     */  
     private static byte charToByte(char c) {  
        return (byte) "0123456789ABCDEF".indexOf(c);  
    } 
    /**
     * 加密
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static String encryptToHex(byte[] data, byte[] key) throws Exception {
    	return byteArr2HexStr(encrypt(data, key));
    }
    /**
     * 解密
     * @param data
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    public static String decryptToString(byte[] data, byte[] key) throws Exception {
    	return new String(decrypt(data, key));
    }
  
  
}   
