package a26c.com.android_frame_test.activity;

import android.os.Bundle;

import com.a26c.android.frame.base.CommonActivity;

import a26c.com.android_frame_test.R;
import butterknife.ButterKnife;

public class MainActivity extends CommonActivity {

    @Override
    public int getContainLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);

    }

    @Override
    protected void setEvent() {

    }
}
