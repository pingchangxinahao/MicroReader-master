package name.caiyao.microreader.presenter.impl;

import android.content.Context;

import com.google.gson.Gson;

import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.zhihu.ZhihuDaily;
import name.caiyao.microreader.bean.zhihu.ZhihuDailyItem;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.presenter.IZhihuPresenter;
import name.caiyao.microreader.ui.iView.IZhihuFragment;
import name.caiyao.microreader.utils.CacheUtil;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public class ZhihuPresenterImpl extends BasePresenterImpl implements IZhihuPresenter {

    private IZhihuFragment mZhihuFragment;
    private CacheUtil mCacheUtil;
    private Gson gson = new Gson();

    public ZhihuPresenterImpl(IZhihuFragment iZhihuFragment, Context context) {
        if (iZhihuFragment == null)
            throw new IllegalArgumentException("iZhihuFragment must not be null");
        mZhihuFragment = iZhihuFragment;
        mCacheUtil = CacheUtil.get(context);
    }

    @Override
    public void getLastZhihuNews() {
        mZhihuFragment.showProgressDialog();
        Subscription subscription = ZhihuRequest.getZhihuApi().getLastDaily()
                .map(new Func1<ZhihuDaily, ZhihuDaily>() {
                    @Override
                    public ZhihuDaily call(ZhihuDaily zhihuDaily) {
                        String date = zhihuDaily.getDate();
                        for (ZhihuDailyItem zhihuDailyItem : zhihuDaily.getStories()) {
                            zhihuDailyItem.setDate(date);
                        }
                        return zhihuDaily;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuDaily>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mZhihuFragment.hidProgressDialog();
                        mZhihuFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuDaily zhihuDaily) {
                        mZhihuFragment.hidProgressDialog();
                        mCacheUtil.put(Config.ZHIHU, gson.toJson(zhihuDaily));
                        mZhihuFragment.updateList(zhihuDaily);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getTheDaily(String date) {
        Subscription subscription = ZhihuRequest.getZhihuApi().getTheDaily(date)
                .map(new Func1<ZhihuDaily, ZhihuDaily>() {
                    @Override
                    public ZhihuDaily call(ZhihuDaily zhihuDaily) {
                        String date = zhihuDaily.getDate();
                        for (ZhihuDailyItem zhihuDailyItem : zhihuDaily.getStories()) {
                            zhihuDailyItem.setDate(date);
                        }
                        return zhihuDaily;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuDaily>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mZhihuFragment.hidProgressDialog();
                        mZhihuFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuDaily zhihuDaily) {
                        mZhihuFragment.hidProgressDialog();
                        mZhihuFragment.updateList(zhihuDaily);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getLastFromCache() {
        if (mCacheUtil.getAsJSONObject(Config.ZHIHU) != null) {
            ZhihuDaily zhihuDaily = gson.fromJson(mCacheUtil.getAsJSONObject(Config.ZHIHU).toString(), ZhihuDaily.class);
            mZhihuFragment.updateList(zhihuDaily);
        }
    }
}
