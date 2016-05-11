package name.caiyao.microreader.ui.iView;

import java.util.ArrayList;

import name.caiyao.microreader.bean.weixin.WeixinNews;

/**
 * Created by 蔡小木 on 2016/4/22 0022.
 */
public interface IWeixinFragment extends IBaseFragment{
    void updateList(ArrayList<WeixinNews> weixinNewses);
}
