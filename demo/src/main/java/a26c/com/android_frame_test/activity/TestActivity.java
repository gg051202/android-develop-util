package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import a26c.com.android_frame_test.R;

public class TestActivity extends AppCompatActivity {


    private android.view.View view1;
    private android.view.View view2;
    private android.view.View view3;

    GestureDetector gestureDetector ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        this.view3 = (View) findViewById(R.id.view3);
        this.view2 = (View) findViewById(R.id.view2);
        this.view1 = (View) findViewById(R.id.view1);

        GestureDetector.OnGestureListener listener = new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                System.out.println("onDown");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

                System.out.println("onShowPress");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                System.out.println("onSingleTapUp");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                System.out.println("onScroll");
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

                System.out.println("onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                System.out.println("onFling");
                return false;
            }
        };
        gestureDetector = new GestureDetector(this, listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
