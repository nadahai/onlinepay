package com.vc.onlinepay.web.callback.replace;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ReplaceServiceImpl;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;


@Controller
@RestController
@RequestMapping("/kuaiBaoReplaceCallBackController")
public class KuaiBaoReplaceCallBackController extends BaseController {

	@Autowired
	private ReplaceServiceImpl commonCallBackServiceImpl;
	@Autowired
	private VcOnlinePaymentServiceImpl vcOnlinePaymentService;

	/**
	 * @描述:快包代付回调接口
	 */
	@Override
	@RequestMapping(value = "", produces = "text/html;charset=UTF-8")
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		setHttpServletContent(request, response);
		try {
			 logger.info("快包代付回调接口接开始。。");
			String result = this.invokeCallback(request);
			response.getWriter().write(result);
		} catch (Exception e) {
			logger.error("快包代付回调接口异常", e);
			super.writeErrorResponse(response);
		}
	}

	/**
	 * @描述:快包代付回调接口
	 */
	public String invokeCallback(HttpServletRequest request) throws IOException {
		try {
			JSONObject jsonObject = HttpRequestTools.getRequestJson(request);
			logger.info("快包代付回调接收参数:{}", jsonObject);
			if (jsonObject == null || jsonObject.isEmpty()) {
				logger.error("快包代付回调参数为空");
				return "FAILED";
			}
			String vcOrderNo = jsonObject.getString("order_id");
			logger.info("快包代付回调接口接收参数:{},{}", vcOrderNo);
			if (StringUtils.isAnyEmpty(vcOrderNo)) {
				logger.error("快包代付回调参数解析失败");
				return Constant.res_FAILED;
			}
			VcOnlinePayment onlinePayment = vcOnlinePaymentService.findVcOnlinePaymentByOrderNo(vcOrderNo);
			if(null == onlinePayment){
				logger.error("快包代付回调接口订单未找到{}", vcOrderNo);
				return Constant.res_FAILED;
			}
			if(onlinePayment.getStatus() != 2){
				logger.error("订单已经处理完毕");
				return "success";
			}
			JSONObject reqData = new JSONObject();
			reqData.put("orderNo", vcOrderNo);
			reqData.put("code", Constant.SUCCESSS);
			
			String query_id = jsonObject.getString("query_id");
            String app_id = jsonObject.getString("app_id");
            String order_id = jsonObject.getString("order_id");
            String amount = jsonObject.getString("amount");
            String amount_real	= jsonObject.getString("amount_real");
            String reserved = jsonObject.getString("reserved");
            String status1	= jsonObject.getString("status");
            String status_info = jsonObject.getString("status_info");
            String amount_t0 = jsonObject.getString("amount_t0");
            String sign = request.getParameter("sign").toUpperCase();
            
            JSONObject parms = new JSONObject();
            parms.put ("query_id",query_id);
            parms.put ("app_id",app_id);
            parms.put ("order_id",order_id);
            parms.put ("amount", amount);
            parms.put ("amount_real", amount_real);
            parms.put ("reserved",reserved);
            parms.put ("status", status1);
            parms.put ("status_info",status_info);
            parms.put ("amount_t0",amount_t0);
            
            String sourctxt1 = Md5CoreUtil.getSignStrNoNull(parms)+"&key=54RAYrWqwharoxPR2pimdJEMHlP56BjW";
            logger.info("排序后{}",sourctxt1);
            String newSign = Md5Util.md5(sourctxt1).toUpperCase();
            
            logger.info("newSign,sing{},{}",newSign,sign);
			
			int statusii = 2;
			if ("19".equals(status1) && newSign.equals(sign)) {
				reqData.put("msg", "代付回调成功");
				statusii = 1;
			}
			JSONObject result = commonCallBackServiceImpl.callBackPayment(vcOrderNo, statusii, reqData);
			if (result != null && result.get("code").equals(Constant.SUCCESSS)) {
				return "success";
			}
			return Constant.res_FAILED;
		} catch (Exception e) {
			logger.error("快包代付回调接口处理异常", e);
			return  Constant.RES_ERROR;
		}
	}
}
