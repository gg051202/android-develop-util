package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import a26c.com.android_frame_test.R;

public class TestActivity extends AppCompatActivity {


    private android.view.View view2;
    private android.view.View emptyView;
    private ScrollView scrollView;

    GestureDetector gestureDetector;
    private int firstTopMargin = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        this.view2 = (View) findViewById(R.id.view2);
        this.emptyView = (View) findViewById(R.id.emptyView);
        this.scrollView = (ScrollView) findViewById(R.id.scrollView);

        view2.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view2.getHeight());
                emptyView.setLayoutParams(layoutParams);
                scrollView.scrollTo(0,view2.getHeight());
                System.out.println("height:"+view2.getHeight());
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
