package a26c.com.android_frame_test.activity;

import com.a26c.android.frame.util.DialogFactory;

/**
 * Created by guilinlin on 2017/6/30 10:32.
 * email 973635949@qq.com
 */

public class DialogSingleData implements DialogFactory.ChoiceData {
    private String key;
    private String value;
    private boolean isSelected;

    public DialogSingleData(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean isSecleted) {
        this.isSelected = isSecleted;
    }
}
