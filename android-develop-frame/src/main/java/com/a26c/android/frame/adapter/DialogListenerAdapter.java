package com.a26c.android.frame.adapter;

import com.a26c.android.frame.widget.UploadPhotoDialog;

/**
 * Created by guilinlin on 2017/1/12 09:23.
 * email 973635949@qq.com
 */
public class DialogListenerAdapter implements UploadPhotoDialog.DialogListener {

    @Override
    public boolean photoClick() {
        return false;
    }

    @Override
    public boolean albumClick() {
        return false;
    }

}
