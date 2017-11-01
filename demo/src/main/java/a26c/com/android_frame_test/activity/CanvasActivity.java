package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.a26c.android.frame.widget.FakeBoldTextView;

import a26c.com.android_frame_test.DomainPriceChartView;
import a26c.com.android_frame_test.R;

/**
 * 原生和React通信的示例
 */
public class CanvasActivity extends AppCompatActivity {

    private a26c.com.android_frame_test.DomainPriceChartView canvasView;
    private android.widget.Button button;
    private android.widget.LinearLayout activityreacttalk;
    private com.a26c.android.frame.widget.FakeBoldTextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_react_talk);
        this.t1 = (FakeBoldTextView) findViewById(R.id.t1);
        this.activityreacttalk = (LinearLayout) findViewById(R.id.activity_react_talk);
        this.button = (Button) findViewById(R.id.button);
        this.canvasView = (DomainPriceChartView) findViewById(R.id.canvasView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                t1.setColor(0xffff0000);
            }
        });

        t1.setBoldText("啊啊啊");
    }
}
