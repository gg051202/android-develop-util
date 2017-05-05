package com.a26c.android.frame.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

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
    public static AlertDialog showSingle(Context context, final List<ChoiceData> list, String selectKey, final OnDialogSelectedListener listener) {
        CharSequence[] scList = new CharSequence[list.size()];
        int select = -1;
        for (int i = 0; i < list.size(); i++) {
            scList[i] = list.get(i).value;
            if (select == -1 && !TextUtils.isEmpty(selectKey) && selectKey.equals(list.get(i).key)) {
                select = i;
            }
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(scList, select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        listener.onSelect(list.get(which).key.toString());
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
    public static AlertDialog showMulti(Context context, final List<ChoiceData> list, final OnDialogSelectedListener listener) {
        CharSequence[] valueList = new CharSequence[list.size()];
        boolean[] checkedItems = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            valueList[i] = list.get(i).value;
            checkedItems[i] = list.get(i).isSelected;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMultiChoiceItems(valueList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        list.get(which).isSelected = isChecked;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onSelect("");
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
        return alertDialog;

    }


    public interface OnDialogSelectedListener {
        /**
         * 如果是单选key就是一个值，不然是逗号分隔
         */
        void onSelect(String key);
    }

    public static class ChoiceData {
        public CharSequence key;
        public CharSequence value;
        public boolean isSelected;

        public ChoiceData(CharSequence key, CharSequence value) {
            this.key = key;
            this.value = value;
        }
    }

}
