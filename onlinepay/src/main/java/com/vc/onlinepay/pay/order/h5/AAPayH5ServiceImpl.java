package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineOrderMadeMapper;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderAaService;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.alipay.AlipayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @描述:AA收款支付宝
 * @时间:2018年5月15日 22:14:30
 */
@SuppressWarnings ("deprecation")
@Service
@Component
public class AAPayH5ServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (AAPayH5ServiceImpl.class);
    @Autowired
    private VcOnlineOrderMadeMapper vcOnlineOrderMadeMapper;
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;
    @Autowired
    private VcOnlineOrderAaService vcOnlineOrderAaService;
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Value ("${onlinepay.project.domainName:}")
    private String domainName;

    @Value("${onlinepay.project.actualName:}")
    private String actualName;

    /**
     * @描述:AA收款支付宝支付下单
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        logger.info ("AA收款支付宝H5接口入参{}", reqData);
        JSONObject result = new JSONObject ();
        try {
            String vcOrderNo = reqData.getString("vcOrderNo").trim();
            if(StringUtils.isAnyEmpty(vcOrderNo)){
                return listener.failedHandler(Constant.failedMsg ("订单号获取为空"));
            }
            String aaDdPayUrlUrl = reqData.containsKey("aaDdPayUrlUrl")?reqData.getString("aaDdPayUrlUrl").trim():"";
            String alipayNo = reqData.containsKey("channelKey")?reqData.getString("channelKey").trim():"";
            String aaDdPayAAOrderNo = reqData.containsKey("aaDdPayAAOrderNo")?reqData.getString("aaDdPayAAOrderNo").trim():"";
            if(StringUtils.isAnyEmpty(alipayNo,aaDdPayUrlUrl,aaDdPayAAOrderNo)){
                return listener.failedHandler(Constant.failedMsg ("收款账信息为空"));
            }
            VcOnlineOrder vcOnlineOrder = new VcOnlineOrder();
            vcOnlineOrder.setOrderNo(vcOrderNo).setUpMerchNo (alipayNo).setOrderDes ("下单成功").setpOrder (aaDdPayAAOrderNo);
            int num = vcOnlineOrderService.updateUpMerchNoOrderNo(vcOnlineOrder);
            if(num<1){
                logger.error ("订单修改异常：{}",vcOrderNo);
                return listener.failedHandler (Constant.failedMsg ("订单修改异常"));
            }
            VcOnlineOrderMade made = VcOnlineOrderMade.buildAlipayMade (reqData,3);
            made.setQrcodeUrl (aaDdPayUrlUrl);
            int r = vcOnlineOrderMadeMapper.save(made);
            if (r < 1) {
                return listener.failedHandler (Constant.failedMsg ("保存链接失败"));
            }
            result = Constant.successMsg("下单成功");
            result.put("pOrder", reqData.getString("vcOrderNo"));
            result.put ("redirectUrl", StringEscapeUtils.unescapeJava (made.getOpenUrl ()));
            result.put ("bankUrl", StringEscapeUtils.unescapeJava (made.getOpenUrl ()));
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("AA收款支付宝支付异常", e);
            return listener.paddingHandler (Constant.failedMsg ("支付宝下单异常"));
        }
    }

    /**
     * @描述:showitemsTotal表示要生成多少个，total表示用金额，money表示没个人的平均金额
     * @作者:nada
     * @时间:2019/4/23
     **/
    public JSONObject getAAAppInfo (String accountNo,String orderNo,int aaAmount,int showTimes,int totalAmount) throws IOException {
        String nettyUrl = coreEngineProviderService.getCacheCfgKey ("online.alipay.netty.push.url");
        StringBuffer param = new StringBuffer ();
        param.append ("amount=").append (aaAmount);
        param.append ("&accountNo=").append (accountNo);
        param.append ("&orderNo=").append (orderNo);
        param.append ("&qrway=").append (3);
        param.append ("&showitemsTotal=").append (showTimes);
        param.append ("&total=").append (totalAmount);
        param.append ("&money=").append (aaAmount);
        String response = HttpClientTools.sendNettyUrlpost (nettyUrl, param.toString ());
        logger.info ("AA收款入参:{},响应结果:{}", param,response);
        return JSONObject.parseObject (response);
    }

    /**
     * @描述:获取钉钉AA收款
     * @作者:nada
     * @时间:2019/4/23
     **/
    public JSONObject getDdAAppInfo (String accountNo,String orderNo,int aaAmount) throws IOException {
        String nettyUrl = coreEngineProviderService.getCacheCfgKey ("online.alipay.netty.push.url");
        StringBuffer param = new StringBuffer ();
        param.append ("amount=").append (aaAmount);
        param.append ("&accountNo=").append (accountNo);
        param.append ("&orderNo=").append (orderNo);
        param.append ("&qrway=").append (1001);
        String response = HttpClientTools.sendNettyUrlpost (nettyUrl, param.toString ());
        logger.info ("获取钉钉AA收款入参:{},响应结果:{}", param,response);
        return JSONObject.parseObject (response);
    }

    public static void main (String[] args) {
        try {
//            String accountNo = "63-9560388152";
            int aaAmount = 1;
            String accountNo = "3316864949@qq.com";
//            long userId= 2088022607345634L;
            String orderNo = System.currentTimeMillis ()+"收款";
            String nettyUrl = "http://paypaul.mall51.top:18765/netty/push";
            // String nettyUrl = "http://test.mall51.top:9988/netty/push";
            int showTimes = AlipayUtils.AA_TOTAL_AMOUNT /aaAmount;
            if(showTimes > 100){
                showTimes = 100;
            }
            StringBuffer param = new StringBuffer ();
            param.append ("amount=").append (aaAmount);
            param.append ("&accountNo=").append (accountNo);
            param.append ("&orderNo=").append (orderNo);
            param.append ("&qrway=").append (3);
            param.append ("&showitemsTotal=").append (showTimes);
            param.append ("&total=").append (showTimes*aaAmount);
            param.append ("&money=").append (aaAmount);
            String response = HttpClientTools.sendNettyUrlpost (nettyUrl, param.toString ());
            logger.info ("AA收款入参:{}", param);
            logger.info ("响应结果:{}",response);
            // {"amount":"1.00","orderNo":"1557390027750收款","goodName":"1557390027750收款","accountNo":"3316864949@qq.com","payUrl":"{\"batchNo\":\"20190509000750021000630016014755\",\"resultStatus\":0,\"success\":true,\"token\":\"drhKGjq2\"}","time":3}
            //响应结果:{"amount":"1.00","orderNo":"1556467405998收款","goodName":"1556467405998收款","accountNo":"63-9560388152","payUrl":"{\"batchNo\":\"20190429000750021000590016045191\",\"resultStatus\":0,\"success\":true,\"token\":\"fXMRQXou\"}","time":1}
            //AA收款响应结果:{"amount":"1.00","orderNo":"1555994595334","goodName":"1555994595334","accountNo":"3316864949@qq.com","payUrl":"{\"batchNo\":\"20190423000750021000630015920315\",\"resultStatus\":0,\"success\":true,\"token\":\"CU9g0tdu\"}","time":1}
            //20190423000750021000630015920315
            //AA收款响应结果:{"amount":".10","orderNo":"1556003458318","goodName":"1556003458318","accountNo":"63-9560388152","payUrl":"{\"batchNo\":\"20190423000750021000590016002070\",\"resultStatus\":0,\"success\":true,\"token\":\"WjbVpkgf\"}","time":1}
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
