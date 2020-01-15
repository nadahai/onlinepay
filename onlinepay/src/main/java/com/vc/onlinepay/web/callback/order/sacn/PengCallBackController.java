package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @类名称:MagicUnionCallBackApi.java
 * @时间:2018年1月4日下午8:07:35
 * @作者:nada
 * @版权:公司 Copyright (c) 2018
 */
@Controller
@RestController
@RequestMapping("/pengCallBackController")
public class PengCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:老彭回调接口
     * @作者:nada
     * @时间:2018年1月4日 下午8:04:18
     */
    @Override
    @RequestMapping(value = "")
    public void doPost(HttpServletRequest request, HttpServletResponse response)  {
        setHttpServletContent(request, response);
        try {
            response.setContentType("text/html");
            String result = this.invokeCallback(request);
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("老彭回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:执行老彭回调接口
     * @作者:nada
     * @时间:2018年1月5日 上午10:08:05
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        try {
        	JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("老彭回调接口接收参数:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("老彭回调接口获取参数为空");
                return "ERROR";
            }
            String vcOrderNo = requestJson.getString("out_order_no");
            if (StringUtil.isEmpty(vcOrderNo)) {
                logger.error("老彭回调接口订单号为空{}", vcOrderNo);
                return "ERROR";
            }
            
            String order_no = requestJson.getString("order_no");
            String out_order_no = requestJson.getString("out_order_no");
            String pay_amount = requestJson.getString("pay_amount");
            String pay_type = requestJson.getString("pay_type");
            String subject = requestJson.getString("subject");
            String order_status = requestJson.getString("order_status");
            String pay_time = requestJson.getString("pay_time");
            String mch_id = requestJson.getString("mch_id");
            
            JSONObject parms = new JSONObject();
            parms.put ("order_no",order_no);
            parms.put ("out_order_no",out_order_no);
            parms.put ("pay_amount",pay_amount);
            parms.put ("pay_type", pay_type);
            parms.put ("subject", subject);
            parms.put ("order_status",order_status);
            parms.put ("pay_time",pay_time);
            parms.put ("mch_id",mch_id);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key=8e3a80fd895e020d9a39cb470094dbb8";
            logger.info("排序后{}",sourctxt1);
            String pay_md5sign = Md5Util.md5(sourctxt1).toLowerCase();
            
            String sign = requestJson.getString("sign").toLowerCase();
            
            int status = 0 ;
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(null==vcOnlineOrder){
                logger.error("老彭回调接口订单号未找到{}", vcOrderNo);
                return "ERROR";
            }
            vcOnlineOrder.setpOrder(vcOrderNo);
            if ("true".equals(order_status) && sign.equals(pay_md5sign)) {
                status = 4;
            } else {
                status = 3;
            }
            boolean isOk  = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOk) {
                return "success";
            } else {
                logger.error("老彭回调结果{}更新失败{}",isOk,requestJson);
                return "ERROR";
            }
        } catch (Exception e) {
            logger.error("老彭回调接口业务处理异常", e);
            return "ERROR";
        }
    }
}
