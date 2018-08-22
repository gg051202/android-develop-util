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

    private String mDescName;
    private String mcancleName = "取消";
    private String mSubmitName = "后台运行";
    private String downloadUrl;
    private OnDialogCancleListener mOnDialogCancleListener;
    private OnDialogSubmitListener mDialogSubmitListener;

    public UpdateDialog(Activity activity) {
        this.activity = activity;

    }


    public void show() {
        if (alertDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(activity).inflate(R.layout.frame_dialog_download, null);
            TextView descTextView = view.findViewById(R.id.descTextView);
            TextView cancelTextView = view.findViewById(R.id.cancelTextView);
            TextView submitTextView = view.findViewById(R.id.submitTextView);
            mNumberProgressBar = view.findViewById(R.id.progressBar);

            descTextView.setText(mDescName);
            cancelTextView.setText(mcancleName);
            submitTextView.setText(mSubmitName);

            cancelTextView.setOnClickListener(this);
            submitTextView.setOnClickListener(this);

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
        startDownload();
    }

    /**
     * 开始下载
     */
    private void startDownload() {
        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
                File.separatorChar + CommonUtils.MD5(downloadUrl) + ".apk";
        FrameDownloadUtil frameDownloadUtil = new FrameDownloadUtil(activity);
        frameDownloadUtil.setDownloadUrl(downloadUrl);
        frameDownloadUtil.setFileName(fileName);
        frameDownloadUtil.setOnDownloadListener(new FrameDownloadUtil.OnDownloadListener() {
            @Override
            public void start() {

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
        });
        frameDownloadUtil.startDownload();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitTextView) {
            if (mDialogSubmitListener != null) {
                mDialogSubmitListener.submit();
            }
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }

        } else if (v.getId() == R.id.cancelTextView) {
            if (mOnDialogCancleListener != null) {
                mOnDialogCancleListener.cancle();
            }
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }

        }
    }


    public UpdateDialog setDescName(String descName) {
        if (!TextUtils.isEmpty(descName)) {
            mDescName = descName;
        }
        return this;
    }

    public UpdateDialog setDownloadUrl(String downloadUrl) {
        if (!TextUtils.isEmpty(downloadUrl)) {
            this.downloadUrl = downloadUrl;
        }
        return this;
    }

    public UpdateDialog setCancleName(String cancleName) {
        if (!TextUtils.isEmpty(cancleName)) {
            this.mcancleName = cancleName;
        }
        return this;
    }

    public UpdateDialog setSubmitName(String submitName) {
        if (!TextUtils.isEmpty(submitName)) {
            mSubmitName = submitName;
        }
        return this;
    }

    public UpdateDialog setOnDialogCancleListener(OnDialogCancleListener onDialogCancleListener) {
        mOnDialogCancleListener = onDialogCancleListener;
        return this;
    }

    public UpdateDialog setDialogSubmitListener(OnDialogSubmitListener dialogSubmitListener) {
        mDialogSubmitListener = dialogSubmitListener;
        return this;
    }

    public interface OnDialogSubmitListener {
        void submit();
    }

    public interface OnDialogCancleListener {
        void cancle();
    }

}

