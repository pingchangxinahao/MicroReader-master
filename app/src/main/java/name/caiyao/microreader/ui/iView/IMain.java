package name.caiyao.microreader.ui.iView;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

import name.caiyao.microreader.bean.UpdateItem;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public interface IMain {
    void initMenu(ArrayList<Fragment> fragments,ArrayList<Integer> titles);

    void showUpdate(UpdateItem updateItem);
}
