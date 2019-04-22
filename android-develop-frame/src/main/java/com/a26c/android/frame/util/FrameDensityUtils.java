package com.a26c.android.frame.util;

import android.content.Context;

/**
 * 常用单位转换的辅助类
 *
 * @author zhy
 */
public class FrameDensityUtils {
    private FrameDensityUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * px转dp
     */
    public static float px2dp(Context context ,float pxVal) {
        final float scale =context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     */
    public static float px2sp(Context context ,float pxVal) {
        return (pxVal /context.getResources().getDisplayMetrics().scaledDensity);
    }

}
