package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.a26c.android.frame.base.CommonActivity;

import a26c.com.android_frame_test.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PullToRefreshAcitivity extends CommonActivity {


    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @Override
    public int getContainLayout() {
        return R.layout.activity_pull_to_refresh_acitivity;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);


    }

    @Override
    protected void setEvent() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
