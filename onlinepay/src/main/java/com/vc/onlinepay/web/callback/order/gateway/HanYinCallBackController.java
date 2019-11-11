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
@RequestMapping("/hanyingPayCallBackApi")
public class HanYinCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:瀚银回调接口
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
            logger.error("瀚银回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:执行瀚银回调接口
     * @时间:2018年1月5日 上午10:08:05
     */
    public String invokeCallback(HttpServletRequest request) throws IOException {
        VcOnlineOrder vcOnlineOrder = null;
        Map<String, String> requestMap = new HashMap<>();
        try {
//{actualAmount=5087, attach=, hpMerCode=WCJRWDTFB2C@20181025150421, orderNo=gw20181030165626140526, signature=3A7A874BD136728EBF947A26CFCBF94F, statusCode=00, statusMsg=[00]交易成功, transAmount=5100, transDate=20181030165631, transSeq=605463185, transStatus=00, transVoucher=}
            requestMap = HttpRequestTools.getRequest(request);
            logger.info("瀚银回调接口接收参数:{}", requestMap);
            if (requestMap == null || requestMap.isEmpty() || StringUtils.isEmpty(requestMap.get("orderNo"))){
                logger.error("瀚银回调接口获取参数为空");
                return "error";
            }
            String vcOrderNo = requestMap.get("orderNo");
            vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (null == vcOnlineOrder) {
                logger.error("瀚银回调接口订单不存在:{}", vcOrderNo);
                return "error";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(vcOnlineOrder.getUpMerchKey());
            if(!checkSign(requestMap,key)){
                logger.error("瀚银回调接口验签失败:{}", vcOrderNo);
                return "error";
            }
            String payStatus = requestMap.get("transStatus");
            boolean isfailOrder = false;
            int status = 0 ;
            if ("00".equals(payStatus)) {
                status = 4;
            } else if ("01".equals(payStatus)||"03".equals(payStatus)) {
                status = 5;
                //瀚银回调支付失败,接受一次回调,不论下游是否处理成功
                logger.error("瀚银回调支付失败,接受一次回调订单:{}", vcOrderNo);
                isfailOrder = true;
            } else {
                status = 6;
            }
            boolean isOk  = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestMap.toString());
            if (isOk || isfailOrder) {
                return "success";
            } else {
                logger.error("瀚银回调结果{}更新失败{}",isOk,requestMap);
                return "error";
            }
        } catch (Exception e) {
            logger.error("瀚银回调接口业务处理异常", e);
            return "error";
        }
    }

    private boolean checkSign(Map<String, String> requestMap, String key) {
        try {
            String signKey = Constant.getChannelKeyDes(key,2);
            StringBuffer buffer = new StringBuffer();
            buffer.append(requestMap.get("hpMerCode")==null?"":requestMap.get("hpMerCode"))
                    .append("|").append(requestMap.get("orderNo")==null?"":requestMap.get("orderNo"))
                    .append("|").append(requestMap.get("transDate")==null?"":requestMap.get("transDate"))
                    .append("|").append(requestMap.get("transStatus")==null?"":requestMap.get("transStatus"))
                    .append("|").append(requestMap.get("transAmount")==null?"":requestMap.get("transAmount"))
                    .append("|").append(requestMap.get("actualAmount")==null?"":requestMap.get("actualAmount"))
                    .append("|").append(requestMap.get("transSeq")==null?"":requestMap.get("transSeq"))
                    .append("|").append(requestMap.get("statusCode")==null?"":requestMap.get("statusCode"))
                    .append("|").append(requestMap.get("statusMsg")==null?"":requestMap.get("statusMsg"))
                    .append("|").append(signKey);
            String sign = Md5Util.MD5(buffer.toString());
            if(sign.equals(requestMap.get("signature"))){
                return true;
            }
        } catch (Exception e) {
            logger.error("回调验签失败!:{}",e);
            return false;
        }
        return false;
    }
}
