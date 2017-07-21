package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.a26c.android.frame.base.CommonActivity;
import com.a26c.android.frame.widget.MutiItemDecoration;

import a26c.com.android_frame_test.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends CommonActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    public int getContainLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        TestAdapter testAdapter = new TestAdapter();
        for (int i = 20; i > 0; i--) {
            testAdapter.add(0, new TestAdapterData());
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new MutiItemDecoration(MutiItemDecoration.Type.ALL));
        recyclerView.setAdapter(testAdapter);
    }

    @Override
    protected void setEvent() {

    }
}
