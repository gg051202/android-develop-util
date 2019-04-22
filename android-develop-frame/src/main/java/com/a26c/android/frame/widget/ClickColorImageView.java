package com.a26c.android.frame.widget;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.a26c.android.frame.R;

public class ClickColorImageView extends AppCompatImageView {

    private ColorStateList tint;

    public ClickColorImageView(Context context) {
        super(context);
    }

    public ClickColorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ClickColorImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClickColorImageView, defStyle, 0);
        tint = a.getColorStateList(R.styleable.ClickColorImageView_cciv_clickColor);
        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (tint != null && tint.isStateful())
            updateTintColor();
    }

    public void setColorFilter(ColorStateList tint) {
        this.tint = tint;
        super.setColorFilter(tint.getColorForState(getDrawableState(), 0));
    }

    private void updateTintColor() {
        int color = tint.getColorForState(getDrawableState(), 0);
        setColorFilter(color);
    }

}