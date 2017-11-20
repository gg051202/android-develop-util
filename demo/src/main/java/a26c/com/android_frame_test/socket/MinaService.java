package a26c.com.android_frame_test.socket;

import android.app.Service;
import android.content.Context;
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
public class MinaService extends Service{

    private ConnectionThread thread;


    @Override
    public void onCreate() {
        super.onCreate();
        thread = new ConnectionThread("mina", getApplicationContext());
        thread.start();
        Log.e("tag", "启动线程尝试连接");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnect();
        thread=null;

        Log.e("tag", "断开连接");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ConnectionThread extends HandlerThread{

        boolean isConnection;
        ConnectionManager mManager;
        public ConnectionThread(String name, Context context){
            super(name);

            ConnectionConfig config = new ConnectionConfig.Builder(context)
                    .setIp("47.94.217.160")
                    .setPort(3000)
                    .setReadBufferSize(10240)
                    .setConnectionTimeout(10000).builder();

            mManager = new ConnectionManager(config);
        }

        @Override
        protected void onLooperPrepared() {
            while(true){
                isConnection = mManager.connnect();
                if(isConnection){
                    Log.e("tag", "连接成功");
                    break;
                }
                try {
                    Log.e("tag", "尝试重新连接");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void disConnect(){
            mManager.disContect();
        }
    }
}
