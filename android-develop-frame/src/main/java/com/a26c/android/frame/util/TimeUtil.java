package com.a26c.android.frame.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 以友好的方式显示时间
     */
    public static String friendly_time(long date) {
        Date time = new Date(date);
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // 判断是否是同一天
        String curDate = dateFormat.format(cal.getTime());
        String paramDate = dateFormat.format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormat.format(time);
        }
        return ftime;
    }



    /**
     * 将值转换成 毫秒
     */
    @SuppressLint("SimpleDateFormat")
    public static long getLongTime_YYYY_MM_DD_HH_MM_SS(String date) {
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
            date1 = new SimpleDateFormat().parse(date);
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
