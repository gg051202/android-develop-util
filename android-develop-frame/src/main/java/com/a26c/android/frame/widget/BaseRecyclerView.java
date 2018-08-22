package com.a26c.android.frame.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.a26c.android.frame.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

/**
 * Created by guilinlin on 16/7/29 15:41.
 * email 973635949@qq.com
 */
@SuppressWarnings("all")
public class BaseRecyclerView extends FrameLayout {

    /**
     * 默认的分页大小，一个APP可以看需要，初始化一次
     */
    public static int DEFAULT_PAGE_SIZE = 10;
    private final Context mContext;

    private BaseQuickAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private MutiItemDecoration mMutiItemDecoration;
    private NetworkHandle mNetworkHandle;
    private ViewCreator mViewCreator;
    private View mNoDataView;
    private View mErrView;
    /**
     * 默认的占位视图，比如没有数据显示"无数据"，请求失败显示"请求失败"
     */
    private TextView mDefaultHintTextView;

    /**
     * 没有数据时，时候需要显示 没有数据的视图
     */
    private boolean mNeedShowNodataView = true;
    /**
     * PageSize用于判断，获取到数据之后，判断是否需要开上拉加载
     */
    private int mPageSize;
    /**
     * 是否需要立即加载数据，如果设置为false，表示init方法中不会自动加载数据
     */
    private boolean mNeedLoadDataAtOnce = true;
    /**
     * 表示分页数据的第几页
     */
    private int mPageIndex = 1;
    /**
     * 标记当前操作，暂时只用来判断，请求数据失败后，是否需要将pageIndex-1
     */
    private boolean mCurrentIsRefresh;

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.frame_layout_base_recycler_view, this, true);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mPageSize = DEFAULT_PAGE_SIZE;

        //初始化recyclerView
        mMutiItemDecoration = new MutiItemDecoration(MutiItemDecoration.Type.ALL);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(mMutiItemDecoration);

        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        mRefreshLayout.setOnRefreshLoadMoreListener(mOnRefreshLoadmoreListener);
        mRefreshLayout.setNestedScrollingEnabled(true);
        mRefreshLayout.setEnableAutoLoadMore(true);
    }

    public void init(BaseQuickAdapter baseQuickAdapter, NetworkHandle networkHandle) {
        this.mNetworkHandle = networkHandle;
        this.mAdapter = baseQuickAdapter;

        if (mNetworkHandle != null) {
            mNetworkHandle.init(this);
        }

        mAdapter.setEnableLoadMore(false);
        mRecyclerView.setAdapter(mAdapter);

        if (mNeedLoadDataAtOnce) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.frame_layout_baserecycler_default_loading_view, null);
            view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mAdapter.setEmptyView(view);
            loadData();
        }

    }

    private OnRefreshLoadMoreListener mOnRefreshLoadmoreListener = new OnRefreshLoadMoreListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            loadData();
        }

        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            mCurrentIsRefresh = false;
            if (mNetworkHandle != null) {
                mNetworkHandle.loadData(mCurrentIsRefresh, String.valueOf(mPageIndex + 1));
            }
        }
    };

    /**
     * 第一次加载数据
     */
    private void loadData() {
        mCurrentIsRefresh = true;
        mAdapter.getData().clear();
        if (mNetworkHandle != null) {
            mNetworkHandle.loadData(mCurrentIsRefresh, "1");
        }
    }

    /**
     * 数据加载完成之后的统一操作,关闭下拉刷新,关闭上拉加载的进度条,显示空数据的界面等等
     *
     * @param data 网络请求到的数据
     */
    public void onLoadDataComplete(List data, CharSequence hint) {
        onLoadDataComplete(hint);
        //获取到数据后，如果当前页数是1，肯定是下拉刷新，所以清空数据
        if (mCurrentIsRefresh) {
            mAdapter.getData().clear();
        }
        mAdapter.addData(data);

        if (mCurrentIsRefresh) {
            mPageIndex = 1;
        } else {
            mPageIndex++;
        }

        if (data.size() >= mPageSize) {
            mRefreshLayout.setEnableLoadMore(true);
        } else {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mRefreshLayout != null) {
                        mRefreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
            }, 500);

        }
    }

    /**
     * 无数据显示的数据
     */
    public void onLoadDataComplete(@NonNull CharSequence noDataText) {
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();

        if (mAdapter.getData().size() > 0 && mCurrentIsRefresh) {
            post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(0);
                }
            });
        }

        if (mAdapter.getData().size() <= 0) {
            showNoDataView(noDataText);
        }
    }


    public void onLoadDataCompleteErr(CharSequence errText) {
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);

        showErrView(errText);
    }

    public void onLoadDataComplete(List data) {
        onLoadDataComplete(data, "暂无数据");
    }

    public void onLoadDataComplete() {
        onLoadDataComplete("暂无数据");
    }

    public void onLoadDataCompleteErr() {
        onLoadDataCompleteErr("请求失败");
    }

    /**
     * 显示没有数据的视图
     */
    private void showNoDataView(CharSequence hint) {
        if (!mNeedShowNodataView) {
            return;
        }
        initViewCreator();
        if (mNoDataView == null) {
            showDefaultHintTextView(hint);
        } else {
            mAdapter.setEmptyView(mNoDataView);
        }
    }

    /**
     * 显示网络加载错误的视图
     */
    private void showErrView(CharSequence hint) {
        if (!mNeedShowNodataView) {
            return;
        }
        initViewCreator();
        if (mErrView == null) {
            showDefaultHintTextView(hint);
        } else {
            mAdapter.setEmptyView(mErrView);
        }

    }


    /**
     * 修改默认的无数据的视图,前提是数据长度是0
     */
    private void showDefaultHintTextView(CharSequence hint) {
        if (!mNeedShowNodataView) {
            return;
        }
        mDefaultHintTextView.setText(hint);
        mAdapter.setEmptyView(mDefaultHintTextView);
    }

    /**
     * 初始化空数据和无数据视图
     */
    private void initViewCreator() {
        if (mViewCreator != null && mViewCreator.getNoDataView() != null) {
            mNoDataView = mViewCreator.getNoDataView();
            mNoDataView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (mViewCreator != null && mViewCreator.getErrDataView() != null) {
            mErrView = mViewCreator.getErrDataView();
            mErrView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        //如果在没有设置两种视图，那么就创建一个默认的 view，用来显示错误信息
        if (mErrView == null || mNoDataView == null) {
            mDefaultHintTextView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.frame_layout_network_nodata, null);
            mDefaultHintTextView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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

    public void removeDivider() {
        mRecyclerView.removeItemDecoration(mMutiItemDecoration);
    }

    public void addOnItemTouchListener(OnItemClickListener listener) {
        mRecyclerView.addOnItemTouchListener(listener);
    }

    public void callRefreshListener() {
        mAdapter.getData().clear();
        mPageIndex = 1;
        mRefreshLayout.autoRefresh(0, 200, 1);
    }

    public BaseQuickAdapter getAdapter() {
        return mAdapter;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public SmartRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    public void setRefreshLayout(SmartRefreshLayout refreshLayout) {
        mRefreshLayout = refreshLayout;
    }

    public MutiItemDecoration getMutiItemDecoration() {
        return mMutiItemDecoration;
    }

    public void setMutiItemDecoration(MutiItemDecoration mutiItemDecoration) {
        mMutiItemDecoration = mutiItemDecoration;
    }

    public NetworkHandle getNetworkHandle() {
        return mNetworkHandle;
    }

    public void setNetworkHandle(NetworkHandle networkHandle) {
        mNetworkHandle = networkHandle;
    }

    public ViewCreator getViewCreator() {
        return mViewCreator;
    }

    public void setViewCreator(ViewCreator viewCreator) {
        mViewCreator = viewCreator;
    }

    public View getNoDataView() {
        return mNoDataView;
    }

    public void setNoDataView(View noDataView) {
        mNoDataView = noDataView;
    }

    public View getErrView() {
        return mErrView;
    }

    public void setErrView(View errView) {
        mErrView = errView;
    }

    public boolean isNeedShowNodataView() {
        return mNeedShowNodataView;
    }

    public void setNeedShowNodataView(boolean needShowNodataView) {
        mNeedShowNodataView = needShowNodataView;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    public boolean isNeedLoadDataAtOnce() {
        return mNeedLoadDataAtOnce;
    }

    public void setNeedLoadDataAtOnce(boolean needLoadDataAtOnce) {
        mNeedLoadDataAtOnce = needLoadDataAtOnce;
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public OnRefreshLoadMoreListener getOnRefreshLoadmoreListener() {
        return mOnRefreshLoadmoreListener;
    }

    public void setOnRefreshLoadmoreListener(OnRefreshLoadMoreListener onRefreshLoadmoreListener) {
        mOnRefreshLoadmoreListener = onRefreshLoadmoreListener;
    }

    public TextView getDefaultHintTextView() {
        return mDefaultHintTextView;
    }

    public void setDefaultHintTextView(TextView defaultHintTextView) {
        this.mDefaultHintTextView = defaultHintTextView;
    }
}
