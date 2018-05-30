package a26c.com.android_frame_test.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

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
        mUser.setName(mUser.getName() + "b");
        mAnimationView.playAnimation();

        Toast a = Toast.makeText(this, "保存图片失败", Toast.LENGTH_LONG);
        View view1 = LayoutInflater.from(this).inflate(R.layout.frame_toast_save_bitmap_hint, null);
        view1.findViewById(R.id.see).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/jpeg");
                startActivityForResult(getImage, 1);
                System.out.println(1);

            }
        });
        a.setView(view1);
        a.show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
