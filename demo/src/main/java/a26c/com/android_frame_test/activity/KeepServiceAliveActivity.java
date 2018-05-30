package a26c.com.android_frame_test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import a26c.com.android_frame_test.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KeepServiceAliveActivity extends AppCompatActivity {

    @BindView(R.id.startButton)
    Button mStartButton;
    @BindView(R.id.stopstartButton)
    Button mStopstartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_service_alive);
        ButterKnife.bind(this);




    }

    @OnClick({R.id.startButton, R.id.stopstartButton})
    public void onClick(View view) {
        Intent intent = new Intent(this, KeepAliveService.class);

        switch (view.getId()) {
            case R.id.startButton:
                startService(intent);
                break;
            case R.id.stopstartButton:
                stopService(intent);
                break;
        }
    }
}
