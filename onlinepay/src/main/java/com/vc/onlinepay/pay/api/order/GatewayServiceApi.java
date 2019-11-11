package com.vc.onlinepay.pay.api.order;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cmd.TradeCmd;
import com.vc.onlinepay.pay.order.gateway.HanYinGatewayServiceImpl;
import com.vc.onlinepay.pay.order.gateway.RemitGateway2ServiceImpl;
import com.vc.onlinepay.pay.order.gateway.RemitGatewayServiceImpl;
import com.vc.onlinepay.pay.order.union.HuiYunUnionServiceImpl;
import com.vc.onlinepay.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:网关交易业务处理
 * @时间:2017年12月28日 上午10:23:55 
 */
@Service
@Component
public class GatewayServiceApi{
    
    public Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TradeCmd tradeCmd;
    @Autowired
    private RemitGatewayServiceImpl remitGatewayService;
    @Autowired
    private RemitGateway2ServiceImpl remitGateway2ServiceImpl;
    @Autowired
    private HanYinGatewayServiceImpl hanYinGatewayService;
    @Autowired
    private HuiYunUnionServiceImpl huiYunUnionServiceImpl;
    
    /**
     * @描述:支付业务处理
     * @时间:2017年12月28日 上午11:19:37
     */
    public JSONObject doRestPay(JSONObject reqData) {
        try {
            int source = reqData.getIntValue("channelSource");
            switch (source) {
                case 19:
                    return remitGatewayService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 66:
                    return remitGateway2ServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 70:
                    return hanYinGatewayService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 92:
                    return huiYunUnionServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                default:
                    return Constant.failedMsg("未找到网关交易通道"+source+",请核实开通支付类型");
            }
        } catch (Exception e) {
            logger.error("网关支付异常{}",reqData, e);
            return Constant.failedMsg("网关支付异常,请联系运维人员");
        }
    }
}

