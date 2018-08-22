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
                        .setTitleName("您有新的版本")
                        .setDescName("更新\n日\n日\n日\n日\n日\n日\n日日\n日\n日日\n日\n日\n日\n日\n日日\n日\n日\n日\n日\n日\n日\n日\n日日\n日\n日\n日\n日\n日志更新日志")
                        .setDownloadUrl("http://imtt.dd.qq.com/16891/A92C29C6A2255AD59E082A9B6336AEAD.apk?fsname=com.lotus.game.popthewheel.android_1.0.1_2.apk&csr=1bbd")
                        .show();
            }

            @Override
            public void fail() {
                System.out.println(123);

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

    }


}
