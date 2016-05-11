package name.caiyao.microreader.presenter.impl;

import name.caiyao.microreader.BuildConfig;
import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.UpdateItem;
import name.caiyao.microreader.presenter.ISettingPresenter;
import name.caiyao.microreader.ui.iView.ISettingFragment;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/24 0024.
 */
public class SettingPresenterImpl extends BasePresenterImpl implements ISettingPresenter {

    private ISettingFragment mSettingFragment;

    public SettingPresenterImpl(ISettingFragment iSettingFragment) {
        if (iSettingFragment == null)
            throw new IllegalArgumentException("iSettingFragment must not be null");
        mSettingFragment = iSettingFragment;
    }

    @Override
    public void checkUpdate() {
        Subscription subscription = ZhihuRequest.getZhihuApi().getUpdateInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateItem>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSettingFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(final UpdateItem updateItem) {
                        if (updateItem.getVersionCode() > BuildConfig.VERSION_CODE)
                            mSettingFragment.showUpdateDialog(updateItem);
                        else
                            mSettingFragment.showNoUpdate();
                    }
                });
        addSubscription(subscription);
    }
}
