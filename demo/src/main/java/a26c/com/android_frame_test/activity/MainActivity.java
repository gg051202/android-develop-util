package a26c.com.android_frame_test.activity;

import android.Manifest;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;

import com.a26c.android.frame.base.CommonActivity;
import com.a26c.android.frame.widget.CommonMenu;
import com.a26c.android.frame.widget.UpdateDialog;

import a26c.com.android_frame_test.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends CommonActivity {

    @BindView(R.id.button)
    Button mButton;
    @BindView(R.id.commonMenu)
    CommonMenu mCommonMenu;

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
                SpannableString spannableString = new SpannableString("12312312313");
                spannableString.setSpan(new ForegroundColorSpan(0xffff0000), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                new UpdateDialog(MainActivity.this)
                        .setIsAutoCheck(false)
                        .setNeedUpdate(true)
                        .setTitleName(spannableString)
                        .setSpaceTimeHour(8)
                        .setDescName(
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "作者：Viola\n" +
                                "著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。")
                        .setDownloadUrl("http://imtt.dd.qq.com/16891/943DD8AB1BA30F9C0A4D0C688CEF53A0.apk?fsname=jp.co.goodroid.LWorld_1.0.4_5.apk&csr=1bbd")
                        .show();
            }

            @Override
            public void fail() {
                System.out.println(123);

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

    }


}
