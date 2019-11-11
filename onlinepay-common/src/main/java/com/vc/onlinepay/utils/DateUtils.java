/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * @author ThinkGem
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	
	private static String[] parsePatterns = {
		"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
		"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}
	
	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}
	
	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}
	
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}
	
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
	 *   "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = System.currentTimeMillis()-date.getTime();
		return t/(24*60*60*1000);
	}

	/**
	 * 获取过去的小时
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = System.currentTimeMillis()-date.getTime();
		return t/(60*60*1000);
	}
	
	/**
	 * 获取过去的分钟
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = System.currentTimeMillis()-date.getTime();
		return t/(60*1000);
	}
	
	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * @param timeMillis
	 * @return
	 */
    public static String formatDateTime(long timeMillis){
		long day = timeMillis/(24*60*60*1000);
		long hour = (timeMillis/(60*60*1000)-day*24);
		long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
		long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
		long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
		return (day>0?day+",":"")+hour+":"+min+":"+s+"."+sss;
    }
	
	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}

    /**
     * 判断某一时间是否在一个区间内
     *
     * @param sourceTime
     *            时间区间,半闭合,如[10:00-20:00)
     * @param curTime
     *            需要判断的时间 如10:00
     * @return
     * @throws IllegalArgumentException
     */
    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            return false;
        }
        if (curTime == null || !curTime.contains(":")) {
            return false;
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if ("00:00".equals(args[1])) {
                args[1] = "24:00";
            }
            if (end < start) {
                return now < end || now >= start;
            }
            else {
                return now >= start && now < end;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }
    
    /**
	 * 判断是否是8点到20点
	 * @return
	 */
	public static boolean isTranferTime(){
		  
	    Calendar currentDate = Calendar.getInstance(); 
	    currentDate.setTime(new Date());
	   
	    Calendar min=Calendar.getInstance();
	    //min=currentDate;
	    min.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
	    min.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
	    min.set(Calendar.HOUR_OF_DAY, 8);
	    min.set(Calendar.MINUTE, 0);
	    min.set(Calendar.SECOND, 0);
	    min.set(Calendar.MILLISECOND, 0);
	   
	    Calendar max=Calendar.getInstance();
	   // max=currentDate;
	    max.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
	    max.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
	    max.set(Calendar.HOUR_OF_DAY, 20);
	    max.set(Calendar.MINUTE, 0);
	    max.set(Calendar.SECOND, 0);
	    max.set(Calendar.MILLISECOND, 0);

        return currentDate.getTimeInMillis() >= min.getTimeInMillis() && currentDate.getTimeInMillis() <= max.getTimeInMillis();
   }
	
	/**
	 * 获取当天的零点零时零分零秒
	 * @return
	 */
	public static Date getTodayDate(){
		Calendar c1 = new GregorianCalendar();
	    c1.set(Calendar.HOUR_OF_DAY, 0);
	    c1.set(Calendar.MINUTE, 0);
	    c1.set(Calendar.SECOND, 0);
        return c1.getTime();
	}
	
	/**
     * 判断时间是不是今天
     * @param date
     * @return    是返回true，不是返回false
     */
    public static boolean isNow(Date date) {
        //当前时间
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        //获取今天的日期
        String nowDay = sf.format(now);
         
        //对比的时间
        String day = sf.format(date);
         
        return day.equals(nowDay);
         
    }
    /**
     * 获取今天的日期
     * @return
     */
    public static String getToday(){
    	//取得商户当前系统时间
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	//对账日期
        return sdf.format(new Date());
    }
    
    /****
     * ��ȡyyyy-MM-dd HH:mm:ss��ʽ�ĵ�ǰʱ��
     * String����
     * @return
     */
    public static String getTimeForY_M_D_H_m_s() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");// ���Է�����޸����ڸ�ʽ
        return dateFormat.format(now);

    }

    public static String getTimeForY_M_D() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -3); // 2000-9-31 => 2000-10-1������

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return formatter.format(calendar.getTime());
    }
    /****
     * ��ȡYYYY��ʽʱ��
     * @return
     */
    public static String getTimeFoeYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -3); // 2000-9-31 => 2000-10-1������
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        return formatter.format(calendar.getTime());
    }
    
    
    /****
     * ��ȡYYYYMMDD��ʽʱ��
     * @return
     */
    public static String getTimeYMD() {
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.DAY_OF_MONTH,-3); // 2000-9-31 => 2000-10-1������
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        return formatter.format(calendar.getTime());
    }
    
    
    
    /****
     * ��ȡ6λ��ʽ�ĵ������κ�
     * @return
     */
    public static String getTimeYYMMDD() {
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.DAY_OF_MONTH,-3); // 2000-9-31 => 2000-10-1������
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(calendar.getTime()).substring(2);
    }
    
    /****
     * ��ȡMMDD��ʽʱ��
     * @return
     */
    public static String getTimeMD() {
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.DAY_OF_MONTH,-3); // 2000-9-31 => 2000-10-1������
        SimpleDateFormat formatter = new SimpleDateFormat("MMdd");

        return formatter.format(calendar.getTime());
    }

    /****
     * ��ȡyyyyMMddHHmmss��ʽ�ĵ�ǰʱ��
     * String����
     * @return
     */
    public static String getTimeYMDhms() {

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// ���Է�����޸����ڸ�ʽ
        return dateFormat.format(now);
    }
    
    /****
     * ��ȡHHmmss��ʽ�ĵ�ǰʱ��
     * String����
     * @return
     */
    public static String getTimeforHms() {

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");// ���Է�����޸����ڸ�ʽ
        return dateFormat.format(now);
    }
    
    /****
     * ��ȡlong��ʽ�ĵ�ǰʱ��
     * String����
     * @return
     */
    public static String getTimeforLong() {
        Date now = new Date();      
        return now.getTime()+"";
    }

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
//		System.out.println(formatDate(parseDate("2010/3/6")));
//		System.out.println(getDate("yyyy年MM月dd日 E"));
//		long time = new Date().getTime()-parseDate("2012-11-19").getTime();
//		System.out.println(time/(24*60*60*1000));
	}
}
