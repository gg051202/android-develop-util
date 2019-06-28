package com.a26c.android.frame.util;

import android.content.Context;
import androidx.recyclerview.widget.LinearSmoothScroller;

/**
 * RecyclerView滑动到某个ITEM，并且设置其为顶部
 */
public class TopSmoothScroller extends LinearSmoothScroller {
    public TopSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected int getHorizontalSnapPreference() {
        return SNAP_TO_START;//具体见源码注释
    }

    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START;//具体见源码注释
    }
}
