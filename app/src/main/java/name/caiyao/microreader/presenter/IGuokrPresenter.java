package name.caiyao.microreader.presenter;

/**
 * Created by 蔡小木 on 2016/4/22 0022.
 */
public interface IGuokrPresenter extends BasePresenter {
    void getGuokrHot(int offset);
    void getGuokrHotFromCache(int offset);
}
