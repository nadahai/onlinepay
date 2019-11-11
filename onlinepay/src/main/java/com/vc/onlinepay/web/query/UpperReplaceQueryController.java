package com.vc.onlinepay.web.query;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vc.onlinepay.pay.api.query.UpperReplaceQueryServiceApi;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 上游代付订单查询接口
 * 
 * @类名称:UpperAccountQueryApi.java
 * @时间:2018年1月4日上午11:36:15
 * @作者:lihai
 * @版权:公司 Copyright (c) 2018
 */
@Controller
@RestController
@RequestMapping("/onlineReplaceQueryApi")
public class UpperReplaceQueryController extends BaseController {

    @Autowired
    private UpperReplaceQueryServiceApi upperReplaceQueryService;
    @Autowired
    private VcOnlinePaymentServiceImpl onlinePaymentService;
    @Autowired
    private CoreEngineProviderService coreEngineService;

    /**
     * @描述:上游代付订单查询接口入口
     * @作者:lihai
     * @时间:2017年6月6日 下午10:24:20
     */
    @Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        setHttpServletContent(request, response);
        try {
            JSONObject params = HttpRequestTools.getRequestJson(request);
            JSONObject result = this.invokeRestOrder(params);
            logger.info("上游代付订单查询完毕:{}", result);
            response.getWriter().write(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("上游代付订单查询接口异常", e);
            super.writeErrorResponse(response);
        }
    }
    /**
     * @描述:查询上游订单并且更新状态
     * @作者:ChaiJing THINK
     * @时间:2018/5/15 14:50
     */
    @RequestMapping(value = "queryAndUpdate", produces = "text/html;charset=UTF-8")
    public ModelAndView doUpdatePost(HttpServletRequest request, HttpServletResponse response){
        setHttpServletContent(request, response);
        try {
            JSONObject params = HttpRequestTools.getRequestJson(request);
            if(params == null || !params.containsKey ("orderNo")){
                return super.writeResponse(response, Constant.successMsg("请求订单号参数为空"));
            }
            String sources = coreEngineService.getCacheCfgKey ("online.replace.query.channels");
            if(StringUtil.isEmpty (sources) || sources.split (",") == null){
                return super.writeResponse(response, Constant.successMsg("代付查询通道为空"));
            }
            List<Long> list = new ArrayList<> ();
            String[] source = sources.split (",");
            for (String s: source) {
                list.add(Long.valueOf (s));
            }
            String orderNo = params.getString("orderNo");
            List<VcOnlinePayment> paymentList = onlinePaymentService.findPaddingOrder(orderNo,list);
            if(paymentList == null || paymentList.size()<1){
                return super.writeResponse(response, Constant.successMsg("未找到代付代付订单"));
            }
            ThreadUtil.execute(() -> {
                for(VcOnlinePayment onlinePayment : paymentList){
                    JSONObject reqData = new JSONObject();
                    reqData.put("update",true);
                    reqData.put("isMemo","isMemo");
                    reqData.put("orderNo",onlinePayment.getOrderNo());
                    JSONObject result = upperReplaceQueryService.checkPaymentOrder(reqData, onlinePayment);
                    logger.info("查询上游代付订单入参:{},结果:{}",reqData,result);
                    if (!result.getString("code").equals(Constant.SUCCESSS)) {
                        continue;
                    }
                    JSONObject res = upperReplaceQueryService.doRestReplaceQuery(reqData, onlinePayment);
                    logger.info("上游代付订单查询完毕:{}",res);
                }
            });
            return super.writeResponse(response, Constant.successMsg("查询代付更新已提交"));
        } catch (Exception e) {
             logger.error("上游代付订单查询接口异常", e);
             super.writeErrorResponse(response);
             return null;
        }
    }


    /**
     * @描述:调用上游代付订单查询订单业务处理
     * @作者:lihai
     * @时间:2017年6月16日 下午6:19:27
     */
    public JSONObject invokeRestOrder(JSONObject params) {
        JSONObject result = new JSONObject();
        try {
            logger.info("上游代付订单查询请求入参:{}", params);

            result = upperReplaceQueryService.checkReqPrms(params, null);
            if (!result.getString("code").equals(Constant.SUCCESSS)) {
                logger.error("上游代付订单查询请求入参验证失败:{}", result);
                return result;
            }

            String orderNo = params.getString("orderNo");
            VcOnlinePayment onlinePayment = payBusService.findVcOnlinePaymentByOrderNo(orderNo);
            result = upperReplaceQueryService.checkPaymentOrder(params, onlinePayment);
            if (!result.getString("code").equals(Constant.SUCCESSS)) {
                logger.error("代付订单参验证失败:{}", result);
                return result;
            }
            return upperReplaceQueryService.doRestReplaceQuery(params, onlinePayment);
        } catch (Exception e) {
            logger.error("上游代付订单查询请求异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "上游代付订单查询失败.");
            return result;
        }
    }
}
