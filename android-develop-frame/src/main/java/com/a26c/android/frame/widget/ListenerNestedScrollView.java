package com.a26c.android.frame.widget;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * Created by guilinlin on 2018/11/14 09:17.
 * email 973635949@qq.com
 */
public class ListenerNestedScrollView extends NestedScrollView {
    public ListenerNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(l, t);
        }
    }

    private OnScrollListener mOnScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public void removeOnScrollListener() {

        mOnScrollListener = null;
    }

    public interface OnScrollListener {
        void onScroll(int x, int y);
    }

}
