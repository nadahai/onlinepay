/**
 * @类名称:银联快捷ServiceImpl.java
 * @时间:2017年12月28日上午10:23:55
 * @作者:lihai 
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.pay.api.order;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cmd.TradeCmd;
import com.vc.onlinepay.pay.order.quick.SandQuickServiceImpl;
import com.vc.onlinepay.pay.order.union.GaoYangUnionServiceImpl;
import com.vc.onlinepay.pay.order.union.HuiYunUnionServiceImpl;
import com.vc.onlinepay.pay.order.union.KuaiBaoKJScanServiceImpl;
import com.vc.onlinepay.pay.order.union.NatieKuaiJieScanServiceImpl;
import com.vc.onlinepay.pay.order.union.RemitUnionServiceImpl;
import com.vc.onlinepay.pay.order.union.WanShiDaUnionServiceImpl;
import com.vc.onlinepay.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:银联快捷交易路由业务处理
 * @作者:lihai 
 * @时间:2017年12月28日 上午10:23:55 
 */
@Service
@Component
public class UnionServiceApi {
    public Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TradeCmd tradeCmd;
    @Autowired
    private RemitUnionServiceImpl remitUnionService;
    @Autowired
    private SandQuickServiceImpl sandQuickServiceImpl;
    @Autowired
    private HuiYunUnionServiceImpl huiYunUnionServiceImpl;
    @Autowired
    private WanShiDaUnionServiceImpl wanShiDaUnionService;
    @Autowired
    private GaoYangUnionServiceImpl gaoYangUnionService;
    @Autowired
    private NatieKuaiJieScanServiceImpl natieKuaiJieScanServiceImpl;
    @Autowired
    private KuaiBaoKJScanServiceImpl kuaiBaoKJScanServiceImpl;
    

    /**
     * @描述:银联快捷支付分发
     * @作者:lihai 
     * @时间:2017年12月28日 上午11:19:37
     */
    public JSONObject doRestPay(JSONObject reqData) {
        try {
            int source = reqData.getIntValue("channelSource");
            switch (source) {
                case 11:
                    return wanShiDaUnionService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 13:
                    return gaoYangUnionService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 19:
                    return remitUnionService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 27:
                    return sandQuickServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 66:
                    return remitUnionService.payOrderWap(reqData, tradeCmd.tradResultListener(reqData));
                case 92:
                    return huiYunUnionServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 134:
                    return natieKuaiJieScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 135:
                    return natieKuaiJieScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 137:
                    return kuaiBaoKJScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                    
                default:
                    return Constant.failedMsg("未知的银联快捷交易通道"+source+",请核实开通支付类型");
            }
        } catch (Exception e) {
            logger.error("银联快捷分发异常{}",reqData, e);
            return Constant.failedMsg("银联快捷分发异常");
        }
    }
}

