package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.a26c.android.frame.util.DialogFactory;
import com.a26c.android.frame.widget.CommonMenu;
import com.a26c.android.frame.widget.UpdateDialog;

import java.util.ArrayList;
import java.util.List;

import a26c.com.android_frame_test.R;

public class Main2aActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 算房贷
     */
    private Button mFangdaiTextView;
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
        mFangdaiTextView = (Button) findViewById(R.id.fangdaiTextView);
        mFangdaiTextView.setOnClickListener(this);
        mImage = (ImageView) findViewById(R.id.image);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(this);
        mCommonMenu = (CommonMenu) findViewById(R.id.commonMenu);

        mCommonMenu.getRedPointView().setEmptyString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.fangdaiTextView:
                new UpdateDialog(this)
                        .setNeedUpdate(false)
                        .setTitleName("发现新版本 ")
                        .setDescName("11M")
                        .setDownloadUrl("https://44c48dfbc00abf22971f5bf20622e816.dd.cdntips.com/imtt.dd.qq.com/16891/2904F0968B511EA84AFBEFE8FCF21CE3.apk?mkey=5cbee67c73c39246&f=8eb5&fsname=tv.acfundanmaku.video_5.16.0.672_672.apk&csr=1bbd&cip=115.195.180.179&proto=https")
                        .setIsAutoCheck(false)
                        .setSubmitName("抢先体验")
                        .setCancleName("留在旧版")
                        .setSpaceTimeHour(1)
                        .show();
                break;
            case R.id.button:
//                DialogFactory.show(this, "提示", "123", "确定", null, "取消", null);
//                DialogFactory.showProgress(this, "123", false) ;
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
}
