package a26c.com.android_frame_test.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;

import com.a26c.android.frame.base.CommonActivity;

import a26c.com.android_frame_test.R;

public class LruCacheAcitivity extends CommonActivity {


    @Override
    public int getContainLayout() {
        return R.layout.activity_lrcache_acitivity;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        int maxMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        int cacheSize = maxMemory / 8;
        LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getWidth() * value.getHeight() / 1024;
            }

        };

    }

    @Override
    protected void setEvent() {

    }

}
