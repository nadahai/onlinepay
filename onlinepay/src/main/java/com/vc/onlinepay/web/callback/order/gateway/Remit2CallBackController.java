package com.vc.onlinepay.web.callback.order.gateway;

import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/remittVer2CallBackApi")
public class Remit2CallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:汇付宝2回调接口
     * @时间:2018年1月4日 下午8:04:18
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
            logger.error("汇付宝2回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:执行汇付宝2回调接口
     * @时间:2018年1月5日 上午10:08:05
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        Map<String, String> requestMap = new HashMap<>();
        try {
            requestMap = HttpRequestTools.getRequest(request);
            logger.info("汇付宝2回调接口接收参数:{}", requestMap);
            if (requestMap == null || requestMap.isEmpty() || StringUtils.isEmpty(requestMap.get("merchantOrderNo"))){
                logger.error("汇付宝2回调接口获取参数为空");
                return "error";
            }
            String vcOrderNo = requestMap.get("merchantOrderNo");
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if(null == vcOnlineOrder){
                logger.error("回调接口订单不存在:{}",vcOrderNo);
                return "error";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            if(!checkSign(requestMap,key)){
                logger.error("回调接口验签失败:{}", vcOrderNo);
                return "error";
            }
            int status = 0 ;
            String payStatus = requestMap.get("result");
            if ("1000".equals(payStatus)) {
                status = 4;
            } else if ("1002".equals(payStatus)) {
                status = 5;
            } else {
                status = 6;
            }
            boolean isOk  = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk) {
                return "ok";
            } else {
                logger.error("汇付宝2回调结果{}更新失败{}",isOk,requestMap);
                return "error";
            }
        } catch (Exception e) {
            logger.error("汇付宝2回调接口业务处理异常", e);
            return "error";
        }
    }

    private boolean checkSign(Map<String, String> requestMap,String key){
        try {
            String md5key = Constant.getChannelKeyDes(key,0);

            StringBuffer buffer = new StringBuffer();
//            BigDecimal payAmount = new BigDecimal(requestMap.get("payAmount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);
//            BigDecimal successAmount = new BigDecimal(requestMap.get("successAmount")).setScale(2,BigDecimal.ROUND_HALF_DOWN);
//            if(payAmount.compareTo(successAmount) != 0){
//                return false;
//            }
            buffer.append("merchantId=").append(requestMap.get("merchantId"))
                .append("&merchantOrderNo=").append(requestMap.get("merchantOrderNo"))
                .append("&payAmount=").append(requestMap.get("payAmount"))
                .append("&result=").append(requestMap.get("result"))
                .append("&successAmount=").append(requestMap.get("successAmount"))
                .append("&transNo=").append(requestMap.get("transNo"))
                .append("&version=").append(requestMap.get("version"))
                .append("&key=").append(md5key);
            String sign = Md5Util.md5(buffer.toString());
            if(sign.equals(requestMap.get("sign"))){
                return true;
            }
        } catch (Exception e) {
            logger.error("回调验签失败!:{}",e);
            return false;
        }
        return false;
    }
}
