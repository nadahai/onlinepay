package com.vc.onlinepay.web.cashier;

import static java.util.regex.Pattern.compile;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.entity.channel.MerchChannel;
import com.vc.onlinepay.persistent.entity.merch.MerchInfo;
import com.vc.onlinepay.persistent.service.channel.MerchChannelServiceImpl;
import com.vc.onlinepay.persistent.service.channel.SupplierSubnoServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.happypay.BiuldUtils;
import com.vc.onlinepay.utils.http.HttpBrowserTools;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author：JauYang
 * @describe：在线收银台 @date： 11:59 2017/12/18 @last_date： @last_describe：
 */
@Controller
public class CashierController extends BaseController {

    @Autowired
    private CommonPayService commonPayService;

    @Autowired
    private MerchChannelServiceImpl merchChannelService;
    @Autowired
    private SupplierSubnoServiceImpl supplierSubnoService;

    @Value("${onlinepay.project.successUrl:}")
    private String successUrl;
    @Value("${onlinepay.project.domainName:}")
    private String domainName;
    @Value("${onlinepay.project.actualName:}")
    private String actualName;
    @Value("${spring.datasource.username:}")
    private String datasourceUsername;
    /**
     * 支付宝服务窗授权获取用户信息
     */
    private static final String ALIPAY_AUTH_USER = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id="+ BiuldUtils.APP_ID+"&scope=auth_user&redirect_uri=";

    /**
     * @描述:测试收银台
     */
    @RequestMapping(value = "/opencashier", method = RequestMethod.GET)
    public ModelAndView opencashier(HttpServletRequest request, HttpServletResponse response) {
    	 try {
             return new ModelAndView("cashier/opencashier");
	      } catch (Exception e) {
	        logger.error("测试收银台异常", e);
             return super.writeResponse(response, Constant.failedMsg("支付转换异常,请联系运维人员"));
	      }
    }

    /**
     * @描述:收银台参数处理
     * @时间:2018/6/4 21:11
     */
    @RequestMapping(value = "/cashier",method = RequestMethod.GET)
    public String cashierGetOpenId(HttpServletRequest request){
        String jumpUrl= domainName + "/" + actualName + "/cashierIndex?";
        String redirectUrl = jumpUrl;
        String userAgent = "OTH";
        try {
            Map<String,String> params = new HashMap<>();
            if(request.getParameter("cmd")!=null){params.put("cmd",request.getParameter("cmd"));}
            if(request.getParameter("remark")!=null){params.put("des",request.getParameter("remark"));}
            if(request.getParameter("amount")!=null){params.put("amt",request.getParameter("amount"));}
            if(request.getParameter("mode")!=null){params.put("mod",request.getParameter("mode"));}
            if(request.getParameter("orderId")!=null){params.put("ord",request.getParameter("orderId"));}
            if(request.getParameter("merchNo")!=null){params.put("mNo",request.getParameter("merchNo"));}

            //判断浏览器
            if(HttpBrowserTools.isWeChat(request)) {
                userAgent = "WX";
            }else if(HttpBrowserTools.isAlipay(request)){
                userAgent = "ALI";
                logger.info("支付宝服务窗跳转处理");
            }
            params.put("cop",userAgent);
            String vcEncryption = StringUtil.getString(params);
            //支付宝限制100字符串长度
            String base64Str = Base64.getEncoder().encodeToString(vcEncryption.getBytes());
            redirectUrl = jumpUrl+"state="+base64Str;
        } catch (Exception e) {
            logger.error("公众号、服务窗跳转异常!",e);
        }
        logger.info("重定向跳转Url:"+redirectUrl);
        return "redirect:"+redirectUrl;
    }
    
    /**
     * @描述:在线收银台页面入口
     * @时间:2018/7/20 15:19
     */
    @RequestMapping(value = "/cashierIndex",method = RequestMethod.GET)
    public String cashierGet(HttpServletRequest request, HttpServletResponse response)  {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            String encryption = request.getParameter("state").replace(" ","+");
            logger.info("收银台接收密文:{}",encryption);
            String content = new String(Base64.getDecoder().decode(encryption));
            Map<String,String> params = StringUtil.getMapParams(content);
            logger.info("收银台接收参数:{}",params);
            String merchNo = params.get("mNo");
            if (StringUtil.isEmpty(merchNo)) {
                request.setAttribute("msg", "商户编号不存在");
                return "failure";
            }
            MerchInfo merchInfo = commonPayService.getCacheMerchInfo(merchNo);
            if (merchInfo == null) {
                request.setAttribute("msg", "商户信息不存在");
                return "failure";
            }
            if (merchInfo.getIsCashier() == 2) {
                request.setAttribute("msg", "您未开通收银台业务，请联系上级");
                return "failure";
            }
            String userAgent = params.get("cop");
            String cmd = params.get("cmd");
            String authCode = "";
            switch (userAgent){
                case "ALI":
                    authCode = request.getParameter("auth_code");
                    BiuldUtils.getAlipayUserId(authCode);
                    break;
                case "WX":
                    authCode = request.getParameter("code");
                    break;
                default:
                    authCode = "";
            }
            logger.info("解析的微信支付宝 auth_code:{}",authCode);
            request.setAttribute("openId", authCode == null ? "":authCode);
            request.setAttribute("merchInfo", merchInfo);
            if (null == cmd) {
                List<MerchChannel> merchChannels = merchChannelService.findCanUsedMerchChannelByMerchId(merchInfo.getId(), null);
                request.setAttribute("merchChannels", merchChannels);
                return "cashier/index";
            }
            switch (cmd){
                case "scan":
                case "qrcode":
                    request.setAttribute("amount", params.get("amt"));
                    request.setAttribute("cmd", cmd);
                    return "cashier/qrCodePay";
                case "quickPay":
                    request.setAttribute("amount", params.get("amt"));
                    request.setAttribute("remark", params.get("des"));
                    List<MerchChannel> merchChannels = merchChannelService.findCanUsedMerchChannelByMerchId(merchInfo.getId(), 7L);
                    request.setAttribute("merchChannels", merchChannels);
                    return "cashier/quickPay";
                case "gateWay":
                    request.setAttribute("amount", params.get("amt"));
                    request.setAttribute("remark", params.get("des"));
                    request.setAttribute("mode", params.get("mod"));
                    return "cashier/gatewayPay";
                 default:
                     request.setAttribute("msg", "支付指令不存在");
                     return "failure";
            }
        } catch (Exception e) {
            logger.error("收银台打开失败", e);
            request.setAttribute("msg", "收银台打开失败");
        }
        return "failure";
    }

    /**
     * @描述:在线收银台参数组装
     * @时间:2018/7/20 15:16
     */
    @RequestMapping(value = "/cashier",method = RequestMethod.POST)
    public void cashierPost(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        response.setCharacterEncoding(Constant.CHART_UTF);
        response.setContentType("application/json;charset=utf-8");
        try {
            setHttpServletContent(request, response);
            String cmd = request.getParameter("cmd");
            // 1.判断商户存不存在：
            MerchInfo merchInfo = commonPayService.getCacheMerchInfo(request.getParameter("merchantId"));
            if (merchInfo == null) {
                result.put("code", "10001");
                result.put("msg", "下单失败,商户信息不存在！");
                response.getWriter().write(new Gson().toJson(result));
                return ;
            }
            //判断是否开通收银台
            if (merchInfo.getIsCashier() == 2) {
                result.put("code", "10001");
                result.put("msg", "您未开通收银台业务，请联系上级！");
                response.getWriter().write(new Gson().toJson(result));
                return ;
            }
            //判断金额必须大于2
            String amount = request.getParameter("amount");
            if(!isMoney(amount)||Double.valueOf(amount)<2){
                result.put("code","10001");
                result.put("msg","下单失败,交易金额最少2元！");
                response.getWriter().write(new Gson().toJson(result));
                return ;
            }
            //组装参数
           JSONObject data = getSign(cmd,request,merchInfo.getPassword(),merchInfo.getId());
           logger.info("收银台组装参数完成:{}",data);
           switch (cmd){
               case "scan":
                   response.getWriter().write(new Gson().toJson(data));
                   return ;
               case "H5":
               case "quick":
               case "union":
               case "gateway":
                   if (data.isEmpty()) {
                       result.put("code", "10001");
                       result.put("msg", "下单失败，支付参数组装失败！");
                   } else {
                       result.put("code", "10000");
                       result.put("msg", "success！");
                       result.put("data", data);
                   }
                   response.getWriter().write(new Gson().toJson(result));
                   return ;
               default:
                   result.put("code", "10001");
                   result.put("msg", "下单失败，支付指令不存在！");
                   response.getWriter().write(new Gson().toJson(result));
                   return ;
           }
        } catch (Exception e) {
            logger.error("收银台下单异常：", e);
            result.put("code", "10001");
            result.put("msg", "下单异常！");
            try {
                response.getWriter().write(new Gson().toJson(result));
            } catch (IOException e1) {
                logger.error("收银台下单HTTP响应异常：", e);
            }
            return ;
        }
    }

    /** 统一签名  */
    private JSONObject getSign(String cmd,HttpServletRequest request, String key, Long merchId) {
        JSONObject jsonObject = new JSONObject();
        try {
            String orderId = request.getParameter("orderId");
            String requestIp = request.getParameter("clientIP");
            jsonObject.put("merchantId", request.getParameter("merchantId"));
            jsonObject.put("orderId", System.currentTimeMillis ());
            jsonObject.put("amount", request.getParameter("amount"));
            jsonObject.put("orderTime",  Constant.getDateString());
            jsonObject.put("requestIp", requestIp!=null?requestIp:request.getRemoteAddr());
            jsonObject.put("goodsName",request.getParameter("remark"));
            jsonObject.put("goodsDesc",merchId + "收银台收款");
            jsonObject.put("notifyUrl", "http://");
            jsonObject.put("returnUrl", successUrl);
            switch (cmd){
                case "scan":
                    jsonObject.put("openId", request.getParameter("openId"));
                    jsonObject.put("transCode","001");
                    jsonObject.put("service", request.getParameter("service"));
                    break;
                case "H5":
                    jsonObject.put("openId", request.getParameter("openId"));
                    jsonObject.put("payType", request.getParameter("payTypeCode"));
                    break;
                case "quick":
                    jsonObject.put("memberId", request.getParameter("merchantId") + merchId);
                    jsonObject.put("payType", "DQP");
                    break;
                case "union":
                    jsonObject.put("payType", "13");
                    break;
                case "gateway":
                    jsonObject.put("cardType", "0");
                    jsonObject.put("payType", "13");
                    break;
                default:
                    break;
            }
            String sign = Md5CoreUtil.md5ascii(jsonObject,key).toUpperCase();
            jsonObject.put("sign", sign);
            return jsonObject;
        } catch (Exception e) {
            logger.error("组装支付参数异常", e);
            return new JSONObject();
        }
    }

    private static boolean isMoney(String str) {
        Pattern pattern = compile("(^[1-9]([0-9]+)?(\\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9])?$)");
        return pattern.matcher(str).matches();
    }
}
