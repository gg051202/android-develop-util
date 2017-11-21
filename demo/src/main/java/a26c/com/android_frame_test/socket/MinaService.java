package a26c.com.android_frame_test.socket;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Description:
 * User: chenzheng
 * Date: 2016/12/9 0009
 * Time: 17:17
 */
public class MinaService extends Service {

    private ConnectionThread thread;

    ConnectionManager mManager;


    @Override
    public void onCreate() {
        super.onCreate();
        thread = new ConnectionThread("mina");
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getIntExtra("id", 0) == 2) {
            stop();
        }else if(intent.getIntExtra("id", 0) == 1){
            thread = new ConnectionThread("mina");
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnect();
        thread = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ConnectionThread extends HandlerThread {


        ConnectionThread(String name) {
            super(name);
            mManager = new ConnectionManager();
        }

        @Override
        protected void onLooperPrepared() {
            while (true) {
                if (mManager.connnect()) {
                    Log.i("tag", "连接成功");
                    break;
                }
                try {
                    Log.i("tag", "尝试重新连接");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void disConnect() {
            mManager.disContect();
        }
    }

    public void stop() {
        mManager.disContect();
    }

}
