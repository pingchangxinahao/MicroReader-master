package name.caiyao.microreader.bean.itHome;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by 蔡小木 on 2016/3/24 0024.
 */
@Root(name = "channel")
public class ItHomeChannel {
    @ElementList(inline = true,name = "items")
    ArrayList<ItHomeItem> items;

    public ArrayList<ItHomeItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItHomeItem> items) {
        this.items = items;
    }
}
