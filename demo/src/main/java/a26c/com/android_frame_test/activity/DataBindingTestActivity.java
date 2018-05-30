package a26c.com.android_frame_test.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.a26c.android.frame.base.CommonActivity;
import com.airbnb.lottie.LottieAnimationView;

import java.util.HashMap;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.databinding.ActivityDataBindingBinding;
import a26c.com.android_frame_test.model.DataBindingTestData;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DataBindingTestActivity extends CommonActivity {


    @BindView(R.id.animation_view)
    LottieAnimationView mAnimationView;
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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
