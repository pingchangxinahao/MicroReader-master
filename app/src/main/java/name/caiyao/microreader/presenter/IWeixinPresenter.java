package name.caiyao.microreader.presenter;

/**
 * Created by 蔡小木 on 2016/4/22 0022.
 */
public interface IWeixinPresenter extends BasePresenter{
    void getWeixinNews(int page);
    void getWeixinNewsFromCache(int page);
}
