package com.a26c.android.frame.adapter;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by guilinlin on 2016/11/26 10:53.
 * email 973635949@qq.com
 * TextWatcher的适配器，可以选择只重写一个方法
 */
public class TextWatcherAdapter implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
