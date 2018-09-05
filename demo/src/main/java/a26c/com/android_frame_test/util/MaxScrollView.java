package a26c.com.android_frame_test.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by guilinlin on 2017/8/11 09:29.
 * email 973635949@qq.com
 */

public class MaxScrollView extends ScrollView {
    public MaxScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t > 770) {
            scrollTo(0, 770);
            return;
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }


}
