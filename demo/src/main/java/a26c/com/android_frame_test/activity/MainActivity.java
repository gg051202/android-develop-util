package a26c.com.android_frame_test.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.a26c.android.frame.base.CommonActivity;
import com.a26c.android.frame.widget.OnUploadPhotoListener;
import com.a26c.android.frame.widget.UpdateDialog;
import com.a26c.android.frame.widget.UploadPhotoDialog;
import com.bumptech.glide.Glide;

import a26c.com.android_frame_test.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends CommonActivity {

    @BindView(R.id.button)
    Button mButton;

    @BindView(R.id.image)
    ImageView image;
    private UploadPhotoDialog mUploadPhotoDialog;

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


        mUploadPhotoDialog = new UploadPhotoDialog(this, new OnUploadPhotoListener() {
            @Override
            public boolean photoClick(int requestCode) {
                return false;
            }

            @Override
            public boolean albumClick(int requestCode) {
                return false;
            }

            @Override
            public void onlyReceivedImage(int requestCode) {

            }

            @Override
            public void success(int requestCode, String imagePath) {
                Glide.with(MainActivity.this).load(imagePath).into(image);
            }

            @Override
            public void fail(int requestCode, Throwable e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUploadPhotoDialog.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.button, R.id.image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                checkPermission(new OnCheckPermissionListener() {
                    @Override
                    public void success() {
                        mUploadPhotoDialog.show();
                    }

                    @Override
                    public void fail() {

                    }
                }, Manifest.permission.CAMERA);
                break;
            case R.id.image:
                checkPermission(new OnCheckPermissionListener() {
                    @Override
                    public void success() {
                        new UpdateDialog(MainActivity.this)
                                .setNeedUpdate(true)
                                .setTitleName("发现新版本 v1.0.0")
                                .setDescName("10.1M")
                                .setDownloadUrl("https://5e03325c9c5257588339c7517cb6db32.dd.cdntips.com/imtt.dd.qq.com/16891/D56151A2A3AC4DE7F751B892E8B64399.apk?mkey=5bf399337d780835&f=1455&fsname=tv.acfundanmaku.video_5.9.0.595_595.apk&csr=1bbd&cip=125.120.46.192&proto=https")
                                .setIsAutoCheck(false)
                                .setSubmitName("抢先体验")
                                .setCancleName("留在旧版")
                                .setSpaceTimeHour(5)
                                .show();
                    }

                    @Override
                    public void fail() {

                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                break;

            default:
                break;
        }

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
