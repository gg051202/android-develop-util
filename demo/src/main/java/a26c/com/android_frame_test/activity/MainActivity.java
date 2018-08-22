package a26c.com.android_frame_test.activity;

import android.Manifest;
import android.os.Bundle;
import android.widget.Button;

import com.a26c.android.frame.base.CommonActivity;
import com.a26c.android.frame.widget.UpdateDialog;

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
        checkPermission(new OnCheckPermissionListener() {
            @Override
            public void success() {
                new UpdateDialog(MainActivity.this)
                        .setDescName("您有新的版本")
                        .setDownloadUrl("http://imtt.dd.qq.com/16891/2F1FE079D5498D7FBDF5565965A868E0.apk?fsname=com.aalife.android_6.0.4_604.apk&csr=1bbd")
                        .show();
            }

            @Override
            public void fail() {
                System.out.println(123);

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

    }


}
