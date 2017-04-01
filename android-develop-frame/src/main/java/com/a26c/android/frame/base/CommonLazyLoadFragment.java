package com.a26c.android.frame.base;


import android.os.Bundle;
import android.view.View;

/**
 * Created by guilinlin on 16/7/20 11:20.
 * email 973635949@qq.com
 *
 * @desc Fragment 基类  使用方法 直接在getLayoutId中传入布局
 */
public abstract class CommonLazyLoadFragment extends CommonFragment {

    /**
     * 标记已加载完成，只能加载一次
     */
    private boolean hasLoaded = false;
    /**
     * 标记是否已经onCreate
     */
    private boolean isCreated = false;
    /**
     * 界面对于用户是否可见
     */
    private boolean isVisibleToUser = false;
    private View view;

    @Override
    public void init(View view, Bundle savedInstanceState) {
        isCreated = true;
        this.view = view;
        lazyLoad(this.view, savedInstanceState);
    }

    /**
     * 监听界面是否展示给用户，实现懒加载
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
        super.setUserVisibleHint(isVisibleToUser);
        lazyLoad(view, null);
    }


    /**
     * 懒加载方法，获取数据什么的放到这边来使用，在切换到这个界面时才进行网络请求
     */
    private void lazyLoad(View view, Bundle savedInstanceState) {

        //如果该界面不对用户显示、已经加载、fragment还没有创建，
        //三种情况任意一种，不获取数据
        if (!isVisibleToUser || hasLoaded || !isCreated) {
            return;
        }
        lazyInit(view, savedInstanceState);
        hasLoaded = true;
    }

    /**
     * 懒加载的初始化方法
     */
    public abstract void lazyInit(View view, Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isCreated = false;
        hasLoaded = false;
    }
}
