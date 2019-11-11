package com.vc.onlinepay.pay.order.h5;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class WanShiDaH5ServiceImpl {

    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;

    private static final String CONTENT_TYPE_TEXT_JSON = "text/html;charset=utf-8";

    private static Logger logger = LoggerFactory.getLogger (WanShiDaH5ServiceImpl.class);

    /**
     * @描述:万事达h5交易
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("万事达h5交易接收入参{}", reqData);
            result.put ("orderNo", reqData.getString ("vcOrderNo"));
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String merchNo = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String backUrl = reqData.getString ("projectDomainUrl") + "/wanShiDaPayCallBackApi";
            BigDecimal amount = new BigDecimal (reqData.getString ("amount")).setScale (2, BigDecimal.ROUND_HALF_DOWN);

            JSONObject prams = new JSONObject ();
            prams.put ("merid", merchNo);
            prams.put ("sn", reqData.getString ("vcOrderNo"));
            prams.put ("money", String.valueOf (amount));
            prams.put ("subject", "深圳盛源网络科技有限公司");
            prams.put ("urlCallback", backUrl);
            String md5Sign = Md5CoreUtil.getSignStr (prams) + key;
            String sign = Md5Util.md5 (md5Sign);
            logger.info ("加密前参数{}加密后sign{}", md5Sign, sign);
            prams.put ("sign", sign);
            logger.info ("万事达h5接口入参{}", prams);

            VcOnlineOrderMade made = new VcOnlineOrderMade ();
            made.setChannelId (reqData.getIntValue ("channelLabel"));
            made.setExpiredTime (300);
            made.setMerchNo (reqData.getString ("merchantNo"));
            made.setOrderNo (reqData.getString ("vcOrderNo"));
            made.setPaySource (reqData.getIntValue ("channelSource"));
            made.setTraAmount (reqData.getBigDecimal ("amount"));
            made.setUpMerchKey (key);
            made.setUpMerchNo (merchNo);
            made.setOpenType (10011);
            made.setOpenUrl (reqData.getString ("projectDomainUrl") + "/code/" + HiDesUtils.desEnCode (reqData.getString ("vcOrderNo")));
            made.setQrcodeUrl (API_PAY_URL);
            made.setRemarks (prams.toJSONString ());
            int r = onlineOrderMadeService.save(made);
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "获取链接成功");
            result.put("bankUrl",made.getOpenUrl());
            return listener.successHandler(result);

            /*result.put("actionUrl", API_PAY_URL);
            result.put("viewPath","auto/autoSubmit");
            result.put("data",prams);
            result.put("code", Constant.SUCCESSS);
            result.put("msg", "下单成功");
            return listener.successHandler(result);*/

            /*StringBuilder builder = new StringBuilder ();
            builder.append ("merid").append ("=").append (prams.getString ("merid")).append ("&");
            builder.append ("sn").append ("=").append (prams.getString ("sn")).append ("&");
            builder.append ("money").append ("=").append (prams.getString ("money")).append ("&");
            builder.append ("subject").append ("=").append (URLEncoder.encode(prams.getString ("subject"),"utf-8")).append ("&");
            builder.append ("urlCallback").append ("=").append (prams.getString ("urlCallback")).append ("&");
            builder.append ("sign").append ("=").append (prams.getString ("sign"));
            String reqUrl = API_PAY_URL+"?"+builder.toString ();
            String respMsg = httpPostHtml302 (reqUrl, prams);
            if(StringUtil.isEmpty (respMsg)){
                result.put ("code", Constant.FAILED);
                result.put ("msg", "下单失败");
                result.put ("bankUrl", "");
                return listener.failedHandler (result);
            }
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "获取链接成功");
            result.put ("bankUrl", respMsg);
            return listener.successHandler (result);*/
        } catch (Exception e) {
            logger.error ("万事达h5下单异常", e);
            result.put ("code", Constant.ERROR);
            result.put ("msg", "h5处理异常");
            return listener.paddingHandler (result);
        }
    }


    /**
     * @描述:302跳转解析
     * @作者:nada
     * @时间:2019/3/29
     **/
    public static String httpPostHtml302 (String url,JSONObject parms) throws Exception {
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost (url);
            httpPost.setHeader ("Content-Type", CONTENT_TYPE_TEXT_JSON);
            StringEntity se = new StringEntity (parms.toString ());
            se.setContentType (CONTENT_TYPE_TEXT_JSON);
            httpPost.setEntity (se);
            HttpResponse response = client.execute (httpPost);
            int code = response.getStatusLine ().getStatusCode ();
            if (code == 302) {
                Header header = response.getFirstHeader ("location");
                String newuri = header.getValue ();
                newuri = new String(newuri.getBytes("iso-8859-1"), "utf-8");
                if(StringUtil.isEmpty (newuri)){
                    return "解析302新URL失败";
                }
                if(newuri.contains ("https://qr.alipay.com")){
                    return newuri;
                }
                logger.info ("新地址Url{}",newuri);
                return  httpPostHtml302 (newuri,parms);
            }else if (code == 200) {
                HttpEntity entity = response.getEntity ();
                String html = EntityUtils.toString (entity, "UTF-8");
                String temp = StringUtils.deleteWhitespace(html);
                logger.info ("响应html:{}",temp);
                Document doc = Jsoup.parse(html);
                Element element = doc.getElementById("wx");
                return element.val ();
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return "解析失败";
    }
}
