package com.vc.onlinepay;

import java.net.InetAddress;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @描述:项目启动入口
 * @作者:nada
 * @时间:2019/3/24
 **/
@SpringBootApplication
@EnableScheduling
@MapperScan (basePackages = "com.vc.onlinepay.persistent.mapper")
public class MainStartup {

    public static final Logger logger = LoggerFactory.getLogger (MainStartup.class);

    /**
     * @描述:服务进程启动入口
     * @作者:nada
     * @时间:2019/3/24
     **/
    public static void main (String[] args) {
        try {
            ApplicationContext ctx = SpringApplication.run (MainStartup.class, args);
            String[] activeProfiles = ctx.getEnvironment ().getActiveProfiles ();
            for (String profile : activeProfiles) {
                System.err.println ("Spring Boot使用profile为:" + profile);
            }
            InetAddress iAddress = InetAddress.getLocalHost ();
            System.err.println ("start successful : http://" + iAddress.getHostAddress ());
        } catch (Exception e){
            logger.error ("服务进程启动异常", e);
        }
    }
}
