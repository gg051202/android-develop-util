package com.a26c.android.frame.adapter;


import com.a26c.android.frame.R;
import com.a26c.android.frame.widget.CommonListDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class CommonListDialogAdapter extends BaseQuickAdapter<CommonListDialog.Data, BaseViewHolder> {


    public CommonListDialogAdapter(List<CommonListDialog.Data> data) {
        super(R.layout.layout_select_type_default, data);
    }

    public CommonListDialogAdapter(List<CommonListDialog.Data> data, int resId) {
        super(resId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, CommonListDialog.Data data) {
        holder.setText(R.id.name, data.getName());
        holder.setImageResource(R.id.img, data.getResId());
        holder.setImageResource(R.id.radio, data.isSelected() ? R.mipmap.frame_a1 : R.mipmap.frame_a2);
    }
}
