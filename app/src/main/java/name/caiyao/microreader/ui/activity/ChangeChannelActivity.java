package name.caiyao.microreader.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.event.StatusBarEvent;
import name.caiyao.microreader.presenter.IChangeChannelPresenter;
import name.caiyao.microreader.presenter.impl.ChangeChannelPresenterImpl;
import name.caiyao.microreader.ui.adapter.ChannelAdapter;
import name.caiyao.microreader.ui.helper.ItemDragHelperCallback;
import name.caiyao.microreader.ui.iView.IChangeChannel;
import name.caiyao.microreader.utils.RxBus;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class ChangeChannelActivity extends BaseActivity implements IChangeChannel {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_channel)
    RecyclerView mRv;

    private ArrayList<Config.Channel> savedChannel = new ArrayList<>();
    private ArrayList<Config.Channel> otherChannel = new ArrayList<>();
    private IChangeChannelPresenter mIChangeChannelPresenter;
    private ChannelAdapter mChannelAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_channel);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        mIChangeChannelPresenter = new ChangeChannelPresenterImpl(this, this);
    }

    private void initView() {
        mToolbar.setTitle(getString(R.string.activity_change_channel_title));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setToolBar(null, mToolbar, true, true, null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRv.setLayoutManager(linearLayoutManager);
        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRv);
        mRv.setHasFixedSize(true);
        mRv.setNestedScrollingEnabled(false);
        mChannelAdapter = new ChannelAdapter(this, helper, savedChannel, otherChannel);
        mRv.setAdapter(mChannelAdapter);
        mIChangeChannelPresenter.getChannel();
    }


    @Override
    public void showChannel(ArrayList<Config.Channel> savedChannel, ArrayList<Config.Channel> otherChannel) {
        this.savedChannel.addAll(savedChannel);
        this.otherChannel.addAll(otherChannel);
        mChannelAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        mIChangeChannelPresenter.saveChannel(savedChannel);
        RxBus.getDefault().send(new StatusBarEvent());
        super.onBackPressed();
    }
}
