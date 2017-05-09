package com.a26c.android.frame.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
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
public class BaseRecyclerView extends FrameLayout {

    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private BaseQuickAdapter adapter;
    private boolean firstLoadData = true;
    private boolean showNodataView = true;
    private MutiItemDecoration decor;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    private NetworkHandle networkHandle;

    private ViewStub nodataViewStub;
    private ViewStub errViewStub;
    private int errLayoutId = 0;
    private int nodataLayoutId = 0;
    private TextView nodataTextView;
    private TextView errTextView;
    private String nodataString = "暂无数据";
    private String errdataString = "请求失败";
    private View nodataView;
    private View errdataView;

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

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.frame_layout_base_recycler_view, this, true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        nodataViewStub = (ViewStub) findViewById(R.id.nodataViewStub);
        errViewStub = (ViewStub) findViewById(R.id.errViewStub);

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

        if (networkHandle != null) networkHandle.init(this);

        //初始化无数据的布局
        initNodataView();

        recyclerView.setAdapter(adapter);
        //baseRecyclerView刚显示时是否需要显示下拉刷新的dialog
        refreshLayout.setRefreshing(isRefreshingFirst);

        //如果需要第一次加载baseRecyclerView就加载数据,设置firstLoadData为true,默认为true
        if (firstLoadData) {
            if (networkHandle != null) networkHandle.loadData(true, "1");
        }

    }

    /**
     * 设置recyclerView使用的layoutManager
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
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
            if (showNodataView) {
                nodataViewStub.setVisibility(VISIBLE);
                errViewStub.setVisibility(INVISIBLE);
            }
        } else {
            nodataViewStub.setVisibility(INVISIBLE);
            errViewStub.setVisibility(INVISIBLE);
        }
    }

    /**
     * 显示网络异常的视图
     * 细节:如果网络异常并且当前无数据才显示errTextView,否则弹出dialog提示就行
     */
    public void showErrView(CharSequence err) {
        nodataViewStub.setVisibility(INVISIBLE);
        errViewStub.setVisibility(VISIBLE);
        if (errTextView != null) {
            errTextView.setText(err);

        }
    }

    private void initNodataView() {
        if (nodataLayoutId == 0) nodataLayoutId = R.layout.frame_layout_network_nodata;
        if (errLayoutId == 0) errLayoutId = R.layout.frame_layout_network_err;

        nodataViewStub.setLayoutResource(nodataLayoutId);
        errViewStub.setLayoutResource(errLayoutId);
        nodataView = nodataViewStub.inflate();
        errdataView = errViewStub.inflate();
        nodataViewStub.setVisibility(GONE);
        errViewStub.setVisibility(GONE);

        if (nodataLayoutId == 0) {
            nodataTextView = (TextView) nodataView.findViewById(R.id.nodataTextView);
            nodataTextView.setText(nodataString);
        }
        if (errLayoutId == 0) {
            errTextView = (TextView) errdataView.findViewById(R.id.errTextView);
            errTextView.setText(errdataString);
        }

    }

    public void addOnItemTouchListener(OnItemClickListener listener) {
        recyclerView.addOnItemTouchListener(listener);
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

    public void loadComplete() {
        adapter.loadComplete();
    }

    public void setIsRefreshing(boolean b) {
        refreshLayout.setRefreshing(b);
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public void openLoadMore(int pageSize) {
        this.pageSize = pageSize;
        adapter.openLoadMore(pageSize);
    }

    public void removeDivider() {
        recyclerView.removeItemDecoration(decor);
    }

    public void addDivider(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
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

    public BaseQuickAdapter getAdapter() {
        return adapter;
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

    public ViewStub getNodataViewStub() {
        return nodataViewStub;
    }

    public void setNodataViewStub(ViewStub nodataViewStub) {
        this.nodataViewStub = nodataViewStub;
    }

    public ViewStub getErrViewStub() {
        return errViewStub;
    }

    public void setErrViewStub(ViewStub errViewStub) {
        this.errViewStub = errViewStub;
    }

    public int getErrLayoutId() {
        return errLayoutId;
    }

    public void setErrLayoutId(int errLayoutId) {
        this.errLayoutId = errLayoutId;
    }

    public int getNodataLayoutId() {
        return nodataLayoutId;
    }

    public void setNodataLayoutId(int nodataLayoutId) {
        this.nodataLayoutId = nodataLayoutId;
    }

    public TextView getNodataTextView() {
        return nodataTextView;
    }

    public void setNodataTextView(TextView nodataTextView) {
        this.nodataTextView = nodataTextView;
    }

    public TextView getErrTextView() {
        return errTextView;
    }

    public void setErrTextView(TextView errTextView) {
        this.errTextView = errTextView;
    }

    public String getNodataString() {
        return nodataString;
    }

    public void setNodataString(String nodataString) {
        this.nodataString = nodataString;
    }

    public String getErrdataString() {
        return errdataString;
    }

    public void setErrdataString(String errdataString) {
        this.errdataString = errdataString;
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

    public View getNodataView() {
        return nodataView;
    }

    public void setNodataView(View nodataView) {
        this.nodataView = nodataView;
    }

    public View getErrdataView() {
        return errdataView;
    }

    public void setErrdataView(View errdataView) {
        this.errdataView = errdataView;
    }
}
