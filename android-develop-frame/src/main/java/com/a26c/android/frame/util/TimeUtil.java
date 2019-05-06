package com.a26c.android.frame.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 不同时区对应的时间处理工具类
 *
 * @author fuguorong
 * @version 2015-3-11 下午4:00:12
 */
public class TimeUtil {
    /**
     * 判断用户的设备时区是否为东八区（中国） 2014年7月31日
     */
    public static boolean isInEasternEightZones() {
        boolean defaultVaule = true;
        if (TimeZone.getDefault() == TimeZone.getTimeZone("GMT+08"))
            defaultVaule = true;
        else
            defaultVaule = false;
        return defaultVaule;
    }

    /**
     * 根据不同时区，转换时间 2014年7月31日
     *
     * @param date
     * @param oldZone
     * @param newZone
     */
    public static Date transformTime(Date date, TimeZone oldZone,
                                     TimeZone newZone) {
        Date finalDate = null;
        if (date != null) {
            int timeOffset = oldZone.getOffset(date.getTime())
                    - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - timeOffset);
        }
        return finalDate;

    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getLeftTime(String time) {
        if (time == null || "".equals(time))
            return "";
        String difference = "";
        long tian = 0;
        long hour = 0;
        long min = 0;
        long mm = 0;
        time = time.replace("/", "-");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日期格式工具
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        try {
            Date date = sdf.parse(time);
            Date systemdDate = sdf.parse(sdf.format(new Date()));
            if (date.getTime() < systemdDate.getTime() + 60 * 1000) {//结束时间小于当前时间
                return "已经结束";
            } else {
                long l = date.getTime() - systemdDate.getTime();
                mm = l / 1000;
                min = mm / 60;
                mm = mm % 60;
                hour = min / 60;
                tian = hour / 24;
                hour = hour % 24;
                min = min % 60;
                if (hour == 0 && min < 3) {
                    return "少于3分钟";
                } else {
                    if (tian != 0) {
                        if (hour != 0) {
                            return tian + "天" + hour + "时";
                        } else if (min != 0) {
                            return tian + "天" + min + "分";
                        } else {
                            return tian + "天" + mm + "秒";
                        }
                    } else {
                        if (hour != 0) {
                            if (min != 0) {
                                return hour + "时" + min + "分" + mm + "秒";
                            } else {
                                return hour + "时" + mm + "秒";
                            }
                        } else {
                            return min + "分" + mm + "秒";
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return difference;
    }

    /**
     * @param time
     * :时间戳，可以是Long或者字符串，但是长度必须是10位
     * @desc 将时间戳转换为字符串
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_MM_SS = " HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final SimpleDateFormat FormatterMMDD = new SimpleDateFormat("MM.dd");
    public static final SimpleDateFormat Formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat FormatterYMDHM = new SimpleDateFormat("yyyy年M月d日 HH:mm");
    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static final SimpleDateFormat Formatter_NOHHMM = new SimpleDateFormat("yyyy-MM-dd");

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    public static String getFormatTime(Object time, String format) {
        if (time == null)
            return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            if (time instanceof Long)
                return sdf.format(new Date((long) time * 1000));
            else if (time instanceof String)
                return sdf.format(Long.parseLong((String) time) * 1000);
            else
                return "";
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatMMDD(long time) {
        Date date = new Date(time);
        return FormatterMMDD.format(date);
    }

    /**
     * 以友好的方式显示时间
     */
    public static String friendly_time(long date) {
        Date time = new Date(date);
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 返回long类型的今天的日期
     */
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
    }

    /**
     * 将字符串转为日期类型
     *
     * @param sdate
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getFormatFinancialTime(String time) {
        String mTime = getFormatTimeByTimeMillis(Long.parseLong(time), "yyyy/MM/dd HH:mm");
        return mTime;
    }

    public static String getFormatTimeByTimeMillis(long timeMills, String formatType) {
        Date date = new Date();
        date.setTime(timeMills);
        SimpleDateFormat formatter = new SimpleDateFormat(formatType, Locale.getDefault());
        String formatTime = formatter.format(date);
        return formatTime;
    }

    public static String formatStr(long time) {
        Date date = new Date(time);
        return Formatter.format(date);
    }

    public static String formatData(long Millis) {
        Date date = new Date(Millis);
        return FormatterYMDHM.format(date);
    }

    public static String formatNoHHMM(long time) {
        Date date = new Date(time);
        return Formatter_NOHHMM.format(date);
    }



    /**
     * 将值转换成 毫秒
     */
    @SuppressLint("SimpleDateFormat")
    public static long getLongTime(String date) {
        if (TextUtils.isEmpty(date)) {
            return 0L;
        }
        Date date1;
        try {
            date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date1.getTime();
    }

    /**
     * 将值转换成 毫秒
     */
    @SuppressLint("SimpleDateFormat")
    public static long getLongTimeyyyyMMdd(String date) {
        if (TextUtils.isEmpty(date)) {
            return 0L;
        }
        Date date1;
        try {
            date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date1.getTime();
    }

    /**
     * 将值转换成 毫秒
     */
    @SuppressLint("SimpleDateFormat")
    public static long getLongTime(String date, String format) {
        if (TextUtils.isEmpty(date)) {
            return 0L;
        }
        Date date1;
        try {
            date1 = new SimpleDateFormat(format).parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date1.getTime();
    }

}
