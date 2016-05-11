package name.caiyao.microreader.presenter.impl;

import name.caiyao.microreader.presenter.BasePresenter;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by 蔡小木 on 2016/4/29 0029.
 */
public class BasePresenterImpl implements BasePresenter {

    private CompositeSubscription mCompositeSubscription;

    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    @Override
    public void unsubcrible() {
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
