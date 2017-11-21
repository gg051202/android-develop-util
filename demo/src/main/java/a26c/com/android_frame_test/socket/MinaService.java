package a26c.com.android_frame_test.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Description:
 * User: chenzheng
 * Date: 2016/12/9 0009
 * Time: 17:17
 */
public class MinaService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getIntExtra("id", 0) == 1) {
            SocketManager.getInstance().connnect(onConnectSuccessListener);
        } else if (intent.getIntExtra("id", 0) == 2) {
            SocketManager.getInstance().disContect();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().disContect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    SocketManager.OnConnectSuccessListener onConnectSuccessListener = new SocketManager.OnConnectSuccessListener() {
        @Override
        public void success() {
            Toast.makeText(getApplicationContext(), "123", Toast.LENGTH_LONG).show();

        }
    };


}
