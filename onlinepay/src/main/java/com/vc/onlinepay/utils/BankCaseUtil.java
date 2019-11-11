package com.vc.onlinepay.utils;

import com.alibaba.fastjson.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;

/**
 *  内容提取
 * @author shadow
 */
public class BankCaseUtil {

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(BankCaseUtil.class);

    public static SimpleDateFormat bankformat = new SimpleDateFormat("yyyy年MM月DD日HH:mm");

    private static  SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
    private static  SimpleDateFormat monthsf = new SimpleDateFormat("MM");

    //翼支付
    public static final Pattern YZF_Amount = Pattern.compile("到账(.*?)元");

    //平安银行
    public static final Pattern PAB_Card4End = Pattern.compile("您尾号(.*?)的账户");
    public static final Pattern PAB_Amount = Pattern.compile("转入人民币(.*?)元");
    public static final Pattern PAB_Time = Pattern.compile("账户于(.*?)付款业务");
    public static final Pattern PAB_Time1 = Pattern.compile("账户于(.*?)银联入账转入");

    //邮储银行
    public static final Pattern PSBC_Card4End = Pattern.compile("您尾号(.*?)账户");
    public static final Pattern PSBC_Amount = Pattern.compile("账户提现金额(.*?)元");
    public static final Pattern PSBC_Time = Pattern.compile("19年(.*?)您尾号");

    //工商银行
    public static final Pattern ICBC_Card4End = Pattern.compile("您尾号(.*?)卡");
    public static final Pattern ICBC_Amount = Pattern.compile("\\)(.*?)元，");
    public static final Pattern ICBC_Time = Pattern.compile("卡(.*?):");

    //农业银行
    public static final Pattern ABC_Card4End = Pattern.compile("您尾号(.*?)账户");
    public static final Pattern ABC_Amount = Pattern.compile("交易人民币(.*?)，");
    public static final Pattern ABC_Time = Pattern.compile("于(.*?)向您尾号");
    public static final Pattern ABC_Time2 = Pattern.compile("账户(.*?)完成");

    //民生银行
    public static final Pattern CMBC_Card4End = Pattern.compile("账户\\*(.*?)于");
    public static final Pattern CMBC_Amount = Pattern.compile("存入￥(.*?)元，");
    public static final Pattern CMBC_Time = Pattern.compile("于(.*?)存入");

    //中国银行
    public static final Pattern BOC_Card4End = Pattern.compile("账户(.*?)，");
    public static final Pattern BOC_Amount = Pattern.compile("人民币(.*?)元，");
    public static final Pattern BOC_Time = Pattern.compile("于(.*?)日");

    //兴业银行
    public static final Pattern CIB_Card4End = Pattern.compile("账户\\*(.*?)\\*");
    public static final Pattern CIB_Amount = Pattern.compile("收入(.*?)元，");
    public static final Pattern CIB_Time = Pattern.compile("(.*?)账户");

    //建设银行
    public static final Pattern CCB_Card4End = Pattern.compile("尾号(.*?)的");
    public static final Pattern CCB_Amount = Pattern.compile("人民币(.*?)元");
    public static final Pattern CCB_Time = Pattern.compile("账户(.*?)分");

    public static void main (String[] args) {
//        String card4EndNo = "95511";
//        String smsContent = "【平安银行】您尾号9999的账户于1月23日13:45付款业务转入人民币500.00元,存款账户余额人民币49147.11元。详询95511-3";
        String card4EndNo = "95533";
        String smsContent = "您尾号7141的储蓄卡账户5月9日20时42分支付机构提现收入人民币400.02元,活期余额93471.91元。[建设银行]";
//        String card4EndNo = "95588";
//        String smsContent = "【工商银行】您尾号5196卡1月22日23:58快捷支付收入(张海龙支付宝转账支付宝)5元，余额9.60元。";
//        String card4EndNo = "95599";
//        String smsContent = "【中国农业银行】您尾号6970账户01月26日14:08完成代付交易人民币20.00，余额47638.71";
//        String card4EndNo = "95561";
//        String smsContent = "【邮储银行】19年04月19日17:32您尾号959账户提现金额2000.03元，余额4000.07元。";
        JSONObject bankInfo  = getBankInfo (card4EndNo,smsContent);
        System.out.println (bankInfo);
    }

    /**
     * @描述:解析银行卡信息
     * @作者:nada
     * @时间:2019/4/17
     **/
    public static JSONObject getBankInfo(String card4EndNo, String smsContent){
        JSONObject bankInfo = new JSONObject();
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = (calendar.get(Calendar.MONTH) + 1)%12;
            String amount = "";
            String time = "";
            String parseTime = "";
            switch(card4EndNo){
                case "95533":
                    //您尾号6744的储蓄卡账户4月20日19时6分银联入账收入人民币3000.01元,活期余额16047.00元。[建设银行]
                    //您尾号7141的储蓄卡账户5月9日20时42分支付机构提现收入人民币400.02元,活期余额93471.91元。[建设银行]
                    amount = BankCaseUtil.getContent(smsContent, BankCaseUtil.CCB_Amount);
                    card4EndNo = BankCaseUtil.getContent(smsContent, BankCaseUtil.CCB_Card4End);
                    parseTime = BankCaseUtil.getContent(smsContent,BankCaseUtil.CCB_Time);
                    time = year + "年" + parseTime;
                    bankInfo.put ("amount",amount);
                    bankInfo.put ("card4EndNo",card4EndNo);
                    bankInfo.put ("time",time.replace ("时",":"));
                    return  bankInfo;
                case "95561":
                    //18日15:53账户*8731*汇款汇入收入250.00元，余额250.00元。对方户名:张荣达[兴业银行]
                    //23日00:09账户*8221*网联付款收入105.00元，余额19098.75元[兴业银行]
                    //18日15:53账户*8731*汇款汇入收入250.00元，余额250.00元。对方户名:张荣达[兴业银行]
                    amount = BankCaseUtil.getContent(smsContent, BankCaseUtil.CIB_Amount);
                    card4EndNo = BankCaseUtil.getContent(smsContent, BankCaseUtil.CIB_Card4End);
                    parseTime = BankCaseUtil.getContent(smsContent,BankCaseUtil.CIB_Time);
                    time = year + "年" + monthsf.format(new Date ())+"月"+parseTime;
                    bankInfo.put ("amount",amount);
                    bankInfo.put ("card4EndNo",card4EndNo);
                    bankInfo.put ("time",time);
                    return  bankInfo;
                case "95566":
                    //【中国银行】 您的借记卡账户4639，于04月18日手机银行支取(跨行支付)人民币30.00元，交易后余额6.19【中国银行】
                    amount = BankCaseUtil.getContent(smsContent, BankCaseUtil.BOC_Amount);
                    card4EndNo = BankCaseUtil.getContent(smsContent, BankCaseUtil.BOC_Card4End);
                    parseTime = BankCaseUtil.getContent(smsContent,BankCaseUtil.BOC_Time);
                    time = year + "年" + parseTime+"日"+sf.format(new Date ());
                    bankInfo.put ("amount",amount);
                    bankInfo.put ("card4EndNo",card4EndNo);
                    bankInfo.put ("time",time);
                    return  bankInfo;
                case "95568":
                    //【民生银行】账户*1546于04月18日16:16存入￥3000.00元，可用余额3000.97元。存款。【民生银行】
                    //账户*6804于01月22日10:43存入￥500.00元，付方支付宝（中国）网络技术有限公司，可用余额40852.20元。1-1-1-1-支付宝（中国）网络技术有限公司。【民生银行】
                    //账户*2140于01月24日12:19存入￥500.00元，可用余额3109.08元。奚晓亚支付宝转账-奚晓亚支付宝转账-支付宝（中国）网络技术有限公司。【民生银行】
                    amount = BankCaseUtil.getContent(smsContent, BankCaseUtil.CMBC_Amount);
                    card4EndNo = BankCaseUtil.getContent(smsContent, BankCaseUtil.CMBC_Card4End);
                    parseTime = BankCaseUtil.getContent(smsContent,BankCaseUtil.CMBC_Time);
                    time = year + "年" + parseTime;
                    bankInfo.put ("amount",amount);
                    bankInfo.put ("card4EndNo",card4EndNo);
                    bankInfo.put ("time",time);
                    return  bankInfo;
                case "95599":
                    //【中国农业银行】张荣达于04月18日16:08向您尾号3912账户完成转存交易人民币328.00，余额328.00。
                    //【中国农业银行】支付宝（中国）网络技术有限公司于01月22日23:49向您尾号9769账户完成代付交易人民币10.00，余额10.07
                    //【中国农业银行】支付宝（中国）网络技术有限公司于01月25日20:42向您尾号4272账户完成代付交易人民币100.01，余额296.04。
                    //【中国农业银行】您尾号6970账户01月26日14:08完成代付交易人民币20.00，余额47638.71。
                	//【中国农业银行】您尾号9376账户05月08日15:16完成代付交易人民币1000.01，余额1586.24。
                    amount = BankCaseUtil.getContent(smsContent, BankCaseUtil.ABC_Amount);
                    card4EndNo = BankCaseUtil.getContent(smsContent, BankCaseUtil.ABC_Card4End);
                    parseTime = BankCaseUtil.getContent(smsContent,BankCaseUtil.ABC_Time);
                    if(parseTime == null || "".equals(parseTime)) {
                    	parseTime = BankCaseUtil.getContent(smsContent,BankCaseUtil.ABC_Time2);
                    }
                    time = year + "年" + parseTime;
                    bankInfo.put ("amount",amount);
                    bankInfo.put ("card4EndNo",card4EndNo);
                    bankInfo.put ("time",time);
                    return  bankInfo;
                case "95588":
                    //【工商银行】您尾号5196卡1月22日23:58快捷支付收入(张海龙支付宝转账支付宝)5元，余额9.60元。
                    //【工商银行】您尾号6073卡4月18日15:39工商银行收入(他行汇入)300元，余额300元。
                    amount = BankCaseUtil.getContent(smsContent, BankCaseUtil.ICBC_Amount);
                    amount = amount.replace(",", "");
                    card4EndNo = BankCaseUtil.getContent(smsContent, BankCaseUtil.ICBC_Card4End);
                    parseTime = BankCaseUtil.getContent(smsContent,BankCaseUtil.ICBC_Time);
                    if(StringUtil.isNotEmpty (parseTime) && smsContent.indexOf (parseTime) >0){
                        int timerIndex = smsContent.indexOf (parseTime)+parseTime.length ();
                        parseTime =  parseTime + smsContent.substring (timerIndex,timerIndex+3);
                    }
                    time = year + "年" + parseTime;
                    bankInfo.put ("amount",amount);
                    bankInfo.put ("card4EndNo",card4EndNo);
                    bankInfo.put ("time",time);
                    return  bankInfo;
                case "95580":
                    //【邮储银行】19年04月19日17:32您尾号959账户提现金额2000.03元，余额4000.07元。
                    //【邮储银行】19年04月17日12:56您尾号188账户提现金额10.30元，余额10.81元。
                    amount = BankCaseUtil.getContent(smsContent, BankCaseUtil.PSBC_Amount);
                    card4EndNo = BankCaseUtil.getContent(smsContent, BankCaseUtil.PSBC_Card4End);
                    time = year + "年" + BankCaseUtil.getContent(smsContent,BankCaseUtil.PSBC_Time);
                    bankInfo.put ("amount",amount);
                    bankInfo.put ("card4EndNo",card4EndNo);
                    bankInfo.put ("time",time);
                    return  bankInfo;
                case "95511":
                    //【平安银行】您尾号4927的账户于4月18日11:32银联入账转入人民币4999.97元,存款账户余额人民币8805.38元。详询95511-3
                    //【平安银行】您尾号9999的账户于1月23日13:45付款业务转入人民币500.08元,存款账户余额人民币49147.11元。详询95511-3
                    amount = BankCaseUtil.getContent(smsContent, BankCaseUtil.PAB_Amount);
                    card4EndNo = BankCaseUtil.getContent(smsContent, BankCaseUtil.PAB_Card4End);
                    parseTime = BankCaseUtil.getContent(smsContent,BankCaseUtil.PAB_Time);
                    if(StringUtil.isEmpty (parseTime)){
                        parseTime = BankCaseUtil.getContent(smsContent,BankCaseUtil.PAB_Time1);
                    }
                    time = year + "年" + parseTime;
                    bankInfo.put ("amount",amount);
                    bankInfo.put ("card4EndNo",card4EndNo);
                    bankInfo.put ("time",time);
                    return  bankInfo;
                case "95106":
                    //【翼支付】钱到啦到账1.02元。如有疑问，请致电95106
                    amount = BankCaseUtil.getContent(smsContent, BankCaseUtil.YZF_Amount);
                    bankInfo.put ("amount",amount);
                    bankInfo.put ("card4EndNo","95106");
                    bankInfo.put ("gmtCreate",System.currentTimeMillis ());
                    return  bankInfo;
                default:
                    return bankInfo;
            }
        } catch (Exception e) {
            logger.error ("解析银行卡信息异常{}",smsContent,e);
        }finally {
            try {
                if(bankInfo !=null && !bankInfo.isEmpty () && bankInfo.containsKey ("time")){
                    long gmtCreate = bankformat.parse(bankInfo.getString ("time")).getTime();
                    bankInfo.put ("gmtCreate",gmtCreate);
                }
            } catch (ParseException e) {
                bankInfo.put("parseException","parseException");
                logger.error ("解析银行卡日期异常{}",smsContent,e);
            }
        }
        return bankInfo;
    }

    /**
     * @描述:获取正则内容
     * @作者:nada
     * @时间:2019/4/17
     **/
    public static String getContent(String content, Pattern pattern){
        StringBuffer sb = new StringBuffer();
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            int i = 1;
            sb.append(m.group(i));
            i++;
        }
        return sb.toString();
    }

}
