package name.caiyao.microreader.ui.iView;

import java.util.ArrayList;

import name.caiyao.microreader.bean.weiboVideo.WeiboVideoBlog;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public interface IVideoFragment extends IBaseFragment{
    void updateList(ArrayList<WeiboVideoBlog> weiboVideoBlogs);
}
