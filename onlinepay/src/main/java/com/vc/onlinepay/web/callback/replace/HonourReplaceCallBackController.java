package com.vc.onlinepay.web.callback.replace;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ReplaceServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.honour.EncryptUtil;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/honnourReplaceCallbackApi")
public class HonourReplaceCallBackController extends BaseController {

	@Autowired
	private ReplaceServiceImpl commonCallBackServiceImpl;

	/**
	 * @描述:荣耀金服代付回调接口
	 * @作者:Alan
	 * @时间:2018年1月4日 下午8:04:18
	 */
	@Override
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		setHttpServletContent(request, response);
		try {
//			logger.info("荣耀金服代付回调IP:{}", Constant.getIpAddr(request));
			response.setContentType("text/html");
			String result = this.invokeCallback(request);
			response.getWriter().write(result);
		} catch (Exception e) {
			logger.error("荣耀金服代付回调接口异常", e);
			super.writeErrorResponse(response);
		}
	}

	/**
	 * @描述:荣耀金服代付回调接口
	 * @作者:Alan
	 * @时间:2018年1月5日 上午10:08:05
	 */
	public String invokeCallback(HttpServletRequest request) throws IOException {
		try {
			JSONObject jsonObject = HttpRequestTools.getRequestJson(request);
			logger.info("荣耀金服代付回调接收参数:{}", jsonObject);
			if (jsonObject == null || jsonObject.isEmpty()) {
				logger.error("荣耀金服代付回调参数为空");
				return "FAILED";
			}
			// 解密回调参数
			JSONObject data = EncryptUtil.decode(jsonObject, EncryptUtil.ORG_NO);
			if (null == data || !"00000".equals(data.getString("respCode"))) {
				logger.error("荣耀金服代付回调参数解析失败");
				return "FAILED";
			}
			String vcOrderNo = data.getString("orderNumber");
			String status = data.getString("oriRespCode");
			if (StringUtils.isAnyEmpty(status, vcOrderNo)) {
				logger.error("荣耀金服代付回调接口订单号为空{}", vcOrderNo);
				return "FAILED";
			}
			JSONObject reqData = new JSONObject();
			reqData.put("orderNo", vcOrderNo);
			if ("000000".equals(status)) {
				reqData.put("code", Constant.SUCCESSS);
				reqData.put("msg", "代付回调成功");
				JSONObject result = commonCallBackServiceImpl.callBackPayment(vcOrderNo, 1, reqData);
				if (result != null && result.get("code").equals(Constant.SUCCESSS)) {
					return "SUCCESS";
				}
				return "FAILED";
			} else if ("111111".equals(status)) {
				reqData.put("code", Constant.SUCCESSS);
				reqData.put("msg", "代付回调失败");
				JSONObject result = commonCallBackServiceImpl.callBackPayment(vcOrderNo, 3, reqData);
				if (result != null && result.get("code").equals(Constant.SUCCESSS)) {
					return "SUCCESS";
				}
				return "FAILED";
			}
		} catch (Exception e) {
			logger.error("荣耀金服代付回调接口处理异常", e);
			return "FAILED";
		}
		return "FAILED";
	}
}
