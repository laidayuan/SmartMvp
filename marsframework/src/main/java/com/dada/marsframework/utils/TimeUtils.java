package com.dada.marsframework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by laidayuan on 2018/2/12.
 */

public class TimeUtils {
    private static final String YYYYMM = "yyyyMM";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    private static final String HH_MI_SS = "HH:mm:ss";
    private static final String YYYY_MM_DD_CN = "yyyy年MM月dd";
    private static final String FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒";
    private static final int[] DAYS_OF_MONTH = { 31, 28, 31, 30, 31, 30, 31,
            31, 30, 31, 30, 31 };
    private static String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四",
            "星期五", "星期六" };

    public static String formatDate(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return returnValue;
    }

    public static String formatNow() {

        return formatDate(new Date(), FORMAT_LONG);
    }

    public static Date parseDate(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 得到星期几
    public static String getWeekOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    // 判断日期是否是周六周末
    public boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == 1 || dayOfWeek == 7;
    }

    // date所处周的星期一
    public static Date getFirstDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    // date所处周的星期天
    public static Date getLastDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal.getTime();
    }

    // 得到日期月份最大的天数
    public static int getMaxDayOfMonth(int year, int month) {
        if (month == 2 && (year % 4 == 0 && year % 100 != 0 || year % 400 == 0))
            return 29;
        return DAYS_OF_MONTH[month - 1];
    }

    // 判断2个日期是否在同一天
    public static boolean isSameDay(Date date, Date date2) {
        String str = formatDate(date, YYYY_MM_DD);
        String str2 = formatDate(date2, YYYY_MM_DD);
        return str.equals(str2);
    }

    // yyyy-MM-dd 0:00:00
    // days=0 今天,-1昨天,1明天下面的都一样
    public static Date getDateStart(Date date, int days) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date);
        startCal.set(5, startCal.get(5) + days);
        startCal.set(11, 0);
        startCal.set(14, 0);
        startCal.set(13, 0);
        startCal.set(12, 0);
        return startCal.getTime();
    }

    // yyyy-MM-dd 23:59:59
    public static Date getDateEnd(Date date, int days) {
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(date);
        endCal.set(5, endCal.get(5) + days);
        endCal.set(11, 23);
        endCal.set(14, 59);
        endCal.set(13, 59);
        endCal.set(12, 59);
        return endCal.getTime();
    }

    // yyyy-MM-1 00:00:00
    public static Date getMonthStart(Date date, int n) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date);
        startCal.set(5, 1);
        startCal.set(11, 0);
        startCal.set(14, 0);
        startCal.set(13, 0);
        startCal.set(12, 0);
        startCal.set(2, startCal.get(2) + n);
        return startCal.getTime();
    }

    // yyyy-MM-end 23:59:59
    public static Date getMonthEnd(Date date, int n) {
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(date);
        endCal.set(5, 1);
        endCal.set(11, 23);
        endCal.set(14, 59);
        endCal.set(13, 59);
        endCal.set(12, 59);
        endCal.set(2, endCal.get(2) + n + 1);
        endCal.set(5, endCal.get(5) - 1);
        return endCal.getTime();
    }

    // yyyy-1-1 00:00:00
    public static Date getYearStart(Date date, int n) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date);
        startCal.set(2, 0);// JANUARY which is 0;
        startCal.set(5, 1);
        startCal.set(11, 0);
        startCal.set(12, 0);
        startCal.set(14, 0);
        startCal.set(13, 0);
        startCal.set(1, startCal.get(1) + n);
        return startCal.getTime();
    }

    // yyyy-12-31 23:59:59
    public static Date getYearEnd(Date date, int n) {
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(date);
        endCal.set(2, 12);
        endCal.set(5, 1);
        endCal.set(11, 23);
        endCal.set(14, 59);
        endCal.set(13, 59);
        endCal.set(12, 59);
        endCal.set(1, endCal.get(1) + n);
        endCal.set(5, endCal.get(5) - 1);
        return endCal.getTime();
    }

    // 日期加上n个月
    public static Date addMonths(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    // 日期加上n天
    public static Date addDays(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, n);
        return cal.getTime();
    }

    // 2个日期相差多少天
    public static long getDayDiff(Date startDate, Date endDate)
            throws ParseException {
        long t1 = startDate.getTime();
        long t2 = endDate.getTime();
        long count = (t2 - t1) / (24L * 60 * 60 * 1000);
        return Math.abs(count);
    }

    // 2个日期相差多少天 不考虑时分秒
    public static long getDayDiffIgnoreHHMISS(Date startDate, Date endDate)
            throws ParseException {
        startDate = getDateStart(startDate, 0);
        endDate = getDateStart(endDate, 0);
        long t1 = startDate.getTime();
        long t2 = endDate.getTime();
        long count = (t2 - t1) / (24L * 60 * 60 * 1000);
        return Math.abs(count);
    }

    // 2个日期相差多少年
    public static int getYearDiff(Date minDate, Date maxDate) {
        if (minDate.after(maxDate)) {
            Date tmp = minDate;
            minDate = new Date(maxDate.getTime());
            maxDate = new Date(tmp.getTime());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(minDate);
        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH);
        int day1 = calendar.get(Calendar.DATE);

        calendar.setTime(maxDate);
        int year2 = calendar.get(Calendar.YEAR);
        int month2 = calendar.get(Calendar.MONTH);
        int day2 = calendar.get(Calendar.DATE);
        int result = year2 - year1;
        if (month2 < month1) {
            result--;
        } else if (month2 == month1 && day2 < day1) {
            result--;
        }
        return result;
    }

    // 2个日期相差多少月
    public static int getMonthDiff(Date minDate, Date maxDate) {
        if (minDate.after(maxDate)) {
            Date tmp = minDate;
            minDate = new Date(maxDate.getTime());
            maxDate = new Date(tmp.getTime());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(minDate);
        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH);
        int day1 = calendar.get(Calendar.DATE);

        calendar.setTime(maxDate);
        int year2 = calendar.get(Calendar.YEAR);
        int month2 = calendar.get(Calendar.MONTH);
        int day2 = calendar.get(Calendar.DATE);

        int months = 0;
        if (day2 >= day1) {
            months = month2 - month1;
        } else {
            months = month2 - month1 - 1;
        }
        return (year2 - year1) * 12 + months;
    }

    // 得到2个日期之间的月份,返回值List<字符串>
    public static List getMonthsBetween(Date minDate, Date maxDate) {
        ArrayList result = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        if (minDate.after(maxDate)) {
            Date tmp = minDate;
            minDate = new Date(maxDate.getTime());
            maxDate = new Date(tmp.getTime());
        }
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(minDate);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.setTime(maxDate);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }
        return result;
    }

    /**
     * 最近一周时间段 今天对应的上周星期n-今天
     */
    public static Date[] getRecentWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Date to = calendar.getTime();
        calendar.set(Calendar.WEEK_OF_MONTH,
                calendar.get(Calendar.WEEK_OF_MONTH) - 1);
        Date from = calendar.getTime();
        return new Date[] { from, to };
    }

    // 最近一个月 今天对应的上月n日-今天
    public static Date[] getRecentMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Date to = calendar.getTime();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        Date from = calendar.getTime();
        return new Date[] { from, to };
    }

    // 中文显示日期与当前时间差
    public static String friendlyFormat(Date date) throws ParseException {
        if (date == null) {
            return "未知";
        }
        Date baseDate = new Date();
        if (baseDate.before(date)) {
            return "未知";
        }
        int year = getYearDiff(baseDate, date);
        int month = getMonthDiff(baseDate, date);
        if (year >= 1) {
            return year + "年前";
        } else if (month >= 1) {
            return month + "月前";
        }
        int day = (int) getDayDiff(baseDate, date);
        if (day > 0) {
            if (day > 2) {
                return day + "天前";
            } else if (day == 2) {
                return "前天";
            } else if (day == 1) {
                return "昨天";
            }
        }
        if (!isSameDay(baseDate, date)) {
            return "昨天";
        }
        int hour = (int) ((baseDate.getTime() - date.getTime()) / (1 * 60 * 60 * 1000));
        if (hour > 6) {
            return "今天";
        } else if (hour > 0) {
            return hour + "小时前";
        }
        int minute = (int) ((baseDate.getTime() - date.getTime()) / (1 * 60 * 1000));
        if (minute < 2) {
            return "刚刚";
        } else if (minute < 30) {
            return minute + "分钟前";
        } else if (minute > 30) {
            return "半个小时以前";
        }
        return "未知";
    }


    /**
     * date 与当前时间相差的天数
     *
     * @param date
     * @return
     */
    public static int getDetalDay(Date date) {
        int det = 0;
        try {
            String t1 = formatDate(date, YYYY_MM_DD);
            String[] str = t1.split("-");
            Calendar cal = Calendar.getInstance();
            int current = cal.get(Calendar.DATE);
            det = current - Integer.valueOf(str[2]);

        } catch (Exception e) {
        }
        return det;
    }

    /**
     * 同年的情况下，单纯的计算月份的差数
     *
     * @param date
     * @return
     */
    public static int getDetalMonth(Date date) {
        int det = 0;
        try {
            String t1 = formatDate(date, YYYY_MM_DD);
            String[] str = t1.split("-");
            Calendar cal = Calendar.getInstance();
            int current = cal.get(Calendar.MONTH);
            det = current - Integer.valueOf(str[1]);
        } catch (Exception e) {
        }
        return Math.abs(det);
    }

    /**
     * 计算年差
     *
     * @param date
     * @return
     */
    public static int getDetalYear(Date date) {
        int det = 0;
        try {
            String t1 = formatDate(date, YYYY_MM_DD);
            String[] str = t1.split("-");
            Calendar cal = Calendar.getInstance();
            int current = cal.get(Calendar.YEAR);
            det = current - Integer.valueOf(str[0]);
        } catch (Exception e) {
        }
        return Math.abs(det);
    }
}