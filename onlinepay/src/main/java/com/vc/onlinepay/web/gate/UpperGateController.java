package com.vc.onlinepay.web.gate;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cmd.CommonCmd;
import com.vc.onlinepay.enums.MethodCodeEnum;
import com.vc.onlinepay.pay.api.query.UpperAccountServiceApi;
import com.vc.onlinepay.pay.api.query.UpperOrderQueryServiceApi;
import com.vc.onlinepay.pay.api.query.UpperReplaceQueryServiceApi;
import com.vc.onlinepay.persistent.common.CommonBusService;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.gateutils.GateRequest;
import com.vc.onlinepay.utils.gateutils.GateResponse;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @描述:上游网关接口
 * @作者:nada
 * @时间:2019/3/18
 **/
@Controller
@RestController
@RequestMapping ("/upxpayapi")
public class UpperGateController {

    private static final Logger logger = LoggerFactory.getLogger (UpperGateController.class);

    @Autowired
    private UpperReplaceQueryServiceApi upperReplaceQueryService;
    @Autowired
    private VcOnlinePaymentServiceImpl onlinePaymentService;
    @Autowired
    private CommonCmd commonServiceImpl;
    @Autowired
    private CommonBusService commonBusService;
    @Autowired
    private UpperAccountServiceApi upperAccountService;
    @Autowired
    private UpperOrderQueryServiceApi upperOrderService;
    @Autowired
    private CoreEngineProviderService coreEngineService;

    /**
     * @描述:上游业务网关入口
     * @作者:nada
     * @时间:2019/3/18
     **/
    @RequestMapping (value = "", produces = "text/html;charset=UTF-8")
    public ModelAndView doUpperGateway (HttpServletRequest request, HttpServletResponse response) {
        GateRequest.initHttpServletRequest (request, response);
        try {
            GateRequest.initHttpServletRequest (request, response);
            JSONObject result = this.balanceGateway (request);
            return GateResponse.writeResponse (response, result);
        } catch (Exception e) {
            logger.error ("上游代付订单查询接口异常", e);
            return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("交易网关异常"));
        }
    }

    /***
     * @描述:上游业务网关分发
     * @时间:2019/3/15
     **/
    private JSONObject balanceGateway (HttpServletRequest request) {
        JSONObject result;
        try {
            JSONObject reqData = HttpRequestTools.getRequestJson (request);
            result = commonServiceImpl.checkCommonReqData (reqData);
            if (!Constant.isOkResult (result)) {
                return result;
            }
            MethodCodeEnum method = MethodCodeEnum.getEnum (reqData.getString ("reqCmd"));
            switch (method) {
                case upperWalletQuery:
                    return queryUpperAccount (reqData);
                case upperTradeQuery:
                    return queryUpperOrder (reqData);
                case upperTransferQuery:
                    return queryAndUpdateTransOrder (reqData);
                default:
                    return GateResponse.BuildFailedResult ("网关业务分发错误" + method);
            }
        } catch (Exception e) {
            logger.error ("交易网关分发异常", e);
            return GateResponse.BuildFailedResult ("交易业务处理异常");
        }
    }

    /**
     * @描述:调用上游交易订单查询订单业务处理
     * @作者:Alan
     * @时间:2017年6月16日 下午6:19:27
     */
    public JSONObject queryUpperOrder (JSONObject reqData) {
        JSONObject result = new JSONObject ();
        try {
            result = upperOrderService.checkReqPrms (reqData);
            if (!result.getString ("code").equals (Constant.SUCCESSS)) {
                logger.error ("交易订单查询请求入参验证失败:{}", result);
                return result;
            }
            String orderNo = reqData.getString ("orderNo");
            VcOnlineOrder onlineOrder = commonBusService.getVcOrderByorderNo (orderNo);
            result = upperOrderService.checkOrder (reqData, onlineOrder);
            if (!result.getString ("code").equals (Constant.SUCCESSS)) {
                logger.error ("交易订单参验证失败:{}", result);
                return result;
            }
            return upperOrderService.doRestOrderQuery (reqData, onlineOrder);
        } catch (Exception e) {
            logger.error ("下游上游交易订单查询请求异常", e);
            result.put ("code", Constant.FAILED);
            result.put ("msg", "上游交易订单查询失败.");
            return result;
        }
    }

    /**
     * @描述:调用上游余额查询订单业务处理
     * @时间:2017年6月16日 下午6:19:27
     */
    public JSONObject queryUpperAccount (JSONObject reqData) {
        JSONObject result = new JSONObject ();
        try {
            ThreadUtil.execute (() -> upperAccountService.doUpperQuery (reqData));
            return Constant.successMsg ("提交成功！");
        } catch (Exception e) {
            logger.error ("下游上游余额查询请求异常", e);
            return Constant.failedMsg ("上游余额查询失败.");
        }
    }

    /**
     * @描述:查询上游并且更新
     * @作者:ChaiJing THINK
     * @时间:2018/5/15 14:50
     */
    public JSONObject queryAndUpdateTransOrder (JSONObject reqData) {
        try {
            final String orderNo = reqData == null || reqData.isEmpty () || reqData.get ("orderNo") == null ? "" : reqData.getString ("orderNo");
            String sources = coreEngineService.getCacheCfgKey ("online.replace.query.channels");
            if(StringUtil.isEmpty (sources) || sources.split (",") == null){
                return GateResponse.BuildFailedResult ("代付查询通道为空");
            }
            List<Long> list = new ArrayList<> ();
            String[] source = sources.split (",");
            for (String s: source) {
                list.add(Long.valueOf (s));
            }
            List<VcOnlinePayment> paymentList = onlinePaymentService.findPaddingOrder (orderNo, list);
            if (paymentList == null || paymentList.size () < 1) {
                logger.error ("未找到代付中订单代付订单");
                return GateResponse.BuildFailedResult ("未找到代付中订单代付订单");
            }
            ThreadUtil.execute (() -> {
                for (VcOnlinePayment onlinePayment : paymentList) {
                    reqData.put ("update", true);
                    reqData.put ("isMemo", "isMemo");
                    reqData.put ("orderNo", onlinePayment.getOrderNo ());
                    logger.info ("上游代付订单查询入参:{}", reqData);
                    JSONObject result = upperReplaceQueryService.checkPaymentOrder (reqData, onlinePayment);
                    if (!result.getString ("code").equals (Constant.SUCCESSS)) {
                        logger.error ("代付订单参验证失败:{}", result);
                        continue;
                    }
                    JSONObject res = upperReplaceQueryService.doRestReplaceQuery (reqData, onlinePayment);
                    logger.info ("上游代付订单查询完毕:{}", res);
                }
            });
            return Constant.successMsg ("异步代付订单更新提交成功");
        } catch (Exception e) {
            logger.error ("上游代付订单查询接口异常", e);
            return GateResponse.BuildFailedResult ("交易业务处理异常");
        }
    }


    /**
     * @描述:查询上游并且更新
     * @作者:ChaiJing THINK
     * @时间:2018/5/15 14:50
     */
    @RequestMapping (value = "queryAndUpdate", produces = "text/html;charset=UTF-8")
    public ModelAndView doUpdatePost (HttpServletRequest request, HttpServletResponse response) {
        GateRequest.initHttpServletRequest (request, response);
        try {
            JSONObject params = HttpRequestTools.getRequestJson (request);
            final String orderNo = params == null || params.isEmpty () || params.get ("orderNo") == null ? "" : params.getString ("orderNo");
            final String searchType = params == null || params.isEmpty () || params.get ("type") == null ? "" : params.getString ("type");
            List<Long> list = null;
            if (StringUtils.isEmpty (orderNo)) {
                list = new ArrayList<> ();
                //微付宝
                list.add (50L);
                list.add (64L);
                list.add (79L);
            }
            List<VcOnlineOrder> orderList = upperOrderService.findPaddingOrder (orderNo, list, searchType);
            if (orderList == null || orderList.size () < 1) {
                logger.error ("未找到中间态订单");
                return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("未找到中间态订单"));
            }
            ThreadUtil.execute (() -> {
                JSONObject reqData = new JSONObject ();
                reqData.put ("update", true);
                reqData.put ("isMemo", "isMemo");
                for (VcOnlineOrder onlineorder : orderList) {
                    reqData.put ("orderNo", onlineorder.getOrderNo ());
                    logger.info ("上游订单查询入参:{}", reqData);
                    JSONObject result = upperOrderService.checkOrder (reqData, onlineorder);
                    if (!result.getString ("code").equals (Constant.SUCCESSS)) {
                        logger.error ("交易订单参验证失败:{}", result);
                        continue;
                    }
                    JSONObject res = upperOrderService.doRestOrderQuery (reqData, onlineorder);
                    logger.info ("上游订单查询完毕:{}", res);
                }
            });
            return GateResponse.writeResponse (response, Constant.successMsg ("异步交易订单更新提交成功"));
        } catch (Exception e) {
            logger.error ("", e);
            return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("上游代付订单查询接口异常"));
        }
    }
}
