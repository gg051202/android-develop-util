package a26c.com.android_frame_test.react;

import com.facebook.react.bridge.Callback;

/**
 * Created by guilinlin on 2017/1/6 09:26.
 * email 973635949@qq.com
 */
public interface ILocalModule {

    void show(String text);

    void startActivity(String name, String params);

    void getMac();

    void getDataFromActivity(String  keys,Callback success,Callback err);
}
