package a26c.com.android_frame_test.model;

import com.a26c.android.frame.util.AndroidScheduler;

import java.util.ArrayList;
import java.util.List;

import a26c.com.android_frame_test.adapter.TestAdapterData;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by guilinlin on 2017/1/4 17:22.
 * email 973635949@qq.com
 */
public class TestModel {

    public void getTestList(final int pageIndex, final OnGetDataListener onGetDataListener) {

        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Integer, List<TestAdapterData>>() {
                    @Override
                    public List<TestAdapterData> call(Integer integer) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        List<TestAdapterData> list = new ArrayList<>();
                        for (int i = 0; i < (pageIndex < 3 ? 20 : 10); i++) {
                            list.add(new TestAdapterData(pageIndex + "页，" + i));
                        }
                        return list;
                    }
                })
                .observeOn(AndroidScheduler.mainThread())
                .subscribe(new Subscriber<List<TestAdapterData>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<TestAdapterData> list) {
                        if (onGetDataListener != null) {
                            onGetDataListener.success(list);
                        }
                    }
                });
    }

    public interface OnGetDataListener {
        void success(List<TestAdapterData> testRecylerData);
    }


}
