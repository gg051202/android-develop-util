package a26c.com.android_frame_test.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a26c.android.frame.util.CommonUtils;
import com.a26c.android.frame.util.DialogFactory;
import com.a26c.android.frame.widget.CommonMenu;
import com.a26c.android.frame.widget.UpdateDialog;

import java.util.ArrayList;
import java.util.List;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.util.OnCheckPermissionListener;
import a26c.com.android_frame_test.util.PermissionData;

import static com.a26c.android.frame.util.CommonUtils.PACKAGE_INSTALLED_ACTION;

public class Main2aActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 算房贷
     */
    private Button mFangdaiTextView;
    private TextView mSDKTextView;
    private ImageView mImage;
    /**
     * 更新
     */
    private Button mButton;
    private CommonMenu mCommonMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mFangdaiTextView = findViewById(R.id.fangdaiTextView);
        mSDKTextView =  findViewById(R.id.sdkTextView);
        mFangdaiTextView.setOnClickListener(this);
        mImage = findViewById(R.id.image);
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(this);
        mCommonMenu = findViewById(R.id.commonMenu);

        mCommonMenu.getRedPointView().setEmptyString();
        mSDKTextView.setText(String.format("Android %s", Build.VERSION.RELEASE));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fangdaiTextView:
                UpdateDialog updateDialog = new UpdateDialog(this)
                        .setNeedUpdate(true)
                        .setTitleName("发现新版本 ")
                        .setDescName("11M")
                        .setDownloadUrl("https://718e31e8894454e98bf531c997f4e6fb.dd.cdntips.com/imtt.dd.qq.com/16891/apk/0EA25E7AB5CEF53B09162351A941A990.apk?mkey=5dca5e157ae94b02&f=8935&fsname=com.kyle.sfc14.jqm_1.11.0807_402.apk&csr=1bbd&cip=122.233.109.247&proto=https")
                        .setIsAutoCheck(false)
                        .setAuthority(getPackageName() + ".mytest")
                        .setSubmitName("抢先体验")
                        .setCancleName("留在旧版")
                        .setSpaceTimeHour(1);
                updateDialog.setOnUpdateListener(updateDialog1 -> {

                    if (CommonUtils.isOverAndroid_10()) {
                        updateDialog1.getAlertDialog().show();
                        updateDialog1.startDownload();
                    } else {
                        checkPermission(new OnCheckPermissionListener() {
                            @Override
                            public void success() {
                                updateDialog1.getAlertDialog().show();
                                updateDialog1.startDownload();
                            }

                            @Override
                            public boolean fail() {
                                return false;
                            }
                        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }


                });
                updateDialog.show();
                break;
            case R.id.button:
                ArrayList<DialogFactory.SimpleChoiceData> list = new ArrayList<>();
                list.add(new DialogFactory.SimpleChoiceData("1", "1"));
                DialogFactory.showMulti(this, list, new DialogFactory.OnDialogSelectedListener<DialogFactory.SimpleChoiceData>() {
                    @Override
                    public void onSelect(DialogFactory.SimpleChoiceData data, List<DialogFactory.SimpleChoiceData> list) {

                    }
                });
                break;
        }
    }


    private static final int REQUEST_PERMISSION = 1221;
    private OnCheckPermissionListener checkPermissionListener;
    private List<PermissionData> list;

    public void checkPermission(@NonNull OnCheckPermissionListener checkPermissionListener, String... permissions) {
        checkPermission(false, checkPermissionListener, permissions);
    }

    public void checkPermission(boolean donotRequest, @NonNull OnCheckPermissionListener checkPermissionListener, String... permissions) {


        this.checkPermissionListener = checkPermissionListener;
        list = new ArrayList<>();
        for (String permission : permissions) {
            PermissionData data = new PermissionData();
            data.setPermissionName(permission);
            data.setGranted(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
            if (!data.isGranted()) {//对要申请的权限进行一次筛选，list里的数据为需要筛选的权限
                list.add(data);
            }
        }

        if (!list.isEmpty()) {
            String[] strs = new String[list.size()];
            int i = 0;
            for (PermissionData data : list) {
                strs[i++] = data.getPermissionName();
            }
            if (donotRequest) {
                checkPermissionListener.fail();
            } else {
                ActivityCompat.requestPermissions(this, strs, REQUEST_PERMISSION);
            }
        } else {
            checkPermissionListener.success();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_PERMISSION) {
            return;
        }

        String failPermissionName = "";
        int isAllGranted = 0;
        for (int i = 0; i < list.size(); i++) {
            PermissionData item = list.get(i);
            if (i < grantResults.length) {
                item.setResult(grantResults[i] == PackageManager.PERMISSION_GRANTED);
            } else {
                item.setResult(false);
            }
            if (!item.isResult()) {
                isAllGranted++;//如果有一个申请结果失败，就自增1，表示申请失败了

                switch (item.getPermissionName()) {
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                        failPermissionName = "读写文件";
                        break;
                    case Manifest.permission.CALL_PHONE:
                        failPermissionName = "拨打电话";
                        break;
                    case Manifest.permission.CAMERA:
                        failPermissionName = "拍摄照片";
                        break;
                    case Manifest.permission.RECORD_AUDIO:
                        failPermissionName = "录音";
                        break;
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                        failPermissionName = "定位";
                        break;

                    default:
                        break;
                }
            }
        }

        if (isAllGranted == 0) {
            checkPermissionListener.success();
        } else {
            if (!checkPermissionListener.fail()) {
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                SpannableString s = new SpannableString("权限管理");
                ssb.append("暂未允许乐偶云").append(failPermissionName).append("，您可以在").append(s).append("中开启");
                DialogFactory.show(this, "提示", ssb,
                        "算了吧", null,
                        "去系统设置", (dialog, which) -> {
                        });

            }
        }

    }


    // Note: this Activity must run in singleTop launchMode for it to be able to receive the intent
    // in onNewIntent().
    @Override
    protected void onNewIntent(Intent intent) {
        System.out.println(123);
        Bundle extras = intent.getExtras();
        if (PACKAGE_INSTALLED_ACTION.equals(intent.getAction())) {
            int status = extras.getInt(PackageInstaller.EXTRA_STATUS);
            String message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE);
            switch (status) {
                case PackageInstaller.STATUS_PENDING_USER_ACTION:
                    // This test app isn't privileged, so the user has to confirm the install.
                    Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
                    startActivity(confirmIntent);

                    break;
                case PackageInstaller.STATUS_SUCCESS:
                    Toast.makeText(this, "Install succeeded!", Toast.LENGTH_SHORT).show();
                    break;
                case PackageInstaller.STATUS_FAILURE:
                case PackageInstaller.STATUS_FAILURE_ABORTED:
                case PackageInstaller.STATUS_FAILURE_BLOCKED:
                case PackageInstaller.STATUS_FAILURE_CONFLICT:
                case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
                case PackageInstaller.STATUS_FAILURE_INVALID:
                case PackageInstaller.STATUS_FAILURE_STORAGE:
                    Toast.makeText(this, "Install failed! " + status + ", " + message,
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Unrecognized status received from installer: " + status,
                            Toast.LENGTH_SHORT).show();
            }
        }
    }
}
