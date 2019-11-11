

package com.vc.onlinepay.utils.ninepie;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiDesUtils {
    
    public static final Logger logger = LoggerFactory.getLogger(HiDesUtils.class);

    private static final String DEFULT_KEY = "cputest";
    
    public HiDesUtils() {
    }
    /** 加密*/
    public static String desEnCode(String srcStr) {
        return desEnCode(srcStr,DEFULT_KEY);
    }
    /** 解密*/
    public static String desDeCode(String srcStr) {
        return desDeCode(srcStr,DEFULT_KEY);
    }

    /**
     * DES加密
     * @param datasource
     * @return
     */
    public static String encode(String datasource, String key){
        try{
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            byte[] temp = Base64.encodeBase64(cipher.doFinal(datasource.getBytes()));
            return new String(temp,"utf-8");
        }catch(Throwable e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * DES解密
     * @return
     */
    public static String decode(String src, String key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(key.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return new String(cipher.doFinal(Base64.decodeBase64(src.getBytes())),"utf-8");
    }

    public static void main(String[] args) throws Exception {
        String str = encode("你好","12345678");
        System.out.println(str);
        System.out.println(decode(str,"12345678"));
    }

    public static String desEnCode(String srcStr,String key) {
        try {
            Key localException = jdMethod_super(key.getBytes());
            Cipher localCipher = Cipher.getInstance("DES");
            localCipher.init(1, localException);
            return byteArr2HexStr(localCipher.doFinal(srcStr.getBytes()));
        } catch (InvalidKeyException var1) {
            var1.printStackTrace();
        } catch (NoSuchAlgorithmException var2) {
            var2.printStackTrace();
        } catch (NoSuchPaddingException var3) {
            var3.printStackTrace();
        }  catch (Exception var6) {
            var6.printStackTrace();
        }
        return "0";
    }

    public static String desDeCode(String desStr,String key) {
        try {
            Key localException = jdMethod_super(key.getBytes());
            Cipher localCipher = Cipher.getInstance("DES");
            localCipher.init(2, localException);
            return new String(localCipher.doFinal(hexStr2ByteArr(desStr)));
        } catch (InvalidKeyException var1) {
            var1.printStackTrace();
        } catch (NoSuchAlgorithmException var2) {
            var2.printStackTrace();
        } catch (NoSuchPaddingException var3) {
            var3.printStackTrace();
        }  catch (Exception var6) {
            var6.printStackTrace();
        }
        return "0";
    }

    private static Key jdMethod_super(byte[] paramArrayOfByte) throws Exception {
        byte[] arrayOfByte = new byte[8];

        for(int localSecretKeySpec = 0; localSecretKeySpec < paramArrayOfByte.length && localSecretKeySpec < arrayOfByte.length; ++localSecretKeySpec) {
            arrayOfByte[localSecretKeySpec] = paramArrayOfByte[localSecretKeySpec];
        }

        SecretKeySpec var3 = new SecretKeySpec(arrayOfByte, "DES");
        return var3;
    }

    public static String byteArr2HexStr(byte[] paramArrayOfByte) throws Exception {
        int i = paramArrayOfByte.length;
        StringBuffer localStringBuffer = new StringBuffer(i * 2);

        for(int j = 0; j < i; ++j) {
            int k;
            for(k = paramArrayOfByte[j]; k < 0; k += 256) {
                ;
            }
            if(k < 16) {
                localStringBuffer.append("0");
            }
            localStringBuffer.append(Integer.toString(k, 16));
        }
        return localStringBuffer.toString();
    }

    public static byte[] hexStr2ByteArr(String paramString) throws Exception {
        byte[] arrayOfByte1 = paramString.getBytes();
        int i = arrayOfByte1.length;
        byte[] arrayOfByte2 = new byte[i / 2];

        for(int j = 0; j < i; j += 2) {
            String str = new String(arrayOfByte1, j, 2);
            arrayOfByte2[j / 2] = (byte)Integer.parseInt(str, 16);
        }
        return arrayOfByte2;
    }

//    public static void main(String[] arg) {
//        //String str = "dbc2c603db13d0212559c9c4dc01b82dde16bc982da5dbbccd4d315c69723588434ce71cb0011bf311031e5a424963f7e74f30d8cce7c9f11f5998438af6adc2efb5b0525faf117e";
//       // System.out.println(str.length());
////        System.out.println(HiDesUtils.desEnCode("阮鑫"));
//        System.out.println(HiDesUtils.desDeCode("93ac35869f05da7d26afb6051c64c9e755a31de33174cbe4309efbfea5386e94611a9c12d4d559cdb95950a03925f63ae09c59f84185a9d0c559c11f4aa6dbe1969ac32201ab30036c77eaa19c64e32dd9d09755b3a896be5699f0040eed2c0c7259a913009bd7dec14d0f486df3b24c00cf3455a11a05fa3f4e96de7849fb8e17579b482202a87a57434905c0f8cc682f0fc48e3479a492065071384dc9a7c703a98060684bb648e8cabf40bb4da57d32214b1901692f91abb0fe191680902dc3974f9cb115de8f86f8486791d36ceb9fa8afa36482425c38e5e715fd89805786f2f4756c8e00a41136cbda32727248fc421131dd3305e9d148daa1500716a1161cf85903655c3a36b2abebac8c31afa46a1798995239de17ee3f54db8a46e7cd2aefbd9799523fa8ed0905dce10fcb"));
//    }

}
