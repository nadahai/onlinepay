/**
 * @类名称:扫码ServiceImpl.java
 * @时间:2017年12月28日上午10:23:55
 * @作者:lihai 
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.pay.api.order;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cmd.TradeCmd;
import com.vc.onlinepay.pay.order.h5.*;
import com.vc.onlinepay.pay.order.scan.*;
import com.vc.onlinepay.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:扫码交易业务处理
 * @作者:lihai 
 * @时间:2017年12月28日 上午10:23:55 
 */
@Service
@Component

public class ScanPayServiceApi{
    
    public Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TradeCmd tradeCmd;
    @Autowired
    private FunPayMerchH5ServiceImpl funPayMerchScanService;
    @Autowired
    private FunPayH5ServiceImpl funPayH5Service;
    @Autowired
    private XiaoMingPayScanServiceImpl xiaoMingPayScanService;
    @Autowired
    private XunJieScanServiceImpl xunJieScanService;
    @Autowired
    private QuickPayScanServiceImpl quickPayScanServiceImpl;
    @Autowired
    private WanShiDaScanServiceImpl wanShiDaScanService;
    @Autowired
    private ZhongLeYiDaScanServiceImpl zhongLeYiDaScanService;
    @Autowired
    private LklScanServiceImpl lklScanService;
    @Autowired
    private QianDaoLaScanServiceImpl shouQianBaScanService;
    @Autowired
    private XiongKongUnionServiceImpl xiongKongUnionService;
    @Autowired
    private AnHuiShouQianBaScanServiceImpl anHuiShouQianBaScanService;
    @Autowired
    private PingAnYunScanServiceImpl pingAnYunScanServiceImpl;
    @Autowired
    private HuiJieScanServiceImpl huiJieScanServiceImpl;
    @Autowired
    private PingAn2ScanServiceImpl pingAn2ScanServiceImpl;
    @Autowired
    private ZongHengWSDDaScanServiceImpl zongHengWSDDaScanServiceImpl;
    @Autowired
    private XunJie2ScanServiceImpl xunJie2ScanServiceImpl;
    @Autowired
    private FLMScanServiceImpl flmScanService;
    @Autowired
    private YouFuScanServiceImpl youFuScanServiceImpl;
    @Autowired
    private AAPayH5ServiceImpl aAPayH5Service;
    @Autowired
    private ShanFuTongScanServiceImpl shanFuTongScanServiceImpl;
    @Autowired
    private HuaFeiScanServiceImpl huaFeiScanServiceImpl;
    @Autowired
    private PddH5ServiceImpl pddH5Service;
    @Autowired
    private HongYunTScanServiceImpl hongYunTScanServiceImpl;
    @Autowired
    private QingHuScanServiceImpl qingHuScanServiceImpl;
    @Autowired
    private NatieScanServiceImpl natieScanServiceImpl;
    @Autowired
    private NatieYunSFScanServiceImpl natieYunSFScanServiceImpl;
    @Autowired
    private HuiJieFuScanServiceImpl huiJieFuScanServiceImpl;
    @Autowired
    private KuaiBaoScanServiceImpl kuaiBaoScanServiceImpl;
    @Autowired
    private YeSeScanServiceImpl yeSeScanServiceImpl;
    @Autowired
    private PeiQiScanServiceImpl peiQiScanServiceImpl;
    @Autowired
    private ZuYongScanServiceImpl zuYongScanServiceImpl;
    @Autowired
    private WXSaoScanServiceImpl wXSaoScanServiceImpl;
    @Autowired
    private SZGMScanServiceImpl sZGMScanServiceImpl;
    @Autowired
    private SZGM2ScanServiceImpl sZGM2ScanServiceImpl;
    @Autowired
    private ChaoLiuWXScanServiceImpl chaoLiuWXScanServiceImpl;
    @Autowired
    private EYuPDDScanServiceImpl eYuPDDScanServiceImpl;
    @Autowired
    private EYuGeMaScanServiceImpl eYuGeMaScanServiceImpl;
    @Autowired
    private ZuYongPDDWXScanServiceImpl zuYongPDDWXScanServiceImpl;
    @Autowired
    private SaoMaScanServiceImpl saoMaScanServiceImpl;
    @Autowired
    private WxSXBScanServiceImpl wxSXBScanServiceImpl;
    @Autowired
    private ZuYongGeMaAllScanServiceImpl zuYongGeMaAllScanServiceImpl;
    @Autowired
    private ZFBGMScanServiceImpl zFBGMScanServiceImpl;
    
    /**
     * @描述:扫码支付分发
     * @作者:nada
     * @时间:2019/3/31
     **/
    public JSONObject doRestPay(JSONObject reqData) {
        try {
            int source = reqData.getIntValue("channelSource");
            switch (source) {
                case 1:
                    return xiaoMingPayScanService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 10:
                    return quickPayScanServiceImpl.payOrder(reqData,tradeCmd.tradResultListener(reqData));
                case 11:
                    return wanShiDaScanService.payOrder(reqData,tradeCmd.tradResultListener(reqData));
                case 12:
                    return zhongLeYiDaScanService.payOrder(reqData,tradeCmd.tradResultListener(reqData));
                case 51:
                    return funPayH5Service.payOrder(reqData,tradeCmd.tradResultListener(reqData));
                case 60:
                    return  lklScanService.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 61:
                    return  shouQianBaScanService.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 65: case 81:
                    return funPayMerchScanService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 91:
                    return xiongKongUnionService.payOrder(reqData,tradeCmd.tradResultListener(reqData));
                case 92:
                    return xunJieScanService.payOrder(reqData,tradeCmd.tradResultListener(reqData));
                case 93:
                    return  anHuiShouQianBaScanService.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 94:
                    return  pingAnYunScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 95:
                    return  huiJieScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 96:
                    return  pingAn2ScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 98:
                    return  xunJie2ScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 99:
                    return  youFuScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 101:
                    return  flmScanService.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 102:
                    return  shanFuTongScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 111:
                    return  pddH5Service.payOrder(reqData, tradeCmd.tradResultListener (reqData));
                case 125:
                    return aAPayH5Service.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 126:
                    return huaFeiScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 127:
                    return hongYunTScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 133:
                    return qingHuScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 134:
                    return natieScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 135:
                    return natieYunSFScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 136:
                    return huiJieFuScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 137:
                    return kuaiBaoScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 138:
                    return yeSeScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 139:
                    return peiQiScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 140:
                    return zuYongScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 143:
                    return wXSaoScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 144:
                    return sZGMScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 145:
                    return sZGM2ScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 146:
                    return chaoLiuWXScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 147:
                    return eYuPDDScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 148:
                    return eYuGeMaScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 149:
                    return zuYongPDDWXScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 150:
                    return saoMaScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 151:
                    return wxSXBScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 228:case 229:case 230:case 231:case 232:case 233:case 234:case 235:case 236:case 237:case 238:
                    return zuYongGeMaAllScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 239:
                    return zFBGMScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                default:
                    return Constant.failedMsg("未知的扫码交易通道"+source+",请核实开通支付类型");
            }
        } catch (Exception e) {
            logger.error("扫码支付分发异常{}",reqData, e);
            return Constant.failedMsg("扫码分发异常");
        }
    }
}

