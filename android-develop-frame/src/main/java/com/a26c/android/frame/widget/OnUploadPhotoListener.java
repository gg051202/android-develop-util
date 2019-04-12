package com.a26c.android.frame.widget;

import android.net.Uri;

/**
 * 压缩图片后获取图片成功的接口
 */
public interface OnUploadPhotoListener {

    /**
     * 点击相机的操作,返回true表示消费掉事件
     */
    boolean photoClick(int requestCode);

    /**
     * 点击相册的操作,返回true表示消费掉事件
     */
    boolean albumClick(int requestCode);

    /**
     * 从相册或者相机刚刚获取到图片时
     */
    void onlyReceivedImage(int requestCode);

    /**
     * @param isVideo 返回的是图片还是视频
     * @param imagePath     图像被保存到本地的地址
     * @param uri           如果是视频，这个uri肯定不为空，图片则不一定
     */
    void success(int requestCode, boolean isVideo, String imagePath, Uri uri);

    void fail(int requestCode, Throwable e);
}