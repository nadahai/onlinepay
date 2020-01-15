package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nada
 * @描述:LJP淘宝代付支付回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/lJPDFCallBackController")
public class LJPDFCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:LJP淘宝代付支付回调处理
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
            logger.error("LJP淘宝代付支付宝回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:LJP淘宝代付支付宝回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
    	try {
            ///JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("LJP淘宝代付支付回调接口接收参数2:{}", request.toString());
            logger.info("LJP淘宝代付支付回调接口接收参数getParameter:{}", request.getParameter("orderid"));
            //logger.info("LJP淘宝代付支付回调接口接收参数getAttribute:{}", request.getAttribute("orderid"));
            if (request.getParameter("orderid") == null || request.getParameter("orderid").isEmpty()){
                logger.error("LJP淘宝代付支付回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = request.getParameter("orderid");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("LJP淘宝代付支付回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            
            String memberid = request.getParameter("memberid");
            String orderid = request.getParameter("orderid");
            String amount = request.getParameter("amount");
            String transaction_id = request.getParameter("transaction_id");
            String datetime	= request.getParameter("datetime");
            String returncode = request.getParameter("returncode");
            
            String sign = request.getParameter("sign").toUpperCase();
            
            JSONObject parms = new JSONObject();
            parms.put ("memberid",memberid);
            parms.put ("orderid",orderid);
            parms.put ("amount",amount);
            parms.put ("transaction_id", transaction_id);
            parms.put ("datetime", datetime);
            parms.put ("returncode",returncode);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key=wj5es2cl5cnzrb5jsvkrtfqdxz0e67gc";
            logger.info("排序后{}",sourctxt1);
            String newSign = Md5Util.md5(sourctxt1).toUpperCase();
            
            logger.info("newSign,sing{},{}",newSign,sign);
            
            int status =0;
            if ("00".equals(returncode) && newSign.equals(sign)) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,request.toString());
            if (isOK) {
            	if(status == 4) {
            		return "ok";
            	}else {
            		return Constant.res_FAILED;
            	}
                
            } else {
                logger.error("LJP淘宝代付支付回调接口更新失败{}", request.toString());
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("LJP淘宝代付支付回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        }
    }
}
