/**
 * @类名称:RedisConfig.java
 * @时间:2018年5月29日下午3:32:35
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.vc.onlinepay.cache.FastJsonRedisSerializer;

/**
 * @描述:TODO
 * @时间:2018年5月29日 下午3:32:35 
 */
@EnableCaching  
@Configuration  
@ConditionalOnClass(RedisOperations.class)  
@EnableConfigurationProperties(RedisProperties.class) 
public class RedisConfig extends CachingConfigurerSupport {  
	
	@Bean(name = "redisTemplate")  
    @SuppressWarnings("unchecked")  
    @ConditionalOnMissingBean(name = "redisTemplate")  
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {  
        RedisTemplate<Object, Object> template = new RedisTemplate<>();  
        //使用fastjson序列化  
        @SuppressWarnings("rawtypes")
		FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);  
        // value值的序列化采用fastJsonRedisSerializer  
        template.setValueSerializer(fastJsonRedisSerializer);  
        template.setHashValueSerializer(fastJsonRedisSerializer);  
        // key的序列化采用StringRedisSerializer  
        template.setKeySerializer(fastJsonRedisSerializer);  
        template.setHashKeySerializer(fastJsonRedisSerializer);  
        template.setConnectionFactory(redisConnectionFactory);
        return template;  
    }  
  
    /*@Bean 
    @ConditionalOnMissingBean(StringRedisTemplate.class) 
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) { 
        StringRedisTemplate template = new StringRedisTemplate(); 
        template.setConnectionFactory(redisConnectionFactory); 
        return template; 
    }*/  
  
    //缓存管理器  
    @Bean  
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {  
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager  
                .RedisCacheManagerBuilder  
                .fromConnectionFactory(redisConnectionFactory);  
        return builder.build();  
    }  
}

