package com.vc.onlinepay.web.cashier;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.config.ConfigUtil;
import com.vc.onlinepay.pay.order.h5.CustomerH5ServiceImpl;
import com.vc.onlinepay.pay.order.h5.UnionH5CommonServiceImpl;
import com.vc.onlinepay.persistent.entity.online.VcOnlineContact;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineContactServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.FilterUtils;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.happypay.BiuldUtils;
import com.vc.onlinepay.utils.http.HttpBrowserTools;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.utils.ninepie.BankUtil;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import com.vc.onlinepay.web.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * 翰银收银台接口
 */
@Controller
@RequestMapping("/hyh5api")
public class CashierHanYinController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CashierHanYinController.class);
    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;
    @Autowired
    private VcOnlineContactServiceImpl vcOnlineContactService;
    @Autowired
    private CustomerH5ServiceImpl hanYinH5Service;
    @Autowired
    private VcOnlineOrderServiceImpl orderService;
    @Autowired
    private UnionH5CommonServiceImpl unionH5CommonService;
    /**
     * @描述 包装快捷支付宝H5扫码跳转收银台
     * @作者 nada
     * @时间 2019/5/28 10:36
     */
    @RequestMapping(value = "/cashier/{no}")
    public ModelAndView hanyinH5coder(HttpServletRequest request, HttpServletResponse response, @PathVariable String no) {
        ModelAndView mode = new ModelAndView("failure");
        try {
            logger.info("cashier/no:{}",no);
            VcOnlineOrderMade made = hanyinCommon(request,response,no);
            Boolean isAlipay = HttpBrowserTools.isAlipay(request);
            if (isAlipay) {
                return new ModelAndView("redirect:"+ base_url.concat("/hyh5api/backHyUserId?orderNo=").concat(no));
            } else {
                mode = new ModelAndView("hanyin/hyCashierCode");
            }
            if(made.getOpenNum()<2){
                orderService.updateDesByOrderNo(made.getOrderNo(),"1打开扫码收银台"+made.getOpenNum());
            }
            mode.addObject("amount", Constant.format2amount(made.getTraAmount()));
            mode.addObject("orderNo",made.getOrderNo());
            mode.addObject("payUrl",made.getOpenUrl());
            mode.addObject("encryptNo",no);
            return mode;
        }catch (IllegalArgumentException e) {
            logger.error("非法参数异常,{}", e);
            mode.addObject("",e.getMessage());
            return mode;
        }catch (Exception e) {
            logger.error("翰银H5收银台异常,{}", e);
            mode.addObject("message", "收银台异常");
            return mode;
        }
    }

    /**
     * @描述:返回支付宝UserId
     * @时间:2019/3/8
     **/
    @RequestMapping("backHyUserId")
    public ModelAndView returnAlipayUserId(HttpServletRequest request,HttpServletResponse response){
        ModelAndView mode = new ModelAndView("failure");
        try {
            request.setCharacterEncoding(Constant.CHART_UTF);
            response.setCharacterEncoding(Constant.CHART_UTF);
            String code = request.getParameter("auth_code");
            String no = request.getParameter("orderNo");
            String orderNo = HiDesUtils.desDeCode(no);
            logger.info("接收订单号:{},解析订单号{}", no,orderNo);
            if (StringUtils.isEmpty(orderNo) || "0".equals(orderNo)) {
                mode.addObject("message","订单号非法"+orderNo);
                return mode;
            }
            VcOnlineOrderMade made = onlineOrderMadeService.findOrderByOrderNo(orderNo);
            if (made == null){
                mode.addObject("message","订单不存在，请重新发起");
                return mode;
            }
            //状态 1下单成功 2下单失败 3下单中 4支付成功 5支付失败 6:处理中 7:代付中 8:代付失败 9交易退款
            Integer status = 3;
            VcOnlineOrder onlineOrder = orderService.findOrderByOrderNo(orderNo);
            if (onlineOrder != null && onlineOrder.getStatus() !=null) {
                status = onlineOrder.getStatus();
            }
            String userid = request.getParameter("userid");
            if (!domainName.contains("paypaul.prxgg.cn")) {
                userid = "123456";
            }
            if(StringUtils.isEmpty (userid)){
                userid = BiuldUtils.getAlipayUserId(code, ConfigUtil.alipayAppId,ConfigUtil.alipayPrivateKey);
            }
            logger.info("授权回调:code:{},orderNo:{},userid:{}",code, orderNo,userid);
            if(StringUtils.isAnyEmpty(no,userid)){
                mode.addObject("message","获取USERID为空");
                return mode;
            }
            made.setUserId(userid);
            int r = onlineOrderMadeService.updateByOrderNo(made);
            if (r < 1) {
                mode.addObject("message","订单更新失败，请重新发起");
                return mode;
            }
            mode = new ModelAndView("hanyin/hYtoAlipay");
            mode.addObject("status",status);
            mode.addObject("userId",userid);
            mode.addObject("amount", Constant.format2amount(made.getTraAmount()));
            mode.addObject("orderNo",made.getOrderNo());
            mode.addObject("payUrl",made.getOpenUrl());
            mode.addObject("encryptNo",no);
            if(status ==3){
                orderService.updateDesByOrderNo(made.getOrderNo(),"2进入收款界面"+made.getOpenNum());
            }
            return mode;
        } catch (Exception e) {
            logger.error ("获取用户的UserId异常",e);
            mode.addObject("message","支付宝账号异常");
            return mode;
        }
    }

    /**
     * @描述 翰银H5扫码下单接口
     * @作者 nada
     * @时间 2019/5/28 10:36
     */
    @ResponseBody
    @RequestMapping(value = "/order/{no}", produces = "application/json;charset=UTF-8")
    public JSONObject hanyinH5Order(HttpServletRequest request, HttpServletResponse response,@PathVariable String no) {
        try {
            super.setHttpServletContent(request, response);
            VcOnlineOrderMade made= hanyinCommon(request,response,no);
            String orderNo = made.getOrderNo();
            VcOnlineOrder onlineOrder = orderService.findOrderByOrderNo(orderNo);
            int status =3;
            if (onlineOrder != null && onlineOrder.getStatus() !=null) {
                //状态 1下单成功 2下单失败 3下单中 4支付成功 5支付失败 6:处理中 7:代付中 8:代付失败 9交易退款
                status = onlineOrder.getStatus();
                JSONObject result = Constant.successMsg("ok");
                result.put("status", status);
                if (status == 1) {
                    result = Constant.successMsg("下单成功，继续支付");
                    result.put("status", status);
                    super.writeResponse(response,result);return null;
                }
                if (status == 4) {
                    result = Constant.successMsg("已经支付成功");
                    result.put("status", status);
                    super.writeResponse(response,result);return null;
                }
                if (status == 5 || status == 2) {
                    result = Constant.failedMsg("处理失败，请重新下单");
                    result.put("status", status);
                    super.writeResponse(response,result);return null;
                }
            }
            JSONObject reqData =  HttpRequestTools.getRequestJson2(request);
            logger.info("翰银H5下单接口参数:{}",reqData );
            String cardId = StringUtils.deleteWhitespace(reqData.getString("cardId"));
            if (StringUtils.isAnyEmpty(cardId)) {
                logger.error("银行卡号为空,订单号{}",orderNo);
                super.writeResponse(response, Constant.failedMsg("银行卡为空，请先绑定银行卡")); return null;
            }
            VcOnlineContact vcOnlineContact =  vcOnlineContactService.findContactByid(cardId);
            if(vcOnlineContact == null || StringUtil.isEmpty(vcOnlineContact.getCardNo())){
                logger.error("银行卡号为空,订单号{}",orderNo);
                super.writeResponse(response, Constant.failedMsg("银行卡为空，请先绑定银行卡"));return null;
            }
            String cardNo = vcOnlineContact.getCardNo();
            if (StringUtils.isAllBlank(vcOnlineContact.getUserName(),vcOnlineContact.getIdNo()) && vcOnlineContact.getActivateStatus()!=2) {
            	logger.info("查询到敏支付的卡,但未绑定完整信息！订单号:{}",orderNo);
            	JSONObject result = Constant.customMsg("5200AAA","卡信息不完整,请完善!");
            	result.put("cardId",cardId);
            	super.writeResponse(response,result);
                return null;
            }

            int amount = made.getTraAmount().multiply(new BigDecimal("100")).intValue();
            JSONObject createData = hanYinH5Service.createOrder(cardNo,orderNo,amount,made.getChannelId());
            String message = "开始创建订单";
            if(createData == null || createData.isEmpty()){
                status = 2;
                logger.error("翰银H5下单响应为空,订单号{}",orderNo);
            }else{
                String transNumber = createData.containsKey("transNumber")?createData.getString("transNumber"):"";
                message = createData.containsKey("message")?createData.getString("message"):"开始创建订单";
                if (StringUtil.isNotEmpty(transNumber) ) {
                    made.setQrcodeUrl(transNumber);
                    int r = onlineOrderMadeService.updateByOrderNo(made);
                    status = 1;
                } else {
                    status = 2;
                }
            }
            VcOnlineOrder vcOnlineOrder = new VcOnlineOrder();
            vcOnlineOrder.setBankNo(vcOnlineContact.getCardNo());
            vcOnlineOrder.setPOrder(com.vc.onlinepay.utils.hanyin.BankUtil.getCardNameOrNo(cardNo));
            vcOnlineOrder.setOrderDes(message);
            vcOnlineOrder.setStatus(status);
            vcOnlineOrder.setOrderNo(orderNo);
            //orderService.updateOrderStatus(vcOnlineOrder);
            orderService.updateByOrderNo(vcOnlineOrder);
            if(status == 2){
                super.writeResponse(response, Constant.failedMsg(message));return null;
            }
            createData.put("status", status);
            super.writeResponse(response,createData);return null;
        }catch (IllegalArgumentException e) {
            logger.error("非法参数异常{}", no,e);
            super.writeResponse(response, Constant.failedMsg(e.getMessage()));
            return null;
        }catch (Exception e) {
            logger.error("翰银H5收银台异常{}",no,e);
            super.writeResponse(response, Constant.failedMsg("翰银H5收银台异常"));
            return null;
        }
    }

    /**
     * @描述 获取dfpSession
     * @作者 nada
     * @时间 2019/5/28 10:36
     */
    @ResponseBody
    @RequestMapping(value = "/dfpSession/{no}", produces = "application/json;charset=UTF-8")
    public JSONObject getDfpSessionId(HttpServletRequest request, HttpServletResponse response,@PathVariable String no) {
        try {
        	VcOnlineOrderMade made= hanyinCommon(request,response,no);
        	JSONObject reqData =  HttpRequestTools.getRequestJson2(request);
        	String cardId = StringUtils.deleteWhitespace(reqData.getString("cardId"));
            VcOnlineContact vcOnlineContact = vcOnlineContactService.findContactByid(cardId);
            if(null == vcOnlineContact){
                logger.error("未找到卡号~ vcOnlineContact");
                super.writeResponse(response, Constant.failedMsg("未找到卡"));return null;
            }
            String orderNo = made.getOrderNo();
            logger.info("翰银轮询到瀚银创建dfpSession,订单号:{}",orderNo);
            super.setHttpServletContent(request, response);
            logger.info("获取dfpSession参数:{},订单号:{}",reqData,orderNo );
            String transNumber = made.getQrcodeUrl();
            String callback = StringUtils.deleteWhitespace(reqData.getString("callback"));
            String encryptData = StringUtils.deleteWhitespace(reqData.getString("encryptData"));
            if (StringUtils.isAnyEmpty(transNumber,callback,encryptData)) {
                logger.info("获取dfpSession信息不全orderNo:{},transNumber:{},callback:{}",orderNo,transNumber,callback);
                super.writeResponse(response, Constant.failedMsg("获取dfpSession信息不全"));return null;
            }
            JSONObject createData = hanYinH5Service.getDfpSessionId(transNumber,callback,encryptData,orderNo);
            if(createData == null || createData.isEmpty() || !createData.containsKey("dfpSessionId")){
                logger.info("获取dfpSession为空,订单号：{}",orderNo);
                super.writeResponse(response, Constant.failedMsg("获取dfpSession为空"));return null;
            }
            String dfpSessionId = createData.getString("dfpSessionId");
            made.setRemarks(dfpSessionId);
            int r = onlineOrderMadeService.updateByOrderNo(made);
            if (r < 1) {
                logger.info("dfpSession更新失败，请重新发起,订单号：{}",orderNo);
                super.writeResponse(response, Constant.failedMsg("dfpSession更新失败，请重新发起"));
                return null;
            }
            orderService.updateDesByOrderNo(made.getOrderNo(),"7创建SESSION成功");
            super.writeResponse(response, Constant.successMsg("dfpSession创建成功"));return null;
        }catch (IllegalArgumentException e) {
            logger.error("非法参数异常{}", e);
            super.writeResponse(response, Constant.failedMsg(e.getMessage()));
            return null;
        }catch (Exception e) {
            logger.error("dfpSession异常,{}", e);
            super.writeResponse(response, Constant.failedMsg("dfpSession异常"));
            return null;
        }
    }

    /**
     * @描述 翰银发送短信验证码接口
     * @作者 nada
     * @时间 2019/5/28 10:36
     */
    @ResponseBody
    @RequestMapping(value = "/sms/{no}", produces = "application/json;charset=UTF-8")
    public JSONObject hanyinH5Sms(HttpServletRequest request, HttpServletResponse response,@PathVariable String no) {
        JSONObject reqData = null;
        try {
            super.setHttpServletContent(request, response);
            VcOnlineOrderMade made= hanyinCommon(request,response,no);
            String orderNo = made.getOrderNo();
            reqData =  HttpRequestTools.getRequestJson2(request);
            logger.info("翰银发送短信参数:{}",reqData );
            String cardId = StringUtils.deleteWhitespace(reqData.getString("cardId"));
            VcOnlineContact vcOnlineContact = vcOnlineContactService.findContactByid(cardId);
            if(vcOnlineContact == null || StringUtil.isEmpty(vcOnlineContact.getCardNo())){
                logger.error("银行卡号为空1,订单号:{}",orderNo);
                super.writeResponse(response, Constant.failedMsg("银行卡号为空"));return null;
            }
            String accNo = vcOnlineContact.getCardNo();
            String transNumber = made.getQrcodeUrl();
            if (StringUtils.isAnyEmpty(accNo,transNumber)) {
                logger.error("银行卡号为空2,订单号:{}",orderNo);
                super.writeResponse(response, Constant.failedMsg("银行卡号为空"));return null;
            }
            logger.info("翰银轮询到瀚银发送短信验证码,订单号:{}",orderNo);
            JSONObject createData = hanYinH5Service.sendSms(transNumber,accNo,made.getOrderNo(),vcOnlineContact.getPhone());
            String message = createData.containsKey("message")?createData.getString("message"):"发送短信";
            orderService.updateDesByOrderNo(made.getOrderNo(),"翰银_短信"+message);
            super.writeResponse(response,createData);
            return null;
        }catch (IllegalArgumentException e) {
            logger.error("非法参数异常,{}",reqData, e);
            super.writeResponse(response, Constant.failedMsg(e.getMessage()));
            return null;
        } catch (Exception e) {
            logger.error("翰银发送短信异常,{}",reqData, e);
            super.writeResponse(response, Constant.failedMsg("发送短信异常"));
            return null;
        }
    }

    /**
     * @描述 包装快捷支付接口
     * @作者 nada
     * @时间 2019/5/28 10:36
     */
    @RequestMapping(value = "/pay/{no}", produces = "application/json;charset=UTF-8")
    public JSONObject hanyinH5Pay(HttpServletRequest request, HttpServletResponse response,@PathVariable String no) {
        try {
            super.setHttpServletContent(request, response);
            VcOnlineOrderMade made = hanyinCommon(request,response,no);
            String orderNo = made.getOrderNo();
            VcOnlineOrder onlineOrder = orderService.findOrderByOrderNo(orderNo);
            if (onlineOrder != null && onlineOrder.getStatus() == 4) {
                super.writeResponse(response, Constant.failedMsg("订单已经支付成功，请勿重复付款"));return null;
            }
            JSONObject reqData =  HttpRequestTools.getRequestJson2(request);
            logger.info("包装快捷支付订单:{},参数:{}",orderNo,reqData );
            
            String smsCode = StringUtils.deleteWhitespace(reqData.getString("smsCode"));
            String cardId = StringUtils.deleteWhitespace(reqData.getString("cardId"));
            if (StringUtils.isAnyEmpty(smsCode,cardId)) {
                logger.error("支付非法请求orderNo:{},smsCode:{},cardId:{}",orderNo,smsCode,cardId);
                super.writeResponse(response, Constant.failedMsg("支付非法请求"));return null;
            }
            
            VcOnlineContact vcOnlineContact = vcOnlineContactService.findContactByid(cardId);
            if(vcOnlineContact == null || StringUtil.isEmpty(vcOnlineContact.getCardNo())){
                super.writeResponse(response, Constant.failedMsg("银行卡号为空"));return null;
            }

            String password = (null == reqData.get("password"))?null:StringUtils.deleteWhitespace(reqData.getString("password"));
            String transNumber = made.getQrcodeUrl();
            if (StringUtils.isAnyEmpty(transNumber)) {
                logger.error("支付非法请求orderNo:{},smsCode:{},transNumber:{},cardId:{}",orderNo,smsCode,transNumber,cardId);
                super.writeResponse(response, Constant.failedMsg("支付非法请求"));return null;
            }

            String idNo = StringUtils.deleteWhitespace(vcOnlineContact.getIdNo());
            JSONObject createData = unionH5CommonService.paymentSubmit(transNumber,smsCode,idNo,no,vcOnlineContact.getPhone(),password);
            String message = createData.containsKey("message")?createData.getString("message"):"开始支付";

            VcOnlineOrder onlineOrder2 = orderService.findOrderByOrderNo(orderNo);
            if (onlineOrder2.getStatus() != 4)  {
                orderService.updateDesByOrderNo(onlineOrder.getOrderNo(),message);
            }
            if(message.indexOf("短信校验")>=0 || message.indexOf("短信")>=0 || message.indexOf("校验")>=0){
                createData.put("smsflag", "0");
            }else{
                createData.put("smsflag", "1");
            }
            super.writeResponse(response,createData);return null;
        } catch (IllegalArgumentException e) {
            logger.error("包装快捷支付接口非法参数", e);
            super.writeResponse(response, Constant.failedMsg(e.getMessage()));return null;
        } catch (Exception e) {
            logger.error("包装快捷支付接口异常", e);
            super.writeResponse(response, Constant.failedMsg("支付异常"));return null;
        }
    }

    /**
     * @描述 翰银绑卡接口
     * @作者 nada
     * @时间 2019/5/28 10:36
     */
    @RequestMapping(value = "/bindCard/{no}", produces = "text/html;charset=UTF-8")
    public JSONObject hanyinH5BindCard(HttpServletRequest request, HttpServletResponse response,@PathVariable String no) {
        try {
            String orderNo = HiDesUtils.desDeCode(no);
            super.setHttpServletContent(request, response);
            VcOnlineOrderMade made= hanyinCommon(request,response,no);
            orderService.updateDesByOrderNo(made.getOrderNo(),"0开始绑卡");
            JSONObject reqData =  HttpRequestTools.getRequestJson2(request);
            logger.info("翰银H5收银台参数:{}",reqData );
            Long merchNo = Long.valueOf(made.getMerchNo());
            String userId = made.getUserId();
            String cardNo = reqData.getString("cardNo");
            String idNo = reqData.getString("idNo");
            String phone = reqData.getString("phone");
            String payType = reqData.getString("payType");
            String userName = reqData.getString("userName");
            String updateFlag = reqData.getString("updateFlag");
            String cardId = reqData.getString("cardId");
            if(StringUtils.isAnyEmpty(userId,cardNo,phone)){
                logger.error("用户参数为空,订单号：{}",orderNo);
                super.writeResponse(response, Constant.customMsg("5200AAA","请勿输入空的参数！"));
                return null;
            }

            //对输入框参数进行过滤
            boolean cardNoFlag = FilterUtils.checkContent(cardNo);
            boolean phoneFlag = FilterUtils.checkContent(phone);

            if( cardNoFlag || phoneFlag||!StringUtils.isNumeric(cardNo)||!StringUtils.isNumeric(phone)){
                logger.error("非法参数,包含敏感字符订单号：{},cardNoFlag:{},{},phoneFlag:{},cardNo:{},phone:{}",orderNo,cardNoFlag,phoneFlag,cardNo,phone);
                orderService.updateDesByOrderNo(made.getOrderNo(),"1绑卡失败，非法字符");
                super.writeResponse(response, Constant.customMsg("5200AAA","请勿输入非法敏感字符！"));
                return null;
            }
            String bankName = BankUtil.getNoCardName(cardNo);
            if (Strings.isEmpty(bankName)||bankName.equals("暂未查到此银行")) {
                logger.error("无效银行卡号{},订单号：{}",cardNo,orderNo);
                orderService.updateDesByOrderNo(made.getOrderNo(),"1绑卡失败，卡库无效"+cardNo);
                super.writeResponse(response, Constant.customMsg("5200AAA","请输入有效卡号！"));
                return null;
            }
            orderService.updateDesByOrderNo(orderNo,"1绑卡成功，白名单内");
            VcOnlineContact vcOnlineCt = new VcOnlineContact();
            vcOnlineCt.setCardNo(cardNo);
            vcOnlineCt.setMemberId(userId);
            vcOnlineCt.setActivateStatus(Long.valueOf(payType));
            
            if (StringUtils.isNotEmpty(updateFlag) && updateFlag.equals("1")) {
            	BigDecimal bdId=new BigDecimal(cardId);
            	vcOnlineCt.setUserName(userName);
            	vcOnlineCt.setIdNo(idNo);
            	vcOnlineCt.setPhone(phone);
            	vcOnlineCt.setId(bdId);
            	vcOnlineContactService.updateNetContactById(vcOnlineCt);
            	super.writeResponse(response, Constant.successMsg("修改成功"));
                return null;
            }
            List<VcOnlineContact> contactList = vcOnlineContactService.findContactByCondition(vcOnlineCt);
            if ( contactList.size()>0 ) {
                logger.error("绑卡卡号重复，卡号：{}，订单号：{}", cardNo,orderNo);
                super.writeResponse(response, Constant.customMsg("5200AAA","绑卡卡号重复！"));
                return null;
            }
            String cardType = "70";  //翰银支付宝
            VcOnlineContact vcOnlineContact = new VcOnlineContact();
            vcOnlineContact.setMerchantNo(merchNo);
            vcOnlineContact.setMemberId(userId);
            vcOnlineContact.setCardNo(cardNo);
            vcOnlineContact.setIdNo(idNo);
            vcOnlineContact.setPhone(phone);
            vcOnlineContact.setCardType(cardType);
            vcOnlineContact.setActivateStatus(Long.valueOf(payType));
            vcOnlineContact.setUserName(userName);
            vcOnlineContactService.saveNetContact(vcOnlineContact);
            orderService.updateDesByOrderNo(made.getOrderNo(),"1绑卡成功");
            super.writeResponse(response, Constant.successMsg("绑卡成功"));
            return null;
        } catch (IllegalArgumentException e) {
            logger.error("非法参数异常,{}", e);
            super.writeResponse(response, Constant.failedMsg(e.getMessage()));
            return null;
        } catch (Exception e) {
            logger.error("翰银绑卡异常", e);
            super.writeResponse(response, Constant.failedMsg("绑卡异常"));
            return null;
        }
    }

    /**
     * @描述 翰银公共通用方法
     * @作者 nada
     * @时间 2019/5/28 11:03
     */
    private VcOnlineOrderMade hanyinCommon(HttpServletRequest request, HttpServletResponse response, @PathVariable String no) throws IllegalArgumentException{
        if (StringUtils.isEmpty(no)) {
            throw  new IllegalArgumentException("订单号为空");
        }
        String orderNo = HiDesUtils.desDeCode(no);
        if (orderNo == null || StringUtils.isEmpty(orderNo) || "0".equals(orderNo)) {
            throw  new IllegalArgumentException("解析订单号非法");
        }
        VcOnlineOrderMade made = onlineOrderMadeService.findOrderByOrderNo(orderNo);
        if (made == null) {
            throw  new IllegalArgumentException("订单号不存在");
        }
        return made;
    }

    /**
     * @描述  綁定银行卡跳转界面
     * @作者 nada
     * @时间 2019/5/30 11:19
     */
    @RequestMapping(value="/jumpAdd/{no}",produces = "text/html;charset=UTF-8")
    public ModelAndView jumpAddBankCard(HttpServletRequest request, HttpServletResponse response,@PathVariable String no){
        ModelAndView mode = new ModelAndView("failure");
        try {
            String orderNo = HiDesUtils.desDeCode(no);
            logger.info("綁定银行卡跳转界面...订单号:{}",orderNo);
            request.setCharacterEncoding(Constant.CHART_UTF);
            response.setCharacterEncoding(Constant.CHART_UTF);
            String payType = request.getParameter("payType");//支付方式 1:支付宝 2:微信
            String merchNo = request.getParameter("merchNo");
            String flag = request.getParameter("flag");
            String cardId = request.getParameter("cardId");
            
            logger.info("綁定银行卡跳转界面参数:merchNo:{}",merchNo);
            if (StringUtils.isEmpty(merchNo)) {
                super.writeResponse(response, Constant.failedMsg("跳转参数为空"));
                return null;
            }
            mode = new ModelAndView("hanyin/hYBindCard");//默认跳转支付宝
            if ("2".equals(payType)) {
                mode = new ModelAndView("hanyin/wechat/bindCard");
            }
            mode.addObject("merchNo",merchNo);
            mode.addObject("orderNo",orderNo);
            mode.addObject("blackList","");
            mode.addObject("whiteList","");
            if ( StringUtils.isNotEmpty(flag) && flag.equals("2") ) {
            	VcOnlineContact vcOnlineContact = vcOnlineContactService.findContactByid(cardId);
            	mode.addObject("cardNo",vcOnlineContact.getCardNo());
            	mode.addObject("phone",vcOnlineContact.getPhone());
            	mode.addObject("cardId",cardId);
            	mode.addObject("uFlag","2");   //返回到前端知道是修改功能
            } else {
            	mode.addObject("uFlag","1");
            }
            return mode;
        } catch (UnsupportedEncodingException e) {
            logger.info("翰银跳转异常！");
            return mode.addObject("message","支付宝跳转异常");
        }
    }
    
    /**
     * @描述:限制银行卡类型（通过卡号查询出是民生卡的就需要绑定身份证，否则不需要）
     * @作者:gongchen
     * @时间:2019年6月21日上午10:08:50
     * @返回:JSONObject
     */
    @RequestMapping(value="/checkCardType/{no}",produces = "text/html;charset=UTF-8")
    public JSONObject checkCardType(HttpServletRequest request, HttpServletResponse response,@PathVariable String no){
    	try {
            String orderNo = HiDesUtils.desDeCode(no);
            request.setCharacterEncoding(Constant.CHART_UTF);
            response.setCharacterEncoding(Constant.CHART_UTF);
            String supportBanks = "广发,兴业,中信,光大,上海";
            
            String cardNo = request.getParameter("cardNo");
            logger.info("绑卡时查询银行卡类型:orderNo{},cardNo:{}",orderNo,cardNo);
            Integer checkFlag =0;
            if (StringUtils.isNotBlank(cardNo)) {
            	 String bankName = BankUtil.getNoCardName(cardNo.replaceAll(" ", ""));
 	            if( bankName.contains("民生") || bankName.indexOf("民生") >= 0){
 	            	checkFlag = 1;
 	            }
 	            
 	           for ( String supportBank: supportBanks.split(",") ) {
 	                if( bankName.contains(supportBank) ){
 	                	checkFlag = 2;
 	                	break;
 	                }
 	            }
            }
            JSONObject result = new JSONObject();
            result.put("checkFlag", checkFlag);
            super.writeResponse(response,result);
            return null;

        } catch (Exception e) {
            logger.info("卡类型查询异常！"+e);
            super.writeResponse(response, Constant.failedMsg("卡类型查询失败！"));
            return null;
        }
    }

    /**
     * @描述   支付结果跳转
     * @作者 gongchen
     * @时间 2019年6月3日10:23:57
     */
    @RequestMapping(value="/jumpSuccess/{no}",produces = "text/html;charset=UTF-8")
    public ModelAndView jumpSuccess(HttpServletRequest request, HttpServletResponse response,@PathVariable String no){
        ModelAndView mode = new ModelAndView("failure");
        try {
            String orderNo = HiDesUtils.desDeCode(no);
            request.setCharacterEncoding(Constant.CHART_UTF);
            response.setCharacterEncoding(Constant.CHART_UTF);

            String amount = request.getParameter("amount");
            String payFlag = request.getParameter("payFlag");
            String dataResult = request.getParameter("dataResult");
            String payType = request.getParameter("payType");//支付方式 1:支付宝 2:微信
            
            logger.info(" 支付结果跳转参数===========>>>:orderNo:{},amount:{},payFlag:{},dataResult:{}",orderNo,amount,payFlag,dataResult);
            if (StringUtils.isEmpty(amount)||StringUtils.isEmpty(orderNo)||StringUtils.isEmpty(payFlag)) {
                super.writeResponse(response, Constant.failedMsg("跳转参数为空"));
                return null;
            }
            if(Integer.valueOf(payFlag)==1){
                //交易成功！
                mode = new ModelAndView("hanyin/paySuccess");
                if (payType.equals("2")) {
                    mode = new ModelAndView("hanyin/wechat/success");
                }
            }else {
                //交易失败
                mode = new ModelAndView("hanyin/payFail");
                if (payType.equals("2")) {
                    mode = new ModelAndView("hanyin/wechat/fail");
                }
                logger.info("翰银支付宝交易（失败）！订单号:{}",orderNo);
            }
            mode.addObject("amount",amount);
            mode.addObject("orderNo",orderNo);
            mode.addObject("dataResult",dataResult);
            return mode;
        } catch (UnsupportedEncodingException e) {
            logger.info("翰银跳转异常！"+e);
            return mode.addObject("message","支付宝跳转异常");
        }
    }


    /**
     * @描述   删除银行卡跳转界面
     * @作者 gongchen
     * @时间 2019年6月3日10:23:57
     */
    @RequestMapping(value="/jumpDel/{no}",produces = "text/html;charset=UTF-8")
    public ModelAndView jumpDel(HttpServletRequest request, HttpServletResponse response,@PathVariable String no){
        ModelAndView mode = new ModelAndView("failure");
        try {
            String orderNo = HiDesUtils.desDeCode(no);

            request.setCharacterEncoding(Constant.CHART_UTF);
            response.setCharacterEncoding(Constant.CHART_UTF);

            String userId = request.getParameter("userId");
            String payType = request.getParameter("payType");//支付方式 1:支付宝 2:微信
            logger.info("删除银行卡界面跳转参数:orderNo:{},userId:{}",orderNo,userId);
            if (StringUtils.isEmpty(userId)) {
                super.writeResponse(response, Constant.failedMsg("跳转参数为空"));
                return null;
            }
            ArrayList<JSONObject> cardJson = vcOnlineContactService.getBankList( new VcOnlineContact(userId));
            if(cardJson == null){
                cardJson = new ArrayList<>();
            }

            mode = new ModelAndView("hanyin/hYDelCard");
            if ("2".equals(payType)) {
                //跳转微信页面
                mode = new ModelAndView("hanyin/wechat/cardManage");
            }
            mode.addObject("cardJson",cardJson.toString());
            return mode;
        } catch (UnsupportedEncodingException e) {
            logger.info("翰银跳转异常！"+e);
            return mode.addObject("message","支付宝跳转异常");
        }
    }


    /**
     * @描述:查询所有银行卡信息
     * @作者:gongchen
     * @时间:2019年6月4日下午10:46:43
     * @返回:JSONObject
     */
    @RequestMapping(value="/selectCardInfos/{no}",produces = "text/html;charset=UTF-8")
    public JSONObject selectCardInfos(HttpServletRequest request, HttpServletResponse response,@PathVariable String no){
        try {
            String orderNo = HiDesUtils.desDeCode(no);
            request.setCharacterEncoding(Constant.CHART_UTF);
            response.setCharacterEncoding(Constant.CHART_UTF);

            String userId = request.getParameter("userId");
            logger.info("查询所有银行卡信息跳转参数:orderNo:{},userId:{}",orderNo,userId);
            if (StringUtils.isEmpty(userId)) {
                super.writeResponse(response, Constant.failedMsg("跳转参数为空"));
                return null;
            }
            ArrayList<JSONObject> cardJson = vcOnlineContactService.getBankList( new VcOnlineContact(userId));
            if(cardJson == null){
                cardJson = new ArrayList<>();
            }
            JSONObject result = new JSONObject();
            result.put("bankInfo", cardJson.toString());
            super.writeResponse(response,result);
            return null;

        } catch (Exception e) {
            logger.info("翰银跳转异常！"+e);
            super.writeResponse(response, Constant.failedMsg("解除卡信息失败！"));
            return null;
        }
    }

    /**
     * @描述   删除银行卡后台操作
     * @作者 gongchen
     * @时间 2019年6月3日10:23:57
     */
    @RequestMapping(value="/delCardByIdNo/{no}",produces = "text/html;charset=UTF-8")
    public JSONObject delCardByIdNo(HttpServletRequest request, HttpServletResponse response,@PathVariable String no){

        try {
            String orderNo = HiDesUtils.desDeCode(no);
            request.setCharacterEncoding(Constant.CHART_UTF);
            response.setCharacterEncoding(Constant.CHART_UTF);

            String id = request.getParameter("id");
            String payType = request.getParameter("payType");
            logger.info("删除银行卡数据参数:id:{},订单号:{}, payType:{}",id, orderNo, payType);
            if (StringUtils.isEmpty(id)) {
                logger.info("跳转参数为空！,订单号:{}",orderNo);
                super.writeResponse(response, Constant.failedMsg("跳转参数为空"));
                return null;
            }
            VcOnlineContact vcOnlineContact = new VcOnlineContact();
            BigDecimal bdId=new BigDecimal(id);
            vcOnlineContact.setId(bdId);
            if (StringUtils.isEmpty(payType)) {
                vcOnlineContact.setExtension("无解绑类型");
            } else if ("1".equals(payType)) {
                vcOnlineContact.setExtension("支付宝解绑");
            } else if ("2".equals(payType)) {
                vcOnlineContact.setExtension("微信解绑");
            } else {
                vcOnlineContact.setExtension("未知类型解绑" + payType);
            }
            vcOnlineContact.setDelFlag(1);
            int index = vcOnlineContactService.delCardInfoById(vcOnlineContact);
            if( index<1 ){
                logger.error("删除卡信息失败");
                super.writeResponse(response, Constant.failedMsg("解除卡信息失败！"));
                return null;
            } else {
                super.writeResponse(response, Constant.successMsg("解除成功！"));
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            logger.info("翰银跳转异常！"+e);
            super.writeResponse(response, Constant.failedMsg("解除卡信息失败！"));
            return null;
        }
    }

    /**
     * @描述 获取支付宝USERID
     * @作者 nada
     * @时间 2019/5/28 11:40
     */
    @RequestMapping("gethyUserId")
    public ModelAndView getAlipayUserId(HttpServletRequest request){
        ModelAndView mode = new ModelAndView("failure");
        try {
            String orderNo = request.getParameter("orderNo");
            String userUrl = base_url.concat("/hyh5api/backHyUserId?").concat("orderNo=" + orderNo);
            String redirectUrl = ConfigUtil.alipayOpenAuthUrl + URLEncoder.encode(userUrl, "utf-8");
            return new ModelAndView("redirect:"+redirectUrl);
        } catch (Exception e) {
            logger.error ("获取支付宝USERID异常",e);
            mode.addObject("message","获取支付宝USERID异常");
            return mode;
        }
    }
    
    /**
     * @描述  瀚银支付页面 替换为 url
     * @作者 leoncongee
     * @时间 2019年6月14日15:33:35
     */
    @RequestMapping(value = "/unionUrl/{no}")
    public ModelAndView hanyinUnionUrl(HttpServletRequest request, HttpServletResponse response, @PathVariable String no) {
    	logger.info("瀚银支付页面替换url，订单号为空{}", no);
        ModelAndView mode = new ModelAndView("failure");
        String viewPath = "union/hanyinUnionPay";
        try {
            if(StringUtils.isBlank(no)){
            	logger.error("瀚银支付页面替换url，订单号为空{}", no);
            	mode.addObject("message","订单异常，请重新下单");
            }
            //查询支付界面参数
            VcOnlineOrderMade vcOnlineOrderMade = onlineOrderMadeService.findOrderByOrderNo(no);
            if(vcOnlineOrderMade!=null){
            	mode = new ModelAndView(viewPath);
            	mode.addObject("actionUrl",vcOnlineOrderMade.getOpenUrl());
            	JSONObject mapParams = new JSONObject();
            	String formDatas = vcOnlineOrderMade.getQrcodeUrl();
            	if(StringUtils.isNotBlank(formDatas)){
            		mapParams = JSONObject.parseObject(formDatas);		
            	}
            	//解密
            	String context =  mapParams.getString("context");
            	String actionUrl = mapParams.getString("actionUrl");
            	if(StringUtils.isNotBlank(context)){
            		mapParams.put("context", HiDesUtils.desDeCode(context,"unionOrder"));
            	}
            	if(StringUtils.isNotBlank(actionUrl)){
            		mapParams.put("actionUrl", HiDesUtils.desDeCode(actionUrl,"unionOrder"));
            	}
            	mode.addObject("map",mapParams);
            	mode.addObject("details",JSONObject.parseObject(vcOnlineOrderMade.getRemarks()));
            	mode.addObject("orderNo",no);
            }
			return mode;
        }catch (Exception e) {
            logger.error("瀚银支付页面替换url异常{}",no, e);
            mode.addObject("message", "订单异常，请重新下单");
            return mode;
        }
    }
    
    
    /**
     * @描述:查询订单状态
     * @作者:gongchen
     * @时间:2019年7月5日下午3:38:57
     * @返回:JSONObject
     */
    @RequestMapping(value="/selectOrderStatus/{no}",produces = "text/html;charset=UTF-8")
    public JSONObject selectOrderStatus(HttpServletRequest request, HttpServletResponse response,@PathVariable String no){
    	try {
    		if (StringUtil.isEmpty(no)) {
    			 logger.info("翰银支付宝查询订单号状态-订单号为空,请重试！");
                 super.writeResponse(response, Constant.failedMsg("翰银支付宝查询订单号状态-订单号为空"));
                 return null;
    		}
    		String orderNo = no;
    		
            request.setCharacterEncoding(Constant.CHART_UTF);
            response.setCharacterEncoding(Constant.CHART_UTF);
            VcOnlineOrder order = orderService.findOrderByOrderNo(orderNo);
            String bankNo = order.getBankNo();
            Integer status = order.getStatus();
            Integer hyStatus = 6;
            if ( status == 6 ) {
            	hyStatus = 6;
            } else if ( status == 4 ) {
            	hyStatus = 4;
            } else if ( status == 5 ) {
            	hyStatus = 5;
            } else {
            	hyStatus = 6;
            }
            JSONObject result = new JSONObject();
            result = Constant.successMsg("查询成功");
            result.put("hyStatus", hyStatus);
            result.put("bankNo", bankNo);
            logger.info("查询支付状态结果，订单号:{}",orderNo);
            super.writeResponse(response,result);
            return null;
            
		} catch (Exception e) {
			logger.info("翰银查询订单状态异常！"+e);
            super.writeResponse(response, Constant.failedMsg("翰银查询订单状态异常！"));
            return null;
		}
    }
    
}
