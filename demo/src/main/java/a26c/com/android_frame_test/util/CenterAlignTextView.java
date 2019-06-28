package a26c.com.android_frame_test.util;

import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by guilinlin on 2017/8/7 17:10.
 * email 973635949@qq.com
 */

public class CenterAlignTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int ruleSize = 5;

    public CenterAlignTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLetterSpacing(0.2f);
    }
}
