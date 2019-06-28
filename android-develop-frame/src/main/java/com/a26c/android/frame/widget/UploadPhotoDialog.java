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
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.a26c.android.frame.R;
import com.a26c.android.frame.util.AndroidScheduler;
import com.a26c.android.frame.util.BitmapUtil;
import com.a26c.android.frame.util.CommonUtils;
import com.a26c.android.frame.util.FrameCropUtils;
import com.a26c.android.frame.util.SelectVideoUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Subscriber;
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
    public static final String SELECT_IMAGE = "image/*";
    public static final String SELECT_IMAGE_AND_VIDEO = "image/*,video/*";
    public static final String SELECT_VIDEO = "video/*";

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
    /**
     * 选择图片的类型，可以设置为：图片，视频，图片和视频
     */
    private String selectMediaType = SELECT_IMAGE;

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
        alertDialog = new AlertDialog.Builder(context, R.style.FrameDefaultDialogStyle)
                .setView(view)
                .create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.photoLayout) {
            if (!(listener != null && listener.photoClick(requestCode))) {
                photoCachePath = String.format("%s/saved_%sphoto.jpg", getFileDir(), System.currentTimeMillis());
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
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selectMediaType);
                ((Activity) context).startActivityForResult(intent, RESULT_ALBUM);
            }
            alertDialog.dismiss();
        }
    }

    // 拍完照片的回调方法
    public void onActivityResult(final int request, final int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        Observable
                .create(new Observable.OnSubscribe<ResultData>() {
                    @Override
                    public void call(Subscriber<? super ResultData> subscriber) {
                        if (request == RESULT_CAMERA) {
                            //如果需要压缩
                            if (imageHeight != imageWidth) {
                                File picture2 = new File(photoCachePath);
                                zoomPhoto(Uri.fromFile(picture2));
                            } else {
                                zipImage(subscriber, photoCachePath);
                            }

                        } else if (request == RESULT_ALBUM) {
                            boolean isVideo = isVideo(data.getDataString());
                            if (isVideo) {
                                ResultData t = new ResultData(true, ResultData.TYPE_SUCCESS);
                                t.setPath(SelectVideoUtil.getPath(context, data.getData()));
                                subscriber.onNext(t);
                            } else {
                                //如果需要压缩，只有图片能压缩
                                if (imageHeight != imageWidth) {
                                    zoomPhoto(data.getData());
                                } else {
                                    zipImage(subscriber, data.getData());
                                }
                            }
                        } else if (request == RESULT_ZOOM_PHOTO) {
                            zipImage(subscriber, cropFile);
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidScheduler.mainThread())
                .subscribe(new Subscriber<ResultData>() {
                    @Override
                    public void onNext(ResultData resultData) {
                        switch (resultData.getType()) {
                            case ResultData.TYPE_RECEIVED_IMAGE:
                                if (listener != null) {
                                    listener.onlyReceivedImage(requestCode);
                                }
                                break;
                            case ResultData.TYPE_SUCCESS:
                                if (listener != null) {
                                    if (TextUtils.isEmpty(resultData.getPath())) {
                                        listener.fail(requestCode, new RuntimeException("获取数据为空"));
                                    } else {
                                        listener.success(requestCode, resultData.isVideo, resultData.getPath());
                                    }
                                }
                                break;
                            default:
                                break;
                        }

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
    private void zipImage(Subscriber<? super ResultData> subscriber, Object data) {
        subscriber.onNext(new ResultData(false, ResultData.TYPE_RECEIVED_IMAGE));
        Bitmap bitmap;
        try {
            RequestOptions requestOptions = new RequestOptions().override(imageWidth, imageHeight);
            bitmap = Glide.with(context).asBitmap().load(data).apply(requestOptions)
                    .into(imageWidth, imageHeight).get();
            String newFilePath = String.format("%s/saved_%s.jpg", getFileDir(), System.currentTimeMillis());
            if (BitmapUtil.savePicture(newFilePath, bitmap)) {
                ResultData resultData = new ResultData(false, ResultData.TYPE_SUCCESS);
                resultData.setPath(newFilePath);
                subscriber.onNext(resultData);
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
        }
    }

    /**
     * 调用系统的裁剪
     */
    private void zoomPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri newUri = Uri.parse("file://" + FrameCropUtils.getPath(context, uri));
        intent.setDataAndType(newUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", imageWidth);//宽高的比例
        intent.putExtra("aspectY", imageHeight);
        intent.putExtra("outputX", imageWidth);//裁剪图片宽高
        intent.putExtra("outputY", imageHeight);
        intent.putExtra("scale", true);
        cropFile = new File(getFileDir(), "saved_" + System.currentTimeMillis() + ".jpg");
        CommonUtils.clearFile(cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropFile));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        ((Activity) context).startActivityForResult(intent, RESULT_ZOOM_PHOTO);
    }

    private static String getFileDir() {
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

    public String getSelectMediaType() {
        return selectMediaType;
    }

    public void setSelectMediaType(String selectMediaType) {
        this.selectMediaType = selectMediaType;
    }

    public static void deleteCacheFiles() {
        File dir = new File(getFileDir());
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((dir1, name) -> name.startsWith("saved_") && (name.endsWith(".jpg") || name.endsWith(".png")));
            for (File file : files) {
                Log.i("delete file", file.getName() + ",result:" + file.delete());
            }
        }
    }

    private boolean isVideo(String path) {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(path);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        return (mimeType != null && mimeType.contains("video"));
    }

    private static class ResultData {

        static final String TYPE_RECEIVED_IMAGE = "type_received_image";
        static final String TYPE_SUCCESS = "type_success";
        private String path;

        private String type;
        private boolean isVideo;

        public ResultData(boolean isVideo, String type) {
            this.type = type;
            this.isVideo = isVideo;
        }


        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

    }
}
