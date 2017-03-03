package a26c.com.android_frame_test.fragment;

import android.os.Bundle;
import android.view.View;

import com.a26c.android.frame.base.CommonFragment;

import a26c.com.android_frame_test.R;

/**
 * Created by guilinlin on 2016/11/18 15:30.
 * email 973635949@qq.com
 */
public class TestFragment extends CommonFragment {

	@Override
	public int getLayoutId() {
		return R.layout.fragment_test;
	}

	@Override
	public void init(View view, Bundle savedInstanceState) {

	}

	@Override
	public void setEvent(View view) {

	}
}
