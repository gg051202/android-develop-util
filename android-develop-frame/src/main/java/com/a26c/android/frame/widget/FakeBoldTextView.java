package com.a26c.android.frame.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;

import com.a26c.android.frame.R;

/**
 * Created by guilinlin on 2017/8/16 10:41.
 * email 973635949@qq.com
 * 纤细的粗体
 */
public class FakeBoldTextView extends AppCompatTextView {

    private int color;
    private float boldSize;

    public FakeBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FakeBoldTextView);
        color = array.getColor(R.styleable.FakeBoldTextView_fbt_color, 0);
        boldSize = array.getFloat(R.styleable.FakeBoldTextView_fbt_boldSize, 0.3f);
        array.recycle();

        updateString(getText().toString());
    }

    private void updateString(String text) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new FakeBoldSpan(boldSize, color), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        super.setText(spannableString);
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        updateString(getText().toString());
    }

    public float getBoldSize() {
        return boldSize;
    }

    public void setBoldSize(float boldSize) {
        this.boldSize = boldSize;
        updateString(getText().toString());
    }

    public void setBoldText(CharSequence charSequence) {
        updateString(charSequence.toString());
    }


}
