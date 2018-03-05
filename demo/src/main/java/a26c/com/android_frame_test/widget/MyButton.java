package a26c.com.android_frame_test.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MyButton extends android.support.v7.widget.AppCompatButton {

    private GestureDetector mGesture;
    private OnDoubleClickListener onDoubleClickListener;

    //自定义监听器接口
    interface OnDoubleClickListener {
        void onDoubleClick(View view);
    }

    //设置双击事件监听器的方法
    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        this.onDoubleClickListener = onDoubleClickListener;
    }

    ;

    public MyButton(Context context) {
        super(context);
    }

    public MyButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        //
        mGesture = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (onDoubleClickListener != null) {
                    onDoubleClickListener.onDoubleClick(MyButton.this);
                }
                Toast.makeText(context, "双击事件", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                /**
                 * 滑动和拖拽最好不要一起实现，会产生矛盾
                 */
                if (Math.abs(e1.getX() - e2.getX()) > 50) {
                    setTranslationX(e2.getX() - e1.getX());
                    //根据手势滑动的距离而在水平方向上滑动控件
                    ObjectAnimator.ofFloat(MyButton.this, "translationX", getTranslationX(), e2.getX() - e1.getX())
                            .setDuration(500).start();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //根据手势拖拽控件的相位而移动控件
                setTranslationX(getTranslationX() + e2.getX() - e1.getX());
                setTranslationY(getTranslationX() + e2.getY() - e1.getY());
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //touch事件传给onTouchEvent()
        mGesture.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}