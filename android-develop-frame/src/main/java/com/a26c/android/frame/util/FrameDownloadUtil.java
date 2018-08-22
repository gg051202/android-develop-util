package com.a26c.android.frame.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

/**
 * Created by guilinlin on 2018/8/6 11:32.
 * email 973635949@qq.com
 */
public class FrameDownloadUtil {


    private static final String TAG = "DownloadUtil";

    private Activity activity;
    private String downloadUrl;
    private long totalSize = 0;
    private File downloadFile;
    private HttpURLConnection mConnection;
    private String fileName;
    private Subscriber<Integer> mSubscriber;


    public FrameDownloadUtil(Activity activity ) {
        this.activity = activity;
    }


    public void startDownload() {
        Log.i(TAG, "start download");
        mSubscriber = new Subscriber<Integer>() {
            @Override
            public void onStart() {
                if (onDownloadListener != null) {
                    onDownloadListener.start();
                }
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
                if (onDownloadListener != null) {
                    onDownloadListener.err(msg);
                }
            }

            @Override
            public void onNext(Integer progress) {
                if (progress == 100) {
                    if (onDownloadListener != null) {
                        onDownloadListener.success(downloadFile);
                    }
                } else {
                    if (progress >= 1) {
                        if (onDownloadListener != null) {
                            onDownloadListener.onProgress(progress);
                        }
                    }
                }
            }
        };
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
                .subscribe(mSubscriber);
    }

    public interface OnDownloadListener {
        void start();

        void onProgress(int progress);

        void success(File file);

        void err(String msg);

    }

    private OnDownloadListener onDownloadListener;

    public void setOnDownloadListener(OnDownloadListener l) {
        onDownloadListener = l;
    }


    private void startDownload(Subscriber<? super Integer> subscriber, String url) {
        if (TextUtils.isEmpty(downloadUrl)) {
            throw new DownLoadError(DownLoadError.DOWNLOAD_URL_ERR);
        }

        downloadFile = new File(fileName);

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

    private void checkNetwork() throws CheckUpdateManager.DownLoadError {
        if (!checkNetwork(activity)) {
            throw new CheckUpdateManager.DownLoadError(CheckUpdateManager.DownLoadError.DOWNLOAD_NETWORK_BLOCKED);
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

    private void checkStatus() throws IOException, CheckUpdateManager.DownLoadError {
        int statusCode = mConnection.getResponseCode();
        if (statusCode != 200 && statusCode != 206) {
            throw new CheckUpdateManager.DownLoadError(CheckUpdateManager.DownLoadError.DOWNLOAD_HTTP_STATUS, "" + statusCode);
        }
    }


    public void cancel() {
        if (mSubscriber != null && !mSubscriber.isUnsubscribed()) {
            mSubscriber.unsubscribe();
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

    private HttpURLConnection create(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "application/*");
        connection.setConnectTimeout(10000);
        return connection;
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
                if (oldProgress != progress) {
                    oldProgress = progress;
                    subscriber.onNext(progress);
                    Log.i(TAG, "progress:" + progress);
                }
            }
        }
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public OnDownloadListener getOnDownloadListener() {
        return onDownloadListener;
    }
}
