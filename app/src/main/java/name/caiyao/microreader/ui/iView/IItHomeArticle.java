package name.caiyao.microreader.ui.iView;

import name.caiyao.microreader.bean.itHome.ItHomeArticle;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public interface IItHomeArticle {
    void showError(String error);

    void showItHomeArticle(ItHomeArticle itHomeArticle);
}
