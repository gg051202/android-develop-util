package a26c.com.android_frame_test.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.databinding.ActivityDataBindingBinding;
import a26c.com.android_frame_test.model.UserData;
import a26c.com.android_frame_test.util.EventHandler;
import a26c.com.android_frame_test.util.Task;

public class DataBindingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDataBindingBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding);
        dataBinding.setUser(new UserData("123", 11, 2));
        dataBinding.setImg(getResources().getDrawable(R.drawable.aa));
        dataBinding.setHandler(new EventHandler(this));
        dataBinding.setTask(new Task());
    }


}
