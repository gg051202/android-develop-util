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
    public static String REFRESH_HEADER_FAILED = "刷新失败";

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
        TextView mTitleText = new TextView(context);
        mTitleText.setText(REFRESH_HEADER_PULLDOWN);
        mTitleText.setTextColor(0xff666666);
        addView(mTitleText);


    }

    @Override
    public void onPullDown(float distance, boolean reachToRefresh) {
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.height  = (int) distance;
        requestLayout();
    }

    @Override
    public void onRefreshing() {

    }

    @Override
    public void onRefreshComplete(int distance, boolean isRefreshed) {

    }

    @Override
    public int getRemainTime() {
        return 2000;
    }
}
