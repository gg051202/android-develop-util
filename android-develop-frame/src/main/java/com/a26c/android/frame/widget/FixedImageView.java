package com.a26c.android.frame.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.a26c.android.frame.R;

/**
 * Created by guilinlin on 2018/7/13 23:56.
 * email 973635949@qq.com
 */
public class FixedImageView extends AppCompatImageView {

    private final int mHeight;
    private final int mWidth;

    public FixedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FixedImageView);
        mHeight = typedArray.getInt(R.styleable.FixedImageView_fixed_height, 1);
        mWidth = typedArray.getInt(R.styleable.FixedImageView_fived_width, 1);
        typedArray.recycle();
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = this.getMeasuredWidth();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (childWidthSize / 1f / mWidth * mHeight), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
