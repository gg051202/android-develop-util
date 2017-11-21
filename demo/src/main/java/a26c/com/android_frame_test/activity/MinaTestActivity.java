package a26c.com.android_frame_test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.socket.MinaService;
import a26c.com.android_frame_test.socket.ReceiveSocketMessageEventBus;
import a26c.com.android_frame_test.socket.SocketManager;

/**
 * Description:
 * User: chenzheng
 * Date: 2016/12/9 0009
 * Time: 18:01
 */
public class MinaTestActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView receive_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_mina_test);

        initView();

    }


    private void initView() {
        receive_tv = (TextView) this.findViewById(R.id.receive_tv);
        findViewById(R.id.start_service_tv).setOnClickListener(this);
        findViewById(R.id.stop_service_tv).setOnClickListener(this);
        findViewById(R.id.send_tv).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        String value = ((EditText) findViewById(R.id.editText)).getText().toString();
        switch (v.getId()) {
            case R.id.start_service_tv:
                intent = new Intent(this, MinaService.class);
                intent.putExtra("id", 1);
                startService(intent);
                break;
            case R.id.stop_service_tv:
                intent = new Intent(this, MinaService.class);
                intent.putExtra("id", 2);
                startService(intent);
                break;
            case R.id.send_tv:
                SocketManager.sendMsg(value);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MinaService.class));
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdate(ReceiveSocketMessageEventBus eventBus) {
        receive_tv.setText(eventBus.getMessage());

    }
}
