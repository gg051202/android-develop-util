package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import a26c.com.android_frame_test.DomainPriceChartView;
import a26c.com.android_frame_test.DomainPriceData;
import a26c.com.android_frame_test.R;

/**
 * 原生和React通信的示例
 */
public class CanvasActivity extends AppCompatActivity {

    private a26c.com.android_frame_test.DomainPriceChartView canvasView;
    private android.widget.Button button;
    private android.widget.LinearLayout activityreacttalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_react_talk);
        this.activityreacttalk = (LinearLayout) findViewById(R.id.activity_react_talk);
        this.button = (Button) findViewById(R.id.button);
        this.canvasView = (DomainPriceChartView) findViewById(R.id.canvasView);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DomainPriceData> list = new ArrayList<>();
                for (int i = 0; i < 16; i++) {
                    list.add(new DomainPriceData(i * 50 * new Random().nextFloat(), "06-21"));
                }
                canvasView.setList(list);
            }
        });

        List<DomainPriceData> list = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            list.add(new DomainPriceData(i * 50 * new Random().nextFloat(), "06-21"));
        }
        canvasView.setList(list);
    }
}
