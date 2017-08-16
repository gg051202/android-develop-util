package a26c.com.android_frame_test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

/**
 * Created by guilinlin on 2017/8/15 11:15.
 * email 973635949@qq.com
 */

public abstract class DomainChartAbsView extends View {

    public final Context context;
    public Paint paint;
    public Paint textPaint;
    public List<DomainPriceData> dataList;
    public int height = -1;
    public int width = -1;
    public float x1, y1, x2, y2, xx, yy;
    /**
     * Y坐标刻度总长度
     */
    public int graduation = 800;
    /**
     * Y坐标几个刻度
     */
    public int graduationCount = 5;
    public int dp10;
    public int textSize;
    public Rect rect;

    public DomainChartAbsView(Context context) {
        super(context);
        this.context = context;
        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        textPaint = new Paint();
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (dataList == null) {
            return;
        }
        if (height < 0) {
            height = canvas.getHeight();
            width = canvas.getWidth();
            dp10 = dp2px(context, 10);
            x1 = dp10 * 4;
            y1 = dp10 * 3;
            x2 = width - dp10;
            y2 = height - dp10 * 4;
            xx = x2 - x1;
            yy = y2 - y1;
            textSize = sp2px(context, 12);
        }
        graduation = 0;
        for (DomainPriceData data : dataList) {
            graduation = data.getPrice() > graduation ? (int) data.getPrice() : graduation;
        }
        graduation = (graduation / 100 + 1) * 100;

        drawSomething(canvas);

    }

    public abstract void drawSomething(Canvas canvas);

    /**
     * sp转px
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources()
                .getDisplayMetrics());
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources()
                .getDisplayMetrics());
    }

    public void setDataList(List<DomainPriceData> dataList) {
        this.dataList = dataList;
        invalidate();
    }
}
