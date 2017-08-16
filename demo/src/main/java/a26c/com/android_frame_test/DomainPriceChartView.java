package a26c.com.android_frame_test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

/**
 * Created by guilinlin on 2017/8/15 09:52.
 * email 973635949@qq.com
 */

public class DomainPriceChartView extends FrameLayout {

    private final BackGroundView backgroundView;
    private final CurveView curveView;
    private final OverFloatView overView;

    public DomainPriceChartView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        backgroundView = new BackGroundView(context);
        curveView = new CurveView(context);
        overView = new OverFloatView(context);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(backgroundView, params);
        addView(curveView, params);
        addView(overView, params);
    }

    public void setList(List<DomainPriceData> list) {
        backgroundView.setDataList(list);
        curveView.setDataList(list);
        overView.setDataList(list);
        overView.setCurrentPosition(-1);

    }

    /**
     * 背景View
     */
    public static class BackGroundView extends DomainChartAbsView {
        public BackGroundView(Context context) {
            super(context);
        }

        @Override
        public void drawSomething(Canvas canvas) {
            textPaint.setTextSize(textSize);
            canvas.drawColor(0xFFFFFFFF);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setColor(0xffCCCCCC);
            canvas.drawText("成交价(元)", 0, textSize, textPaint);

            //画横线和纵坐标
            paint.setColor(0xffFEF2E8);
            paint.setStrokeWidth(2);
            textPaint.setTextAlign(Paint.Align.RIGHT);
            textPaint.setColor(0xff666666);
            float delatY = yy / (graduationCount - 1);
            textPaint.getTextBounds("160", 0, 1, rect);
            int h = rect.height() / 2;
            for (int i = 0; i < graduationCount; i++) {
                if (i == graduationCount - 1) {//如果是最后一条横线就画黑线
                    paint.setStrokeWidth(4);
                    paint.setColor(0xffC4C4C4);
                }
                float startY = y1 + delatY * i;
                canvas.drawLine(x1, startY, x2, startY, paint);
                canvas.drawText(String.valueOf(graduation / (graduationCount - 1) * (graduationCount - 1 - i)), x1 - dp10, startY + h, textPaint);
            }

            //画横坐标刻度和点
            textPaint.setTextAlign(Paint.Align.CENTER);
            int size = dataList.size();
            float delatX = xx / (size - 1);
            int distance = size / 6;
            for (int i = 0; i < size; i++) {
                if (i == size - 1) {
                    textPaint.setTextAlign(Paint.Align.RIGHT);
                }
                DomainPriceData item = dataList.get(i);
                float startX = x1 + delatX * i;
                canvas.drawLine(startX, y2 - 2, startX, y2 + dp10 / 3, paint);
                if (i % distance == 0) {
                    canvas.drawText(item.getTime(), startX, y2 + textSize * 2, textPaint);
                }
            }
        }

    }

    public static class CurveView extends DomainChartAbsView {

        private LinearGradient shader;

        public CurveView(Context context) {
            super(context);
        }

        @Override
        public void drawSomething(Canvas canvas) {
            if (shader == null) {
                shader = new LinearGradient(x1, y1, x1, y2, 0xccFDCEAD, 0x00FDCEAD, Shader.TileMode.REPEAT);
            }

            //画折线
            Path path = new Path();
            paint.setColor(0x66FC7E2C);
            textPaint.setColor(0xffFC7E2C);
            textPaint.setShader(null);
            int size = dataList.size();
            float delatX = xx / (size - 1);
            for (int i = 0; i < size; i++) {
                DomainPriceData item = dataList.get(i);
                float startX = x1 + delatX * i;
                int startY = (int) (y1 + (yy * (1 - item.getPrice() / graduation)));
                canvas.drawCircle(startX, startY, dp10 / 4, textPaint);
                canvas.drawCircle(startX, startY, dp10 / 2, paint);
                if (i == 0) {
                    path.moveTo(startX, startY);
                } else {
                    path.lineTo(startX, startY);
                }
            }
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setColor(0xffFC7E2C);
            textPaint.setStrokeWidth(5);
            canvas.drawPath(path, textPaint);

            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setShader(shader);
            path.lineTo(x2, y2);

            canvas.drawPath(path, textPaint);
        }
    }


    public static class OverFloatView extends DomainChartAbsView {

        private int currentPosition = -1;

        public OverFloatView(Context context) {
            super(context);
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            // 防止滑动事件和viewpager的滑动事件冲突
//                            requestDisallowInterceptTouchEvent(true);
//                             requestDisallowInterceptTouchEvent(true);
                            move(event.getX());
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_DOWN:
                            move(event.getX());
                            break;
                    }
                    return true;
                }
            });
        }

        private void move(float actionX) {
            if (dataList == null) {
                return;
            }
            if (actionX > (x1 - 10)) {
                currentPosition = (int) ((actionX - x1) / (xx / dataList.size()));
                currentPosition = (actionX - xx * currentPosition - x1) < (xx / 2) ? currentPosition : currentPosition + 1;
                invalidate();
            }
        }

        @Override
        public void drawSomething(Canvas canvas) {
            if (currentPosition < 0 || currentPosition >= dataList.size()) {
                return;
            }
            paint.setColor(Color.RED);
            // 获取到手指动到哪个点的数据和点坐标
            float delatX = xx / (dataList.size() - 1);
            DomainPriceData item = dataList.get(currentPosition);
            float startX = x1 + delatX * currentPosition;
            float startY = (int) (y1 + (yy * (1 - item.getPrice() / graduation)));
            int rectWidth = dp10 * 4;//矩形宽度的一半
            int rectHeight = (dp10 * 7);//矩形的高度
            int distance = dp10 * 2;//矩形的高度

            // 计算动态显示的方框的坐标
            float xx1 = startX - rectWidth;
            float yy1 = startY - rectHeight;
            float xx4 = startX + rectWidth;
            float yy4 = startY - distance;

            if (startY < (y1 + y2) / 2) {
                xx1 = startX - rectWidth;
                yy1 = startY + distance;
                xx4 = startX + rectWidth;
                yy4 = startY + rectHeight;
            }
            if (startX < x1 + rectWidth / 2) {
                xx1 = x1 - rectWidth;
                xx4 = x1 + rectWidth;
            } else if (startX > width - rectWidth * 2) {
                xx1 = width - rectWidth * 2 - distance;
                xx4 = width - distance;
            }

            // 绘制方框
            paint.setColor(0xffECECEC);
            paint.setAlpha(150);
            RectF rect = new RectF(xx1, yy1, xx4, yy4);
            canvas.drawRoundRect(rect, dp10 / 3, dp10 / 3, paint);

            // 绘制日期
            paint.setColor(0xff757575);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(item.getTime(), (xx1 + xx4) / 2, yy1 + (yy4 - yy1) / 3, paint);

            // 绘制金额
            paint.setColor(0xff000000);
            paint.setTextSize(textSize);
            canvas.drawText(getFormatNumber(item.getPrice()) + "元", (xx1 + xx4) / 2, yy1 + (yy4 - yy1) / 4 * 3.2f, paint);

            // 绘制大点子
            paint.setColor(Color.WHITE);
            paint.setAlpha(255);
            canvas.drawCircle(startX, startY, 20, paint);

            paint.setColor(0xff86A6DF);
            canvas.drawCircle(startX, startY, 15, paint);
        }


        // 格式化数字显示方式,double类型会显示小数点后很多位
        public String getFormatNumber(double e) {
            String s;
            if (e < 1)
                s = ((float) Math.round(e * 10000)) / 10000 + "";
            else if (e < 10)
                s = ((float) Math.round(e * 1000)) / 1000 + "";
            else if (e < 100)
                s = ((float) Math.round(e * 100)) / 100 + "";
            else if (e < 1000)
                s = ((float) Math.round(e * 10)) / 10 + "";
            else
                s = ((float) Math.round(e)) + "";
            return s;

        }

        public void setCurrentPosition(int currentPosition) {
            this.currentPosition = currentPosition;
        }
    }

}
