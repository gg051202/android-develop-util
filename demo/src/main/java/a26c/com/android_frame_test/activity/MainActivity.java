package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.view.View;
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
        View viewById = findViewById(R.id.commonMenu);
        viewById.setFocusable(true);
        viewById.findViewById(R.id.parentLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(123);
            }
        });

    }

    @OnClick(R.id.button)
    public void onClick() {

//        checkPermission(new OnCheckPermissionListener() {
//            @Override
//            public void success() {
//                SpannableString spannableString = new SpannableString("12312312313");
//                spannableString.setSpan(new ForegroundColorSpan(0xffff0000), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                new UpdateDialog(MainActivity.this)
//                        .setIsAutoCheck(false)
//                        .setNeedUpdate(true)
//                        .setTitleName(spannableString)
//                        .setSpaceTimeHour(8)
//                        .setDescName(
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "作者：Viola\n" +
//                                "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
//                        .setDownloadUrl("https://imtt.dd.qq.com/16891/594918EC7AF0BC9E2E22B55753DDEE2D.apk?fsname=com.ijinshan.duba_3.5.0_30500016.apk&csr=1bbd")
//                        .show();
//            }
//
//            @Override
//            public void fail() {
//                System.out.println(123);
//
//            }
//        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
//
    }


}
