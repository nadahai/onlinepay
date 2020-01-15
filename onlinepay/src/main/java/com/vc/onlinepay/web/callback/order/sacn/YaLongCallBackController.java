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
import cn.hutool.json.XML;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nada
 * @描述:亚龙支付回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/yaLongCallBackController")
public class YaLongCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:亚龙支付回调处理
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
            logger.error("亚龙支付回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:友付回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
    	try {
            JSONObject requestJson = HttpRequestTools.getRequestJson(request);
            logger.info("亚龙支付回调接口接收参数2:{}", requestJson);

            if (requestJson == null || requestJson.isEmpty()){
                logger.error("亚龙支付回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            String vcOrderNo = requestJson.getString("OrderID");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("亚龙支付回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            String MerID = requestJson.getString("MerID");
            String OrderID = requestJson.getString("OrderID");
            String State = requestJson.getString("State");
            String Money = requestJson.getString("Money");
            String SysOrderID = requestJson.getString("SysOrderID");
            
            Map<String, String> parms = new HashMap<String, String>();
            parms.put ("MerID",MerID);
            parms.put ("OrderID",OrderID);
            parms.put ("State",State);
            parms.put ("Money", Money);
            parms.put ("SysOrderID", SysOrderID);
            
            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&Key=HLEQdKIW90rCWkj49DVw1ndsi4gT2aBKR2hBHiI9geBrxwjkFM";
            logger.info("排序后{}",sourctxt1);
            
            String newSign = Md5Util.md5(sourctxt1).toUpperCase();
            String sign = requestJson.getString("Sign").toUpperCase();
            
            int status =0;
            if ("1".equals(requestJson.getString("State")) && newSign.equals(sign)) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "ok";
            } else {
                logger.error("亚龙支付回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("亚龙支付回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        }
    }
}
