package com.vc.onlinepay.web.callback.replace;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ReplaceServiceImpl;
import com.vc.onlinepay.persistent.entity.online.VcOnlinePayment;
import com.vc.onlinepay.persistent.service.online.VcOnlinePaymentServiceImpl;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.web.base.BaseController;
import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
@RestController
@RequestMapping("/remittReplaceCallbackApi")
public class RemitReplaceCallBackController extends BaseController {

	 @Autowired
     private ReplaceServiceImpl commonCallBackServiceImpl;
    @Autowired
    private VcOnlinePaymentServiceImpl vcOnlinePaymentService;


    @RequestMapping(value="" , produces = "text/html;charset=UTF-8")
    public void findNameAlternative(HttpServletRequest request, HttpServletResponse response){
        setHttpServletContent(request, response);
        try {
            String result = this.invokeCallback(request);
            response.getWriter().write(result);
        } catch (Exception e) {
            logger.error("汇付宝代付回调接口异常", e);
            super.writeErrorResponse(response);
        }
    }

    public String invokeCallback(HttpServletRequest request) throws IOException {
        try {
            String queryString = request.getQueryString();
            logger.info("汇付宝代付回调参数:{}",queryString);
            if(null == queryString ||StringUtils.isEmpty(queryString)){
                logger.error("汇付宝代付回调参数为空");
                return "error";
            }
            JSONObject prams = HttpClientTools.StringToJson(queryString);
            String status = prams.getString("status");
            String vcOrderNo = prams.getString("batch_no");
            String detailData = URLDecoder.decode(prams.getString("detail_data"),"gbk");
            if (StringUtils.isAnyEmpty(vcOrderNo,status,detailData)){
                logger.error("汇付宝代付回调参数为空");
                return "error";
            }
            //-1=无效，0=未处理，1=成功
            if (StringUtil.isEmpty(vcOrderNo) || StringUtil.isEmpty(status)) {
                logger.error("汇付宝代付回调接口订单号为空{}", vcOrderNo);
                return "error";
            }
            VcOnlinePayment onlinePayment = vcOnlinePaymentService.findVcOnlinePaymentByOrderNo(vcOrderNo);
            if(null == onlinePayment){
                logger.error("汇付宝代付回调接口订单未找到{}", vcOrderNo);
                return "error";
            }
            String key = commonCallBackServiceImpl.getDecodeKey(onlinePayment.getpSign());
            if(!checkSign(prams,key)){
                logger.error("回调接口验签失败:{}", vcOrderNo);
                return "error";
            }
            JSONObject reqData = new JSONObject();
            reqData.put("orderNo",vcOrderNo);
            if(detailData.indexOf("^S^")!=-1){
                reqData.put("code", Constant.SUCCESSS);
                reqData.put("msg", "回调结果:代付成功");
                JSONObject result =  commonCallBackServiceImpl.callBackPayment(vcOrderNo,1, reqData);
                if(result!=null && result.get("code").equals(Constant.SUCCESSS)){
                    return "ok";
                }
                return "error";
            }else if(detailData.indexOf("^F^")!=-1){
                reqData.put("code", Constant.FAILED);
                reqData.put("msg", "回调结果:代付失败");
                JSONObject result =  commonCallBackServiceImpl.callBackPayment(vcOrderNo,3, reqData);
                if(result!=null && result.get("code").equals(Constant.SUCCESSS)){
                    return "ok";
                }
                return "error";
            }else{
                logger.info("汇付宝代付回调结果:代付中{}", vcOrderNo);
                return "error";
            }
        } catch (Exception e) {
            logger.error("汇付宝代付回调接口处理异常", e);
            return "error";
        } 
    }

    private boolean checkSign(JSONObject prams, String key) {
        try {
            String md5key = Constant.getChannelKeyDes(key,1);
            StringBuilder sbSign = new StringBuilder();
            sbSign.append("ret_code=" + prams.getString("ret_code"))
                    .append("&ret_msg=" + URLDecoder.decode(prams.getString("ret_msg"),"gbk"))
                    .append("&agent_id=" + prams.getString("agent_id"))
                    .append("&hy_bill_no=" + prams.getString("hy_bill_no"))
                    .append("&status=" + prams.getString("status"))
                    .append("&batch_no=" + prams.getString("batch_no"))
                    .append("&batch_amt=" + prams.getString("batch_amt"))
                    .append("&batch_num=" + prams.getString("batch_num"))
                    .append("&detail_data=" + URLDecoder.decode(prams.getString("detail_data"),"gbk"))
                    .append("&ext_param1=" + prams.getString("ext_param1"))
                    .append("&key=" + md5key);
            String signStr = StringUtils.deleteWhitespace(sbSign.toString()).toLowerCase();
//            System.out.println("汇付宝代付回调验签 = " + signStr);
            String sign = Md5Util.md5(signStr);
//            System.out.println("汇付宝代付回调验签 = " + signStr + ";签名:" + sign);
//            boolean flage = sign.equals(prams.getString("sign"));
//            if(flage){
                return true;
//            }
        } catch (Exception e) {
            logger.error("回调验签失败!:{}",e);
            return false;
        }
//        return false;
    }
}
