package name.caiyao.microreader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import name.caiyao.microreader.R;
import name.caiyao.microreader.bean.zhihu.ZhihuDaily;
import name.caiyao.microreader.bean.zhihu.ZhihuDailyItem;
import name.caiyao.microreader.presenter.IZhihuPresenter;
import name.caiyao.microreader.presenter.impl.ZhihuPresenterImpl;
import name.caiyao.microreader.ui.adapter.ZhihuAdapter;
import name.caiyao.microreader.ui.iView.IZhihuFragment;
import name.caiyao.microreader.utils.NetWorkUtil;
import name.caiyao.microreader.utils.SharePreferenceUtil;


public class ZhihuFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IZhihuFragment {

    @BindView(R.id.swipe_target)
    RecyclerView swipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Unbinder mUnbinder;

    private String currentLoadedDate;
    private ZhihuAdapter zhihuAdapter;
    private IZhihuPresenter mZhihuPresenter;
    private ArrayList<ZhihuDailyItem> zhihuStories = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public ZhihuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        mZhihuPresenter = new ZhihuPresenterImpl(this, getActivity());
    }

    private void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        setSwipeRefreshLayoutColor(swipeRefreshLayout);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        swipeTarget.setLayoutManager(mLinearLayoutManager);
        swipeTarget.setHasFixedSize(true);
        zhihuAdapter = new ZhihuAdapter(getActivity(), zhihuStories);
        swipeTarget.setAdapter(zhihuAdapter);
        swipeTarget.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //向下滚动
                {
                    visibleItemCount = mLinearLayoutManager.getChildCount();
                    totalItemCount = mLinearLayoutManager.getItemCount();
                    pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = true;
                            onLoadMore();
                        }
                    }
                }
            }
        });
        mZhihuPresenter.getLastFromCache();
        if (SharePreferenceUtil.isRefreshOnlyWifi(getActivity())) {
            if (NetWorkUtil.isWifiConnected(getActivity())) {
                onRefresh();
            }
        } else {
            onRefresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mZhihuPresenter.unsubcrible();
    }

    @Override
    public void onRefresh() {
        currentLoadedDate = "0";
        zhihuStories.clear();
        zhihuAdapter.notifyDataSetChanged();
        mZhihuPresenter.getLastZhihuNews();
    }

    public void onLoadMore() {
        mZhihuPresenter.getTheDaily(currentLoadedDate);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
        }
    }

    @Override
    public void showError(String error) {
        if (swipeTarget != null) {
            Snackbar.make(swipeTarget, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentLoadedDate.equals("0")) {
                        mZhihuPresenter.getLastZhihuNews();
                    } else {
                        mZhihuPresenter.getTheDaily(currentLoadedDate);
                    }
                }
            }).show();
        }
    }

    @Override
    public void updateList(ZhihuDaily zhihuDaily) {
        currentLoadedDate = zhihuDaily.getDate();
        zhihuStories.addAll(zhihuDaily.getStories());
        zhihuAdapter.notifyDataSetChanged();
        //若未填满屏幕
        if (!swipeTarget.canScrollVertically(View.SCROLL_INDICATOR_BOTTOM)){
            onLoadMore();
        }
    }
}
