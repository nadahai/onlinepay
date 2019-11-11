package com.vc.onlinepay.web.callback.replace;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ReplaceServiceImpl;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.web.base.BaseController;
import cn.hutool.json.XML;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
@RestController
@RequestMapping("/natieReplaceCallBackController")
public class NatieReplaceCallBackController extends BaseController {

	@Autowired
	private ReplaceServiceImpl commonCallBackServiceImpl;
	@Autowired
	private VcOnlinePaymentServiceImpl vcOnlinePaymentService;
	@Autowired
	private VcOnlinePaymentServiceImpl vcOnlinePaymentServiceImpl;

	/**
	 * @描述:拿铁代付回调接口
	 */
	@Override
	@RequestMapping(value = "", produces = "text/html;charset=UTF-8")
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		setHttpServletContent(request, response);
		try {
			 logger.info("拿铁代付回调接口接开始。");
			String result = this.invokeCallback(request);
			response.getWriter().write(result);
		} catch (Exception e) {
			logger.error("拿铁代付回调接口异常", e);
			super.writeErrorResponse(response);
		}
	}

	/**
	 * @描述:万事达代付回调接口
	 */
	public String invokeCallback(HttpServletRequest request) throws IOException {
		try {
			
			String str = HttpRequestTools.getFormDataRequest(request);
			logger.info("拿铁代付回调接口接收参数:{}", str);
            
			//获取所有拿铁代付订单
			
			List<VcOnlinePayment> list = vcOnlinePaymentServiceImpl.selectByChannelSource("134");
			logger.info("拿铁代付回调接口所有代付中的订单:{}", list);
			if(list != null && list.size() > 1) {
				for (int i = 0; i < list.size(); i++) {
					VcOnlinePayment vcOnlinePayment = list.get(i);

					VcOnlinePayment onlinePayment = vcOnlinePaymentService.findVcOnlinePaymentByOrderNo(vcOnlinePayment.getOrderNo());
					if(null == onlinePayment){
						logger.error("拿铁代付回调接口订单未找到{}", vcOnlinePayment.getOrderNo());
						continue;
					}
					if(onlinePayment.getStatus() != 2){
						logger.error("订单已经处理完毕");
						continue;
					}
					//查询订单状态
					String md5Key = "F6EF72223B2200D1A5B48184D44D0ABD";
		            
		            String queryUrl = "http://pay.yudugs.com:89/tran/cashier/TX0002.ac";
		            String version = "2.1";
		            String orgNo="0190600442";
		            String custId = "19062800002052";
		            
		            String custOrdNo = vcOnlinePayment.getOrderNo();
		            
		            JSONObject parms = new JSONObject();
		            parms.put ("version",version);
		            parms.put ("orgNo",orgNo);
		            parms.put ("custId",custId);
		            parms.put ("custOrdNo", custOrdNo);
		            
		            String sourctxt1 = Md5CoreUtil.getSignStr(parms)+"&key="+md5Key;
		            logger.info("排序后{}",sourctxt1);
		            String sign = Md5Util.md5(sourctxt1);
		            parms.put ("sign",sign);
		           
		            
		            logger.info("拿铁代付查询接口入参:{}",parms);
		    		String response = HttpClientTools.httpSendPostFrom(queryUrl,parms);
		            logger.info("拿铁代付查询接口返参{}",response);
		            if(StringUtils.isEmpty(response)){
		            	logger.error("发送超时");
						continue;
		            }
		            JSONObject payParams = Constant.stringToJson (response);
		            if(payParams == null){
		            	logger.error("查询失败");
						continue;
		            }
		            int statusii = 2;
		            JSONObject reqData = new JSONObject();
					reqData.put("orderNo", vcOnlinePayment.getOrderNo());
					reqData.put("code", Constant.SUCCESSS);
		            if(payParams.containsKey("ordStatus") 
		            		&& "07".equals(payParams.getString("ordStatus"))){
		            	reqData.put("msg", "代付回调成功");
						statusii = 1;
		            }
					
					JSONObject result = commonCallBackServiceImpl.callBackPayment(vcOnlinePayment.getOrderNo(), statusii, reqData);
					if (result != null && result.get("code").equals(Constant.SUCCESSS)) {
						logger.info("代付回调成功",vcOnlinePayment.getOrderNo());
					}
				}
			}
			
            
			return Constant.SUCCESSS;
		} catch (Exception e) {
			logger.error("拿铁代付回调接口处理异常", e);
			return  Constant.RES_ERROR;
		}
	}
}
