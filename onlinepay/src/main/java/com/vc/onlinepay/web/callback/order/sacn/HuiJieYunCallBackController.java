package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.utils.sand.MD5;
import com.vc.onlinepay.web.base.BaseController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jauyang
 * @描述:惠捷云回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/huiJieYunCallBackController")
public class HuiJieYunCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:惠捷云回调处理
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
            logger.error("惠捷云回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:惠捷云回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("惠捷云回调接口接收参数2:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("惠捷云回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestJson.getString("order_id");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("惠捷云回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            String mer_id = requestJson.getString("mer_id");
            String order_id = requestJson.getString("order_id");
            String pay_state = requestJson.getString("pay_state");
            String amount = requestJson.getString("amount");
            String resSign = requestJson.getString("sign").toUpperCase();
            
            String str = "amount="+amount+"&mer_id="+mer_id+"&order_id="+order_id+"&pay_state="+pay_state+"&key=cfaedf4da018b1bd43c8dec217776e2f";
            String sign = DigestUtils.md5DigestAsHex(str.getBytes()).toUpperCase();
            logger.info("排序后:{}"+str);
            logger.info("惠捷云sign，sjprice:{}",sign);
            int status =0;
            if (sign.equals(resSign) && "1".equals(pay_state)) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "success";
            } else {
                logger.error("惠捷云回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("惠捷云回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        } 
    }
}
