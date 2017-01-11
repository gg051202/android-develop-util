package com.a26c.android.frame.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MutiItemDecoration extends RecyclerView.ItemDecoration {

    public enum Type {
        VERTICAL, HORIZONTAL, ALL
    }

    private Type type;//分割线类型
    private Paint paint;
    private int dividerSize = 1;//分割线尺寸


    public MutiItemDecoration(MutiItemDecoration.Type type, int dividerSize, int color) {
        this.paint = new Paint();
        this.paint.setColor(color);
        this.type = type;
        this.dividerSize = dividerSize;
    }

    public MutiItemDecoration(MutiItemDecoration.Type type) {
        this.paint = new Paint();
        this.paint.setColor(0xffe3e3e3);
        this.type = type;
        this.dividerSize = 1;
    }


    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        switch (type) {
            case ALL:
                if (itemPosition % spanCount == 0) {//第一列
                    if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                        outRect.set(0, 0, dividerSize / 2, 0);
                    } else {
                        outRect.set(0, 0, dividerSize / 2, dividerSize);
                    }
                } else if (itemPosition % spanCount == spanCount - 1) {//最后一列
                    if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                        outRect.set(dividerSize / 2, 0, 0, 0);
                    } else {
                        outRect.set(dividerSize / 2, 0, 0, dividerSize);
                    }
                } else {//中间列
                    if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                        outRect.set(dividerSize / 2, 0, dividerSize / 2, 0);
                    } else {
                        outRect.set(dividerSize / 2, 0, dividerSize / 2, dividerSize);
                    }
                }
                break;
            case VERTICAL:
                if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, 0, dividerSize);
                }
                break;
            case HORIZONTAL:
                if (isLastColum(parent, itemPosition, spanCount, childCount)) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, dividerSize, 0);
                }
                break;
        }
    }

    // 是否是最后一列
    private boolean isLastColum(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0)
                return true;
        } else {
            if (pos == childCount - 1)
                return true;
        }
        return false;
    }

    // 是否是最后一行
    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)
                return true;
        } else {
            if (pos == childCount - 1)
                return true;
        }
        return false;
    }


    //返回列数
    private int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return -1;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (type == Type.HORIZONTAL) {
            drawHorizontal(c, parent);
        } else if (type == Type.VERTICAL) {
            drawVertical(c, parent);
        } else if (type == Type.ALL) {
            drawHorizontal(c, parent);
            drawVertical(c, parent);
        }
    }

    /**
     * 设置分割线尺寸
     *
     * @param size 尺寸
     */
    public void setSize(int size) {
        this.dividerSize = size;
    }

    // 绘制垂直分割线
    protected void drawVertical(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + dividerSize;

            c.drawRect(left, top, right, bottom, paint);
        }
    }

    // 绘制水平分割线
    protected void drawHorizontal(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + dividerSize;

            c.drawRect(left, top, right, bottom, paint);
        }
    }


}