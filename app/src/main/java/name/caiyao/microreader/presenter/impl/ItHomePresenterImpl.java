package name.caiyao.microreader.presenter.impl;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;

import name.caiyao.microreader.api.itHome.ItHomeRequest;
import name.caiyao.microreader.bean.itHome.ItHomeItem;
import name.caiyao.microreader.bean.itHome.ItHomeResponse;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.presenter.IItHomePresenter;
import name.caiyao.microreader.ui.iView.IItHomeFragment;
import name.caiyao.microreader.utils.CacheUtil;
import name.caiyao.microreader.utils.ItHomeUtil;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public class ItHomePresenterImpl extends BasePresenterImpl implements IItHomePresenter {

    private Gson gson = new Gson();

    private IItHomeFragment mItHomeFragment;

    private CacheUtil mCacheUtil;

    public ItHomePresenterImpl(IItHomeFragment iItHomeFragment, Context context) {
        if (iItHomeFragment == null)
            throw new IllegalArgumentException("iItHomeFragment must not be null");
        this.mItHomeFragment = iItHomeFragment;
        mCacheUtil = CacheUtil.get(context);
    }

    @Override
    public void getNewItHomeNews() {
        mItHomeFragment.showProgressDialog();
        Subscription subscription = ItHomeRequest.getItHomeApi().getItHomeNews()
                .subscribeOn(Schedulers.io())
                .map(new Func1<ItHomeResponse, ArrayList<ItHomeItem>>() {
                    @Override
                    public ArrayList<ItHomeItem> call(ItHomeResponse itHomeResponse) {
                        //过滤广告新闻
                        ArrayList<ItHomeItem> itHomeItems1 = itHomeResponse.getChannel().getItems();
                        Iterator<ItHomeItem> iter = itHomeItems1.iterator();
                        while (iter.hasNext()) {
                            ItHomeItem item = iter.next();
                            if (item.getUrl().contains("digi"))
                                iter.remove();
                        }
                        return itHomeItems1;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ItHomeItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mItHomeFragment.hidProgressDialog();
                        mItHomeFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<ItHomeItem> it) {
                        mItHomeFragment.hidProgressDialog();
                        mItHomeFragment.updateList(it);
                        mCacheUtil.put(Config.IT, gson.toJson(it));
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getMoreItHomeNews(String lastNewsId) {
        Subscription subscription = ItHomeRequest.getItHomeApi().getMoreItHomeNews(ItHomeUtil.getMinNewsId(lastNewsId))
                .map(new Func1<ItHomeResponse, ArrayList<ItHomeItem>>() {
                    @Override
                    public ArrayList<ItHomeItem> call(ItHomeResponse itHomeResponse) {
                        //过滤广告新闻
                        ArrayList<ItHomeItem> itHomeItems1 = itHomeResponse.getChannel().getItems();
                        Iterator<ItHomeItem> iter = itHomeItems1.iterator();
                        while (iter.hasNext()) {
                            ItHomeItem item = iter.next();
                            if (item.getUrl().contains("digi"))
                                iter.remove();
                        }
                        return itHomeItems1;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ItHomeItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mItHomeFragment.hidProgressDialog();
                        mItHomeFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<ItHomeItem> it) {
                        mItHomeFragment.hidProgressDialog();
                        mItHomeFragment.updateList(it);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getNewsFromCache() {
        if (mCacheUtil.getAsJSONArray(Config.IT) != null && mCacheUtil.getAsJSONArray(Config.IT).length() != 0) {
            ArrayList<ItHomeItem> it = gson.fromJson(mCacheUtil.getAsJSONArray(Config.IT).toString(), new TypeToken<ArrayList<ItHomeItem>>() {
            }.getType());
            mItHomeFragment.updateList(it);
        }
    }
}
