package a26c.com.android_frame_test;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;


/**
 * Created by guilinlin on 2017/1/5 10:34.
 * email 973635949@qq.com
 */
public class MyApplication extends Application {

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                ClassicsHeader.REFRESH_HEADER_PULLING = "下拉以刷新";
                ClassicsHeader.REFRESH_HEADER_REFRESHING = "正在刷新";
                ClassicsHeader.REFRESH_HEADER_LOADING = "正在加载";
                ClassicsHeader.REFRESH_HEADER_RELEASE = "释放以刷新";
                ClassicsHeader.REFRESH_HEADER_FINISH = "";
                ClassicsHeader.REFRESH_HEADER_FAILED = "";
                ClassicsHeader classicsHeader = new ClassicsHeader(context);
                classicsHeader.setBackgroundColor(0xffffffff);
                classicsHeader.setEnableLastTime(false);
                classicsHeader.setFinishDuration(1);
                classicsHeader.setTextSizeTitle(13);
                classicsHeader.setDrawableSize(13);
                classicsHeader.setDrawableMarginRight(10);
                return classicsHeader;
            }
        });

        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                ClassicsFooter.REFRESH_FOOTER_PULLING = "上拉加载更多";
                ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放已加载";
                ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新";
                ClassicsFooter.REFRESH_FOOTER_LOADING = "正在加载";
                ClassicsFooter.REFRESH_FOOTER_FINISH = "";
                ClassicsFooter.REFRESH_FOOTER_FAILED = "加载失败";
                ClassicsFooter.REFRESH_FOOTER_NOTHING = "没有更多数据了";
                ClassicsFooter classicsFooter = new ClassicsFooter(context);
                classicsFooter.setBackgroundColor(0xffffffff);
                classicsFooter.setFinishDuration(1);
                classicsFooter.setTextSizeTitle(13);
                classicsFooter.setDrawableSize(13);
                classicsFooter.setDrawableMarginRight(10);
                return classicsFooter;
            }
        });

    }

}
