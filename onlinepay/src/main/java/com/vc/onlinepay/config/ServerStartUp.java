package com.vc.onlinepay.config;
/**
 * @类名称:SpringBootStartApplication.java
 * @时间:2017年11月14日下午9:40:21
 * @作者:nada
 * @版权:版权所有 Copyright (c) 2017 
 */

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vc.onlinepay.MainStartup;

/**
* @描述:修改启动类，继承 SpringBootServletInitializer 并重写 configure 方法
* @作者:nada
* @时间:2017年11月14日 下午9:40:21 
*/
public class ServerStartUp  extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
      // 注意这里要指向原先用main方法执行的Application启动类
      return builder.sources(MainStartup.class);
    }
}

