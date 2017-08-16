package a26c.com.android_frame_test.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.a26c.android.frame.base.CommonLazyLoadFragment;

import a26c.com.android_frame_test.R;

/**
 * Created by guilinlin on 2017/3/3 11:43.
 * email 973635949@qq.com
 */
public class TestLazyFragment extends CommonLazyLoadFragment {

    public static TestLazyFragment getInstance(int i) {
        TestLazyFragment tes = new TestLazyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("a", i);
        tes.setArguments(bundle);
        return tes;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_lazy;
    }

    @Override
    public void init(View view, Bundle savedInstanceState) {
        super.init(view, savedInstanceState);
    }

    @Override
    public void lazyInit(View view, Bundle savedInstanceState) {
        Log.i("tag", "当且仅当Fragment第一次显示时，加载数据");
    }



    @Override
    public void setEvent(View view) {

    }
}
