/**
 * @类名称:CacheConstants.java
 * @时间:2017年8月24日下午4:13:23
 * @作者:lihai 
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.cache;

/**
 * @描述:系统字典key
 * @作者:lihai 
 * @时间:2017年8月24日 下午4:13:23 
 */
public class CacheConstants {
    /**
     * @描述:缓存1分钟
     **/
    public static final long DEFAULT_INVALID_TIMER_1 = 60000L;
    /**
     * @描述:缓存5分钟
     **/
    public static final long DEFAULT_INVALID_TIMER_5 = 300000L;
    /**
     * @描述:缓存10分钟
     **/
    public static final long DEFAULT_INVALID_TIMER_10 = 600000L;

    /**
     * @描述:缓存30分钟
     **/
    public static final long DEFAULT_INVALID_TIMER_30 = 1800000L;

    /**
     * @描述:缓存2小时
     **/
    public static final long DEFAULT_INVALID_TIMER_2H = 7200000L;

    /**
     * @描述:缓存1小时
     **/
    public static final long DEFAULT_INVALID_TIMER_1H = 3600000L;

    /**
     * @描述:拉卡拉订单 生效时间支付有效期5分钟 相同金额时间间隔缓存5分钟
     **/
    public static final int EXPIRED_TIME_5 = 300;

    /**
     * @描述:收钱吧终端信息缓存
     **/
    public static final String REDIS_SHOUQIANBA_TERMINANINFO =  "REDIS_SHOUQIANBA_TERMINANINFO_";
    public static final String REDIS_SHOUQIANBA_UPNO =  "REDIS_SHOUQIANBA_UPNO_";
    /**
     * 付临门
     */
    public static final String REDIS_FULINGMEN_TOKEN=  "REDIS_FULINGMEN_TOKEN_";

    /** 拉卡拉订单打开通知地址 */
    public static final String ONLINE_LKLORDER_NOTICE_URL = "online.lkl.order.notice.url";
    /** 拉卡拉后台账号 */
    public static final String ONLINE_LKL_LOGIN_NAME = "online.lkl.agent.login.name";
    /** 拉卡拉后台密码 */
    public static final String ONLINE_LKL_LOGIN_PASSWORD = "online.lkl.agent.login.password";

    public static final String COMMON_SPLIT = "_";
    /** 数据库商户信息行级缓存 */
    public static final String CACHE_TABLE_KEY_MERCH_INFO = "tb_merch_info_";
    /** 数据库已经代付过银行卡信息行级缓存 */
    public static final String CACHE_TABLE_KEY_PAYMENT_CARD = "tb_payment_card_";
    /** 数据库商户通道行级信息缓存 */
    public static final String CACHE_TABLE_KEY_CHANNEL = "tb_channel_info_";
    /** 保存日志开关 */
    public static final String SAVE_LOG_SWITCH = "online.is.save.log.switch";
    /** 测试环境代付白名单 */
    public static final String ORDER_REPLACE_MERCH = "online.replace.test.merch";
    /** 代付特殊通道开关 */
    public static final String REPLACE_PASS_MERCH = "online.replace.pass.merch";
    /** T1代付通道开关 */
    public static final String CASH_SWITCH_T1 = "online.cash.t1.switch";
    /** T0代付通道开关 */
    public static final String CASH_SWITCH_T0 = "online.cash.t0.switch";
    /** 代付通道开始时间 */
    public static final String CASH_TIME_BEGIN = "online.cash.begin.time";
    /** 代付通道结束时间 */
    public static final String CASH_TIME_END = "online.cash.end.time";
    /** 代付通道最高限额 单位：元 */
    public static final String CASH_MAX_AMOUNT = "online.cash.max.amount";
    /** 当日单卡代付限额 单位：元 */
    public static final String CASH_AMOUNT_PREDAY_FORCARD = "online.cash.amount.preday.card";
    /** 代付手机号黑名单 */
    public static final String CASH_BLACK_PHONE = "online.cash.phone.black.list";
    /** 代付姓名黑名单 */
    public static final String CASH_BLACK_NAME = "online.cash.name.black.list";
    /** 代付卡号黑名单 */
    public static final String CASH_BLACK_BANKNO = "online.cash.bankno.black.list";
    /** 提现总开关 */
    public static final String CASH_SWITCH_SERVICE = "online.cash.service.switch";
    /** 交易代付日切时间 */
    public static final String ONLINE_LIMIT_TIME_ORDER = "online.system.order.dtime";
    /** 下单监控开关 */
    public static final String ONLINE_SWITCH_MONITOR_AUTO = "online.monitor.auto.switch";
    /** 下单失败监控 */
    public static final String ONLINE_SWITCH_MONITOR_FAILED = "online.monitor.failed.order";
    /** 监控通知 微信推送用户id列表 */
    public static final String ONLINE_MONITOR_NOTICE_WECHATID_LIST = "online.monitor.notice.open.ids";
    /** 相同金额订单间隔时间 */
    public static final String ONLINE_ALIPAY_SAME_ORDER_EXPIRED_TIME ="online.alipay.order.money.expired.time";
    /** 交易金额自动上浮通道 */
    public static final String ONLINE_ORDER_AMOUNT_FLOAT_CHANNEL ="online.order.amount.float.channel";
    /** 供应商列表 */
    public static final String ALL_SUPPER_ACCOUNT_LIST = "ALL_SUPPER_ACCOUNT_LIST";
    /** 供应商map */
    public static final String ALL_SUPPER_ACCOUNT_MAP = "ALL_SUPPER_ACCOUNT_MAP";
    /** 供应商账号 */
    public static final String SUPPER_ACCOUNT_NO = "supperAccountNo_";
    /** 支付宝账号map */
    public static final String LOOP_ROBIN_ALIPAY_MAP = "LoopRobinAlipayMap_";
    /** 支付宝账号 */
    public static final String LOOP_ROBIN_ALIPAY_NO = "LoopRobinAlipayNo_";
    /** 通道解密秘钥 */
    public static final String ONLINE_DECODE_PUBLIC_KEY = "online.decode.public.key";
    /** 支付宝轮询算法*/
    public static final String ONLINE_SUPPLIER_LOOP_ROBIN = "online.supplier.loop.robin";
    /** 通道子商户账号列表 */
    public static final String CHANNEL_SUBNO_LIST = "channelsubnolist_";
    /** 集合算法轮询下标 */
    public static final String CHANNEL_LIST_POLLING_INDEX = "online.list.polling.index";
}

