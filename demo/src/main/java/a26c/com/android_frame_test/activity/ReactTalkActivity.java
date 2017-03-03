package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import a26c.com.android_frame_test.R;

/**
 * 原生和React通信的示例
 */
public class ReactTalkActivity extends AppCompatActivity {

    private android.widget.TextView text;
    private android.widget.RelativeLayout activityreacttalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_react_talk);
        this.activityreacttalk = (RelativeLayout) findViewById(R.id.activity_react_talk);
        this.text = (TextView) findViewById(R.id.text);

        String params = getIntent().getStringExtra("param");
        text.setText("从React获取到的参数:" + params);
    }
}
