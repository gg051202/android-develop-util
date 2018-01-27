package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.a26c.android.frame.widget.BaseRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.adapter.TestAdapter;
import a26c.com.android_frame_test.adapter.TestAdapterData;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BaseRecyclerViewActivity extends AppCompatActivity {

    @BindView(R.id.refresh)
    Button mRefresh;
    @BindView(R.id.refresh2)
    Button mRefresh2;
    @BindView(R.id.refresh3)
    Button mRefresh3;
    @BindView(R.id.baseRecyclerView)
    BaseRecyclerView mBaseRecyclerView;
    @BindView(R.id.checkbox)
    CheckBox mCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_recycler_view_acitivity);
        ButterKnife.bind(this);

        final TestAdapter adapter = new TestAdapter();
        mBaseRecyclerView.init(adapter, new BaseRecyclerView.NetworkHandle() {
            @Override
            public void init(BaseRecyclerView baseRecyclerView) {
                baseRecyclerView.setPageSize(20);
            }

            @Override
            public void loadData(boolean isRefresh, String pageIndex) {
                System.out.println(String.format("isRefresh:%s , pageIndex:%s", isRefresh, pageIndex));
                final List<TestAdapterData> list = new ArrayList<>();
                if (mCheckbox.isChecked()) {
                    mBaseRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("获取数据成功\n");
                            System.out.println(" ");
                            for (int i = 0; i < 20; i++) {
                                list.add(new TestAdapterData(i));
                            }
                            success(list);
                        }
                    }, 1000);
                } else {
                    if (new Random().nextBoolean()) {
                        mBaseRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("获取数据失败\n");
                                System.out.println(" ");
                                fail();
                            }
                        }, 1000);
                    } else {
                        System.out.println("获取数据失败\n");
                        System.out.println(" ");
                        fail();
                    }


                }

            }
        });
    }


    private void success(List<TestAdapterData> list) {
        mBaseRecyclerView.onLoadDataComplete(list);
    }

    private void fail() {
        mBaseRecyclerView.onLoadDataCompleteErr();
    }


    @OnClick({R.id.refresh, R.id.refresh2, R.id.refresh3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.refresh:
                mBaseRecyclerView.getAdapter().getData().clear();
                mBaseRecyclerView.callRefreshListener();
                break;
            case R.id.refresh2:
                break;
            case R.id.refresh3:
                break;
        }
    }
}
