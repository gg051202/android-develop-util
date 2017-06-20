package com.a26c.android.frame.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.a26c.android.frame.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by guilinlin on 2017/6/9 08:44.
 * email 973635949@qq.com
 */

public class VerticalScrollView<T> extends FrameLayout {

    private SparseArray<View> viewList;

    private InflateView<T> inflateView;
    private OnVerticalViewClickListener<T> itemListener;

    private List<T> mList;

    private int currentPosition = 0;

    private Animation inAnimation;
    private Animation outAnimation;

    private Context context;

    /**
     * 滚动的时间间隔，秒为单位
     */
    private int time = 5;

    private Subscriber<Long> subscriber = new Subscriber<Long>() {

        @Override
        public void onStart() {
            show(currentPosition++);
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Long aLong) {
            show(currentPosition++);
        }
    };

    public VerticalScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListener != null) {
                    int p = currentPosition % mList.size();
                    itemListener.itemClick(p, mList.get(p));

                }
            }
        });
    }


    public void start() {
        Observable.interval(time, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void stop() {
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
    }

    public void show(int position) {
        if (viewList != null && position >= 0
                && inflateView != null && mList != null && mList.size() > 0) {

            int p = position % mList.size();
            if (viewList.get(p) == null) {
                viewList.put(p, inflateView.inflate(mList.get(p)));
                addView(viewList.get(p));
            }
            if (p == 0) {
                View view = viewList.get(mList.size() - 1);
                if (view != null) {
                    view.setVisibility(GONE);
                    view.startAnimation(getOutAnimation());
                }
            } else {
                viewList.get(p - 1).setVisibility(GONE);
                viewList.get(p - 1).startAnimation(getOutAnimation());
            }
            viewList.get(p).setVisibility(VISIBLE);
            viewList.get(p).startAnimation(getInAnimation());

        }
    }

    public void setmList(List<T> mList) {
        this.mList = mList;
        this.viewList = new SparseArray<>();
    }

    public void setInflateView(InflateView<T> inflateView) {
        this.inflateView = inflateView;
    }

    public interface InflateView<T> {
        View inflate(T t);
    }

    public interface OnVerticalViewClickListener<T> {
        void itemClick(int position, T data);
    }

    public Animation getInAnimation() {
        if (inAnimation == null) {
            inAnimation = AnimationUtils.loadAnimation(context, R.anim.frame_pop_bottom_in_400);

        }
        return inAnimation;
    }

    public Animation getOutAnimation() {
        if (outAnimation == null) {
            outAnimation = AnimationUtils.loadAnimation(context, R.anim.frame_pop_top_out_400);
        }
        return outAnimation;
    }


    public void setOnVerticalViewClickListener(OnVerticalViewClickListener<T> itemListener) {
        this.itemListener = itemListener;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
