package com.vc.onlinepay.pay.api.order;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cmd.TradeCmd;
import com.vc.onlinepay.pay.order.bakup.*;
import com.vc.onlinepay.pay.order.h5.*;
import com.vc.onlinepay.pay.order.scan.*;
import com.vc.onlinepay.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:H5支付业务处理
 * @作者:nada
 * @时间:2017年12月28日 上午10:23:55
 */
@Service
@Component
public class H5ServiceApi {

    public Logger logger = LoggerFactory.getLogger (getClass ());
    @Autowired
    private TradeCmd tradeCmd;
    @Autowired
    private FunPayH5ServiceImpl funPayH5ServiceImpl;
    @Autowired
    private FunPayMerchH5ServiceImpl funPayMerchH5Service;
    @Autowired
    private ChunPayScanServiceImpl chunPayScanService;
    @Autowired
    private HuiYunH5ServiceImpl huiYunH5Service;
    @Autowired
    private WanShiDaH5ServiceImpl wanShiDaH5Service;
    @Autowired
    private PinDuoDuoScanServiceImpl pinDuoDuoScanServiceImpl;
    @Autowired
    private YouFuH5ScanServiceImpl youFuH5ScanServiceImpl;
    @Autowired
    private AAPayH5ServiceImpl aAPayH5Service;
    @Autowired
    private XinZhiFuScanServiceImpl xinZhiFuScanServiceImpl;
    @Autowired
    private HuaFeiScanServiceImpl huaFeiScanServiceImpl;
    @Autowired
    private HongYunTScanServiceImpl hongYunTScanServiceImpl;
    @Autowired
    private XunJieScanServiceImpl xunJieScanService;
    @Autowired
    private GaoYangZuyongServiceImpl gaoYangZuyongService;
    @Autowired
    private LLH5ScanServiceImpl lLH5ScanServiceImpl;
    @Autowired
    private YaLongScanServiceImpl yaLongScanServiceImpl;
    @Autowired
    private ErYuanZHScanServiceImpl erYuanZHScanServiceImpl;
    @Autowired
    private NatieScanServiceImpl natieScanServiceImpl;
    @Autowired
    private JieXinH5ServiceImpl jieXinScanServiceImpl;
    @Autowired
    private PeiQiScanServiceImpl peiQiScanServiceImpl;
    @Autowired
    private WangWangH5ServiceImpl wangWangScanServiceImpl;
    @Autowired
    private NanNingH5ServiceImpl nanNingScanServiceImpl;
    @Autowired
    private QingHuScanServiceImpl qingHuScanServiceImpl;
    @Autowired
    private NanNingZHScanServiceImpl nanNingZHScanServiceImpl;
    @Autowired
    private PYFScanServiceImpl pYFScanServiceImpl;
    @Autowired
    private ZhongRenScanServiceImpl zhongRenScanServiceImpl;
    @Autowired
    private ChaoLiu2ScanServiceImpl chaoLiu2ScanServiceImpl;
    @Autowired
    private ChaoLiu3ScanServiceImpl chaoLiu3ScanServiceImpl;
    @Autowired
    private PengScanServiceImpl pengScanServiceImpl;
    @Autowired
    private LJPDFScanServiceImpl lJPDFScanServiceImpl;
    @Autowired
    private LSWPDDScanServiceImpl lSWPDDScanServiceImpl;
    @Autowired
    private ZuYongPDD3ScanServiceImpl zuYongPDD3ScanServiceImpl;
    @Autowired
    private XiaoNiaoScanServiceImpl xiaoNiaoScanServiceImpl;
    @Autowired
    private HuFeiScanServiceImpl huFeiScanServiceImpl;
    @Autowired
    private SuRenHuaFeiScanServiceImpl suRenHuaFeiScanServiceImpl;
    @Autowired
    private ZFBGMH5ServiceImpl zFBGMScanServiceImpl;
    @Autowired
    private WeiBoScanServiceImpl weiBoScanServiceImpl;
    @Autowired
    private TaoBDFScanServiceImpl taoBDFScanServiceImpl;
    @Autowired
    private JinMoPDDScanServiceImpl jinMoPDDScanServiceImpl;
    @Autowired
    private HuaFeiNewScanServiceImpl huaFeiNewScanServiceImpl;
    @Autowired
    private HangKongScanServiceImpl hangKongScanServiceImpl;
    @Autowired
    private PiXiuH5ServiceImpl piXiuScanService;
    @Autowired
    private WolfCubScanServiceImpl wolfCubScanService;
    @Autowired
    private SunPayScanServiceImpl sunPayScanService;

//    @Autowired
//    private PddH5ServiceImpl pddH5Service;
//    @Autowired
//    private ZuYongScanServiceImpl zuYongScanServiceImpl;
//    @Autowired
//    private ZuYongDJHScanServiceImpl zuYongDJHScanServiceImpl;
//    @Autowired
//    private ZuYongDFScanServiceImpl zuYongDFScanServiceImpl;
//    @Autowired
//    private ZuYongGeMaH5ScanServiceImpl zuYongGeMaH5ScanServiceImpl;
//    @Autowired
//    private ZuYongPDDAllScanServiceImpl zuYongPDDAllScanServiceImpl;
//    @Autowired
//    private ZuYongPDD2ScanServiceImpl zuYongPDD2ScanServiceImpl;

    /**
     * @描述:H5支付分发
     * @时间:2017年12月28日 上午11:19:37
     */
    public JSONObject doRestPay (JSONObject reqData) {
        try {
            int source = reqData.getIntValue ("channelSource");
            switch (source) {
                case 11:
                    return wanShiDaH5Service.payOrder (reqData, tradeCmd.tradResultListener(reqData));
                case 50:case 139:
                    return zuYongPDD3ScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 51:case 104:
                    return funPayH5ServiceImpl.payOrder (reqData, tradeCmd.tradResultListener(reqData));
                case 69: case 76: case 82:
                    return chunPayScanService.payOrder (reqData, tradeCmd.tradResultListener(reqData));
                case 92:
                    return huiYunH5Service.payOrder (reqData, tradeCmd.tradResultListener(reqData));
                case 65: case 81: case 83:
                    return funPayMerchH5Service.payOrder (reqData, tradeCmd.tradResultListener(reqData));
                case 91:
                    return gaoYangZuyongService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 97:
                    return pinDuoDuoScanServiceImpl.payOrder (reqData, tradeCmd.tradResultListener(reqData));
                case 98:
                    return youFuH5ScanServiceImpl.payOrder (reqData, tradeCmd.tradResultListener(reqData));
                case 103:
                    return xinZhiFuScanServiceImpl.payOrder (reqData, tradeCmd.tradResultListener(reqData));
                case 125:
                    return aAPayH5Service.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 126:
                    return huaFeiScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 127:
                    return hongYunTScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 128:
                    return xunJieScanService.payOrder(reqData,tradeCmd.tradResultListener(reqData));
                case 131:
                    return lLH5ScanServiceImpl.payOrder(reqData,tradeCmd.tradResultListener(reqData));
                case 132:
                    return yaLongScanServiceImpl.payOrder(reqData,tradeCmd.tradResultListener(reqData));
                case 201:
                    return erYuanZHScanServiceImpl.payOrder(reqData,tradeCmd.tradResultListener(reqData));
                case 134:
                    return natieScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 133:
                    return qingHuScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 202:
                    return jieXinScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                //case 139:
                //    return peiQiScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 203:
                    return wangWangScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 204:
                    return nanNingScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 205:
                    return nanNingZHScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 143:
                	return zhongRenScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 206:
                    return pYFScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 207:
                    return chaoLiu2ScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 208:
                    return pengScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 209:
                    return chaoLiu3ScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 210:
                    return lJPDFScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 211:
                    return lSWPDDScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 213:case 216:case 217:case 218:case 219:case 220:case 221:case 222:case 223:case 224:case 225:
                case 240:case 241:case 242:case 243:case 244:case 245:case 246:case 247:case 248:case 249:
                case 260:case 261:case 262:case 263:case 264:case 265:case 266:case 267:case 268:case 269:
                case 215:case 140:case 142:case 111:case 212:case 141:
                case 228:case 229:case 230:case 231:case 232:case 233:case 234:case 235:case 236:case 237:case 238:
                    return zuYongPDD3ScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                /*case 212:
                    return zuYongPDD2ScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 215:
                    return zuYongPDDAllScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 228:case 229:case 230:case 231:case 232:case 233:case 234:case 235:case 236:case 237:case 238:
                    return zuYongGeMaH5ScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                 case 111:
                    return  pddH5Service.payOrder(reqData, tradeCmd.tradResultListener(reqData))
                 case 140 :
                    return zuYongScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                 case 141:
                	return zuYongDJHScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                  case 142:
                	return zuYongDFScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));*/
                case 214:
                    return xiaoNiaoScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 226:
                    return huFeiScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 227:
                    return suRenHuaFeiScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 239:
                    return zFBGMScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 250:
                    return weiBoScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 152:
                    return taoBDFScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 153:
                    return jinMoPDDScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 154:
                    return huaFeiNewScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 165:
                    return hangKongScanServiceImpl.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 301:
                    return piXiuScanService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 302:
                    return wolfCubScanService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                case 303:
                    return sunPayScanService.payOrder(reqData, tradeCmd.tradResultListener(reqData));
                default:
                    return Constant.failedMsg ("未知的H5支付通道" + source + ",请核实开通支付类型");
            }
        } catch (Exception e) {
            logger.error ("H5支付分发异常{}", reqData, e);
            return Constant.failedMsg ("H5支付分发异常");
        }
    }
}

