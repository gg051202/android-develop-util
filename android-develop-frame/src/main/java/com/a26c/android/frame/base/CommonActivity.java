package com.a26c.android.frame.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a26c.android.frame.R;
import com.a26c.android.frame.util.FrameAppManager;
import com.a26c.android.frame.util.FrameDensityUtils;

import java.util.ArrayList;
import java.util.List;

import static com.a26c.android.frame.util.FrameAppManager.getAppManager;


/**
 * @author hwz
 * @desc Activity 基类  使用方法 直接在getContainLayout中传入布局
 */

public abstract class CommonActivity extends AppCompatActivity {
    protected String TAG = getClass().getSimpleName();
    protected Context mContext;
    protected Activity mActivity;

    /**
     * 标题文字
     */
    private TextView titleTextView;
    /**
     * 子类的布局
     */
    protected ViewGroup mContainView;
    /**
     * 标题栏
     */
    private Toolbar titleBar;
    private List<PermissionData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        FrameAppManager.getAppManager().addActivity(this);
        mContainView = (ViewGroup) LayoutInflater.from(this).inflate(getContainLayout(), null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = FrameDensityUtils.dp2px(this, 49);
        super.setContentView(mContainView, params);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        mActivity = this;
        initTitleBar();
        init(savedInstanceState);
        setEvent();

    }


    public abstract int getContainLayout();

    public void leftOnClickListener(View view) {
        finish();
    }


    /**
     * 设置标题栏
     */
    private void initTitleBar() {
        ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
        titleBar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.frame_layout_toolbar, null);
        titleBar.setTitle("");
        titleTextView = (TextView) titleBar.findViewById(R.id.titleTextView);
        content.addView(titleBar, new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, FrameDensityUtils.dp2px(mContext, 49)));
        setSupportActionBar(titleBar);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    protected void setTitle(String title) {
        titleTextView.setText(title);
        setToolBar(true);
    }

    protected void setTitle(String title, boolean b) {
        titleTextView.setText(title);
        setToolBar(b);
    }

    private void setToolBar(boolean showNavigationIcon) {
        if (showNavigationIcon) {
            titleBar.setNavigationIcon(R.mipmap.frame_navicon_back);
            titleBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    leftOnClickListener(view);
                }
            });
        }
    }

    public Context getContext() {
        return mContext;
    }

    protected abstract void init(Bundle savedInstanceState);

    protected abstract void setEvent();


    /**
     * 将fragment显示在布局中
     */
    protected void replaceFragment(int layoutId, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(layoutId, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        getAppManager().removeActivity(this);
        super.onDestroy();

    }


    /**
     * 跳转Activity并且关闭其他全部
     */
    public void gotoActivityClearTop(Class<?> cls, Bundle bundle) {
        Intent intent;
        intent = new Intent(this, cls);
        if (null != bundle)
            intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 跳转Activity
     */
    public void gotoActivity(Class<?> cls, Bundle bundle) {
        Intent intent;
        intent = new Intent(this, cls);
        if (null != bundle)
            intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * 跳转Activity
     */
    public void gotoActivity(Class<?> cls) {
        Intent intent;
        intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * 跳转ActivityForResult
     */
    public void gotoActivityForResult(Class<?> cls, Bundle bundle, int key) {
        Intent intent = new Intent(this, cls);
        if (null != bundle)
            intent.putExtras(bundle);

        startActivityForResult(intent, key);
    }

    /**
     * 跳转ActivityForResult
     */
    public void gotoActivityForResult(Class<?> cls, int key) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, key);
    }

    /**
     * 为TextView添加右边图片
     */
    protected void viewDrawableRight(TextView view, int resid, int width, int height) {
        Drawable drawable = mContext.getResources().getDrawable(resid);
        // / 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, FrameDensityUtils.dp2px(this, width), FrameDensityUtils.dp2px(this, height));
        view.setCompoundDrawables(null, null, drawable, null);
    }

    /**
     * 为texteview添加左边图片
     */
    protected void viewDrawableLeft(TextView view, int resid, int width, int height) {
        Drawable drawable = mContext.getResources().getDrawable(resid);
        // / 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, FrameDensityUtils.dp2px(this, width), FrameDensityUtils.dp2px(this, height));
        view.setCompoundDrawables(drawable, null, null, null);
    }

    /**
     * 判断text控件内容是否为空
     */
    protected boolean isTvEmpty(TextView text, String msg) {
        if (text.getText().toString().trim().equals("")) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * 判断text控件内容是否为空
     */
    protected boolean isTvEmpty(TextView text) {
        if (text.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 显示导入的布局，比如在网络请求之后，显示当前界面
     */
    protected void showContainView() {
        if (mContainView != null) {
            mContainView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏导入的布局，在网络请求之前，隐藏当前界面
     */
    protected void hideContainView() {
        if (mContainView != null) {
            mContainView.setVisibility(View.GONE);
        }
    }


    private static final int REQUEST_PERMISSION = 1221;
    private OnCheckPermissionListener checkPermissionListener;

    protected void checkPermission(@NonNull OnCheckPermissionListener checkPermissionListener, String... permissions) {

        if (!isMarshmallow()) {//如果是6.0以下系统，不需要验证权限
            checkPermissionListener.success();
            return;
        }

        this.checkPermissionListener = checkPermissionListener;
        list = new ArrayList<>();
        for (String permission : permissions) {
            PermissionData data = new PermissionData();
            data.setPermissionName(permission);
            data.setGranted(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
            if (!data.isGranted()) {//对要申请的权限进行一次筛选，list里的数据为需要筛选的权限
                list.add(data);
            }
        }

        if (list.size() > 0) {
            String[] strs = new String[list.size()];
            int i = 0;
            for (PermissionData data : list) {
                strs[i++] = data.getPermissionName();
            }
            ActivityCompat.requestPermissions(this, strs, REQUEST_PERMISSION);
        } else {
            checkPermissionListener.success();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_PERMISSION) {
            return;
        }

        int isAllGranted = 0;
        for (int i = 0; i < list.size(); i++) {
            PermissionData item = list.get(i);
            item.setResult(grantResults[i] == PackageManager.PERMISSION_GRANTED);
            if (!item.isResult()) {
                isAllGranted++;//如果有一个申请结果失败，就自增1，表示申请失败了
            }
        }

        if (isAllGranted == 0) {
            checkPermissionListener.success();
        } else {
            checkPermissionListener.fail();
        }

    }

    class PermissionData {
        private String permissionName;
        /**
         * 该权限是否拥有
         */
        private boolean isGranted;
        /**
         * 申请权限的结果，true表示用户通过，false表示用户未通过
         */
        private boolean result;

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public boolean isGranted() {
            return isGranted;
        }

        public void setGranted(boolean granted) {
            isGranted = granted;
        }

        public String getPermissionName() {
            return permissionName;
        }

        public void setPermissionName(String permissionName) {
            this.permissionName = permissionName;
        }
    }

    public interface OnCheckPermissionListener {
        /**
         * 申请权限成功，所有权限全部允许才算申请成功
         */
        void success();

        /**
         * 申请权限失败
         */
        void fail();
    }

    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= 23;
    }


}

