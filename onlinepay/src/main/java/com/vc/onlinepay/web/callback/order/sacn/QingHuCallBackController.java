package com.vc.onlinepay.web.callback.order.sacn;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonCallBackService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
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
 * @author jauyang
 * @描述:清湖支付宝支付回调处理
 * @时间:2018/12/26 15:23
 */
@RestController
@RequestMapping("/qingHuCallBackController")
public class QingHuCallBackController extends BaseController {
    
    @Autowired
    private CommonCallBackService commonCallBackServiceImpl;
    
    /**
     * @描述:清湖支付宝支付回调处理
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
            logger.error("清湖支付宝支付回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:友付回调接口
     * @时间:2018年12月26日 下午15:24:18
     */
    public String invokeCallback(HttpServletRequest request) {
    	try {
    		logger.info("清湖支付宝支付回调接口接收参数2:{}", request.getParameter("data"));
    		//String req = HttpRequestTools.getFormDataRequest(request.getParameter("data"));
            //logger.info("清湖支付宝支付回调接口接收参数2:{}", req);
            
            if (request.getParameter("data") == null || request.getParameter("data").isEmpty()){
                logger.error("清湖支付宝支付回调接口获取参数为空");
                return Constant.res_FAILED;
            }
            JSONObject requestJson = JSONObject.parseObject(request.getParameter("data"));
            String vcOrderNo = requestJson.getString("lsh");
            VcOnlineOrder vcOnlineOrder = payBusService.getVcOrderByorderNo(vcOrderNo);
            if (StringUtil.isEmpty(vcOrderNo) || vcOnlineOrder==null) {
                logger.error("清湖支付宝支付回调接口订单号为空{}", vcOrderNo);
                return Constant.res_FAILED;
            }
            
            String lsh = requestJson.getString("lsh");
            String money = requestJson.getString("money");
            String stud = requestJson.getString("stud");
            String sign = requestJson.getString("ch").toUpperCase();
            
            
            String sourctxt1 = lsh+money+stud+"EN2CT6W2KPBPFQRMT4D8";
            logger.info("排序后{}",sourctxt1);
            
            String newSign = Md5Util.md5(sourctxt1).toUpperCase();
            
            int status =0;
            if ("2".equals(requestJson.getString("stud")) && newSign.equals(sign)) {
                status = 4;
            }else {
                status = 3;
            }
            boolean isOK = commonCallBackServiceImpl.callBackOrder(vcOnlineOrder, status,requestJson.toString());
            if (isOK) {
                return "ok";
            } else {
                logger.error("清湖支付宝支付回调接口更新失败{}", requestJson);
                return Constant.res_FAILED;
            }
        } catch (Exception e) {
            logger.error("清湖支付宝支付回调接口业务处理异常", e);
            return Constant.RES_ERROR;
        }
    }
}
