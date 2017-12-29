package a26c.com.android_frame_test.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.a26c.android.frame.base.CommonActivity;
import com.a26c.android.frame.util.DialogFactory;
import com.a26c.android.frame.widget.OnUploadPhotoListener;
import com.a26c.android.frame.widget.UploadPhotoDialog;
import com.bumptech.glide.Glide;

import java.io.File;

import a26c.com.android_frame_test.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by guilinlin on 2017/1/16 11:57.
 * email 973635949@qq.com
 */
public class TakePhotoActivity extends CommonActivity implements CommonActivity.OnCheckPermissionListener {


    @BindView(R.id.button)
    Button button;
    @BindView(R.id.image)
    ImageView image;
    private UploadPhotoDialog dialog;

    @Override
    public int getContainLayout() {
        return R.layout.activity_pressmisson;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    private void takePhoto() {
        dialog = new UploadPhotoDialog(this, new OnUploadPhotoListener() {
            @Override
            public boolean photoClick(int requestCode) {
                System.out.println("photoClick:" + Thread.currentThread().getName());
                return false;
            }

            @Override
            public boolean albumClick(int requestCode) {
                System.out.println("albumClick:" + Thread.currentThread().getName());
                return false;
            }

            @Override
            public void onlyReceivedImage(int requestCode) {

                System.out.println("onlyReceivedImage:" + Thread.currentThread().getName());
            }

            @Override
            public void success(int requestCode, String imagePath) {
                System.out.println("success:" + Thread.currentThread().getName());
                System.out.println(new File(imagePath).length());
                Glide.with(TakePhotoActivity.this).load(imagePath).into(image);
                System.out.println("Request code:" + requestCode);
            }

            @Override
            public void fail(int requestCode, Throwable e) {
                System.out.println("fail:" + Thread.currentThread().getName());
            }
        });
        dialog.setImageHeight(1000);
        dialog.showForResult(500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dialog.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void setEvent() {

    }


    @OnClick(R.id.button)
    public void onClick() {
        String[] permissions = {Manifest.permission.READ_CALENDAR,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        checkPermission(this, permissions);

    }

    @Override
    public void success() {
        takePhoto();
    }

    @Override
    public void fail() {
        DialogFactory.show(this, "提示", "权限申请失败，无法使用", "取消", null, "确定", null);
    }

}
