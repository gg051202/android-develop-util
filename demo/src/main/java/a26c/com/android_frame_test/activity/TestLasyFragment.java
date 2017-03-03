package a26c.com.android_frame_test.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.a26c.android.frame.base.CommonLasyLoadFragment;

import a26c.com.android_frame_test.R;

/**
 * Created by guilinlin on 2017/3/3 11:43.
 * email 973635949@qq.com
 */
public class TestLasyFragment extends CommonLasyLoadFragment {

    public static TestLasyFragment getInstance(int i) {
        TestLasyFragment tes = new TestLasyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("a", i);
        tes.setArguments(bundle);
        return tes;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_lasy;
    }


    @Override
    public void lasyInit(View view, Bundle savedInstanceState) {
        System.out.println(getArguments().getInt("a"));
    }


    @Override
    public void setEvent(View view) {

    }
}
