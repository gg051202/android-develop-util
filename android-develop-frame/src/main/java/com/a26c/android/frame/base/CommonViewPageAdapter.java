package com.a26c.android.frame.base;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guilinlin on 16/11/4 11:23.
 * email 973635949@qq.com
 */
public class CommonViewPageAdapter extends PagerAdapter {

    private List<View> viewList;

    public CommonViewPageAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    public CommonViewPageAdapter() {
        this.viewList = new ArrayList<>();
    }

    public void addView(View view) {
        viewList.add(view);
    }


    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }
}

