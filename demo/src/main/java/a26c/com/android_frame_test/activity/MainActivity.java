package a26c.com.android_frame_test.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.a26c.android.frame.base.CommonActivity;
import com.a26c.android.frame.widget.CommonListDialog;
import com.a26c.android.frame.widget.OnUploadPhotoListener;
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUploadPhotoDialog.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.button, R.id.image, R.id.fangdaiTextView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fangdaiTextView:
                gotoActivity(BuyRoomActivity.class);
                break;
            case R.id.button:
                UploadPhotoDialog.deleteCacheFiles();
                new CommonListDialog(mActivity)
                        .addData("1", "a1")
                        .addData("2", "a2")
                        .addData("3", "a3")
                        .setCurrentKey("3")
                        .setTitleName("123")
                        .setShowButtonLayout(true)
                        .setDialogSubmitListener(new CommonListDialog.OnDialogSubmitListener() {
                            @Override
                            public void submit(String key, String value) {
                                System.out.println(value);
                            }
                        })
                        .show();
                break;
            case R.id.image:

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
                    public void success(int requestCode, boolean isVideo, String imagePath) {
                        Glide.with(MainActivity.this).load(imagePath).into(image);
                        System.out.println(imagePath);
                    }

                    @Override
                    public void fail(int requestCode, Throwable e) {
                        e.printStackTrace();
                    }
                });
                mUploadPhotoDialog.setSelectMediaType(UploadPhotoDialog.SELECT_IMAGE_AND_VIDEO);
                checkPermission(new OnCheckPermissionListener() {
                    @Override
                    public void success() {

                        mUploadPhotoDialog.show();
                    }

                    @Override
                    public void fail() {

                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);


                break;

            default:
                break;
        }
    }


}
