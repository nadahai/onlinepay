/**
 * @类名称:SpringBootDemoRedis3ApplicationTests.java
 * @时间:2018年5月29日下午3:34:53
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.vc.onlinepay.cache.RedisCacheApi;

/**
 * @描述:TODO
 * @时间:2018年5月29日 下午3:34:53 
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest  
public class TestRedis {
	
	@Autowired  
    private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	RedisCacheApi recompile;
  
    @Ignore
    //直接使用redisTemplate存取字符串  
    public void setAndGet() {  
    	recompile.set("test:set", "2342423");
        System.out.println(redisTemplate.opsForValue().get("test:set"));
    }  
  
    @Ignore
    //直接使用redisTemplate存取对象  
    public void setAndGetAUser() {  
        redisTemplate.opsForValue().set("test:setUser", "lihai");  
        System.out.println(redisTemplate.opsForValue().get("test:setUser"));
    }  
}

