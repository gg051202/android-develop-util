package a26c.com.android_frame_test.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import a26c.com.android_frame_test.R;

public class TestBaseAdapter extends BaseQuickAdapter<TestBaseAdapterData> {
	public TestBaseAdapter(List<TestBaseAdapterData> data) {
		super(R.layout.layout_test_base_adapter, data);
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, TestBaseAdapterData data) {

	}
}
