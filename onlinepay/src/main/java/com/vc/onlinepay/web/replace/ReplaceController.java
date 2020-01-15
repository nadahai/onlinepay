package com.vc.onlinepay.web.replace;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.pay.api.replace.ReplaceServiceApi;
import com.vc.onlinepay.persistent.entity.online.VcOnlineWallet;
import com.vc.onlinepay.pay.common.ReplaceServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.web.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外统一代付提现接口
 * @类名称:AmalgamateTransfer.java
 * @时间:2017年12月14日下午3:33:27
 * @作者:nada
 * @版权:公司 Copyright (c) 2017
 */
@Controller
@RestController
@RequestMapping("/agentTransfer")
public class ReplaceController extends BaseController{

    @Autowired
    private ReplaceServiceApi replaceService;
    @Autowired
    private ReplaceServiceImpl replaceServiceImpl;

	/**
    * @描述:代付下单接口入口
    * @时间:2017年6月6日 下午10:24:20
    */
    @Override
	@RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public void doPost(HttpServletRequest request,HttpServletResponse response){
        try {
        	super.setHttpServletContent(request, response);
        	JSONObject result = this.invokeRestOrder(request);
        	logger.info("代付下单响应完毕:{}",result);
        	if(result == null || result.isEmpty()){
        		super.writeResponse(response, Constant.failedMsg("代付接口返回结果为空,请联系运维人员"));
        		return;
        	}
        	super.writeResponse(response,result);
        } catch (Exception e) {
            logger.error("代付下单接口异常", e);
            super.writeResponse(response, Constant.failedMsg("代付接口异常,请联系运维人员"));
    		return;
        }
    }
    
    /**
	 * @描述:调用代付下单业务处理
	 * @时间:2017年6月16日 下午6:19:27
	 */
	public JSONObject invokeRestOrder(HttpServletRequest request) {
		JSONObject result =  new JSONObject();
		JSONObject reqData = null;
		String orderNo = "";
		try {
			reqData = HttpRequestTools.getRequestJson(request);
			
			//第一步：参数验证业务处理
			result = replaceServiceImpl.checkReqPrms(reqData,request);
			orderNo = (null != reqData && reqData.containsKey("orderNo")) ?reqData.getString("orderNo"):"";
			if(!result.getString("code").equals(Constant.SUCCESSS)){
			    logger.error("代付下单单号:{},参验证失败:{}",orderNo,result);
			    return result;
			}
			
			//第二步：通道配置验证业务处理
			String merchantId = reqData.getString("merchantId");
			VcOnlineWallet vcOnlineWallet = payBusService.findVcOnlineWalletBymerchNo(merchantId);
			result = replaceServiceImpl.checkAccount(reqData,vcOnlineWallet);
			if(!result.getString("code").equals(Constant.SUCCESSS)){
				logger.error("代付下单单号:{},账户验证失败:{}",orderNo,result);
                return replaceService.replaceFailedResponse(reqData, result);
            }
			
			//第三步：路由代付通道并设置通道
			result = replaceServiceImpl.replaceRoute(reqData,reqData.getString("mode"));
			if(!result.getString("code").equals(Constant.SUCCESSS)){
				logger.error("代付下单单号:{},通道路由失败:{}",orderNo,result);
				reqData.put("balanceLabel","RouteFail");
				reqData.put("balanceMode", 1);
				reqData.put("replacePoundage","0");
				reqData.put("channelSource", "0");
				reqData.put("channelId", "1");
				reqData.put("channelMerchNo", "1000001");
				reqData.put("channelKeyDes", "");
				reqData.put("channelMerchKey", "1000001");
				reqData.put("failRouteMsg",result.get("msg"));
//				return replaceService.replaceFailedResponse(reqData, result);
			}
			logger.info("接口代付统一接口路由入参:{}",reqData);
            //第四步：代付下单
            result = replaceServiceImpl.persistentReplaceBefore(reqData, vcOnlineWallet);
            if(!result.getString("code").equals(Constant.SUCCESSS)){
				logger.error("代付下单单号:{},下单系统异常（下单保存失败）", orderNo);
                return replaceService.replaceFailedResponse(reqData, result);
            }
			
			//第五步：代付业务处理
			result = replaceService.doRestReplace(reqData,vcOnlineWallet);
			logger.info("代付下单单号:{},下单结束:{}", orderNo,result);
			return  result;
		} catch (Exception e) {
			logger.error("代付下单单号:{},业务处理异常{}",orderNo,reqData, e);
			asynNotice.asynWxMsgNotice("代付报警","代付下单业务处理异常需紧急处理");
			return replaceService.replaceFailedResponse(reqData, result);
		}
	}
}
