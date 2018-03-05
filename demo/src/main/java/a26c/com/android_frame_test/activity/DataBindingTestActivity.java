package a26c.com.android_frame_test.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.a26c.android.frame.base.CommonActivity;

import java.util.HashMap;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.databinding.ActivityDataBindingBinding;
import a26c.com.android_frame_test.model.DataBindingTestData;

public class DataBindingTestActivity extends CommonActivity {


    private HashMap<String, String> mMap;
    private ActivityDataBindingBinding mDataBinding;
    private DataBindingTestData mUser;

    @Override
    public int getContainLayout() {
        return R.layout.activity_data_binding;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mMap = new HashMap<>();
        mMap.put("name", "guili22n");

        mDataBinding = DataBindingUtil.bind(mContainView);
        mUser = new DataBindingTestData("xiaping", "女", 22);
        mDataBinding.setUser(mUser);
    }

    @Override
    protected void setEvent() {


    }

    public void showName(View view) {
        mUser.setName(mUser.getName() + "b");

    }
}
