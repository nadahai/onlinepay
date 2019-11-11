package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
@Component
public class XiaoMingPayScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (XiaoMingPayScanServiceImpl.class);
    private static String url = "http://coin.cccepay.com/api/Index/pay";

    /**
     * @描述:小明交易
     * @作者:lihai
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            String sn = reqData.getString ("vcOrderNo");
            result.put ("orderNo", sn);
            String merno = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String urlCallback = reqData.getString ("projectDomainUrl") + "/xiaomingPayCallbackApi";
            String money = reqData.getString ("amount");
            String acode = "ZFB";
            if(reqData.containsKey ("service")){
                //微信
                if(reqData.getString ("service").equals ("0002")){
                    acode = "WX";
                }else if(reqData.getString ("service").equals ("010800")){
                	//银联二维码
                	acode  = reqData.containsKey("corpOrg")?reqData.getString("corpOrg"):"CMB";
                }
            }
            StringBuilder text = new StringBuilder();
            text.append ("acode").append ("=").append (acode).append ("&");
            text.append ("merno").append ("=").append (merno).append ("&");
            text.append ("money").append ("=").append (money).append ("&");
            text.append ("sn").append ("=").append (sn).append ("&");
            text.append ("urlCallback").append ("=").append (urlCallback);
            text.append (key);
            String sign = Md5Util.md5(text.toString ());

            StringBuilder builder = new StringBuilder ();
            builder.append ("merno").append ("=").append (merno).append ("&");
            builder.append ("sn").append ("=").append (sn).append ("&");
            builder.append ("money").append ("=").append (money).append ("&");
            builder.append ("acode").append ("=").append (acode).append ("&");
            builder.append ("urlCallback").append ("=").append (urlCallback).append ("&");
            builder.append ("sign").append ("=").append (sign);
            logger.info ("小明企业H5扫码接口入参{}", builder);
            String respMsg = HttpClientTools.sendGet (url, builder.toString ());
            //logger.info ("小明企业扫码支付响应{}", respMsg);

            if (StringUtil.isEmpty (respMsg)) {
                result.put ("code", Constant.FAILED);
                result.put ("msg", "扫码支付获取链接为空");
                return listener.failedHandler (result);
            }
            result.put ("code", Constant.SUCCESSS);
            result.put ("redirectHtml", respMsg);
            result.put ("bankUrl", respMsg);
            result.put ("msg", "下单成功");
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("小明扫码下单异常", e);
            result.put ("code", Constant.ERROR);
            result.put ("msg", "支付处理异常");
            return listener.paddingHandler (result);
        }
    }
}
