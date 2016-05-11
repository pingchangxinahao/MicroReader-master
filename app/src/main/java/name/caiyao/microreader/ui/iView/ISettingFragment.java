package name.caiyao.microreader.ui.iView;

import name.caiyao.microreader.bean.UpdateItem;

/**
 * Created by 蔡小木 on 2016/4/24 0024.
 */
public interface ISettingFragment {
    void showError(String error);

    void showUpdateDialog(UpdateItem updateItem);

    void showNoUpdate();
}
