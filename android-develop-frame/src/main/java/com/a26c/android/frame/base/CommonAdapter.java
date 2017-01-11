package com.a26c.android.frame.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guilinlin on 16/4/19 09:11.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected List<T> list;
    protected Context context;
    protected int layoutId;

    public CommonAdapter(Context context, List<T> list, int layoutId) {
        this.context = context;
        this.list = list;
        this.layoutId = layoutId;
    }

    public CommonAdapter(Context context, int layoutId) {
        list = new ArrayList<>();
        this.context = context;
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {//如果缓存view为空，则解析一个布局，并创建对饮的viewHolder设置tag
            convertView = LayoutInflater.from(context).inflate(layoutId, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        convert(viewHolder, getItem(i),i);

        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * 处理布局
     *
     * @param viewHolder
     * @param data
     */
    public abstract void convert(ViewHolder viewHolder, T data,int position);

    /**
     * 对面提供一个增加list的方法，在一些简单的地方可以直接用这个
     *
     * @param data
     */
    public void add(T data) {
        list.add(data);
    }
    public List<T> getList(){
        return list;
    }
}
