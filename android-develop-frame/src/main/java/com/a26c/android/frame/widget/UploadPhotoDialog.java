package com.a26c.android.frame.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.a26c.android.frame.R;
import com.a26c.android.frame.util.FrameBitmapUtil;
import com.a26c.android.frame.util.FrameCropUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * 封装一个获取图片的工具类，
 * showDialog（）方法调用后弹出选择相机或者相册的dialog，
 * onActivityResult（）方法装饰了相关操作，可以直接在对应Activity的OnActivityResult的方法里调用，
 *
 * @author gl
 */
public class UploadPhotoDialog {

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
     * 想要获取图片的大小
     */
    private Integer imageSize = 100;
    /**
     * 裁剪出来的图片的高
     */
    private int HEIGHT = 500;
    /**
     * 图片的高
     */
    public int WIDTH;

    private AlertDialog dialog;
    private DialogListener listener;
    private Context context;
    /**
     * 这个值等于0表示获取到的图片无需压缩
     */
    private float radio = 0;

    /**
     * 压缩图片后保存至此file
     */
    private File outFile;

    /**
     * 拍照是相片保存的临时路径
     */
    private String photoCachePath;

    /**
     * @param context
     * @param ratio_  想要获取图片的宽高比，必传！传0表示不进行压缩
     * @param l       点击按钮的监听 ，可以设为空，那么就默认打开相机和相册的操作
     */
    public UploadPhotoDialog(Context context, float ratio_, DialogListener l) {
        this.context = context;
        radio = ratio_;
        listener = l;
        WIDTH = (int) (HEIGHT * ratio_);
        createFile();
    }

    /**
     * 对外暴露一个显示dialog的方法
     */
    public void showDialog() {
        View view = View.inflate(context, R.layout.frame_layout_upload_photo_dialog, null);
        View photoLayout = view.findViewById(R.id.photoLayout);
        View albumLayout = view.findViewById(R.id.albumLayout);
        photoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(listener != null && listener.photoClick())) {
                    photoCachePath = String.format("%s/temp_%s.jpg", Environment.getExternalStorageDirectory(), System.currentTimeMillis());
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoCachePath)));

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        ((Activity) context).startActivityForResult(intent, RESULT_CAMERA);
                    } else {//修复7.0无法更新
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        ((Activity) context).startActivityForResult(intent, RESULT_CAMERA);
                    }

                }
                dialog.dismiss();
            }
        });
        albumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(listener != null && listener.albumClick())) {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    ((Activity) context).startActivityForResult(intent, RESULT_ALBUM);
                }
                dialog.dismiss();
            }
        });
        if (type == PHOTO) {
            albumLayout.setVisibility(View.GONE);
        }
        if (type == ALBUM) {
            photoLayout.setVisibility(View.GONE);
        }
        dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        dialog.show();

    }


    public interface DialogListener {
        boolean photoClick();

        boolean albumClick();

    }

    /**
     * 压缩图片后获取图片成功的接口
     */
    public interface OnGetImageSuccessListener {

        /**
         * 从相册或者相机刚刚获取到图片时
         */
        void onlyReceivedImage();

        /**
         * @param bitmap    拍照或者从图库拿到并且压缩过后的bitmap对象
         * @param imagePath 图像被保存到本地的地址
         */
        void success(Bitmap bitmap, String imagePath);

        void fail(Throwable e);
    }


    /**
     * dialog消失时的操作
     */
    public void setCancelListener(OnCancelListener l) {
        dialog.setOnCancelListener(l);
    }


    // 拍完照片的回调方法
    public void onActivityResult(int requestCode, int resultCode, final Intent data, final OnGetImageSuccessListener l) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case RESULT_CAMERA:
                if (l != null && radio == 0) l.onlyReceivedImage();
                Observable.just(1)
                        .map(new Func1<Integer, HashMap<String, Object>>() {
                            @Override
                            public HashMap<String, Object> call(Integer integer) {
                                //如果需要压缩
                                if (radio != 0) {
                                    File picture2 = new File(photoCachePath);
                                    ZoomPhoto(Uri.fromFile(picture2));
                                    return new HashMap<>();
                                } else {
                                    Bitmap bitmap = BitmapFactory.decodeFile(photoCachePath);
                                    if (bitmap != null) {
                                        return saveBitmap(bitmap);
                                    } else {
                                        return null;
                                    }
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(getSubscriber(l));


                break;

            case RESULT_ALBUM:
                if (l != null && radio == 0) l.onlyReceivedImage();
                Observable.just(1)
                        .map(new Func1<Integer, HashMap<String, Object>>() {
                            @Override
                            public HashMap<String, Object> call(Integer integer) {
                                if (radio != 0) {
                                    ZoomPhoto(data.getData());
                                    return new HashMap<>();
                                } else {
                                    Bitmap bitmap = FrameBitmapUtil.getBitmapFromUri(context, data.getData());
                                    if (bitmap != null) {
                                        HashMap<String, Object> result = saveBitmap(bitmap);
                                        result.put("code", 1);
                                        return result;
                                    } else {
                                        return null;
                                    }
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(getSubscriber(l));
                break;

            // 压缩并保存图片
            case RESULT_ZOOM_PHOTO:
                if (l != null) l.onlyReceivedImage();
                Observable.just(1)
                        .map(new Func1<Integer, HashMap<String, Object>>() {
                            @Override
                            public HashMap<String, Object> call(Integer integer) {
                                Bitmap bitmap = BitmapFactory.decodeFile(outFile.getAbsolutePath());
                                if (bitmap != null) {
                                    HashMap<String, Object> result = saveBitmap(bitmap);
                                    result.put("code", 1);
                                    return result;
                                } else {
                                    return null;
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(getSubscriber(l));

                break;
            default:
                break;
        }

    }

    @NonNull
    private Subscriber<HashMap<String, Object>> getSubscriber(final OnGetImageSuccessListener l) {
        return new Subscriber<HashMap<String, Object>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (l != null) l.fail(e);
            }

            @Override
            public void onNext(HashMap<String, Object> map) {
                if (l != null) {
                    if (map != null) {
                        if (map.get("code") != null) {//不等于空，有可能是压缩图片，返回一个new
                            l.success((Bitmap) map.get("bitmap"), (String) map.get("filePath"));
                        }
                    } else {
                        l.fail(new Throwable("获取图片为空"));
                    }
                }
            }
        };
    }

    @NonNull
    public HashMap<String, Object> saveBitmap(Bitmap bitmap) {
        String fileName = System.currentTimeMillis() + "life.jpg";
        String filePath = FrameBitmapUtil.savePicture(context, fileName, bitmap, imageSize);
        HashMap<String, Object> map = new HashMap<>();
        map.put("bitmap", bitmap);
        map.put("filePath", filePath);
        return map;
    }

    // 调用系统压缩图片的工具类
    private void ZoomPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");// 调用Android系统自带的一个图片剪裁页面,
        Uri newUri = Uri.parse("file://" + FrameCropUtils.getPath(context, uri));
        intent.setDataAndType(newUri, "image/*");
        intent.putExtra("crop", "true");// 进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", WIDTH);
        intent.putExtra("aspectY", HEIGHT);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", WIDTH);
        intent.putExtra("outputY", HEIGHT);
        intent.putExtra("scale", true);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        ((Activity) context).startActivityForResult(intent, RESULT_ZOOM_PHOTO);
    }

    private void createFile() {
        outFile = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/", "android_frame_scrop.jpg");
        if (!outFile.exists()) {
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置保存之后的压缩的图片的大小
     */
    public void setImageSize(int image_size) {
        this.imageSize = image_size;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhotoCachePath() {
        return photoCachePath;
    }

    public int getType() {
        return type;
    }

    public Integer getImageSize() {
        return imageSize;
    }

    public void setImageSize(Integer imageSize) {
        this.imageSize = imageSize;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

    public DialogListener getListener() {
        return listener;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public void setPhotoCachePath(String photoCachePath) {
        this.photoCachePath = photoCachePath;
    }
}
