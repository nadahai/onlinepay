package com.vc.onlinepay.web.query;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.pay.api.query.QueryServiceApi;
import com.vc.onlinepay.persistent.entity.online.VcOnlineWallet;
import com.vc.onlinepay.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 财富查询
 * 
 * @类名称:AmalgamatePayQueryApi.java
 * @时间:2017年12月27日下午2:20:35
 * @作者:nada
 * @版权:公司 Copyright (c) 2017
 */
@Controller
@RestController
@RequestMapping("/walletQuery")
public class WalletQueryController extends BaseController {

    @Autowired
    private QueryServiceApi queryService;

    /**
     * @描述:统一财富查询接口入口
     * @作者:nada
     * @时间:2017年12月27日 下午4:38:26
     */
    @Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        setHttpServletContent(request, response);
        try {
            JSONObject result = this.invoke(request);
            response.getWriter().write(new Gson().toJson(result));
        } catch (Exception e) {
            logger.error("统一财富查询接口入口异常", e);
            super.writeErrorResponse(response);
        }
    }

    /**
     * @描述:统一财富查询接口业务处理
     * @作者:nada
     * @时间:2017年12月27日 下午4:25:18
     */
    public JSONObject invoke(HttpServletRequest request) throws IOException {
        JSONObject result = new JSONObject();
        try {
            JSONObject params = HttpRequestTools.getRequestJson(request);
            JSONObject checkResult = queryService.checkReqPrms(params, request);
            if (!checkResult.getString("code").equals(Constant.SUCCESSS)) {
                logger.error("下游查询接口业务参验证失败:{}", checkResult);
                return checkResult;
            }
            String merchantId = params.getString("merchantId");
            VcOnlineWallet onlineWallet = payBusService.findVcOnlineWalletBymerchNo(merchantId);
            if (onlineWallet == null) {
                result.put("code", Constant.FAILED);
                result.put("msg", "商户不存在！");
                return result;
            }
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "查询成功");
            String mode = params.getString("mode");
            if ("T0".equals(mode)) {
                result.put("amount", onlineWallet.getD0UsableAmount().setScale(2,BigDecimal.ROUND_HALF_DOWN));
            } else if ("T1".equals(mode)) {
                result.put("amount", onlineWallet.getUsableTotalAmount().setScale(2,BigDecimal.ROUND_HALF_DOWN));
            }else{
                result.put("amount", onlineWallet.getD0UsableAmount().add (onlineWallet.getUsableTotalAmount()).setScale(2,BigDecimal.ROUND_HALF_DOWN));
            }
            result.put("sign", Md5CoreUtil.md5ascii(result, params.getString("password")).toUpperCase());
            return result;
        } catch (Exception e) {
            logger.error("监听到查询结果处理异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "系统异常");
            return result;
        }

    }

}
