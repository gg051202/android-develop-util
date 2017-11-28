package a26c.com.android_frame_test.widget;

/**
 * Created by guilinlin on 2017/11/24 14:31.
 * email 973635949@qq.com
 */

public interface RefreshHeader {

    /**
     * 正在下拉,未达到触发的高度
     *
     * @param distance       下拉的距离
     * @param reachToRefresh 是否达到了触发刷新的高度
     */
    void onPullDown(float distance, boolean reachToRefresh);

    /**
     * 正在刷新
     */
    void onRefreshing();

    /**
     * 刷新完成了，正在回弹
     */
    void onAutoScrollBack(int distance, boolean isRefreshed);

    /**
     * 刷新提示的停留事件
     */
    int getRemainTime();

}
