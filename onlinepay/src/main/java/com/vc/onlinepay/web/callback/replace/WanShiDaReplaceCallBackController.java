package com.vc.onlinepay.web.callback.replace;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ReplaceServiceImpl;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
@RestController
@RequestMapping("/wanshidaReplaceCallbackApi")
public class WanShiDaReplaceCallBackController extends BaseController {

	@Autowired
	private ReplaceServiceImpl commonCallBackServiceImpl;
	@Autowired
	private VcOnlinePaymentServiceImpl vcOnlinePaymentService;

	/**
	 * @描述:万事达代付回调接口
	 */
	@Override
	@RequestMapping(value = "", produces = "text/html;charset=UTF-8")
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		setHttpServletContent(request, response);
		try {
			 logger.info("万事达代付回调接口接开始。");
			String result = this.invokeCallback(request);
			response.getWriter().write(result);
		} catch (Exception e) {
			logger.error("万事达代付回调接口异常", e);
			super.writeErrorResponse(response);
		}
	}

	/**
	 * @描述:万事达代付回调接口
	 */
	public String invokeCallback(HttpServletRequest request) throws IOException {
		Map<String, String> requestMap = new HashMap<> ();
		try {
			requestMap = HttpRequestTools.getRequest(request);
			logger.info("万事达银联回调接口接收参数:{}", requestMap);
			String vcOrderNo = requestMap.get ("order_no");
			String code = requestMap.get("code");
			logger.info("万事达代付回调接口接收参数:{},{}", vcOrderNo);
			if (StringUtils.isAnyEmpty(vcOrderNo,code)) {
				logger.error("万事达代付回调参数解析失败");
				return Constant.res_FAILED;
			}
			VcOnlinePayment onlinePayment = vcOnlinePaymentService.findVcOnlinePaymentByOrderNo(vcOrderNo);
			if(null == onlinePayment){
				logger.error("万事达代付回调接口订单未找到{}", vcOrderNo);
				return Constant.res_FAILED;
			}
			if(onlinePayment.getStatus() != 2){
				logger.error("订单已经处理完毕");
				return "SUCCESS";
			}
			JSONObject reqData = new JSONObject();
			reqData.put("orderNo", vcOrderNo);
			reqData.put("code", Constant.SUCCESSS);
			int statusii = 2;
			if ("200".equals(code)) {
				reqData.put("msg", "代付回调成功");
				statusii = 1;
			}
			JSONObject result = commonCallBackServiceImpl.callBackPayment(vcOrderNo, statusii, reqData);
			if (result != null && result.get("code").equals(Constant.SUCCESSS)) {
				return "SUCCESS";
			}
			return Constant.res_FAILED;
		} catch (Exception e) {
			logger.error("万事达代付回调接口处理异常", e);
			return  Constant.RES_ERROR;
		}
	}
}
