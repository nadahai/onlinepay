package com.vc.onlinepay.web.callback.order.sacn;

import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import cn.hutool.json.XML;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jauyang
 * @描述:友付回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/youFuCallBackController")
public class YouFuCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:友付回调接口
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
            logger.error("友付回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:友付回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
        try {
        	
        	String str = HttpRequestTools.getFormDataRequest(request);
        	
            logger.info("友付回调接口接收参数:{}", str);
            
            if (str == null || str.isEmpty()){
                logger.error("友付回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            cn.hutool.json.JSONObject payParams = XML.toJSONObject(str);
            
            String vcOrderNo = payParams.getJSONObject("xml").getStr("spbillno");
            
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("友付回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            Map<String, String> parms = new HashMap<String, String>();
            
            parms.put("version", "2.0");
            parms.put("charset", "UTF-8");
            parms.put("retcode", payParams.getJSONObject("xml").getStr("retcode"));
            parms.put("retmsg", payParams.getJSONObject("xml").getStr("retmsg"));
            parms.put("spid", payParams.getJSONObject("xml").getStr("spid"));
            parms.put("spbillno", payParams.getJSONObject("xml").getStr("spbillno"));
            parms.put("transactionId", payParams.getJSONObject("xml").getStr("transactionId"));
            parms.put("outTransactionId", payParams.getJSONObject("xml").getStr("outTransactionId"));
            parms.put("tranAmt", payParams.getJSONObject("xml").getStr("tranAmt"));
            parms.put("payAmt", payParams.getJSONObject("xml").getStr("payAmt"));
            parms.put("result", payParams.getJSONObject("xml").getStr("result"));
            parms.put("attach", payParams.getJSONObject("xml").getStr("attach"));
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key=b7d76880afcb4f33957bd71e10fbb446";
            logger.info("排序后{}",sourctxt1);
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            String reqSign = payParams.getJSONObject("xml").getStr("sign");
            logger.info("友付返回sign,实际sign{},{}",reqSign,sign);
            int status =0;
            if ("pay_success".equals(payParams.getJSONObject("xml").getStr("result"))
            		&& sign.equals(reqSign)) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,str);
            if (isOK) {
                return "success";
            } else {
                logger.error("友付回调接口更新失败{}", str);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("友付回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        }
    }
}
