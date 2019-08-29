package com.a26c.android.frame.widget;


import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.a26c.android.frame.util.DialogFactory;
import com.a26c.android.frame.util.DownloadUtil;
import com.a26c.android.frame.util.FrameSPUtils;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;

/**
 * 统一风格的dialog
 * <p>
 * SpannableString spannableString = new SpannableString("12312312313");
 * spannableString.setSpan(new ForegroundColorSpan(0xffff0000), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
 * <p>
 * new UpdateDialog(MainActivity.this)
 * .setIsAutoCheck(false)
 * .setNeedUpdate(true)
 * .setTitleName(spannableString)
 * .setSpaceTimeHour(8)
 * .setDescName("更新日志")
 * .setDownloadUrl("http://imtt.dd.qq.com/16891/A92C29C6A2255AD59E082A9B6336AEAD.apk?fsname=com.lotus.game.popthewheel.android_1.0.1_2.apk&csr=1bbd")
 * .show();
 */
public class UpdateDialog implements View.OnClickListener {

    private static final String TAG = "UpdateDialog";
    private AlertDialog mAlertDialog;
    private Activity mActivity;
    private NumberProgressBar mNumberProgressBar;
    private View mBottomLayout1;
    private View mBottomLayout2;
    private CharSequence mTitleName;
    private CharSequence mDescName;
    private CharSequence mSubmitName = "抢先体验";
    private CharSequence mCancleName = "留在旧版";
    private String mDownloadUrl;
    private DownloadUtil mDownloadUtil;


    /**
     * true表示是自动检测更新，当自动更新时如果是最新版本，不需要弹出"已是最新版本"的提示窗
     */
    private boolean mIsAutoCheck = false;
    /**
     * 是否需要更新，根据这个值判断，如果需要更新，弹出更新框，如果不需要更新，弹出"已是最新版本提示框"
     */
    private boolean mNeedUpdate = false;
    /**
     * 点击"暂不更新"，下次再弹窗更新窗口的间隔,小时为单位
     */
    private int mSpaceTimeHour = 8;

    public UpdateDialog(Activity activity) {
        this.mActivity = activity;

    }

    public void show() {
        if (mNeedUpdate) {
            update();
        } else {
            if (!mIsAutoCheck) {//如果不是自动更新，才需要弹出提示窗
                new AlertDialog.Builder(mActivity, R.style.FrameDefaultDialogStyle)
                        .setTitle("提示")
                        .setMessage("当前版本已是最新版本")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create()
                        .show();
            }
        }
    }

    private void update() {
        //如果是自动更新但是距离上次点击"明天再说"的时间还没超过一天，就不弹出
        if (mIsAutoCheck && !checkLastTimeIsOver()) {
            Log.i(TAG, "需要更新但是用户设置了提醒间隔");
            return;
        }

        if (mAlertDialog == null) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(mActivity).inflate(R.layout.frame_dialog_download, null);
            TextView titleTextView = view.findViewById(R.id.titleTextView);
            TextView descTextView = view.findViewById(R.id.descTextView);
            TextView submitTextView = view.findViewById(R.id.submitTextView);
            TextView cancelTextView = view.findViewById(R.id.cancelTextView);
            mNumberProgressBar = view.findViewById(R.id.progressBar);
            mBottomLayout1 = view.findViewById(R.id.bottomLayout1);
            mBottomLayout2 = view.findViewById(R.id.bottomLayout2);

            titleTextView.setText(mTitleName);
            descTextView.setText(mDescName);
            submitTextView.setText(mSubmitName);
            cancelTextView.setText(mCancleName);

            view.findViewById(R.id.cancelDownloadTextView).setOnClickListener(this);
            view.findViewById(R.id.backgroundTextView).setOnClickListener(this);
            view.findViewById(R.id.cancelTextView).setOnClickListener(this);
            view.findViewById(R.id.submitTextView).setOnClickListener(this);

            mNumberProgressBar.setProgress(0);


            mAlertDialog = new AlertDialog.Builder(mActivity, R.style.frame_loading_dialog)
                    .setView(view)
                    .setCancelable(false)
                    .show();
            Window window = mAlertDialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
            }

        }
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }

    /**
     * 开始下载
     */
    public void startDownload() {
        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
                File.separatorChar + CommonUtils.MD5(mDownloadUrl) + ".apk";
        mDownloadUtil = new DownloadUtil(mActivity);
        mDownloadUtil.setOnDownloadListener(mDownloadListener);
        mDownloadUtil.startDownload(fileName, mDownloadUrl);
    }

    @Override
    public void onClick(View v) {
        //立即更新
        if (v.getId() == R.id.submitTextView) {
            if (isRunningBackground()) {
                dismissDialog();
                DialogFactory.show(mActivity, "提示", "更新程序正在后台运行，请稍候", "确定", null);
                return;
            }
            if (mOnUpdateListener != null) {
                dismissDialog();
                mOnUpdateListener.submit(this);
            }
        }
        //暂不更新
        else if (v.getId() == R.id.cancelTextView) {
            dismissDialog();
            saveLatestTime();
        }
        //取消下载
        else if (v.getId() == R.id.cancelDownloadTextView) {
            dismissDialog();
            if (mDownloadUtil != null) {
                mDownloadUtil.cancel();
            }
        }
        //后台运行
        else if (v.getId() == R.id.backgroundTextView) {
            dismissDialog();
            setIsRunningBackground(true);
        }
    }

    private void dismissDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    /**
     * 下载的监听事件
     */
    private DownloadUtil.OnDownloadListener mDownloadListener = new DownloadUtil.OnDownloadListener() {
        @Override
        public void start() {
            mNumberProgressBar.setVisibility(View.VISIBLE);
            mBottomLayout1.setVisibility(View.GONE);
            mBottomLayout2.setVisibility(View.VISIBLE);

        }

        @Override
        public void onProgress(int progress) {
            if (progress == 100) {
                dismissDialog();
            } else {
                if (progress >= 1) {
                    mNumberProgressBar.setProgress(progress);
                }
            }
        }

        @Override
        public void success(File file) {
            dismissDialog();
            CommonUtils.install(mActivity, file, false);
            setIsRunningBackground(false);
        }

        @Override
        public void err(String msg) {
            dismissDialog();
            Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
            setIsRunningBackground(false);
        }
    };

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
        return ((System.currentTimeMillis() - latest) > (mSpaceTimeHour == 0 ? 24 : mSpaceTimeHour * 3600000L));//24*60*60*1000
    }

    private void setIsRunningBackground(boolean isRunningBackground) {
        FrameSPUtils.put(mActivity, "setIsRunningBackground_update", isRunningBackground);
    }

    private boolean isRunningBackground() {
        return (boolean) FrameSPUtils.get(mActivity, "setIsRunningBackground_update", false);
    }


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
            this.mDownloadUrl = downloadUrl;
        }
        return this;
    }

    public UpdateDialog setIsAutoCheck(boolean isAutoCheck) {
        this.mIsAutoCheck = isAutoCheck;
        return this;
    }

    public UpdateDialog setNeedUpdate(boolean needUpdate) {
        this.mNeedUpdate = needUpdate;
        return this;
    }

    public UpdateDialog setSpaceTimeHour(int spaceTimeHour) {
        this.mSpaceTimeHour = spaceTimeHour;
        return this;
    }

    public CharSequence getSubmitName() {
        return mSubmitName;
    }

    public UpdateDialog setSubmitName(CharSequence submitName) {
        mSubmitName = submitName;
        return this;
    }

    public CharSequence getCancleName() {
        return mCancleName;
    }

    public UpdateDialog setCancleName(CharSequence cancleName) {
        mCancleName = cancleName;
        return this;
    }

    public interface OnUpdateListener {
        void submit(UpdateDialog updateDialog);
    }

    private OnUpdateListener mOnUpdateListener;

    public void setOnUpdateListener(OnUpdateListener l) {
        mOnUpdateListener = l;
    }

    public AlertDialog getAlertDialog() {
        return mAlertDialog;
    }

    public void setAlertDialog(AlertDialog alertDialog) {
        mAlertDialog = alertDialog;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public NumberProgressBar getNumberProgressBar() {
        return mNumberProgressBar;
    }

    public void setNumberProgressBar(NumberProgressBar numberProgressBar) {
        mNumberProgressBar = numberProgressBar;
    }

    public View getBottomLayout1() {
        return mBottomLayout1;
    }

    public void setBottomLayout1(View bottomLayout1) {
        mBottomLayout1 = bottomLayout1;
    }

    public View getBottomLayout2() {
        return mBottomLayout2;
    }

    public void setBottomLayout2(View bottomLayout2) {
        mBottomLayout2 = bottomLayout2;
    }

    public CharSequence getTitleName() {
        return mTitleName;
    }

    public CharSequence getDescName() {
        return mDescName;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public DownloadUtil getDownloadUtil() {
        return mDownloadUtil;
    }

    public void setDownloadUtil(DownloadUtil downloadUtil) {
        mDownloadUtil = downloadUtil;
    }

    public boolean isAutoCheck() {
        return mIsAutoCheck;
    }

    public void setAutoCheck(boolean autoCheck) {
        mIsAutoCheck = autoCheck;
    }

    public boolean isNeedUpdate() {
        return mNeedUpdate;
    }

    public int getSpaceTimeHour() {
        return mSpaceTimeHour;
    }

    public DownloadUtil.OnDownloadListener getDownloadListener() {
        return mDownloadListener;
    }

    public void setDownloadListener(DownloadUtil.OnDownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    public OnUpdateListener getOnUpdateListener() {
        return mOnUpdateListener;
    }
}

