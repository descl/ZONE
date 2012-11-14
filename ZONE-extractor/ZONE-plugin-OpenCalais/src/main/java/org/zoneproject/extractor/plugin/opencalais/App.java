package org.zoneproject.extractor.plugin.opencalais;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.Prop;

/**
 * Hello world!
 *
 */
public class App 
{
    public static String PLUGIN_URI = "http://www.zone-project.org/plugins/OpenCalais";
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Item[] items = Database.getItemsNotAnotatedForOnePlugin(PLUGIN_URI);
        System.out.println("OpenCalais has "+items.length+" items to annotate");
        for(Item item : items){
            System.out.println("Add ExtractArticlesContent for item: "+item);
            ArrayList<Prop> props = new ArrayList<Prop>();
            props.addAll(openCalaisExtractor.getCitiesResultProp(item.concat()));
            props.addAll(openCalaisExtractor.getPersonsResultProp(item.concat()));

            Database.addAnnotations(item.getUri(), props);
            Database.addAnnotation(item.getUri(), new Prop(PLUGIN_URI,"true"));
        }
    }
}
