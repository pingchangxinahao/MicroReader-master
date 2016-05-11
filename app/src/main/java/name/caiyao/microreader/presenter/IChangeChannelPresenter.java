package name.caiyao.microreader.presenter;

import java.util.ArrayList;

import name.caiyao.microreader.config.Config;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public interface IChangeChannelPresenter {

    void getChannel();

    void saveChannel(ArrayList<Config.Channel> savedChannel);
}
