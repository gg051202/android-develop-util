package a26c.com.android_frame_test.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import a26c.com.android_frame_test.R;

/**
 * Description:
 * User: chenzheng
 * Date: 2016/12/9 0009
 * Time: 18:01
 */
public class MinaTestActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView start_service_tv, send_tv, receive_tv;

    private MessageBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mina_test);

        initView();
        registerBroadcast();

    }

    private void registerBroadcast() {
        receiver = new MessageBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.commonlibrary.mina.broadcast");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void initView() {
        receive_tv = (TextView) this.findViewById(R.id.receive_tv);
        start_service_tv = (TextView) this.findViewById(R.id.start_service_tv);
        start_service_tv.setOnClickListener(this);
        send_tv = (TextView) this.findViewById(R.id.send_tv);
        send_tv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String value = ((EditText) findViewById(R.id.editText)).getText().toString();
        switch (v.getId()) {
            case R.id.start_service_tv:
                Log.e("tag", "点击启动服务");
                Intent intent = new Intent(this, MinaService.class);
                startService(intent);
                break;
            case R.id.send_tv:
                Log.e("tag", "点击发送消息");
                SessionManager.getInstance().writeToServer(value);
                break;
        }
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MinaService.class));
        unregisterBroadcast();

    }

    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            receive_tv.setText(intent.getStringExtra("message"));
        }
    }
}
