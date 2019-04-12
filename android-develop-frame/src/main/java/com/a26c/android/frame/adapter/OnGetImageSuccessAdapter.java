package com.a26c.android.frame.adapter;

import com.a26c.android.frame.widget.OnUploadPhotoListener;

/**
 * Created by guilinlin on 2017/8/30 11:00.
 * email 973635949@qq.com
 */

public class OnGetImageSuccessAdapter implements OnUploadPhotoListener {

    @Override
    public boolean photoClick(int requestCode) {
        return false;
    }

    @Override
    public boolean albumClick(int requestCode) {
        return false;
    }

    @Override
    public void onlyReceivedImage(int requestCode) {

    }

    @Override
    public void success(int requestCode, boolean isVideo, String filePath) {

    }

    @Override
    public void fail(int requestCode, Throwable e) {

    }
}
