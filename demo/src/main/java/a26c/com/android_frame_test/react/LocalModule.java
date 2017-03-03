package a26c.com.android_frame_test.react;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import a26c.com.android_frame_test.util.SystemInfoUtil;

/**
 * Created by guilinlin on 2017/1/5 15:44.
 * email 973635949@qq.com
 */
public class LocalModule extends ReactContextBaseJavaModule implements ILocalModule {

    private ReactContext reactContext;

    public LocalModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "LocalMethod";
    }

    @ReactMethod
    @Override
    public void show(String text) {
        Toast.makeText(getReactApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    /**
     * 通过react启动本地的Activity
     *
     * @param name Activityvty类名，不需要传包名
     */
    @ReactMethod
    @Override
    public void startActivity(String name, String params) {
        Activity activity = getCurrentActivity();
        if (null != activity) {
            try {
                Class targetActivity = Class.forName("a26c.com.android_frame_test." + name);
                Intent intent = new Intent(activity, targetActivity);
                intent.putExtra("param", params);
                activity.startActivity(intent);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    @ReactMethod
    @Override
    public void getMac() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                WritableMap writableMap = new WritableNativeMap();
                writableMap.putString("key", SystemInfoUtil.getMac());
                sendTransMisson(reactContext, "EventName", writableMap);

            }
        }).start();
    }


    @ReactMethod
    @Override
    public void getDataFromActivity(String key,Callback success, Callback err) {

        try {
            Activity activity = getCurrentActivity();
            if (activity != null) {
                Bundle extras = activity.getIntent().getExtras();
                success.invoke(extras.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
            err.invoke(e.getMessage());
        }

    }


    /**
     * @param reactContext
     * @param eventName    事件名
     * @param params       传惨
     */
    public void sendTransMisson(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);

    }

}
