package a26c.com.android_frame_test.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by guilinlin on 2017/11/24 14:37.
 * email 973635949@qq.com
 */

public class XiaolinHeader extends RelativeLayout implements RefreshHeader {

    public static String REFRESH_HEADER_PULLDOWN = "下拉可以刷新";
    public static String REFRESH_HEADER_REFRESHING = "正在刷新...";
    public static String REFRESH_HEADER_RELEASE = "释放立即刷新";
    public static String REFRESH_HEADER_FINISH = "刷新完成";
    private TextView titleText;

    public XiaolinHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public XiaolinHeader(Context context) {
        super(context);
        initView(context, null);
    }


    private void initView(Context context, AttributeSet attrs) {

        LayoutParams titleLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLayoutParams.addRule(CENTER_IN_PARENT);
        titleText = new TextView(context);
        titleText.setText(REFRESH_HEADER_PULLDOWN);
        titleText.setTextColor(0xff666666);
        addView(titleText, titleLayoutParams);


    }

    @Override
    public void onPullDown(float distance, boolean reachToRefresh) {
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.height = (int) distance;
        requestLayout();
        titleText.setText(reachToRefresh?REFRESH_HEADER_RELEASE:REFRESH_HEADER_PULLDOWN);
    }

    @Override
    public void onRefreshing() {
        titleText.setText(REFRESH_HEADER_REFRESHING);

    }

    @Override
    public void onAutoScrollBack(int distance, boolean isRefreshed) {
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.height = distance;
        requestLayout();
        titleText.setText(isRefreshed?REFRESH_HEADER_FINISH:REFRESH_HEADER_REFRESHING);
    }

    @Override
    public int getRemainTime() {
        return 2000;
    }
}
