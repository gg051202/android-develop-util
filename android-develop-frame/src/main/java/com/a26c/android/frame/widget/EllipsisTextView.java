package com.a26c.android.frame.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by MrYan on 2018/9/11.
 * 监听是否触发 ellipsize 属性的TextView
 */

public class EllipsisTextView extends androidx.appcompat.widget.AppCompatTextView {

    private OnEllipsisListener onEllipsisListener;

    public EllipsisTextView(Context context) {
        super(context);
        init();
    }

    public EllipsisTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EllipsisTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        //这里未把观察者注销，是因为TextView宽高是随时变化的，如果是固定的最好根据需要注销掉
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                analyzeProcess();
            }
        });
    }

    /**
     * 通过 layout 的 getEllipsisCount(int line) 方法，来获取被省略的部分数量
     * 为0时就是没省略
     * 在利用自定义的监听器返回给待操作的对象；
     */
    private void analyzeProcess() {
        Layout layout = getLayout();//拿到Layout
        int line = getLineCount();//获取文字行数
        if (line > 0) {
            int ellipsisCount = layout.getEllipsisCount(line - 1);
            //ellipsisCount > 0 时，说明省略生效
            if (onEllipsisListener != null) {
                onEllipsisListener.onEllipsis(this, ellipsisCount > 0, ellipsisCount);
            }
        }
    }

    public void setOnEllipsisListener(OnEllipsisListener onEllipsisListener) {
        this.onEllipsisListener = onEllipsisListener;
    }

    /**
     * 自定义监听器
     * boolean 省略是否生效  ellipsisCount 省略部分字数
     */
    public interface OnEllipsisListener {
        void onEllipsis(TextView textView, boolean isEllipsis, int ellipsisCount);
    }

}
