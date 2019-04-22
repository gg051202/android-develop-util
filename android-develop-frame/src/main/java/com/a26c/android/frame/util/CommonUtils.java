package com.a26c.android.frame.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    @SuppressLint("MissingPermission")
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

    /**
     * 如果文件存在删除并重新创建，如果不存在创建
     */
    public static void clearFile(File file) {
        if (file.isFile() && file.exists()) {
            if (file.delete()) {
                Log.i("", "删除单个文件" + file.getAbsolutePath() + "成功！");
                try {
                    if (file.createNewFile()) {
                        Log.i("", "创建文件成功");
                    } else {
                        Log.i("", "创建文件失败");
                    }
                } catch (IOException e) {
                    Log.i("", "创建文件失败");
                    e.printStackTrace();
                }
            } else {
                Log.i("", "删除单个文件" + file.getAbsolutePath() + "失败！");
            }
        } else {
            try {
                if (file.createNewFile()) {
                    Log.i("", "创建文件成功");
                } else {
                    Log.i("", "创建文件失败");
                }
            } catch (IOException e) {
                Log.i("", "创建文件失败");
                e.printStackTrace();
            }
        }
    }


    /**
     * Glide对文件缓存时, 采用SHA-256加密算法, 所以如果需要获得图片, 需要将获得的文件copy一份
     * oldPath: 图片缓存的路径
     * newPath: 图片缓存copy的路径
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteRead;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读/写检查
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 只读检查
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 应根据实际展示需要，压缩图片，而不是直接显示原图。手机屏幕比较小，
     * 直接显示原图，并不会增加视觉上的收益，但是却会耗费大量宝贵的内存。
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 首先通过 inJustDecodeBounds=true 获得图片的尺寸
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 然后根据图片分辨率以及我们实际需要展示的大小，计算压缩率
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 设置压缩率，并解码
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * 保存图片到系统相册
     */
    public static void savePictureToAlbum(Context context, Bitmap bitmap, String title, String desc) {
        String filePath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, desc);
        if (!TextUtils.isEmpty(filePath)) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
            Toast.makeText(context, "已保存，请在相册中查看", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "保存图片失败", Toast.LENGTH_LONG).show();
        }
    }


    public static void install(Context context, File file, boolean force) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        } else {//修复7.0无法更新
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (force) {
            System.exit(0);
        }
    }


}
