package com.a26c.android.frame.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guilinlin on 2016/11/18 15:14.
 * email 973635949@qq.com
 */
public class CommonUtils {

    private static final String TAG = "CommonUtils";


    /**
     * @return 返回true表示没有授权
     */
    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED;
    }

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F'};

    /**
     * 将字符串转md5，小写
     */
    public static String MD5(String inStr) {
        byte[] inStrBytes = inStr.getBytes();
        try {
            MessageDigest MD = MessageDigest.getInstance("MD5");
            MD.update(inStrBytes);
            byte[] mdByte = MD.digest();
            char[] str = new char[mdByte.length * 2];
            int k = 0;
            for (int i = 0; i < mdByte.length; i++) {
                byte temp = mdByte[i];
                str[k++] = hexDigits[temp >>> 4 & 0xf];
                str[k++] = hexDigits[temp & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取设备的型号
     *
     * @param context
     */
    public static String getDeviceId(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
    }

    /**
     * 获取版本名称 如 1.0.1
     *
     * @param context
     */
    public static String getVerName(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return info.versionName;
    }

    /**
     * 判断是否有网络连接
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo == null || !mNetworkInfo.isAvailable()) {
                return false;
            }
        }
        return true;
    }


    public static NetworkInfo getActiveNetwork(Context context) {
        if (context == null) return null;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (manager == null) return null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo;
    }

    public static boolean checkNet(Context context) {// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 将ip的整数形式转换成ip形式
     */
    private static String int2ip(int ipInt) {
        String sb = String.valueOf(ipInt & 0xFF) + "." +
                ((ipInt >> 8) & 0xFF) + "." +
                ((ipInt >> 16) & 0xFF) + "." +
                ((ipInt >> 24) & 0xFF);
        return sb;
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     */
    public static String getLocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return "请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
    }

    public static boolean isWifiConnect(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                return info.isConnected();
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void showKeyboard(Activity activity, boolean isShow) {
        if (activity == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        if (isShow) {
            if (activity.getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(activity.getCurrentFocus(), 0);
            }
        } else {
            if (activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }
    }

    public static void showKeyboardDelayed(final Activity activity, View focus) {
        final View viewToFocus = focus;
        if (focus != null) {
            focus.requestFocus();
        }

        viewToFocus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (viewToFocus == null || viewToFocus.isFocused()) {
                    showKeyboard(activity, true);
                }
            }
        }, 500);
    }


    public static void hideKeyboard(Activity activity, View view) {
        if (activity == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        imm.hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 重新设置ListView高度
     *
     * @param listView
     * @param max      listview的最大高度，0表示不限制
     */
    public static void setListViewHeightBasedOnChildren(final Context context, final ListView listView, final int max) {
        listView.post(new Runnable() {
            @Override
            public void run() {

                int maxHeight = FrameDensityUtils.dp2px(context, max);
                Adapter listAdapter = listView.getAdapter();
                if (listAdapter == null) {
                    return;
                }
                int totalHeight = 0;
                for (int i = 0; i < listAdapter.getCount(); i++) {
                    View listItem = listAdapter.getView(i, null, listView);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
                if (maxHeight != 0)
                    height = height > maxHeight ? maxHeight : height;
                params.height = height;

                listView.setLayoutParams(params);
            }
        });
    }

    /**
     * 计算GridView高度
     *
     * @paramGridView
     */
    public static void setGridViewHeightBasedOnChildren(final GridView gridView) {
        gridView.post(new Runnable() {
            @Override
            public void run() {
                // 获取GridView对应的Adapter
                BaseAdapter listAdapter = (BaseAdapter) gridView.getAdapter();
                if (listAdapter == null) {
                    return;
                }
                int rows;
                int columns = 0;
                int horizontalBorderHeight = 0;
                Class<?> clazz = gridView.getClass();
                try {
                    // 利用反射，取得每行显示的个数
                    Field column = clazz.getDeclaredField("mRequestedNumColumns");
                    column.setAccessible(true);
                    columns = (Integer) column.get(gridView);
                    // 利用反射，取得横向分割线高度
                    Field horizontalSpacing = clazz.getDeclaredField("mRequestedHorizontalSpacing");
                    horizontalSpacing.setAccessible(true);
                    horizontalBorderHeight = (Integer) horizontalSpacing.get(gridView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
                if (listAdapter.getCount() % columns > 0) {
                    rows = listAdapter.getCount() / columns + 1;
                } else {

                    rows = listAdapter.getCount() / columns;
                }
                int totalHeight = 0;
                for (int i = 0; i < rows; i++) { // 只计算每项高度*行数
                    View listItem = listAdapter.getView(i, null, gridView);
                    listItem.measure(0, 0); // 计算子项View 的宽高
                    totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
                }
                ViewGroup.LayoutParams params = gridView.getLayoutParams();
                params.height = totalHeight + horizontalBorderHeight * (rows - 1);// 最后加上分割线总高度
                gridView.setLayoutParams(params);
            }
        });

    }

    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static String getYyyyMmDdHhMm(Object time) {
        return getFormatTime(time, YYYY_MM_DD_HH_MM);
    }

    public static String getYyyyMmDdHhMmSs(Object time) {
        return getFormatTime(time, YYYY_MM_DD_HH_MM_SS);
    }

    public static String getHhMmSs(Object time) {
        return getFormatTime(time, HH_MM_SS);
    }

    public static String getYyyyMmDd(Object time) {
        return getFormatTime(time, YYYY_MM_DD);
    }


    /**
     * 将时间戳转换为字符串
     *
     * @param time   时间戳，可以是Long或者字符串，但是长度必须是10位
     * @param format
     */
    public static String getFormatTime(Object time, String format) {
        if (time == null)
            return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            if (time instanceof Long)
                return sdf.format(new Date((long) time * 1000));
            else if (time instanceof String)
                return sdf.format(Long.parseLong((String) time) * 1000);
            else if (time instanceof Integer)
                return sdf.format(new Date((long) (int) time * 1000));
            else
                return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 构造方法的字符格式这里如果小数不足2位,会以0补足.
     *
     * @param f 字符串或者float型
     */
    public static String getTwoPointNumber(Object f) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return getPointNumber(f, decimalFormat);
    }

    public static String getOnePointNumber(Object f) {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return getPointNumber(f, decimalFormat);
    }

    private static String getPointNumber(Object f, DecimalFormat decimalFormat) {
        if (f instanceof Float) {
            return decimalFormat.format((float) f);
        } else if (f instanceof String) {
            return decimalFormat.format(Float.parseFloat(f.toString()));
        } else if (f instanceof Double) {
            return decimalFormat.format(((Double) f).floatValue());
        } else if (f instanceof Integer) {
            return decimalFormat.format(((Integer) f).floatValue());
        } else if (f instanceof Long) {
            return decimalFormat.format(((Long) f).floatValue());
        } else {
            return "";
        }
    }


}
