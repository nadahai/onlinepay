package com.vc.onlinepay.web.api;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.CacheConstants;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.pay.order.h5.AAPayH5ServiceImpl;
import com.vc.onlinepay.pay.order.scan.FLMScanServiceImpl;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.merch.SupplierSubno;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderAa;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.mapper.online.VcOnlineOrderMadeMapper;
import com.vc.onlinepay.persistent.service.channel.SupplierSubnoServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderAaService;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.BankCaseUtil;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.alipay.AlipayUtils;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import com.vc.onlinepay.web.base.BaseController;

import java.util.regex.Matcher;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 支付宝下单接口
 *
 * @类名称:RemittanceWebpagePayApi.java
 * @时间:2017年12月28日上午9:53:06
 * @版权:公司 Copyright (c) 2017
 */
@Controller
@RestController
@RequestMapping ("/openApi")
@CrossOrigin("*")
public class OpenApiController extends BaseController {

    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    @Autowired
    private CoreEngineProviderService coreEngineProviderService;
    @Autowired
    private RedisCacheApi redisCacheApi;
    @Autowired
    private CommonPayService commonPayService;
    @Autowired
    private SupplierSubnoServiceImpl supplierSubnoService;
    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;
    @Autowired
    private VcOnlineOrderMadeService vcOnlineOrderMadeService;
    @Autowired
    private FLMScanServiceImpl flmScanService;
    @Autowired
    private VcOnlineOrderAaService vcOnlineOrderAaService;
    @Autowired
    private VcOnlineOrderMadeMapper vcOnlineOrderMadeMapper;
    @Autowired
    private AAPayH5ServiceImpl funPayH5Service;
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmm");

    private static final BigDecimal MINAMOUNT_SUBTRACT = new BigDecimal (0.3);
    
    
    /**
	 * @描述:获取体验金口令
	 * @时间:2018年9月17日 下午9:35:46
	 */
    @RequestMapping("/takeTyjCode")
    @ResponseBody
    public JSONObject takeTyjCode(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject reqData = HttpRequestTools.getRequestJson (request);
        try {
        	String merchantIdStr = "bffc7c789cd5dd88cc9b3630524fa37c";
        	
        	String merchantId = reqData.containsKey("merchantId") ? reqData.getString ("merchantId") : "";
        	String vcOrderNo = reqData.containsKey("vcOrderNo") ? reqData.getString ("vcOrderNo") : "";
        	String returnUrl = reqData.containsKey ("returnUrl") ? reqData.getString ("returnUrl") : "";
        	String floatAmount = reqData.containsKey ("floatAmount") ? reqData.getString ("floatAmount") : "";
        	String cardIdx = reqData.containsKey ("cardIdx") ? reqData.getString ("cardIdx") : "";
        	String bankRemarks = reqData.containsKey ("bankRemarks") ? reqData.getString ("bankRemarks") : "";
        	String bankMark = reqData.containsKey ("bankMark") ? reqData.getString ("bankMark") : "";
        	String accountName = reqData.containsKey ("accountName") ? reqData.getString ("accountName") : "";
        	
        	if(StringUtils.isEmpty (merchantId)){
                return Constant.failedMsg("merchantId不能为空");
            }
        	if(merchantIdStr.contains(merchantId)){
                return Constant.failedMsg("merchantId不存在");
            }
        	if(StringUtils.isEmpty (vcOrderNo)){
                return Constant.failedMsg("vcOrderNo不能为空");
            }
        	if(StringUtils.isEmpty (returnUrl)){
                return Constant.failedMsg("returnUrl不能为空");
            }
        	if(StringUtils.isEmpty (floatAmount)){
                return Constant.failedMsg("floatAmount不能为空");
            }
        	if(StringUtils.isEmpty (cardIdx)){
                return Constant.failedMsg("cardIdx不能为空");
            }
        	if(StringUtils.isEmpty (bankRemarks)){
                return Constant.failedMsg("bankRemarks不能为空");
            }
        	if(StringUtils.isEmpty (bankMark)){
                return Constant.failedMsg("bankMark不能为空");
            }
        	if(StringUtils.isEmpty (accountName)){
                return Constant.failedMsg("accountName不能为空");
            }
        	
        	VcOnlineOrderMade made = new VcOnlineOrderMade();
        	made.setOrderNo("TYJ_"+vcOrderNo);
        	made.setMerchNo(merchantId);
        	made.setOpenUrl(returnUrl);
        	made.setQrcodeUrl(returnUrl);
        	made.setPaySource(9001);
        	made.setOpenType(9001);
        	
        	String bankUrl = AlipayUtils.buildBankPayUrl (cardIdx,bankRemarks,bankMark,accountName,floatAmount);
            
            String aliUser = coreEngineProviderService.getCacheCfgKey("online.ali.login.username");
            //获取体验金
            String url="http://hawkeyepay.cn:9988/netty/push";
            String parms="accountNo="+aliUser+
            		"&amount="+floatAmount+
            		"&orderNo="+vcOrderNo+
            		"&msg=test"+
            		"&qrway=101"+
            		"&bizContent="+URLEncoder.encode(bankUrl);
            
            try {
         	   logger.info ("API获取体验金请求参数{}",parms);
         	   String res = HttpClientTools.sendGet(url, parms);
         	   logger.info ("API获取体验金响应参数{}",res);
 			} catch (Exception e) {
 				logger.error ("API获取体验金异常{}",parms,e);
 			}
        	
            int r = vcOnlineOrderMadeService.save (made);
            if (r < 1) {
                return Constant.failedMsg("保存订单失败");
            }
        	return Constant.successMsg("获取成功");
        } catch (Exception e) {
            logger.error("状态修改异常",e);
            return Constant.failedMsg("系统异常");
        }
    }
    
    /**
     * @描述:付临门单订单状态更新
     * @时间:2018年12月26日 下午15:24:18
     */
    @RequestMapping("/findFlmOrder")
    @ResponseBody
    public JSONObject findFlmOrder(HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("付临门订单状态抓取:{}", requestJson);
            if (requestJson == null || requestJson.isEmpty()){
                logger.info("付临门订单状态获取参数为空");
                return Constant.failedMsg("付临门订单状态获取参数为空");
            }
            if (!requestJson.containsKey("orderNo") || requestJson.getString("orderNo").isEmpty()){
                logger.info("付临门订单编号为空");
                return Constant.failedMsg("付临门订单编号为空");
            }
            VcOnlineOrderMade vcOnlineOrderMade = new VcOnlineOrderMade();
            vcOnlineOrderMade.setOrderNo(requestJson.getString("orderNo"));
            try {
                List<VcOnlineOrderMade> lists = vcOnlineOrderMadeService.findFlmOrders(vcOnlineOrderMade);
                if(lists == null || lists.size()<1){
                	logger.info("没有获取到此订单");
                    return Constant.failedMsg("订单为空");
                }
                vcOnlineOrderMade = lists.get(0);
                String orderNo = vcOnlineOrderMade.getOrderNo();
                JSONObject result = flmScanService.orderMadeQuery(vcOnlineOrderMade.getUpMerchNo()
                		,vcOnlineOrderMade.getUpMerchKey(),vcOnlineOrderMade.getQrcodeUrl());
                logger.info("付临门回调数据:{},result:{}",orderNo,result);
                if(result == null || result.isEmpty()){
                	logger.info("付临门接口订单为空{}", vcOnlineOrderMade);
                    return Constant.failedMsg("付临门回调接口订单号为空");
                }
                String code = result.containsKey("code")?result.getString("code"):"";
                String msg = result.containsKey("msg")?result.getString("msg"):"";
                String status = result.containsKey("status")?result.getString("status"):"";
                VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(orderNo);
                if (vcOnlineOrder==null) {
                    logger.info("付临门回调接口订单号为空{}", vcOnlineOrderMade);
                    return Constant.failedMsg("付临门回调接口订单号为空");
                }
                Integer oldStatus = vcOnlineOrder.getStatus ();
                if( oldStatus== 4 || oldStatus == 2 || oldStatus == 5){
                    logger.info("付临门订单已经成功:{}", vcOnlineOrderMade);
                    return Constant.failedMsg("付临门订单已经成功");
                }
                String pOrderNo = result.containsKey("pOrderNo")?result.getString("pOrderNo"):"";
                if(StringUtil.isNotEmpty(pOrderNo)){
                    vcOnlineOrder.setpOrder(pOrderNo);
                }
                if(StringUtil.isEmpty(status)){
                    boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, 3,requestJson.toString(),msg);
                    logger.info("付临门订单已更新:{}", vcOnlineOrderMade);
                    return Constant.successMsg("付临门订单已更新");
                }
                if("交易成功".equalsIgnoreCase(status)){
                    boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, 4,requestJson.toString(),status);
                    logger.info("付临门订单已更新:{}", vcOnlineOrderMade);
                    return Constant.successMsg("付临门订单已更新");
                }
            } catch (Exception e) {
                logger.error("付临门回调接口业务处理异常", e);
                e.printStackTrace();
            }
            logger.error("付临门订单已更新:{}", vcOnlineOrderMade);
            return Constant.failedMsg("付临门订单");
        } catch (Exception e) {
            logger.error("付临门订单更新接口业务处理异常", e);
            return Constant.failedMsg("付临门订单更新异常");
        } 
    }
    
    /**
	 * @描述:修改订单描述信息
	 * @时间:2018年9月17日 下午9:35:46
	 */
    @RequestMapping("/modifyOrderdes")
    @ResponseBody
    public JSONObject modifyOrderdes(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject reqData = HttpRequestTools.getRequestJson (request);
        try {
        	String vcOrderNo = reqData.containsKey("vcOrderNo") ? reqData.getString ("vcOrderNo") : "";
        	String desType = reqData.containsKey ("desType") ? reqData.getString ("desType") : "";
        	if(vcOrderNo == null || "".equals(vcOrderNo)
        			|| desType== null || "".equals(desType)
        			) {
        		return Constant.failedMsg("订单号或描述类型不能为空");
        	}
        	vcOrderNo = HiDesUtils.desDeCode(vcOrderNo);
        	
        	switch (desType) {
            case "1":
            	commonPayService.updateOrderDes (vcOrderNo, "用户点击启动");
                return Constant.successMsg("描述更新成功");
            case "2":
            	commonPayService.updateOrderDes (vcOrderNo, "Android跳转");
                return Constant.successMsg("描述更新成功");
            case "3":
            	commonPayService.updateOrderDes (vcOrderNo, "其他跳转");
                return Constant.successMsg("描述更新成功");
            case "4":
            	commonPayService.updateOrderDes (vcOrderNo, "打开页面");
                return Constant.successMsg("描述更新成功");
            case "5":
            	commonPayService.updateOrderDes (vcOrderNo, "复制链接成功");
                return Constant.successMsg("描述更新成功");
            case "6":
            	commonPayService.updateOrderDes (vcOrderNo, "打开授权页");
                return Constant.successMsg("描述更新成功");
            case "7":
            	commonPayService.updateOrderDes (vcOrderNo, "Android授权页跳转");
            case "8":
            	commonPayService.updateOrderDes (vcOrderNo, "IOS授权页跳转");
                return Constant.successMsg("描述更新成功");
            default:
                return Constant.failedMsg("未知的描述类型"+desType);
        	}
			
            
        } catch (Exception e) {
            logger.error("状态修改异常",e);
            return Constant.failedMsg("系统异常");
        }
    }
    /**
     * @描述:支付宝回调接口
     * @时间:2017年12月28日 上午9:53:54
     */
    @RequestMapping (value = "/noticeData", produces = "text/html;charset=UTF-8")
    public void noticeData (HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject reqData = HttpRequestTools.getRequestJson (request);
            logger.info ("支付宝回调接口数据:{}", reqData);
            if (reqData == null || reqData.isEmpty ()) {
                super.writeResponse (response, Constant.failedMsg ("支付宝回调接口为空"));
                return;
            }
            //1.个人转账 2.协议转账 3.AA收款 4.转卡浮动 5.企业红包 6:转卡不浮动 7:好友转账 8:个人红包 10:主动收款 11:个人码 1001:钉钉群收款
            String pay_way = reqData.containsKey ("pay_way")?reqData.getString ("pay_way"):"";
            String type = reqData.containsKey ("type")?reqData.getString ("type"):"";
            if(StringUtil.isEmpty(pay_way) && StringUtil.isNotEmpty(type)){
                if("1001".equalsIgnoreCase(type)){
                    JSONObject result = this.saveDdAAPool(reqData);
                    super.writeResponse(response, result);
                    return;
                }else if ("12".equals (pay_way)) {
                    JSONObject  result = this.invokeCallback (reqData);
                }else if("login".equalsIgnoreCase(type)){
                    JSONObject result = this.invokeDDInfoCallback (reqData);
                    super.writeResponse(response, result);
                    return;
                }else if("alipay".equalsIgnoreCase(type)){
                    //保存remark
                	String orderNo = reqData.getString ("mark");
                	String payurl = reqData.getString ("payurl");
                	VcOnlineOrderMade vcOnlineOrder = new VcOnlineOrderMade();
                	vcOnlineOrder.setOrderNo(orderNo);
                	vcOnlineOrder.setRemarks(payurl);
                	vcOnlineOrderMadeMapper.updateRemarksByOrderNo(vcOnlineOrder);
                	
                	VcOnlineOrderMade made = vcOnlineOrderMadeMapper.findOrderByOrderNo(orderNo);
                	if("9001".equals(made.getOpenType()+"")) {
                		//调用接口
                		Map<String, String> prams = new HashMap<String, String>();
                		prams.put("vcOrderNo", made.getOrderNo());
                		prams.put("code", payurl);
                		String res = HttpClientTools.doPostMethodWithUrl(made.getOpenUrl(), prams);
                        
                        logger.info("API体验金回调{}",res);
                	}
                    return;
                }
            }
            JSONObject result = new JSONObject ();
            String groupBillItem = reqData.containsKey ("groupBillItem")?reqData.getString ("groupBillItem"):"";
            if (reqData.containsKey ("card4EndNo")) {
                result = this.invokeBankCallback (reqData);
            }else if (reqData.containsKey ("cardIdx")) {
                //result = this.invokeBankInfoCallback (reqData);
            }else if (StringUtil.isNotEmpty (groupBillItem)) {
                result = this.invokeDDAACallback (reqData);
            }else {
                result = this.invokeCallback (reqData);
            }
            super.writeResponse (response, result);
        } catch (Exception e) {
            logger.error ("支付宝回调接口异常", e);
            super.writeErrorResponse (response);
        }
    }

    /**
     * @描述:钉钉群收款回调接口
     * @时间:2018年1月5日 上午10:08:05
     */
    public JSONObject invokeDDAACallback(JSONObject reqData) throws IOException {
        try {
            logger.info("钉钉回调参数:{}", reqData);
            if (reqData == null || reqData.isEmpty()) {
                logger.error("钉钉回调参数为空{}",reqData);
                return  Constant.failedMsg("钉钉回调数据为空");
            }
            JSONObject groupBillModelJson = reqData.containsKey("groupBillModel")?reqData.getJSONObject("groupBillModel"):null;
            if(groupBillModelJson == null){
                logger.error("钉钉回调groupBillModel为空{}",reqData);
                return  Constant.failedMsg("钉钉回调groupBillModel为空");
            }
            List<Object> groupBillItem = reqData.containsKey("groupBillItem")?reqData.getJSONArray("groupBillItem"):null;
            if(groupBillItem == null){
                logger.error("钉钉回调groupBillItem为空{}",reqData);
                return  Constant.failedMsg("钉钉回调groupBillItem为空");
            }
            List<String> userIds = new ArrayList<>();
            Double amount = 0d;
            for (Object group: groupBillItem) {
                JSONObject groupJson = (JSONObject)group;
                int payStatus = groupJson.containsKey("payStatus")?groupJson.getIntValue("payStatus"):0;
                amount = groupJson.containsKey("amount")?groupJson.getDouble("amount"):0;
                String uid = groupJson.containsKey("uid")?groupJson.getString("uid"):"";
                if(payStatus == 1){
                    userIds.add(uid);
                }
            }
            String groupBillId = groupBillModelJson.getString("groupBillId");
            List<VcOnlineOrderAa> vcOnlineOrderAas = vcOnlineOrderAaService.findCallByUserIds (VcOnlineOrderAa.buildAAAATradNo(groupBillId,amount.intValue(),userIds));
            if(vcOnlineOrderAas == null || vcOnlineOrderAas.size ()<1){
                VcOnlineOrder vcOnlineOrder = new VcOnlineOrder();
                vcOnlineOrder.setOrderNo(groupBillId);
                vcOnlineOrder = VcOnlineOrder.biuldEmptyCopyOrder(groupBillId,"0",groupBillId,groupBillId+"未找到",reqData.toString(),125);
                payBusService.saveCopyOrder(vcOnlineOrder);
                return Constant.failedMsg("订单未找到"+groupBillId);
            }
            for (VcOnlineOrderAa vcOnlineOrderAa : vcOnlineOrderAas) {
                String orderNo = vcOnlineOrderAa.getOrderNo();
                String pOrder = vcOnlineOrderAa.getAaOrderNo();
                VcOnlineOrder vcOnlineOrder = new VcOnlineOrder();
                vcOnlineOrder.setpOrder(pOrder);
                vcOnlineOrder.setOrderNo(orderNo);
                vcOnlineOrder.setTraAmount(new BigDecimal(vcOnlineOrderAa.getAmount()));
                List<VcOnlineOrder> lists = payBusService.findOrderBySmstrxId(vcOnlineOrder);
                if(lists == null || lists.size()<1){
                    vcOnlineOrder.setRemarks("钉钉未找到订单"+pOrder);
                    vcOnlineOrder.setTraType(2);
                    vcOnlineOrder.setOrderDes (reqData.toString ());
                    payBusService.saveCopyOrder(vcOnlineOrder);
                    return Constant.failedMsg("钉钉未找到订单"+pOrder);
                }
                if(lists.size ()>1){
                    logger.error("钉钉多笔订单{}", reqData);
                    vcOnlineOrder = VcOnlineOrder.biuldEmptyCopyOrder(pOrder,"0",pOrder,pOrder+"找到多笔",reqData.toString(),125);
                    vcOnlineOrder.setOrderNo(pOrder);
                    payBusService.saveCopyOrder(vcOnlineOrder);
                    return Constant.failedMsg("钉钉多笔订单"+pOrder);
                }
                vcOnlineOrder = lists.get(0);
                if(vcOnlineOrder.getStatus()==4){
                    vcOnlineOrder.setpOrder(pOrder);
                    vcOnlineOrder.setRemarks("钉钉重复支付"+orderNo);
                    vcOnlineOrder.setTraType(1);
                    vcOnlineOrder.setOrderDes (reqData.toString ());
                    payBusService.saveCopyOrder(vcOnlineOrder);
                    return Constant.failedMsg("钉钉重复支付"+orderNo);
                }
                boolean isOk = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, 4, reqData.toString());
                if (isOk) {
                    logger.info("钉钉回调成功:{}",orderNo);
                    vcOnlineOrderAaService.updateOrderSuccess (new VcOnlineOrderAa(pOrder,orderNo));
                } else {
                    logger.error("钉钉回调失败{}", reqData);
                }
            }
            return Constant.successMsg("钉钉回调成功");
        } catch (Exception e) {
            logger.error("钉钉回调异常", e);
            return Constant.failedMsg("钉钉回调异常");
        }
    }
    /**
     * 钉钉信息回调接口
     * {"msg":"login successfully","uid":"341684038","accountNo":"15118135523","noticeUrl":"http://test.mall51.top/onlinepay/openApi/noticeData","login":"success","type":"login"}
     */
    public JSONObject invokeDDInfoCallback (JSONObject reqData) throws IOException {
        try {
            logger.info ("钉钉信息回调参数:{}", reqData);
            if (reqData == null || reqData.isEmpty ()) {
                return Constant.failedMsg ("钉钉信息回调参数为空");
            }
            String accountNo = reqData.containsKey ("accountNo") ? reqData.getString ("accountNo") : "";
            String uid = reqData.containsKey ("uid") ? reqData.getString ("uid") : "";
            if(StringUtils.isAnyEmpty (accountNo,uid)){
                return Constant.failedMsg ("accountNo,uid参数为空");
            }
            SupplierSubno supplierSubno = new SupplierSubno();
            supplierSubno.setUpMerchNo (accountNo);
            supplierSubno.setUserId(uid);
            int r = supplierSubnoService.updateUserId (supplierSubno);
            if(r > 0){
                return Constant.successMsg ("钉钉信息更新成功");
            }
            return Constant.failedMsg ("钉钉信息更新失败");
        } catch (Exception e) {
            logger.error ("钉钉信息回调异常", e);
            return Constant.failedMsg ("钉钉信息回调异常");
        }
    }

    /**
     * 银行卡信息回调接口
     * @data {"receiverName":"李海","accountNo":"63-9560388152","cardIdx":"1811252478690582809","cardNo":"622622******7454"}
     */
    public JSONObject invokeBankInfoCallback (JSONObject reqData) throws IOException {
        try {
            logger.info ("银行卡信息回调参数:{}", reqData);
            if (reqData == null || reqData.isEmpty ()) {
                return Constant.failedMsg ("银行卡信息回调参数为空");
            }
            String accountNo = reqData.containsKey ("accountNo") ? reqData.getString ("accountNo") : "";
            String cardIdx = reqData.containsKey ("cardIdx") ? reqData.getString ("cardIdx") : "";
            String receiverName = reqData.containsKey ("receiverName") ? reqData.getString ("receiverName") : "";
            String cardNo = reqData.containsKey ("cardNo") ? reqData.getString ("cardNo") : "";
            if(StringUtils.isAnyEmpty (accountNo,cardIdx,cardNo,receiverName)){
                return Constant.failedMsg ("accountNo,cardIdx,cardNo,receiverName参数为空");
            }
            SupplierSubno supplierSubno = new SupplierSubno();
            supplierSubno.setUpMerchNo (accountNo);
            supplierSubno.setCardIdx (cardIdx);
            supplierSubno.setName (receiverName);
            supplierSubno.setBankNo (cardNo.replace ("*********","***").replace ("******","***"));
            int r = supplierSubnoService.updateCardIdx (supplierSubno);
            if(r > 0){
                return Constant.successMsg ("银行卡信息更新成功");
            }
            return Constant.successMsg ("银行卡信息更新失败");
        } catch (Exception e) {
            logger.error ("银行卡信息回调异常", e);
            return Constant.failedMsg ("银行卡信息回调异常");
        }
    }

    /**
     * 转卡回调接口
     */
    public JSONObject invokeBankCallback(JSONObject reqData) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        String oldOrderNo = "";
        try {
            logger.info("转卡回调接口参数:{}", reqData);
            if (reqData == null || reqData.isEmpty()) {
                return  Constant.failedMsg("转卡回调数据为空");
            }
            String telPhone = reqData.containsKey("telPhone")?reqData.getString("telPhone"):"";
            String amount = reqData.containsKey("amount")?reqData.getString("amount"):"";
            String card4EndNo = reqData.containsKey("card4EndNo")?reqData.getString("card4EndNo"):"";
            String tradeNO = reqData.containsKey("tradeNo")?reqData.getString("tradeNo"):"";
            String gmtCreate = reqData.containsKey("gmtCreate")?reqData.getString("gmtCreate"):"";
            String smsContent = reqData.containsKey("smsContent")?reqData.getString("smsContent"):"";
            if(StringUtil.isEmpty (telPhone)){
                telPhone = reqData.containsKey("accountNo")?reqData.getString("accountNo"):"";
            }
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(telPhone);
            telPhone = m.replaceAll("");
            
            if((StringUtil.isEmpty (amount)|| "0".equals (amount)) && StringUtil.isNotEmpty (smsContent)){
                JSONObject bankInfo  = BankCaseUtil.getBankInfo (card4EndNo,smsContent);
                logger.info ("银行卡回调解析短信:{},结果:{}",smsContent,bankInfo);
                if(bankInfo !=null && !bankInfo.isEmpty ()){
                    amount = bankInfo.containsKey ("amount")?bankInfo.getString ("amount"):"";
                    card4EndNo = bankInfo.containsKey ("card4EndNo")?bankInfo.getString ("card4EndNo"):"";
                    gmtCreate = bankInfo.containsKey ("gmtCreate")?bankInfo.getString ("gmtCreate"):"";
                }
                if(StringUtil.isEmpty (gmtCreate)){
                    gmtCreate = sf.format(new Date ());
                }
            }
            if(StringUtil.isEmpty (tradeNO)){
                tradeNO = amount+"_"+card4EndNo+"_"+gmtCreate+"_"+telPhone;
            }
            if(StringUtil.isEmpty(amount)){
                logger.info ("转卡回调金额解析失败:{},{}",tradeNO,smsContent);
                vcOnlineOrder = VcOnlineOrder.biuldEmptyCopyOrder(tradeNO,amount,telPhone,"金额解析失败"+tradeNO,smsContent);
                vcOnlineOrder.setcOrder(tradeNO);
                vcOnlineOrder.setOrderNo(tradeNO);
                payBusService.saveCopyOrder(vcOnlineOrder);
                return Constant.failedMsg("金额解析失败"+tradeNO);
            }
            if(StringUtil.isEmpty(telPhone)){
                logger.info ("转卡回调账号为空:{},{}",tradeNO,smsContent);
                vcOnlineOrder = VcOnlineOrder.biuldEmptyCopyOrder(tradeNO,amount,telPhone,"账号为空"+tradeNO,smsContent);
                vcOnlineOrder.setcOrder(tradeNO);
                vcOnlineOrder.setOrderNo(tradeNO);
                payBusService.saveCopyOrder(vcOnlineOrder);
                return Constant.failedMsg("回调账号为空"+tradeNO);
            }
            String bankTransKey = AlipayUtils.buildBankTransKey (telPhone,amount);
            if(redisCacheApi.get (bankTransKey)==null){
                logger.info ("转卡缓存回调失效订单:{},{}",bankTransKey,smsContent);
                int timeout = coreEngineProviderService.getIntCacheCfgKey (CacheConstants.ONLINE_ALIPAY_SAME_ORDER_EXPIRED_TIME);
                vcOnlineOrder = VcOnlineOrder.biuldEmptyCopyOrder(tradeNO,amount,telPhone,"超时"+timeout+"|"+bankTransKey,smsContent);
                String allKey = AlipayUtils.buildBankTransAllCash (telPhone,amount,card4EndNo);
                vcOnlineOrder.setcOrder(allKey);
                if(redisCacheApi.get (allKey)!=null){
                    oldOrderNo = (String)redisCacheApi.get (allKey);
                    vcOnlineOrder.setOrderNo(oldOrderNo);
                }else{
                    vcOnlineOrder.setOrderNo(allKey);
                }
                payBusService.saveCopyOrder(vcOnlineOrder);
                return Constant.failedMsg("支付超过"+timeout+"分钟"+tradeNO);
            }
            oldOrderNo = (String)redisCacheApi.get (bankTransKey);
            VcOnlineOrder query = new VcOnlineOrder();
            //query.setUpMerchNo(telPhone);
            query.setSmstrxid(oldOrderNo);
            query.setTraAmount(new BigDecimal(amount));
            List<VcOnlineOrder> lists = payBusService.findOrderBySmstrxId(query);
            if(lists == null || lists.size() <=0){
                vcOnlineOrder = new VcOnlineOrder();
                vcOnlineOrder.setpOrder(tradeNO);
                List<VcOnlineOrder> list = vcOnlineOrderService.verifyRePayPOrderExist(vcOnlineOrder);
                if(list != null && list.size() > 0){
                    return Constant.failedMsg("转卡订单已存在"+tradeNO);
                }
                vcOnlineOrder = VcOnlineOrder.biuldEmptyCopyOrder(tradeNO,amount,telPhone,"转账到卡订单未找到",reqData.toString());
                vcOnlineOrder.setOrderNo(oldOrderNo);
                payBusService.saveCopyOrder(vcOnlineOrder);
                return Constant.failedMsg("转卡订单未找到"+oldOrderNo);
            }
            if(lists.size()>1){
                logger.error("找到多笔订单{}", reqData);
                return Constant.failedMsg("找到多笔订单"+oldOrderNo);
            }
            vcOnlineOrder = lists.get(0);
            //支付单号多次回调导致重复
            if(vcOnlineOrder.getStatus() == 4){
                vcOnlineOrder.setpOrder(tradeNO);
                vcOnlineOrder.setRemarks("转卡重复支付");
                vcOnlineOrder.setTraType(1);
                payBusService.saveCopyOrder(vcOnlineOrder);
                return Constant.failedMsg("重复支付"+oldOrderNo);
            }
            //支付宝单号已成功重复
            vcOnlineOrder.setpOrder(tradeNO);
            if(commonPayService.verifyCacheMerchPOrderExist(vcOnlineOrder)){
                vcOnlineOrder.setRemarks("转卡已成功");
                vcOnlineOrder.setTraType(2);
                payBusService.saveCopyOrder(vcOnlineOrder);
                return Constant.failedMsg("转卡已成功"+tradeNO);
            }
            int status = 4;
            boolean isOk = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status, reqData.toString());
            logger.info("转卡回调结果{},订单:{}",isOk,oldOrderNo);
            if (isOk) {
                redisCacheApi.remove(bankTransKey);
                return Constant.successMsg("转卡回调成功"+oldOrderNo);
            }
            return Constant.failedMsg("转卡回调更新失败"+oldOrderNo);
        } catch (Exception e) {
            logger.error("转卡处理异常", e);
            return Constant.failedMsg("转卡回调处理异常"+oldOrderNo);
        }
    }

    /**
     * @描述:自研支付宝回调
     * @时间:2018年1月5日 上午10:08:05
     * @数据{"amount":"1.45","orderNo":"h5190117201027076452","tradeNo":"20190117200040011100130067928552","accountNo":"e18033920362@163.com","gmtCreate":0}
     */
    public JSONObject invokeCallback (JSONObject reqData) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        try {
            logger.info ("自研支付宝回调:{}", reqData);
            if (reqData == null || reqData.isEmpty ()) {
                return Constant.failedMsg ("接收数据为空");
            }
            String oldOrderNo = reqData.containsKey ("orderNo") ? reqData.getString ("orderNo") : "";
            String accountNo = reqData.containsKey("accountNo")?reqData.getString("accountNo"):"";
            String amount = reqData.containsKey("amount")?reqData.getString("amount"):"";
            String name = reqData.containsKey("name")?reqData.getString("name"):"";
            String tradeNO = reqData.containsKey("tradeNo")?reqData.getString("tradeNo"):"";
            if(StringUtil.isNotEmpty(amount) && StringUtil.isNotEmpty(accountNo)){
                String key = accountNo+"_"+amount;
                oldOrderNo = (String) redisCacheApi.get(key);
                logger.info("获取缓存订单key:{},value：{}",key,oldOrderNo);
            }
            if (StringUtil.isEmpty (oldOrderNo)) {
                oldOrderNo = tradeNO;
            }
            if(StringUtil.isNotEmpty(name)){
                tradeNO = tradeNO+name;
            }
            if (oldOrderNo.contains ("订单描述:")) {
                oldOrderNo = oldOrderNo.replace ("订单描述:", "");
            }
            /*if (oldOrderNo.length () > 18) {
                oldOrderNo = oldOrderNo.substring (0, 18);
            }*/
            if (StringUtil.isEmpty (oldOrderNo)) {
                logger.error ("回调订单号为空{},{}", oldOrderNo, reqData);
                return Constant.failedMsg ("回调订单号为空" + oldOrderNo);
            }
            VcOnlineOrder query = new VcOnlineOrder ();
            query.setOrderNo (oldOrderNo);
            query.setTraAmount (new BigDecimal (amount));
            List<VcOnlineOrder> lists = payBusService.findOrderBySmstrxId (query);
            if (lists == null || lists.size () <= 0) {
                vcOnlineOrder = new VcOnlineOrder ();
                vcOnlineOrder.setpOrder (tradeNO);
                List<VcOnlineOrder> list = vcOnlineOrderService.verifyRePayPOrderExist (vcOnlineOrder);
                if (list != null && list.size () > 0) {
                    return Constant.failedMsg ("异常订单已存在" + tradeNO);
                }
                vcOnlineOrder = VcOnlineOrder.biuldEmptyCopyOrder (tradeNO, amount, accountNo, "订单未找到", reqData.toString ());
                vcOnlineOrder.setOrderNo (oldOrderNo);
                payBusService.saveCopyOrder (vcOnlineOrder);
                return Constant.failedMsg ("订单未找到" + oldOrderNo);
            }
            //回调单号过多
            if (lists.size () > 1) {
                vcOnlineOrder = VcOnlineOrder.biuldEmptyCopyOrder (tradeNO, amount, accountNo, "找到多笔订单", reqData.toString ());
                vcOnlineOrder.setOrderNo (oldOrderNo);
                payBusService.saveCopyOrder (vcOnlineOrder);
                return Constant.failedMsg ("找到多笔订单" + oldOrderNo);
            }
            vcOnlineOrder = lists.get (0);
            //回调账号错误
			if(StringUtil.isNotEmpty(accountNo) && !accountNo.equals(vcOnlineOrder.getUpMerchNo().trim())){
				vcOnlineOrder.setpOrder(tradeNO);
				vcOnlineOrder.setRemarks("回调账号错误");
				vcOnlineOrder.setTraType(2);
				payBusService.saveCopyOrder(vcOnlineOrder);
				return Constant.failedMsg("回调账号错误"+oldOrderNo);
			}
            //回调单号重复
            if (vcOnlineOrder.getStatus () == 4) {
                vcOnlineOrder.setpOrder (tradeNO);
                vcOnlineOrder.setRemarks ("回调单号重复");
                vcOnlineOrder.setTraType (1);
                payBusService.saveCopyOrder (vcOnlineOrder);
                return Constant.failedMsg ("回调单号重复" + oldOrderNo);
            }
            //回调单号错误
            vcOnlineOrder.setpOrder (tradeNO);
            if (commonPayService.verifyCacheMerchPOrderExist (vcOnlineOrder)) {
                vcOnlineOrder.setRemarks ("订单匹配错误");
                vcOnlineOrder.setTraType (2);
                payBusService.saveCopyOrder (vcOnlineOrder);
                return Constant.failedMsg ("订单匹配错误" + oldOrderNo);
            }
            //回调金额差额过大(-1,1)
            BigDecimal newAmout = new BigDecimal (amount).setScale (2, BigDecimal.ROUND_HALF_DOWN);
            BigDecimal oldAmout = vcOnlineOrder.getTraAmount ().setScale (2, BigDecimal.ROUND_HALF_DOWN);
            BigDecimal abs = newAmout.subtract (oldAmout).abs ();
            if (abs.compareTo (MINAMOUNT_SUBTRACT) == 1) {
                vcOnlineOrder.setTraAmount (newAmout);
                vcOnlineOrder.setTraType (2);
                vcOnlineOrder.setRemarks ("回调金额误差");
                payBusService.saveCopyOrder (vcOnlineOrder);
                return Constant.failedMsg ("回调金额误差" + oldOrderNo);
            }
            //回调超过5分钟
            if (System.currentTimeMillis () - vcOnlineOrder.getCreateDate ().getTime () > 300000L) {
                vcOnlineOrder.setTraType (2);
                vcOnlineOrder.setRemarks ("支付超过5分钟");
                payBusService.saveCopyOrder (vcOnlineOrder);
                return Constant.failedMsg ("支付超过5分钟" + oldOrderNo);
            }
            boolean isOk = commonCallBackServiceImpl.callBackOrder (vcOnlineOrder, 4, reqData.toString ());
            if (isOk) {
                return Constant.successMsg ("回调成功" + oldOrderNo);
            } else {
                logger.error ("更新回调失败{}", reqData);
                return Constant.failedMsg ("更新回调失败" + oldOrderNo);
            }
        } catch (Exception e) {
            logger.error ("自研支付宝回调异常：{}",reqData,e);
            return Constant.failedMsg ("自研支付宝回调异常");
        }
    }

    /**
     * @描述:推送数据接收
     * @时间:2017年12月28日 上午9:53:54
     */
    @RequestMapping (value = "/pushErrorData", produces = "text/html;charset=UTF-8")
    public void pushErrorData (HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject reqData = HttpRequestTools.getRequestJson (request);
            logger.info ("测试异常数据:{}", reqData);
            writeResponse (response,Constant.successMsg ("ok"));
        } catch (Exception e) {
            logger.error ("支付宝异步通知接口响应异常", e);
            super.writeErrorResponse (response);
        }
    }

    /**
     * @描述 保存钉钉池
     * @时间 2019/5/19 15:40
     */
    public JSONObject saveDdAAPool(JSONObject reqData) {
        try {
            String users = reqData.containsKey ("users")?reqData.getString ("users"):"";
            String id = reqData.containsKey ("id")?reqData.getString ("id"):"";
            String upMerchNo = reqData.containsKey ("mobile")?reqData.getString ("mobile"):"";
            if(StringUtils.isAnyEmpty (id,upMerchNo)){
                return Constant.failedMsg ("money,creator,groupBillName,qrcode参数为空");
            }
            if(StringUtil.isNotEmpty(users)){
                String amount = reqData.containsKey ("amount")?reqData.getString ("amount"):"";
                String mark = reqData.containsKey ("mark")?reqData.getString ("mark"):"";
                String cid = reqData.containsKey ("cid")?reqData.getString ("cid"):"";
                String[] ids =  users.split(",");
                int times = ids.length;
                int amount2 = Double.valueOf(amount).intValue();
                VcOnlineOrderAa baseOrder = new VcOnlineOrderAa(){{
                    setUpMerchNo(upMerchNo);
                    setUserid(id);
                    setTradeno(cid);
                    setAaOrderNo(id+"#"+cid+"#"+mark);
                    setOrderNo(cid);
                    setToken(upMerchNo);
                    setZhOrder(mark);
                    setRemarks(users);
                    setRemark(mark);
                    setType(1);
                    setStatus(4);
                    setAmount(amount2);
                    setShowTimesTotal (times);
                    setTotalAmount(times*amount2);
                }};
                vcOnlineOrderAaService.insertOne(baseOrder);
                vcOnlineOrderAaService.updateToken(VcOnlineOrderAa.buildZhOrder(mark,id,upMerchNo));
                return Constant.successMsg("修改成功");
            }
            String money = reqData.containsKey ("money")?reqData.getString ("money"):"";
            String bizId = reqData.containsKey ("bizId")?reqData.getString ("bizId"):"";
            String qrcode = reqData.containsKey ("qrcode")?reqData.getString ("qrcode"):"";
            String creator = reqData.containsKey ("creator")?reqData.getString ("creator"):"";
            String groupBillName = reqData.containsKey ("groupBillName")?reqData.getString ("groupBillName"):"";
            if(StringUtils.isAnyEmpty (creator,money,groupBillName,qrcode)){
                return Constant.failedMsg ("money,creator,groupBillName,qrcode参数为空");
            }
            JSONObject qrcodeJson = reqData.getJSONObject("qrcode");
            if(qrcodeJson == null || qrcodeJson.isEmpty() || !qrcodeJson.containsKey("payUrl")){
                return Constant.failedMsg ("qrcodeJson参数为空");
            }
            String payUrl = qrcodeJson.getString("payUrl");
            if(StringUtil.isEmpty(payUrl)){
                return Constant.failedMsg ("payUrl参数为空");
            }
            String token = "";
            VcOnlineOrderAa vcOnlineOrderAa = vcOnlineOrderAaService.findAAByZhOrder(VcOnlineOrderAa.buildZhOrder(groupBillName,creator,""));
            if(vcOnlineOrderAa !=null && StringUtil.isNotEmpty(vcOnlineOrderAa.getUpMerchNo())){
                token = vcOnlineOrderAa.getUpMerchNo();
            }else{
                token = creator;
            }
            int money2 = Double.valueOf(money).intValue();
            VcOnlineOrderAa baseOrder = new VcOnlineOrderAa(){{
                setUpMerchNo(upMerchNo);
                setUserid(id);
                setTradeno(bizId);
                setAaOrderNo(id+"#"+bizId);
                setZhOrder(groupBillName);
                setShowTimesTotal (1);
                setTotalAmount(money2);
                setStatus(3);
                setType (2);
                setAmount(money2);
                setRemark(groupBillName);
                setRemarks(getDdAAPayUrl(payUrl));
            }};
            baseOrder.setToken(token);
            vcOnlineOrderAaService.insertOne(baseOrder);
            return Constant.successMsg ("创建库存成功");
        } catch (Exception e) {
            logger.error("创建AA收款池异常", e);
            return  Constant.failedMsg ("创建AA收款池异常");
        }
    }

    /**
     * @描述:创建钉钉AA收款池
     * @时间:2017年12月28日 上午9:53:54
     */
    @PostMapping(value = "/createDdAAPool")
    public void createDdAAPool(HttpServletRequest request,HttpServletResponse response) {
        try {
            JSONObject reqData = HttpRequestTools.getRequestJson(request);
            logger.info("钉钉AA收款池数据:{}", reqData);
            if (reqData == null || reqData.isEmpty()) {
                super.writeResponse(response, Constant.failedMsg("AA收款池数据为空"));
                return;
            }
            String amount = reqData.containsKey ("amount")?reqData.getString ("amount"):"";
            String upMerchNo = reqData.containsKey ("upMerchNo")?reqData.getString ("upMerchNo"):"";
            String reason = reqData.containsKey ("reason")?reqData.getString ("reason"):"";
            if(StringUtils.isAnyEmpty (upMerchNo,reason,amount)){
                super.writeResponse(response, Constant.failedMsg ("amount,upMerchNo,reason参数为空"));
                return;
            }
            if(!Constant.isNumeric (amount)){
                super.writeResponse(response, Constant.failedMsg ("仅支持整数金额"));
            }
            int amount2 = Integer.valueOf (amount);
            if(amount2 <1 &&  amount2>5000){
                super.writeResponse(response, Constant.failedMsg ("金额范围1-5000"));
                return;
            }
            if(vcOnlineOrderAaService.findOrderCount(upMerchNo)>30){
                super.writeResponse(response, Constant.failedMsg ("每账号每天限制30单"));
                return;
            }
            int repeatCount = vcOnlineOrderAaService.findRepead(amount2,upMerchNo,reason);
            if(repeatCount>0) {
                super.writeResponse(response, Constant.failedMsg (upMerchNo+"收款理由重复!"));
                return;
            }
            JSONObject resutl = funPayH5Service.getDdAAppInfo (upMerchNo,reason,amount2);
            logger.info("钉钉下单推送,账号:{},推送结果:{}",upMerchNo,resutl);
            super.writeResponse(response,resutl);
        } catch (Exception e) {
            logger.error("创建AA收款池异常", e);
            super.writeErrorResponse(response);
            return;
        }
    }
    /**
     * @描述 获取钉钉支付链接
     * @作者 nada
     * @时间 2019/5/19 17:37
     */
    public static String getDdAAPayUrl(String str) {
        try {
            String[] strs = str.split("&");
            if(strs == null || strs.length<1){
                return "";
            }
            StringBuffer buffer = new StringBuffer();
            int i = 0;
            for(String s : strs) {
                i ++;
                String key = s.substring(0,s.indexOf("="));
                String value = s.substring(s.indexOf("=")+1);
                if(StringUtils.isAnyEmpty(key,value)){
                    continue;
                }
                if("biz_content".equalsIgnoreCase(key) || "sign".equalsIgnoreCase(key) || "timestamp".equalsIgnoreCase(key)){
                    buffer.append(key).append("=").append(URLEncoder.encode(value,"utf-8")).append("&");
                    continue;
                }
                if(strs.length == i){
                    buffer.append(key).append("=").append(value);
                }else{
                    buffer.append(key).append("=").append(value).append("&");
                }
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @描述:测试接收API
     * @时间:2017年12月28日 上午9:53:54
     */
    @RequestMapping (value = "/test", produces = "text/html;charset=UTF-8")
    public void test (HttpServletRequest request, HttpServletResponse response) {
        try {
            JSONObject reqData = HttpRequestTools.getRequestJson (request);
            logger.info ("测试接收API:{}", reqData);
            writeResponse (response,Constant.successMsg ("ok"));
        } catch (Exception e) {
            logger.error ("测试接收API异常", e);
            super.writeErrorResponse (response);
        }
    }
}
