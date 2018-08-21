package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.widget.Button;

import com.a26c.android.frame.base.CommonActivity;

import a26c.com.android_frame_test.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends CommonActivity {

    @BindView(R.id.button)
    Button mButton;

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

    @OnClick(R.id.button)
    public void onClick() {
        gotoActivity(BaseRecyclerViewActivity.class);

    }


}
