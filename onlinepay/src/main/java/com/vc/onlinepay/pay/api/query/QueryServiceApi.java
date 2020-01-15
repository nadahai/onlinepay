/**
 * @类名称:QuickServiceImpl.java
 * @时间:2017年12月21日下午12:29:46
 * @作者:nada
 * @版权:公司 Copyright (c) 2017 
 */
package com.vc.onlinepay.pay.api.query;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonPayService;
import com.vc.onlinepay.persistent.entity.merch.MerchInfo;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.http.HttpBrowserTools;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @描述:统一快捷选择验签业务处理
 * @作者:nada
 * @时间:2017年12月21日 下午12:29:46 
 */
@Service
@Component

public class QueryServiceApi{
    
    public Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private CommonPayService commonPayService;
   
    /**
     * @描述:验证入参
     * @作者:nada
     * @时间:2017年12月19日 上午10:29:39
     */
    public JSONObject checkReqPrms(JSONObject params, HttpServletRequest request) {
        JSONObject result =  new JSONObject();
        try {
            if(!params.containsKey("merchantId") || StringUtil.isEmpty(params.getString("merchantId"))){
                result.put("code", Constant.FAILED);
                result.put("msg", "merchantId参数为空");
                return result;
            }
            if(!params.containsKey("transCode") || StringUtil.isEmpty(params.getString("transCode"))){
                result.put("code", Constant.FAILED);
                result.put("msg", "transCode参数为空");
                return result;
            }
            String transCode = params.getString("transCode");
            String orderNo = "";
            if("002".equals(transCode)){//订单查询
                if(!params.containsKey("orderId") || StringUtil.isEmpty(params.getString("orderId"))){
                    result.put("code", Constant.FAILED);
                    result.put("msg", "orderId参数为空");
                    return result;
                }
                orderNo = params.getString("orderId");
            }else if("007".equals(transCode)){//代付查询
                if(!params.containsKey("orderNo") || StringUtil.isEmpty(params.getString("orderNo"))){
                    result.put("code", Constant.FAILED);
                    result.put("msg", "orderNo参数为空");
                    return result;
                }
                orderNo = params.getString("orderNo");
            }else if("102".equals(transCode)){//财富查询
                logger.info("财富查询");
            }else{
                result.put("code", Constant.FAILED);
                result.put("msg", "transCode参数为空");
                return result;
            }
            if(orderNo.length() > 32){
                result.put("code", Constant.FAILED);
                result.put("msg", "订单号超长!");
                return result;
            }
            MerchInfo merchInfo = commonPayService.getCacheMerchInfo(params.getString("merchantId"));
            if (merchInfo == null){
                logger.error("获取商户信息不存在:{}",params.getString("merchantId"));
                result.put("code", Constant.FAILED);
                result.put("msg",  "商户不存在");
                return result;
            }
            
            String pSign = params.getString("sign");
            String sign = Md5CoreUtil.md5ascii(params, merchInfo.getPassword()).toUpperCase();
            if (!sign.equals(pSign)) {
                logger.error("验签失败下游sign{}平台pSign:{}", sign,pSign);
                result.put("code", Constant.FAILED);
                result.put("msg", "验签失败");
                return result;
            }
            result.put("code", Constant.SUCCESSS);
            params.put("ipaddress", HttpBrowserTools.getIpAddr(request));
            params.put("password",merchInfo.getPassword());
            return result;
        } catch (Exception e) {
            logger.error("下游查询检查异常", e);
            result.put("code", Constant.FAILED);
            result.put("msg", "验证参数失败");
            return result;
        }
    }
}

