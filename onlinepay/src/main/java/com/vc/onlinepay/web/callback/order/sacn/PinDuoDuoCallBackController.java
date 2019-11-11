package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.DigestUtil;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jauyang
 * @描述:拼多多支付宝回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/pinDuoDuoCallBackController")
public class PinDuoDuoCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:拼多多支付宝回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    @Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request, HttpServletResponse response)  {
        setHttpServletContent(request, response);
        try {
            response.setContentType("text/html");
            String result = this.invokeCallback(request);
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("拼多多支付宝调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:拼多多ZFB回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("拼多多ZFB回调接口接收参数:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("拼多多ZFB回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            
            String Key = "TZRBcmFv71MVpxyjzQ0wDuy5uHJ0fuMy";//商户密钥
            String p1_MerId = "30067";							//商户ID
            String r0_Cmd = request.getParameter("r0_Cmd");
            String r1_Code = request.getParameter("r1_Code");
            String r2_TrxId = request.getParameter("r2_TrxId");	//平台流水号
            String r3_Amt = request.getParameter("r3_Amt");		//支付金额
            String r4_Cur = request.getParameter("r4_Cur");
            String r5_Pid = request.getParameter("r5_Pid");
            String r6_Order =request.getParameter("r6_Order");	//商户订单号
            String r7_Uid = request.getParameter("r7_Uid");
            String r8_MP = request.getParameter("r8_MP");
            String r9_BType = request.getParameter("r9_BType");  //通知类型 1同步通知 2异步通知
            String rp_PayDate = request.getParameter("rp_PayDate");
            String hmac = request.getParameter("hmac");			//数据签名
            String sbOld = "";
            sbOld += p1_MerId;
            sbOld += r0_Cmd;
            sbOld += r1_Code;
            sbOld += r2_TrxId;
            sbOld += r3_Amt;
            sbOld += r4_Cur;
            sbOld += r5_Pid;
            sbOld += r6_Order;
            sbOld += r7_Uid;
            sbOld += r8_MP;
            sbOld += r9_BType;        
            sbOld += rp_PayDate;

            String nhmac = DigestUtil.hmacSign(sbOld, Key); //数据签名
            
            String vcOrderNo = requestJson.getString("r6_Order");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("拼多多ZFB回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            //状态 1下单成功 2下单失败 3下单中 4支付成功 5支付失败 6:处理中 7:代付中 8:代付失败 9交易退款
            int status =0;
            if (nhmac.equals(hmac)) {
                if ("1".equals(r1_Code)) {         	
                	//支付成功,请处理自己的逻辑 请注意通知可能会多次 请避免重复到帐
                	status = 4;
                	
                }else {
                	logger.error("拼多多ZFB回调接口支付失败{}", requestJson);
               	 	status = 3;
                }
            }else {
            	logger.error("拼多多ZFB回调接口支付失败{}", requestJson);
           	 	status = 3;
            }
            
            
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "success";
            } else {
                logger.error("拼多多ZFB回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("拼多多ZFB回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        } 
    }
}
