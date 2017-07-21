package a26c.com.android_frame_test.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.a26c.android.frame.base.CommonActivity;
import com.a26c.android.frame.util.CheckUpdateManager;
import com.a26c.android.frame.widget.MutiItemDecoration;

import a26c.com.android_frame_test.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends CommonActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    public int getContainLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        TestAdapter testAdapter = new TestAdapter();
        for (int i = 20; i > 0; i--) {
            testAdapter.add(0, new TestAdapterData());
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new MutiItemDecoration(MutiItemDecoration.Type.ALL));
        recyclerView.setAdapter(testAdapter);
    }

    @Override
    protected void setEvent() {

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(new OnCheckPermissionListener() {
                    @Override
                    public void success() {
                        new CheckUpdateManager.Builder(MainActivity.this)
                                .desc("123123")
                                .isAutoCheck(false)
                                .needUpdate(true)
                                .title("更新提示")
                                .downloadUrl("http://m.22.cn/AiMing2.94.apk")
                                .iconResourceId(R.mipmap.frame_icon_photo)
                                .build().showDialog();
                    }

                    @Override
                    public void fail() {

                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
        });

    }
}
