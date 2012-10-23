/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zone.plugin.wikimeta;

import java.util.ArrayList;
import zone.utils.Item;
import zone.utils.Prop;

/**
 *
 * @author cdesclau
 */
public class ZONEPluginWikiMeta {
    public static ArrayList<Prop> getProps(Item item) {
        ArrayList<Prop> props = new ArrayList<Prop>();
        props.addAll(WikiMetaRequest.getProperties(item.concat()));
        return props;
    }
}
