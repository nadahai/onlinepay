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
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nada
 * @描述:乐乐h5回调
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/lLH5CallBackController")
public class LLH5CallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:乐乐h5回调
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
            logger.error("乐乐h5回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:迅捷回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
        try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("乐乐h5回调接口接收参数:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("乐乐h5回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestJson.getString("ddh");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("乐乐h5回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            String status1 = requestJson.getString("status");
            String shid = requestJson.getString("shid");
            String ddh = requestJson.getString("ddh");
            String je = requestJson.getString("je");
            String bb = requestJson.getString("bb");
            String zftd = requestJson.getString("zftd");
            String ybtz = requestJson.getString("ybtz");
            String tbtz = requestJson.getString("tbtz");
            String ddmc = requestJson.getString("ddmc");
            String ddbz = requestJson.getString("ddbz");
            String sign = requestJson.getString("sign");
            

            String signStr = "status="+status1+"&shid="+shid+"&bb="+bb
            		+ "&zftd="+zftd+"&ddh="+ddh+"&je="+je+"&ddmc="+ddmc
            		+"&ddbz="+ddbz+"&ybtz="+ybtz+"&tbtz="+tbtz+"&by7jz4MTOO4ArkO5tDCaVqIfHC7ybxOYtTsHWi5b";
            
            String reqSign =DigestUtils.md5DigestAsHex(signStr.getBytes()).toUpperCase();
            
            int status =0;
            if (reqSign.equalsIgnoreCase(sign.toUpperCase())) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "success";
            } else {
                logger.error("乐乐h5回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("乐乐h5回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        } 
    }
}
