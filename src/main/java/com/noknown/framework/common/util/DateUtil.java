/**
 * @Title: DateUtil.java
 * @Package com.soulinfo.commons.util
 * @Description: 基础工具类-cookie操作
 * CopyRright (c) 2014-2015 SOUL
 * Company:无锡众志和达数据计算股份有限公司
 * @author xingweiwei
 * @date 2015年5月19日 下午3:05:54
 * @version V0.0.1
 */
package com.noknown.framework.common.util;

import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /** yyyy:年 */
    public static final String DATE_YEAR = "yyyy";

    /** MM：月 */
    public static final String DATE_MONTH = "MM";

    /** DD：日 */
    public static final String DATE_DAY = "dd";

    /** HH：时 */
    public static final String DATE_HOUR = "HH";

    /** mm：分 */
    public static final String DATE_MINUTE = "mm";

    /** ss：秒 */
    public static final String DATE_SECONDES = "ss";

    /** yyyy-MM-dd */
    public static final String DATE_FORMAT1 = "yyyy-MM-dd";

    /** yyyy-MM-dd hh:mm:ss */
    public static final String DATE_FORMAT2 = "yyyy-MM-dd HH24:mm:ss";

    /** yyyy-MM-dd hh:mm:ss|SSS */
    public static final String TIME_FORMAT_SSS = "yyyy-MM-dd HH24:mm:ss|SSS";

    /** yyyyMMdd */
    public static final String DATE_NOFUll_FORMAT = "yyyyMMdd";

    /** yyyyMMddhhmmss */
    public static final String TIME_NOFUll_FORMAT = "yyyyMMddHH24mmss";

    public static final String[] weeks = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String ALIGN_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String QFT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String QFT_DATETIME_FORMAT_NOYEAY = "MM-dd HH:mm";
    /**yyyyMMddHHmmss*/
    public static final String ACCOUNT_DATETIME_FORMAT = "yyyyMMddHHmmss";
    /**yyyyMMdd*/
    public static final String ACCOUNT_DATE_FORMAT = "yyyyMMdd";


    //2014-05-06
    public static String toStringByDate(Date date) {
        return toString(date, DEFAULT_DATE_FORMAT);
    }

    //13:00:00
    public static String toStringByTime(Date date) {
        return toString(date, DEFAULT_DATE_FORMAT);
    }

    //2014-05-06 16:46:35
    public static String toStringByDateTime(Date date) {
        return toString(date, DEFAULT_DATE_FORMAT);
    }


    /**date formart : yyyyMMddHHmmss*/
    public static String toStringAccountDateTime(Date d) {
        return toString(d, ACCOUNT_DATETIME_FORMAT);
    }

    /**date formart : yyyyMMdd*/
    public static String toStringAccountDate(Date d) {
        return toString(d, ACCOUNT_DATE_FORMAT);
    }

    /**date formart : yyyyMMddHHmmss*/
    public static Date toDateAccountDateTime(String d) {
        return toDate(d, ACCOUNT_DATETIME_FORMAT);
    }

    /**date formart : yyyyMMdd*/
    public static Date toDateAccountDate(String d) {
        return toDate(d, ACCOUNT_DATE_FORMAT);
    }

    /**
     * 日期字符串转换, 页面显示格式转换为Account数据库存储格式
     * 2006-11-14 => 20061114
     * */
    public static String transAccount(String src) {
        if (src == null) {
            return null;
        }
        if (src.indexOf('-') < 0) {
            return src;
        }
        Date d = toDate(src, DEFAULT_DATE_FORMAT);
        return toStringAccountDate(d);
    }

    /**
     * 日期字符串转换, Account数据库存储格式转换为页面显示格式
     * 20061114 => 2006-11-14
     * */
    public static String trans2view(String src) {
        if (StringUtils.isEmpty(src)) {
            return "";
        }
//		return src.substring(0, 4) + src.substring(4, 6) + src.substring(6);
        Date d = toDateAccountDate(src);
        return toString(d, DEFAULT_DATE_FORMAT);
    }


    /**
     * 日期字符串转换, Account数据库存储格式转换为页面显示格式
     * 20061114055545 => 2006-11-14 05:55
     * */
    public static String trans2viewTime(String src) {
        if (StringUtils.isEmpty(src)) {
            return "";
        }

        Date d = toDateAccountDateTime(src);
        return toString(d, "yyyy-MM-dd HH:mm");
    }

    /**
     * 日期字符串转换, Account数据库存储格式转换为页面显示格式
     * 20061114055545 => 2006-11-14 05:55:45
     * */
    public static String trans3viewTime(String src) {
        if (StringUtils.isEmpty(src)) {
            return "";
        }

        Date d = toDateAccountDateTime(src);
        return toString(d, DEFAULT_DATETIME_FORMAT);
    }


    public static String trans5viewTime(String src) {
        if (StringUtils.isEmpty(src)) {
            return "";
        }

        Date d = toDateAccountDateTime(src);
        return toString(d, QFT_DATETIME_FORMAT_NOYEAY);
    }

    private static final double[] LIMITS = {0, 1, 2};

    private static final String[] MINUTES_PART =
            {"", "1 minute ", "{0,number} minutes "};

    private static final String[] SECONDS_PART =
            {"0 seconds", "1 second", "{1,number} seconds"};

    private static final ChoiceFormat MINUTES_FORMAT =
            new ChoiceFormat(LIMITS, MINUTES_PART);

    private static final ChoiceFormat SECONDS_FORMAT =
            new ChoiceFormat(LIMITS, SECONDS_PART);

    private static final MessageFormat MINUTE_SECONDS =
            new MessageFormat("{0}{1}");

    static {
        MINUTE_SECONDS.setFormat(0, MINUTES_FORMAT);
        MINUTE_SECONDS.setFormat(1, SECONDS_FORMAT);
    }


    public static final long ONE_DAY = 24 * 60 * 60 * 1000;

    public static String toString(Date date, String format) {

        SimpleDateFormat formatter;

        if ((date == null) || (format == null) || (format.length() == 0)) {
            return null;
        }
        formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static Date toDate(String str, String format) {
        if ((str == null)
                || (str.length() == 0)
                || (format == null)
                || (format.length() == 0)) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setLenient(false);
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(str, pos);
    }

    public static boolean compare(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return true;
        }
	    if (date1 == null || date2 == null) {
		    return false;
	    } else {
		    return date1.getTime() == date2.getTime();
	    }
    }

    public static Date toDate(String str) {
        try {
            if (str.indexOf(':') > 0) {
                return toDate(str, DEFAULT_DATETIME_FORMAT);
            } else {
                return toDate(str, DEFAULT_DATE_FORMAT);
            }
        } catch (Exception ex) {
            return null;
        }
    }


    public static String currentDateToString(String format) {
        Date date = new Date();
        return toString(date, format);
    }

    public static String curDateStr() {
	    SimpleDateFormat format =
			    new SimpleDateFormat(DEFAULT_DATE_FORMAT);
	    return format.format(new Date());
    }

    public static String curDateTimeStr() {
	    SimpleDateFormat format =
			    new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
	    return format.format(new Date());
    }

    public static String formatElapsedTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        Object[] args = {new Long(minutes), new Long(seconds % 60)};
        return MINUTE_SECONDS.format(args);
    }


    /**
     * 取得指定月份的第一天日期
     * @param year
     * @param month
     * @return
     */
    public static String getFirstDateOfMonth(int year, int month) {
	    if (year < 0 || month > 12 || month < 1) {
		    return null;
	    }
        return year + (month < 10 ? ("0" + month) : ("" + month)) + "01";
    }

    /**
     * 取得指定月份的最后一天日期
     * @param year
     * @param month
     * @return
     */
    public static String getLastDateOfMonth(int year, int month) {
	    if (year < 0 || month > 12 || month < 1) {
		    return null;
	    }

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, 1);  //下个月的第一天
        cal.add(Calendar.DATE, -1); //减一天

        int day = cal.get(Calendar.DATE);

        return year + (month < 10 ? ("0" + month) : ("" + month)) + (day < 10 ? ("0" + day) : ("" + day));
    }

    /**
     * 取得指定日期的相隔天数对应的日期
     * @param year
     * @param month
     * @param day
     * @param days 相隔天数
     * @return
     */
    public static int[] findYearMonthAndDay(int year, int month, int day, int days) {
        int[] result = new int[3];
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, day);
        cal.add(Calendar.DATE, days);

        result[0] = cal.get(Calendar.YEAR);
        result[1] = cal.get(Calendar.MONTH) + 1;
        result[2] = cal.get(Calendar.DATE);
        return result;
    }

    /**
     * 得到指定日期的上一天的日期
     * @param date 格式：yyyyMMdd
     * @return yyyyMMdd
     */
    public static String getPreviousDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        int[] result = findYearMonthAndDay(year, month, day, -1);

        return result[0] + (result[1] < 10 ? ("0" + result[1]) : ("" + result[1])) + (result[2] < 10 ? ("0" + result[2]) : ("" + result[2]));
    }

    /**
     * 得到指定日期的后一天的日期
     * @param date 格式：yyyyMMdd
     * @return yyyyMMdd
     */
    public static String getBackDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        int[] result = findYearMonthAndDay(year, month, day, 1);

        return result[0] + (result[1] < 10 ? ("0" + result[1]) : ("" + result[1])) + (result[2] < 10 ? ("0" + result[2]) : ("" + result[2]));
    }

    /**
     * 得到指定日期的前七天的日期
     * @param date 格式：yyyyMMdd
     * @return yyyyMMdd
     */
    public static String getPreviousSevenDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        int[] result = findYearMonthAndDay(year, month, day, -6);

        return result[0] + (result[1] < 10 ? ("0" + result[1]) : ("" + result[1])) + (result[2] < 10 ? ("0" + result[2]) : ("" + result[2]));
    }

    /**
     * 2011年10月
     * @param date 格式：yyyyMMdd或YYYYMM
     * @return 2011年10月
     */
    public static String toChinaAccountDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));


        return year + "年" + month + "月";
    }

    /**
     * 得到指定日期的上一个月的日期
     * @param date 格式：yyyyMMdd或YYYYMM
     * @return yyyyMM
     */
    public static String getPreMonthDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = 0;
	    if (date.length() == 8) {
		    day = Integer.parseInt(date.substring(6, 8));
	    } else {
		    day = Integer.parseInt("01");
	    }

        int[] result = findYearMonth(year, month, day, -1);
        return result[0] + (result[1] < 10 ? ("0" + result[1]) : ("" + result[1])) + (result[2] < 10 ? ("0" + result[2]) : ("" + result[2]));
    }

    /**
     * 得到指定日期的上一个年的日期
     * @param date 格式：yyyyMMdd
     * @return yyyyMM
     */
    public static String getPreYearDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        int[] result = findYearDate(year, month, day, -1);
        return result[0] + (result[1] < 10 ? ("0" + result[1]) : ("" + result[1])) + (result[2] < 10 ? ("0" + result[2]) : ("" + result[2]));
    }

    /**
     * 取得指定日期的相隔月数对应的日期
     * @param year
     * @param month
     * @param day
     * @param months 相隔月数
     * @return
     */
    public static int[] findYearMonth(int year, int month, int day, int months) {
        int[] result = new int[3];
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, day);
        cal.add(Calendar.MONTH, months);

        result[0] = cal.get(Calendar.YEAR);
        result[1] = cal.get(Calendar.MONTH) + 1;
        result[2] = cal.get(Calendar.DATE);
        return result;
    }

    /**
     * 取得指定日期的相隔年数对应的日期
     * @param year
     * @param month
     * @param day
     * @param years 相隔年数
     * @return
     */
    public static int[] findYearDate(int year, int month, int day, int years) {
        int[] result = new int[3];
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, day);
        cal.add(Calendar.YEAR, years);

        result[0] = cal.get(Calendar.YEAR);
        result[1] = cal.get(Calendar.MONTH) + 1;
        result[2] = cal.get(Calendar.DATE);
        return result;
    }

    /**
     * 校验传的日期是否是合法日期
     * @param date 格式:yyyyMMdd
     * @return
     */
    public static boolean isValidAccountDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        ;
        try {
            dateFormat.parse(date);
            return true;
        } catch (Exception e) {
            //如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return false;
        }
    }

    /**
     * 日期间相隔的天数
     * @param t1
     * @param t2
     * @return
     * @throws ParseException
     */
    public static int getBetweenDays(String t1, String t2) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        int betweenDays = 0;
        Date d1 = format.parse(t1.substring(0, 8));
        Date d2 = format.parse(t2.substring(0, 8));
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        // 保证第二个时间一定大于第一个时间
        if (c1.after(c2)) {
            c1 = c2;
            c2.setTime(d1);
        }
        int betweenYears = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        betweenDays = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i < betweenYears; i++) {
            c1.set(Calendar.YEAR, (c1.get(Calendar.YEAR) + 1));
            betweenDays += c1.getMaximum(Calendar.DAY_OF_YEAR);
        }
        return betweenDays + 1;

    }

    /**
     * 距离今天多久
     * @param date
     * @return
     *
     */
    public static String fromToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
	    long oneMinute = 60;
	    long oneHour = 3600;
	    long oneDay = 86400;
	    long oneMonth = 2592000;
	    long oneYear = 31104000;
        long time = date.getTime() / 1000;
	    long now = System.currentTimeMillis() / 1000;
        long ago = now - time;
	    if (ago <= oneHour) {
		    return ago / oneMinute + "分钟前";
	    } else if (ago <= oneDay) {
		    return ago / oneHour + "小时" + (ago % oneHour / oneMinute)
				    + "分钟前";
	    } else if (ago <= oneDay * 2) {
		    return "昨天" + calendar.get(Calendar.HOUR_OF_DAY) + "点"
				    + calendar.get(Calendar.MINUTE) + "分";
	    } else if (ago <= oneDay * 3) {
		    return "前天" + calendar.get(Calendar.HOUR_OF_DAY) + "点"
				    + calendar.get(Calendar.MINUTE) + "分";
	    } else if (ago <= oneMonth) {
		    long day = ago / oneDay;
            return day + "天前" + calendar.get(Calendar.HOUR_OF_DAY) + "点"
                    + calendar.get(Calendar.MINUTE) + "分";
	    } else if (ago <= oneYear) {
		    long month = ago / oneMonth;
		    long day = ago % oneMonth / oneDay;
            return month + "个月" + day + "天前"
                    + calendar.get(Calendar.HOUR_OF_DAY) + "点"
                    + calendar.get(Calendar.MINUTE) + "分";
        } else {
		    long year = ago / oneYear;
            int month = calendar.get(Calendar.MONTH) + 1;// JANUARY which is 0 so month+1
            return year + "年前" + month + "月" + calendar.get(Calendar.DATE)
                    + "日";
        }

    }

    /**
     * 根据指定格式获取当前时间
     * @param format
     * @return String
     */
    public static String getCurrentTime(String format){
        SimpleDateFormat sdf = getFormat(format);
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 获取当前时间，格式为：yyyy-MM-dd HH:mm:ss
     * @return String
     */
    public static String getCurrentTime(){
        return getCurrentTime(DateUtil.DATE_FORMAT2);
    }

    /**
     * 获取指定格式的当前时间：为空时格式为yyyy-mm-dd HH:mm:ss
     * @param format
     * @return Date
     * @throws Exception
     */
    public static Date getCurrentDate(String format) throws Exception{
        SimpleDateFormat sdf = getFormat(format);
        String dateS = getCurrentTime(format);
        Date date = null;
        try {
            date = sdf.parse(dateS);
        } catch (ParseException e) {
            throw new Exception("时间转换出错..");
        }
        return date;
    }

    /**
     * 获取当前时间，格式为yyyy-MM-dd HH:mm:ss
     * @return Date
     * @throws Exception
     */
    public static Date getCurrentDate() throws Exception{
        return getCurrentDate(DateUtil.DATE_FORMAT2);
    }

    /**
     *
     * 格式转换<br>
     * yyyy-MM-dd hh:mm:ss 和 yyyyMMddhhmmss 相互转换<br>
     * yyyy-mm-dd 和yyyymmss 相互转换
     * @param value
     * 				日期
     * @return String
     */
    public static String stringFormat(String value) {
        String sReturn = "";
	    if (value == null || "".equals(value)) {
		    return sReturn;
	    }
        if (value.length() == 14) {   //长度为14格式转换成yyyy-mm-dd hh:mm:ss
            sReturn = value.substring(0, 4) + "-" + value.substring(4, 6) + "-" + value.substring(6, 8) + " "
                    + value.substring(8, 10) + ":" + value.substring(10, 12) + ":" + value.substring(12, 14);
            return sReturn;
        }
        if (value.length() == 19) {   //长度为19格式转换成yyyymmddhhmmss
            sReturn = value.substring(0, 4) + value.substring(5, 7) + value.substring(8, 10) + value.substring(11, 13)
                    + value.substring(14, 16) + value.substring(17, 19);
            return sReturn;
        }
        if(value.length() == 10){     //长度为10格式转换成yyyymmhh
            sReturn = value.substring(0, 4) + value.substring(5,7) + value.substring(8,10);
        }
        if(value.length() == 8){      //长度为8格式转化成yyyy-mm-dd
            sReturn = value.substring(0, 4) + "-" + value.substring(4, 6) + "-" + value.substring(6, 8);
        }
        return sReturn;
    }

    /**
     * 给指定日期加入年份，为空时默认当前时间
     * @param year 年份  正数相加、负数相减
     * @param date 为空时，默认为当前时间
     * @param format 默认格式为：yyyy-MM-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addYearToDate(int year,Date date,String format) throws Exception{
        Calendar calender = getCalendar(date,format);
        SimpleDateFormat sdf = getFormat(format);

        calender.add(Calendar.YEAR, year);

        return sdf.format(calender.getTime());
    }

    /**
     * 给指定日期加入年份，为空时默认当前时间
     * @param year 年份  正数相加、负数相减
     * @param date 为空时，默认为当前时间
     * @param format 默认格式为：yyyy-MM-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addYearToDate(int year,String date,String format) throws Exception{
        Date newDate = new Date();
        if(null != date && !"".equals(date)){
            newDate = string2Date(date, format);
        }

        return addYearToDate(year, newDate, format);
    }

    /**
     * 给指定日期增加月份 为空时默认当前时间
     * @param month  增加月份  正数相加、负数相减
     * @param date 指定时间
     * @param format 指定格式 为空默认 yyyy-mm-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addMothToDate(int month,Date date,String format) throws Exception{
        Calendar calender = getCalendar(date,format);
        SimpleDateFormat sdf = getFormat(format);

        calender.add(Calendar.MONTH, month);

        return sdf.format(calender.getTime());
    }

    /**
     * 给指定日期增加月份 为空时默认当前时间
     * @param month  增加月份  正数相加、负数相减
     * @param date 指定时间
     * @param format 指定格式 为空默认 yyyy-mm-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addMothToDate(int month,String date,String format) throws Exception{
        Date newDate = new Date();
        if(null != date && !"".equals(date)){
            newDate = string2Date(date, format);
        }

        return addMothToDate(month, newDate, format);
    }

    /**
     * 给指定日期增加天数，为空时默认当前时间
     * @param day 增加天数 正数相加、负数相减
     * @param date 指定日期
     * @param format 日期格式 为空默认 yyyy-mm-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addDayToDate(int day,Date date,String format) throws Exception{
        Calendar calendar = getCalendar(date, format);
        SimpleDateFormat sdf = getFormat(format);

        calendar.add(Calendar.DATE, day);

        return sdf.format(calendar.getTime());
    }

    /**
     * 给指定日期增加天数，为空时默认当前时间
     * @param day 增加天数 正数相加、负数相减
     * @param date 指定日期
     * @param format 日期格式 为空默认 yyyy-mm-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addDayToDate(int day,String date,String format) throws Exception{
        Date newDate = new Date();
        if(null != date && !"".equals(date)){
            newDate = string2Date(date, format);
        }

        return addDayToDate(day, newDate, format);
    }

    /**
     * 给指定日期增加小时，为空时默认当前时间
     * @param hour 增加小时  正数相加、负数相减
     * @param date 指定日期
     * @param format 日期格式 为空默认 yyyy-mm-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addHourToDate(int hour,Date date,String format) throws Exception{
        Calendar calendar = getCalendar(date, format);
        SimpleDateFormat sdf = getFormat(format);

        calendar.add(Calendar.HOUR, hour);

        return sdf.format(calendar.getTime());
    }

    /**
     * 给指定日期增加小时，为空时默认当前时间
     * @param hour 增加小时  正数相加、负数相减
     * @param date 指定日期
     * @param format 日期格式 为空默认 yyyy-mm-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addHourToDate(int hour,String date,String format) throws Exception{
        Date newDate = new Date();
        if(null != date && !"".equals(date)){
            newDate = string2Date(date, format);
        }

        return addHourToDate(hour, newDate, format);
    }

    /**
     * 给指定的日期增加分钟，为空时默认当前时间
     * @param minute 增加分钟  正数相加、负数相减
     * @param date 指定日期
     * @param format 日期格式 为空默认 yyyy-mm-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addMinuteToDate(int minute,Date date,String format) throws Exception{
        Calendar calendar = getCalendar(date, format);
        SimpleDateFormat sdf = getFormat(format);

        calendar.add(Calendar.MINUTE, minute);

        return sdf.format(calendar.getTime());
    }

    /**
     * 给指定的日期增加分钟，为空时默认当前时间
     * @param minute 增加分钟  正数相加、负数相减
     * @param date 指定日期
     * @param format 日期格式 为空默认 yyyy-mm-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addMinuteToDate(int minute,String date,String format) throws Exception{
        Date newDate = new Date();
        if(null != date && !"".equals(date)){
            newDate = string2Date(date, format);
        }

        return addMinuteToDate(minute, newDate, format);
    }

    /**
     * 给指定日期增加秒，为空时默认当前时间
     * @param second 增加秒 正数相加、负数相减
     * @param date 指定日期
     * @param format 日期格式 为空默认 yyyy-mm-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addSecondToDate(int second,Date date,String format) throws Exception{
        Calendar calendar = getCalendar(date, format);
        SimpleDateFormat sdf = getFormat(format);

        calendar.add(Calendar.SECOND, second);

        return sdf.format(calendar.getTime());
    }

    /**
     * 给指定日期增加秒，为空时默认当前时间
     * @param second 增加秒 正数相加、负数相减
     * @param date 指定日期
     * @param format 日期格式 为空默认 yyyy-mm-dd HH:mm:ss
     * @return String
     * @throws Exception
     */
    public static String addSecondToDate(int second,String date,String format) throws Exception{
        Date newDate = new Date();
        if(null != date && !"".equals(date)){
            newDate = string2Date(date, format);
        }

        return addSecondToDate(second, newDate, format);
    }

    /**
     * 获取指定格式指定时间的日历
     * @param date 时间
     * @param format 格式
     * @return Calendar
     * @throws Exception
     */
    public static Calendar getCalendar(Date date,String format) throws Exception{
        if(date == null){
            date = getCurrentDate(format);
        }

        Calendar calender = Calendar.getInstance();
        calender.setTime(date);

        return calender;
    }

    /**
     * 获取日期显示格式，为空默认为yyyy-mm-dd HH:mm:ss
     * @param format
     * @return
     * @return SimpleDateFormat
     */
    private static SimpleDateFormat getFormat(String format){
        if(format == null || "".equals(format)){
            format = DateUtil.DATE_FORMAT2;
        }
        return new SimpleDateFormat(format);
    }

    /**
     * 将字符串(格式符合规范)转换成Date
     * @param value 需要转换的字符串
     * @param format 日期格式
     * @return Date
     * @throws Exception
     */
    public static Date string2Date(String value,String format) throws Exception{
        if(value == null || "".equals(value)){
            return null;
        }

        SimpleDateFormat sdf = getFormat(format);
        Date date = null;
        value = formatDate(value, format);
        try {
            date = sdf.parse(value);
        } catch (ParseException e) {
            throw new Exception("时间转换出错..");
        }
        return date;
    }

    /**
     * 将日期格式转换成String
     * @param value 需要转换的日期
     * @param format 日期格式
     * @return String
     */
    public static String date2String(Date value,String format){
        if(value == null){
            return null;
        }

        SimpleDateFormat sdf = getFormat(format);
        return sdf.format(value);
    }

    /**
     * @desc:格式化时间
     *
     * @param date 时间
     * @param format 指定格式
     * @return
     * @throws Exception
     * @throws ParseException
     */
    public static String formatDate(String date,String format) throws Exception{
	    if (StringUtil.isBlank(date) || StringUtil.isBlank(format)) {
            return "";
        }
        Date dt = null;
        SimpleDateFormat inFmt = null;
        SimpleDateFormat outFmt = null;
        ParsePosition pos = new ParsePosition(0);
        date = date.replace("-", "").replace(":", "");
	    if ((date == null) || ("".equals(date.trim()))) {
		    return "";
	    }
        try {
	        if (Long.parseLong(date) == 0L) {
		        return "";
	        }
        } catch (Exception nume) {
            return date;
        }
        try {
            switch (date.trim().length()) {
                case 14:
                    inFmt = new SimpleDateFormat("yyyyMMddHHmmss");
                    break;
                case 12:
                    inFmt = new SimpleDateFormat("yyyyMMddHHmm");
                    break;
                case 10:
                    inFmt = new SimpleDateFormat("yyyyMMddHH");
                    break;
                case 8:
                    inFmt = new SimpleDateFormat("yyyyMMdd");
                    break;
                case 6:
                    inFmt = new SimpleDateFormat("yyyyMM");
                    break;
                case 7:
                case 9:
                case 11:
                case 13:
                default:
                    return date;
            }
	        if ((dt = inFmt.parse(date, pos)) == null) {
		        return date;
	        }
            if ((format == null) || ("".equals(format.trim()))) {
                outFmt = new SimpleDateFormat("yyyy年MM月dd日");
            } else {
                outFmt = new SimpleDateFormat(format);
            }
            return outFmt.format(dt);
        } catch (Exception ex) {
        }
        return date;
    }

    /**
     * @desc:格式化是时间，采用默认格式（yyyy-MM-dd HH24:mm:ss）
     *
     * @param value
     * @return
     * @throws Exception
     */
    public static String formatDate(String value) throws Exception{
        return getFormat(DateUtil.DATE_FORMAT2).format(string2Date(value, DateUtil.DATE_FORMAT2));
    }


    /**
     * 获取指定日期的年份
     * @param value 日期
     * @return int
     */
    public static int getCurrentYear(Date value){
        String date = date2String(value, DateUtil.DATE_YEAR);
        return Integer.valueOf(date);
    }

    /**
     * 获取指定日期的年份
     * @param value 日期
     * @return int
     * @throws Exception
     */
    public static int getCurrentYear(String value) throws Exception{
        Date date = string2Date(value, DateUtil.DATE_YEAR);
        Calendar calendar = getCalendar(date, DateUtil.DATE_YEAR);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取指定日期的月份
     * @param value 日期
     * @return int
     */
    public static int getCurrentMonth(Date value){
        String date = date2String(value, DateUtil.DATE_MONTH);
        return Integer.valueOf(date);
    }

    /**
     * 获取指定日期的月份
     * @param value 日期
     * @return int
     * @throws Exception
     */
    public static int getCurrentMonth(String value) throws Exception{
        Date date = string2Date(value, DateUtil.DATE_MONTH);
        Calendar calendar = getCalendar(date, DateUtil.DATE_MONTH);

        return calendar.get(Calendar.MONTH);
    }

    /**
     * 获取指定日期的天份
     * @param value 日期
     * @return int
     */
    public static int getCurrentDay(Date value){
        String date = date2String(value, DateUtil.DATE_DAY);
        return Integer.valueOf(date);
    }

    /**
     * 获取指定日期的天份
     * @author chenssy
     * @data Dec 31, 2013
     * @param value 日期
     * @return int
     * @throws Exception
     */
    public static int getCurrentDay(String value) throws Exception{
        Date date = string2Date(value, DateUtil.DATE_DAY);
        Calendar calendar = getCalendar(date, DateUtil.DATE_DAY);

        return calendar.get(Calendar.DATE);
    }

    /**
     * 获取当前日期为星期几
     * @param value 日期
     * @return String
     * @throws Exception
     */
    public static String getCurrentWeek(Date value) throws Exception{
        Calendar calendar = getCalendar(value, DateUtil.DATE_FORMAT1);
        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1 < 0 ? 0 : calendar.get(Calendar.DAY_OF_WEEK) - 1;

        return weeks[weekIndex];
    }

    /**
     * 获取当前日期为星期几
     * @param value 日期
     * @return String
     * @throws Exception
     */
    public static String getCurrentWeek(String value) throws Exception{
        Date date = string2Date(value, DateUtil.DATE_FORMAT1);
        return getCurrentWeek(date);
    }

    /**
     * 获取指定日期的小时
     * @param value 日期
     * @return int
     */
    public static int getCurrentHour(Date value){
        String date = date2String(value, DateUtil.DATE_HOUR);
        return Integer.valueOf(date);
    }

    /**
     * 获取指定日期的小时
     * @param value 日期
     * @return
     * @return int
     * @throws Exception
     */
    public static int getCurrentHour(String value) throws Exception{
        Date date = string2Date(value, DateUtil.DATE_HOUR);
        Calendar calendar = getCalendar(date, DateUtil.DATE_HOUR);

        return calendar.get(Calendar.DATE);
    }

    /**
     * 获取指定日期的分钟
     * @param value 日期
     * @return int
     */
    public static int getCurrentMinute(Date value){
        String date = date2String(value, DateUtil.DATE_MINUTE);
        return Integer.valueOf(date);
    }

    /**
     * 获取指定日期的分钟
     * @param value 日期
     * @return int
     * @throws Exception
     */
    public static int getCurrentMinute(String value) throws Exception{
        Date date = string2Date(value, DateUtil.DATE_MINUTE);
        Calendar calendar = getCalendar(date, DateUtil.DATE_MINUTE);

        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 比较两个日期相隔多少天(月、年) <br>
     * 例：<br>
     * &nbsp;compareDate("2009-09-12", null, 0);//比较天 <br>
     * &nbsp;compareDate("2009-09-12", null, 1);//比较月 <br>
     * &nbsp;compareDate("2009-09-12", null, 2);//比较年 <br>
     *
     * @param startDay 需要比较的时间 不能为空(null),需要正确的日期格式 ,如：2009-09-12
     * @param endDay 被比较的时间  为空(null)则为当前时间
     * @param stype 返回值类型   0为多少天，1为多少个月，2为多少年
     * @return int
     * @throws Exception
     */
    public static int compareDate(String startDay,String endDay,int stype) throws Exception{
        int n = 0;
        startDay = formatDate(startDay, "yyyy-MM-dd");
        endDay = formatDate(endDay, "yyyy-MM-dd");

        String formatStyle = "yyyy-MM-dd";
        if("1".equals(stype + "")){
            formatStyle = "yyyy-MM";
        }else if("2".equals(stype + "")){
            formatStyle = "yyyy";
        }

        endDay = StringUtil.isBlank(endDay) ? getCurrentTime("yyyy-MM-dd") : endDay;

        DateFormat df = new SimpleDateFormat(formatStyle);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(startDay));
            c2.setTime(df.parse(endDay));
        } catch (Exception e) {
            throw new Exception("时间转换出错..");
        }
        while (!c1.after(c2)) {                   // 循环对比，直到相等，n 就是所要的结果
            n++;
            if(stype==1){
                c1.add(Calendar.MONTH, 1);          // 比较月份，月份+1
            }
            else{
                c1.add(Calendar.DATE, 1);           // 比较天数，日期+1
            }
        }
        n = n-1;
        if(stype==2){
            n = (int)n/365;
        }
        return n;
    }

    /**
     * 比较两个时间相差多少小时(分钟、秒)
     * @param startTime 需要比较的时间 不能为空，且必须符合正确格式：2012-12-12 12:12:
     * @param endTime 需要被比较的时间 若为空则默认当前时间
     * @param type 1：小时   2：分钟   3：秒
     * @return int
     * @throws Exception
     */
    public static int compareTime(String startTime , String endTime , int type) throws Exception{
        //endTime是否为空，为空默认当前时间
        if(endTime == null || "".equals(endTime)){
            endTime = getCurrentTime();
        }

        SimpleDateFormat sdf = getFormat("");
        int value = 0;
        try {
            Date begin = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            long between = (end.getTime() - begin.getTime()) / 1000;  //除以1000转换成豪秒
            if(type == 1){   //小时
                value = (int) (between % (24 * 36000) / 3600);
            }
            else if(type == 2){
                value = (int) (between % 3600 / 60);
            }
            else if(type == 3){
                value = (int) (between % 60 / 60);
            }
        } catch (ParseException e) {
            throw new Exception("时间转换出错..");
        }
        return value;
    }

    /**
     * 比较两个日期的大小。<br>
     * 若date1 > date2 则返回 1<br>
     * 若date1 = date2 则返回 0<br>
     * 若date1 < date2 则返回-1
     *
     * @param date1
     * @param date2
     * @param format  待转换的格式
     * @return 比较结果
     */
    public static int compare(String date1, String date2,String format) {
        DateFormat df = getFormat(format);
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 将String 转换为 timestamp<br>
     * 注：value必须形如： yyyy-mm-dd hh:mm:ss[.f...] 这样的格式，中括号表示可选，否则报错！！！
     *
     * @param value
     * @return
     * @throws Exception
     */
    public static Timestamp string2Timestamp(String value) throws Exception{
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ts = Timestamp.valueOf(value);
        return ts;
    }

    /**
     * 将timeStamp 转换为String类型，format为null则使用默认格式 yyyy-MM-dd HH:mm:ss
     *
     * @param value
     * @param format
     * @return
     */
    public static String timeStamp2String(Timestamp value,String format){
        if(null == value){
            return "";
        }
        SimpleDateFormat sdf = getFormat(format);

        return sdf.format(value);
    }


    /**
     * 获取指定日期，当天最小时间 即0点
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getMinTimeByDay(Date date) throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int house = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.HOUR, -house);
        calendar.add(Calendar.MINUTE,-minute);
        return calendar.getTime();
    }

    /**
     * 获取指定日期，当天最小时间 即23点59分
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getMaxTimeByDay(Date date) throws Exception{

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int house = calendar.get(Calendar.HOUR_OF_DAY);
        int adhouse= 23-house;

        int minute = calendar.get(Calendar.MINUTE);
        int adminute= 59-minute;

        calendar.add(Calendar.HOUR, adhouse);
        calendar.add(Calendar.MINUTE,adminute);
        return  calendar.getTime();

    }

    public static java.sql.Time strToTime(String strDate, String formart) {
        if (formart == null) {
            formart = "hh:mm:ss";
        }
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat(formart);
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Time date = new java.sql.Time(d.getTime());
        return date;
    }

    // 获得某天最大时间 2017-10-15 23:59:59
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        ;
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    // 获得某天最小时间 2017-10-15 00:00:00
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

}
