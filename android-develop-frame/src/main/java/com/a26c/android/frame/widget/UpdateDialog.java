package com.a26c.android.frame.widget;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.a26c.android.frame.R;
import com.a26c.android.frame.util.CommonUtils;
import com.a26c.android.frame.util.FrameDownloadUtil;
import com.a26c.android.frame.util.FrameSPUtils;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;

/**
 * 统一风格的dialog
 */
public class UpdateDialog implements View.OnClickListener {

    private static final String TAG = "UpdateDialog";

    private AlertDialog alertDialog;
    private Activity mActivity;

    private NumberProgressBar mNumberProgressBar;
    private View bottomLayout1;
    private View bottomLayout2;

    private CharSequence mTitleName;
    private CharSequence mDescName;
    private String downloadUrl;
    private FrameDownloadUtil mDownloadUtil;


    /**
     * true表示是自动检测更新，当自动更新时如果是最新版本，不需要弹出"已是最新版本"的提示窗
     */
    private boolean isAutoCheck = false;
    /**
     * 是否需要更新，根据这个值判断，如果需要更新，弹出更新框，如果不需要更新，弹出"已是最新版本提示框"
     */
    private boolean needUpdate = false;
    /**
     * 点击"暂不更新"，下次再弹窗更新窗口的间隔,小时为单位
     */
    private int spaceTimeHour = 8;

    public UpdateDialog(Activity activity) {
        this.mActivity = activity;

    }

    public void show() {
        if (needUpdate) {
            update();
        } else {
            if (!isAutoCheck) {//如果不是自动更新，才需要弹出提示窗
                new AlertDialog.Builder(mActivity).
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

    private void update() {
        //如果是自动更新但是距离上次点击"明天再说"的时间还没超过一天，就不弹出
        if (isAutoCheck && !checkLastTimeIsOver()) {
            Log.i(TAG, "需要更新但是用户设置了提醒间隔");
            return;
        }


        if (alertDialog == null) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(mActivity).inflate(R.layout.frame_dialog_download, null);
            TextView titleTextView = view.findViewById(R.id.titleTextView);
            TextView descTextView = view.findViewById(R.id.descTextView);
            mNumberProgressBar = view.findViewById(R.id.progressBar);
            bottomLayout1 = view.findViewById(R.id.bottomLayout1);
            bottomLayout2 = view.findViewById(R.id.bottomLayout2);

            titleTextView.setText(mTitleName);
            descTextView.setText(mDescName);

            view.findViewById(R.id.cancelDownloadTextView).setOnClickListener(this);
            view.findViewById(R.id.backgroundTextView).setOnClickListener(this);
            view.findViewById(R.id.cancelTextView).setOnClickListener(this);
            view.findViewById(R.id.submitTextView).setOnClickListener(this);

            mNumberProgressBar.setProgress(0);


            alertDialog = new AlertDialog.Builder(mActivity, R.style.frame_loading_dialog)
                    .setView(view)
                    .setCancelable(false)
                    .show();
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
            }

        }
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    /**
     * 开始下载
     */
    private void startDownload() {
        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
                File.separatorChar + CommonUtils.MD5(downloadUrl) + ".apk";
        mDownloadUtil = new FrameDownloadUtil(mActivity);
        mDownloadUtil.setDownloadUrl(downloadUrl);
        mDownloadUtil.setFileName(fileName);
        mDownloadUtil.setOnDownloadListener(mDownloadListener);
        mDownloadUtil.startDownload();
    }

    @Override
    public void onClick(View v) {
        //立即更新
        if (v.getId() == R.id.submitTextView) {
            startDownload();
        }
        //暂不更新
        else if (v.getId() == R.id.cancelTextView) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            saveLatestTime();
        }
        //取消下载
        else if (v.getId() == R.id.cancelDownloadTextView) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            if (mDownloadUtil != null) {
                mDownloadUtil.cancel();
            }
        }
        //后台运行
        else if (v.getId() == R.id.backgroundTextView) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
    }

    private FrameDownloadUtil.OnDownloadListener mDownloadListener = new FrameDownloadUtil.OnDownloadListener() {
        @Override
        public void start() {
            mNumberProgressBar.setVisibility(View.VISIBLE);
            bottomLayout1.setVisibility(View.GONE);
            bottomLayout2.setVisibility(View.VISIBLE);

        }

        @Override
        public void onProgress(int progress) {
            if (progress == 100) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            } else {
                if (progress >= 1) {
                    mNumberProgressBar.setProgress(progress);
                }
            }
        }

        @Override
        public void success(File file) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            CommonUtils.install(mActivity, file, false);
        }

        @Override
        public void err(String msg) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
        }
    };


    public UpdateDialog setDescName(CharSequence descName) {
        if (!TextUtils.isEmpty(descName)) {
            mDescName = descName;
        }
        return this;
    }

    public UpdateDialog setTitleName(CharSequence titleName) {
        if (!TextUtils.isEmpty(titleName)) {
            mTitleName = titleName;
        }
        return this;
    }

    public UpdateDialog setDownloadUrl(String downloadUrl) {
        if (!TextUtils.isEmpty(downloadUrl)) {
            this.downloadUrl = downloadUrl;
        }
        return this;
    }

    public UpdateDialog setIsAutoCheck(boolean isAutoCheck) {
        this.isAutoCheck = isAutoCheck;
        return this;
    }

    public UpdateDialog setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
        return this;
    }

    public UpdateDialog setSpaceTimeHour(int spaceTimeHour) {
        this.spaceTimeHour = spaceTimeHour;
        return this;
    }

    /**
     * 保存点击"明天再说"时的时间
     */
    private void saveLatestTime() {
        FrameSPUtils.put(mActivity, "checkUpdate_tomorrow_dialog", System.currentTimeMillis());
    }

    /**
     * 检查距离上次点击"明天再说"是否超过1天
     *
     * @return true表示超过
     */
    private boolean checkLastTimeIsOver() {
        long latest = (long) FrameSPUtils.get(mActivity, "checkUpdate_tomorrow_dialog", 0L);
        return ((System.currentTimeMillis() - latest) > (spaceTimeHour == 0 ? 24 : spaceTimeHour * 3600000L));//24*60*60*1000
    }
}

