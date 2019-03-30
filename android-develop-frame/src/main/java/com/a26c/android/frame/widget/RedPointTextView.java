/**
 *
 */
package com.a26c.android.frame.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.a26c.android.frame.R;

/**
 * @author guilin
 * @desc 在xml中创建时只需要高度写需要的，宽度会自动根据高度计算
 */
public class RedPointTextView extends View {

    private int color;
    private String number;

    private Paint paint;
    private float height;
    private float width;
    private float radius;
    private float textWidth;
    private Rect textRect;// 计算text的宽高
    private int mMaxNumber = 99;

    public RedPointTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RedPointTextView);
        color = array.getColor(R.styleable.RedPointTextView_pointView_color, 0xffF74C31);
        number = array.getString(R.styleable.RedPointTextView_pointView_number);
        array.recycle();


        setVisibility(TextUtils.isEmpty(number) ? View.GONE : VISIBLE);
    }

    protected void onDraw(Canvas canvas) {
        if (number.equals(""))
            return;
        init(canvas);

        // 画圆，如果数字长度大于两位，画的是个椭圆
        paint.setColor(color);
        if (number.length() == 1) {
            canvas.drawCircle(width / 2, height / 2, radius, paint);
        } else {
            canvas.drawCircle(width / 2 - textWidth / 2, height / 2, radius, paint);
            canvas.drawCircle(width / 2 + textWidth / 2, height / 2, radius, paint);
            canvas.drawRect(width / 2 - textWidth / 2, 0, width / 2 + textWidth / 2, height, paint);
        }

        // 画数字
        paint.setColor(0xffffffff);
        paint.setTextAlign(Align.CENTER);
        if (!number.equals("0"))
            canvas.drawText(number + "", width / 2, height / 2 + textRect.height() / 2, paint);
    }

    private void init(Canvas canvas) {
        paint = new Paint();
        paint.setAntiAlias(true);
        height = canvas.getHeight();
        // 圆的半径
        radius = height / 2;
        // 设置text的字体大小，高度的3/5
        paint.setTextSize(height * 3 / 5);
        // 计算所画数字的宽高
        textRect = new Rect();
        paint.getTextBounds(number + "", 0, number.length(), textRect);
        textWidth = (number.length() == 1 ? 0 : (textRect.width() - paint.measureText("2")));

        LayoutParams lp = getLayoutParams();
        // 根据字体宽度重新设置view的宽度
        width = (int) (height + textWidth);
        lp.width = (int) width;
        setLayoutParams(lp);
    }

    public void setNumber(int number) {
        setVisibility(View.VISIBLE);
        this.number = number + "";
        if (number > mMaxNumber) {
            this.number = mMaxNumber + "+";
        }
        if (number == 0) {
            setVisibility(View.GONE);
            return;
        }
        invalidate();

    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    /**
     * 仅最为一个小红点
     */
    public void setEmptyString() {
        number = "0";
        setVisibility(View.VISIBLE);
        invalidate();
    }

    public void setLayoutParams(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
        if (leftMargin != 0)
            lp.leftMargin = leftMargin;
        if (topMargin != 0)
            lp.topMargin = topMargin;
        if (rightMargin != 0)
            lp.rightMargin = rightMargin;
        if (bottomMargin != 0)
            lp.bottomMargin = bottomMargin;
        setLayoutParams(lp);
    }

    /**
     * 设置已一个点为中心点对其
     *
     * @param left
     * @param top
     */
    public void setLocationLeftTop(int left, int top) {
        android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
        lp.leftMargin = (int) (left - height / 2);
        lp.topMargin = (int) (top - height / 2);
        setLayoutParams(lp);
    }

    public int getMaxNumber() {
        return mMaxNumber;
    }

    public void setMaxNumber(int maxNumber) {
        mMaxNumber = maxNumber;
    }
}
