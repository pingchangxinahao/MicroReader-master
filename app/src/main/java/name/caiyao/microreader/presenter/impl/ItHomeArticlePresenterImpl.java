package name.caiyao.microreader.presenter.impl;

import name.caiyao.microreader.api.itHome.ItHomeRequest;
import name.caiyao.microreader.bean.itHome.ItHomeArticle;
import name.caiyao.microreader.presenter.IItHomeArticlePresenter;
import name.caiyao.microreader.ui.iView.IItHomeArticle;
import name.caiyao.microreader.utils.ItHomeUtil;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class ItHomeArticlePresenterImpl extends BasePresenterImpl implements IItHomeArticlePresenter {

    private IItHomeArticle mIItHomeArticle;

    public ItHomeArticlePresenterImpl(IItHomeArticle iItHomeArticle) {
        if (iItHomeArticle == null)
            throw new IllegalArgumentException("iItHomeArticle must not be null");
        mIItHomeArticle = iItHomeArticle;
    }

    @Override
    public void getItHomeArticle(String id) {
        Subscription subscription = ItHomeRequest.getItHomeApi().getItHomeArticle(ItHomeUtil.getSplitNewsId(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ItHomeArticle>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mIItHomeArticle.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ItHomeArticle itHomeArticle) {
                        mIItHomeArticle.showItHomeArticle(itHomeArticle);
                    }
                });
        addSubscription(subscription);
    }
}
