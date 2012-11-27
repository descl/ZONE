package org.zoneproject.extractor.plugin.inseegeo;

import java.util.ArrayList;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.Prop;

public class App 
{
    public static String PLUGIN_URI = "http://www.zone-project.org/plugins/INSEEGeo";
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    
    public static void main(String[] args) {
        String openCalaisPrefix = "http://www.opencalais.org/Entities";
        String [] deps = {openCalaisPrefix+"#LOC"};
        Item[] items = Database.getItemsNotAnotatedForPluginsWithDeps(PLUGIN_URI,deps);
        System.out.println("INSEEGeo has "+items.length+" items to annotate");
        for(Item item : items){
            System.out.println("Add INSEEGeo for item: "+item);
            ArrayList<Prop> props;
            props = InseeSparqlRequest.getDimensions(item.getElements(openCalaisPrefix+"#LOC"));
            Database.addAnnotations(item.getUri(), props);
            Database.addAnnotation(item.getUri(), new Prop(PLUGIN_URI,"true"));
            
        }
    }
}
