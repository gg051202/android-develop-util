package com.a26c.android.frame.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.TextView;

import com.a26c.android.frame.R;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by guilinlin on 16/8/30 14:09.
 * email 973635949@qq.com
 */
public class DialogFactory {


    public static AlertDialog show(Context context, CharSequence title, CharSequence message,
                                   CharSequence negative, DialogInterface.OnClickListener negativeListener,
                                   CharSequence positive, DialogInterface.OnClickListener positiveListener) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setNegativeButton(negative, negativeListener)
                .setPositiveButton(positive, positiveListener).create();
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog show(Context context, CharSequence title, CharSequence message,
                                   CharSequence positive, DialogInterface.OnClickListener positiveListener) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setPositiveButton(positive, positiveListener).create();
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog show(Context context, CharSequence title,
                                   CharSequence positive, DialogInterface.OnClickListener positiveListener) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
                .setPositiveButton(positive, positiveListener).create();
        alertDialog.show();
        return alertDialog;
    }

    /**
     * @param list map必须有  key和value两组键值对
     */
    public static <T extends ChoiceData> AlertDialog showSingle(Context context, final List<T> list, String selectKey, final OnDialogSelectedListener<T> listener) {
        CharSequence[] scList = new CharSequence[list.size()];
        int select = -1;
        for (int i = 0; i < list.size(); i++) {
            scList[i] = list.get(i).getDesc();
            if (select == -1 && !TextUtils.isEmpty(selectKey) && selectKey.equals(list.get(i).getKey())) {
                select = i;
            }
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(scList, select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        listener.onSelect(list.get(which), null);
                        Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .map(new Func1<Integer, Object>() {
                                    @Override
                                    public Object call(Integer integer) {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Object>() {
                                    @Override
                                    public void call(Object o) {
                                        dialog.dismiss();
                                    }
                                });

                    }
                }).create();
        alertDialog.show();
        return alertDialog;

    }


    /**
     * 多选的dialog
     *
     * @param list map必须有  key和value两组键值对
     */
    public static <T extends ChoiceData> AlertDialog showMulti(Context context, final List<T> list, final OnDialogSelectedListener<T> listener) {
        CharSequence[] valueList = new CharSequence[list.size()];
        boolean[] checkedItems = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            valueList[i] = list.get(i).getDesc();
            checkedItems[i] = list.get(i).isSelected();
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMultiChoiceItems(valueList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        list.get(which).setSelected(isChecked);
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onSelect(null, list);
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
        return alertDialog;

    }


    /**
     * 进度框dialog
     */
    public static AlertDialog getProgressDialog(Context context, boolean iscancel) {

        return new AlertDialog.Builder(context)
                .setView(R.layout.frame_layout_progressbar)
                .setCancelable(iscancel)
                .create();
    }

    public static AlertDialog showProgress(Context context, String msg, boolean iscancel) {
        AlertDialog dialog = getProgressDialog(context, iscancel);
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(R.id.text);
        if (textView != null) {
            textView.setText(msg);
        }
        return dialog;
    }


    public interface OnDialogSelectedListener<T extends ChoiceData> {
        /**
         * 如果是单选key就是一个值，不然是逗号分隔
         */
        void onSelect(T data, List<T> list);
    }

    public interface ChoiceData {

        String getKey();

        CharSequence getDesc();

        boolean isSelected();

        void setSelected(boolean selected);

    }

    public class SimpleChoiceData implements ChoiceData {
        private String key;
        private CharSequence desc;
        private boolean isSelected;

        public SimpleChoiceData(String key, CharSequence desc) {
            this.key = key;
            this.desc = desc;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setDesc(CharSequence desc) {
            this.desc = desc;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public CharSequence getDesc() {
            return desc;
        }

        @Override
        public boolean isSelected() {
            return isSelected;
        }

        @Override
        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

}
