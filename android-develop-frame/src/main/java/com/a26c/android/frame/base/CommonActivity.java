package com.a26c.android.frame.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
    public void gotoActivity(Class<?> cls ) {
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
}

