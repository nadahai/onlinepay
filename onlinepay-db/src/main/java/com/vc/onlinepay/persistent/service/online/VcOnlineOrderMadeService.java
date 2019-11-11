/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.service.online;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineOrderMadeMapper;
import com.vc.onlinepay.persistent.service.channel.ChannelSubNoServiceImpl;
import com.vc.onlinepay.utils.Constant;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单转换Service
 *
 * @author 订单转换
 * @version 2018-08-21
 */
@Service
@Transactional (readOnly = true)
public class VcOnlineOrderMadeService {

    private Logger logger = LoggerFactory.getLogger (getClass ());

    @Autowired
    private VcOnlineOrderMadeMapper vcOnlineOrderMadeMapper;

    @Autowired
    private ChannelSubNoServiceImpl channelSubNoServiceImpl;

    @Autowired
    private RedisCacheApi redisCacheApi;

    @Autowired
    private CoreEngineProviderService coreEngineProviderService;

    private static final String AA_PAY_URL = "alipays://platformapi/startapp?appId=60000154&url=%2fwww%2findex%2fdetail.htm%3fbatchNo%3dBATCHNO%26token%3dTOKEN%26source%3dqrCode";
    /**
     * @描述:查找
     * @时间:2018年8月21日 上午10:33:52
     */
    public VcOnlineOrderMade findOrderByOrderNo (String orderNo) {
        return vcOnlineOrderMadeMapper.findOrderByOrderNo (orderNo);
    }

    public List<VcOnlineOrderMade> findFlmOrders (VcOnlineOrderMade vcOnlineOrderMade){
        return vcOnlineOrderMadeMapper.findFlmOrders (vcOnlineOrderMade);
    }


    /**
     * @描述:描述当前信息 
     * @作者:nada
     * @时间:2019/3/26
     **/
    @Transactional (readOnly = false)
    public int updateQrcodelByOrderNo(VcOnlineOrderMade vcOnlineOrder){
        return vcOnlineOrderMadeMapper.updateQrcodelByOrderNo (vcOnlineOrder);
    }
    /**
     * @描述:查找历史订单记录
     * @时间:2018年8月21日 上午10:33:52
     */
    public VcOnlineOrderMade getRecordOrder (VcOnlineOrderMade made) {
        return vcOnlineOrderMadeMapper.getRecordOrder (made);
    }

    /**
     * @描述:修改
     * @时间:2018年1月31日 下午2:37:24
     */
    @Transactional (readOnly = false)
    public int updateByOrderNo (VcOnlineOrderMade vcOnlineOrder) {
        return vcOnlineOrderMadeMapper.updateByOrderNo (vcOnlineOrder);
    }

    /**
     * @描述:保存
     * @时间:2018年1月31日 下午2:32:08
     */
    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public int save (VcOnlineOrderMade vcOnlineOrder) {
        return vcOnlineOrderMadeMapper.save (vcOnlineOrder);
    }

    /**
     * @描述:获取链接信息
     * @时间:2018年1月31日 下午2:32:08
     */
    @Transactional (readOnly = false, rollbackFor = Exception.class)
    public JSONObject getOrderMadePayUrl (VcOnlineOrderMade vcOnlineOrder) {
        JSONObject result = new JSONObject ();
        try {
            if (vcOnlineOrder == null) {
                return Constant.failedMsg ("获取链接信息为空");
            }
            int r = vcOnlineOrderMadeMapper.save (vcOnlineOrder);
            if (r < 1) {
                return Constant.failedMsg ("保存链接失败");
            }
            //r = channelSubNoServiceImpl.updateLastOrderTime(new ChannelSubNo (vcOnlineOrder.getUpMerchNo()));
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "请求链接成功");
            result.put ("openUrl", vcOnlineOrder.getOpenUrl ());
            return result;
        } catch (Exception e) {
            logger.error ("获取链接信息异常", e);
            return Constant.failedMsg ("获取链接信息异常");
        }
    }


    /**
     * @描述:获取自研支付宝支付链接信息
     * @时间:2018年1月31日 下午2:32:08
     */
    @Transactional (readOnly = false)
    public JSONObject getOrderMadeAliPayUrl (VcOnlineOrderMade vcOnlineOrder) {
        JSONObject result = new JSONObject ();
        String payUrl = "";
        try {
            if (vcOnlineOrder == null) {
                return Constant.failedMsg ("获取支付链接信息为空");
            }
            if (StringUtils.isEmpty (vcOnlineOrder.getUpMerchNo ())) {
                return Constant.failedMsg ("未配置支付宝账号");
            }
            StringBuffer param = new StringBuffer ();
            DecimalFormat df = new DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.FLOOR);
            param.append ("seq=").append (System.currentTimeMillis ());
            param.append ("&accountNo=").append (vcOnlineOrder.getUpMerchNo ());
            param.append ("&amount=").append (df.format(vcOnlineOrder.getTraAmount ()));
            param.append ("&orderNo=").append (vcOnlineOrder.getOrderNo ());
            if(vcOnlineOrder.getOpenType()==3){
                // AA收款
                param.append ("&qrway=").append (2);
            }
            logger.info ("自研支付宝支付链接信息{}", param);
            String reString = HttpClientTools.sendUrlGet (coreEngineProviderService.getCacheCfgKey ("online.alipay.netty.push.url"), param.toString ());
            logger.info ("自研支付宝支付链接响应{}", reString);
            JSONObject payResult = JSONObject.parseObject (reString);
            payUrl = payResult.containsKey ("payUrl") ? payResult.getString ("payUrl") : "";
            if (StringUtils.isEmpty (payUrl)) {
                String msg = payResult.containsKey ("msg") ? payResult.getString ("msg") : "获取远程链接失败";
                return Constant.failedMsg (msg);
            }
            /*SupplierSubno subno = new SupplierSubno (vcOnlineOrder.getUpMerchNo (), vcOnlineOrder.getUpMerchKey ());
            int r = merchChannelServiceImpl.updateAliPayNum (subno);
            if (r < 1) {
                return Constant.failedMsg ("获取链接更新下单数失败");
            }*/
            String key = CacheConstants.LOOP_ROBIN_ALIPAY_NO + vcOnlineOrder.getUpMerchNo ();
            redisCacheApi.remove (key);
            vcOnlineOrder.setRemarks ("5");
            if(vcOnlineOrder.getOpenType()==3){
                // AA收款
                vcOnlineOrder.setOpenType(111);
                vcOnlineOrder.setQrcodeUrl(buildAAPayUrl(payUrl));
            } else {
                String qrcodeUrl = biuldAlipayUrl (payUrl);
                vcOnlineOrder.setQrcodeUrl (qrcodeUrl);
            }
            int r = vcOnlineOrderMadeMapper.save (vcOnlineOrder);
            if (r < 1) {
                return Constant.failedMsg ("保存支付链接信息失败");
            }
            r = vcOnlineOrderMadeMapper.saveTemplate (vcOnlineOrder);
            if (r < 1) {
                return Constant.failedMsg ("保存支付链接模板信息失败");
            }
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "请求链接成功");
            result.put ("payUrl", vcOnlineOrder.getOpenUrl ());
            return result;
        } catch (Exception e) {
            logger.error ("获取链接支付链接信息", e);
            return Constant.failedMsg ("获取链接支付链接信息异常");
        }
    }

    /**
     * 支付宝链接兼容IOS处理
     */
    public String biuldAlipayUrl (String qrcodeUrl) {
        try {
            String shortUrl = Constant.getDwzShortUrl (qrcodeUrl);
            shortUrl = Constant.getURLEncode (shortUrl);
            String head = "https://render.alipay.com/p/s/i?scheme=";
            String body = "alipays://platformapi/startapp?saId=10000007&qrcode=";
            String payUrl = head + Constant.getURLEncode (body + shortUrl);
            return payUrl;
        } catch (Exception e) {
            return qrcodeUrl;
        }
    }

    /**
     * @描述:AA收款链接抓换
     * @作者:zhaoyang
     * @时间:2019/01/2
     **/
    public String buildAAPayUrl (String payUrl) {
        try {
            JSONObject payJson= JSONObject.parseObject(payUrl);
            return AA_PAY_URL.replace ("BATCHNO", payJson.getString("batchNo")).replace ("TOKEN", payJson.getString("token"));
        } catch (Exception e) {
            logger.error (" 支付宝AA收款获取连接失败", e);
            return "";
        }
    }

    /**
     * @描述:异步通知下单
     * @作者:ChaiJing THINK
     * @时间:2018/8/24 16:48
     */
    public void orderUrlOpenEvent(JSONObject prms) {

        try {
            synchronized (prms) {
                ThreadUtil.execute(() -> {
                    if(prms.getString("payType").equals("99")){
                        logger.info("打开方式错误，不用通知{}",prms);
                        return;
                    }
                    String noticeUrl = coreEngineProviderService.getCacheCfgKey (CacheConstants.ONLINE_LKLORDER_NOTICE_URL);
                    String loginName = coreEngineProviderService.getCacheCfgKey(CacheConstants.ONLINE_LKL_LOGIN_NAME);
                    String loginPassword = coreEngineProviderService.getCacheCfgKey(CacheConstants.ONLINE_LKL_LOGIN_PASSWORD);
                    prms.put("loginName", loginName);
                    prms.put("loginPassword",loginPassword);
                    logger.info("异步下单通知{}",prms);
                    String result = null;
                    try {
                        result = HttpClientTools.baseHttpSendPost(noticeUrl,prms);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    logger.info("异步下单通知{}响应{}",prms,result);
                });
            }
        } catch (Exception e) {
            logger.error("异步下单通知异常",prms);
        }

    }
}