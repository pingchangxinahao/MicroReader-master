package name.caiyao.microreader.presenter;

import android.support.design.widget.NavigationView;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public interface IMainPresenter extends BasePresenter{
    void initMenu(NavigationView navigationView);

    void checkUpdate();
}
