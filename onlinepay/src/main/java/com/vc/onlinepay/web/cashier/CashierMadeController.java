package com.vc.onlinepay.web.cashier;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.alipay.AlipayUtils;
import com.vc.onlinepay.utils.http.HttpBrowserTools;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import com.vc.onlinepay.web.base.BaseController;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @类名称:CashierMadeController.java
 * @时间:2018年8月21日上午11:38:30
 * @版权:公司 Copyright (c) 2018
 */
@Controller
public class CashierMadeController extends BaseController {

    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;
    @Autowired
    private VcOnlineOrderServiceImpl orderService;
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Autowired
    private RedisCacheApi redisCacheApi;

    @Value ("${onlinepay.project.domainName:}")
    private String domainName;
    @Value ("${onlinepay.project.actualName:}")
    private String actualName;

    private static final String DES_KEY = "C6FD3C90";

    private static final String CASHIER_ORDER = "CASHIER_ORDER_";

    private final static String startAlipay_url = "alipayqr://platformapi/startapp?saId=10000007&qrcode=";

    //private final static String FINAL_ALIPAY_QRCODE = "alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={'a':'AMOUNT','u':'USERID','m':'ORDERID'}";

    private final static String FINAL_ALIPAY_QRCODE = "alipays://platformapi/startapp?appId=20000123&actionType=scan&s=AMOUNT&a=AMOUNT&u=USERID&m=ORDERID";

    /**
     * @描述: 自研支付包装收银台 91:微信 92:支付宝 95记录打开 99:好友转账  89：协议转账 87:协议转账 111:AA收款 110:转账到银行卡 120:预授权支付 86:惠云H5
     * @时间:2018年8月21日 上午11:38:42
     */
    @RequestMapping (value = "/api/{no}", method = RequestMethod.GET)
    public ModelAndView autoCashier (HttpServletRequest request, HttpServletResponse response, @PathVariable String no) {
        try {
            ModelAndView mode = new ModelAndView ("failure");
            //验证订单
            if (StringUtils.isEmpty (no)) {
                super.writeResponse (response, Constant.failedMsg ("订单号为空，请重新发起"));
                return null;
            }
            String orderNo = HiDesUtils.desDeCode (no);
            logger.info ("自研包装链接订单号:{},解密订单号{}", no, orderNo);
            if (StringUtils.isEmpty (orderNo) || "0".equals (orderNo)) {
                super.writeResponse (response, Constant.failedMsg ("解析订单失败，请重新发起"));
                return null;
            }
            //验证订单有效性
            VcOnlineOrderMade made = onlineOrderMadeService.findOrderByOrderNo (orderNo);
            if (made == null) {
                mode.addObject ("msg", orderNo + "订单不存在，请重新发起");
                return mode;
            }
            if (StringUtil.isEmpty (made.getUserId ())) {
                mode.addObject ("msg", "用户ID不存在");
                return mode;
            }
            if (made.getOpenNum () > 1) {
                VcOnlineOrder onlineOrder = orderService.findOrderByOrderNo (orderNo);
                if (onlineOrder == null) {
                    mode.addObject ("msg", orderNo + "非法订单，请重新发起");
                    return mode;
                }
                //状态 1下单成功 2下单失败 3下单中 4支付成功 5支付失败 6:处理中 7:代付中 8:代付失败 9交易退款
                int status = onlineOrder.getStatus ();
                if (status == 2) {
                    mode.addObject ("msg", orderNo + "下单失败，请重新发起");
                    return mode;
                } else if (status == 4) {
                    mode = new ModelAndView ("success");
                    return mode;
                } else if (status == 5) {
                    mode.addObject ("msg", orderNo + "订单支付失败，请重新发起");
                    return mode;
                }
            }
            long diff = System.currentTimeMillis () - made.getCreateDate ().getTime ();
            if (diff > made.getExpiredTime () * 1000) {
                mode.addObject ("msg", orderNo + "订单过期，请重新发起");
                return mode;
            }

            //更新自研包装打开类型
            int opentype = made.getOpenType ();
            int r = onlineOrderMadeService.updateByOrderNo (this.buildNewUpadateMade (opentype, orderNo, request));
            if (r < 1) {
                mode.addObject ("msg", orderNo + "订单更新失败，请重新发起");
                return mode;
            }

            int openNum = made.getOpenNum ();
            String upmerchNo = made.getUpMerchNo ();
            BigDecimal amount = made.getTraAmount ().setScale (2, BigDecimal.ROUND_HALF_UP);
            String userId = made.getUserId ();
            boolean  isAlipay = HttpBrowserTools.isAlipay(request);
            logger.info ("收款类型：{},转账跳转:{}", opentype, made.getQrcodeUrl ());
            switch (opentype) {
                case 1:
                    String openUrl = "http://online.toxpay.com/xpay/wechat/getAlipayUserId?orderNo=" + no;
                    return new ModelAndView("redirect:"+openUrl);
                case 2:
                    mode = new ModelAndView ("alipay/xyTransJump");
                    mode.addObject ("a", String.valueOf (amount));
                    mode.addObject ("u", userId);
                    mode.addObject ("m", orderNo);
                    return mode;
                case 3:
                    logger.info("AA收款跳转页面:{}", made.getQrcodeUrl());
                    mode = new ModelAndView("alipay/toDDAAJump");
                    mode.addObject("qrcodeUrl",made.getQrcodeUrl());
                    return mode;
                case 4:
                    String targetUrl = made.getQrcodeUrl ();
                    if(!isAlipay){
                        mode = new ModelAndView("alipay/toCardJump6");
                        //mode.addObject("copyUrl",Constant.getShortUrl(made.getOpenUrl()));
                        mode.addObject("copyUrl",made.getOpenUrl());
                    }else{
                        mode = new ModelAndView("alipay/toCardJump5");
                    }
                    mode.addObject("amount",amount);
                    mode.addObject("orderNo", made.getOrderNo());
                    mode.addObject("openUrl",made.getOpenUrl());
                    mode.addObject("payUrl",targetUrl);
                    return mode;
                    /*String prefix ="https://ds.alipay.com/?from=mobilecodec&scheme=";
                    String targetUrl = prefix.concat(URLEncoder.encode(made.getQrcodeUrl (),"utf-8"));
                    logger.info ("支付宝转账到银行卡:{}",targetUrl);
                    if(!isAlipay){
                        vcOnlineOrderService.updateOrderDes(new VcOnlineOrder(orderNo,"非扫码跳H5",10));
                    }
                    if(orderNo.contains ("h5") || !isAlipay){
                    	//PC端
                    	if("177".equals(made.getChannelId()+"")) {
                    		mode = new ModelAndView("alipay/CardCashierQrcodePC");
                            mode.addObject("orderNo",made.getOrderNo());
                            mode.addObject("remarks",made.getRemarks());
                            mode.addObject("amount",made.getTraAmount().setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                            mode.addObject("payUrl",Constant.getDwzShortUrl(targetUrl));
                            mode.addObject("openUrl",Constant.getDwzShortUrl(targetUrl));
                            return mode;
                    	}
                        *//*String targetUrl2 = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=2019022463364107&scope=auth_base&redirect_uri=http://online.toxpay.com/xpay/openauth/returnAauth?o="+no;
                        mode = new ModelAndView("alipay/CardCashierQrcode");
                        mode.addObject("orderNo",no);
                        mode.addObject("amount",made.getTraAmount().setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                        mode.addObject("payUrl",targetUrl2);
                        mode.addObject("remarks",made.getRemarks());
                        mode.addObject("openUrl",targetUrl2);
                        return mode;*//*
                        String amountView = made.getTraAmount().setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
                        String signStr = HiDesUtils.desEnCode(made.getOrderNo().concat("_").concat(amountView));
                        String targetUrl2 = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=2019022663445033&scope=auth_base&redirect_uri=http://boss.hawkeyepay.cn/xpay/openauth/returnAauth?o="+no;
                        mode = new ModelAndView("alipay/toCardJump4");
                        mode.addObject("amount",amountView);
                        mode.addObject("orderNo", made.getOrderNo());
                        mode.addObject("copyUrl",targetUrl2);
                        mode.addObject("payUrl", targetUrl2);
                        mode.addObject("signStr",signStr);
                        String strs = made.getQrcodeUrl().replace("alipays://platformapi/startapp?appId=09999988&","");
                        strs = strs.replaceAll("&","\",\"");
                        strs = strs.replaceAll("=","\":\"");
                        strs = "{\"".concat(strs).concat("\"}");
                        mode.addObject("dataObj",strs);
                        return mode;
                    }else{
                        return new ModelAndView("redirect:".concat(targetUrl));
                    }*/
                case 7:
                    mode = new ModelAndView("cashier/qrAlipayPay");
                    mode.addObject("qrImgUrl",made.getQrcodeUrl());
                    mode.addObject("qrcodeUrl",made.getQrcodeUrl());
                    mode.addObject("remarks", "请验证姓名（姓"+made.getRemarks()+"）");
                    return mode;
                case 10:
                    if (openNum < 1) {
                        mode = new ModelAndView ("alipay/cashierQrcode");
                        String jumpUrl = "https://ds.alipay.com/?from=mobilecodec&scheme="+URLEncoder.encode(startAlipay_url+made.getOpenUrl (), "utf-8");
                        mode.addObject ("l", jumpUrl);
                        mode.addObject ("u", userId);
                        mode.addObject ("n", upmerchNo);
                        mode.addObject ("o", orderNo);
                        mode.addObject ("m", no);
                        mode.addObject ("a", String.valueOf (amount));
                        return mode;
                    }
                    mode = new ModelAndView ("alipay/receiptScanJump");
                    String userUrl = "http://online.toxpay.com/xpay/wechat/getAlipayUserId?orderNo=" + no;
                    String fUrl = "alipays://platformapi/startapp?appId=20000186&actionType=addfriend&userId=" +userId + "&loginId=" + upmerchNo + "&source=by_home&alert=true";
                    mode.addObject ("jUrl", userUrl);
                    mode.addObject ("fUrl", fUrl);
                    return mode;
                case 12:
                    mode = new ModelAndView("alicashier/merchZfbReceipt");
                    targetUrl = "https://ds.alipay.com/?from=mobilecodec&scheme=alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode="+URLEncoder.encode(made.getQrcodeUrl(), "utf-8");
                    mode.addObject("amount",amount);
                    mode.addObject("orderNo", made.getOrderNo());
                    mode.addObject("copyUrl",made.getQrcodeUrl());
                    mode.addObject("targetUrl", targetUrl);
                    mode.addObject("isAlipay", isAlipay);
                    return mode;
                case 8:
                    if (openNum < 1) {
                        mode = new ModelAndView ("alipay/cashierQrcode");
                        String url = "alipayqr://platformapi/startapp?saId=10000007&qrcode=" + made.getOpenUrl ();
                        mode.addObject ("l", "https://ds.alipay.com/?from=mobilecodec&scheme=" + URLEncoder.encode (url, "utf-8"));
                    } else {
                        mode = new ModelAndView ("alipay/sendRedJump");
                    }
                    userId = Constant.getChannelKeyDes (made.getQrcodeUrl (), 0);
                    mode.addObject ("u", userId);
                    mode.addObject ("n", upmerchNo);
                    mode.addObject ("o", orderNo);
                    mode.addObject ("a", String.valueOf (amount));
                    return mode;
                case 111:
                    logger.info ("AA收款跳转页面:{}", made.getQrcodeUrl ());
                    mode = new ModelAndView ("alipay/toAAJump");
                    mode.addObject ("qrcodeUrl", made.getQrcodeUrl ());
                    return mode;
                case 120:
                    logger.info ("预授权支付跳转页面:{}", made.getQrcodeUrl ());
                    mode = new ModelAndView ("alipay/authFreezePay");
                    mode.addObject ("orderStr", made.getQrcodeUrl ());
                    mode.addObject ("successUrl", Constant.buildUrl (domainName, actualName, "success"));
                    return mode;
                default:
                    response.sendRedirect (made.getQrcodeUrl ());
                    return null;
            }
        } catch (Exception e) {
            logger.error ("自研支付包装收银台异常", e);
            super.writeResponse (response, Constant.failedMsg ("自研支付转换异常"));
            return null;
        }
    }


    /**
     * @描述:定制收银台，转换到上游
     * @时间:2018年8月21日 上午11:38:42
     */
    @RequestMapping(value = "/code/{no}", method = RequestMethod.GET)
    public ModelAndView cashierGetOpenId(HttpServletRequest request, HttpServletResponse response, @PathVariable("no") String no) {
        try {
            if (StringUtils.isEmpty(no)) {
                return super.writeResponse(response, Constant.failedMsg("订单信息异常"));
            }
            String orderNo = HiDesUtils.desDeCode(no);
            if (orderNo == null || StringUtils.isEmpty(orderNo) || "0".equals(orderNo)) {
                return  super.writeResponse(response, Constant.failedMsg("支付链接异常"));
            }
            VcOnlineOrderMade made = onlineOrderMadeService.findOrderByOrderNo(orderNo);
            /*if (made == null || made.getOpenNum () > 2) {
                return super.writeResponse(response, Constant.failedMsg("订单限制一次有效"));
            }*/
            String remarks = made.getRemarks ();
            if(StringUtil.isEmpty (remarks)){
                return super.writeResponse(response, Constant.failedMsg("订单信息缺失"));
            }
            long diff = System.currentTimeMillis() - made.getCreateDate().getTime();
            if (diff > made.getExpiredTime() * 1000) {
                return super.writeResponse(response, Constant.failedMsg("订单限制5分钟有效"));
            }

            VcOnlineOrderMade made2 = new VcOnlineOrderMade(orderNo);
            made2.setOpenNum(1);
            made2.setOpenType (made.getOpenType ());
            int r = onlineOrderMadeService.updateByOrderNo(made2);
            if (r < 1) {
                return super.writeResponse(response, Constant.failedMsg("订单更新失败"));
            }
            logger.info ("获取信息:{},{}",made.getOpenUrl (),made.getRemarks ());
            int type = made.getOpenType ();
            BigDecimal amount = made.getTraAmount ().setScale (2, BigDecimal.ROUND_HALF_UP);
            if (type == 60  || type == 61) {
                /*JSONObject prms = new JSONObject();
                prms.put("orderNo", made.getOrderNo());
                prms.put("mercId", made.getUpMerchNo());
                prms.put("payType", made2.getOpenType());
                prms.put("tranAmount", amount);
                prms.put("tranDateTime",new SimpleDateFormat ("yyyyMMddHHmmss").format(made.getCreateDate()));
                prms.put("expireTime", CacheConstants.EXPIRED_TIME_5);
                onlineOrderMadeService.orderUrlOpenEvent(prms);*/
                ModelAndView mode = new ModelAndView("cashier/qrWchartCashier");
                mode.addObject ("a", String.valueOf (amount));
                mode.addObject("qrcodeUrl",made.getQrcodeUrl());
                return mode;
            }else if (type == 92) {
                ModelAndView mode = new ModelAndView("cashier/ahAlipayCashier");
                mode.addObject ("a", String.valueOf (amount));
                mode.addObject("orderNo",no);
                mode.addObject("qrcodeUrl",made.getQrcodeUrl());
                mode.addObject("encodeUrl",URLEncoder.encode(made.getQrcodeUrl(),"utf-8"));
                return mode;
            }else if (type == 93) {
                ModelAndView mode = new ModelAndView("cashier/ahWchartCashier");
                mode.addObject ("a", String.valueOf (amount));
                mode.addObject("orderNo",no);
                mode.addObject("qrcodeUrl",made.getQrcodeUrl());
                mode.addObject("encodeUrl",URLEncoder.encode(made.getQrcodeUrl(),"utf-8"));
                return mode;
            }else if (type == 101) {
                ModelAndView mode = new ModelAndView("cashier/ahWchartCashier");
                mode.addObject ("a", String.valueOf (amount));
                mode.addObject("orderNo",no);
                mode.addObject("qrcodeUrl",made.getQrcodeUrl());
                return mode;
            }else if (type == 149) {
                ModelAndView mode = new ModelAndView("cashier/ahWchartCashier");
                mode.addObject ("a", String.valueOf (amount));
                mode.addObject("orderNo",no);
                mode.addObject("qrcodeUrl",made.getQrcodeUrl());
                return mode;
            }else if (type == 97) {
                return new ModelAndView("redirect:".concat(made.getQrcodeUrl ()));
            }else if(type == 61) {
                ModelAndView mode = new ModelAndView("cashier/qrWchartCashier");
                mode.addObject ("a", String.valueOf (amount));
                mode.addObject("qrcodeUrl",made.getQrcodeUrl());
                return mode;
            }else if(type == 10011){
                JSONObject redirectMap = Constant.stringToJson (remarks);
                ModelAndView mode = new ModelAndView("auto/autoSubmit");
                mode.addObject("map",redirectMap);
                mode.addObject("actionUrl",made.getQrcodeUrl ());
                return mode;
            }else if(type == 10012){
                ModelAndView mode = new ModelAndView("auto/autoBlack");
                mode.addObject("redirectHtml",remarks);
                return mode;
            }else if (type == 98) {
                ModelAndView mode = new ModelAndView("auto/autoBlack");
                /*ModelAndView mode = new ModelAndView("cashier/xunjieWchartCashier");
                mode.addObject ("a", String.valueOf (amount));
                mode.addObject("orderNo",no);
                mode.addObject("qrcodeUrl",made.getQrcodeUrl());
                mode.addObject("encodeUrl",URLEncoder.encode(made.getQrcodeUrl(),"utf-8"));*/
                mode.addObject("redirectHtml",made.getQrcodeUrl());
                return mode;
            }else if (type == 103) {
            	PrintWriter out = response.getWriter();
				/*
				 * String html =
				 * "<html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/><script>\t\tfunction paySubmit() {\r\n\t\t\twindow.location.href='"
				 * +
				 * 
				 * made.getQrcodeUrl() + "'\r\n" + "\t\t}\r\n" + "</script></head>" +
				 * "<body onload='paySubmit();'>\r\n" + "</body></html>";
				 */
            	response.setContentType("text/html; charset=utf-8");
            	out.write(made.getQrcodeUrl());
            	out.flush();
            	out.close();
                return null;
            }else if(type == 131) {
            	ModelAndView mode = new ModelAndView("alipay/lLalipayh5Jump");
        		String parm = made.getQrcodeUrl();
        		String[] ps = parm.split("&");
        		for (int i = 0; i < ps.length; i++) {
        			String[] s = ps[i].split("=");
        			mode.addObject(s[0],s[1]);
				}
                
                return mode;
            }else if(type == 132) {
            	ModelAndView mode = new ModelAndView("alipay/yaLongh5Jump");
        		String parm = made.getQrcodeUrl();
        		String[] ps = parm.split("&");
        		for (int i = 0; i < ps.length; i++) {
        			String[] s = ps[i].split("=");
        			mode.addObject(s[0],s[1]);
				}
                
                return mode;
            }
            response.sendRedirect(made.getQrcodeUrl());
            return null;
        } catch (Exception e) {
            logger.error("定制收银台，转换到上游异常!", e);
            return super.writeResponse(response, Constant.failedMsg("下单响应异常,请联系运维人员"));
        }
    }

    /**
     * 如果未手机收银台二次打开，获取缓存里面跳转mode，继续跳转（针对协议转账）
     */
    @RequestMapping(value = "/apiQrCode/{no}", method = RequestMethod.GET)
    public ModelAndView apiQrCode(HttpServletRequest request, HttpServletResponse response,@PathVariable String no) {
        try {
            if (StringUtils.isEmpty(no)) {
                return super.showErrorMsg(response, "参数为空");
            }
            logger.info("定制收银台接收到的订单号:{}", no);
            String orderNo = HiDesUtils.desDeCode(no);
            if (orderNo == null || StringUtils.isEmpty(orderNo) || "0".equals(orderNo)) {
                return super.showErrorMsg(response, "订单号为空");
            }
            ModelAndView mode = this.getCacheMode(orderNo);
            if(null == mode){
                return super.showErrorMsg(response, "订单号不存在或已失效");
            }
            return mode;
        } catch (Exception e) {
            logger.error("定制收银台，转换到上游异常", e);
            super.writeResponse(response, Constant.failedMsg("支付转换异常,请联系运维人员"));
            return null;
        }
    }
    
    /**
     * @描述:缓存订单对应mode跳转（针对协议转账）
     * @作者:nada
     * @时间:2019/3/28
     **/
    private ModelAndView getCacheMode(String orderNo){
        try {
            if(!redisCacheApi.exists(CASHIER_ORDER+orderNo) || null == redisCacheApi.get(CASHIER_ORDER+orderNo)){
                return null;
            }
            JSONObject jsonObject = (JSONObject)redisCacheApi.getBean2(CASHIER_ORDER+orderNo,JSONObject.class);
            logger.info("定制收银台缓存的订单信息:{}", jsonObject);
            ModelAndView mode = new ModelAndView(jsonObject.getString("viewName"));
            mode.addAllObjects((Map<String,Object>) jsonObject.get("map"));
            redisCacheApi.remove(CASHIER_ORDER+orderNo);
            return mode;
        } catch (Exception e) {
            logger.error("收银台获取缓存ModelAndView异常", e);
        }
        return null;
    }

    /**
     * @描述://打开方式 91:微信 92:支付宝 95记录打开 99其他
     * @作者:nada
     * @时间:2019/3/3
     **/
    private VcOnlineOrderMade buildNewUpadateMade (int opentype, String orderNo, HttpServletRequest request) {
        VcOnlineOrderMade newOrderMade = new VcOnlineOrderMade (orderNo);
        if (opentype > 0) {
            newOrderMade.setOpenType (opentype);
            return newOrderMade;
        }
        if (HttpBrowserTools.isWeChat (request)) {
            newOrderMade.setOpenType (91);
        } else if (HttpBrowserTools.isAlipay (request)) {
            newOrderMade.setOpenType (92);
        } else {
            newOrderMade.setOpenType (99);
        }
        return newOrderMade;
    }

    /**
     * @描述:缓存mode
     * @作者:nada
     * @时间:2019/3/28
     **/
    private boolean saveMode(String orderNo,ModelAndView mode){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("map",mode.getModel());
            jsonObject.put("viewName",mode.getViewName());
            redisCacheApi.setBeanValid2(CASHIER_ORDER + orderNo,jsonObject,CacheConstants.DEFAULT_INVALID_TIMER_5,JSONObject.class);
            return true;
        } catch (Exception e) {
            logger.error("收银台缓存ModelAndView异常", e);
        }
        return false;
    }
}
