/**
 * @类名称:MainController.java
 * @时间:2017年10月27日下午5:55:40
 * @作者:nada
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.web.gate;

import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.pay.order.h5.FunPayH5ServiceImpl;
import com.vc.onlinepay.pay.order.scan.AnHuiShouQianBaScanServiceImpl;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.entity.channel.ChannelSubNo;
import com.vc.onlinepay.persistent.entity.merch.SupplierSubno;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderAa;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.channel.ChannelSubNoServiceImpl;
import com.vc.onlinepay.persistent.service.channel.SupplierSubnoServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderAaService;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.alipay.AlipayUtils;
import com.vc.onlinepay.utils.happypay.BiuldUtils;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


/**
 * @ClassName:  IndexController   
 * @Description: api接口调用
 * @author: lihai 
 * @date: 2018年4月11日 下午2:36:50  
 * @Copyright: 2018 www.guigu.com Inc. All rights reserved. 
 * 注意：本内容仅限于本信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Controller
@RequestMapping("/wechat")
@CrossOrigin ("*")
public class AliPayGateController extends BaseController {

    public static final Logger logger = LoggerFactory.getLogger(AliPayGateController.class);
    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;
    @Autowired
    private FunPayH5ServiceImpl funPayH5Service;
    @Autowired
    private CommonPayService commonPayService;
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Autowired
    private SupplierSubnoServiceImpl supplierSubnoService;
    @Autowired
    private VcOnlineOrderAaService vcOnlineOrderAaService;
    @Autowired
    private AnHuiShouQianBaScanServiceImpl anHuiShouQianBaScanService;
    @Autowired
    private ChannelSubNoServiceImpl channelSubNoServiceImpl;
    
    
	@Value("${onlinepay.project.domainName:}")
    private String domainName;
    @Value("${onlinepay.project.actualName:}")
    private String actualName;

    //支付宝服务窗授权获取用户信息
    private static final String ALIPAY_AUTH_USER = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id="+ BiuldUtils.APP_ID+"&scope=auth_user&redirect_uri=";

    //生产环境
    private static String ALIPAY_APP_ID = "2019022463364107";
    private static String ALIPAY_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCq8uyOMszizxBNMai1OtOil+IWhbBl2yd2CqfL3YUqUANI/yfuQ6fllBmjbVQrz4j3pHe1qyw0B3PJ97La7PGr0yuqQgu/NnBzvGvqO4CNApnXBZjV1Xu/aNBWh9XFENf4ORU6nGNhj9jr4pJrN6l+5uJ8T5kYWqfHcUUYDMsbtu1YjQpSq/TIXetNKcZnuYsq5Qjera01odrJbhhDAoBs3RGhCWaBxv3MSFCz9xNumCmsoIQu29JhVz4/B55OdSebrTfATEzOwGxuFs4T0KXJj3dL10bQqHJqqDMnjaoJStart1F6mrgLkEKVs2aP09cVdbC1y/miCSI1y0diTgTdAgMBAAECggEATvEPPDtJUhO8u2kLN2WLBbKNvUjPRLoHZwNUqVgKfpekbLknf2fOyL2zeTyree/EmFdi0InTR9OJLOMtvNteXrKNn3oQYqSJGWkRjIEdxABHenwjL9v94U5NpyfjF7XHheEWZJKDcjIzQfrHEqwJoYiNXkqDsDNs9zTfa1O9F3aE8QJmYH5JFUT+k38mqR5DTMVgkLHwPzU8pdoEi9HGSuTZ2odFKRZ5/k/9Vu04hc5/fnUFIUomEiu3V8R8FzJjGrv2aCtGT5q5p1vGClYXsJEeeSEqX42o4CQEmBGPTrv0mAA5IeqU23hs9ktnx+8WAa+a/Ldpa1utdt2kfwaVIQKBgQD/BVWh9RPiAWUHUs60+Z0D/8bIdHZW1rKI1e5DKLLxc7FfBnuP2IjdkIDzXx96IqoM40lGaX1edroTgQB/DOuiuPKv3J0JJ2VNu82WNe+ObUmWUnhJTCBm/hLZKTsXi9KHiUPM4HtxTL3Dkud++KVUUVH6F8COGQP+nuiE0IHrXwKBgQCrmvQUfx6XHKkBi8zfazpEFKVHMsW1pGMBVCUh/tm5omPBxG7ZBs0WyWXRzXprl8w7Zky4z7qPBBxgPZ+NdhAY+XlalRqr18y6qSwS7ysJ57yDtEgnzlxB/FfdtvHxbiYPfX7x33Qld7kMUbKt+WHSreVZQyRWvJwskXIgdSV1QwKBgQCUEeDanJXiz7R9QBNM+PG19LjSguyDFz2qPayNyf+8OdRuvDDaIHu3ScPVixGXtLDPsthEzdNBGeaIlIpZOoNGg+RFP+7d9cXYEIcaBE8Hf2UOpuu4gz79DeCbvljVHxYqJAT93AlQi6JS/+Tx0CUOg/j5IPloiBXNrS6MjxQgOQKBgAdbm3+Ne8hK6EwyrFQgCt2EbRnCaYvCQqR58SWmAbvd5J0YSRBxJDYH6J+4Sbl3RsB9QGjkL0GWkYjm24J7P3FysOtbXUtk81hFjKg7LQM9tm2HO1jJllcV9MaC45jQej1LyjegtyAsI/kNP7YJ7VHVNvI+2L4HVELs8ZHGtBZ1AoGAJSXQ4l48ore33DmQ2HbZO6Yl+zgR4SW+2yjhaUTQso9JyCNEUMVokJOIfR9Ufos8BT9iOWZZS3UJLgQCgxWr47XoPCwOdRU2qcEqnmrqRAg72EAkvP35GiYhIcUFiI3gMVGme1Z35asA0zeP/1N467w4huxTO6KMDJNHhmf3vUk=";

    private static final String ALIPAY_CODE_URL = "http://online.toxpay.com/xpay/wechat/";
    private static  final String ALIPAY_OPEN_AUTH_URL = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id="+ALIPAY_APP_ID+"&scope=auth_base&redirect_uri=";

    /**
     * @描述:获取userID
     * @作者:nada
     * @时间:2019/3/11
     **/
    @RequestMapping("getAlipayCode")
    public ModelAndView getAlipayCode(HttpServletRequest request,HttpServletResponse response){
        ModelAndView mode = new ModelAndView("failure");
        try {
            String returnMethod = request.getParameter("returnMethod");
            StringBuilder jumpUrl = new StringBuilder(ALIPAY_CODE_URL);
            if(StringUtil.isNotEmpty (returnMethod)){
                jumpUrl.append (returnMethod);
            }else{
                jumpUrl.append("returnAlipayCode");
            }
            Map<String, String> reqPrms =  HttpRequestTools.getRequest (request);
            if(reqPrms !=null && !reqPrms.isEmpty () && reqPrms.size ()>0){
                jumpUrl.append ("?1=1");
                for (String key : reqPrms.keySet ()) {
                    jumpUrl.append ("&").append (key).append ("=").append (reqPrms.get (key));
                }
            }
            return new ModelAndView("redirect:"+ALIPAY_OPEN_AUTH_URL+URLEncoder.encode(jumpUrl.toString (),"utf-8"));
        } catch (Exception e) {
            logger.error ("支付宝授权异常",e);
            mode.addObject("msg","支付宝授权失败");
            return mode;
        }
    }
    /**
     * @描述:仅获取UserID返回
     * @作者:nada
     * @时间:2019/3/11
     **/
    @RequestMapping("returnAlipayCode")
    @ResponseBody
    public String returnAlipayCode(HttpServletRequest request,HttpServletResponse response){
        try {
            String code = request.getParameter("auth_code");
            String userid = request.getParameter("userid");
            if(StringUtils.isEmpty (userid)){
                userid = BiuldUtils.getAlipayUserId(code,ALIPAY_APP_ID,ALIPAY_PRIVATE_KEY);
            }
            logger.info ("解析的userId:{}",userid);
            return userid;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error ("获取用户的UserId异常",e);
            return "获取用户的UserId异常";
        }
    }


    /**
     * @描述:扫码测试收钱吧
     * @时间:2018年9月12日 下午2:24:27
     */
    @RequestMapping (value = "sqbPayTest", produces = "text/html;charset=UTF-8")
    public ModelAndView sqbPayTest (HttpServletRequest request, HttpServletResponse response) {
        super.setHttpServletContent(request, response);
        try {
            String upMerchNo = request.getParameter ("upMerchNo");
            String amount = request.getParameter ("amount");
            String pwd = request.getParameter ("pwd");
            String type = request.getParameter ("type");
            if(StringUtils.isAnyEmpty (upMerchNo,amount,pwd)){
                return writeResponse(response,Constant.failedMsg("no,pwd,amount信息不完整"));
            }
            if(StringUtil.isEmpty (type)){
                return writeResponse(response,Constant.failedMsg("type仅支持1,2"));
            }
            int openType = 93;
            if ("1".equalsIgnoreCase (type)){
                openType = 92;
            }
            JSONObject terminalInfoJson = anHuiShouQianBaScanService.getTerminalInfo (upMerchNo,pwd);
            if (terminalInfoJson == null || terminalInfoJson.isEmpty ()) {
                return writeResponse(response,Constant.failedMsg("设备信息失败"));
            }
            if(!terminalInfoJson.getString ("code").equalsIgnoreCase (Constant.SUCCESSS)){
                String mgs = terminalInfoJson.containsKey ("msg")?terminalInfoJson.getString ("msg"):"设备登录失败";
                return writeResponse(response,Constant.failedMsg(mgs));
            }
            String vcOrderNo = System.currentTimeMillis ()+"";
            String retUrl ="http://online.toxpay.com/xpay/shouQianBaPayCallbackApi";
            String wapUrl ="http://online.toxpay.com/xpay/shouQianBaPayCallbackApi/returnWap";
            String terminalInfo = terminalInfoJson.getString ("msg");
            String res = anHuiShouQianBaScanService.preCreate (terminalInfo,vcOrderNo,Constant.changeBranch (new BigDecimal (amount)).toString (),wapUrl, retUrl,type);
            String payUrl = StringEscapeUtils.unescapeJava(res);
            StaticLog.info ("收钱吧下单结果：{},url:{}", res,payUrl);

            VcOnlineOrderMade made = new VcOnlineOrderMade();
            made.setOpenUrl ("http://online.toxpay.com/xpay/code/" + HiDesUtils.desEnCode (vcOrderNo));
            made.setPaySource (93);
            made.setChannelId (231);
            made.setOpenType (openType);
            made.setExpiredTime (CacheConstants.EXPIRED_TIME_5);
            made.setMerchNo ("999941000001");
            made.setTraAmount(new BigDecimal (amount));
            made.setOrderNo (vcOrderNo);
            made.setUpMerchKey(pwd);
            made.setUpMerchNo(upMerchNo);
            made.setRemarks (pwd);
            made.setQrcodeUrl (pwd);
            made.setOpenType (openType);
            made.setQrcodeUrl (payUrl);
            JSONObject resMade = onlineOrderMadeService.getOrderMadePayUrl (made);
            return writeResponse(response,Constant.successMsg (resMade.getString ("openUrl")));
        } catch (Exception e) {
            logger.error ("扫码测试收钱吧异常", e);
            return writeResponse(response,Constant.failedMsg("扫码测试收钱吧异常"));
        }
    }

    /**
     * @描述:扫码测试支付宝
     * @时间:2018年9月12日 下午2:24:27
     */
    @RequestMapping (value = "alipayPayTest")
    public ModelAndView receiptAlipayJump (HttpServletRequest request, HttpServletResponse response) {
    	logger.info ("测试收款入request:{}", request);
        ModelAndView mode = new ModelAndView ("failure");
        try {
            String id = request.getParameter ("id");
            String amount1 = request.getParameter ("amount");
            String upMerchNo1 = request.getParameter ("upMerchNo");
            SupplierSubno  supplierSubno = null;
            if(StringUtil.isNotEmpty (id)){
                supplierSubno = supplierSubnoService.getSubnoById (Long.valueOf (id));
                logger.info ("测试收款入id:{}", supplierSubno);
            }
            if(StringUtil.isNotEmpty (upMerchNo1)){
            	ChannelSubNo channelSubNo = new ChannelSubNo();
            	channelSubNo.setUpMerchNo(upMerchNo1);
            	channelSubNo = channelSubNoServiceImpl.getByUpMerchNo(channelSubNo);
                //supplierSubno = supplierSubnoService.getOneByUpMerchNo (upMerchNo1);
                logger.info ("测试收款入upMerchNo:{}", supplierSubno);
                if(channelSubNo == null){
                	mode.addObject("msg","无可用账号");
                    return mode;
                }
                
                String bankUrl = AlipayUtils.buildBankPayUrl (
                		channelSubNo.getCardIdx(),
                		channelSubNo.getRemarks(),
                		channelSubNo.getBankMark(),
                		channelSubNo.getName(),String.valueOf (amount1));
                mode.addObject ("payUrl", bankUrl);
                logger.info ("银行卡转账:{}",bankUrl);
                String prefix ="https://ds.alipay.com/?from=mobilecodec&scheme=";
                String targetUrl = prefix.concat(URLEncoder.encode(bankUrl,"utf-8"));
                mode = new ModelAndView("redirect:".concat(targetUrl));
                return mode;
                
            }
            if(supplierSubno == null){
                List<SupplierSubno> subnos = supplierSubnoService.getAllSupplierList(SupplierSubno.getSupplierSubno (51));
                if(subnos == null || subnos.size() <1){
                    mode.addObject("msg","账号修整中,请稍后再试");
                    return mode;
                }
                Collections.shuffle(subnos);
                supplierSubno = subnos.get(0);
            }
            String userId = supplierSubno.getUserId ();
            String upMerchNo = supplierSubno.getUpMerchNo ();
            String userName = supplierSubno.getName ();
            int type = supplierSubno.getType ();
            String orderNo = System.currentTimeMillis ()+"";
            logger.info ("测试收款入参userid:{},amount:{},upMerchNo:{}", userId, amount1, upMerchNo);
            if (StringUtils.isAnyEmpty (userId, amount1, upMerchNo)) {
                mode.addObject ("msg", "userid,amount,upMerchNo,type不能为空");
                return mode;
            }
            BigDecimal amount = new BigDecimal(amount1).setScale(2,BigDecimal.ROUND_HALF_UP);
            switch (type) {
                case 2:
                    mode = new ModelAndView ("alipay/xyTransJump");
                    mode.addObject ("a", String.valueOf (amount));
                    mode.addObject ("u", userId);
                    mode.addObject ("m", orderNo);
                    return mode;
                case 4:case 6:
                    String cardIndex = supplierSubno.getCardIdx();
                    String accountName = userName;
                    String cardNo = supplierSubno.getBankNo ();
                    String bankMark = supplierSubno.getBankMark ();
                    String bankRemarks = supplierSubno.getRemarks ();
                    if(StringUtil.isEmpty (bankRemarks)){
                        bankRemarks = cardNo;
                    }
                    if(StringUtil.isNotEmpty (cardIndex)){
                        String bankUrl = AlipayUtils.buildBankPayUrl (cardIndex,bankRemarks,bankMark,accountName,String.valueOf (amount));
                        mode.addObject ("payUrl", bankUrl);
                        logger.info ("银行卡转账:{}",bankUrl);
                        String prefix ="https://ds.alipay.com/?from=mobilecodec&scheme=";
                        String targetUrl = prefix.concat(URLEncoder.encode(bankUrl,"utf-8"));
                        mode = new ModelAndView("redirect:".concat(targetUrl));
                        return mode;

                    }
                    mode = new ModelAndView ("alipay/toCardJump");
                    mode.addObject ("u", URLEncoder.encode (userName, "UTF-8"));
                    mode.addObject ("c", cardNo);
                    mode.addObject ("b", bankMark);
                    mode.addObject ("a", amount);
                    return mode;
                case 7:
                    mode = new ModelAndView("cashier/qrAlipayPay");
                    String url = AlipayUtils.biuldAlipayUrl (amount, orderNo,userId, userName);
                    mode.addObject("qrImgUrl",url);
                    mode.addObject("qrcodeUrl",url);
                    mode.addObject("remarks", "请验证姓名（姓"+userName+"）");
                    return mode;
                case 10:
                    mode = new ModelAndView ("alipay/receiptJump");
                    String friendUrl = "alipays://platformapi/startapp?appId=20000186&actionType=addfriend&userId=" + userId + "&loginId=" + upMerchNo + "&source=by_home&alert=true";
                    String userUrl = "http://online.toxpay.com/xpay/wechat/getAlipayCode?returnMethod=returnTestJump&upMerchNo=" + upMerchNo + "&amount=" + amount + "+&orderNo=" + orderNo;
                    String alipayUrl = "alipayqr://platformapi/startapp?saId=10000007&qrcode=" + URLEncoder.encode (userUrl, "utf-8");
                    String jumpUrl = "https://ds.alipay.com/?from=mobilecodec&scheme=" + URLEncoder.encode (alipayUrl, "utf-8");
                    mode.addObject ("friendUrl", friendUrl);
                    mode.addObject ("jUrl", jumpUrl);
                    mode.addObject ("fUrl", friendUrl);
                    return mode;
                default:
                    mode.addObject ("msg", "不支持类型"+type);
                    return mode;
            }

        } catch (Exception e) {
            logger.error ("扫码测试支付宝异常", e);
            mode.addObject ("msg", "扫码测试支付宝异常");
            return mode;
        }
    }

    /**
     * @描述:获取支付宝USERID
     * @时间:2019/3/8
     **/
    @RequestMapping("getAlipayUserId")
    public ModelAndView getAlipayUserId(HttpServletRequest request){
        ModelAndView mode = new ModelAndView("failure");
        try {
            String orderNo = request.getParameter("orderNo");
            String dev = request.getParameter("dev");
            String userUrl = ALIPAY_CODE_URL.concat("aaReceipt?dev=").concat(dev + "&orderNo=" + orderNo);
            String redirectUrl = ALIPAY_OPEN_AUTH_URL + URLEncoder.encode(userUrl, "utf-8");
            logger.info("orderNo:{},dev:{},redirectUrl:{}",orderNo,dev,redirectUrl);
            return new ModelAndView("redirect:"+redirectUrl);
        } catch (Exception e) {
            logger.error ("获取支付宝USERID异常",e);
            mode.addObject("message","获取支付宝USERID异常");
            return mode;
        }
    }

    /**
     * @描述:AA收款
     * @时间:2019/3/8
     **/
    @RequestMapping("aaReceipt")
    public ModelAndView aaUserId(HttpServletRequest request,HttpServletResponse response){
        ModelAndView mode = new ModelAndView("failure");
        try {
            String code = request.getParameter("auth_code");
            String no = request.getParameter("orderNo");
            String userid = request.getParameter("userid");
            String dev = request.getParameter("dev");
            logger.info("授权回调:code:{},no:{},userid:{},dev:{},error_scope:{}",code,no,userid,dev);
            if(StringUtils.isEmpty (userid)){
                userid = BiuldUtils.getAlipayUserId(code,ALIPAY_APP_ID,ALIPAY_PRIVATE_KEY);
            }
            if("test".equals (dev)){
                userid = System.currentTimeMillis ()+"";
            }
            logger.info ("解析:{},订单号:{},环境:{}",userid,no,dev);
            if(StringUtils.isAnyEmpty(no,userid)){
                mode.addObject("message","Alipay Internal Argument Error");
                return mode;
            }
            String orderNo = HiDesUtils.desDeCode(no);
            logger.info("解析订单号:{},解密订单号{}", no,orderNo);
            if (StringUtils.isEmpty(orderNo) || "0".equals(orderNo)) {
                mode.addObject("message","Alipay Order No Internal Argument Error");
                return mode;
            }
            VcOnlineOrderMade made = onlineOrderMadeService.findOrderByOrderNo(orderNo);
            if (made == null || made.getOpenNum () > 2){
                mode.addObject("message","订单一次有效，请重新发起");
                return mode;
            }
            int amount = made.getTraAmount ().intValue ();
            String userId = StringUtils.deleteWhitespace (userid);
            String upMerchNo = StringUtils.deleteWhitespace (made.getUpMerchNo ());

            //1,当前tradeNo收款的库存情况
            VcOnlineOrderAa vcOnlineOrderAa = new VcOnlineOrderAa();
            vcOnlineOrderAa.setUserid (userId);
            vcOnlineOrderAa.setTradeno (made.getUpMerchKey ());
            vcOnlineOrderAa.setUpMerchNo (upMerchNo);
            List<VcOnlineOrderAa> list = vcOnlineOrderAaService.findByUserId (vcOnlineOrderAa);
            if(list == null || list.size ()<1){
                vcOnlineOrderAa.setId (new BigDecimal (made.getRemarks ()));
                vcOnlineOrderAa.setOrderNo (orderNo);
                int r = vcOnlineOrderAaService.updateUserId (vcOnlineOrderAa);
                if (r < 1) {
                    logger.error ("AA收款异常修改USERID状态失败{}",orderNo);
                    mode.addObject("message","修改失败，请稍后重试");
                    return mode;
                }
                logger.info ("第一次收款:{},结果:{}",orderNo,made.getQrcodeUrl ());
                mode = new ModelAndView("alipay/toAAJump");
                mode.addObject("qrcodeUrl",made.getQrcodeUrl ());
                return mode;
            }
            //2,获取一条已经存在的AA收款记录信息
            if(list.size () == 1){
                vcOnlineOrderAa = list.get (0);
            }
            if(list.size ()>1 || vcOnlineOrderAa.getStatus () == 4){
                VcOnlineOrderAa otherAa = new VcOnlineOrderAa();
                otherAa.setUserid (userId);
                otherAa.setTradeno (vcOnlineOrderAa.getTradeno ());
                otherAa.setAmount (amount);
                vcOnlineOrderAa = vcOnlineOrderAaService.findOtherUserId (otherAa);
            }
            if(vcOnlineOrderAa == null){
                logger.error ("AA收款异常没有库存{}",orderNo);
                mode.addObject("message",amount+"库存不足，请稍后重试");
                return mode;
            }
            VcOnlineOrder vcOnlineOrder = new VcOnlineOrder();
            vcOnlineOrder.setOrderNo(orderNo).setUpMerchNo (upMerchNo);
            int num = vcOnlineOrderService.updateUpMerchNoOrderNo(vcOnlineOrder);
            if(num<1){
                logger.error ("AA收款异常订单保存支付宝账号异常{}",orderNo);
                mode.addObject("message","修改订单失败，请稍后重试");
                return mode;
            }
            String purl = AlipayUtils.buildAAPayUrl(vcOnlineOrderAa.getTradeno (),vcOnlineOrderAa.getToken ());
            logger.info ("第二次收款:{},结果:{}",orderNo,purl);
            mode = new ModelAndView("alipay/toAAJump");
            mode.addObject("qrcodeUrl",purl);
            return mode;
        } catch (Exception e) {
            logger.error ("AA收款异常",e);
            mode.addObject("message","支付异常");
            return mode;
        }
    }

    /**
     * @描述:测试个人收款
     * @作者:nada
     * @时间:2019/3/11
     **/
    @RequestMapping("returnTestJump")
    public ModelAndView returnTestJump(HttpServletRequest request,HttpServletResponse response) throws IOException {
        ModelAndView mode = new ModelAndView("failure");
        try {
            String orderNo = request.getParameter("orderNo");
            String amount = request.getParameter("amount");
            String upMerchNo =  request.getParameter("upMerchNo");
            String userid = BiuldUtils.getAlipayUserId(request.getParameter("auth_code"),ALIPAY_APP_ID,ALIPAY_PRIVATE_KEY);
            logger.info("测试收款响应接收userid:{},amount:{},orderNo:{},upMerchNo:{}",userid,amount,orderNo,upMerchNo);
            if(StringUtils.isAnyEmpty (userid,amount,orderNo,upMerchNo)){
                mode.addObject("msg","userid,amount,orderNo,upMerchNo不能为空");
                return mode;
            }
            JSONObject payResult = funPayH5Service.pushNettyUrl (10,upMerchNo,orderNo,new BigDecimal (amount),userid);
            String payUrl = payResult.containsKey ("payUrl") ? payResult.getString ("payUrl") : "";
            if(StringUtils.isNoneEmpty (payUrl)){
               /* String url = "alipays://platformapi/startapp?appId=20000090&actionType=toBillDetails&tradeNO="+payUrl;
                String jumpUrl = "https://ds.alipay.com/?from=mobilecodec&scheme="+URLEncoder.encode(url, "utf-8");
                return new ModelAndView("redirect:"+jumpUrl);*/
                mode = new ModelAndView("alipay/okReceiptJump");
                mode.addObject("n",payUrl);
                return mode;
            }
            mode.addObject("msg","支付宝下单失败");
            return mode;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error ("获取用户的UserId异常",e);
            mode.addObject("msg","支付宝账号异常");
            return mode;
        }
    }

    /**
     * @描述:返回UserId执行业务逻辑
     * @时间:2019/3/8
     **/
    @RequestMapping("returnAlipayUserId")
    public ModelAndView returnAlipayUserId(HttpServletRequest request,HttpServletResponse response){
        ModelAndView mode = new ModelAndView("failure");
        try {
            String code = request.getParameter("auth_code");
            String no = request.getParameter("orderNo");
            String userid = request.getParameter("userid");
            if(StringUtils.isEmpty (userid)){
                userid = BiuldUtils.getAlipayUserId(code,ALIPAY_APP_ID,ALIPAY_PRIVATE_KEY);
            }
            logger.info ("获取UserId:{},订单号:{}",userid,no);
            if(StringUtils.isAnyEmpty(no,userid)){
                mode.addObject("msg","Alipay Internal Argument Error");
                return mode;
            }
            String orderNo = HiDesUtils.desDeCode(no);
            logger.info("解析订单号:{},解密订单号{}", no,orderNo);
            if (StringUtils.isEmpty(orderNo) || "0".equals(orderNo)) {
                mode.addObject("msg","Alipay Order No Internal Argument Error");
                return mode;
            }
            int r = vcOnlineOrderMadeService.updateQrcodelByOrderNo(new VcOnlineOrderMade (orderNo,userid));
            if(r<1){
                mode.addObject("msg","保存UserID失败");
                return mode;
            }
            VcOnlineOrderMade made = onlineOrderMadeService.findOrderByOrderNo(orderNo);
            if (made == null){
                mode.addObject("msg","订单不存在，请重新发起");
                return mode;
            }
            if(made.getOpenNum () > 3){
                mode.addObject("msg","订单一次有效，请重新发起");
                return mode;
            }
            JSONObject reqData = new JSONObject();
            reqData.put ("vcOrderNo",orderNo);
            made.setPayUserId (StringUtils.deleteWhitespace (userid));
            JSONObject result = funPayH5Service.buildAliPayUrl (made.getOpenType (),made,getResultListener (reqData));
            logger.info ("响应次数:{},结果:{}",made.getOpenNum (),result);
            String payUrl = result.containsKey ("payUrl")?result.getString ("payUrl"):"";
            if(StringUtil.isEmpty (payUrl)){
                mode.addObject("msg","支付宝下单失败");
                return mode;
            }
            int openType = made.getOpenType ();
            if(openType == 1){
                mode = new ModelAndView("alipay/toAAJump");
                mode.addObject("qrcodeUrl",payUrl);
                return mode;
            }else if(openType == 10){
                mode = new ModelAndView("alipay/okReceiptJump");
                mode.addObject("n",payUrl);
                return mode;
            }
            mode.addObject("msg","支付宝下单失败");
            return mode;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error ("获取用户的UserId异常",e);
            mode.addObject("msg","支付宝账号异常");
            return mode;
        }
    }

    /**
     * @描述:获取支付宝监听
     * @时间:2017年12月19日 下午3:42:31
     */
    public ResultListener getResultListener(JSONObject reqData){
        return new ResultListener() {
            @Override
            public JSONObject successHandler(JSONObject resultData) {
                logger.info("获取支付宝监听successHandler结果:{}",resultData);
                String message = resultData.containsKey("msg")?resultData.getString("msg"):"下单成功";
                commonPayService.updateOrderStatus(reqData.getString("vcOrderNo"), 1, message);
                return resultData;
            }
            @Override
            public JSONObject paddingHandler(JSONObject resultData) {
                logger.info("获取支付宝监听paddingHandler结果:{}",resultData);
                return resultData;
            }
            @Override
            public JSONObject failedHandler(JSONObject resultData) {
                logger.info("获取支付宝监听failedHandler结果:{}",resultData);
                commonPayService.updateOrderStatus(reqData.getString("vcOrderNo"), 2, resultData.containsKey("msg")?resultData.getString("msg"):"下单失败");
                return Constant.failedMsg("下单失败,请稍后重试");
            }
        };
    }

}
