/*
 * Copyright 2015-2102 RonCoo(http://www.roncoo.com) Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vc.onlinepay.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @desc 属性文件工具类
 * @author nada
 * @create 2019/7/2 14:02
*/
public class ConfigUtil {

    public static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    private final static  String  config = "config.properties";

    /**
     * 通过静态代码块读取上传文件的验证格式配置文件,静态代码块只执行一次(单例)
     */
    private static Properties properties = new Properties();

    // 通过类装载器装载进来，从类路径下读取属性文件
    static {
        try {
            properties.load(ConfigUtil.class.getClassLoader().getResourceAsStream(config));
        } catch (Exception e) {
            logger.error("读取文件异常:{}",config,e);
        }
    }

    private ConfigUtil() {}

    /**
     * 函数功能说明 ：读取配置项
     */
    public static String readConfig(String key) {
         String propertie = (String) properties.get(key);
        logger.info("读取配置项:{}",propertie);
         return propertie;
    }

    //企业支付宝授权信息
    public static final String alipayAppId = readConfig("alipay_appId");
    public static final String alipayPrivateKey = readConfig("alipay_private_key");
    public static final String alipayOpenAuthUrl = readConfig("alipay_open_auth_url");
}
