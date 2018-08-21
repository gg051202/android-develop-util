package a26c.com.android_frame_test.activity;

import android.os.Bundle;

import com.a26c.android.frame.base.CommonActivity;
import com.a26c.android.frame.widget.BaseRecyclerView;

import java.util.List;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.adapter.TestAdapter;
import a26c.com.android_frame_test.adapter.TestAdapterData;
import a26c.com.android_frame_test.model.TestModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecylerViewAcitivity extends CommonActivity {


    @BindView(R.id.baseRecyclerView)
    BaseRecyclerView mBaseRecyclerView;

    @Override
    public int getContainLayout() {
        return R.layout.activity_recyler_view_acitivity;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        TestAdapter adapter = new TestAdapter();
        mBaseRecyclerView.init(adapter, new BaseRecyclerView.NetworkHandle() {
            @Override
            public void init(BaseRecyclerView baseRecyclerView) {
                baseRecyclerView.setPageSize(20);
            }

            @Override
            public void loadData(boolean isRefresh, String pageIndex) {
                new TestModel().getTestList(Integer.parseInt(pageIndex),new TestModel.OnGetDataListener() {
                    @Override
                    public void success(List<TestAdapterData> list) {
                        mBaseRecyclerView.onLoadDataComplete(list);
                    }
                });
            }
        });

    }

    @Override
    protected void setEvent() {

    }
}

