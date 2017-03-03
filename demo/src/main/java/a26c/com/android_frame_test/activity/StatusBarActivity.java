package a26c.com.android_frame_test.activity;

import android.os.Bundle;

import com.a26c.android.frame.base.CommonActivity;

import a26c.com.android_frame_test.R;

/**
 * Created by guilinlin on 2017/1/16 16:25.
 * email 973635949@qq.com
 */
public class StatusBarActivity extends CommonActivity {


    @Override
    public int getContainLayout() {
        return R.layout.activity_statusbar;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setTitle("状态栏");

    }

    @Override
    protected void setEvent() {

    }
}
