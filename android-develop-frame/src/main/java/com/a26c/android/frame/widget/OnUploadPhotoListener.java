package com.a26c.android.frame.widget;

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
     * @param imagePath 图像被保存到本地的地址
     */
    void success(int requestCode, String imagePath);

    void fail(int requestCode, Throwable e);
}