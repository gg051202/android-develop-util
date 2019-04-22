package com.a26c.android.frame.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.a26c.android.frame.R;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * 带清除功能的EditText
 */
public class ClearEditText extends AppCompatEditText {
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean mHasFoucs;
    private Context context;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight() + AutoSizeUtils.dp2px(context, 8), getPaddingBottom());
        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            // throw new
            // NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = getResources().getDrawable(R.mipmap.frame_icon_delete);
        }

        int width = AutoSizeUtils.dp2px(context, 20);
        mClearDrawable.setBounds(0, 0, width, width);
        // 默认设置隐藏图标
        setClearIconVisible(false);
        // 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏

        setOnFocusChangeListener(onFocusChangeListener);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(watcher);
    }

    private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            mHasFoucs = hasFocus;
            if (hasFocus) {
                setClearIconVisible(getText().length() > 0);
            } else {
                setClearIconVisible(false);
            }
        }
    };

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mHasFoucs) {
                setClearIconVisible(s.length() > 0);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (onTextChangeListener != null) {
                onTextChangeListener.afterTextChanged(s);
            }
        }
    };

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight
                        ())));

                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }


    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    public interface OnTextChangeListener {
        void afterTextChanged(Editable s);
    }

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

    private OnTextChangeListener onTextChangeListener;

    public void destory() {

        setOnFocusChangeListener(null);
        // 设置输入框里面内容发生改变的监听
        removeTextChangedListener(watcher);
        watcher = null;
        onFocusChangeListener = null;
    }

}
