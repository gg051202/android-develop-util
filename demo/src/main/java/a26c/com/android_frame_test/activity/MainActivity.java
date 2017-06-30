package a26c.com.android_frame_test.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a26c.android.frame.adapter.DialogListenerAdapter;
import com.a26c.android.frame.base.CommonActivity;
import com.a26c.android.frame.base.CommonFragmentPagerAdapter;
import com.a26c.android.frame.base.HtmlActivity;
import com.a26c.android.frame.util.CommonUtils;
import com.a26c.android.frame.util.DialogFactory;
import com.a26c.android.frame.widget.BaseRecyclerView;
import com.a26c.android.frame.widget.CommonItem;
import com.a26c.android.frame.widget.FrameRatingBar;
import com.a26c.android.frame.widget.MutiItemDecoration;
import com.a26c.android.frame.widget.RedPointTextView;
import com.a26c.android.frame.widget.UploadPhotoDialog;
import com.a26c.android.frame.widget.VerticalScrollView;

import java.util.ArrayList;
import java.util.List;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.adapter.TestBaseAdapter;
import a26c.com.android_frame_test.model.TestModel;
import a26c.com.android_frame_test.model.TestRecylerData;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends CommonActivity {

    @BindView(R.id.ratingBar)
    FrameRatingBar ratingBar;
    @BindView(R.id.reactNative)
    Button reactNative;
    @BindView(R.id.commonItem2)
    CommonItem commonItem2;
    @BindView(R.id.red1)
    RedPointTextView red1;
    @BindView(R.id.red2)
    RedPointTextView red2;
    @BindView(R.id.red3)
    RedPointTextView red3;
    @BindView(R.id.red4)
    RedPointTextView red4;
    @BindView(R.id.red5)
    RedPointTextView red5;
    @BindView(R.id.uploadImageButton)
    Button uploadImageButton;
    @BindView(R.id.uploadImageView)
    ImageView uploadImageView;
    @BindView(R.id.gotoHtmlButton)
    Button gotoHtmlButton;
    @BindView(R.id.baseRecyclerView)
    BaseRecyclerView baseRecyclerView;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    @BindView(R.id.dialog)
    Button dialog;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.verticalView)
    VerticalScrollView<String> verticalView;
    private int number5 = 1002;

    private UploadPhotoDialog uploadPhotoDialog;

    @Override
    public int getContainLayout() {
        return R.layout.activity_main;
    }

    private String key;

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("测试标题");

        textRatingBar();

        testRedPointView();

        testBaseRecyclerView();

        final List<DialogSingleData> list = new ArrayList<>();
        for (int i = 0; i < 225; i++) {
            list.add(new DialogSingleData("key" + i, "value" + i));
        }

        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                DialogFactory.showSingle(MainActivity.this, list, key, new DialogFactory.OnDialogSelectedListener() {
//                    @Override
//                    public void onSelect(String k, String value) {
//                        key = k;
//                        System.out.println(k + "." + value);
//                    }
//                });
                DialogFactory.showMulti(MainActivity.this, list, new DialogFactory.OnDialogSelectedListener() {
                    @Override
                    public void onSelect(String key, String value) {
                        for (DialogFactory.ChoiceData choiceData : list) {
                            if (choiceData.isSelected()) {
                                System.out.println(choiceData.getKey() + "." + choiceData.getValue());
                            }
                        }
                    }
                });

            }
        });

        CommonFragmentPagerAdapter adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addTab(TestLazyFragment.getInstance(1), "1");
        adapter.addTab(TestLazyFragment.getInstance(2), "2");
        adapter.addTab(TestLazyFragment.getInstance(3), "3");
        adapter.addTab(TestLazyFragment.getInstance(4), "4");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);


        verticalView.setInflateView(new VerticalScrollView.InflateView<String>() {
            @Override
            public View inflate(String s) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.layout_text2, null);

                TextView textView = (TextView) view.findViewById(R.id.TextView);
                textView.setText(s);
                return view;
            }
        });
        verticalView.setTime(2);
        verticalView.setOnVerticalViewClickListener(new VerticalScrollView.OnVerticalViewClickListener<String>() {
            @Override
            public void itemClick(int position, String data) {
                System.out.println(position);
            }
        });
        List<String> list3 = new ArrayList<>();
        list3.add("1");
        list3.add("2");
        list3.add("3");
        list3.add("4");
        verticalView.setmList(list3);
        verticalView.start();

    }

    private void textRatingBar() {
        ratingBar.setRating(3.4f);
    }

    private void testBaseRecyclerView() {
        final List<TestRecylerData> list = TestModel.getTestList();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        baseRecyclerView.setLayoutManager(layoutManager);
        TestBaseAdapter adapter = new TestBaseAdapter(list);
        baseRecyclerView.setAdapter(adapter);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return list.get(position).getItemType() == TestRecylerData.TITLE ? 5 : 1;
            }
        });

        baseRecyclerView.removeDivider();
        baseRecyclerView.addDivider(new MutiItemDecoration(MutiItemDecoration.Type.ALL, 5, 0xff00ff00));
    }

    private void testRedPointView() {
        red1.setNumber(1);
        red2.setNumber(2);
        red3.setNumber(33);
        red4.setNumber(44);
    }

    @Override
    protected void setEvent() {


    }


    @OnClick({R.id.red5, R.id.uploadImageButton, R.id.gotoHtmlButton, R.id.reactNative})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reactNative:
                DialogFactory.showProgress(this, "正在绑定", true);
                break;

            case R.id.red5:
                red5.setNumber(number5 /= 2);
                break;
            case R.id.uploadImageButton:
                if (CommonUtils.checkPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        CommonUtils.checkPermission(mContext, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, 1);
                } else {
                    showUploadDialog();
                }
                break;

            case R.id.gotoHtmlButton:
                HtmlActivity.languch(mActivity, "aaa", "https://ssl.22.cn/");
                break;

        }
    }

    private void showUploadDialog() {
        if (uploadPhotoDialog == null) {
            uploadPhotoDialog = new UploadPhotoDialog(mContext, 0, new DialogListenerAdapter() {
                @Override
                public void receiveImage() {
                    super.receiveImage();
                }
            });
        }
        uploadPhotoDialog.showDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadPhotoDialog.onActivityResult(requestCode, resultCode, data, new UploadPhotoDialog.OnGetImageSuccessListener() {
            @Override
            public void success(Bitmap bitmap, String imagePath) {
                uploadImageView.setImageBitmap(bitmap);
                Log.i(TAG, "保存到本地的地址：" + imagePath);
            }

            @Override
            public void fail() {
                Toast.makeText(MainActivity.this, "失败啦", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            showUploadDialog();
        } else {
            DialogFactory.show(mContext, "提示", "上传头像需要相机、文件读写权限", "确定", null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        verticalView.stop();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
