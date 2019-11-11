package com.vc.onlinepay.web.query;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.pay.api.query.UpperOrderQueryServiceApi;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.web.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 上游交易订单查询接口
 * 
 * @类名称:UpperOrderQueryApi.java
 * @时间:2018年1月17日下午2:52:13
 * @作者:lihai
 * @版权:公司 Copyright (c) 2018
 */
@Controller
@RestController
@RequestMapping("/onlineOrderQueryApi")
public class UpperOrderQueryController extends BaseController {

    @Autowired
    private UpperOrderQueryServiceApi upperOrderService;

    /**
     * @描述:上游交易订单查询接口入口
     * @作者:Alan
     * @时间:2017年6月6日 下午10:24:20
     */
    @Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        setHttpServletContent(request, response);
        try {
            JSONObject result = this.invokeRestOrder(request);
            logger.info("下游上游交易订单查询完毕:{}", result);
            response.getWriter().write(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("下游上游交易订单查询接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:查询上游并且更新
     * @作者:ChaiJing THINK
     * @时间:2018/5/15 14:50
     */
    @RequestMapping(value = "queryAndUpdate", produces = "text/html;charset=UTF-8")
    public void doUpdatePost(HttpServletRequest request, HttpServletResponse response){
        setHttpServletContent(request, response);
        try {
            JSONObject params = HttpRequestTools.getRequestJson(request);
            final String orderNo = params==null||params.isEmpty()||params.get("orderNo")==null?"":params.getString("orderNo");
            final String searchType = params==null||params.isEmpty()||params.get("type")==null?"":params.getString("type");
            List<Long> list = null;
            if(StringUtils.isEmpty(orderNo)){
                list = new ArrayList<>();
                list.add(27L);
                list.add(111L);
            }
            List<VcOnlineOrder> orderList  = upperOrderService.findPaddingOrder(orderNo,list,searchType);
            if(orderList == null||orderList.size() < 1){
                logger.error("未找到中间态订单");
                super.writeResponse(response, Constant.successMsg("未找到中间态订单"));
                return;
            }
            ThreadUtil.execute(() -> {
                JSONObject reqData = new JSONObject();
                reqData.put("update",true);
                reqData.put("isMemo","isMemo");
                for(VcOnlineOrder onlineorder:orderList){
                    reqData.put("orderNo",onlineorder.getOrderNo());
                    logger.info("上游订单查询入参:{}",reqData);
                    JSONObject result = upperOrderService.checkOrder(reqData, onlineorder);
                    if (!result.getString("code").equals(Constant.SUCCESSS)) {
                        logger.error("交易订单参验证失败:{}", result);
                        continue;
                    }
                    JSONObject res = upperOrderService.doRestOrderQuery(reqData, onlineorder);
                    logger.info("上游订单查询完毕:{}",res);
                }
            });
           super.writeResponse(response, Constant.successMsg("异步交易订单更新提交成功"));
        } catch (Exception e) {
            logger.error("上游代付订单查询接口异常", e);
            super.writeErrorResponse(response);
        }

    }

    /**
     * @描述:调用上游交易订单查询订单业务处理
     * @作者:Alan
     * @时间:2017年6月16日 下午6:19:27
     */
    public JSONObject invokeRestOrder(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        try {
            JSONObject params = HttpRequestTools.getRequestJson(request);
            logger.info("下游上游交易订单查询请求入参:{}", params);

            result = upperOrderService.checkReqPrms(params);
            if (!result.getString("code").equals(Constant.SUCCESSS)) {
                logger.error("交易订单查询请求入参验证失败:{}", result);
                return result;
            }

            String orderNo = params.getString("orderNo");
            VcOnlineOrder onlineOrder = payBusService.getVcOrderByorderNo(orderNo);
            result = upperOrderService.checkOrder(params, onlineOrder);
            if (!result.getString("code").equals(Constant.SUCCESSS)) {
                logger.error("交易订单参验证失败:{}", result);
                return result;
            }
            return upperOrderService.doRestOrderQuery(params, onlineOrder);
        } catch (Exception e) {
            logger.error("下游上游交易订单查询请求异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "上游交易订单查询失败.");
            return result;
        }
    }

}
