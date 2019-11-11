package com.vc.onlinepay.pay.order.scan;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.HtmlCompressor;
import com.vc.onlinepay.utils.Md5CoreUtil;
import com.vc.onlinepay.utils.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Component
public class XunJieScanServiceImpl {

    private static Logger logger = LoggerFactory.getLogger (XunJieScanServiceImpl.class);

    private static Map<Integer,Integer> wxScanAmount  = new HashMap<Integer,Integer> ();
	
	 static { 
		/*
		 * wxScanAmount.put (50,50); wxScanAmount.put (100,100);
		 */
		 wxScanAmount.put (200,200); 
		 wxScanAmount.put (300,300); 
		 wxScanAmount.put (500,500); 
		 wxScanAmount.put (800,800); 
		 wxScanAmount.put (1000,1000);
		 wxScanAmount.put (2000,2000); 
		 wxScanAmount.put (3000,3000); 
		 wxScanAmount.put (5000,5000); 
		 wxScanAmount.put (8000,8000); 
		 wxScanAmount.put (10000,10000);
		 wxScanAmount.put (20000,20000); 
		 wxScanAmount.put (30000,30000); }
	 

    /**
     * @描述:迅捷云闪付
     * @时间:2017年12月1日 下午3:15:40
     */
    public JSONObject payOrder (JSONObject reqData, ResultListener listener) {
        JSONObject result = new JSONObject ();
        try {
            logger.info ("迅捷云闪付接收入参{}", reqData);
            String orderNo = reqData.getString ("vcOrderNo");
            result.put ("orderNo", orderNo);
            String API_PAY_URL = StringUtils.deleteWhitespace (reqData.getString ("channelPayUrl"));
            String merchNo = StringUtils.deleteWhitespace (reqData.getString ("channelKey"));
            String key = StringUtils.deleteWhitespace (reqData.getString ("channelDesKey"));
            String backUrl = reqData.getString ("projectDomainUrl") + "/xunJieDaPayCallBackApi";
            String returnUrl = reqData.getString("projectDomainUrl")+"/success";
            String amount2 = reqData.getString ("amount");
            /*if(!Constant.isNumeric (amount2)){
                return listener.failedHandler (Constant.failedMsg ("仅支持整数金额"));
            }*/
			
			/*
			 * int amount = Integer.valueOf (amount2); if(!wxScanAmount.containsKey (amount)
			 * || wxScanAmount.get (amount) <1){ return
			 * listener.failedHandler(Constant.failedMsg ("仅支持固定金额")); }
			 */
			 
            int type = reqData.containsKey ("payType")?reqData.getIntValue ("payType"):0;
            
	         String service = reqData.containsKey ("service") ? reqData.getString ("service") : "";
		     //payType 1:微信 2:支付宝
	         String paytype = "alipay_h5";
	         if (type == 2 || type == 10 || Constant.service_alipay.equals (service)) {
	        	 paytype = "alipay_h5";
	         }

            
            String uid = merchNo;
            String price = String.valueOf (amount2);
            String notify_url = backUrl;
            JSONObject prams = new JSONObject ();
            prams.put ("merchant_no", uid);
            prams.put ("total_fee", price);
            prams.put ("pay_type",paytype);
            prams.put ("m_order_code",orderNo);
            prams.put ("subject","一双白色运动鞋");
            prams.put ("body","一双白色运动鞋");
            prams.put ("notify_url", notify_url);
            prams.put ("return_url", returnUrl);
            prams.put ("return_type","json");
            prams.put ("passback_params",orderNo);
            String sign =Md5Util.md5(Md5CoreUtil.getSignStr(prams)+key).toUpperCase();
            prams.put ("sign",sign);

            logger.info("支付接口入参{}",prams);
            String response = HttpClientTools.baseHttpSendPost(API_PAY_URL,prams);
            logger.info("支付接口返参{}",response);
            if(StringUtils.isEmpty (response)){
                return listener.failedHandler (Constant.failedMsg ("下单为空"));
            }
            JSONObject payParams = Constant.stringToJson (response);
            if(payParams == null || payParams.isEmpty () || !payParams.containsKey ("pay_url")){
                return listener.failedHandler (Constant.failedMsg ("获取连接为空"));
            }
            result.put ("code", Constant.SUCCESSS);
            result.put ("msg", "获取链接成功");
            result.put ("bankUrl",payParams.getString ("pay_url"));
            result.put ("redirectUrl",payParams.getString ("pay_url"));
            result.put ("qrCodeUrl",payParams.getString ("pay_url"));
            return listener.successHandler (result);
        } catch (Exception e) {
            logger.error ("迅捷云闪付下单异常", e);
            return listener.failedHandler (Constant.failedMsg ("扫码处理异常"));
        }
    }


    public static void main (String[] args) {
        try {
            //String url = "http://manage.xtiane.top:8848/qrpay/pay/json";
            String url = "http://manage.xtiane.top:8848/qrpay/pay";
            String uid = "1924414Z7d";
            String Token = "5bf768203a324705bbfce09e1ae294ec";

            String price = "200";
            int paytype = 14;
            String user_order_no = (System.currentTimeMillis () + "").substring (0,13);
            String notify_url = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            String return_url = "http://pay.mastepay.com/Pay/yunPay/TUNPay.php";
            JSONObject prams = new JSONObject ();
            prams.put ("uid", uid);
            prams.put ("price", price);
            prams.put ("paytype",paytype);
            prams.put ("notify_url", notify_url);
            prams.put ("return_url", return_url);
            prams.put ("user_order_no",user_order_no);
            prams.put ("note",user_order_no);
            prams.put ("cuid", user_order_no);
            prams.put ("tm",Constant.yyyyMMdd.format(new Date ()));
            String sourctxt1 = uid + price + paytype + notify_url + return_url + user_order_no+ Token;
            String sign = DigestUtils.md5DigestAsHex(sourctxt1.getBytes()).toUpperCase();
            prams.put ("sign",sign);
            logger.info("支付接口入参{}",prams);
            //String response = HttpClientTools.baseHttpSendPost(url,prams);
            String response = HttpClientTools.httpSendPostFrom(url, prams);
            logger.info("response{}",response);
            String base64Str = Base64.getEncoder().encodeToString(response.getBytes());
            logger.info("支付接口返参{}",base64Str);

//            String entry = "PCFET0NUWVBFIGh0bWw+PCEtLSDlkI3lrZfkuIrluKbnuqLmoYYg5LiN5pi+56S66Lez6L2s5oyJ6ZKuLS0+PGh0bWwgbGFuZz0iZW4iPjxoZWFkPjxtZXRhIGh0dHAtZXF1aXY9IkNvbnRlbnQtVHlwZSIgY29udGVudD0idGV4dC9odG1sOyBjaGFyc2V0PVVURi04Ij48bWV0YSBodHRwLWVxdWl2PSJDb250ZW50LUxhbmd1YWdlIiBjb250ZW50PSJ6aC1jbiI+PG1ldGEgbmFtZT0iYXBwbGUtbW9iaWxlLXdlYi1hcHAtY2FwYWJsZSIgY29udGVudD0ibm8iPjxtZXRhIG5hbWU9ImFwcGxlLXRvdWNoLWZ1bGxzY3JlZW4iIGNvbnRlbnQ9InllcyI+PG1ldGEgbmFtZT0iZm9ybWF0LWRldGVjdGlvbiIgY29udGVudD0idGVsZXBob25lPW5vLGVtYWlsPW5vIj48bWV0YSBuYW1lPSJhcHBsZS1tb2JpbGUtd2ViLWFwcC1zdGF0dXMtYmFyLXN0eWxlIiBjb250ZW50PSJ3aGl0ZSI+PG1ldGEgaHR0cC1lcXVpdj0iWC1VQS1Db21wYXRpYmxlIiBjb250ZW50PSJJRT1FZGdlLGNocm9tZT0xIj48bWV0YSBodHRwLWVxdWl2PSJFeHBpcmVzIiBjb250ZW50PSIwIj48bWV0YSBodHRwLWVxdWl2PSJQcmFnbWEiIGNvbnRlbnQ9Im5vLWNhY2hlIj48bWV0YSBodHRwLWVxdWl2PSJDYWNoZS1jb250cm9sIiBjb250ZW50PSJuby1jYWNoZSI+PG1ldGEgaHR0cC1lcXVpdj0iQ2FjaGUiIGNvbnRlbnQ9Im5vLWNhY2hlIj48bWV0YSBuYW1lPSJ2aWV3cG9ydCIgY29udGVudD0id2lkdGg9ZGV2aWNlLXdpZHRoLCB1c2VyLXNjYWxhYmxlPW5vLCBpbml0aWFsLXNjYWxlPTEuMCwgbWF4aW11bS1zY2FsZT0xLjAsIG1pbmltdW0tc2NhbGU9MS4wIj48dGl0bGU+5pSv5LuYPC90aXRsZT48bGluayBocmVmPSIvY3NzL3BheS5jc3MiIHJlbD0ic3R5bGVzaGVldCIgbWVkaWE9InNjcmVlbiIvPjxsaW5rIGhyZWY9Ii9jc3MvdG9hc3RyLm1pbi5jc3MiIHJlbD0ic3R5bGVzaGVldCIgIG1lZGlhPSJzY3JlZW4iLz48c2NyaXB0IHNyYz0iL2pzL2pxdWVyeS5taW4uanMiPjwvc2NyaXB0PjxzY3JpcHQgc3JjPSIvanMvanF1ZXJ5LnFyY29kZS5taW4uanMiIGNoYXJzZXQ9InV0Zi04Ij48L3NjcmlwdD48c2NyaXB0IHNyYz0iL2pzL2NsaXBib2FyZC5taW4uanMiPjwvc2NyaXB0PjxzY3JpcHQgIHNyYz0iL2pzL3RvYXN0ci5taW4uanMiPjwvc2NyaXB0Pgk8L2hlYWQ+PGJvZHk+CTxkaXYgY2xhc3M9ImJvZHkiPgkJPGgxIGNsYXNzPSJtb2QtdGl0bGUiPgkJCSAJCQkJCQkJCQkJCQkJCQkJCQkJCQkJCQkJCQkJCQkgCQkJCQkJCQkJPHNwYW4gY2xhc3M9J2ljby0zIGljb19sb2cnPjwvc3Bhbj4JCQkJCQkJCQkJCQkJCQkJCQkJCQkgCQkJCQkJIAkJCQkJCSAJCQkJCQkJCQkJCQkgIAkJCSAJCQkgCQkJCQkJIAkJCSAJCQkJCQkgCQkJCQkJCQkJCQkJIAkJCQkJCQkJCQkJCQkJPC9oMT4JCTxpbnB1dCB0eXBlPSJoaWRkZW4iIGlkPSJ0eXBlIiBuYW1lPSJ0eXBlIiB2YWx1ZT0iMTQiIC8+IDxpbnB1dCB0eXBlPSJoaWRkZW4iIGlkPSJ0cmFkZV9ubyIgbmFtZT0idHJhZGVfbm8iIHZhbHVlPSJIMDYxNzIxMTU1NTQxNDA4M1dJIiAvPiA8aW5wdXQgdHlwZT0iaGlkZGVuIiBpZD0iYWNjb3VudE5hbWUiIHZhbHVlPSIgIiAvPgkJPGRpdiBjbGFzcz0ibW9kLWN0Ij4JCQkgIDwhLS0g5Yqo56CBIC0tPgkJCTxkaXYgID48YnI+IAkJCTxzcGFuID4g5q2k5LqM57u056CB5q+P5qyh5Y+q6IO95L2/55So5LiA5qyh77yMIOWmgumcgOmHjeaWsOWFheWAvOivt+iOt+WPluacgOaWsOS6jOe7tOeggSA8L3NwYW4+CQkJPC9kaXY+IAkJCTwhLS0g5Zu656CBIC0tPgkJCTxkaXYgPgkJCQk8YnI+IAkJCSAgICA8aDE+CQkJCQkgPHNwYW4gc3R5bGU9ImNvbG9yOiAjRkYwMDAwOyAgIiA+5Yqh5b+F5oyJ5LiL6Z2i55qE6YeR6aKd5LuY5qy+77yM5Lul5YWN5LiN6IO95Y+K5pe25Yiw6LSm77yM6LCi6LCiPC9zcGFuPgkJCSAgPC9oMT4JCQkgPC9kaXY+IAkJCTxkaXYgIGNsYXNzPSJhbW91bnQiID4JCSAgIAkJCTxzcGFuIGlkPSJtb25leSI+77+lMTk5LjcxPC9zcGFuPiAJCQk8L2Rpdj4JCQkgPGRpdiBzdHlsZT0iY29sb3I6ICNGRjAwMDA7IHBhZGRpbmctYm90dG9tOiA1cHg7IGZvbnQtc2l6ZTogMjBweDsgZm9udC13ZWlnaHQ6IGJvbGQ7IiAgPgkJCQk8c3Bhbj4gPC9zcGFuPiAJCQk8L2Rpdj4JICAgICAgICAgIAkJCTwhLS3mlK/ku5jlrp1hcHDmlK/ku5gtLT4JCQk8ZGl2IGNsYXNzPSJxcmNvZGUtaW1nLXdyYXBwZXIiIGRhdGEtcm9sZT0icXJQYXlJbWdXcmFwcGVyIiA+CQkJCTxkaXYgZGF0YS1yb2xlPSJxclBheUltZyIgY2xhc3M9InFyY29kZS1pbWctYXJlYSI+CQkJCQk8ZGl2IGNsYXNzPSJ1aS1sb2FkaW5nIHFyY29kZS1sb2FkaW5nIiBkYXRhLXJvbGU9InFyUGF5SW1nTG9hZGluZyIgc3R5bGU9ImRpc3BsYXk6IG5vbmU7Ij48L2Rpdj4JCQkJCTxkaXYgc3R5bGU9InBvc2l0aW9uOiByZWxhdGl2ZTsgZGlzcGxheTogaW5saW5lLWJsb2NrOyI+CQkJCQkJPGltZyBpZD0ic2hvd19xcmNvZGUiIHN0eWxlPSJkaXNwbGF5OiBibG9jazsgd2lkdGg6IDIwMHB4OyBoZWlnaHQ6IDIwMHB4OyI+CQkJCQkJPCEtLSAgdGg6c3JjPSIke3BheXVybH0iICAtLT4JCQkJCQkgCQkJCQkJIAkJCQkJCQkJCQkJCTxkaXYgaWQ9InFyY29kZSIgc3R5bGU9ImRpc3BsYXk6IG5vbmU7Ij48L2Rpdj4JCQkJCTwvZGl2PgkJCQk8L2Rpdj4JCQk8L2Rpdj4JCQk8ZGl2IGNsYXNzPSJwYXlidG4iPgkJCQkJCQkJPCEtLSA8YSAgdGg6aWY9IiR7dHlwZT09M30gb3IgICR7dHlwZT09MTJ9IG9yICR7dHlwZT09MTZ9IG9yICR7dHlwZT09MjJ9IG9yICR7dHlwZT09MjR9IG9yICR7dHlwZT09MjV9IG9yICR7dHlwZT09MjZ9ICBvciAke3R5cGU9PTM1fSAiIHN0eWxlPSJkaXNwbGF5OiBub25lOyIgdGg6aHJlZj0iJHtjYWxsdXJsfSIgaWQ9ImFsaXBheWJ0biIgY2xhc3M9ImJ0biBidG4tcHJpbWFyeSIgdGFyZ2V0PSJfYmxhbmsiPueCueWHu+i/memHjOWQr+WKqOaUr+S7mOWunUFwcOaUr+S7mDwvYT4gLS0+CQkJCTwhLS08YSAgdGg6aWY9IiR7dHlwZT09MTJ9IG9yICR7dHlwZT09MTZ9IG9yICR7dHlwZT09MjJ9IG9yICR7dHlwZT09MjV9IG9yICR7dHlwZT09MjZ9ICBvciAke3R5cGU9PTM1fSBvciAke3R5cGU9PTUyIH0iIHN0eWxlPSJkaXNwbGF5OiBub25lOyIgdGg6aHJlZj0iJHtjYWxsdXJsfSIgaWQ9ImFsaXBheWJ0biIgY2xhc3M9ImJ0biBidG4tcHJpbWFyeSIgdGFyZ2V0PSJfYmxhbmsiPueCueWHu+i/memHjOWQr+WKqOaUr+S7mOWunUFwcOaUr+S7mDwvYT4gCQkJCTwhLS08YSAgdGg6aWY9IiR7dHlwZT09MTF9ICAiIHN0eWxlPSJkaXNwbGF5OiBub25lOyIgdGg6aHJlZj0iJHtjYWxsdXJsfSIgaWQ9ImFsaXBheWJ0biIgY2xhc3M9ImJ0biBidG4tcHJpbWFyeSIgdGFyZ2V0PSJfYmxhbmsiPueCueWHu+i/memHjOWQr+WKqOaUr+S7mOWuneaUr+S7mCjpnIDlt7Llronoo4Xmt5jlrp0pPC9hPiAJCQkJIAkJCQk8YSAgdGg6aWY9IiR7dHlwZT09MTF9ICAiICB0aDpocmVmPSIke3RiSDVVcmx9IiBzdHlsZT0iZGlzcGxheTogbm9uZTsiIGlkPSJhbGlwYXlidG4yIiBjbGFzcz0iYnRuIGJ0bi1wcmltYXJ5IiB0YXJnZXQ9Il9ibGFuayI+54K55Ye76L+Z6YeM5ZCv5Yqo5pSv5LuY5a6dYXBw5pSv5LuYPC9hPgkJCQk8YnV0dG9uICB0aDppZj0iJHt0eXBlPT0xMX0gIiB0aDphdHRyPSJkYXRhLWNsaXBib2FyZC10ZXh0PSR7cGF5dXJsfSAiICAgY2xhc3M9ImJ0biBidG4tcHJpbWFyeSIgaWQ9ImFsaXBheWJ0biIgICA+5aSN5Yi25LuY5qy+6ZO+5o6l5bm25omT5byA5pSv5LuY5a6dPC9idXR0b24+CQkJICAgIDxhICB0aDppZj0iJHt0eXBlPT0xfSAiIHN0eWxlPSJkaXNwbGF5OiBub25lOyIgdGg6aHJlZj0iJHtjYWxsdXJsfSIgaWQ9ImFsaXBheWJ0biIgY2xhc3M9ImJ0biBidG4tcHJpbWFyeSIgdGFyZ2V0PSJfYmxhbmsiPueCueWHu+i/memHjOWQr+WKqOaUr+S7mOWunWFwcOaUr+S7mDwvYT4JCQkJPGEgIHRoOmlmPSIke3R5cGU9PTF9ICIgc3R5bGU9ImRpc3BsYXk6IG5vbmU7IiAgaWQ9ImFsaXBheWJ0biIgY2xhc3M9ImJ0biBidG4tcHJpbWFyeSIgdGFyZ2V0PSJfYmxhbmsiPueCueWHu+i/memHjOWQr+WKqOaUr+S7mOWunWFwcOaUr+S7mDwvYT4gIC0tPgkJCQk8IS0tIOaUr+S7mOWunSAgIDHovazkuKrkurog5oiW57qi5YyFLS0+CQkJCQkJCSAJCQkgCQkJCQkJCQk8IS0tIOaUr+S7mOWunSAgOCDnvZHllYYgMTEg6ZO26KGM5Y2hICR7dHlwZT09MTF9IG9yLS0+CQkJCQkJCSAJCQkJPCEtLeW+ruS/oSAtLT4JCQkJCQkJCQkJCQkgPCEtLeW+ruS/oSDovazpk7booYzljaEtLT4JCQkJIAkJCQkJCQkJCQkJCTwhLS0g5LqR6Zeq5LuYIC0tPgkJCQkJCQkJPCEtLSDpk7booYzlm7rnoIEgLS0+CQkJCTx1bCBjbGFzcz0iY2VudGVyVWwiPgkJCQkJPGxpPgkJCQkJCTxoMT4JCQkJCQkJPHNwYW4gc3R5bGU9ImNvbG9yOiAjRkYwMDAwOyAgIiA+5aaC5p6c5oKo5piv5omL5py6572R6aG15pSv5LuY77yaPC9zcGFuPgkJCQkJCTwvaDE+CQkJCQk8L2xpPgkJCQkJPGxpPgkJCQkJCTxoMT4JCQkJCQkJPHNwYW4gc3R5bGU9ImNvbG9yOiAjRkYwMDAwOyAgIiA+4pGgIOaIquWbvuS/neWtmOWIsOebuOWGjDwvc3Bhbj4JCQkJCQk8L2gxPgkJCQkJPC9saT4JCQkJCTxsaT4JCQkJCQk8aDE+CQkJCQkJCQkJCQkJCQkJCQkJCQkJPHNwYW4gc3R5bGU9J2NvbG9yOiAjRkYwMDAwJyA+4pGhIOeEtuWQjuaJk+W8gOW+ruS/oSAi5omr5LiA5omrIjwvc3Bhbj4JCQkJCQkJCQkJCQkJPC9oMT4JCQkJCTwvbGk+CQkJCQk8bGk+CQkJCQkJPGgxPgkJCQkJCQk8c3BhbiBzdHlsZT0nY29sb3I6ICNGRjAwMDAnPuKRoiDnm7jlhozkuK3pgInmi6nlm748L3NwYW4+CQkJCQkJPC9oMT4JCQkJCTwvbGk+CQkJCTwvdWw+CQkJCQkJCQkgPCEtLSDpk7booYzlm7rnoIEg55+t5L+h5Zue6LCDIC0tPgkJCQkJCQk8L2Rpdj4JCQk8YnI+CQkJPCEtLSAgPHAgc3R5bGU9ImNvbG9yOiAjRkYwMDAwIj7mraTkuoznu7TnoIHlj6rmj5DkvpvmnKzmrKHlhYXlgLzvvIzov4fmnJ/kvZzlup/vvIzor7fli7/ph43lpI3lhYXlgLw8L3A+IC0tPgkJPC9kaXY+CTwvZGl2PjxzY3JpcHQgdHlwZT0idGV4dC9qYXZhc2NyaXB0Ij4gCQl2YXIgY2xpcGJvYXJkID0gbmV3IENsaXBib2FyZEpTKCcuYnRuQ29weScpOwljbGlwYm9hcmQub24oJ3N1Y2Nlc3MnLCBmdW5jdGlvbihlKSB7CQllLmNsZWFyU2VsZWN0aW9uKCk7IC8v6YCJ5Lit6ZyA6KaB5aSN5Yi255qE5YaF5a65CQlhbGVydCgi5aSN5Yi25oiQ5Yqf77yBIik7CX0pOwljbGlwYm9hcmQub24oJ2Vycm9yJywgZnVuY3Rpb24oZSkgewkJZS5jbGVhclNlbGVjdGlvbigpOyAvL+mAieS4remcgOimgeWkjeWItueahOWGheWuuQkJYWxlcnQoIuWkjeWItuaIkOWKn++8gSIpOwl9KTsJCS8vdmFyIGNsaXBib2FyZCA9IG5ldyBDbGlwYm9hcmRKUygnLmJ0bkNvcHknKTsgICAgdmFyIG15VGltZXI7IAl2YXIgbW9uZXk9IjE5OS43MSI7IAl2YXIgdHlwZT0xNDsgCSB2YXIgdWFhID0gbmF2aWdhdG9yLnVzZXJBZ2VudDsgICAgIHZhciBpc2lPUyA9ICEhdWFhLm1hdGNoKC9cKGlbXjtdKzsoIFU7KT8gQ1BVLitNYWMgT1MgWC8pOyAvL2lvc+e7iOerryAgICAgIAkgdmFyIGlzQW5kcm9pZCA9IHVhYS5pbmRleE9mKCdBbmRyb2lkJykgPiAtMSB8fCB1YWEuaW5kZXhPZignQWRyJykgPiAtMTsgLy9hbmRyb2lk57uI56uvICAgICAgICAgZnVuY3Rpb24gdGltZXJzKGludERpZmYpIHsgICAgICAgIG15VGltZXIgPSB3aW5kb3cuc2V0SW50ZXJ2YWwoZnVuY3Rpb24gKCkgeyAgICAgICAgICAgIGNoZWNrZGF0YSgpOyAgICAgICAgfSwgMzAwMCk7ICAgIH0JLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8v6K6i5Y2V55+l5ZCm5pSv5LuY5p+l6K+iLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8gICAgZnVuY3Rpb24gY2hlY2tkYXRhKCl7CQl2YXIgbm8gPSAkKCIjdHJhZGVfbm8iKS52YWwoKTsgICAgICAgICAJCS8vIi9QYXlfRXhlbXB0aW9uX3N1Y2Nlc3NOb3RpZnkuaHRtbCIsICAgICAgICAkLnBvc3QoCQkJIi9xcnBheS9zdGF0dXNRdWVyeSIsICAgICAgICAgICAgeyAgICAgICAgICAgICAgICB0cmFkZV9ubyA6IG5vLCAgICAgICAgICAgICAgICB0eXBlICAgICA6IHR5cGUsICAgICAgICAgICAgfSwgICAgICAgICAgICBmdW5jdGlvbihkYXRhKXsgICAgICAgICAgICAgICAgaWYgKGRhdGEubXNnID09ICfmlK/ku5jmiJDlip8nKXsgICAgICAgICAgICAgICAgICAgIC8vd2luZG93LmNsZWFySW50ZXJ2YWwodGltZXIpOyAgICAgICAgICAgICAgICAgICAgd2luZG93LmNsZWFySW50ZXJ2YWwodGltZXJzKTsgICAgICAgICAgICAgICAgICAgICQoIiNzaG93X3FyY29kZSIpLmF0dHIoInNyYyIsIi9pbWFnZXMvcGF5X29rLnBuZyIpOyAgICAgICAgICAgICAgICAgICAgJCgiI3VzZSIpLnJlbW92ZSgpOyAgICAgICAgICAgICAgICAgICAgJCgiI21vbmV5IikudGV4dCgi5pSv5LuY5oiQ5YqfIik7ICAgICAgICAgICAgICAgICAgICAkKCIjbXNnIikuaHRtbCgiPGgxPuWNs+Wwhui/lOWbnuWVhuWutumhtTwvaDE+Iik7ICAgICAgICAgICAgICAgICAgICBpZiAoaXNNb2JpbGUoKSA9PSAxKXsgICAgICAgICAgICAgICAgICAgICAgICAkKCIucGF5YnRuIikuaHRtbCgnPGEgaHJlZj0iJytkYXRhLnJldHVybnVybCsnIiBjbGFzcz0iYnRuIGJ0bi1wcmltYXJ5Ij7ov5Tlm57llYblrrbpobU8L2E+Jyk7ICAgICAgICAgICAgICAgICAgICB9ICAgICAgICAgICAgICAgICAgICAgc2V0VGltZW91dChmdW5jdGlvbigpeyAgICAgICAgICAgICAgICAgICAgICAgIHdpbmRvdy5sb2NhdGlvbiA9IGRhdGEucmV0dXJudXJsOyAgICAgICAgICAgICAgICAgICAgfSwgMzAwMCk7ICAgICAgICAgICAgICAgIH0gICAgICAgICAgICB9ICAgICAgICApOyAgICB9CSAgICBmdW5jdGlvbiBhY2NvdW50Q29sb3IoKXsgICAgCXZhciBhY2NvdW50TmFtZT0kKCIjYWNjb3VudE5hbWUiKS52YWwoKTsJCXZhciBsZW49YWNjb3VudE5hbWUubGVuZ3RoOwkJaWYobGVuPT0wKXsJCQlyZXR1cm47CQl9CQkkKCIjZmlyc3ROYW1lIikuaHRtbChhY2NvdW50TmFtZS5zdWJzdHJpbmcoMCwxKSk7CQkkKCIjZmlyc3ROYW1lIikuY3NzKHsiZm9udC1zaXplIjoiMzZweCIsImJvcmRlciI6IjVweCBzb2xpZCByZ2IoOTcsMjM4LDApIiwiYm9yZGVyLXJhZGl1cyI6IjJweCIsImJhY2tncm91bmQiOiIjZmZmIn0pOwkJJCgiI2xhc3ROYW1lIikuaHRtbChhY2NvdW50TmFtZS5zdWJzdHJpbmcoMSxsZW4pKTsJCQl9ICAgICAgZnVuY3Rpb24gcXJjb2RlX3RpbWVvdXQoKXsgICAgICAgICQoJyNzaG93X3FyY29kZScpLmF0dHIoInNyYyIsIi9pbWFnZXMvcXJjb2RlX3RpbWVvdXQucG5nIik7ICAgICAgICAkKCIjdXNlIikuaGlkZSgpOyAgICAgICAgJCgnI21zZycpLmh0bWwoIjxoMT7mlK/ku5jotoXml7Yg6K+36YeN5paw5o+Q5Lqk6K6i5Y2VPC9oMT4iKTsgICAgfSAgICBmdW5jdGlvbiBpc1dlaXhpbihxcnVybCkgeyAgICAgICAgICAgICAgICBpZiAocXJ1cmwubWF0Y2goL3d4cC9pKSA9PSAnd3hwJykgeyAgICAgICAgICAgIHJldHVybiAxOyAgICAgICAgfSBlbHNlIHsgICAgICAgICAgICByZXR1cm4gMDsgICAgICAgIH0gICAgfSAgICAvLyDnlJ/miJDkuoznu7TnoIEgICAgZnVuY3Rpb24gcXJzaG93KHN0cmNvZGUpIHsgICAgICAgIHZhciBxcmNvZGUgPSAkKCcjcXJjb2RlJykucXJjb2RlKHsgdGV4dDogc3RyY29kZSB9KS5oaWRlKCk7ICAgICAgICB2YXIgY2FudmFzID0gcXJjb2RlLmZpbmQoJ2NhbnZhcycpLmdldCgwKTsgICAgICAgICQoJyNzaG93X3FyY29kZScpLmF0dHIoJ3NyYycsIGNhbnZhcy50b0RhdGFVUkwoJ2ltYWdlL2pwZycpKTsgICAgICAgIGNhbnZhcy5yZW1vdmUoKTsgICAgfSAgICAvL+WIt+aWsOWKqOaAgeeggSAgICBmdW5jdGlvbiBxcnNob3dCYXNlKHN0cmNvZGUpIHsgICAgICAgICQoJyNzaG93X3FyY29kZScpLmF0dHIoJ3NyYycsc3RyY29kZSk7ICAgIH0gICAgICAgICQoKS5yZWFkeShmdW5jdGlvbigpeyAgICAJICAgIAkvL3ZhciBjb2xvcj1uZXcgYWNjb3VudENvbG9yKCk7ICAgICAgICB0b2FzdHIub3B0aW9ucyA9IHsgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcG9zaXRpb25DbGFzczogInRvYXN0LXRvcC1mdWxsLXdpZHRoIiwgICAgICAgICAgICAgICAgICAgIH07ICAgIAkvL3ZhciBjbGlwYm9hcmQgPSBuZXcgQ2xpcGJvYXJkSlMoJyNhbGlwYXlidG4nKTsJICAgICAgLy8gIGNsaXBib2FyZC5vbignc3VjY2VzcycsIGZ1bmN0aW9uKGUpIHsJICAgIAkvL3RvYXN0ci5zdWNjZXNzKCLlpI3liLbmiJDlip/vvIzor7fliY3lvoDmlK/ku5jlrp3lj5HpgIHpk77mjqXnu5nku7vkvZXlpb3lj4vjgIIiKTsJICAgICAgICAvLyBsb2NhdGlvbi5ocmVmPSdhbGlwYXlzOi8vcGxhdGZvcm1hcGkvc3RhcnRhcHAnIDsgIAkgICAgICAgLy8gIHdpbmRvdy5vcGVuKCdhbGlwYXlzOi8vcGxhdGZvcm1hcGkvc3RhcnRhcHA/YXBwSWQ9MjAwMDAwNjcnKQkgICAgLy99KTsgICAgICAgIHRpbWVycygnMTgwJyk7ICAgICAgICB2YXIgaXN0eXBlID0gdHlwZTsgICAgICAgIHZhciBzdXJlbW9uZXkgPSAiMSI7ICAgICAgICB2YXIgdWFhID0gbmF2aWdhdG9yLnVzZXJBZ2VudDsgICAgICAgICAgICAgICAgICQoJy5wYXlidG4nKS5jc3MoJ3BhZGRpbmctdG9wJywnMCcpOyAgICAgICAgICQoJy5wYXlidG4nKS5jc3MoJ2hlaWdodCcsJzYwcHgnKTsgICAgICAgICB2YXIgcXJVcmw9Imh0dHA6XC9cLzEwMy40NC4zMS4yNTA6ODg0OFwvZ2V0UXJcL0gwNjE3MjExNTU1NDE0MDgzV0kiOyAgICAgICAgIHZhciBoNVVybD0iYWxpcGF5czpcL1wvcGxhdGZvcm1hcGlcL3N0YXJ0YXBwP2FwcElkPTIwMDAwMDY3XHUwMDI2dXJsPWh0dHA6XC9cLzEwMy40NC4zMS4yNTA6ODg0OFwvY2FsbGJhY2tGb3JHTVwvSDA2MTcyMTE1NTU0MTQwODNXSSI7ICAgICAgICAgY29uc29sZS5sb2cocXJVcmwpOyAgICAgICAgIGNvbnNvbGUubG9nKGg1VXJsKTsgICAgICAgIC8v57uY5Yi25LqM57u056CB5pi+56S65o6n5Yi2ICAgICAgIGlmKGlzdHlwZT09MjR8fGlzdHlwZT09MzF8fGlzdHlwZT09Mzd8fGlzdHlwZT09Mzh8fGlzdHlwZT09MTB8fGlzdHlwZT09MTN8fGlzdHlwZT09MTR8fGlzdHlwZT09MTV8fGlzdHlwZT09MTZ8fGlzdHlwZT09MTd8fGlzdHlwZT09MTh8fGlzdHlwZT09MTl8fGlzdHlwZT09NDB8fGlzdHlwZT09NDF8fGlzdHlwZT09NDV8fGlzdHlwZT09NTR8fGlzdHlwZT09NTUpeyAgICAJICAgaWYocXJVcmwuaW5kZXhPZigiYXBwUGF5Iik+LTF8fGlzdHlwZT09NDV8fGlzdHlwZT09NTR8fGlzdHlwZT09NTUpeyAgICAJCSAgIHFyc2hvd0Jhc2UocXJVcmwpOyAvL+WQjuWPsOa4suafk+WbviAgICAJICAgfSBlbHNleyAgICAJCSAgIHFyc2hvdyhxclVybCk7Ly/liY3lj7DmuLLmn5Plm74gICAgCSAgIH0gICAgICAgfSAgIGVsc2V7CQkgICBxcnNob3cocXJVcmwpOy8v5YmN5Y+w5riy5p+T5Zu+CSAgIH0gICAgICAgICAvL+S/ruaUueWQjeWtl+minOiJsiAgICAJYWNjb3VudENvbG9yKCk7ICAgICAgICAvL+i3s+i9rOaMiemSruaYvuekuuaOp+WItiAgICAgICAgLy8oaXN0eXBlID09IDF8fCBpc3R5cGUgPT0gOHx8aXN0eXBlID09IDExfHxpc3R5cGUgPT0gMTIpICDmlK/ku5jmlrnlvI8x5pqC5LiN5pi+56S66Lez6L2s5oyJ6ZKuIDEw5piv5paw5Zu656CB6YCa6YGTIDTmmK/lvq7kv6EgICAgICAgIGlmIChpc01vYmlsZSgpID09IDEgKXsgICAgICAgIAlpZihpc3R5cGU9PTI0KXsJCQkJJCgiLnFyY29kZS1pbWctd3JhcHBlciIpLmhpZGUoKTsgICAgICAgIAl9ICAgICAgICAgICAgJCgiLnBheWJ0biBhIikuc2hvdygpOyAgICAgICAgICAgICQoIiNhbGlwYXlidG4iKS5zaG93KCk7ICAgICAgICAgICAgJCgiI2FsaXBheWJ0bjIiKS5zaG93KCk7ICAgICAgICB9ZWxzZXsgICAgICAgIAkgICAgICAgIAkkKCIjdG90YW9iYW9hcHAiKS5oaWRlKCk7ICAgICAgICB9ICAgICAgICAgaWYgKGlzaU9TKSB7ICAgICAgICAJJCgnI3Nob3dfcXJjb2RlJykuY3NzKHt3aWR0aDogMjAwLGhlaWdodDoyMDB9KTsgICAgICAgIAkkKCcjdG90YW9iYW9hcHAnKS5hdHRyKCJocmVmIiwiaHR0cDovL2l0dW5lcy5hcHBsZS5jb20vYXBwL2lkMzg3NjgyNzI2P3NwcmVmZXI9cGhwMDU2Jm10PTgiKTsgICAgICAgIH0gZWxzZSBpZiAoaXNBbmRyb2lkKSB7ICAgICAgICAJJCgnI3RvdGFvYmFvYXBwJykuYXR0cigiaHJlZiIsImh0dHA6Ly9tYS50YW9iYW8uY29tL1poRW04MSIpOyAgICAgICAgfSAgICAgICAgICAgICAgICAvL+ebtOaOpeWOu+aUr+S7mOWunSAgICAgICAgLyogICAgICBpZihpc3R5cGUgPT0gMjQpeyAgICAgICAgICAgICAgICQoIiNhbGlwYXlidG4iKS5jbGljayhmdW5jdGlvbigpIHsgICAgICAgICAgICAgICAgCSBsb2NhdGlvbi5ocmVmPSdhbGlwYXlxcjovL3BsYXRmb3JtYXBpL3N0YXJ0YXBwP3NhSWQ9MTAwMDAwMDcmcXJjb2RlPScrcXJVcmwgOyAgICAgICAgICAgICAgIH0pOyAgICAgICAgICAgfSAgKi8gICAgICAgICAgICAgIC8vbG9jYXRpb24uaHJlZj0iYWxpcGF5czpcL1wvcGxhdGZvcm1hcGlcL3N0YXJ0YXBwP2FwcElkPTIwMDAwMDY3XHUwMDI2dXJsPWh0dHA6XC9cLzEwMy40NC4zMS4yNTA6ODg0OFwvY2FsbGJhY2tGb3JHTVwvSDA2MTcyMTE1NTU0MTQwODNXSSI7ICAgICAgICAgICAgIC8vd2luZG93Lm9wZW4oImFsaXBheXM6XC9cL3BsYXRmb3JtYXBpXC9zdGFydGFwcD9hcHBJZD0yMDAwMDA2N1x1MDAyNnVybD1odHRwOlwvXC8xMDMuNDQuMzEuMjUwOjg4NDhcL2NhbGxiYWNrRm9yR01cL0gwNjE3MjExNTU1NDE0MDgzV0kiKTsgICAgICAgICAgLy8gaWYoaXN0eXBlID09IDEgKXsgICAgICAgICAgICAvLyAgIHZhciBteW1lc3NhZ2U9Y29uZmlybSgi5YmN5b6A5pSv5LuY5a6d5pSv5LuYIik7CQkJIC8vICAgaWYobXltZXNzYWdlPT10cnVlKQkJCSAvLyAgIHsgICAgCQkJICAgIAkvL2ludGVudDovL3BsYXRmb3JtYXBpL3N0YXJ0YXBwP3NhSWQ9MTAwMDAwMDcmcXJjb2RlPQkJCSAgICAJLy9sb2NhdGlvbi5ocmVmPSdpbnRlbnQ6Ly9wbGF0Zm9ybWFwaS9zdGFydGFwcD9zYUlkPTEwMDAwMDA3JnFyY29kZT0nK3FyVXJsIDsJCQkgICAgICAgIC8vYWxpcGF5cXI6Ly9wbGF0Zm9ybWFwaS9zdGFydGFwcD9zYUlkPTEwMDAwMDA3JnFyY29kZSAgaHR0cHM6Ly9kcy5hbGlwYXkuY29tLz9mcm9tPW1vYmlsZWNvZGVjJnNjaGVtZT0JCQkgICAgCS8vbG9jYXRpb24uaHJlZj0nYWxpcGF5cXI6Ly9wbGF0Zm9ybWFwaS9zdGFydGFwcD9zYUlkPTEwMDAwMDA3JnFyY29kZT0nK3FyVXJsIDsJCQkgICAgCS8vJCgiI2FsaXBheWJ0biIpIC5jbGljaygpOyAJCQkvLyAgICB9ICAgICAgICAgICAgIC8vfSAgICB9KTsgICAgICAgZnVuY3Rpb24gaXNNb2JpbGUoKSB7ICAgICAgICAgdmFyIHVhID0gbmF2aWdhdG9yLnVzZXJBZ2VudC50b0xvd2VyQ2FzZSgpOyAgICAgICAgIF9sb25nX21hdGNoZXMgPSAnZ29vZ2xlYm90LW1vYmlsZXxhbmRyb2lkfGF2YW50Z298YmxhY2tiZXJyeXxibGF6ZXJ8ZWxhaW5lfGhpcHRvcHxpcChob25lfG9kKXxraW5kbGV8bWlkcHxtbXB8bW9iaWxlfG8yfG9wZXJhIG1pbml8cGFsbSggb3MpP3xwZGF8cGx1Y2tlcnxwb2NrZXR8cHNwfHNtYXJ0cGhvbmV8c3ltYmlhbnx0cmVvfHVwXC4oYnJvd3NlcnxsaW5rKXx2b2RhZm9uZXx3YXB8d2luZG93cyBjZTsgKGllbW9iaWxlfHBwYyl8eGlpbm98bWFlbW98ZmVubmVjJzsgICAgICAgICBfbG9uZ19tYXRjaGVzID0gbmV3IFJlZ0V4cChfbG9uZ19tYXRjaGVzKTsgICAgICAgICBfc2hvcnRfbWF0Y2hlcyA9ICcxMjA3fDYzMTB8NjU5MHwzZ3NvfDR0aHB8NTBbMS02XWl8Nzcwc3w4MDJzfGEgd2F8YWJhY3xhYyhlcnxvb3xzXC0pfGFpKGtvfHJuKXxhbChhdnxjYXxjbyl8YW1vaXxhbihleHxueXx5dyl8YXB0dXxhcihjaHxnbyl8YXModGV8dXMpfGF0dHd8YXUoZGl8XC1tfHIgfHMgKXxhdmFufGJlKGNrfGxsfG5xKXxiaShsYnxyZCl8YmwoYWN8YXopfGJyKGV8dil3fGJ1bWJ8YndcLShufHUpfGM1NVwvfGNhcGl8Y2N3YXxjZG1cLXxjZWxsfGNodG18Y2xkY3xjbWRcLXxjbyhtcHxuZCl8Y3Jhd3xkYShpdHxsbHxuZyl8ZGJ0ZXxkY1wtc3xkZXZpfGRpY2F8ZG1vYnxkbyhjfHApb3xkcygxMnxcLWQpfGVsKDQ5fGFpKXxlbShsMnx1bCl8ZXIoaWN8azApfGVzbDh8ZXooWzQtN10wfG9zfHdhfHplKXxmZXRjfGZseShcLXxfKXxnMSB1fGc1NjB8Z2VuZXxnZlwtNXxnXC1tb3xnbyhcLnd8b2QpfGdyKGFkfHVuKXxoYWllfGhjaXR8aGRcLShtfHB8dCl8aGVpXC18aGkocHR8dGEpfGhwKCBpfGlwKXxoc1wtY3xodChjKFwtfCB8X3xhfGd8cHxzfHQpfHRwKXxodShhd3x0Yyl8aVwtKDIwfGdvfG1hKXxpMjMwfGlhYyggfFwtfFwvKXxpYnJvfGlkZWF8aWcwMXxpa29tfGltMWt8aW5ub3xpcGFxfGlyaXN8amEodHx2KWF8amJyb3xqZW11fGppZ3N8a2RkaXxrZWppfGtndCggfFwvKXxrbG9ufGtwdCB8a3djXC18a3lvKGN8ayl8bGUobm98eGkpfGxnKCBnfFwvKGt8bHx1KXw1MHw1NHxlXC18ZVwvfFwtW2Etd10pfGxpYnd8bHlueHxtMVwtd3xtM2dhfG01MFwvfG1hKHRlfHVpfHhvKXxtYygwMXwyMXxjYSl8bVwtY3J8bWUoZGl8cmN8cmkpfG1pKG84fG9hfHRzKXxtbWVmfG1vKDAxfDAyfGJpfGRlfGRvfHQoXC18IHxvfHYpfHp6KXxtdCg1MHxwMXx2ICl8bXdicHxteXdhfG4xMFswLTJdfG4yMFsyLTNdfG4zMCgwfDIpfG41MCgwfDJ8NSl8bjcoMCgwfDEpfDEwKXxuZSgoY3xtKVwtfG9ufHRmfHdmfHdnfHd0KXxub2soNnxpKXxuenBofG8yaW18b3AodGl8d3YpfG9yYW58b3dnMXxwODAwfHBhbihhfGR8dCl8cGR4Z3xwZygxM3xcLShbMS04XXxjKSl8cGhpbHxwaXJlfHBsKGF5fHVjKXxwblwtMnxwbyhja3xydHxzZSl8cHJveHxwc2lvfHB0XC1nfHFhXC1hfHFjKDA3fDEyfDIxfDMyfDYwfFwtWzItN118aVwtKXxxdGVrfHIzODB8cjYwMHxyYWtzfHJpbTl8cm8odmV8em8pfHM1NVwvfHNhKGdlfG1hfG1tfG1zfG55fHZhKXxzYygwMXxoXC18b298cFwtKXxzZGtcL3xzZShjKFwtfDB8MSl8NDd8bWN8bmR8cmkpfHNnaFwtfHNoYXJ8c2llKFwtfG0pfHNrXC0wfHNsKDQ1fGlkKXxzbShhbHxhcnxiM3xpdHx0NSl8c28oZnR8bnkpfHNwKDAxfGhcLXx2XC18diApfHN5KDAxfG1iKXx0MigxOHw1MCl8dDYoMDB8MTB8MTgpfHRhKGd0fGxrKXx0Y2xcLXx0ZGdcLXx0ZWwoaXxtKXx0aW1cLXx0XC1tb3x0byhwbHxzaCl8dHMoNzB8bVwtfG0zfG01KXx0eFwtOXx1cChcLmJ8ZzF8c2kpfHV0c3R8djQwMHx2NzUwfHZlcml8dmkocmd8dGUpfHZrKDQwfDVbMC0zXXxcLXYpfHZtNDB8dm9kYXx2dWxjfHZ4KDUyfDUzfDYwfDYxfDcwfDgwfDgxfDgzfDg1fDk4KXx3M2MoXC18ICl8d2ViY3x3aGl0fHdpKGcgfG5jfG53KXx3bWxifHdvbnV8eDcwMHx4ZGEoXC18MnxnKXx5YXNcLXx5b3VyfHpldG98enRlXC0nOyAgICAgICAgIF9zaG9ydF9tYXRjaGVzID0gbmV3IFJlZ0V4cChfc2hvcnRfbWF0Y2hlcyk7ICAgICAgICAgaWYgKF9sb25nX21hdGNoZXMudGVzdCh1YSkpIHsgICAgICAgICAgICAgcmV0dXJuIDE7ICAgICAgICAgfSAgICAgICAgIHVzZXJfYWdlbnQgPSB1YS5zdWJzdHJpbmcoMCwgNCk7ICAgICAgICAgaWYgKF9zaG9ydF9tYXRjaGVzLnRlc3QodXNlcl9hZ2VudCkpIHsgICAgICAgICAgICAgcmV0dXJuIDE7ICAgICAgICAgfSAgICAgICAgIHJldHVybiAwOyAgICAgfSAgICAgPC9zY3JpcHQ+IDwvYm9keT48L2h0bWw+";
//            byte[] base64Str =   Base64.getDecoder().decode(entry);
//            logger.info("支付接口返参{}",byteArrayToStr(base64Str));
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
