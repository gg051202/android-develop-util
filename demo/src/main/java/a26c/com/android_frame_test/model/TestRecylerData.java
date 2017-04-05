package a26c.com.android_frame_test.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by guilinlin on 2017/4/1 10:59.
 * email 973635949@qq.com
 */
public class TestRecylerData implements MultiItemEntity {

    public static final int TITLE = 1;
    public static final int ITEM = 2;

    private int type ;
    private String name ;

    public TestRecylerData(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static final String TAG = "TestRecylerData";

    @Override
    public int getItemType() {
        return type;
    }
}
