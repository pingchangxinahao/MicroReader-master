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
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import name.caiyao.microreader.R;
import name.caiyao.microreader.bean.weixin.WeixinNews;
import name.caiyao.microreader.presenter.IWeixinPresenter;
import name.caiyao.microreader.presenter.impl.WeiXinPresenterImpl;
import name.caiyao.microreader.ui.adapter.WeixinAdapter;
import name.caiyao.microreader.ui.iView.IWeixinFragment;
import name.caiyao.microreader.utils.NetWorkUtil;
import name.caiyao.microreader.utils.SharePreferenceUtil;

public class WeixinFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IWeixinFragment {


    WeixinAdapter weixinAdapter;
    @BindView(R.id.swipe_target)
    RecyclerView swipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Unbinder mUnbinder;
    private IWeixinPresenter mWeixinPresenter;
    private ArrayList<WeixinNews> weixinNewses = new ArrayList<>();
    private int currentPage = 1;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public WeixinFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        mUnbinder= ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initView() {
        showProgressDialog();
        swipeRefreshLayout.setOnRefreshListener(this);
        setSwipeRefreshLayoutColor(swipeRefreshLayout);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        swipeTarget.setLayoutManager(mLinearLayoutManager);
        swipeTarget.setHasFixedSize(true);
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
        weixinAdapter = new WeixinAdapter(getActivity(),weixinNewses);
        swipeTarget.setAdapter(weixinAdapter);
        mWeixinPresenter.getWeixinNews(1);
        if (SharePreferenceUtil.isRefreshOnlyWifi(getActivity())) {
            if (NetWorkUtil.isWifiConnected(getActivity())) {
                onRefresh();
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_wifi_refresh_data), Toast.LENGTH_SHORT).show();
            }
        } else {
            onRefresh();
        }
    }

    private void initData() {
        mWeixinPresenter = new WeiXinPresenterImpl(this, getActivity());
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        weixinNewses.clear();
        //2016-04-05修复Inconsistency detected. Invalid view holder adapter positionViewHolder
        weixinAdapter.notifyDataSetChanged();
        mWeixinPresenter.getWeixinNews(currentPage);
    }

    public void onLoadMore() {
        mWeixinPresenter.getWeixinNews(currentPage);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mWeixinPresenter.unsubcrible();
    }

    @Override
    public void hidProgressDialog() {
        if (swipeRefreshLayout != null) {//不加可能会崩溃
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
        }
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        if (swipeTarget != null) {
            mWeixinPresenter.getWeixinNewsFromCache(currentPage);
            Snackbar.make(swipeTarget, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeixinPresenter.getWeixinNews(currentPage);
                }
            }).show();
        }
    }

    @Override
    public void updateList(ArrayList<WeixinNews> weixinNewsesList) {
        currentPage++;
        weixinNewses.addAll(weixinNewsesList);
        weixinAdapter.notifyDataSetChanged();
    }
}
