/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zone.plugin.opencalais;

import java.util.ArrayList;
import zone.utils.Item;
import zone.utils.Prop;

/**
 *
 * @author cdesclau
 */
public class ZONEpluginOpenCalais {
    public static ArrayList<Prop> getProps(Item item) {
        ArrayList<Prop> props = new ArrayList<Prop>();
        props.addAll(openCalaisExtractor.getCitiesResultProp(item.concat()));
        props.addAll(openCalaisExtractor.getPersonsResultProp(item.concat()));
        return props;
    }  
}
