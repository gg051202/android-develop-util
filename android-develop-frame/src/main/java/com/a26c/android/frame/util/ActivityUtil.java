package com.a26c.android.frame.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

/**
 * Activity 工具类
 */
public class ActivityUtil {

    private static int ScreenWidth = 0;
    private static int ScreenHeight = 0;
    private static int statusHeight = 0;

    /**
     * 判断是否在后台运行
     */
    public static boolean isRunBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && tasks.size() > 0) {
            String packname = tasks.get(0).topActivity.getPackageName();
            if (packname.equals(context.getPackageName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取屏幕状态栏高度
     */
    public static int getStatusHeight(Activity activity) {

        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取屏幕状态栏高度(包括Actionbar)
     *
     * @param activity
     */
    public static int getToolbarHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     */
    public static int getScreenWidth(Context context) {
        if (ScreenWidth == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            ScreenWidth = outMetrics.widthPixels;
        }
        return ScreenWidth;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     */
    public static int getScreenHeight(Context context) {
        if (ScreenHeight == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            ScreenHeight = outMetrics.heightPixels;
        }
        return ScreenHeight;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     */
    public static int getStatusHeight(Context context) {
        if (statusHeight <= 0) {
            try {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
                statusHeight = context.getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

}
