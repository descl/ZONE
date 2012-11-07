package org.zoneproject.plugin.wikimeta;

import java.util.ArrayList;
import org.zoneproject.utils.FourStoreDatabase;
import org.zoneproject.utils.Item;
import org.zoneproject.utils.Prop;

/**
 * Hello world!
 *
 */
public class App 
{
    public static String PLUGIN_URI = "http://www.zone-project.org/plugins/WikiMeta";
    public static void main(String[] args) {

        Item[] items = FourStoreDatabase.getItemsNotAnotatedForOnePlugin(PLUGIN_URI);
        System.out.println("ExtractArticlesContent has "+items.length+" items to annotate");
        for(Item item : items){
            System.out.println("Add ExtractArticlesContent for item: "+item);
            
            ArrayList<Prop> content= WikiMetaRequest.getProperties(item.toString());
            FourStoreDatabase.addAnnotations(item.getUri(), content);
            
            FourStoreDatabase.addAnnotation(item.getUri(), new Prop(PLUGIN_URI,"true"));

        }
    }
}
