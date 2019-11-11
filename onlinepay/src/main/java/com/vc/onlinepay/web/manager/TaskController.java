/**
 * @类名称:MonitorController.java
 * @时间:2018年6月15日下午4:28:16
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.web.manager;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.persistent.common.CommonWalletService;
import com.vc.onlinepay.persistent.common.CoreEngineProviderService;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @描述:TODO
 * @时间:2018年6月15日 下午4:28:16
 */
@Controller
@RestController
@RequestMapping("/task")
@CrossOrigin("*")
public class TaskController extends BaseController {
	 @Autowired
	 private CoreEngineProviderService coreEngineProviderService;
	@Autowired
	private VcOnlineOrderServiceImpl vcOnlineOrderService;
	@Autowired
	private CommonWalletService commonWalletService;
	//自动分润锁
	private static  boolean isLockAutoProfit = false;

	/**
	 * @描述:支付宝企业分润入口
	 * @时间:2018年6月15日 下午4:29:00
	 */
	@Override
	@RequestMapping(value = "/alipayPrifit", produces = "text/html;charset=UTF-8")
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject reqData = HttpRequestTools.getRequestJson(request);
			logger.info("支付宝企业分润入参{}",reqData);
			if(reqData == null || reqData.isEmpty() || !reqData.containsKey("orderNo")){
				super.writeResponse(response, Constant.failedMsg("支付宝企业分润订单号为空"));
			}
			String orderNo = reqData.getString("orderNo");
			VcOnlineOrder vcOnlineOrder = vcOnlineOrderService.findOrderByOrderNo(orderNo);
			if (vcOnlineOrder == null) {
				logger.error("异步更新财富信息订单号信息为空{}", orderNo);
				super.writeResponse(response, Constant.failedMsg("支付宝企业分润失败"));
			}
			JSONObject result = coreEngineProviderService.aliPaySettleProfit(vcOnlineOrder,0,false,true);
			logger.info("企业账号开始归集钱结果:{},{}", vcOnlineOrder.getOrderNo(), result);
			super.writeResponse(response,result);
		} catch (Exception e) {
			logger.error("支付宝企业分润入口异常", e);
			super.writeResponse(response, Constant.failedMsg("支付宝企业分润异常"));
		}
	}

	/**
	 * @描述:支付宝自动处理未分润订单
	 * @时间:2018年6月15日 下午4:29:00
	 */
	@RequestMapping(value = "/autoDoPrifit", produces = "text/html;charset=UTF-8")
	public void autoProfit(HttpServletRequest request, HttpServletResponse response) {
		try {
			if(isLockAutoProfit){
				super.writeResponse(response, Constant.failedMsg("自动分润进程处理中"));
			}else{
				isLockAutoProfit = Boolean.TRUE;
			}
			List<VcOnlineOrder> list = vcOnlineOrderService.findNoPrifitOrderList(null);
			if (list == null || list.size ()< 0 ) {
				super.writeResponse(response, Constant.failedMsg("没有需要分润订单"));
			}
			logger.info ("定时搜索到{}条未分润记录，开始处理",list.size ());
			JSONObject profitResult = new JSONObject();
			for (VcOnlineOrder vcOnlineOrder : list) {
				JSONObject result = coreEngineProviderService.aliPaySettleProfit(vcOnlineOrder,0,false,false);
				profitResult.put ("result",result);
				logger.info ("定时分润结果{}",result);
			}
            isLockAutoProfit = Boolean.FALSE;
			super.writeResponse(response,Constant.successMsg ("分润记录处理完毕"+list.size ()+"result:"+profitResult));
		} catch (Exception e) {
			logger.error("支付宝自动处理未分润订单异常", e);
            isLockAutoProfit = Boolean.FALSE;
			super.writeResponse(response, Constant.failedMsg("支付宝自动处理未分润订单异常"));
		}
	}

	/**
	 * 订单手动结算接口
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/orderPrifit", produces = "text/html;charset=UTF-8")
	public void orderPrifit(HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONObject reqData = HttpRequestTools.getRequestJson(request);
			logger.info("订单手动结算入参{}",reqData);
			if(reqData == null || reqData.isEmpty() || !reqData.containsKey("orderNo")){
				super.writeResponse(response, Constant.failedMsg("订单结算订单号为空"));return;
			}
			String orderNo = reqData.getString("orderNo");
			synchronized (orderNo){
				boolean isOk = commonWalletService.asynUpdateOkOrderWallet(orderNo);
				if(isOk){
					logger.info("订单手动结算成功{}",reqData);
					super.writeResponse(response,Constant.successMsg("订单"+orderNo+"结算成功！"));return;
				}
				logger.info("订单手动结算失败{}",reqData);
			}
			super.writeResponse(response,Constant.successMsg("订单"+orderNo+"结算失败！"));
		} catch (Exception e) {
			logger.error("订单结算入口异常", e);
			super.writeResponse(response, Constant.failedMsg("订单结算异常"));
		}
	}
}
