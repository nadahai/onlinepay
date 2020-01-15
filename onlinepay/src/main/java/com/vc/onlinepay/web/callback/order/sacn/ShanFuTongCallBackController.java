package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.utils.sand.MD5;
import com.vc.onlinepay.web.base.BaseController;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nada
 * @描述:闪付通回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/shanFuTongCallBackController")
public class ShanFuTongCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:闪付通回调接口
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
            logger.error("闪付通回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:闪付通回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("闪付通回调接口接收参数:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("闪付通回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestJson.getString("user_order_no");
            
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("闪付通回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            String orderno = requestJson.getString("orderno");
            String tradeno = requestJson.getString("tradeno");
            String price = requestJson.getString("price");
            String realprice = requestJson.getString("realprice");
            
            String sourctxt1 = vcOrderNo + orderno + tradeno + price + realprice + "7682aae3fba842b0b09cfc8c160246ca";
            		
            String sign = Md5Util.md5(sourctxt1).toUpperCase();
            String resSign = requestJson.getString("sign").toUpperCase();
            
            logger.info("排序后:{}"+sourctxt1);
            logger.info("闪付通sign，sjprice:{}",sign);
            int status =0;
            if (sign.equals(resSign)) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "success";
            } else {
                logger.error("闪付通回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("闪付通回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        } 
    }
}
