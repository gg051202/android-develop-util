package com.a26c.android.frame.widget;

import android.graphics.Paint;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

/**
 * 粗体，但是比一般粗体要细一些
 */
public class FakeBoldSpan extends CharacterStyle {

    private float boldSize = 0.4f;
    private int color = 0;

    public FakeBoldSpan(float boldSize) {
        this.boldSize = boldSize;
    }


    public FakeBoldSpan() {
    }

    public FakeBoldSpan(float boldSize, int color) {
        this.boldSize = boldSize;
        this.color = color;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setStyle(Paint.Style.FILL_AND_STROKE);
        tp.setStrokeWidth(boldSize);//控制字体加粗的程度
        if (color != 0) {
            tp.setColor(color);
        }
    }


    public void setBold(float bold) {
        this.boldSize = bold;
    }

    public void setColor(int color) {
        this.color = color;
    }


}