package com.a26c.android.frame.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a26c.android.frame.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by guilinlin on 16/7/29 15:41.
 * email 973635949@qq.com
 */
@SuppressWarnings("all")
public class BaseRecyclerView extends FrameLayout {

    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private FrameLayout noDataRelativeLayout;
    private BaseQuickAdapter adapter;
    private boolean firstLoadData = true;
    /**
     * 没有数据时，时候需要显示 没有数据的视图
     */
    private boolean showNodataView = true;
    private MutiItemDecoration decor;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    private NetworkHandle networkHandle;

    private ViewCreator viewCreator;
    private CharSequence defaultNoDataString = "暂无数据";
    private CharSequence defaultErrString = "请求失败";

    /**
     * 初始值设置这么大表示不需要上拉加载，但是如果外部调用了baseRecyclerView.openLoadMore,将会改变这个值，
     * 每次下拉刷新会重新开启上拉加载，避免出现上拉加载后没有数据后，再下拉刷新反而无法上拉加载的情况
     */
    private int pageSize = 10000;
    /**
     * recyclerView第一次加载时,是否显示dialog,默认显示
     */
    private boolean isRefreshingFirst = true;


    /**
     * 表示分页数据的第几页
     */
    private int pageIndex = 1;
    private View noDataView;
    private View errView;

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.frame_layout_base_recycler_view, this, true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        noDataRelativeLayout = (FrameLayout) findViewById(R.id.noDataRelativeLayout);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);

        //初始化recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        refreshLayout.setColorSchemeColors(0xffff8400);
        decor = new MutiItemDecoration(MutiItemDecoration.Type.ALL);
        recyclerView.addItemDecoration(decor);
    }

    public void init(BaseQuickAdapter baseQuickAdapter, final NetworkHandle networkHandle) {
        this.networkHandle = networkHandle;
        adapter = baseQuickAdapter;

        if (networkHandle != null) networkHandle.init(this);

        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                if (networkHandle != null) networkHandle.loadData(true, "1");
            }
        };
        refreshLayout.setOnRefreshListener(onRefreshListener);


        //初始化baseAdapter
        adapter.openLoadMore(pageSize);//设置这么大表示不需要加载更多
        adapter.openLoadAnimation();
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (networkHandle != null) networkHandle.loadData(false, String.valueOf(pageIndex));
            }
        });

        //初始化无数据的布局
        initViewCreator();

        recyclerView.setAdapter(adapter);
        //baseRecyclerView刚显示时是否需要显示下拉刷新的dialog
        refreshLayout.setRefreshing(isRefreshingFirst);

        //如果需要第一次加载baseRecyclerView就加载数据,设置firstLoadData为true,默认为true
        if (firstLoadData) {
            if (networkHandle != null) networkHandle.loadData(true, "1");
        }

    }

    /**
     * 数据加载完成之后的统一操作,关闭下拉刷新,关闭上拉加载的进度条,显示空数据的界面等等
     *
     * @param data 网络请求到的数据
     */
    public void onLoadDataComplete(List data) {
        if (pageIndex == 1) {//获取到数据后，如果当前页数是1，肯定是下拉刷新，所以清空数据
            adapter.getData().clear();
            adapter.openLoadMore(pageSize);
        }
        if (data.size() < pageSize) {//如果上拉加载没有获取到数据设置上拉加载已完成
            adapter.loadComplete();
            if (pageIndex != 1) {
                Toast.makeText(context, "已到最后", Toast.LENGTH_LONG).show();
            }
        }
        adapter.addData(data);
        onLoadDataComplete();
    }

    public void onLoadDataComplete() {
        //数据加载成功pageIndex+1
        pageIndex++;
        adapter.notifyDataSetChanged();

        refreshLayout.setRefreshing(false);
        //如果listview没有数据,显示无数据的提示,否则隐藏
        if (adapter.getItemCount() - adapter.getHeaderLayoutCount() - adapter.getFooterLayoutCount() == 0) {
            noDataView.setVisibility(showNodataView ? VISIBLE : INVISIBLE);
            errView.setVisibility(INVISIBLE);
        } else {
            noDataView.setVisibility(INVISIBLE);
            errView.setVisibility(INVISIBLE);
        }
    }

    private void initViewCreator() {
        if (viewCreator == null) {
            viewCreator = new ViewCreator() {
                @Override
                public View getNoDataView() {
                    View view = LayoutInflater.from(context).inflate(R.layout.frame_layout_network_nodata, null);
                    return view;
                }

                @Override
                public View getErrDataView() {
                    View view = LayoutInflater.from(context).inflate(R.layout.frame_layout_network_err, null);
                    return view;
                }
            };
        }
        if (viewCreator.getNoDataView() == null) {
            noDataView = LayoutInflater.from(context).inflate(R.layout.frame_layout_network_nodata, null);
        } else {
            noDataView = viewCreator.getNoDataView();
        }
        if (viewCreator.getErrDataView() == null) {
            errView = LayoutInflater.from(context).inflate(R.layout.frame_layout_network_err, null);
        } else {
            errView = viewCreator.getErrDataView();
        }


        noDataView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        noDataRelativeLayout.addView(noDataView);

        errView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        noDataRelativeLayout.addView(errView);

        showNoDataView(defaultNoDataString);
        showErrView(defaultErrString);
    }

    /**
     * 修改默认的无数据的视图,前提是数据长度是0
     */
    public void showNoDataView(CharSequence text) {
        if (adapter.getItemCount() - adapter.getHeaderLayoutCount() - adapter.getFooterLayoutCount() == 0) {
            if (noDataView instanceof TextView) {
                ((TextView) noDataView).setText(text);
            }
            noDataView.setVisibility(VISIBLE);
            errView.setVisibility(INVISIBLE);
        }
    }

    /**
     * 修改默认的显示网络异常的视图,前提是数据长度是0
     */
    public void showErrView(CharSequence text) {
        if (adapter.getItemCount() - adapter.getHeaderLayoutCount() - adapter.getFooterLayoutCount() == 0) {
            if (errView instanceof TextView) {
                ((TextView) errView).setText(text);
            }
            noDataView.setVisibility(INVISIBLE);
            errView.setVisibility(VISIBLE);
        }
    }


    public interface NetworkHandle {
        /**
         * 可以在此初始化的有:<p>
         * 1.是否需要开启上拉加载:baseRecyclerView.openLoadMore(20);<p>
         * 2.baseRecyclerView刚显示时是否需要显示下拉刷新的dialog baseRecyclerView.setRefreshingFirst(true),默认为true<p>
         * 3.第一次加载baseRecyclerView就加载数据,baseRecyclerView.setFirstLoadData(true),默认为true<p>
         * 4.设置空数据布局：baseRecyclerView.setNodataLayoutId(R.layout.layout_empty)<p>
         * 4.设置网络请求错误布局：baseRecyclerView.setErrLayoutId(R.layout.layout_err)
         */
        void init(BaseRecyclerView baseRecyclerView);

        /**
         * @param isRefresh true表示下拉刷新
         * @param pageIndex 加载的页数
         */
        void loadData(boolean isRefresh, String pageIndex);
    }


    public interface ViewCreator {
        /**
         * 返回没有数据的视图
         */
        View getNoDataView();

        /**
         * 返回请求失败的视图
         */
        View getErrDataView();
    }

    public void openLoadMore(int pageSize) {
        this.pageSize = pageSize;
        adapter.openLoadMore(pageSize);
    }

    public void removeDivider() {
        recyclerView.removeItemDecoration(decor);
    }

    public void addOnItemTouchListener(OnItemClickListener listener) {
        recyclerView.addOnItemTouchListener(listener);
    }

    /**
     * 点击按钮触发下拉刷新事件
     */
    public void callRefreshListener() {
        pageIndex = 1;
        if (networkHandle != null) {
            networkHandle.loadData(true, "1");
        }
        refreshLayout.setRefreshing(true);
    }

    public void addData(List data) {
        adapter.addData(data);
    }

    public void setViewCreator(ViewCreator viewCreator) {
        this.viewCreator = viewCreator;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public void setRefreshLayout(SwipeRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    public FrameLayout getNoDataRelativeLayout() {
        return noDataRelativeLayout;
    }

    public void setNoDataRelativeLayout(FrameLayout noDataRelativeLayout) {
        this.noDataRelativeLayout = noDataRelativeLayout;
    }

    public BaseQuickAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        this.adapter = adapter;
    }

    public boolean isFirstLoadData() {
        return firstLoadData;
    }

    public void setFirstLoadData(boolean firstLoadData) {
        this.firstLoadData = firstLoadData;
    }

    public boolean isShowNodataView() {
        return showNodataView;
    }

    public void setShowNodataView(boolean showNodataView) {
        this.showNodataView = showNodataView;
    }

    public MutiItemDecoration getDecor() {
        return decor;
    }

    public void setDecor(MutiItemDecoration decor) {
        this.decor = decor;
    }

    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return onRefreshListener;
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public NetworkHandle getNetworkHandle() {
        return networkHandle;
    }

    public void setNetworkHandle(NetworkHandle networkHandle) {
        this.networkHandle = networkHandle;
    }

    public ViewCreator getViewCreator() {
        return viewCreator;
    }

    public CharSequence getDefaultNoDataString() {
        return defaultNoDataString;
    }

    public void setDefaultNoDataString(CharSequence defaultNoDataString) {
        this.defaultNoDataString = defaultNoDataString;
    }

    public CharSequence getDefaultErrString() {
        return defaultErrString;
    }

    public void setDefaultErrString(CharSequence defaultErrString) {
        this.defaultErrString = defaultErrString;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isRefreshingFirst() {
        return isRefreshingFirst;
    }

    public void setRefreshingFirst(boolean refreshingFirst) {
        isRefreshingFirst = refreshingFirst;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}
