package a26c.com.android_frame_test.activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import a26c.com.android_frame_test.R;

public class TestAdapter extends BaseQuickAdapter<TestAdapterData> {
    public TestAdapter() {
        super(R.layout.layout_test_adapter, null);
    }

    @Override
    protected void convert(BaseViewHolder holder, TestAdapterData data) {

    }
}
