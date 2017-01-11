package com.a26c.android.frame.base;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by guilinlin on 16/4/19 09:23.
 * desc:adapter的背包，避免重复findViewById
 */
public class ViewHolder {

    private SparseArray<View> viewMaps;
    private View convertView;

    public ViewHolder(View convertView) {
        this.convertView = convertView;
        this.viewMaps = new SparseArray<>();
    }

    /**
     * 查找view，并把查询到的放在SparseArray中，避免下次重复查找
     *
     * @param id
     * @param <T>
     */
    public <T extends View> T getView(int id) {
        View view = viewMaps.get(id);
        if (view == null) {
            view = convertView.findViewById(id);
            viewMaps.put(id, view);
        }
        return (T) view;
    }


    public void setText(int id, String text) {
        TextView textView = getView(id);
        textView.setText(text);
    }

    public void setVisibility(int id, int visibility) {
        getView(id).setVisibility(visibility);
    }


    public void setImage(int id, int imageResourceID) {
        ImageView img = getView(id);
        img.setImageResource(imageResourceID);
    }
}