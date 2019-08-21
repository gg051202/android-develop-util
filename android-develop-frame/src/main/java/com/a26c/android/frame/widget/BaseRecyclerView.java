package com.a26c.android.frame.widget;

import android.content.Context;
import android.os.Build;
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
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

/**
 * Created by guilinlin on 16/7/29 15:41.
 * email 973635949@qq.com
 */
public class BaseRecyclerView<T> extends FrameLayout {

    /**
     * 无操作状态
     */
    public static final int STATUS_NONE = 101;
    /**
     * 第一次进来加载数据
     */
    public static final int STATUS_FIRST_LOAD_DATA = 102;
    /**
     * 下拉刷新中
     */
    public static final int STATUS_REFRESHING = 103;
    /**
     * 正在上拉加载
     */
    public static final int STATUS_LOADMORE = 104;


    /**
     * 默认的分页大小，一个APP可以看需要，初始化一次
     */
    public static int DEFAULT_PAGE_SIZE = 10;
    /**
     * 是否需要显示"没有更多"的footer
     */
    private boolean mNeedShowNoMoreFooter = true;
    private final Context mContext;

    private BaseQuickAdapter<T, BaseViewHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private FrameLayout mContentFrameLayout;
    private SmartRefreshLayout mRefreshLayout;
    private MutiItemDecoration mMutiItemDecoration;
    private NetworkHandle mNetworkHandle;
    private ViewCreator mViewCreator;
    protected static BaseRecyclerViewPlaceholderCreater placeholderCreater = null;
    private View mNoDataView;
    private View mErrView;
    private String mNodataString = "暂无数据";
    private String mErrString = "请求失败";

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
     * 当前的状态
     */
    private int mStatus;
    private View mProgressView;
    private final LinearLayoutManager mLayoutManager;
    private View mNoMoreFooterView;

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.frame_layout_base_recycler_view, this, true);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mContentFrameLayout = findViewById(R.id.contentFrameLayout);
        mPageSize = DEFAULT_PAGE_SIZE;

        //初始化recyclerView
        mMutiItemDecoration = new MutiItemDecoration(MutiItemDecoration.Type.ALL);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mMutiItemDecoration);

        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        mRefreshLayout.setOnRefreshLoadMoreListener(mOnRefreshLoadmoreListener);
        mRefreshLayout.setNestedScrollingEnabled(true);
    }

    public void init(BaseQuickAdapter<T, BaseViewHolder> baseQuickAdapter, NetworkHandle networkHandle) {
        mNetworkHandle = networkHandle;
        mAdapter = baseQuickAdapter;

        if (mNetworkHandle != null) {
            mNetworkHandle.init(this);
        }

        mAdapter.setEnableLoadMore(false);
        mRecyclerView.setAdapter(mAdapter);

        if (mNeedLoadDataAtOnce) {
            mPageIndex = 1;
            mStatus = STATUS_FIRST_LOAD_DATA;
            mAdapter.setEmptyView(getProgressView());
            if (mNetworkHandle != null) {
                mNetworkHandle.loadData(true, String.valueOf(mPageIndex));
            }
        }
    }


    private OnRefreshLoadMoreListener mOnRefreshLoadmoreListener = new OnRefreshLoadMoreListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mStatus = STATUS_REFRESHING;
            mPageIndex = 1;
            if (mNetworkHandle != null) {
                mNetworkHandle.loadData(true, String.valueOf(mPageIndex));
            }
        }

        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            mStatus = STATUS_LOADMORE;
            if (mNetworkHandle != null) {
                mNetworkHandle.loadData(false, String.valueOf(mPageIndex));
            }
        }
    };

    public void callRefreshListener() {
        mPageIndex = 1;
        mStatus = STATUS_FIRST_LOAD_DATA;
        mAdapter.getData().clear();
        mAdapter.notifyDataSetChanged();
        mAdapter.setEmptyView(new View(mContext));
        mRefreshLayout.autoRefresh(0, 0, 0);
    }


    /**
     * 数据加载完成之后的统一操作,关闭下拉刷新,关闭上拉加载的进度条,显示空数据的界面等等
     *
     * @param data 网络请求到的数据
     */
    public void onLoadDataComplete(List<T> data) {
        if (isRefreshing() || mPageIndex == 1) {
            mAdapter.getData().clear();
            if (mNeedShowNoMoreFooter && mNoMoreFooterView != null) {
                mAdapter.removeFooterView(mNoMoreFooterView);
                mNoMoreFooterView = null;
            }
        }
        mAdapter.addData(data);
        mPageIndex++;

        mAdapter.notifyDataSetChanged();

        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
        RefreshFooter refreshFooter = mRefreshLayout.getRefreshFooter();
        if (refreshFooter instanceof ClassicsFooter) {
            ClassicsFooter classicsFooter = (ClassicsFooter) refreshFooter;
            classicsFooter.setFinishDuration(300);
        }

        mRefreshLayout.setEnableAutoLoadMore(true);

        if (!mAdapter.getData().isEmpty() && isRefreshing()) {
            post(() -> mRecyclerView.smoothScrollToPosition(0));
        }

        showNoDataView();

        mStatus = STATUS_NONE;

        if (data.size() >= mPageSize) {
            mRefreshLayout.setEnableLoadMore(true);
        } else {
            if (mRefreshLayout != null) {
                if (mNeedShowNoMoreFooter && mNoMoreFooterView == null && !mAdapter.getData().isEmpty()) {
                    mNoMoreFooterView = View.inflate(mContext, R.layout.frame_layout_no_more_data_footer, null);
                    mAdapter.addFooterView(mNoMoreFooterView);
                }
                mRefreshLayout.setEnableLoadMore(false);
            }
        }
    }

    public void onLoadDataCompleteErr() {
        onLoadDataCompleteErr(mErrString);
    }

    public void onLoadDataCompleteErr(String errText) {
        this.mErrString = errText;
        if (isRefreshing() || mPageIndex == 1) {
            mAdapter.getData().clear();
        }
        mAdapter.notifyDataSetChanged();

        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
        mRefreshLayout.setEnableAutoLoadMore(false);
        mRefreshLayout.setEnableLoadMore(false);
        RefreshFooter refreshFooter = mRefreshLayout.getRefreshFooter();
        if (refreshFooter instanceof ClassicsFooter) {
            ClassicsFooter classicsFooter = (ClassicsFooter) refreshFooter;
            classicsFooter.setFinishDuration(500);
        }

        showErrView();

        mStatus = STATUS_NONE;
    }

    public void finishLoadData() {
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
        mRefreshLayout.setEnableAutoLoadMore(false);
        mRefreshLayout.setEnableLoadMore(false);
        RefreshFooter refreshFooter = mRefreshLayout.getRefreshFooter();
        if (refreshFooter instanceof ClassicsFooter) {
            ClassicsFooter classicsFooter = (ClassicsFooter) refreshFooter;
            classicsFooter.setFinishDuration(500);
        }
        mStatus = STATUS_NONE;
    }

    /**
     * 显示没有数据的视图
     */
    private void showNoDataView() {
        if (!mAdapter.getData().isEmpty() || !mNeedShowNodataView) {
            return;
        }
        initNoDataView();
        TextView tv = mNoDataView.findViewById(R.id.hint);
        if (tv != null) tv.setText(mNodataString);
        mAdapter.setEmptyView(mNoDataView);
    }

    /**
     * 显示网络加载错误的视图
     */
    private void showErrView() {
        if (!mAdapter.getData().isEmpty() || !mNeedShowNodataView) {
            return;
        }
        initErrView();
        TextView tv = mErrView.findViewById(R.id.hint);
        if (tv != null) tv.setText(mErrString);
        mAdapter.setEmptyView(mErrView);
    }

    /**
     * 初始化空数据和无数据视图
     */
    private void initNoDataView() {
        if (mViewCreator == null && placeholderCreater != null) {
            mViewCreator = placeholderCreater.create(mContext);
        }

        if (mViewCreator != null && mViewCreator.getNoDataView() != null) {
            mNoDataView = mViewCreator.getNoDataView();
            if (mNoDataView.getLayoutParams() == null) {
                mNoDataView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }

        if (mNoDataView == null) {
            mNoDataView = View.inflate(mContext, R.layout.frame_layout_network_nodata, null);
            mNoDataView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        View refreshView = mNoDataView.findViewById(R.id.refresh);
        if (refreshView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (!refreshView.hasOnClickListeners()) {
                    refreshView.setOnClickListener(v -> callRefreshListener());
                }
            } else {
                refreshView.setOnClickListener(v -> callRefreshListener());
            }

        }
    }

    /**
     * 初始化空数据和无数据视图
     */
    private void initErrView() {
        if (mViewCreator == null && placeholderCreater != null) {
            mViewCreator = placeholderCreater.create(mContext);
        }

        if (mViewCreator != null && mViewCreator.getErrDataView() != null) {
            mErrView = mViewCreator.getErrDataView();
            if (mErrView.getLayoutParams() == null) {
                mErrView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }

        if (mErrView == null) {
            mErrView = View.inflate(mContext, R.layout.frame_layout_network_nodata, null);
            mErrView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        View refreshView = mErrView.findViewById(R.id.refresh);
        if (refreshView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (!refreshView.hasOnClickListeners()) {
                    refreshView.setOnClickListener(v -> callRefreshListener());
                }
            } else {
                refreshView.setOnClickListener(v -> callRefreshListener());
            }
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

    public void setAdapter(BaseQuickAdapter adapter) {
        mAdapter = adapter;
    }

    public void setPageIndex(int pageIndex) {
        mPageIndex = pageIndex;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public void setProgressView(View progressView) {
        mProgressView = progressView;
    }

    public FrameLayout getContentFrameLayout() {
        return mContentFrameLayout;
    }

    public void setContentFrameLayout(FrameLayout contentFrameLayout) {
        mContentFrameLayout = contentFrameLayout;
    }

    public String getNodataString() {
        return mNodataString;
    }

    public void setNodataString(String nodataString) {
        mNodataString = nodataString;
    }

    public String getErrString() {
        return mErrString;
    }

    public void setErrString(String errString) {
        mErrString = errString;
    }

    public View getNoMoreFooterView() {
        return mNoMoreFooterView;
    }

    public void setNoMoreFooterView(View noMoreFooterView) {
        mNoMoreFooterView = noMoreFooterView;
    }

    private boolean isRefreshing() {
        return mStatus == STATUS_FIRST_LOAD_DATA || mStatus == STATUS_REFRESHING;
    }

    @NonNull
    private View getProgressView() {
        if (mProgressView == null) {
            mProgressView = LayoutInflater.from(mContext).inflate(R.layout.frame_layout_baserecycler_default_loading_view, null);
            mProgressView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        return mProgressView;
    }


    public static void setPlaceholderCreater(BaseRecyclerViewPlaceholderCreater placeholderCreater) {
        BaseRecyclerView.placeholderCreater = placeholderCreater;
    }

    public boolean isNeedShowNoMoreFooter() {
        return mNeedShowNoMoreFooter;
    }

    public void setNeedShowNoMoreFooter(boolean needShowNoMoreFooter) {
        mNeedShowNoMoreFooter = needShowNoMoreFooter;
    }

    public LinearLayoutManager getLayoutManager() {
        return mLayoutManager;
    }
}
