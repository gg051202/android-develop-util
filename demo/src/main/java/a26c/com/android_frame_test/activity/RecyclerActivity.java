package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.a26c.android.frame.base.CommonActivity;
import com.a26c.android.frame.widget.MutiItemDecoration;

import java.util.List;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.adapter.TestBaseAdapter;
import a26c.com.android_frame_test.model.TestModel;
import a26c.com.android_frame_test.model.TestRecylerData;

public class RecyclerActivity extends CommonActivity {
    RecyclerView recyclerView;

    @Override
    public int getContainLayout() {
        return R.layout.activity_recycler;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final List<TestRecylerData> list = TestModel.getTestList();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(layoutManager);
        TestBaseAdapter adapter = new TestBaseAdapter(list);
        recyclerView.setAdapter(adapter);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return list.get(position).getItemType() == TestRecylerData.TITLE ? 5 : 1;
            }
        });

        recyclerView.addItemDecoration(new MutiItemDecoration(MutiItemDecoration.Type.VERTICAL, 10, 0xffff0000));

    }

    @Override
    protected void setEvent() {

    }

}
