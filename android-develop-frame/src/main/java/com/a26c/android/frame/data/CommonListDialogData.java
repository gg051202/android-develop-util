package com.a26c.android.frame.data;

import com.a26c.android.frame.widget.CommonListDialog;

public class CommonListDialogData implements CommonListDialog.Data {
    private String name;
    private String key;
    private int resId;
    private boolean isSelected;

    public CommonListDialogData(String key, String name, int resId) {
        this.name = name;
        this.key = key;
        this.resId = resId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
