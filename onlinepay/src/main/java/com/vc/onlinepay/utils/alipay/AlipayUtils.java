package com.vc.onlinepay.utils.alipay;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.cache.RedisCacheApi;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.utils.AutoFloatAmountUtil;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AlipayUtils {
    private static  Logger logger = LoggerFactory.getLogger(AlipayUtils.class);

    @Autowired
    private AutoFloatAmountUtil autoFloatAmountUtil;

    @Autowired
    private RedisCacheApi redisCacheApi;

    //AA收款链接
    public static final String AA_PAY_URL = "alipays://platformapi/startapp?appId=60000154&url=%2fwww%2findex%2fdetail.htm%3fbatchNo%3dBATCHNO%26token%3dTOKEN%26source%3dqrCode";

    //好友转账链接1
    private static final String FRIEND_ALIPAYQR = "https://render.alipay.com/p/s/i?scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FappId%3D09999988%26actionType%3DtoAccount%26goBack%3DNO%26" +
        "amount%3DORDER_AMOUNT%26userId%3DORDER_USERID%26memo%3DORDER_MEMO";
    //好友转账链接2
    /*private static final  String FRIEND_ALIPAYQR = "alipayqr://platformapi/startapp?saId=10000007&qrcode=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FappId%3D09999988%26actionType%3DtoAccount%26goBack%3DNO%26amount%3D"
                   +made.getTraAmount()+"%26userId%3D"+userId+"%26memo%3D"+made.getOrderNo()+"(姓"+appUserName+"修改信息不到账)";*/

    //String url = "alipayqr://platformapi/startapp?saId=10000007&qrcode=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FappId%3D09999988%26actionType%3DtoAccount%26goBack%3DNO%26amount%3D"+made.getTraAmount()+"%26userId%3D"+userId+"%26memo%3D"+made.getOrderNo()+"(姓"+appUserName+"修改信息不到账)";
    //private static final String ALIPAYQR = "https://render.alipay.com/p/s/i?scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FappId%3D09999988%26actionType%3DtoAccount%26goBack%3DNO%26amount%3DORDER_AMOUNT%26userId%3DORDER_USERID%26memo%3DORDER_MEMO";
    private static final String ALIPAYQR = "alipays://platformapi/startapp?appId=20000067&url=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FappId%3D09999988%26actionType%3DtoAccount%26goBack%3DNO%26amount%3DORDER_AMOUNT%26userId%3DORDER_USERID%26memo%3DORDER_MEMO";

    public static final String ALIPAY_QR_10000007 = "alipayqr://platformapi/startapp?saId=10000007&qrcode=";
    public static final String ALIPAY_HTTPS_SCHEME = "https://ds.alipay.com/?from=mobilecodec&scheme=";
    public static final String GET_ALIPAY_USER_URL = "http://online.toxpay.com/xpay/wechat/getAlipayUserId";


    //AA收款链接
    public static final int AA_TOTAL_AMOUNT = 10000;


    public static final Map<String, String> bankCodeMap = new HashMap<> ();
    static {
        bankCodeMap.put("CMBC","中国民生银行");
        bankCodeMap.put("BOC","中国银行");
        bankCodeMap.put("COMM","交通银行");
        bankCodeMap.put("CCB","中国建设银行");
        bankCodeMap.put("CNCB","中信银行");
        bankCodeMap.put("ICBC","中国工商银行");
        bankCodeMap.put("BOB","北京银行");
        bankCodeMap.put("CGB","广发银行");
        bankCodeMap.put("CMB","招商银行");
        bankCodeMap.put("CIB","兴业银行");
        bankCodeMap.put("CEB","中国光大银行");
        bankCodeMap.put("BEA","华夏银行");
        bankCodeMap.put("SPABANK","平安银行");
        bankCodeMap.put("ABC","中国农业银行");
        bankCodeMap.put("PSBC","中国邮政储蓄银行");
        bankCodeMap.put("TJBHB","天津滨海农村商业银行");
        bankCodeMap.put("FJNX","福建省农村信用社联合社");
    }

    /**
     * @描述:转账到银行卡缓存永久缓存
     * @作者:zhaoyang
     * @时间:2019/01/02
     **/
    public static String buildBankTransAllCash(String accountNo,String floatAmount,String cardNo4End) {
        return accountNo+"_"+floatAmount+"_"+cardNo4End;
    }

    /**
     * @描述:构建阿里https协议
     * @作者:nada
     * @时间:2019/4/25
     **/
    public static String buildAliHttpsSchemEncoder (String enUrl) throws UnsupportedEncodingException {
        return ALIPAY_HTTPS_SCHEME + URLEncoder.encode(ALIPAY_QR_10000007 + URLEncoder.encode(enUrl, "utf-8"), "utf-8");
    }

    /**
     * @描述:支付宝参数
     * @作者:nada
     * @时间:2019/3/3
     **/
    public static String buildAliPayPrms(String userId,String userName,BigDecimal amount,String orderNo,int funType) {
        switch (funType){
            case 7:
                String remark = orderNo +"("+ userName+")";
                return userId+"###"+remark+"###"+amount;
            case 8:
                return userId+"###"+userName+"###"+amount+"###"+orderNo;
            default:
                remark = orderNo +"("+ userName+")";
                return userId+"###"+remark+"###"+amount;
        }
    }

    /**
     * @描述:转账到银行卡缓存key
     * @作者:zhaoyang
     * @时间:2019/01/02
     **/
    public static String buildBankTransKey(String accountNo,String floatAmount) {
        floatAmount = new BigDecimal(floatAmount).setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
        return accountNo+"_"+floatAmount;
    }

    public static String biuldFriendAlipayUrl(VcOnlineOrderMade made,String userId,String appUserName){
        try {
            String memo = made.getOrderNo()+"(姓"+appUserName+"修改信息不到账)";
            return FRIEND_ALIPAYQR.replace("ORDER_AMOUNT",String.valueOf(made.getTraAmount())).
                replace("ORDER_USERID",userId).
                replace("ORDER_MEMO", Constant.getURLEncode(memo));
        } catch (Exception e) {
            logger.error("获取连接失败",e);
            return "";
        }
    }

    /**
     * @描述:AA收款链接抓换
     **/
    public static String buildAAPayUrl (String batchNo,String token) {
        try {
            return AlipayUtils.AA_PAY_URL.replace ("BATCHNO",batchNo).replace ("TOKEN",token);
        } catch (Exception e) {
            logger.error (" 支付宝AA收款获取连接失败", e);
            return "";
        }
    }

    /**
     * @描述:转账到银行卡缓存永久缓存
     * @作者:zhaoyang
     * @时间:2019/01/02
     **/
    public static String buildBankTransAllCash(String accountNo,String floatAmount) {
        return "cardNo4Endall"+accountNo+"_"+floatAmount;
    }

    /**
     * @描述:获取缓存支付key
     * @作者:nada
     * @时间:2019/1/4
     **/
    /*private String getAutoLoopBankAmount(String accountNo,String amount,String orderNo,long cacheTime){
        String  autoAmount = autoFloatAmountUtil.getAutoAmount(accountNo,amount);
        autoAmount = autoFloatAmountUtil.amountFormat (autoAmount);
        String bankTransKey = accountNo+"_"+autoAmount;
        if(redisCacheApi.exists (bankTransKey) && redisCacheApi.get (bankTransKey)!=null){
            return "";
        }else{
            logger.info ("银行卡转账信息,orderNo:{},accountNo:{},amount:{},autoAmount:{}",orderNo,accountNo,amount,autoAmount);
            redisCacheApi.set (bankTransKey,orderNo,cacheTime);
            return autoAmount;
        }
    }*/

    /**
     * @描述:支付宝构建转账到银行卡参数
     * @时间:2019/01/02
     **/
    public static String buildBankPayPrms (String accountName,String cardNo,String bankMark,BigDecimal amount) {
        return accountName + "###" + cardNo + "###" + bankMark + "###" + amount;
    }

    /**
     * @描述:支付宝构建转账到银行卡参数
     * @时间:2019/01/02
     **/
    public static String buildBankPayPrms (String accountName,String cardNo,String bankMark,String amount,String cardIdx,String bankRemarks) {
        return accountName + "###" + cardNo + "###" + bankMark + "###" + amount+ "###" + cardIdx+ "###" + bankRemarks;
    }

    /**
     * @描述:支付宝好友转账兼容IOS
     * @作者:nada
     * @时间:2018/12/12
     **/
    public static String biuldAlipayUrl (VcOnlineOrderMade made, String userId, String appUserName) {
        try {
            String memo = made.getOrderNo () + "(" + appUserName + ")";
            return ALIPAYQR.replace ("ORDER_AMOUNT", String.valueOf (made.getTraAmount ())).replace ("ORDER_USERID", userId).replace ("ORDER_MEMO", Constant.getURLEncode (memo));
        } catch (Exception e) {
            logger.error (" 支付宝好友转账获取连接失败", e);
            return "";
        }
    }

    /**
     * @描述:支付宝好友转账兼容IOS
     * @作者:nada
     * @时间:2018/12/12
     **/
    public static String biuldAlipayUrl (BigDecimal amount,String orderNo,String userId, String appUserName) {
        try {
            String memo = orderNo + "(" + appUserName + ")";
            return ALIPAYQR.replace ("ORDER_AMOUNT", amount.toString ()).replace ("ORDER_USERID", userId).replace ("ORDER_MEMO", Constant.getURLEncode (memo));
        } catch (Exception e) {
            logger.error (" 支付宝好友转账获取连接失败", e);
            return "";
        }
    }

    /**
     * 支付宝链接兼容IOS处理
     */
    public static String biuldAlipayUrl (String qrcodeUrl) {
        try {
            String shortUrl = Constant.getDwzShortUrl (qrcodeUrl);
            shortUrl = Constant.getURLEncode (shortUrl);
            String head = "https://render.alipay.com/p/s/i?scheme=";
            String body = "alipays://platformapi/startapp?saId=10000007&qrcode=";
            String payUrl = head + Constant.getURLEncode (body + shortUrl);
            return payUrl;
        } catch (Exception e) {
            return qrcodeUrl;
        }
    }

    /**
     * 支付宝链接H5处理
     */
    public static String biuldH5AlipayUrl (String payUrl,String baseUrl) {
        try {
            String alipayUrl = StringEscapeUtils.unescapeJava (payUrl);
            return alipayUrl.replace("https://qr.alipay.com/",baseUrl+"alipayQr/");
        } catch (Exception e) {
            return payUrl;
        }
    }

    /**
     * @描述:AA收款链接抓换
     **/
    public static String buildAAPayUrl (String payUrl) {
        try {
            JSONObject payJson= JSONObject.parseObject(payUrl);
            return AlipayUtils.AA_PAY_URL.replace ("BATCHNO", payJson.getString("batchNo")).replace ("TOKEN", payJson.getString("token"));
        } catch (Exception e) {
            logger.error (" 支付宝AA收款获取连接失败", e);
            return "";
        }
    }

    /**
     * @描述:红包转账
     * @作者:nada
     * @时间:2019/3/3
     **/
    public static String getAlipayRedUrl(String key,String userId,String upMerchNo,String orderNo, BigDecimal amount) {
        if("addFriendUrl".equals (key)){
            String addFriendUrl = "alipays://platformapi/startapp?appId=20000167&tUserId="+userId+"&tUserType=1&tLoginId="+upMerchNo+"";
            return  addFriendUrl;
        }else if("addFriendUrlS".equals (key)){
            String addFriendUrlS = "alipays://platformapi/startapp?appId=20000186&actionType=addfriend&source=by_persnal_feed&userId="+userId+"&loginId="+upMerchNo+"&appClearTop=ture";
            return  addFriendUrlS;
        }else if("redPayUrl".equals (key)){
            String redPayUrl = "alipays://platformapi/startapp?appId=88886666&target=personal&schemaMode=portalInside&prevBiz=chat&chatUserId="+userId+"&chatUserType=1&amount="+amount+"&remark="+orderNo+"";
            return redPayUrl;
        }else{
            return "";
        }
    }

    /**
     * @描述:转账到银行卡
     **/
    public static String buildBankPayUrl (String cardIndex,String bankRemarks,String bankMark,String accountName,String amount) {
        try {
            String bankName = "";
            if(bankCodeMap.containsKey (bankMark) && StringUtil.isNotEmpty (bankCodeMap.get (bankMark))){
                bankName = bankCodeMap.get (bankMark);
            }
            StringBuffer bankUrl = new StringBuffer ();
            bankUrl.append ("alipays://platformapi/startapp?appId=09999988&actionType=toCard&sourceId=bill&cardNoHidden=true&");
            bankUrl.append ("cardIndex=").append (cardIndex.trim()).append ("&");
            bankUrl.append ("cardNo=").append (bankRemarks.trim()).append ("&");
            bankUrl.append ("bankMark=").append (bankMark.trim()).append ("&");
            bankUrl.append ("bankName=").append (URLEncoder.encode (bankName.trim(), "UTF-8")).append ("&");
            bankUrl.append ("bankAccount=").append (URLEncoder.encode (accountName.trim(), "UTF-8")).append ("&");
            bankUrl.append ("receiverName=").append (URLEncoder.encode (accountName.trim(), "UTF-8")).append ("&");
            bankUrl.append ("amount=").append (amount.trim()).append ("&");
            bankUrl.append ("money=").append (amount.trim()).append ("&");
            bankUrl.append ("orderSource=").append ("HISTORY").append ("&");
            bankUrl.append ("cardChannel=").append ("HISTORY_CARD");
            return  bankUrl.toString ();
        } catch (Exception e) {
            logger.error (" 支付宝AA收款获取连接失败", e);
            return "";
        }
    }

}

