package com.a26c.android.frame.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.a26c.android.frame.R;

import java.io.InputStream;

/**
 * 自定义RatingBar,默认五颗星，不可点击，step=1
 *
 * @author guilin
 */
public class FrameRatingBar extends FrameLayout {

    /**
     * 星星的个数
     */
    private int INDEX = 5;

    /**
     * 图片资源,RATING_DST背景，RATING_SRC前景
     */
    private int STAR_DST = R.drawable.grey_star;
    private int STAR_SRC = R.drawable.yellow_star;
    /**
     * 星星是否可点击，true为可点击
     */
    private boolean CLICKED = false;
    /**
     * 设置每两个星星见距离与高度的比例
     */
    private float SPACE = 0.4f;
    /**
     * 分辨率，比如设置为0.2最小可以显示0.2颗星星
     */
    private float STEP = 1f;

    private int height;
    private int width;
    private Bitmap bitmap_src, bitmap_dst;
    private RatingBarDst ratingBarDst;
    private RatingBarSrc ratingBarSrc;
    /**
     * 间距加上星星的宽度的和
     */
    private int spacing;

    public FrameRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获取xml文件中的自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FrameRatingBar);
        INDEX = a.getInt(R.styleable.FrameRatingBar_rating_starNumber, INDEX);
        STAR_DST = a.getResourceId(R.styleable.FrameRatingBar_rating_starDst, STAR_DST);
        STAR_SRC = a.getResourceId(R.styleable.FrameRatingBar_rating_starSrc, STAR_SRC);
        CLICKED = a.getBoolean(R.styleable.FrameRatingBar_rating_clickable, CLICKED);
        SPACE = a.getFloat(R.styleable.FrameRatingBar_rating_space, SPACE);
        STEP = a.getFloat(R.styleable.FrameRatingBar_rating_step, STEP);

        // 新建两个bitmap对象，用于接下来canvas绘制
        InputStream isDst = context.getResources().openRawResource(STAR_DST);
        bitmap_dst = BitmapFactory.decodeStream(isDst);
        InputStream isSrc = context.getResources().openRawResource(STAR_SRC);
        bitmap_src = BitmapFactory.decodeStream(isSrc);

        // 新建两个view，分别显示背景和前景
        ratingBarSrc = new RatingBarSrc(context, STEP);
        ratingBarDst = new RatingBarDst(context);

        final FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(ratingBarDst);
        frameLayout.addView(ratingBarSrc);
        addView(frameLayout);

        // 重新计算宽度
        post(new Runnable() {
            public void run() {
                height = getHeight();
                spacing = (int) ((1 + SPACE) * height);
                width = (int) (spacing * INDEX - height * SPACE);
                LayoutParams lp = (LayoutParams) frameLayout.getLayoutParams();
                lp.width = width;
                frameLayout.setLayoutParams(lp);
            }
        });
        a.recycle();

    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (CLICKED) {
                    float x = event.getX();
                    int big = (int) (x / spacing);// 获取点击的是哪一个范围的
                    float x2 = x - big * spacing;
                    float small = x2 / height;
                    small = small < 1 ? small : 1;// 获取点击的小数部分
                    ratingBarSrc.setRating(big + small);
                }
                break;

            default:
                break;
        }
        return true;
    }

    /**
     * 设置ratingBar图片资源
     *
     * @param ratingSrc 选中的图片,null表示不设置
     * @param ratingDst 未选中的图片,null表示不设置
     */
    public void setRatingDrawable(Integer ratingSrc, Integer ratingDst) {
        if (ratingSrc != null)
            STAR_SRC = ratingSrc;
        if (ratingDst != null)
            STAR_DST = ratingDst;
    }

    /**
     * 星星是否可点击，true为可点击
     */
    public void setClickable(boolean clickable) {
        this.CLICKED = clickable;
    }

    public void setStep(float step) {
        ratingBarSrc.setStep(step);
    }

    /**
     * @return 当前ratingBar的星级, 返回浮点型
     */
    public float getRating() {
        return ratingBarSrc.small + ratingBarSrc.big;
    }

    public void setRating(float rating) {
        ratingBarSrc.setRating(rating);
    }

    /**
     * 内部类
     * <p>
     * ratingbar背景View
     */
    class RatingBarDst extends View {

        private PaintFlagsDrawFilter filter;
        private Rect bitmapRect;

        public RatingBarDst(Context context) {
            super(context);
            filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            bitmapRect = new Rect();
        }

        protected void onDraw(Canvas canvas) {

            canvas.setDrawFilter(filter);
            // 画五个背景星星
            for (int i = 0; i < INDEX; i++) {
                int xStart = spacing * i;
                bitmapRect.set(xStart, 0, xStart + height, height);
                canvas.drawBitmap(bitmap_dst, null, bitmapRect, null);
            }
        }
    }

    /**
     * 内部类
     * <p>
     * ratingbar前景View
     */
    class RatingBarSrc extends View {

        /**
         * 整数部分
         */
        int big;
        /**
         * 小数部分
         */
        float small;
        /**
         * 数量级，表示递增单位， 范围是0.1到1
         */
        private float step;
        private PaintFlagsDrawFilter filter;
        private Rect clipRect;
        private Rect bitmapRect;

        public RatingBarSrc(Context context, float step) {
            super(context);
            setStep(step);
            filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            bitmapRect = new Rect();
            clipRect = new Rect();
        }

        protected void onDraw(Canvas canvas) {
            clipRect.set(0, 0, (int) (big * spacing + small * height), height);

            canvas.clipRect(clipRect);
            canvas.setDrawFilter(filter);
            // 画五个前景星星
            for (int i = 0; i < big + 1; i++) {
                int xStart = spacing * i;
                bitmapRect.set(xStart, 0, xStart + height, height);
                canvas.drawBitmap(bitmap_src, null, bitmapRect, null);
            }

        }

        /**
         * 设置星星的个数，可以设置浮点型
         */
        public void setRating(float rating) {
            big = (int) rating;
            small = rating - big;
            small = (int) (small * 10) / (int) (step * 10) * step + step;
            invalidate();
        }

        /**
         * 设置递增单位，只能是0.1，0.2，0.3等等等
         */
        public void setStep(float step) {
            // 做一个保护，如果不是规定值，则设置为1
            if (step >= 0.1 && step <= 1) {
                this.step = step;
            } else
                this.step = 1;
        }

    }
}