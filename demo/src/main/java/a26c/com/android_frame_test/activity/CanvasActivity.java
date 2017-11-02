package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.a26c.android.frame.widget.BaseRecyclerView;

import java.util.ArrayList;
import java.util.List;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.adapter.TestAdapter;
import a26c.com.android_frame_test.adapter.TestAdapterData;

/**
 * 原生和React通信的示例
 */
public class CanvasActivity extends AppCompatActivity {


    private com.a26c.android.frame.widget.BaseRecyclerView baseRecyclerView;
    private android.widget.LinearLayout activityreacttalk;
    private android.widget.Button a1;
    private android.widget.Button a2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_react_talk);
        this.a2 = (Button) findViewById(R.id.a2);
        this.a1 = (Button) findViewById(R.id.a1);
        this.activityreacttalk = (LinearLayout) findViewById(R.id.activity_react_talk);
        this.baseRecyclerView = (BaseRecyclerView) findViewById(R.id.baseRecyclerView);

        TestAdapter testAdapter = new TestAdapter();
        baseRecyclerView.setViewCreator(new BaseRecyclerView.ViewCreator() {
            @Override
            public View getNoDataView() {
                return null;
            }

            @Override
            public View getErrDataView() {
                return null;
            }
        });
        baseRecyclerView.init(testAdapter, new BaseRecyclerView.NetworkHandle() {
            @Override
            public void init(BaseRecyclerView baseRecyclerView) {


            }

            @Override
            public void loadData(boolean isRefresh, String pageIndex) {
                List<TestAdapterData> list = new ArrayList<>();
//                for (int i = 20; i > 0; i--) {
//                    list.add(new TestAdapterData());
//                }
                baseRecyclerView.onLoadDataComplete();
                baseRecyclerView.setErrView("请求错误啦");

            }
        });

        a1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseRecyclerView.setNoDataView("nodata");
            }
        });
        a2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseRecyclerView.setErrView("err");
            }
        });
    }
}
