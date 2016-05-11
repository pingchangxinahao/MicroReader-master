package name.caiyao.microreader.ui.iView;

import java.util.ArrayList;

import name.caiyao.microreader.bean.itHome.ItHomeItem;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public interface IItHomeFragment extends IBaseFragment {
    void updateList(ArrayList<ItHomeItem> itHomeItems);
}
