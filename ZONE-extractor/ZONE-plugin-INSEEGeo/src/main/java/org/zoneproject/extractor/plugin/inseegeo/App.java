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
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String openCalaisPrefix = "http://www.opencalais.org/Entities";
        String [] deps = {openCalaisPrefix+"#LOC"};
        Item[] items = Database.getItemsNotAnotatedForPluginsWithDeps(PLUGIN_URI,deps);
        System.out.println("INSEEGeo has "+items.length+" items to annotate");
        for(Item item : items){
            System.out.println("Add ExtractArticlesContent for item: "+item);
            ArrayList<Prop> props = new ArrayList<Prop>();
            //System.out.println(item.getElements(openCalaisPrefix+"#LOC").length);
            System.out.println(InseeSparqlRequest.getDimensions(item.getElements(openCalaisPrefix+"#LOC")));
            //props.addAll(InseeSparqlRequest.getDimensions(item.getElements(org.zoneproject.extractor.plugin.opencalais.App.PLUGIN_URI+"#LOC")));
            //props.addAll(openCalaisExtractor.getCitiesResultProp(item.concat()));
            //props.addAll(openCalaisExtractor.getPersonsResultProp(item.concat()));

            //Database.addAnnotations(item.getUri(), props);
            //Database.addAnnotation(item.getUri(), new Prop(PLUGIN_URI,"true"));
        }
    }
}
