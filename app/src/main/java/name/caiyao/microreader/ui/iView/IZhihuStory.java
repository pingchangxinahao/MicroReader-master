package name.caiyao.microreader.ui.iView;

import name.caiyao.microreader.bean.guokr.GuokrArticle;
import name.caiyao.microreader.bean.zhihu.ZhihuStory;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public interface IZhihuStory {

    void showError(String error);

    void showZhihuStory(ZhihuStory zhihuStory);

    void showGuokrArticle(GuokrArticle guokrArticle);
}
