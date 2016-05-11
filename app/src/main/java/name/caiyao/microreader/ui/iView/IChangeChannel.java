package name.caiyao.microreader.ui.iView;

import java.util.ArrayList;

import name.caiyao.microreader.config.Config;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public interface IChangeChannel {

    void showChannel(ArrayList<Config.Channel> savedChannel, ArrayList<Config.Channel> otherChannel);
}
