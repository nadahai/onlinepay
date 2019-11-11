package com.vc.onlinepay.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shadow
 * 通用工具类:
 *      >过滤非法字符
 *      >检查IP地址
 */
public class FilterUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FilterUtils.class);

    private static final String IPV4_SPLIT_SYMBOL=".";

    public static boolean checkIP(String ip){
        if(null == ip || "".equals(ip.trim())){
            LOG.error("传入IP为空！");
            return false;
        }
        if(ip.contains(IPV4_SPLIT_SYMBOL)){
            return checkIPV4(ip);
        }
        return checkIPV6(ip);
    }

    /**
     * 检查内网IP
     * @param ip
     * @return
     */
    private static final Pattern IPV4_INNER_PATTERN = Pattern.compile("((192\\.168|172\\.([1][6-9]|[2]\\d|3[01]))(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}|^(\\D)*10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3})");
    private static final Pattern IPV4_PATTERN=Pattern.compile("^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$");
    private static boolean checkIPV4(String ip) {
        Matcher m = IPV4_PATTERN.matcher(ip);
        if(!m.matches()){
            return false;
        }
        return !IPV4_INNER_PATTERN.matcher(ip).find();
    }

    private static String IPV6_REGEX_STR = "(^((([0-9A-Fa-f]{1,4}:){7}(([0-9A-Fa-f]{1,4}){1}|:))"
            .concat("|(([0-9A-Fa-f]{1,4}:){6}((:[0-9A-Fa-f]{1,4}){1}|")
            .concat("((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|")
            .concat("([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|")
            .concat("[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|")
            .concat("(([0-9A-Fa-f]{1,4}:){5}((:[0-9A-Fa-f]{1,4}){1,2}|")
            .concat(":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|")
            .concat("([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|")
            .concat("[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|")
            .concat("(([0-9A-Fa-f]{1,4}:){4}((:[0-9A-Fa-f]{1,4}){1,3}")
            .concat("|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|")
            .concat("([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|")
            .concat("([0-9]){1,2})){3})|:))|(([0-9A-Fa-f]{1,4}:){3}((:[0-9A-Fa-f]{1,4}){1,4}|")
            .concat(":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|")
            .concat("([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|")
            .concat("[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|")
            .concat("(([0-9A-Fa-f]{1,4}:){2}((:[0-9A-Fa-f]{1,4}){1,5}|")
            .concat(":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|")
            .concat("([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|")
            .concat("[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))")
            .concat("|(([0-9A-Fa-f]{1,4}:){1}((:[0-9A-Fa-f]{1,4}){1,6}")
            .concat("|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|")
            .concat("([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|")
            .concat("[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|")
            .concat("(:((:[0-9A-Fa-f]{1,4}){1,7}|(:[fF]{4}){0,1}:((22[0-3]|2[0-1][0-9]|")
            .concat("[0-1][0-9][0-9]|([0-9]){1,2})")
            .concat("([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:)))$)");
    private static final Pattern IPV6_PATTERN = Pattern.compile(IPV6_REGEX_STR);
    private static boolean checkIPV6(String ip) {
        Matcher matcher = IPV6_PATTERN.matcher(Normalizer.normalize(ip, Normalizer.Form.NFKC));
        return matcher.matches();
    }
    private static final List<String> FILTER_CHARACTER_LIST = new ArrayList<String>(){{
        add("javascript");
        add("union ");
        add("concat(");
        add("convert(");
        add("upper(");
        add("sleep(");
        add("order by");
        add(" where ");
        add("exists");
        add("'%");
        add("\"%");
        add("%'");
        add("%\"");
        add("concat");
        add("--");
        add("/*");
        add("*/");
        add("`");
        add(" ascii");
        add("|");
        add("^");
        add("&&");
        add("delete ");
        add("drop ");
        add(" dbms");
        add(".location");
        add("eval");
        add("alert");
        add("self");
        add(".cookie");
        add("expression");
        add(".open");
    }};

    public static boolean checkContent(String content){
        String newContent = content.toLowerCase();
        return FILTER_CHARACTER_LIST.stream().parallel().anyMatch(item->newContent.contains(item));
    }

    public static void main(String[] args) {
        String content ="javascript";
        boolean result = checkContent(content);
        LOG.info("result : {}",result);
        /*
        String content = "111111111111111111111) UNION ALL SELECT NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL-- ";
        String content2 = "111111111111111111111%' AND 4708=DBMS_PIPE.RECEIVE_MESSAGE(CHR(70)||CHR(74)||CHR(105)||CHR(73),5) AND '%'='";
        String content3 = "111111111111111111111) UNION ALL SELECT NULL,NULL,NULL,NULL,NULL,NULL#";
        String content4 ="111111111111111111111) AND 4708=DBMS_PIPE.RECEIVE_MESSAGE(CHR(70)||CHR(74)||CHR(105)||CHR(73),5) AND (3534=3534";
        String content5 ="(SELECT CONCAT(0x716a7a6a71,(SELECT (CASE WHEN (8322=8322) THEN 1 ELSE 0 END)),0x716a716271))";
        String content6 ="111111111111111111111' AND 6361=3861 AND 'LdDd'='LdDd";
        String content7 ="111111111111111111111%' ORDER BY 5482-- ";
        String content8 ="eval('hello')";
        String content9 ="javaScript:alert();";
        String content10 ="javaScript:window.location.harf=";
        String content11 =".self=...";
        String content12 =" ascii(";
        LOG.info("result : {}",checkContent(content2));
        LOG.info("result : {}",checkContent(content3));
        LOG.info("result : {}",checkContent(content4));
        LOG.info("result : {}",checkContent(content5));
        LOG.info("result : {}",checkContent(content6));
        LOG.info("result : {}",checkContent(content7));
        LOG.info("result : {}",checkContent(content8));
        LOG.info("result : {}",checkContent(content9));
        LOG.info("result : {}",checkContent(content10));
        LOG.info("result : {}",checkContent(content11));
        LOG.info("result : {}",checkContent(content12));
        LOG.info("result : {}",checkContent(content));
        */

        /*
        String ip01= "192.168.1.101";
        String ip02= "58.69.144.181";
        String ip03= "218.92.139.157";
        String ip04= "2001:db8:85a3::8a2e:370:7334";
        String ip05= "172.31.255.255";
        String ip06= "";
        String ip07= "21DA:D3:0:2F3B:2AA:FF:FE28:9C5A";
        String ip08= "FF02::2";
        String ip09= ":";

        LOG.info("result : {}",checkIP(ip01));
        LOG.info("result : {}",checkIP(ip02));
        LOG.info("result : {}",checkIP(ip03));
        LOG.info("result : {}",checkIP(ip04));
        LOG.info("result : {}",checkIP(ip05));
        LOG.info("result : {}",checkIP(ip06));
        LOG.info("result : {}",checkIP(ip07));
        LOG.info("result : {}",checkIP(ip08));
        LOG.info("result : {}",checkIP(ip09));
        */

    }
}
