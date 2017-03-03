package a26c.com.android_frame_test.model;

import java.util.ArrayList;
import java.util.List;

import a26c.com.android_frame_test.adapter.TestBaseAdapterData;

/**
 * Created by guilinlin on 2017/1/4 17:22.
 * email 973635949@qq.com
 */
public class TestModel {

    public static List<TestBaseAdapterData> getTestList(String pageIndex) {
        List<TestBaseAdapterData> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new TestBaseAdapterData(pageIndex));
        }

        return list;
    }


}
