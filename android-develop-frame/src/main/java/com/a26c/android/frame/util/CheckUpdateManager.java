package com.a26c.android.frame.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static android.content.Context.NOTIFICATION_SERVICE;

//new CheckUpdateManager.Builder(activity)
//        .desc(ss)
//        .isAutoCheck(isAutoUpdate)
//        .needUpdate(android.getVcode() > NormalUtil.getVersionCode(activity))
//        .title("更新提示")
//        .downloadUrl(android.getDownUrl())
//        .iconResourceId(R.drawable.jpush_notification_icon)
//        .build().show();

/**
 * Created by guilinlin on 2017/5/10 10:22.
 * email 973635949@qq.com
 * 下载的工具类
 */

public class CheckUpdateManager {

    private static final String TAG = "CheckUpdateManager";

    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 描述
     */
    private CharSequence desc;
    /**
     * 标题
     */
    private CharSequence title;
    /**
     * 通知的内容
     */
    private CharSequence contentTitle;
    /**
     * 通知栏的资源id
     */
    private int iconResourceId;
    /**
     * true表示是自动检测更新，当自动更新时如果是最新版本，不需要弹出"已是最新版本"的提示窗
     */
    private boolean isAutoCheck = false;
    /**
     * 两次更新的时间间隔,小时为单位
     */
    private int spaceTimeHour;

    /**
     * 是否需要更新
     */
    private boolean needUpdate;

    private Context mContext;
    private long totalSize = 0;
    private HttpURLConnection mConnection;
    private File downloadFile;
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;
    private final int NOTIFICATION_ID = 222;

    private CheckUpdateManager(Builder builder) {
        downloadUrl = builder.downloadUrl;
        desc = builder.desc;
        title = builder.title;
        iconResourceId = builder.iconResourceId;
        isAutoCheck = builder.isAutoCheck;
        needUpdate = builder.needUpdate;
        mContext = builder.mContext;
        contentTitle = builder.contentTitle;
        spaceTimeHour = builder.spaceTimeHour;


        mManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(iconResourceId)
                .setPriority(2);
    }


    public void showDialog() {
        if (needUpdate) {
            //如果是自动更新但是距离上次点击"明天再说"的时间还没超过一天，就不弹出
            if (isAutoCheck && !checkLastTimeIsOver()) {
                Log.i(TAG, "需要更新但是用户设置了提醒间隔");
                return;
            }
            new AlertDialog.Builder(mContext).
                    setTitle(title)
                    .setMessage(desc)
                    .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDownload();
                        }
                    })
                    .setNeutralButton("下次再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveLatestTime();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();

        } else {
            if (!isAutoCheck) {//如果不是自动更新，才需要弹出提示窗
                new AlertDialog.Builder(mContext).
                        setTitle("提示")
                        .setMessage("当前版本已是最新版本")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();
            }

        }

    }


    /**
     * 开始下载，下载完成自动更新
     */
    public void startDownload() {


        Observable
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        startDownload(subscriber, downloadUrl);
                    }
                })
                .sample(1, TimeUnit.SECONDS)//过滤 1秒只能更新一次
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidScheduler.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onStart() {
                        mBuilder.setProgress(100, 0, false)
                                .setContentTitle(contentTitle)
                                .setDefaults(Notification.DEFAULT_VIBRATE);
                        final Notification notification = mBuilder.build();
                        notification.flags = Notification.FLAG_NO_CLEAR;
                        mManager.notify(NOTIFICATION_ID, notification);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        String msg = "下载失败";
                        if (e instanceof DownLoadError) {
                            msg = ((DownLoadError) e).getMessage();
                        }
                        final Notification notification = mBuilder.setContentTitle(msg)
                                .setDefaults(0).build();

                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        mManager.notify(NOTIFICATION_ID, notification);
                    }

                    @Override
                    public void onNext(Integer progress) {
                        if (progress == 100) {
                            mManager.cancel(NOTIFICATION_ID);
                            install(mContext, downloadFile, false);
                        } else {
                            if (progress % 5 == 0 || progress >= 1) {
                                mBuilder.setProgress(100, progress, false);
                                final Notification notification = mBuilder.setDefaults(0).build();
                                notification.flags = Notification.FLAG_NO_CLEAR;
                                mManager.notify(NOTIFICATION_ID, notification);
                            }
                        }
                    }
                });

    }

    private void startDownload(Subscriber<? super Integer> subscriber, String url) {
        if (TextUtils.isEmpty(downloadUrl)) {
            throw new DownLoadError(DownLoadError.DOWNLOAD_URL_ERR);
        }

        downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                CommonUtils.MD5(downloadUrl) + ".apk");


        try {

            mConnection = create(new URL(url));

            mConnection.connect();

            checkStatus();

            totalSize = mConnection.getContentLength();

            if (downloadFile.exists() && downloadFile.length() == totalSize) {
                Log.i(TAG, "文件已存在：" + downloadFile.getAbsolutePath());
                subscriber.onNext(100);
                return;
            }

            CommonUtils.clearFile(downloadFile);

            Log.i(TAG, "开始下载，将要保存到的文件路径：" + downloadFile.getAbsolutePath());

            int bytesCopied = copy(mConnection.getInputStream(), new LoadingRandomAccessFile(subscriber, downloadFile));

            if (bytesCopied != totalSize && totalSize != -1) {
                throw new DownLoadError(DownLoadError.DOWNLOAD_INCOMPLETE);
            }
            subscriber.onNext(100);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DownLoadError(DownLoadError.DOWNLOAD_NETWORK_IO);
        }
    }

    private HttpURLConnection create(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "application/*");
        connection.setConnectTimeout(10000);
        return connection;
    }

    private void checkNetwork() throws DownLoadError {
        if (!checkNetwork(mContext)) {
            throw new DownLoadError(DownLoadError.DOWNLOAD_NETWORK_BLOCKED);
        }
    }

    public static boolean checkNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void checkStatus() throws IOException, DownLoadError {
        int statusCode = mConnection.getResponseCode();
        if (statusCode != 200 && statusCode != 206) {
            throw new DownLoadError(DownLoadError.DOWNLOAD_HTTP_STATUS, "" + statusCode);
        }
    }

    private int copy(InputStream in, RandomAccessFile out) {

        int BUFFER_SIZE = 1024;
        BufferedInputStream bis = new BufferedInputStream(in, BUFFER_SIZE);
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            out.seek(out.length());
            int bytes = 0;
            while (true) {
                int n = bis.read(buffer, 0, BUFFER_SIZE);
                if (n == -1) {
                    break;
                }
                out.write(buffer, 0, n);
                bytes += n;
                checkNetwork();
            }
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                bis.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private final class LoadingRandomAccessFile extends RandomAccessFile {

        private Subscriber<? super Integer> subscriber;
        private int mBytesLoaded = 0;
        private int oldProgress;

        public LoadingRandomAccessFile(Subscriber<? super Integer> subscriber, File file) throws FileNotFoundException {
            super(file, "rw");
            this.subscriber = subscriber;
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);
            mBytesLoaded += count;
            if (mBytesLoaded < totalSize) {
                int progress = (int) ((float) (mBytesLoaded) / totalSize * 100);
                if (progress % 5 == 0) {
                    if (oldProgress != progress) {
                        oldProgress = progress;
                        subscriber.onNext(progress);
                    }
                }
            }
        }
    }


    public static class DownLoadError extends RuntimeException {

        public final int code;

        public DownLoadError(int code) {
            this(code, null);
        }

        public DownLoadError(int code, String message) {
            super(make(code, message));
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return messages.get(code);
        }

        public static String getMessage(int code) {
            return messages.get(code);
        }

        private static String make(int code, String message) {
            String m = messages.get(code);
            if (m == null) {
                return message;
            }
            if (message == null) {
                return m;
            }
            return m + "(" + message + ")";
        }


        public static final int CHECK_UNKNOWN = 2001;
        public static final int CHECK_NO_WIFI = 2002;
        public static final int CHECK_NO_NETWORK = 2003;
        public static final int CHECK_NETWORK_IO = 2004;
        public static final int CHECK_HTTP_STATUS = 2005;
        public static final int CHECK_PARSE = 2006;


        public static final int DOWNLOAD_UNKNOWN = 3001;
        public static final int DOWNLOAD_CANCELLED = 3002;
        public static final int DOWNLOAD_DISK_NO_SPACE = 3003;
        public static final int DOWNLOAD_DISK_IO = 3004;
        public static final int DOWNLOAD_NETWORK_IO = 3005;
        public static final int DOWNLOAD_NETWORK_BLOCKED = 3006;
        public static final int DOWNLOAD_NETWORK_TIMEOUT = 3007;
        public static final int DOWNLOAD_HTTP_STATUS = 3008;
        public static final int DOWNLOAD_INCOMPLETE = 3009;
        public static final int DOWNLOAD_VERIFY = 3010;
        public static final int DOWNLOAD_URL_ERR = 3011;

        public static final SparseArray<String> messages = new SparseArray<>();

        static {

            messages.append(CHECK_UNKNOWN, "查询更新失败：未知错误");
            messages.append(CHECK_NO_WIFI, "查询更新失败：没有 WIFI");
            messages.append(CHECK_NO_NETWORK, "查询更新失败：没有网络");
            messages.append(CHECK_NETWORK_IO, "查询更新失败：网络异常");
            messages.append(CHECK_HTTP_STATUS, "查询更新失败：错误的HTTP状态");
            messages.append(CHECK_PARSE, "查询更新失败：解析错误");

            messages.append(DOWNLOAD_UNKNOWN, "下载失败：未知错误");
            messages.append(DOWNLOAD_CANCELLED, "下载失败：下载被取消");
            messages.append(DOWNLOAD_DISK_NO_SPACE, "下载失败：磁盘空间不足");
            messages.append(DOWNLOAD_DISK_IO, "下载失败：磁盘读写错误");
            messages.append(DOWNLOAD_NETWORK_IO, "下载失败：网络异常");
            messages.append(DOWNLOAD_NETWORK_BLOCKED, "下载失败：网络中断");
            messages.append(DOWNLOAD_NETWORK_TIMEOUT, "下载失败：网络超时");
            messages.append(DOWNLOAD_HTTP_STATUS, "下载失败：错误的HTTP状态");
            messages.append(DOWNLOAD_INCOMPLETE, "下载失败：下载不完整");
            messages.append(DOWNLOAD_VERIFY, "下载失败：校验错误");
            messages.append(DOWNLOAD_URL_ERR, "下载失败：下载地址错误");
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


    public static final class Builder {
        private String downloadUrl;
        private CharSequence desc;
        private CharSequence title;
        private int iconResourceId;
        private boolean isAutoCheck;
        private boolean needUpdate;
        private Context mContext;
        private CharSequence contentTitle;
        private int spaceTimeHour;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder downloadUrl(String val) {
            downloadUrl = val;
            return this;
        }

        public Builder spaceTimeHour(int val) {
            spaceTimeHour = val;
            return this;
        }


        public Builder contentTitle(CharSequence val) {
            contentTitle = val;
            return this;
        }

        public Builder desc(CharSequence val) {
            desc = val;
            return this;
        }

        public Builder title(CharSequence val) {
            title = val;
            return this;
        }

        public Builder iconResourceId(int val) {
            iconResourceId = val;
            return this;
        }

        public Builder isAutoCheck(boolean val) {
            isAutoCheck = val;
            return this;
        }

        public Builder needUpdate(boolean val) {
            needUpdate = val;
            return this;
        }

        public CheckUpdateManager build() {
            return new CheckUpdateManager(this);
        }
    }

    /**
     * 保存点击"明天再说"时的时间
     */
    private void saveLatestTime() {
        FrameSPUtils.put(mContext, "checkUpdate_tomorrow", System.currentTimeMillis());
    }

    /**
     * 检查距离上次点击"明天再说"是否超过1天
     *
     * @return true表示超过
     */
    private boolean checkLastTimeIsOver() {
        long latest = (long) FrameSPUtils.get(mContext, "checkUpdate_tomorrow", 0L);
        return ((System.currentTimeMillis() - latest) > (spaceTimeHour == 0 ? 24 : spaceTimeHour * 3600000L));//24*60*60*1000
    }

}
