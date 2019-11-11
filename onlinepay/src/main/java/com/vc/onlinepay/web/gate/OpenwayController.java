package com.vc.onlinepay.web.gate;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cmd.CommonCmd;
import com.vc.onlinepay.cmd.TradeCmd;
import com.vc.onlinepay.cmd.TransferCmd;
import com.vc.onlinepay.enums.MethodCodeEnum;
import com.vc.onlinepay.enums.PayCommandEnum;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.gateutils.GateRequest;
import com.vc.onlinepay.utils.gateutils.GateResponse;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 报文网关入口 包含： 1,交易网关入口 2，交易网关业务处理 3，交易网关响应
 * @author daniel
 */
@Controller
@RestController
@CrossOrigin ("*")
@RequestMapping ("/xopenapi")
public class OpenwayController {

    private static final Logger logger = LoggerFactory.getLogger (OpenwayController.class);
    @Autowired
    private CommonCmd commonCmd;
    @Autowired
    private TradeCmd tradeCmd;
    @Autowired
    private TransferCmd transferCmd;

    /**
     * @描述: 1，交易网关入口
     * @时间:2019/3/15
     */
    @RequestMapping (value = "", produces = "text/html;charset=UTF-8")
    public ModelAndView doGateway (HttpServletRequest request, HttpServletResponse response) {
        try {
            GateRequest.initHttpServletRequest (request, response);
            JSONObject reqData = HttpRequestTools.getRequestJson (request);
            JSONObject result = this.balanceGateway (request, reqData);
            return this.doTransResult (response, reqData, result);
        } catch (Exception e) {
            logger.error ("交易网关异常", e);
            return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("交易网关异常"));
        }
    }

    /***
     * @描述: 2，交易网关分发
     * @时间:2019/3/15
     **/
    private JSONObject balanceGateway (HttpServletRequest request, JSONObject reqData) {
        JSONObject result;
        try {
            result = commonCmd.checkCommonReqData (reqData);
            if (!Constant.isOkResult (result)) {
                return commonCmd.bulidResponseData (reqData, result);
            }
            tradeCmd.addNetReqPrms (request, reqData);
            MethodCodeEnum method = MethodCodeEnum.getEnum (reqData.getString ("reqCmd"));
            switch (method) {
                case trade:
                    result = tradeCmd.doAllTransOrder (reqData);
                    return commonCmd.bulidResponseData (reqData, result);
                case tradeQuery:
                    return commonCmd.bulidResponseData (reqData, result);
                default:
                    return GateResponse.BuildFailedResult ("网关业务分发错误" + method);
            }
        } catch (Exception e) {
            logger.error ("交易网关分发异常", e);
            return GateResponse.BuildFailedResult ("交易业务处理异常");
        }
    }

    /**
     * @描述:3，交易网关响应
     * @时间:2019/3/15
     **/
    public ModelAndView doTransResult (HttpServletResponse response, JSONObject reqData, JSONObject result) {
        try {
            logger.info ("交易网关响应结果:{},reqData:{}", result, reqData);
            if (!Constant.isOkResult (result)) {
                return GateResponse.writeResponse (response, result);
            }
            MethodCodeEnum method = MethodCodeEnum.getEnum (reqData.getString ("reqCmd"));
            switch (method) {
                case transfer: case tradeQuery: case transferQuery:case walletQuery:
                    return GateResponse.writeResponse (response, result);
            }
            int payType = reqData.containsKey ("payType") ? reqData.getIntValue ("payType") : 0;
            if(payType < 1){
                return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("未知交易类型"+payType));
            }
            PayCommandEnum payTypeEnum = PayCommandEnum.getPayTypeEnum (payType);
            switch (payTypeEnum) {
                case scan:
                    return commonResult (response, result);
                case h5:
                    return commonResult (response, result);
                default:
                    return GateResponse.writeResponse (response, result);
            }
        } catch (Exception e) {
            logger.info ("交易网关响应结果异常:{},reqData:{}", result, reqData, e);
            return GateResponse.writeResponse (response, GateResponse.buildExceptionResult ("响应异常"));
        }
    }

    /**
     * @描述:公共响应结果
     **/
    public ModelAndView commonResult (HttpServletResponse response, JSONObject result) {
        String bankUrl = result.containsKey ("bankUrl") ? result.getString ("bankUrl") : "";
        String redirectUrl = result.containsKey ("redirectUrl") ? result.getString ("redirectUrl") : "";
        if (StringUtil.isNotEmpty (bankUrl)) {
            return GateResponse.writeResponse (response, result);
        }
        if (StringUtil.isNotEmpty (redirectUrl)) {
            return GateResponse.writeRedirect (response, redirectUrl);
        }
        return GateResponse.writeResponse (response, Constant.failedMsg (result.containsKey ("msg") ? result.getString ("msg") : "交易失败"));
    }
}
