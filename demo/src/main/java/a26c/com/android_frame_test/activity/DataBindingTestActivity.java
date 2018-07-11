package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.view.View;

import com.a26c.android.frame.base.CommonActivity;

import java.util.HashMap;

import a26c.com.android_frame_test.R;
import butterknife.ButterKnife;

public class DataBindingTestActivity extends CommonActivity {


    private HashMap<String, String> mMap;

    @Override
    public int getContainLayout() {
        return R.layout.activity_data_binding;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mMap = new HashMap<>();
        mMap.put("name", "guili22n");



    }

    @Override
    protected void setEvent() {


    }

    public void showName(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
