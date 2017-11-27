package a26c.com.android_frame_test.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by guilinlin on 2017/11/24 09:15.
 * email 973635949@qq.com
 */

public class XiaolinRefreshLayout extends RelativeLayout {

    private View childView;
    private LayoutParams childLayoutParams;
    private float dragRate = 0.6f;
    private ValueAnimator autoScrollBackanimator;
    private RefreshHeader refreshHeader;
    /**
     * 0表示滑动已完成（刚开始，也属于这个阶段，可以认为是滑动完毕）
     * 1表示正在自动划回去
     * 2表示正在滑回去途中遭遇点击事件
     * 3表示正在显示刷新动画
     */
    private int autoScrollStatus = 0;
    private boolean triggerRefresh = false;
    private int refreshHeight = 500;
    /**
     * 已经滑动的距离
     */
    private float distance;
    /**
     * 当自动回滚过程中，接受到了触摸事件，记录当时的topMargin
     */
    private int oldMarginTop;


    public XiaolinRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initChild();
    }

    private float startY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        initChild();
        if (childView == null) {
            return super.dispatchTouchEvent(event);
        }
        if (checkChildViewIsScoll()) {
            return super.dispatchTouchEvent(event);

        }
        distance = (event.getY() - startY) * dragRate;


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (autoScrollStatus == 1) {//正在滑动过程中
                    autoScrollStatus = 2;
                    oldMarginTop = childLayoutParams.topMargin;
                    return true;

                } else if (autoScrollStatus == 2) {//正在自动滑回去的途中，遭遇滑动事件
                    int i = (int) distance + oldMarginTop;
                    childLayoutParams.topMargin = i < 0 ? 0 : i;
                    childView.requestLayout();
                    return true;
                } else if (autoScrollStatus == 3) {//正在刷新
                    if (autoScrollBacksubscriber != null) {
                        autoScrollBacksubscriber.unsubscribe();
                    }
                    autoScrollStatus = 2;
                    oldMarginTop = childLayoutParams.topMargin;
                    return true;
                } else if (autoScrollStatus == 0) {//手动  向下滑动
                    if (distance >= 0) {//向下滑动
                        refreshHeader.onPullDown(distance, distance >= refreshHeight);
                        childLayoutParams.topMargin = (int) distance;
                        childView.requestLayout();
                        return true;
                    } else {//向上滑动
                        return super.dispatchTouchEvent(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                triggerRefresh = distance >= refreshHeight;
                if (autoScrollBacksubscriber != null) {
                    autoScrollBacksubscriber.unsubscribe();
                }
                autoScrollBack(childLayoutParams.topMargin, triggerRefresh ? refreshHeight : 0, scrollBackDuration, new OnScrollbackCompleteListener() {
                    @Override
                    public void complete() {
                        refreshHeader.onRefreshing();
                        autoScrollStatus = 3;

                        autoScrollBacksubscriber = new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();

                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    autoScrollBack(refreshHeight, 0, scrollBackDuration, null);
                                }
                            }
                        };
                        Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .map(new Func1<Integer, Boolean>() {
                                    @Override
                                    public Boolean call(Integer integer) {
                                        try {
                                            Thread.sleep(refreshHeader.getRemainTime());
                                            return true;
                                        } catch (InterruptedException ignored) {
                                        }
                                        return false;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(autoScrollBacksubscriber);
                    }
                });
                break;

            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    Subscriber<Boolean> autoScrollBacksubscriber;


    /**
     * @param d1 从Y轴距离
     * @param d2 到Y轴距离
     */
    private void autoScrollBack(final float d1, float d2, int duration, final OnScrollbackCompleteListener listener) {
        System.out.println("autoScrollBack");
        autoScrollStatus = 1;
        if (autoScrollBackanimator == null) {
            autoScrollBackanimator = ObjectAnimator.ofFloat(d1, 0).setDuration(0);
            autoScrollBackanimator.setInterpolator(new IosScrollInterpolator());
            autoScrollBackanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (autoScrollStatus == 1) {
                        int animatedValue = (int) (float) animation.getAnimatedValue();
                        childLayoutParams.topMargin = animatedValue <= 0 ? 0 : animatedValue;
                        childView.requestLayout();
                    } else if (autoScrollStatus == 2) {
                        autoScrollBackanimator.cancel();
                        autoScrollStatus = 2;
                    }
                }
            });
            autoScrollBackanimator.addListener(new Animator.AnimatorListener() {
                private boolean isCancel;

                @Override
                public void onAnimationStart(Animator animation) {
                    autoScrollStatus = 1;
                    isCancel = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isCancel && triggerRefresh) {
                        if (listener != null) {
                            listener.complete();
                        }
                        triggerRefresh = false;
                    } else {
                        autoScrollStatus = 0;
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isCancel = true;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        autoScrollBackanimator.setFloatValues(d1, d2);
        autoScrollBackanimator.setDuration(duration);
        autoScrollBackanimator.start();


    }

    private void initChild() {
        if (childView != null) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = this.getChildAt(i);
            if (view instanceof RefreshHeader) {
                refreshHeader = (RefreshHeader) view;
            } else {
                childView = getChildAt(i);
                childLayoutParams = (LayoutParams) childView.getLayoutParams();
            }
        }
    }

    private boolean checkChildViewIsScoll() {
        if (childView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) childView;
            return scrollView.getScrollY() != 0;

        }
        return false;
    }

    private static class IosScrollInterpolator implements Interpolator {
        private float A = 119.770627922267f;
        private float B = -1.02917487229601f;
        private float C = 20.0368252485957f;
        private float D = -0.0233729665813318f;


        @Override
        public float getInterpolation(float x) {
            x = x * 100;
            //四参数方程，模拟ios的阻尼效果
            return (float) ((A - D) / (1 + Math.pow(x / C, B)) + D) / 100;
        }
    }


    private int scrollBackDuration = 300;

    public interface OnScrollbackCompleteListener {
        void complete();
    }

}
