package com.vc.onlinepay.utils;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.http.HttpClientTools;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constant {
    private static Logger logger = LoggerFactory.getLogger(Constant.class);
    
    public static final SimpleDateFormat ymdhms = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat sdf = new SimpleDateFormat ("MMddHHmmss");
    public static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final  BigDecimal zeroDecimal = new BigDecimal("0");
    public static final  BigDecimal oneDecimal = new BigDecimal("1");
    public static final  BigDecimal oneHundredDecimal = new BigDecimal("100");

    //回调返回上游的字符串
    public static final String RES_SUCCESS ="SUCCESS";
    public static final String res_FAILED="FAILED";
    public static final String RES_ERROR ="ERROR";
    public static final String res_OK="OK";

    /* 成功码 */
    public static final String SUCCESSS="10000";
    /* 失败码 */
    public static final String FAILED="10001";
    /* 异常码 */
    public static final String ERROR="10003";
    /* 受理中 */
    public static final String UNKNOW="10004";

    /**
     * 微信:0002 //支付宝:0010 //QQ钱包:0015    //京东钱包:010700 //银联钱包:010800
     */
    public static final String service_weixin="0002";
    public static final String service_alipay="0010";
    public static final String service_qq="0015";
    public static final String service_jd="010700";
    public static final String service_yl="010800";

    //编码
    public static String CHART_UTF ="UTF-8";

    private static final String BASESTR = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String BAIDU_URL = "https://dwz.cn/admin/v2/create";

    //自定义状态
    public static JSONObject customMsg(String code, String msg){
        return getCommonResJson(code,msg);
    }

    /**
     * @desc 获取金额
     * @author nada
     * @create 2019/6/14 10:31
     */
    public static String format2amount(BigDecimal amount){
        if(amount == null){
            return new BigDecimal("0").setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
        }
        return amount.setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
    }

    /**
     * @return
     * @since JDK 1.8
     */
    public static JSONObject commonError(String msg){
        JSONObject resJson = new JSONObject();
        resJson.put("code",Constant.FAILED);
        resJson.put("msg", msg);
        return resJson;
    }

    /**
     * @描述:0-1之间的数
     * @作者:nada
     * @时间:2019/4/9
     **/
    public static boolean zeroAndOneDecimal(BigDecimal rate) {
        try {
            return rate.compareTo(Constant.zeroDecimal) > 0 && rate.compareTo(Constant.oneDecimal) < 0;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * @描述: 是否响应成功
     * @作者:nada
     * @时间:2019/3/18
     **/
    public static boolean isOkResult (JSONObject result) {
        try {
            if(result == null){
                return false;
            }
            if(!result.containsKey ("code")){
                return false;
            }
            if (!result.getString ("code").equals (Constant.SUCCESSS)){
                logger.error ("响应结果失败:{}",result);
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace ();
            return  false;
        }
    }

    /**
     * unicode转中文
     * @param str
     * @return
     * @author yutao
     * @date 2017年1月24日上午10:33:25
     */
    private static Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
    public static String unicodeToString(String str) {
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch+"" );
        }
        return str;
    }

    /**
     * @描述:异常通用json
     * @作者:nada
     * @时间:2018年3月5日 下午4:37:12
     */
    public static JSONObject exception(String msg){
        JSONObject resJson = new JSONObject();
        resJson.put("code",Constant.FAILED);
        if(StringUtil.isEmpty(msg)){
            resJson.put("msg", "处理异常");
        }else{
            resJson.put("msg", msg);
        }
        return resJson;
    }


    public static  JSONObject stringToJson(String str) throws Exception{
        JSONObject jsonObject = new JSONObject();
        return JSONObject.parseObject(str);
    }

    public static String getAutoOrderNo() {
        String str = sdf.format (new Date ());
        String s = String.valueOf (System.nanoTime ());
        StringBuffer orderNo = new StringBuffer ();
        orderNo.append (str);
        orderNo.append (s.substring (s.length () - 6));
        return orderNo.toString ();
    }


    /**
     * 生成32位编码
     * @return string
     */
    public static String getNetOrder(BigDecimal amount){
        StringBuffer order = new StringBuffer();
        order.append (Constant.format2BigDecimal (amount)).append ("&").append (UUID.randomUUID().toString().trim().replaceAll("-", ""), 10, 24);
        return order.toString ();
    }

    /**
     * 生成32位编码
     * @return string
     */
    public static String getUUid(){
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

    public static String getDateString() {
        return DatePattern.PURE_DATETIME_FORMAT.format(new Date());
    }


    /**
     * @param length 表示生成字符串的长度
     * @return 随机字符
     */
    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(BASESTR.length());
            sb.append(BASESTR.charAt(number));
        }
        return sb.toString();
    }

    public static boolean isNumberChars(String text){
        String pattern = "[0-9]*";
        return text.matches(pattern);
    }

    public static boolean isMoney(String text){
        String pattern = "(^[1-9]([0-9]+)?(\\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9])?$)";
        return text.matches(pattern);
    }

    public static boolean isIdcard(String text){
        text = StringUtils.deleteWhitespace(text);
        String pattern = "([1-9]{1})([0-9]{13,16})([0-9xX]{1})";
        return text.matches(pattern);
    }


    /**
     * 检查可用时间限制 （兼容方法）
     * @return startTime = "9:10" endTime = "21:40"
     * @example startTime = "23:50" endTime = "00:10"
     * @throws
     */
    @SuppressWarnings("all")
    public static Boolean checkRfTime(String startTime,String endTime) throws IllegalArgumentException {
        try {
            if(StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)){
                return true;
            }
            if("0".equals(startTime) || "0:00".equals(startTime) || "00:00".equals(startTime) || startTime.length() <3
                    || "0".equals(endTime) || "0:00".equals(endTime) || "00:00".equals(endTime) || endTime.length() <3 ){
                return true;
            }
            SimpleDateFormat HHMM = new SimpleDateFormat("HH:mm");
            Date start = HHMM.parse(startTime);
            Date end   = HHMM.parse(endTime);
            if(end.getTime() < start.getTime()){
                return !checkRfTime(endTime,startTime);
            }
            return isEffectiveDate(HHMM.parse(HHMM.format(new Date())),start,end);
        } catch (Exception e) {
            logger.error("检查可用时间限制异常.",e);
        }
        return true;
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        try {
            if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
                return true;
            }
            Calendar date = Calendar.getInstance();
            date.setTime(nowTime);
            Calendar begin = Calendar.getInstance();
            begin.setTime(startTime);
            Calendar end = Calendar.getInstance();
            end.setTime(endTime);
            return date.after(begin) && date.before(end);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isEffectiveTimeNow(String timeIntervalStr){
        if(StringUtil.isEmpty(timeIntervalStr) || !timeIntervalStr.contains("-") || timeIntervalStr.contains("00:00")){
            return false;
        }
        String[] time = timeIntervalStr.split("-");
        return Constant.checkRfTime(time[0], time[1]);
    }
    /**
     * @描述:获取收款真实金额
     * @作者:Alan
     * @时间:2017年7月3日 下午7:54:35
     */
    public static BigDecimal getActualMoney(BigDecimal payMoney, BigDecimal tranRate) {
        try {
            BigDecimal taxRate = tranRate.divide(new BigDecimal(100));// 除100
            BigDecimal rate = new BigDecimal(1);
            rate = rate.subtract(taxRate);
            return  payMoney.multiply(rate);
        } catch (Exception e) {
            logger.error("获取收款真实金额异常",e);
            return payMoney;
        }
    }

    //通用返回
    private static JSONObject getCommonResJson(String code, String msg){
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg",msg);
        return result;
    }
    //失败返回
    public static JSONObject failedMsg(String msg){
        return getCommonResJson(Constant.FAILED,msg);
    }
    //失败返回
    public static JSONObject successMsg(String msg){
        return getCommonResJson(Constant.SUCCESSS,msg);
    }
    //解析字符串，按照###分割获取参数
    public static String getChannelKeyDes(String channelKeyDes,int index){
        try {
            channelKeyDes = StringUtils.deleteWhitespace(channelKeyDes);
            String[] arry = channelKeyDes.split("###");
            if(arry.length < 1){
                return "";
            }
            if(index > arry.length){
                return "";
            }
            return arry[index];
        } catch (Exception e) {
            e.printStackTrace ();
            return "";
        }
    }

    /**
     * @描述:获取模式
     * @时间:2018年6月13日 下午6:03:49
     */
    public static int getMode(String mode){
        int cashMode = 0;
        if("T0".equals(mode)){
            cashMode = 1;
        }else{
            cashMode = 2;
        }
        return cashMode;
    }
    /**
     * @描述:拼接网址
     * @时间:2018/10/15 11:50
     */
    public static String buildUrl(String ... urls){
        StringBuilder sb = new StringBuilder();
        for(String url : urls){
            String filledUrl = sb.toString();
            if(url.contains("\\")){url = url.replace("\\","");}
            if(url.endsWith("/")){url = url.substring(0,url.length()-1);}
            if(StringUtil.isEmpty(url)){continue;}
            if(StringUtil.isEmpty(filledUrl)){sb.append(url);continue;}
            if(!url.startsWith("/")){sb.append("/");}
            sb.append(url);
        }
        return sb.toString();
    }
    /**
     * @描述: 交易金额限制
     * @参数:amount 金额 minAmount maxAmount,
     * @参数:needInt (0 不限制,1 整数,2 非整数) excluded 特殊金额  badFoot 个位数限制
     * @时间:2018/10/22 11:40
     */
    public static boolean checkAmount(String amount,int needInt,String minAmount,String maxAmount,String excluded,String badFoot){
        if(amount.endsWith(".00") || amount.endsWith(".0")){
            amount = amount.substring(0,amount.indexOf("."));
        }
        if(StringUtils.isNotEmpty(excluded) && excluded.contains(amount)){
            System.out.println("特殊金额");
            return true;
        }
        BigDecimal oriAmount = new BigDecimal(amount).setScale(2,BigDecimal.ROUND_HALF_DOWN);
        BigDecimal intAmount = new BigDecimal(amount).setScale(0,BigDecimal.ROUND_HALF_DOWN);
        boolean isInt = intAmount.compareTo(oriAmount)==0;
        if(needInt == 1 && !isInt){
            System.out.println("必须整数");
            return false;
        }
        if(needInt == 2 && isInt){
            System.out.println("必须非整数");
            return false;
        }
        if(needInt == 1 && StringUtils.isNotEmpty(badFoot)){
            String footer = amount.substring(amount.length()-1);
            if(badFoot.contains(footer)){
                System.out.println("金额尾数限制");
                return false;
            }
        }
        if(StringUtils.isNotEmpty(minAmount)){
            BigDecimal min = new BigDecimal(minAmount).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            return oriAmount.compareTo(min)!=-1;
        }
        if(StringUtils.isNotEmpty(maxAmount)){
            BigDecimal max = new BigDecimal(maxAmount).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            return oriAmount.compareTo(max)!=1;
        }
        return true;
    }


    public static String getURLEncode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text,CHART_UTF);
    }

    /**
     * 统计字符串出现的次数
     * @param str
     * @param key
     * @return
     */
    public static int getSubCountStr(String str, String key) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(key, index)) != -1) {
            index = index + key.length();
            count++;
        }
        return count;
    }

    /**
     * 获取百度短连接
     * @param qrcodeUrl
     * @return
     */
    public static String getDwzShortUrl(String qrcodeUrl){
        try {
            JSONObject prams = new JSONObject();
            prams.put("url", qrcodeUrl);
            Map<String, String> header = new HashMap<>();
            header.put("token","63933c0b1fc8edc9f204166bad09b2f4");
            String response = HttpClientTools.sendPost(BAIDU_URL, prams.toString(), "application/json",header);
            JSONObject jsonObject = JSONObject.parseObject(response);
            System.out.println("jsonObject = " + jsonObject);
            if (!"0".equals(jsonObject.getString("Code"))) {
                return qrcodeUrl;
            }
            String shortUrl = jsonObject.getString("ShortUrl");
            return shortUrl;
        }catch (Exception e){
            return qrcodeUrl;
        }
    }

    /**
     * 获取短连接
     * @param qrcodeUrl
     * @return
     */
    public static String getShortUrl(String qrcodeUrl){
        try {
            String urlencode = getURLEncode(qrcodeUrl);
            String BAIDU_URL ="http://suo.nz/api.php?url="+urlencode+"&key=5d1862e391d2c4049bf82aa5@3a26811856a6362a190f4b82fd2a3c6f&expireDate=2019-07-03";
            return HttpClientTools.sendUrlGet(BAIDU_URL,"");
        }catch (Exception e){
            e.printStackTrace();
            return qrcodeUrl;
        }
    }

    public static void main (String[] args) {
        try {
            String url = "http://www.baidu.com";
            String response = getShortUrl(url);
            System.out.println (response);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    /**
     * @描述: 保留2位小数，非四舍五入
     * @作者:nada
     * @时间:2018/12/25
     **/
    public static BigDecimal format2BigDecimal (BigDecimal amount) {
        return amount.setScale(2,BigDecimal.ROUND_HALF_DOWN);
    }

    /**
     *
     * 将元为单位的转换为分 （乘100）
     */
    public static BigDecimal changeBranch(BigDecimal amount) {
        return amount.multiply (Constant.oneHundredDecimal);
    }
}
