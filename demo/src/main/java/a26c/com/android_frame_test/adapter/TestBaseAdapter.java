package a26c.com.android_frame_test.adapter;


import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import a26c.com.android_frame_test.R;
import a26c.com.android_frame_test.model.TestRecylerData;

public class TestBaseAdapter extends BaseMultiItemQuickAdapter<TestRecylerData> {


    public TestBaseAdapter(List<TestRecylerData> data) {
        super(data);
        addItemType(TestRecylerData.ITEM, R.layout.layout_test_base_adapter);
        addItemType(TestRecylerData.TITLE, R.layout.layout_test_base_adapter);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, TestRecylerData testRecylerData) {
        baseViewHolder.setText(R.id.text, testRecylerData.getName());
    }
}
