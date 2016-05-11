package name.caiyao.microreader.presenter.impl;

import android.content.Context;

import com.google.gson.Gson;

import name.caiyao.microreader.api.weixin.TxRequest;
import name.caiyao.microreader.bean.weixin.TxWeixinResponse;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.presenter.IWeixinPresenter;
import name.caiyao.microreader.ui.iView.IWeixinFragment;
import name.caiyao.microreader.utils.CacheUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/22 0022.
 */
public class WeiXinPresenterImpl extends BasePresenterImpl implements IWeixinPresenter {

    private CacheUtil mCacheUtil;
    private IWeixinFragment mWeixinFragment;
    private Gson mGson = new Gson();

    public WeiXinPresenterImpl(IWeixinFragment weixinFragment, Context context) {
        if (weixinFragment==null)
            throw new IllegalArgumentException("weixinFragment must not be null");
        this.mWeixinFragment = weixinFragment;
        mCacheUtil = CacheUtil.get(context);
    }

    @Override
    public void getWeixinNews(final int page) {
        mWeixinFragment.showProgressDialog();
        Subscription subscription = TxRequest.getTxApi().getWeixin(page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TxWeixinResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mWeixinFragment.hidProgressDialog();
                        mWeixinFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(TxWeixinResponse txWeixinResponse) {
                        mWeixinFragment.hidProgressDialog();
                        if (txWeixinResponse.getCode() == 200) {
                            mWeixinFragment.updateList(txWeixinResponse.getNewslist());
                            mCacheUtil.put(Config.WEIXIN + page, mGson.toJson(txWeixinResponse));
                        } else {
                            mWeixinFragment.showError("服务器内部错误！");
                        }
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getWeixinNewsFromCache(int page) {
        if (mCacheUtil.getAsJSONObject(Config.WEIXIN + page) != null) {
            TxWeixinResponse txWeixinResponse = mGson.fromJson(mCacheUtil.getAsJSONObject(Config.WEIXIN + page).toString(), TxWeixinResponse.class);
            mWeixinFragment.updateList(txWeixinResponse.getNewslist());
        }
    }
}
