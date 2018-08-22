package com.a26c.android.frame.widget;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.a26c.android.frame.R;
import com.a26c.android.frame.util.CommonUtils;
import com.a26c.android.frame.util.FrameDownloadUtil;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;

/**
 * 统一风格的dialog
 */
public class UpdateDialog implements View.OnClickListener {

    private AlertDialog alertDialog;
    private Activity activity;

    private NumberProgressBar mNumberProgressBar;
    private View bottomLayout1;
    private View bottomLayout2;

    private String mTitleName;
    private String mDescName;
    private String downloadUrl;
    private FrameDownloadUtil mDownloadUtil;

    public UpdateDialog(Activity activity) {
        this.activity = activity;

    }


    public void show() {
        if (alertDialog == null) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(activity).inflate(R.layout.frame_dialog_download, null);
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


            alertDialog = new AlertDialog.Builder(activity, R.style.frame_loading_dialog)
                    .setView(view)
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
        mDownloadUtil = new FrameDownloadUtil(activity);
        mDownloadUtil.setDownloadUrl(downloadUrl);
        mDownloadUtil.setFileName(fileName);
        mDownloadUtil.setOnDownloadListener(mDownloadListener);
        mDownloadUtil.startDownload();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitTextView) {
            startDownload();
        } else if (v.getId() == R.id.cancelTextView) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        } else if (v.getId() == R.id.cancelDownloadTextView) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            if (mDownloadUtil != null) {
                mDownloadUtil.cancel();
            }
        } else if (v.getId() == R.id.backgroundTextView) {
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
            CommonUtils.install(activity, file, false);
        }

        @Override
        public void err(String msg) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
        }
    };


    public UpdateDialog setDescName(String descName) {
        if (!TextUtils.isEmpty(descName)) {
            mDescName = descName;
        }
        return this;
    }

    public UpdateDialog setTitleName(String titleName) {
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

}

