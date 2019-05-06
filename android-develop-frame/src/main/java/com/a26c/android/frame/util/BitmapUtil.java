package com.a26c.android.frame.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Bitmap工具类主要包括获取Bitmap和对Bitmap的操作
 *
 * @author jingle1267@163.com
 */
public final class BitmapUtil {
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {

        try {
            byte[] bytes = readStream(
                    context.getContentResolver().openInputStream(Uri.parse(uri.toString())));
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    /**
     * 图片保存至本应用下
     *
     * @param fileName
     * @param bitmap
     * @param size     空表示不压缩
     *                 文件大小
     * @return 保存图片的地址
     */
    public static String savePicture(Context context, String fileName, Bitmap bitmap, Integer size) {

        FileOutputStream fos = null;
        // 压缩图片的大小为多少KB
        if (size != null) {
            bitmap = compress(bitmap, (double) size);
        }
        try {
            // 直接写入名称即可，没有会被自动创建；私有：只有本应用才能访问，重新内容写入会被覆盖
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 把图片写入指定文件夹中

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                    fos = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return context.getFilesDir() + "/" + fileName;
    }


    public static boolean savePicture(String fileName, Bitmap bitmap) {

        FileOutputStream fos = null;
        try {
            // 直接写入名称即可，没有会被自动创建；私有：只有本应用才能访问，重新内容写入会被覆盖
            fos = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 把图片写入指定文件夹中
            try {
                fos.close();
                fos = null;
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (null != fos) {
                try {
                    fos.close();
                    fos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 图片压缩方法：（使用compress的方法）
     * <p/>
     * <br>
     * <b>说明</b> 如果bitmap本身的大小小于maxSize，则不作处理
     *
     * @param bitmap  要压缩的图片
     * @param maxSize 压缩后的大小，单位kb
     */
    public static Bitmap compress(Bitmap bitmap, double maxSize) {
        // 将bitmap放至数组中，意在获得bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 格式、质量、输出流
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] b = baos.toByteArray();
        // 将字节换成KB
        double mid = b.length / 1024;
        // 获取bitmap大小 是允许最大大小的多少倍
        double i = mid / maxSize;
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (i > 1) {
            // 缩放图片 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
            // （保持宽高不变，缩放后也达到了最大占用空间的大小）
            bitmap = compass(bitmap, bitmap.getWidth() / Math.sqrt(i), bitmap.getHeight() / Math.sqrt(i));
        }
        return bitmap;
    }

    /**
     * 图片的缩放方法
     *
     * @param src       ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     */
    public static Bitmap compass(Bitmap src, double newWidth, double newHeight) {
        // 记录src的宽高
        float width = src.getWidth();
        float height = src.getHeight();
        // 创建一个matrix容器
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 开始缩放
        matrix.postScale(scaleWidth, scaleHeight);
        // 创建缩放后的图片
        return Bitmap.createBitmap(src, 0, 0, (int) width, (int) height, matrix, true);
    }

}