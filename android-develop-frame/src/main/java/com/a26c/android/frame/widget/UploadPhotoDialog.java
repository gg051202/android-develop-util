package com.a26c.android.frame.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.a26c.android.frame.R;
import com.a26c.android.frame.util.CommonUtils;
import com.a26c.android.frame.util.FrameBitmapUtil;
import com.a26c.android.frame.util.FrameCropUtils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 封装一个获取图片的工具类，
 * show（）方法调用后弹出选择相机或者相册的dialog，
 * onActivityResult（）方法装饰了相关操作，可以直接在对应Activity的OnActivityResult的方法里调用，
 *
 * @author gl
 */
public class UploadPhotoDialog implements View.OnClickListener {

    private static final String TAG = "UploadPhotoDialog";

    public static final byte RESULT_CAMERA = 12;
    public static final byte RESULT_ALBUM = 13;
    public static final byte RESULT_ZOOM_PHOTO = 14;

    public static final byte PHOTO = 1;
    public static final byte ALBUM = 2;
    public static final byte PHOTO_AND_ALBUM = 3;

    /**
     * 默认相册和拍照的入口都有
     */
    private int type = PHOTO_AND_ALBUM;

    /**
     * 裁剪出来的图片的高
     */
    private int imageHeight = 200;
    /**
     * 图片的高
     */
    private int imageWidth;
    /**
     * 拍照是相片保存的临时路径
     */
    private String photoCachePath;

    private File cropFile;
    private Context context;
    private int requestCode;
    private AlertDialog alertDialog;
    private OnUploadPhotoListener listener;


    public UploadPhotoDialog(Context context, OnUploadPhotoListener listener) {
        this.context = context;
        this.listener = listener;
    }


    public void show() {
        show(1, 0);
    }

    public void showForResult(int requestCode) {
        show(requestCode, 0);
    }

    public void showScale(float imageScaleSize) {
        show(1, imageScaleSize);
    }

    /**
     * @param imageScaleSize 想要获取图片的宽高比，必传！传0表示不进行压缩
     */
    public void show(final int requestCode, float imageScaleSize) {
        this.imageWidth = imageScaleSize == 0 ? imageHeight : (int) (imageHeight * imageScaleSize);
        this.requestCode = requestCode;
        View view = View.inflate(context, R.layout.frame_layout_upload_photo_dialog, null);
        View photoLayout = view.findViewById(R.id.photoLayout);
        View albumLayout = view.findViewById(R.id.albumLayout);
        photoLayout.setOnClickListener(this);
        albumLayout.setOnClickListener(this);
        albumLayout.setVisibility(type == PHOTO ? View.GONE : View.VISIBLE);
        photoLayout.setVisibility(type == ALBUM ? View.GONE : View.VISIBLE);
        alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.photoLayout) {
            if (!(listener != null && listener.photoClick(requestCode))) {
                photoCachePath = String.format("%s/%sphoto.jpg", getFileDir(), System.currentTimeMillis());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoCachePath)));

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    ((Activity) context).startActivityForResult(intent, RESULT_CAMERA);
                } else {//修复7.0无法获取
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    ((Activity) context).startActivityForResult(intent, RESULT_CAMERA);
                }
            }
            alertDialog.dismiss();
        } else if (v.getId() == R.id.albumLayout) {
            if (!(listener != null && listener.albumClick(requestCode))) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                ((Activity) context).startActivityForResult(intent, RESULT_ALBUM);
            }
            alertDialog.dismiss();
        }
    }

    // 拍完照片的回调方法
    public void onActivityResult(final int request, int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        if (request == RESULT_CAMERA) {
                            //如果需要压缩
                            if (imageHeight != imageWidth) {
                                File picture2 = new File(photoCachePath);
                                ZoomPhoto(Uri.fromFile(picture2));
                                subscriber.onNext("isCrop");
                            } else {
                                zipImage(subscriber, photoCachePath);
                            }

                        } else if (request == RESULT_ALBUM) {
                            //如果需要压缩
                            if (imageHeight != imageWidth) {
                                ZoomPhoto(data.getData());
                                subscriber.onNext("isCrop");
                            } else {
                                zipImage(subscriber, data.getData());
                            }

                        } else if (request == RESULT_ZOOM_PHOTO) {
                            zipImage(subscriber, cropFile);
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String imageSavedPath) {
                        if (listener == null) {
                            return;
                        }
                        if (TextUtils.isEmpty(imageSavedPath)) {
                            listener.fail(requestCode, null);
                            return;
                        }
                        if (imageSavedPath.equals("isCrop")) {
                            Log.i(TAG, "获取图片完成，调用系统裁剪图片");
                            return;
                        }
                        if (imageSavedPath.equals("onlyReceivedImage")) {
                            listener.onlyReceivedImage(requestCode);
                            return;
                        }

                        listener.success(requestCode, imageSavedPath);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listener != null) {
                            listener.fail(requestCode, e);
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                });

    }

    /**
     * 压缩下载图片
     */
    private void zipImage(Subscriber<? super String> subscriber, Object data) {
        subscriber.onNext("onlyReceivedImage");
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context).load(data).asBitmap().override(imageWidth, imageHeight)
                    .into(imageWidth, imageHeight).get();
            String newFilePath = String.format("%s/saved_%s.jpg", getFileDir(), System.currentTimeMillis());
            if (FrameBitmapUtil.savePicture(newFilePath, bitmap)) {
                subscriber.onNext(newFilePath);
            } else {
                subscriber.onNext(null);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            subscriber.onNext(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
            subscriber.onNext(null);
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;

            }
        }
    }

    /**
     * 调用系统的裁剪
     */
    private void ZoomPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri newUri = Uri.parse("file://" + FrameCropUtils.getPath(context, uri));
        intent.setDataAndType(newUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", imageWidth);//宽高的比例
        intent.putExtra("aspectY", imageHeight);
        intent.putExtra("outputX", imageWidth);//裁剪图片宽高
        intent.putExtra("outputY", imageHeight);
        intent.putExtra("scale", true);
        cropFile = new File(getFileDir(), System.currentTimeMillis() + "crop.jpg");
        CommonUtils.clearFile(cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropFile));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        ((Activity) context).startActivityForResult(intent, RESULT_ZOOM_PHOTO);
    }

    private String getFileDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    /**
     * 只显示拍照选项
     */
    public void onlyShowCamera() {
        type = PHOTO;
    }

    /**
     * 只显示相册选项
     */
    public void onlyShowAlbum() {
        type = ALBUM;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public String getPhotoCachePath() {
        return photoCachePath;
    }

    public void setPhotoCachePath(String photoCachePath) {
        this.photoCachePath = photoCachePath;
    }

    public File getCropFile() {
        return cropFile;
    }

    public void setCropFile(File cropFile) {
        this.cropFile = cropFile;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }

    public void setAlertDialog(AlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }

    public OnUploadPhotoListener getListener() {
        return listener;
    }

    public void setListener(OnUploadPhotoListener listener) {
        this.listener = listener;
    }
}
