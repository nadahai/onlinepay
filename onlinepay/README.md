# onlinepay核心交易系统

## 平台简介
核心交易系统系统：springMvc,spring,myBatis,mysql,redis,maven

1，服务器对外暴露IP
47.75.5.110，47.244.63.98，125.31.34.82，47.244.57.76，47.244.56.125

2，对外暴露web服务
http://boss.mall51.top/cms
http://boss.mall51.top/agent
http://payline.uicp.cn/cagent
http://payline.uicp.cn/ccms
http://yiqian.wunlun.cn/yagent
http://yiqian.wunlun.cn/ycms

3，对外暴露接口项目
http://paypaul.385mall.top/onlinepay
http://payline.uicp.cn/chunpay
http://online.a422.cn/yonlinepay

4，对外暴露服务接口
http://paypaul.385mall.top/onlinepay/h5PayApi
http://paypaul.385mall.top/onlinepay/agentTransfer
http://payline.uicp.cn/chunpay/h5PayApi
http://online.a422.cn/yonlinepay/h5PayApi

#版本更新
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
V1.0.0 2018-11-30	 增加清算中心，剥离接口
V3.0.1 2018.06.20  1，代付通道自动路由2，解决汇付宝卡号重叠问题3，配备独立通道时，不受代付时间的限制8，修复账户验证失败问题 9，修复高并发订单号重复问题
V3.0.1 2018.06.15  1，账务操作,手工账务结算、冻结、解冻 2，分润实时性数据采集,3，信付宝通道对接，荣行接口对接 4，自动监控(代付,交易,推送,定时任务推送) 5，代付秒切轮询自动路由
V3.0.1 2018.02.01  部使用新接口
V3.0.1 2018.01.31  停止维护bft旧接口
V3.0.1 2017.11.01  发布新建版本
V3.0.0 2017.09.01  研发系统
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
#项目结构
────────────────────────────────────────────────────────────────────────────────────────────────────────────────
├── onlinepay-provider -- dubbo架构实现，服务提供者
├── onlinepay-api --协议接口定义
├── onlinepay-gate --mvc架构实现,服务消费者
├── onlinepay-common --公共模块
├── onlinepay-db -- 数据持久层
├── onlinepay-test -- 测试中心
├── onlinepay-timer -- 任务调度系统
├── onlinepay-demo -- 演示中心
├── onlinepay-shop -- 演示商城
├── onlinepay-cms -- 运营管理平台
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
#项目介绍
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────
onlinepay聚合支付,使用Java开发，包括spring-cloud、dubbo、spring-boot三个架构版本
包含spring,myBatis,mysql,redis,maven,dubbo,zookepper
系统容器服务：tomcat8.5,ngnix1.11.13
持久化DB：mysql7.5版本，redis4.0版本
运行环境和系统：jdk1.8，centos7.5
─────────────────────────────────────────────────────────────────────────────────────────────────────────────────