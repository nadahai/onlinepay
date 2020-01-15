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
 * @描述:快包支付回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/kuaiBaoCallBackController")
public class KuaiBaoCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:快包支付回调处理
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
            logger.error("拿铁支付宝回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:拿铁支付宝回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
    	try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("快包支付回调接口接收参数2:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("快包支付回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestJson.getString("order_id");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("快包支付回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            String query_id = requestJson.getString("query_id");
            String app_id = requestJson.getString("app_id");
            String order_id = requestJson.getString("order_id");
            String amount = requestJson.getString("amount");
            String amount_real	= requestJson.getString("amount_real");
            String reserved = requestJson.getString("reserved");
            String status1	= requestJson.getString("status");
            String status_info = requestJson.getString("status_info");
            
            String sign = requestJson.getString("sign").toUpperCase();
            
            JSONObject parms = new JSONObject();
            parms.put ("query_id",query_id);
            parms.put ("app_id",app_id);
            parms.put ("order_id",order_id);
            parms.put ("amount", amount);
            parms.put ("amount_real", amount_real);
            parms.put ("reserved",reserved);
            parms.put ("status", status1);
            parms.put ("status_info",status_info);
            
            String sourctxt1 = Md5CoreUtil.getSignStrNoNull(parms)+"&key=54RAYrWqwharoxPR2pimdJEMHlP56BjW";
            logger.info("排序后{}",sourctxt1);
            String newSign = Md5Util.md5(sourctxt1.replace("NBSP***", "")).toUpperCase();
            
            logger.info("newSign,sing{},{}",newSign,sign);
            
            int status =0;
            if ("9".equals(status1) && newSign.equals(sign)) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "ok";
            } else {
                logger.error("快包支付回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("快包支付回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        }
    }
}
