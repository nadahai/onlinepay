/**
 * @类名称:FastJsonRedisSerializer.java
 * @时间:2018年5月29日下午3:31:14
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @描述:自定义序列化器
 * @时间:2018年5月29日 下午3:31:14
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private Class<T> clazz;

	public FastJsonRedisSerializer(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (null == t) {
			return new byte[0];
		}
		return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (null == bytes || bytes.length <= 0) {
			return null;
		}
		String str = new String(bytes, DEFAULT_CHARSET);
		return (T) JSON.parseObject(str, clazz);
	}

	/**
	 * @throws IOException 
	 * @描述:对象非json
	 * @时间:2018年9月15日 下午12:01:38
	 */
	static byte[] serialize2(Object object) throws IOException {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
            return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(oos!=null){
				oos.close();
        	}
        	if(baos!=null){
        		baos.close();
        	}
		}
		return null;
	}

	static Object deserialize2(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
        	if(bais!=null){
        		bais.close();
        	}
        	if(ois!=null){
        		ois.close();
        	}
        }
		return null;
	}
}
