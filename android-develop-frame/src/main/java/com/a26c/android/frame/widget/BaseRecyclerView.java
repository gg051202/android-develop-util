package com.a26c.android.frame.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.List;

/**
 * Created by guilinlin on 16/7/29 15:41.
 * email 973635949@qq.com
 */
@SuppressWarnings("all")
public class BaseRecyclerView extends FrameLayout {

    private Context mContext;
    private BaseQuickAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private FrameLayout noDataRelativeLayout;
    private MutiItemDecoration mMutiItemDecoration;
    private NetworkHandle mNetworkHandle;
    private ViewCreator mViewCreator;
    private CharSequence mDefaultNoDataString = "暂无数据";
    private CharSequence mDefaultErrString = "请求失败";
    private View mNoDataView;
    private View mErrView;

    /**
     * 没有数据时，时候需要显示 没有数据的视图
     */
    private boolean mNeedShowNodataView = true;
    /**
     * PageSize用于判断，获取到数据之后，判断是否需要开上拉加载
     */
    private int mPageSize = 20;
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
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.frame_layout_base_recycler_view, this, true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        noDataRelativeLayout = (FrameLayout) findViewById(R.id.noDataRelativeLayout);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        //初始化recyclerView
        mMutiItemDecoration = new MutiItemDecoration(MutiItemDecoration.Type.ALL);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(mMutiItemDecoration);

        mRefreshLayout.setEnableLoadmore(true);
        mRefreshLayout.setEnableLoadmoreWhenContentNotFull(false);
        mRefreshLayout.setOnRefreshLoadmoreListener(mOnRefreshLoadmoreListener);
        mRefreshLayout.setNestedScrollingEnabled(true);
    }

    public void init(BaseQuickAdapter baseQuickAdapter, NetworkHandle networkHandle) {
        this.mNetworkHandle = networkHandle;
        this.mAdapter = baseQuickAdapter;

        if (mNetworkHandle != null) {
            mNetworkHandle.init(this);
        }

        //初始化无数据的布局,如果init中没有初始化ViewCreator，会自动设置一个默认的
        initViewCreator();

        mAdapter.setEnableLoadMore(false);
        mRecyclerView.setAdapter(mAdapter);

        if (mNeedLoadDataAtOnce) {
            mRefreshLayout.autoRefresh(1, 0, 1);
        }

    }

    private OnRefreshLoadmoreListener mOnRefreshLoadmoreListener = new OnRefreshLoadmoreListener() {
        @Override
        public void onRefresh(RefreshLayout refreshLayout) {
            mCurrentIsRefresh = true;
            if (mNetworkHandle != null) {
                mNetworkHandle.loadData(mCurrentIsRefresh, "1");
            }
        }

        @Override
        public void onLoadmore(RefreshLayout refreshLayout) {
            mCurrentIsRefresh = false;
            if (mNetworkHandle != null) {
                mNetworkHandle.loadData(mCurrentIsRefresh, String.valueOf(mPageIndex + 1));
            }
        }
    };

    /**
     * 数据加载完成之后的统一操作,关闭下拉刷新,关闭上拉加载的进度条,显示空数据的界面等等
     *
     * @param data 网络请求到的数据
     */
    public void onLoadDataComplete(List data) {
        //获取到数据后，如果当前页数是1，肯定是下拉刷新，所以清空数据
        if (mCurrentIsRefresh) {
            mAdapter.getData().clear();
        }
        if (data.size() < mPageSize) {
            mRefreshLayout.setEnableLoadmore(false);
        }
        mAdapter.addData(data);
        onLoadDataComplete();

    }


    public void onLoadDataCompleteErr() {
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadmore(false);

        showErrView();
    }

    public void onLoadDataComplete() {
        if (mCurrentIsRefresh) {
            mPageIndex = 1;
        } else {
            mPageIndex++;
        }
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadmore();

        showNoDataView();
    }

    private void initViewCreator() {
        if (mViewCreator == null) {
            mViewCreator = new ViewCreator() {
                @Override
                public View getNoDataView() {
                    return LayoutInflater.from(mContext).inflate(R.layout.frame_layout_network_nodata, null);
                }

                @Override
                public View getErrDataView() {
                    return LayoutInflater.from(mContext).inflate(R.layout.frame_layout_network_err, null);
                }
            };
        }
        if (mViewCreator.getNoDataView() == null) {
            mNoDataView = LayoutInflater.from(mContext).inflate(R.layout.frame_layout_network_nodata, null);
        } else {
            mNoDataView = mViewCreator.getNoDataView();
        }
        if (mViewCreator.getErrDataView() == null) {
            mErrView = LayoutInflater.from(mContext).inflate(R.layout.frame_layout_network_err, null);
        } else {
            mErrView = mViewCreator.getErrDataView();
        }

        mNoDataView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mNoDataView.setVisibility(INVISIBLE);
        noDataRelativeLayout.addView(mNoDataView);

        mErrView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mErrView.setVisibility(INVISIBLE);
        noDataRelativeLayout.addView(mErrView);

    }


    public void showNoDataView() {
        showNoDataView(mDefaultNoDataString);
    }

    /**
     * 修改默认的无数据的视图,前提是数据长度是0
     */
    public void showNoDataView(CharSequence text) {
        if (!mNeedShowNodataView) {
            return;
        }
        if (mAdapter.getItemCount() - mAdapter.getHeaderLayoutCount() - mAdapter.getFooterLayoutCount() == 0) {
            if (mNoDataView.getId() == R.id.frame_nodataTextView) {
                ((TextView) mNoDataView).setText(TextUtils.isEmpty(text) ? mDefaultNoDataString : text);
            }
            mNoDataView.setVisibility(VISIBLE);
            mErrView.setVisibility(INVISIBLE);
        } else {
            mNoDataView.setVisibility(INVISIBLE);
            mErrView.setVisibility(INVISIBLE);
        }
    }

    public void showErrView() {
        showErrView(mDefaultErrString);
    }

    /**
     * 修改默认的显示网络异常的视图,前提是数据长度是0
     */
    public void showErrView(CharSequence text) {
        if (!mNeedShowNodataView) {
            return;
        }
        if (mAdapter.getItemCount() - mAdapter.getHeaderLayoutCount() - mAdapter.getFooterLayoutCount() == 0) {
            if (mErrView.getId()==R.id.frame_errTextView) {
                ((TextView) mErrView).setText(TextUtils.isEmpty(text) ? mDefaultErrString : text);
            }
            mNoDataView.setVisibility(INVISIBLE);
            mErrView.setVisibility(VISIBLE);
        } else {
            mNoDataView.setVisibility(INVISIBLE);
            mErrView.setVisibility(INVISIBLE);
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
        mPageIndex = 1;
        mRefreshLayout.autoRefresh(0, 200, 1);
    }

    public void addData(List data) {
        mAdapter.addData(data);
    }


    public void setContext(Context context) {
        mContext = context;
    }

    public BaseQuickAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        mAdapter = adapter;
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

    public FrameLayout getNoDataRelativeLayout() {
        return noDataRelativeLayout;
    }

    public void setNoDataRelativeLayout(FrameLayout noDataRelativeLayout) {
        this.noDataRelativeLayout = noDataRelativeLayout;
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

    public CharSequence getDefaultNoDataString() {
        return mDefaultNoDataString;
    }

    public void setDefaultNoDataString(CharSequence defaultNoDataString) {
        mDefaultNoDataString = defaultNoDataString;
    }

    public CharSequence getDefaultErrString() {
        return mDefaultErrString;
    }

    public void setDefaultErrString(CharSequence defaultErrString) {
        mDefaultErrString = defaultErrString;
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

    public OnRefreshLoadmoreListener getOnRefreshLoadmoreListener() {
        return mOnRefreshLoadmoreListener;
    }

    public void setOnRefreshLoadmoreListener(OnRefreshLoadmoreListener onRefreshLoadmoreListener) {
        mOnRefreshLoadmoreListener = onRefreshLoadmoreListener;
    }
}
